# OpenCPX Python SDK

A lightweight Python SDK for implementing OpenCPX compliance posture endpoints.

## Installation

```bash
pip install opencpx

# With Flask support
pip install opencpx[flask]

# With FastAPI support
pip install opencpx[fastapi]

# With Django support
pip install opencpx[django]
```

## Quick Start

### Basic Usage

```python
from opencpx import (
    Posture,
    Framework,
    Control,
    Organization,
    CompliancePosture,
    FrameworkStatus,
    ControlStatus,
)

# Create a posture
posture = Posture(compliance_posture=CompliancePosture.COMPLIANT)

# Set organization
posture.set_organization(Organization(
    name="Acme Corp",
    domain="acme.com",
    contact="security@acme.com"
))

# Add SOC 2 framework
soc2 = Framework(
    name="SOC2",
    status=FrameworkStatus.COMPLIANT,
    score=1.0,
    version="Type II",
    last_audit="2024-01-15",
    auditor="BigFour Audit LLP"
)

# Add controls
soc2.add_control(Control(
    id="CC1.1",
    status=ControlStatus.COMPLIANT,
    title="Demonstrates Commitment to Integrity",
    evidence_refs=["https://evidence.acme.com/code-of-conduct.pdf"]
))

posture.add_framework(soc2)

# Convert to JSON
print(posture.to_json(indent=2))
```

### Flask Integration

```python
from flask import Flask
from opencpx import (
    create_flask_handler,
    Posture,
    Framework,
    FrameworkStatus,
    CompliancePosture,
)

app = Flask(__name__)

def get_posture():
    posture = Posture(compliance_posture=CompliancePosture.COMPLIANT)
    posture.add_framework(Framework(
        name="SOC2",
        status=FrameworkStatus.COMPLIANT,
        score=1.0
    ))
    return posture

app.route('/cpx')(create_flask_handler(get_posture))

if __name__ == '__main__':
    app.run(port=8080)
```

### FastAPI Integration

```python
from fastapi import FastAPI
from opencpx import (
    create_fastapi_router,
    Posture,
    Framework,
    FrameworkStatus,
    CompliancePosture,
)

app = FastAPI()

def get_posture():
    posture = Posture(compliance_posture=CompliancePosture.COMPLIANT)
    posture.add_framework(Framework(
        name="SOC2",
        status=FrameworkStatus.COMPLIANT,
        score=1.0
    ))
    return posture

router = create_fastapi_router(get_posture)
app.include_router(router)
```

### Django Integration

```python
# views.py
from opencpx import (
    create_django_view,
    Posture,
    Framework,
    FrameworkStatus,
    CompliancePosture,
)

def get_posture():
    posture = Posture(compliance_posture=CompliancePosture.COMPLIANT)
    posture.add_framework(Framework(
        name="SOC2",
        status=FrameworkStatus.COMPLIANT,
        score=1.0
    ))
    return posture

cpx_view = create_django_view(get_posture)

# urls.py
from django.urls import path
from .views import cpx_view

urlpatterns = [
    path('cpx', cpx_view),
]
```

## Usage Patterns

### Dynamic Posture Generation

```python
def get_posture():
    # Query your database for current compliance status
    controls = db.get_compliance_controls()

    posture = Posture()

    # Build frameworks dynamically
    framework = Framework(
        name="SOC2",
        status=FrameworkStatus.COMPLIANT,
        score=0.95
    )

    for ctrl in controls:
        framework.add_control(Control(
            id=ctrl.id,
            status=ControlStatus(ctrl.status),
            title=ctrl.title,
            evidence_refs=ctrl.evidence_urls
        ))

    posture.add_framework(framework)
    posture.compliance_posture = posture.calculate_overall_posture()

    return posture
```

### Multiple Frameworks

```python
posture = Posture(compliance_posture=CompliancePosture.PARTIALLY_COMPLIANT)

# SOC 2
posture.add_framework(Framework(
    name="SOC2",
    status=FrameworkStatus.COMPLIANT,
    score=1.0
))

# ISO 27001
posture.add_framework(Framework(
    name="ISO27001",
    status=FrameworkStatus.PARTIAL,
    score=0.85
))

# HIPAA
posture.add_framework(Framework(
    name="HIPAA",
    status=FrameworkStatus.COMPLIANT,
    score=1.0
))
```

### Custom Extensions

```python
posture.add_extension("acme", {
    "customer_count": 5000,
    "data_centers": ["us-east-1", "eu-west-1"],
    "encryption": "AES-256-GCM",
    "uptime_sla": 99.99
})
```

### Evidence References

```python
from opencpx import EvidenceRef

posture.evidence_refs = [
    EvidenceRef(
        type="presigned_url",
        url="https://s3.aws.com/evidence/audit.pdf?signature=...",
        expires="2024-01-24T00:00:00Z",
        hash="sha256:a1b2c3...",
        description="Complete audit package"
    )
]
```

## API Reference

### Classes

- `Posture` - Main compliance posture structure
- `Framework` - Compliance framework (SOC2, ISO27001, etc.)
- `Control` - Individual compliance control
- `Organization` - Organization metadata
- `EvidenceRef` - Evidence reference with metadata

### Enums

- `CompliancePosture` - COMPLIANT, PARTIALLY_COMPLIANT, NON_COMPLIANT, UNKNOWN
- `FrameworkStatus` - COMPLIANT, PARTIAL, NON_COMPLIANT
- `ControlStatus` - COMPLIANT, PARTIAL, NON_COMPLIANT

### Handler Functions

- `create_flask_handler(provider)` - Create Flask route handler
- `create_fastapi_router(provider)` - Create FastAPI router
- `create_django_view(provider)` - Create Django view

## Testing Your Endpoint

```bash
# Get compliance posture
curl http://localhost:8080/cpx

# Pretty print
curl http://localhost:8080/cpx | python -m json.tool

# Check specific framework
curl http://localhost:8080/cpx | jq '.frameworks[] | select(.name=="SOC2")'
```

## License

MIT License
