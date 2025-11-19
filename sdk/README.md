# OpenCPX SDKs

Official SDKs for implementing OpenCPX compliance posture endpoints in your applications.

## Available SDKs

| SDK | Language | Description | Status |
|-----|----------|-------------|--------|
| [Go SDK](./go) | Go | HTTP handlers and middleware for Go services | Stable |
| [Python SDK](./python) | Python | Flask, FastAPI, and Django integration | Stable |
| [JavaScript SDK](./js) | TypeScript/JS | Express, Fastify, Koa, and Next.js integration | Stable |
| [Java SDK](./java) | Java | Spring Boot and Jakarta Servlet integration | Stable |
| [.NET SDK](./dotnet) | C# | ASP.NET Core integration | Stable |

## Quick Comparison

### Go

```go
import cpx "github.com/opencpx/sdk-go"

provider := func() (*cpx.Posture, error) {
    posture := cpx.NewPosture().SetPosture(cpx.PostureCompliant)
    posture.AddFramework(cpx.NewFramework("SOC2", cpx.StatusCompliant, 1.0))
    return posture, nil
}

cpx.RegisterHandler(mux, provider)
```

### Python

```python
from opencpx import Posture, Framework, FrameworkStatus, CompliancePosture

def get_posture():
    posture = Posture(compliance_posture=CompliancePosture.COMPLIANT)
    posture.add_framework(Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0))
    return posture

# Flask
app.route('/cpx')(create_flask_handler(get_posture))

# FastAPI
app.include_router(create_fastapi_router(get_posture))
```

### JavaScript/TypeScript

```typescript
import { Posture, CompliancePosture, FrameworkStatus, createExpressHandler } from '@opencpx/sdk';

function getPosture(): Posture {
    const posture = new Posture(CompliancePosture.COMPLIANT);
    posture.addFramework({ name: 'SOC2', status: FrameworkStatus.COMPLIANT, score: 1.0 });
    return posture;
}

app.get('/cpx', createExpressHandler(getPosture));
```

### Java

```java
import io.opencpx.*;

@Bean
public CpxController cpxController() {
    return new CpxController(() -> {
        Posture posture = new Posture(CompliancePosture.COMPLIANT);
        posture.addFramework(new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0));
        return posture;
    });
}
```

### C# / .NET

```csharp
using OpenCPX;

app.MapCpxEndpoint(() =>
{
    var posture = new Posture(CompliancePosture.Compliant);
    posture.AddFramework(new Framework("SOC2", FrameworkStatus.Compliant, 1.0));
    return posture;
});
```

## Common Features

All SDKs provide:

- **Type-safe models** for Posture, Framework, Control, and Evidence
- **HTTP handlers** for popular web frameworks
- **Builder patterns** for easy posture construction
- **JSON serialization** with proper formatting
- **Auto-calculation** of overall compliance posture

## Installation

### Go

```bash
go get github.com/opencpx/sdk-go
```

### Python

```bash
pip install opencpx
# With extras: pip install opencpx[flask,fastapi]
```

### JavaScript/TypeScript

```bash
npm install @opencpx/sdk
```

### Java (Maven)

```xml
<dependency>
    <groupId>io.opencpx</groupId>
    <artifactId>opencpx-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### .NET

```bash
dotnet add package OpenCPX
```

## Testing Your Endpoint

Once implemented, test your endpoint:

```bash
# Basic request
curl http://localhost:8080/cpx

# Pretty print
curl http://localhost:8080/cpx | jq

# Check frameworks
curl http://localhost:8080/cpx | jq '.frameworks'

# Verify specific framework
curl http://localhost:8080/cpx | jq '.frameworks[] | select(.name=="SOC2")'
```

## Compliance Posture Response

All SDKs produce the same JSON structure:

```json
{
  "version": "v1",
  "timestamp": "2024-01-15T12:00:00Z",
  "compliance_posture": "compliant",
  "organization": {
    "name": "Acme Corp",
    "domain": "acme.com",
    "contact": "security@acme.com"
  },
  "frameworks": [
    {
      "name": "SOC2",
      "version": "Type II",
      "status": "compliant",
      "score": 1.0,
      "last_audit": "2024-01-15",
      "controls": [
        {
          "id": "CC1.1",
          "title": "Demonstrates Commitment to Integrity",
          "status": "compliant",
          "evidence_refs": ["https://evidence.acme.com/code-of-conduct.pdf"]
        }
      ]
    }
  ]
}
```

## Contributing

See the [contributing guide](../community/CONTRIBUTING.md) for information on how to contribute to the SDKs.

## License

MIT License - see individual SDK directories for details.
