# OpenCTS v1 Specification

**Version:** 1.0
**Status:** Draft
**Release Date:** 2025-10-16

---

## 1. Overview

OpenCTS defines a **standardized telemetry schema** and **API exposure model**
for SaaS platforms to report their **compliance posture** and **evidence references**.

### Design Principles and Rationale

Each design principle addresses a specific pain point in today's compliance landscape:

| Principle | Pain Point It Solves | Real-World Example |
|-----------|---------------------|-------------------|
| **Lightweight** — minimal mandatory fields | Vendors avoid implementing complex standards that require months of engineering work | A 5-person startup can expose `/cts` in a day, not 6 months |
| **Incremental** — partial compliance allowed | Binary "compliant/not compliant" doesn't reflect reality; vendors working toward compliance can still participate | A vendor with 45/50 SOC2 controls implemented can show `score: 0.9` instead of being excluded |
| **Adaptable** — supports custom frameworks and controls | New regulations (AI Act, DORA) emerge faster than standards can update | Vendors can add new frameworks immediately without waiting for spec updates |
| **Secure** — evidence references, not raw data | Embedding sensitive documents in API responses creates security and privacy risks | Evidence files are accessed via secure, time-limited, auditable URLs |

### Why These Fields?

Every field in OpenCTS exists because of a specific problem:

**`timestamp`** — Auditors constantly ask "when was this data collected?" Without timestamps, they can't determine if compliance data is current.

**`compliance_posture`** — Executives need a single answer: "Are we compliant?" This field provides that rollup while details live in `frameworks`.

**`frameworks[]`** — Every vendor is asked about multiple frameworks. Without structure, responses come as scattered emails and PDFs.

**`evidence_refs[]`** — The #1 time sink in audits is evidence collection. Structured references eliminate email chains and portal logins.

---

## 2. Telemetry Object

The root telemetry object is intentionally minimal. Every required field exists because auditors and compliance platforms need it in 100% of cases.

| Field | Type | Description | Required | Why It's Here |
|--------|------|-------------|-----------|---------------|
| `version` | string | Schema version (e.g., `v1`) | Yes | **Prevents breaking changes**: Consumers know how to parse the response. Without versioning, schema changes break integrations. |
| `timestamp` | string (ISO 8601) | When the compliance posture was generated | Yes | **Freshness verification**: Auditors need to know if data is stale. A 6-month-old SOC2 report may no longer be valid. |
| `compliance_posture` | string | `compliant`, `partially_compliant`, `non_compliant`, `unknown` | Yes | **Executive summary**: Leaders and sales teams need a one-word answer. Details are in frameworks. |
| `frameworks` | array | List of frameworks and their compliance details | Yes | **Multi-framework reality**: Most vendors are asked about SOC2 AND ISO27001 AND GDPR. One array handles all. |
| `evidence_refs` | array | References to external evidence files or hashes | No | **Optional because**: Some vendors prefer control-level evidence only. Bulk evidence packages are supplementary. |

### Why `compliance_posture` Has Four States

**`compliant`** — All frameworks at 100%. Rare but important to distinguish from partial.

**`partially_compliant`** — The most common state. Shows the vendor is working toward compliance and is transparent about gaps.

**`non_compliant`** — Honest vendors who are early in their compliance journey. Better to expose this than hide it.

**`unknown`** — For programmatically generated responses where posture can't be determined. Prevents false positives.

### Real-World Example: Why Timestamp Matters

```json
{
  "timestamp": "2023-06-15T00:00:00Z",  // 7 months old!
  "compliance_posture": "compliant"
}
```

An auditor seeing this in January knows the vendor's SOC2 Type II report has likely expired (they're typically annual). Without the timestamp, they'd assume it's current.

---

## 3. Framework Object

Each `framework` element represents a compliance framework like SOC 2, ISO 27001, or GDPR. The framework object answers: "How compliant is this vendor with framework X?"

| Field | Type | Description | Required | Why It's Here |
|--------|------|-------------|-----------|---------------|
| `name` | string | Framework name (e.g., SOC2) | Yes | **Identification**: Must know which framework we're discussing. Names should match common industry usage. |
| `status` | string | `compliant`, `partial`, or `non_compliant` | Yes | **Quick filter**: Allows "show me all vendors with partial SOC2" queries without parsing scores. |
| `score` | number | Compliance score between 0 and 1 | Yes | **Granularity**: `0.85` vs `0.95` matters when comparing vendors or tracking progress over time. |
| `controls` | array | List of control-level compliance | No | **Depth vs. simplicity**: Small vendors may only report framework-level. Enterprise needs control detail. |

### Why Score is 0-1, Not 0-100 or Percentage

**Consistency**: All scores use the same scale regardless of framework.

**Calculation flexibility**: `0.85` can mean 85% of controls passed, or a weighted risk score.

**Avoids false precision**: `87.3%` implies precision that doesn't exist. `0.87` is appropriately vague.

### Real-World Example: The Score vs. Status Relationship

```json
{
  "name": "SOC2",
  "status": "partial",
  "score": 0.95
}
```

**Why is this partial at 95%?** Because SOC2 requires 100% for "compliant." Missing even one control means partial. The score tells you HOW partial.

Compare to:

```json
{
  "name": "ISO27001",
  "status": "compliant",
  "score": 1.0
}
```

ISO27001 allows some controls to be "not applicable." A score of 1.0 means all applicable controls are met.

### The Controls Array: When to Use It

**Use controls when:**
- Auditors need control-level evidence
- Customers are assessing specific risk areas (e.g., "show me your access controls")
- You want to show exactly where gaps are

**Skip controls when:**
- You're a small vendor with limited compliance resources
- You're just starting and don't have control-level mapping yet
- The framework doesn't have granular controls (some early-stage frameworks)

### Control Object Structure

Each control in the array represents a specific requirement:

| Field | Type | Description | Required |
|--------|------|-------------|-----------|
| `id` | string | Control identifier (e.g., CC6.1, A.8.2) | Yes |
| `title` | string | Human-readable control name | Yes |
| `status` | string | `compliant`, `partial`, `non_compliant` | Yes |
| `evidence_refs` | array | URLs or hashes pointing to evidence | No |
| `reason` | string | Explanation for non-compliant status | No |
| `remediation_date` | string | Expected fix date for gaps | No |

### Why `reason` and `remediation_date` Matter

```json
{
  "id": "CC7.2",
  "title": "Monitors System Components",
  "status": "partial",
  "reason": "SIEM integration in progress",
  "remediation_date": "2024-03-01"
}
```

**Without these fields**: Auditor sees "partial" and must email to ask why.

**With these fields**: Auditor knows exactly what's happening and when it'll be fixed. No back-and-forth.

This saves 1-3 days per audit, per vendor.

---

### Example (JSON)
```json
{
  "version": "v1",
  "timestamp": "2025-10-16T12:00:00Z",
  "compliance_posture": "partially_compliant",
  "frameworks": [
    {
      "name": "SOC2",
      "status": "partial",
      "score": 0.7,
      "controls": [
        {
          "id": "CC1.1",
          "title": "Control Environment",
          "status": "partial",
          "evidence_ref": "s3://evidence/soc2/cc1.1.pdf"
        }
      ]
    },
    {
      "name": "ISO27001",
      "status": "compliant",
      "score": 1.0
    }
  ],
  "evidence_refs": ["s3://compliance-bucket/account123/soc2.zip"]
}
```

---

## 4. Exposure Endpoint

### Why `/cts`?

The endpoint path was chosen for discoverability and consistency:

- **Short and memorable**: Like `/health` or `/metrics`
- **No collisions**: Unlikely to conflict with existing API paths
- **Standard location**: Every OpenCTS-compliant service uses the same path

| Path | Method | Description | Use Case |
|------|---------|-------------|----------|
| `/cts` | GET | Returns compliance telemetry JSON | Primary endpoint for all consumers |
| `/cts/metrics` | GET | Prometheus/OTEL format | Monitoring dashboards, alerting |

### Authentication Considerations

OpenCTS does not mandate authentication, but recommends:

**Public `/cts`** — For trust center replacement. Shows framework-level status, no sensitive details.

**Authenticated `/cts`** — For customer-specific views with control-level details and evidence.

**Example of tiered access:**

```
# Public (no auth)
GET /cts
→ Returns framework scores only

# Customer (API key)
GET /cts
Authorization: Bearer <customer_api_key>
→ Returns full control details and evidence URLs

# Auditor (special access)
GET /cts
Authorization: Bearer <auditor_token>
→ Returns everything including presigned evidence URLs
```

### Why Prometheus/OTEL Metrics?

Compliance is not just a point-in-time snapshot. Organizations need:

- **Trend tracking**: "Our SOC2 score improved from 0.8 to 0.95 over 6 months"
- **Alerting**: "Alert when any vendor drops below 0.9"
- **Dashboards**: Aggregate view of all vendor compliance

```
# Prometheus format
opencts_compliance_score{framework="SOC2",vendor="payflow"} 0.85
opencts_compliance_score{framework="ISO27001",vendor="payflow"} 1.0
opencts_framework_status{framework="SOC2",vendor="payflow",status="partial"} 1
opencts_last_audit_timestamp{framework="SOC2",vendor="payflow"} 1704931200
```

### Response Codes

| Code | Meaning | When to Use |
|------|---------|-------------|
| 200 | Success | Normal response |
| 401 | Unauthorized | Invalid or missing API key for protected endpoints |
| 403 | Forbidden | Valid key but insufficient permissions |
| 503 | Unavailable | Compliance data temporarily unavailable |

**Why 503?** — Better to return "unavailable" than stale data. Consumers can retry.

---

## 5. Evidence Handling

### The Core Principle: References, Not Content

OpenCTS **never transports evidence directly**. Here's why:

**Security risk**: Embedding SOC2 reports, penetration test results, or security policies in API responses exposes sensitive data.

**Size constraints**: Evidence packages can be 100MB+. APIs aren't built for this.

**Access control**: Different consumers need different access levels. References enable this.

### Evidence Reference Types

| Type | Format | Use Case | Example |
|------|--------|----------|---------|
| S3 URL | `s3://bucket/object` | When consumer has AWS access | `s3://evidence/soc2.pdf` |
| Presigned URL | `https://...?signature=...` | Time-limited, auditable access | See below |
| Hash reference | `sha256:abc123...` | When consumer already has file | Integrity verification |
| Trust center URL | `https://trust.vendor.com/...` | Public documentation | SOC2 report cover page |

### Why Presigned URLs Are Preferred

```json
{
  "type": "presigned_url",
  "url": "https://s3.amazonaws.com/evidence/soc2.pdf?AWSAccessKeyId=...&Signature=...&Expires=1706054400",
  "expires": "2024-01-24T00:00:00Z",
  "hash": "sha256:9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"
}
```

**Time-limited**: URL expires after 7 days. No permanent links floating around.

**Auditable**: Vendor can log who generated what URLs.

**No credential sharing**: Consumer doesn't need AWS keys or portal logins.

**Integrity**: Hash allows verification that file wasn't tampered with.

### Real-World Evidence Flow

**Scenario**: Auditor needs to review vendor's access control policy.

**Today (without OpenCTS)**:
1. Auditor emails vendor
2. Vendor finds document (2 days)
3. Vendor uploads to shared drive
4. Vendor emails link with password
5. Password in separate email
6. Auditor downloads
7. Link expires, auditor needs it again, repeat from step 1

**With OpenCTS**:
1. Auditor queries `/cts`
2. Finds control CC6.1 with `evidence_refs`
3. Downloads via presigned URL
4. Verifies hash matches

Time saved: 3-5 days per evidence request.

### Evidence Reference Best Practices

**DO:**
- Use presigned URLs with 7-day expiration
- Include hashes for integrity verification
- Log all evidence access for audit trails
- Organize evidence by control ID

**DON'T:**
- Use permanent public URLs for sensitive documents
- Include credentials in URLs (beyond presigned signatures)
- Embed actual evidence content in the CTS response
- Reference evidence that requires portal login

---

## 6. Extensibility

### Why Extensions Exist

No standard can anticipate every vendor's needs. Extensions allow:

- Industry-specific fields (healthcare, finance, government)
- Vendor-specific metadata
- Experimental features before standardization

### Extension Structure

```json
{
  "version": "v1",
  "compliance_posture": "compliant",
  "frameworks": [...],
  "extensions": {
    "vendor_name": {
      "custom_field": "value",
      "another_field": 123
    }
  }
}
```

### Naming Conventions

**Use your company or product name as the key**:
- `"payflow"`: for PayFlow Inc.
- `"medcloud"`: for MedCloud Health
- `"aws"`: for AWS-specific fields

**Avoid generic names** that might collide:
- Bad: `"custom"`, `"extra"`, `"other"`
- Good: `"stripe"`, `"datadog"`, `"okta"`

### Real-World Extension Examples

**Payment processor**:
```json
"extensions": {
  "payflow": {
    "pci_level": 1,
    "transaction_volume_monthly": "2.3B USD",
    "encryption_standard": "AES-256-GCM"
  }
}
```

**Healthcare SaaS**:
```json
"extensions": {
  "medcloud": {
    "phi_data_types": ["demographics", "medications"],
    "hipaa_breach_history": [],
    "patient_count": 2500000
  }
}
```

**API platform**:
```json
"extensions": {
  "connecthub": {
    "api_rate_limits": {"standard": "1000/min", "enterprise": "10000/min"},
    "data_residency_options": ["us", "eu", "ap"],
    "sso_providers": ["okta", "azure-ad", "google"]
  }
}
```

### Extensions Roadmap

Popular extensions may be promoted to the core spec:

- `control_mappings` — Cross-framework control relationships
- `service_commitments` — SLA, RTO, RPO
- `organization` — Vendor metadata

---

## 7. Compatibility

### Framework Support

CTS is intentionally framework-agnostic. The `name` field accepts any string:

| Framework | Common Names | Notes |
|-----------|-------------|-------|
| SOC 2 | `SOC2`, `SOC2-TypeII` | Most common for SaaS |
| ISO 27001 | `ISO27001`, `ISO27001:2022` | International standard |
| ISO 20000 | `ISO20000` | Service management |
| ISO 42001 | `ISO42001` | AI governance |
| HIPAA | `HIPAA` | Healthcare (US) |
| GDPR | `GDPR` | Privacy (EU) |
| PCI-DSS | `PCI-DSS`, `PCI-DSS-4.0` | Payment cards |
| NIST 800-53 | `NIST-800-53` | Government (US) |
| CIS | `CIS-Benchmarks` | Security baselines |
| FedRAMP | `FedRAMP` | Government cloud (US) |
| AI Act | `EU-AI-Act` | AI regulation (EU, coming) |
| DORA | `DORA` | Financial services (EU, coming) |

### Framework Versioning

Include version in `version` field, not `name`:

```json
{
  "name": "ISO27001",
  "version": "2022",
  "status": "compliant"
}
```

This allows filtering by framework while preserving version history.

### Cross-Framework Mapping

Use `control_mappings` to show how frameworks overlap:

```json
{
  "control_mappings": {
    "SOC2:CC6.1": ["ISO27001:A.9.2", "HIPAA:164.312(a)"],
    "SOC2:CC7.2": ["ISO27001:A.12.4", "PCI-DSS:10.1"]
  }
}
```

**Why this matters**: Auditors can verify one control and apply to multiple frameworks, reducing audit time by 30-50%.

---

## 8. Implementation Checklist

### Minimum Viable Implementation (1 day)

```json
{
  "version": "v1",
  "timestamp": "2024-01-15T12:00:00Z",
  "compliance_posture": "compliant",
  "frameworks": [
    {"name": "SOC2", "status": "compliant", "score": 1.0}
  ]
}
```

That's it. You're OpenCTS compliant.

### Standard Implementation (1 week)

Add controls and evidence:

```json
{
  "version": "v1",
  "timestamp": "2024-01-15T12:00:00Z",
  "compliance_posture": "partially_compliant",
  "frameworks": [
    {
      "name": "SOC2",
      "status": "partial",
      "score": 0.85,
      "controls": [
        {
          "id": "CC6.1",
          "title": "Logical Access Controls",
          "status": "compliant",
          "evidence_refs": ["https://evidence.vendor.com/cc6.1.pdf"]
        }
      ]
    }
  ]
}
```

### Enterprise Implementation (1 month)

Full implementation with:
- Multiple frameworks
- All controls mapped
- Presigned evidence URLs
- Control mappings
- Extensions
- Prometheus/OTEL metrics

See [examples/](examples/) for complete payloads.

---

## 9. Roadmap

### v1.1 (Q2 2024)

**Attestation Signing**: Cryptographically sign CTS responses so consumers can verify authenticity.

```json
{
  "version": "v1.1",
  "signature": "...",
  "signed_by": "vendor.com",
  "certificate": "..."
}
```

### v2 (Q4 2024)

**Real-time Compliance Events**: Webhook or OTEL-based streaming for compliance changes.

```json
{
  "event": "control_status_changed",
  "control_id": "CC6.1",
  "old_status": "compliant",
  "new_status": "non_compliant",
  "reason": "Quarterly access review overdue"
}
```

### vNext (2025+)

**Auto-evidence Validation**: Machine-readable evidence that can be automatically verified.

- Terraform state files validated against security policies
- IAM policies checked against least-privilege requirements
- Encryption configurations verified against standards

---

## 🧾 References
- [Prometheus Exposition Format](https://prometheus.io/docs/instrumenting/exposition_formats/)
- [OpenTelemetry Metrics Data Model](https://opentelemetry.io/)
- [ISO 27001 & SOC 2 Mappings](https://www.iso.org)

---

MIT License © 2025 OpenCTS Working Group
