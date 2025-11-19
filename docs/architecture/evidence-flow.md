# Evidence Flow Architecture

This document describes how compliance evidence flows through OpenCPX.

## Overview

OpenCPX provides a standardized way to expose compliance posture, including references to supporting evidence. The architecture ensures:

- **Security**: Evidence is never exposed directly through the API
- **Flexibility**: Multiple evidence retrieval methods supported
- **Auditability**: Clear chain of custody for evidence
- **Freshness**: Timestamps indicate data currency

## Architecture Diagram

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   SaaS Platform │     │  Compliance     │     │    Auditor      │
│                 │     │  Platform       │     │                 │
│  ┌───────────┐  │     │                 │     │                 │
│  │ Your App  │  │     │   (LowerPlane,  │     │                 │
│  │           │  │     │  Vanta, Drata)  │     │                 │
│  └─────┬─────┘  │     │                 │     │                 │
│        │        │     │                 │     │                 │
│  ┌─────▼─────┐  │     │                 │     │                 │
│  │  /cpx     │◄─┼─────┼────GET──────────┼─────┼──GET────────────┤
│  │ Endpoint  │  │     │                 │     │                 │
│  └─────┬─────┘  │     │                 │     │                 │
│        │        │     │                 │     │                 │
│        ▼        │     │                 │     │                 │
│   JSON/YAML     │     │                 │     │                 │
│   Response      │     │                 │     │                 │
│   (with refs)   │     │                 │     │                 │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         │              ┌────────▼────────┐              │
         │              │  Parse refs,    │              │
         │              │  fetch evidence │              │
         │              └────────┬────────┘              │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Evidence Storage                             │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐             │
│  │   S3    │  │  GCS    │  │  Azure  │  │  Custom │             │
│  │         │  │         │  │  Blob   │  │  Store  │             │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

## Evidence Reference Types

### 1. Direct URLs

Simple, publicly accessible links:

```json
{
  "evidence_refs": [
    "https://evidence.company.com/soc2-report-2024.pdf"
  ]
}
```

**Use when**:
- Evidence is public (e.g., published SOC 2 reports)
- No access control needed

### 2. Presigned URLs

Time-limited, signed URLs for secure access:

```json
{
  "evidence_refs": [
    {
      "type": "presigned_url",
      "url": "https://s3.amazonaws.com/bucket/evidence.pdf?signature=...",
      "expires": "2024-01-24T00:00:00Z",
      "hash": "sha256:a1b2c3..."
    }
  ]
}
```

**Use when**:
- Evidence is private
- Time-limited access required
- Need hash verification

### 3. API References

Endpoints that require authentication:

```json
{
  "evidence_refs": [
    {
      "type": "api",
      "url": "https://api.company.com/evidence/12345",
      "auth_type": "bearer_token"
    }
  ]
}
```

**Use when**:
- Need programmatic access control
- Evidence is generated dynamically
- Complex authorization logic required

### 4. Hash References

Pointers for out-of-band retrieval:

```json
{
  "evidence_refs": [
    {
      "type": "hash",
      "hash": "sha256:a1b2c3d4e5f6...",
      "description": "IAM policy export"
    }
  ]
}
```

**Use when**:
- Evidence shared separately (e.g., via secure portal)
- Integrity verification is primary concern

## Evidence Flow Patterns

### Pattern 1: Real-Time Generation

```
Request → Generate Evidence → Sign URL → Return in Response
```

**Example**: Export current IAM policies on demand

```python
def get_posture():
    # Generate fresh evidence
    iam_export = export_iam_policies()
    url = upload_and_sign(iam_export, expires_in=3600)

    posture = Posture()
    posture.evidence_refs = [{
        "type": "presigned_url",
        "url": url,
        "expires": (datetime.now() + timedelta(hours=1)).isoformat(),
        "hash": calculate_sha256(iam_export)
    }]
    return posture
```

### Pattern 2: Pre-Generated Evidence

```
Periodic Job → Generate Evidence → Store → Reference in Response
```

**Example**: Nightly compliance report generation

```python
# Nightly job
def generate_nightly_evidence():
    report = generate_compliance_report()
    upload_to_s3("evidence/nightly-report.pdf", report)

# API handler
def get_posture():
    posture = Posture()
    posture.evidence_refs = [
        sign_s3_url("evidence/nightly-report.pdf", expires_in=3600)
    ]
    return posture
```

### Pattern 3: Hybrid Approach

```
Static Evidence (reports) + Dynamic Evidence (configs)
```

**Example**: Combine audit reports with live configuration

```python
def get_posture():
    posture = Posture()

    # Static: Annual audit report
    posture.evidence_refs.append({
        "type": "presigned_url",
        "url": sign_url("audit-reports/2024-soc2.pdf"),
        "description": "SOC 2 Type II Report"
    })

    # Dynamic: Current configuration
    config = export_current_config()
    posture.evidence_refs.append({
        "type": "presigned_url",
        "url": upload_and_sign(config),
        "description": "Current security configuration"
    })

    return posture
```

## Security Considerations

### Never Expose Evidence Directly

❌ **Don't do this**:
```json
{
  "evidence": {
    "iam_policy": { ... actual policy content ... }
  }
}
```

✅ **Do this instead**:
```json
{
  "evidence_refs": [
    "https://evidence.company.com/iam-policy.json"
  ]
}
```

### Use Short Expiration Times

Presigned URLs should have short lifetimes:
- **Default**: 1 hour
- **Maximum**: 24 hours
- **For audits**: Generate fresh URLs for each request

### Verify Evidence Integrity

Include hashes for verification:

```json
{
  "evidence_refs": [{
    "url": "https://...",
    "hash": "sha256:a1b2c3...",
    "size_bytes": 1048576
  }]
}
```

Consumers should verify:
1. Downloaded file matches hash
2. File size matches expected

### Audit Access

Log all evidence access:
- Who accessed
- When
- Which evidence
- IP address / user agent

### Principle of Least Privilege

Evidence URLs should only grant read access to specific files, not broad bucket access.

## Implementation Checklist

### For SaaS Providers

- [ ] Choose evidence storage (S3, GCS, Azure, custom)
- [ ] Implement URL signing
- [ ] Set appropriate expiration times
- [ ] Generate hashes for integrity
- [ ] Log all evidence access
- [ ] Document retrieval process for auditors

### For Compliance Platforms

- [ ] Parse evidence references from CPX response
- [ ] Handle different reference types
- [ ] Verify file integrity with hashes
- [ ] Store evidence with audit trail
- [ ] Handle expired URLs gracefully

### For Auditors

- [ ] Understand reference types
- [ ] Verify evidence integrity
- [ ] Document evidence chain of custody
- [ ] Request fresh URLs if expired

## Best Practices

### 1. Organize Evidence by Framework

```
evidence/
├── soc2/
│   ├── cc1.1-code-of-conduct.pdf
│   └── cc6.1-access-controls.json
├── iso27001/
│   └── a5.1-security-policy.pdf
└── shared/
    └── penetration-test-2024.pdf
```

### 2. Use Descriptive Names

```json
{
  "evidence_refs": [{
    "url": "https://...",
    "description": "Q1 2024 access review showing 100% completion"
  }]
}
```

### 3. Version Evidence

Include dates and versions in filenames or metadata:
- `soc2-report-2024-01.pdf`
- `iam-policy-v3.2.json`

### 4. Automate Evidence Generation

Don't manually create evidence. Automate:
- Configuration exports
- Log aggregation
- Policy snapshots
- Training completion reports

## Troubleshooting

### Expired URLs

**Problem**: Presigned URL has expired

**Solution**: Fetch fresh CPX response to get new URL

### Hash Mismatch

**Problem**: Downloaded file doesn't match hash

**Causes**:
- File was updated after posture generated
- Transmission error
- Potential tampering

**Solution**: Request fresh posture, contact provider if persists

### Access Denied

**Problem**: Cannot access evidence URL

**Causes**:
- URL expired
- IP restrictions
- Missing authentication

**Solution**: Check reference type, ensure proper auth is provided
