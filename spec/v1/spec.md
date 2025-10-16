# 🧩 OpenCTS v1 Specification

**Version:** 1.0  
**Status:** Draft  
**Release Date:** 2025-10-16  

---

## 1️⃣ Overview

OpenCTS defines a **standardized telemetry schema** and **API exposure model**  
for SaaS platforms to report their **compliance posture** and **evidence references**.

It is designed to be:
- **Lightweight** — minimal mandatory fields
- **Incremental** — partial compliance allowed
- **Adaptable** — supports custom frameworks and controls
- **Secure** — evidence references, not raw data

---

## 2️⃣ Telemetry Object

| Field | Type | Description | Required |
|--------|------|-------------|-----------|
| `version` | string | Schema version (e.g., `v1`) | ✅ |
| `timestamp` | string (ISO 8601) | Time when the compliance posture was generated | ✅ |
| `compliance_posture` | string | One of: `compliant`, `partially_compliant`, `non_compliant`, `unknown` | ✅ |
| `frameworks` | array | List of frameworks and their compliance details | ✅ |
| `evidence_refs` | array | Optional references to external evidence files or hashes | ❌ |

---

## 3️⃣ Framework Object

Each `framework` element represents a compliance framework like SOC 2, ISO 27001, or GDPR.

| Field | Type | Description | Required |
|--------|------|-------------|-----------|
| `name` | string | Framework name (e.g., SOC2) | ✅ |
| `status` | string | `compliant`, `partial`, or `non_compliant` | ✅ |
| `score` | number | Compliance score between 0 and 1 | ✅ |
| `controls` | array | List of control-level compliance | ❌ |

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

## 4️⃣ Exposure Endpoint

| Path | Method | Description |
|------|---------|-------------|
| `/cts` | GET | Returns current compliance telemetry JSON |
| `/cts/metrics` | GET | Optional Prometheus/OTEL metrics exposure |

---

### Example (Prometheus Style)
```
opencts_compliance_score{framework="SOC2"} 0.7
opencts_compliance_score{framework="ISO27001"} 1.0
```

---

## 5️⃣ Evidence Handling

OpenCTS **does not transport evidence** — it references it:
- `s3://bucket/object`
- `https://pre.signed.url/file.zip`
- `sha256:abcdef123...` (hash reference)

Evidence flow may be:
1. **Inline Reference** (simple URL or hash)
2. **Presigned URL Pull** (auditor retrieves)
3. **Attestation Push** (signed validation event)

---

## 6️⃣ Extensibility

Vendors can define additional fields under:
```json
"extensions": {
  "vendor_name": { "field": "value" }
}
```

---

## 7️⃣ Compatibility

CTS is framework-agnostic but compatible with:
- SOC 2, ISO 27001/20000/42001
- HIPAA, GDPR, PCI-DSS
- NIST 800-53, CIS
- AI Act (future)

---

## 8️⃣ Next Steps

- **v1.1** → Attestation signing  
- **v2** → Realtime compliance events via Webhook / OTEL  
- **vNext** → Auto-evidence validation

---

## 🧾 References
- [Prometheus Exposition Format](https://prometheus.io/docs/instrumenting/exposition_formats/)
- [OpenTelemetry Metrics Data Model](https://opentelemetry.io/)
- [ISO 27001 & SOC 2 Mappings](https://www.iso.org)

---

MIT License © 2025 OpenCTS Working Group
