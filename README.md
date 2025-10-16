# 🧩 OpenCTS — Compliance Telemetry Standard

**OpenCTS** is a **lightweight, extensible, and vendor-neutral standard** for exposing **compliance telemetry** from SaaS platforms.  
It helps SaaS providers, auditors, and GRC platforms exchange compliance posture in a **machine-readable** format — like how Prometheus exposes metrics, or how OpenTelemetry exposes traces.

---

## 🚀 Why OpenCTS?

Today, compliance verification across SaaS tools is manual and inconsistent.  
Auditors or compliance platforms (like Vanta, LowerPlane, or Drata) have to:
- Query APIs in non-standard ways  
- Manually map controls to SOC2, ISO, or HIPAA  
- Ask for zip files or evidence exports  

**OpenCTS** standardizes this by allowing every SaaS product to expose a simple `/cts` endpoint that returns the product’s compliance posture.

---

## 🧠 Core Concept

Every SaaS platform can expose a simple JSON (or YAML/XML) payload at `/cts`:

```json
{
  "version": "v1",
  "timestamp": "2025-10-16T12:00:00Z",
  "compliance_posture": "partially_compliant",
  "frameworks": [
    { "name": "SOC2", "status": "partial", "score": 0.7 },
    { "name": "ISO27001", "status": "compliant", "score": 1.0 }
  ],
  "evidence_refs": ["s3://example-bucket/evidence/soc2.zip"]
}
```

Auditors or compliance tools can fetch this and **instantly understand**:
- What frameworks are implemented
- How compliant the account is
- Where evidence files or references are stored (optional)

---

## 🏗️ Repository Layout

| Directory | Description |
|------------|-------------|
| **`spec/`** | JSON/YAML/XML schemas, OpenAPI spec, and examples |
| **`sdk/`** | Lightweight SDKs for Go, Python, and JS to embed CTS |
| **`integrations/`** | Adapters for tools like Vanta, Drata, LowerPlane |
| **`collectors/`** | Prometheus exporters and OTEL bridges |
| **`docs/`** | Architecture, governance, roadmap |
| **`tests/`** | Schema validators and SDK test cases |
| **`community/`** | Governance, contributing guide, and working group info |

---

## 🌍 Example Endpoint

| HTTP Method | Path | Description |
|--------------|------|-------------|
| `GET` | `/cts` | Returns compliance posture JSON |
| `GET` | `/cts?format=yaml` | Returns same data in YAML format |

---

## 🧩 Key Features

- ✅ Vendor-neutral and SaaS-friendly
- 🔄 Incremental compliance reporting
- 🧱 Evidence-aware (supports presigned URLs or references)
- 🌐 Cross-platform SDKs
- 📊 Integrates with Prometheus / OTEL metrics
- 🧾 Easy to implement (one endpoint, one JSON schema)

---

## 🔒 Supported Frameworks (examples)

| Framework | Example Controls |
|------------|------------------|
| SOC 2 | CC1.1, CC2.2, CC3.1 |
| ISO 27001 | A.5, A.6 |
| HIPAA | §164.306, §164.308 |
| GDPR | Article 5, Article 32 |
| ISO 20000 | Service Delivery & Incident Mgmt |
| ISO 42001 | AI System Governance |

---

## 🔗 Evidence Flow

Evidence is never exposed directly.  
Instead, CTS supports **hash references** or **secure presigned URLs**.  
Each SaaS platform defines its own retrieval or attestation model.

---

## 🛠 Example SDKs

- `sdk/go` → Exposes `/cts` endpoint for Go services  
- `sdk/python` → Generate CTS payloads dynamically  
- `sdk/js` → Integrate with frontend compliance dashboards  

---

## 🤝 Governance

OpenCTS is designed to be **community-governed**, not vendor-controlled.  
Working groups define schema versions, SDK evolution, and interoperability.

- Governance docs → [`docs/governance/`](docs/governance/)
- Roadmap → [`docs/roadmap.md`](docs/roadmap.md)

---

## 📬 Get Involved

We’re building a global standard for compliance telemetry.

- 💬 Join working group meetings → `community/meetings/`
- 🧠 Read the spec → [`spec/v1/spec.md`](spec/v1/spec.md)
- 🧑‍💻 Implement a CTS endpoint in your SaaS

---

## 📜 License
MIT License © 2025 OpenCTS Working Group
