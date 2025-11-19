# OpenCPX Quickstart Guide

Get your first OpenCPX endpoint running in 5 minutes.

## Prerequisites

Choose your language:
- **Go**: Go 1.21+
- **Python**: Python 3.9+
- **JavaScript**: Node.js 18+

## Step 1: Install the SDK

### Go
```bash
go get github.com/opencpx/sdk-go
```

### Python
```bash
pip install opencpx
```

### JavaScript
```bash
npm install @opencpx/sdk
```

## Step 2: Create Your Endpoint

### Go

```go
package main

import (
    "net/http"
    cpx "github.com/opencpx/sdk-go"
)

func main() {
    provider := func() (*cpx.Posture, error) {
        posture := cpx.NewPosture().
            SetPosture(cpx.PostureCompliant).
            SetOrganization(cpx.Organization{
                Name:   "My Company",
                Domain: "mycompany.com",
            })

        posture.AddFramework(cpx.NewFramework("SOC2", cpx.StatusCompliant, 1.0))

        return posture, nil
    }

    mux := http.NewServeMux()
    cpx.RegisterHandler(mux, provider)

    println("Server running on :8080")
    http.ListenAndServe(":8080", mux)
}
```

### Python (Flask)

```python
from flask import Flask
from opencpx import (
    create_flask_handler,
    Posture,
    Framework,
    Organization,
    CompliancePosture,
    FrameworkStatus,
)

app = Flask(__name__)

def get_posture():
    posture = Posture(compliance_posture=CompliancePosture.COMPLIANT)
    posture.set_organization(Organization(
        name="My Company",
        domain="mycompany.com"
    ))
    posture.add_framework(Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0))
    return posture

app.route('/cpx')(create_flask_handler(get_posture))

if __name__ == '__main__':
    print("Server running on :8080")
    app.run(port=8080)
```

### JavaScript (Express)

```javascript
const express = require('express');
const {
    Posture,
    CompliancePosture,
    FrameworkStatus,
    createExpressHandler
} = require('@opencpx/sdk');

const app = express();

function getPosture() {
    const posture = new Posture(CompliancePosture.COMPLIANT);
    posture.setOrganization({
        name: 'My Company',
        domain: 'mycompany.com'
    });
    posture.addFramework({
        name: 'SOC2',
        status: FrameworkStatus.COMPLIANT,
        score: 1.0
    });
    return posture;
}

app.get('/cpx', createExpressHandler(getPosture));

console.log('Server running on :8080');
app.listen(8080);
```

## Step 3: Run Your Server

### Go
```bash
go run main.go
```

### Python
```bash
python app.py
```

### JavaScript
```bash
node app.js
```

## Step 4: Test Your Endpoint

```bash
# Basic request
curl http://localhost:8080/cpx

# Pretty print
curl http://localhost:8080/cpx | jq
```

You should see:

```json
{
  "version": "v1",
  "timestamp": "2024-01-15T12:00:00Z",
  "compliance_posture": "compliant",
  "organization": {
    "name": "My Company",
    "domain": "mycompany.com"
  },
  "frameworks": [
    {
      "name": "SOC2",
      "status": "compliant",
      "score": 1.0
    }
  ]
}
```

## Next Steps

### Add More Detail

Add controls to your frameworks:

```python
framework = Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0)
framework.add_control(Control(
    id="CC1.1",
    status=ControlStatus.COMPLIANT,
    title="Demonstrates Commitment to Integrity",
    evidence_refs=["https://evidence.mycompany.com/code-of-conduct.pdf"]
))
```

### Add Multiple Frameworks

```python
posture.add_framework(Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0))
posture.add_framework(Framework("ISO27001", FrameworkStatus.PARTIAL, 0.85))
posture.add_framework(Framework("HIPAA", FrameworkStatus.COMPLIANT, 1.0))
```

### Connect to Real Data

Instead of hardcoded values, query your systems:

```python
def get_posture():
    # Query your compliance database
    compliance_data = db.get_compliance_status()

    posture = Posture()
    for framework in compliance_data.frameworks:
        posture.add_framework(Framework(
            name=framework.name,
            status=map_status(framework.status),
            score=framework.score
        ))

    return posture
```

### Add Evidence References

```python
posture.evidence_refs = [
    {
        "type": "presigned_url",
        "url": generate_presigned_url("soc2-report.pdf"),
        "expires": "2024-01-24T00:00:00Z",
        "description": "SOC 2 Type II Report"
    }
]
```

## Resources

- [Implementation Guide](implementation-guide.md) - Detailed scenarios
- [SDK Documentation](../sdk/) - Full SDK reference
- [Examples](../spec/v1/examples/) - Real-world JSON examples
- [Schema Reference](../spec/v1/cpx-schema.json) - Full schema

## Common Issues

### Port Already in Use

Change the port number in your code or stop the other process.

### Module Not Found

Ensure you've installed the SDK:
```bash
# Go
go mod tidy

# Python
pip install opencpx

# JavaScript
npm install
```

### JSON Formatting

Use `jq` to format output:
```bash
curl http://localhost:8080/cpx | jq
```

## Getting Help

- Check the [FAQ](FAQ.md)
- Open a [GitHub Issue](https://github.com/opencpx/OpenCPX/issues)
- Join working group meetings
