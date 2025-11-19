# OpenCPX .NET SDK

A lightweight .NET SDK for implementing OpenCPX compliance posture endpoints.

## Installation

### NuGet

```bash
dotnet add package OpenCPX
```

### Package Manager

```powershell
Install-Package OpenCPX
```

## Quick Start

### Basic Usage

```csharp
using OpenCPX;

// Create a posture
var posture = new Posture(CompliancePosture.Compliant);

// Set organization
posture.SetOrganization(new Organization(
    "Acme Corp",
    "acme.com",
    "security@acme.com"
));

// Add SOC 2 framework
var soc2 = new Framework("SOC2", FrameworkStatus.Compliant, 1.0)
{
    Version = "Type II",
    LastAudit = "2024-01-15",
    Auditor = "BigFour Audit LLP"
};

// Add controls
soc2.AddControl(new Control("CC1.1", ControlStatus.Compliant, "Demonstrates Commitment to Integrity")
    .AddEvidenceRef("https://evidence.acme.com/code-of-conduct.pdf"));

posture.AddFramework(soc2);

// Convert to JSON
string json = posture.ToJsonPretty();
Console.WriteLine(json);
```

### ASP.NET Core Minimal API

```csharp
using OpenCPX;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

app.MapCpxEndpoint(() =>
{
    var posture = new Posture(CompliancePosture.Compliant);

    posture.SetOrganization(new Organization(
        "Acme Corp",
        "acme.com",
        "security@acme.com"
    ));

    posture.AddFramework(new Framework("SOC2", FrameworkStatus.Compliant, 1.0));

    return posture;
});

app.Run();
```

### ASP.NET Core with Dependency Injection

```csharp
using OpenCPX;

var builder = WebApplication.CreateBuilder(args);

// Register your compliance service
builder.Services.AddScoped<IComplianceService, ComplianceService>();

var app = builder.Build();

app.MapCpxEndpointAsync(async () =>
{
    using var scope = app.Services.CreateScope();
    var complianceService = scope.ServiceProvider.GetRequiredService<IComplianceService>();

    return await complianceService.GetPostureAsync();
});

app.Run();
```

### Using Middleware

```csharp
using OpenCPX;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

app.UseCpx(() =>
{
    var posture = new Posture(CompliancePosture.Compliant);
    posture.AddFramework(new Framework("SOC2", FrameworkStatus.Compliant, 1.0));
    return posture;
});

app.MapGet("/", () => "Hello World!");

app.Run();
```

## Usage Patterns

### Dynamic Posture Generation

```csharp
app.MapCpxEndpointAsync(async () =>
{
    using var scope = app.Services.CreateScope();
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();

    // Query your database for current compliance status
    var controls = await db.ComplianceControls.ToListAsync();

    var posture = new Posture();

    var framework = new Framework("SOC2", FrameworkStatus.Compliant, 0.95);
    foreach (var ctrl in controls)
    {
        framework.AddControl(new Control(
            ctrl.Id,
            Enum.Parse<ControlStatus>(ctrl.Status),
            ctrl.Title
        ));
    }

    posture.AddFramework(framework);
    posture.SetPosture(posture.CalculateOverallPosture());

    return posture;
});
```

### Multiple Frameworks

```csharp
var posture = new Posture(CompliancePosture.PartiallyCompliant);

// SOC 2
posture.AddFramework(new Framework("SOC2", FrameworkStatus.Compliant, 1.0));

// ISO 27001
posture.AddFramework(new Framework("ISO27001", FrameworkStatus.Partial, 0.85));

// HIPAA
posture.AddFramework(new Framework("HIPAA", FrameworkStatus.Compliant, 1.0));
```

### Custom Extensions

```csharp
posture.AddExtension("acme", new Dictionary<string, object>
{
    ["customer_count"] = 5000,
    ["data_centers"] = new[] { "us-east-1", "eu-west-1" },
    ["encryption"] = "AES-256-GCM",
    ["uptime_sla"] = 99.99
});
```

### Evidence References

```csharp
posture.AddEvidenceRef(new Dictionary<string, object>
{
    ["type"] = "presigned_url",
    ["url"] = "https://s3.aws.com/evidence/audit.pdf?signature=...",
    ["expires"] = "2024-01-24T00:00:00Z",
    ["hash"] = "sha256:a1b2c3...",
    ["description"] = "Complete audit package"
});
```

## API Reference

### Classes

- `Posture` - Main compliance posture structure
- `Framework` - Compliance framework (SOC2, ISO27001, etc.)
- `Control` - Individual compliance control
- `Organization` - Organization metadata

### Enums

- `CompliancePosture` - Compliant, PartiallyCompliant, NonCompliant, Unknown
- `FrameworkStatus` - Compliant, Partial, NonCompliant
- `ControlStatus` - Compliant, Partial, NonCompliant

### Extension Methods

- `MapCpxEndpoint(postureProvider)` - Map /cpx endpoint with sync provider
- `MapCpxEndpointAsync(postureProvider)` - Map /cpx endpoint with async provider
- `UseCpx(postureProvider)` - Add CPX middleware

## Testing Your Endpoint

```bash
# Get compliance posture
curl http://localhost:5000/cpx

# Pretty print
curl http://localhost:5000/cpx | jq

# Check specific framework
curl http://localhost:5000/cpx | jq '.frameworks[] | select(.name=="SOC2")'
```

## Requirements

- .NET 8.0+
- ASP.NET Core (for web integration)

## Building

```bash
dotnet build
dotnet pack
```

## License

MIT License
