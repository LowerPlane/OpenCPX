# OpenCTS — Compliance Telemetry Standard

**OpenCTS** is a **lightweight, extensible, and vendor-neutral standard** for exposing **compliance telemetry** from SaaS platforms.
It helps SaaS providers, auditors, and GRC platforms exchange compliance posture in a **machine-readable** format — like how Prometheus exposes metrics, or how OpenTelemetry exposes traces.

---

## Why OpenCTS Exists

### The Problem Today

Imagine you're a compliance officer at a mid-sized company using 47 different SaaS tools. Your SOC 2 Type II audit is in 3 weeks. Here's what your week looks like:

**Monday**: Email Slack asking for their security documentation. They reply with a 200-page PDF and a link to their trust center.

**Tuesday**: Log into AWS console, manually screenshot IAM policies, S3 bucket configurations, and CloudTrail settings. Export to a folder called "AWS Evidence Q4".

**Wednesday**: Your auditor asks "Does your payment processor meet PCI-DSS requirements?" You spend 4 hours on Stripe's support chat trying to get their compliance certificates.

**Thursday**: Realize your CI/CD tool (GitHub Actions) needs evidence too. Search through GitHub's docs for 2 hours to find their SOC 2 report. It's from last year.

**Friday**: Try to map all this evidence to your control matrix. Discover that Slack's documentation uses different control IDs than your framework. Spend the day manually translating controls.

**This is broken.**

---

## Real-World Pain Points

### Pain Point 1: No Standard Way to Ask "Are You Compliant?"

**The Scenario**: A fintech startup uses 23 SaaS vendors. Their auditor needs to verify each vendor's compliance status for SOC 2.

**What Happens Today**:
```
Auditor: "Please provide SOC 2 compliance evidence for all your vendors"

Company: *Spends 3 weeks*
- Emails 23 vendors
- Gets 15 different formats (PDFs, spreadsheets, portal links, "contact sales")
- 4 vendors don't respond
- 2 vendors require NDAs before sharing anything
- 1 vendor sends a 500MB zip file with no documentation
```

**What Should Happen (with OpenCTS)**:
```bash
# Query all vendors in 5 minutes
for vendor in vendors.txt; do
  curl -s "https://$vendor/cts" | jq '.frameworks[] | select(.name=="SOC2")'
done
```

### Pain Point 2: Manual Control Mapping is Error-Prone

**The Scenario**: An auditor needs to verify that a company's infrastructure meets control CC6.1 (Logical Access Controls).

**What Happens Today**:
1. Auditor asks: "Show me evidence for CC6.1"
2. Company provides: AWS IAM screenshots, Okta export, GitHub team permissions
3. Auditor manually determines if this evidence actually covers CC6.1
4. Auditor finds gaps, asks for more evidence
5. Repeat 3-4 times per control
6. Multiply by 60+ controls

**Time spent**: 40+ hours per audit just on control mapping

**With OpenCTS**:
```json
{
  "frameworks": [{
    "name": "SOC2",
    "controls": [{
      "id": "CC6.1",
      "title": "Logical Access Controls",
      "status": "compliant",
      "evidence_refs": [
        "https://evidence.company.com/iam-policy-2024.pdf",
        "https://evidence.company.com/okta-config.json"
      ]
    }]
  }]
}
```

The vendor has already mapped their evidence to controls. The auditor knows exactly what to review.

### Pain Point 3: Compliance Data is Stale

**The Scenario**: You onboarded a SaaS vendor 8 months ago. They were SOC 2 compliant then. Are they still?

**What Happens Today**:
- You have no idea
- Their annual report expired 2 months ago
- You only find out when your auditor flags it
- Now you're scrambling during audit season

**With OpenCTS**:
```bash
# Set up monitoring for all vendors
curl https://vendor.com/cts | jq '.timestamp, .compliance_posture'
# "2024-01-15T08:00:00Z"
# "compliant"

# Alert when status changes or timestamp is stale
```

### Pain Point 4: Evidence Retrieval is a Security Nightmare

**The Scenario**: Auditor needs to download evidence files from your vendors.

**What Happens Today**:
- Vendor emails a password-protected zip file
- Password sent in separate email (security theater)
- Link expires, auditor needs to request again
- No audit trail of who accessed what
- Files sitting in email inboxes indefinitely

**With OpenCTS**:
```json
{
  "evidence_refs": [
    {
      "type": "presigned_url",
      "url": "https://s3.aws.com/evidence/soc2.pdf?signature=...",
      "expires": "2024-01-20T00:00:00Z",
      "hash": "sha256:a1b2c3..."
    }
  ]
}
```

- Time-limited, auditable access
- Hash verification for integrity
- No passwords in emails

### Pain Point 5: Multi-Framework Compliance is Redundant

**The Scenario**: A healthcare SaaS needs to prove compliance with HIPAA, SOC 2, and ISO 27001.

**What Happens Today**:
- Three separate audits
- Three separate sets of evidence (mostly overlapping)
- Three separate reports
- Controls that map to each other are verified independently

**With OpenCTS**:
```json
{
  "frameworks": [
    {"name": "SOC2", "status": "compliant", "score": 1.0},
    {"name": "HIPAA", "status": "compliant", "score": 1.0},
    {"name": "ISO27001", "status": "partial", "score": 0.85}
  ],
  "control_mappings": {
    "SOC2:CC6.1": ["ISO27001:A.9.2", "HIPAA:164.312(a)"]
  }
}
```

Auditors can see framework overlap and verify once, apply to many.

---

## Why a Standard Matters

### The Prometheus Analogy

Before Prometheus, every application exposed metrics differently:
- Custom `/health` endpoints with random JSON shapes
- Proprietary monitoring agents
- No way to aggregate metrics across services

After Prometheus standardized the format:
```
http_requests_total{method="GET", status="200"} 1234
```

Every monitoring tool could instantly understand any application.

### OpenCTS Does the Same for Compliance

Before OpenCTS:
- Every SaaS vendor has their own "Trust Center"
- PDFs, portals, emails, spreadsheets
- No machine-readable format
- No way to aggregate across vendors

After OpenCTS:
```json
{
  "version": "v1",
  "compliance_posture": "compliant",
  "frameworks": [{"name": "SOC2", "status": "compliant", "score": 1.0}]
}
```

Every compliance platform can instantly understand any SaaS vendor.

---

## Who Benefits

### For SaaS Providers
- **Reduce audit fatigue**: Stop answering the same compliance questions from every customer
- **Competitive advantage**: Customers prefer vendors with transparent compliance
- **Faster sales cycles**: Enterprise buyers can verify compliance instantly

### For Compliance Platforms (Vanta, Drata, LowerPlane)
- **Automated vendor assessment**: No more manual PDF parsing
- **Real-time monitoring**: Know when vendor compliance changes
- **Standardized integrations**: Build once, work with any OpenCTS-compliant vendor

### For Auditors
- **Faster evidence collection**: Query instead of request
- **Pre-mapped controls**: Vendors do the mapping work
- **Verifiable evidence**: Hash-verified, timestamped evidence

### For Companies Being Audited
- **Reduced prep time**: Vendor evidence is always current
- **Fewer back-and-forths**: Evidence is pre-mapped to controls
- **Continuous compliance**: Monitor vendors between audits

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
