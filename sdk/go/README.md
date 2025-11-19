# OpenCPX Go SDK

A lightweight Go SDK for implementing OpenCPX compliance posture endpoints.

## Installation

```bash
go get github.com/opencpx/sdk-go
```

## Quick Start

```go
package main

import (
    "net/http"
    cpx "github.com/opencpx/sdk-go"
)

func main() {
    // Create a provider function that returns your compliance posture
    provider := func() (*cpx.Posture, error) {
        posture := cpx.NewPosture().
            SetPosture(cpx.PostureCompliant).
            SetOrganization(cpx.Organization{
                Name:    "Acme Corp",
                Domain:  "acme.com",
                Contact: "security@acme.com",
            })

        // Add SOC 2 framework
        soc2 := cpx.NewFramework("SOC2", cpx.StatusCompliant, 1.0)
        soc2.Version = "Type II"
        soc2.LastAudit = "2024-01-15"
        soc2.Auditor = "BigFour Audit LLP"

        // Add controls
        soc2.AddControl(cpx.Control{
            ID:     "CC1.1",
            Title:  "Demonstrates Commitment to Integrity",
            Status: cpx.ControlCompliant,
            EvidenceRefs: []string{
                "https://evidence.acme.com/code-of-conduct.pdf",
            },
        })

        posture.AddFramework(soc2)

        return posture, nil
    }

    // Register the handler
    mux := http.NewServeMux()
    cpx.RegisterHandler(mux, provider)

    // Start the server
    http.ListenAndServe(":8080", mux)
}
```

## Usage Patterns

### Using as Middleware

```go
// Wrap your existing handler with CPX middleware
handler := cpx.Middleware(provider, yourExistingHandler)
http.ListenAndServe(":8080", handler)
```

### Dynamic Posture Generation

```go
provider := func() (*cpx.Posture, error) {
    // Query your database for current compliance status
    controls, err := db.GetComplianceControls()
    if err != nil {
        return nil, err
    }

    posture := cpx.NewPosture()

    // Build frameworks dynamically
    framework := cpx.NewFramework("SOC2", cpx.StatusCompliant, 0.95)
    for _, ctrl := range controls {
        framework.AddControl(cpx.Control{
            ID:           ctrl.ID,
            Title:        ctrl.Title,
            Status:       cpx.ControlStatus(ctrl.Status),
            EvidenceRefs: ctrl.EvidenceURLs,
        })
    }

    posture.AddFramework(framework)
    posture.CompliancePosture = posture.CalculateOverallPosture()

    return posture, nil
}
```

### Multiple Frameworks

```go
posture := cpx.NewPosture().
    SetPosture(cpx.PosturePartiallyCompliant)

// SOC 2
soc2 := cpx.NewFramework("SOC2", cpx.StatusCompliant, 1.0)
posture.AddFramework(soc2)

// ISO 27001
iso := cpx.NewFramework("ISO27001", cpx.StatusPartial, 0.85)
posture.AddFramework(iso)

// HIPAA
hipaa := cpx.NewFramework("HIPAA", cpx.StatusCompliant, 1.0)
posture.AddFramework(hipaa)
```

### Custom Extensions

```go
posture.AddExtension("acme", map[string]interface{}{
    "customer_count":    5000,
    "data_centers":      []string{"us-east-1", "eu-west-1"},
    "encryption":        "AES-256-GCM",
    "uptime_sla":        99.99,
})
```

## API Reference

### Types

- `Posture` - Main compliance posture structure
- `Framework` - Compliance framework (SOC2, ISO27001, etc.)
- `Control` - Individual compliance control
- `Organization` - Organization metadata
- `EvidenceRef` - Evidence reference with metadata

### Constants

- `PostureCompliant`, `PosturePartiallyCompliant`, `PostureNonCompliant`, `PostureUnknown`
- `StatusCompliant`, `StatusPartial`, `StatusNonCompliant`
- `ControlCompliant`, `ControlPartial`, `ControlNonCompliant`

### Functions

- `NewPosture()` - Create a new posture
- `NewFramework(name, status, score)` - Create a new framework
- `NewControl(id, status)` - Create a new control
- `Handler(provider)` - Create HTTP handler
- `Middleware(provider, next)` - Create middleware
- `RegisterHandler(mux, provider)` - Register handler on ServeMux

## Testing Your Endpoint

```bash
# Get compliance posture
curl http://localhost:8080/cpx

# Pretty print
curl http://localhost:8080/cpx | jq

# Check specific framework
curl http://localhost:8080/cpx | jq '.frameworks[] | select(.name=="SOC2")'
```

## License

MIT License
