# Why LowerPlane Created OpenCPX

## The Origin Story

OpenCPX was born from real-world pain. At LowerPlane, we've built and maintained over **300 integrations** with SaaS platforms to help our customers achieve continuous compliance. After years of wrestling with inconsistent APIs, manual evidence collection, and fragmented compliance data, we decided to create the standard we wished existed.

## The Problem We Lived Every Day

### Integration Fatigue at Scale

When you're building your first 10 integrations, the inconsistency is annoying. When you're building your 300th, it's a fundamental blocker to innovation.

Every SaaS platform we integrated with had:
- **Different API designs** - REST, GraphQL, SOAP, custom protocols
- **Different authentication schemes** - OAuth 2.0, API keys, SAML, custom tokens
- **Different data models** - No two vendors structured compliance data the same way
- **Different update frequencies** - Some real-time, some daily, some "when we remember"

We spent **70% of our engineering time** on integration maintenance, not innovation.

### The Evidence Collection Nightmare

For each of our 300+ integrations, we had to solve the same problems repeatedly:

1. **Where is the compliance data?** - Buried in trust centers, scattered across endpoints, hidden behind sales calls
2. **What format is it in?** - PDFs, spreadsheets, JSON, XML, screenshots, "contact us"
3. **How do we access it?** - Public URLs, presigned links, email attachments, secure portals
4. **How do we map it?** - Every vendor uses different control IDs and terminology

We built 300 custom parsers. 300 custom mappers. 300 maintenance headaches.

### The Staleness Problem

Compliance data has a shelf life. We discovered that:
- **40% of vendor compliance reports** we collected were outdated within 3 months
- **No programmatic way** to know when data changed
- **Manual refresh processes** that couldn't scale
- **Audit failures** due to stale evidence we didn't know was stale

### The Mapping Maze

Control mapping was our biggest time sink:
- SOC 2 CC6.1 ≈ ISO 27001 A.9.2 ≈ HIPAA 164.312(a) ... but not exactly
- Every vendor had their own interpretation
- We maintained **thousands of manual mappings**
- One vendor's "compliant" meant another's "partial"

## The OpenTelemetry Inspiration

While struggling with these problems, we looked at how other domains solved similar challenges. **OpenTelemetry** stood out.

### Before OpenTelemetry

Observability was fragmented:
- Custom `/health` endpoints with random JSON
- Proprietary monitoring agents
- No way to aggregate metrics across services
- Vendor lock-in everywhere

### After OpenTelemetry

A unified standard emerged:
```
http_requests_total{method="GET", status="200"} 1234
```

Suddenly:
- Any monitoring tool understood any application
- Developers wrote instrumentation once
- Data was portable and aggregatable
- Innovation accelerated

### The Parallel to Compliance

We realized compliance had the same structural problem:

| Observability (Before OTel) | Compliance (Today) |
|----------------------------|-------------------|
| Custom health endpoints | Custom trust centers |
| Proprietary formats | PDFs, spreadsheets, portals |
| No aggregation possible | Manual evidence collection |
| Vendor lock-in | Platform lock-in |

**If Prometheus standardized metrics, why couldn't we standardize compliance posture?**

## What We Built

OpenCPX applies OpenTelemetry's philosophy to compliance:

### Simple, Standardized Endpoint

Just like `/metrics`, every SaaS can expose `/cpx`:

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

### Machine-Readable by Default

No more parsing PDFs. No more scraping trust centers. Compliance data that tools can actually use.

### Evidence References, Not Evidence

Inspired by how OTel handles trace context, we reference evidence without exposing it:

```json
{
  "evidence_refs": [
    {
      "type": "presigned_url",
      "url": "https://s3.aws.com/evidence/...",
      "expires": "2024-01-24T00:00:00Z",
      "hash": "sha256:..."
    }
  ]
}
```

### Control Mapping Built In

Pre-mapped controls, just like OTel's semantic conventions:

```json
{
  "controls": [
    {
      "id": "CC6.1",
      "status": "compliant",
      "evidence_refs": ["..."]
    }
  ]
}
```

## The Impact We're Seeing

### For LowerPlane

- **90% reduction** in new integration development time
- **Eliminated** custom parser maintenance
- **Real-time** compliance monitoring instead of periodic checks
- **Automated** control mapping

### For Our Customers

- **Hours instead of weeks** to assess vendor compliance
- **Continuous monitoring** instead of annual reviews
- **Confident audit prep** with always-current evidence

### For the Ecosystem

- **SaaS vendors** differentiate on compliance transparency
- **Auditors** get pre-mapped, verifiable evidence
- **Everyone** spends less time on compliance theater

## Why Open Source?

We could have kept this proprietary. Instead, we open-sourced it because:

### 1. Network Effects Win

A standard only works if everyone adopts it. Proprietary standards die.

### 2. We Don't Want to Own This

LowerPlane is a compliance platform, not a standards body. We want to build products, not gatekeep specifications.

### 3. Community Governance Works

OpenTelemetry proved that vendor-neutral, community-governed standards thrive. We're following that model.

### 4. Rising Tide Lifts All Boats

If every SaaS platform exposes `/cpx`, the entire compliance ecosystem improves. Including our competitors. We're okay with that.

## Our Commitment

LowerPlane commits to:

1. **Active maintenance** - We use OpenCPX daily, so we're motivated to keep it great
2. **Community participation** - Our engineers participate in working groups
3. **No proprietary extensions** - We won't fork the standard for competitive advantage
4. **Backward compatibility** - We'll advocate for stability as the spec evolves

## Join Us

We built OpenCPX to solve our own problems. But we know we're not alone.

If you've built custom integrations with compliance data, you've felt this pain. If you've manually collected evidence from vendors, you've wasted those hours. If you've mapped controls between frameworks, you've made those mistakes.

**Help us make compliance as observable as infrastructure.**

- Implement `/cpx` in your SaaS platform
- Contribute to the SDKs
- Join working group meetings
- Share your use cases

The compliance industry is decades behind observability. Let's catch up.

---

*OpenCPX is maintained by the OpenCPX Working Group with founding contributions from LowerPlane.*

## Learn More

- [OpenCPX Specification](../spec/v1/spec.md)
- [Implementation Guide](implementation-guide.md)
- [Getting Started](QUICKSTART.md)
- [Contributing](../community/CONTRIBUTING.md)
