# Frequently Asked Questions

## General Questions

### What is OpenCPX?

OpenCPX (Open Compliance Posture eXchange) is a vendor-neutral standard for exposing compliance posture from SaaS platforms in a machine-readable format. Think of it like Prometheus for compliance data.

### Why do I need OpenCPX?

If you:
- Spend hours collecting compliance evidence from vendors
- Manually map evidence to controls
- Struggle with stale compliance data
- Want to automate vendor risk assessments

OpenCPX standardizes this information so it can be queried, aggregated, and monitored automatically.

### Is OpenCPX free to use?

Yes, OpenCPX is completely free and open source under the MIT license. You can implement it in your products without any fees or restrictions.

### Who maintains OpenCPX?

OpenCPX is community-governed through working groups. It's designed to be vendor-neutral and not controlled by any single company.

## Implementation Questions

### How long does it take to implement?

- **Basic endpoint**: 1 hour
- **With controls and evidence**: 1 day
- **Enterprise with full mapping**: 1 week

See the [implementation guide](implementation-guide.md) for detailed scenarios.

### Which frameworks are supported?

OpenCPX supports any compliance framework. Common ones include:
- SOC 2
- ISO 27001
- HIPAA
- GDPR
- PCI-DSS
- ISO 42001 (AI)
- ISO 20000

You can also define custom frameworks.

### Do I need to expose all my controls?

No. You control what you expose. OpenCPX defines the format, not the content. You can start with high-level framework status and add control details over time.

### How do I handle partial compliance?

Use the `partial` status and a score between 0 and 1:

```json
{
  "name": "SOC2",
  "status": "partial",
  "score": 0.85
}
```

For controls, you can add a `reason` field:

```json
{
  "id": "CC7.2",
  "status": "partial",
  "reason": "SIEM integration in progress"
}
```

### Can I add custom fields?

Yes, use the `extensions` field:

```json
{
  "extensions": {
    "mycompany": {
      "customer_count": 5000,
      "data_centers": ["us-east-1", "eu-west-1"]
    }
  }
}
```

## Security Questions

### Is it safe to expose compliance data?

Yes, if done correctly:
- Only expose what you want to be public
- Use evidence references, not raw evidence
- Implement authentication if needed
- Use presigned URLs with short expiration

### Should I require authentication?

It depends on your use case:
- **Public endpoint**: For publicly traded companies or trust centers
- **Authenticated**: For customer-specific data or detailed evidence

### How do I protect sensitive evidence?

Never include raw evidence in the response. Use references:

```json
{
  "evidence_refs": [
    {
      "type": "presigned_url",
      "url": "https://s3.aws.com/...",
      "expires": "2024-01-24T00:00:00Z"
    }
  ]
}
```

See [evidence flow documentation](architecture/evidence-flow.md) for details.

## Technical Questions

### What format should I use?

JSON is recommended and most widely supported. YAML and XML are also valid.

### How often should the data update?

The `timestamp` field should reflect when the data was generated. Options:
- **Real-time**: Generate fresh data on each request
- **Cached**: Update periodically (hourly, daily)
- **Event-driven**: Update when compliance status changes

### How do I handle versioning?

The `version` field indicates schema version (currently "v1"). As the spec evolves, new versions will be released with migration guides.

### Can I have multiple endpoints?

Yes, you might have:
- `/cpx` - Public summary
- `/cpx/detailed` - Authenticated detailed view
- `/cpx/{tenant}` - Tenant-specific posture

### How do I validate my response?

Use the JSON Schema at `/spec/v1/cpx-schema.json`:

```bash
# Using ajv-cli
npx ajv-cli validate -s cpx-schema.json -d your-response.json
```

## Integration Questions

### Does OpenCPX work with Vanta/Drata?

Yes, that's a primary use case. Compliance platforms can query OpenCPX endpoints to automatically assess vendor compliance.

### Can I integrate with Prometheus?

Yes, OpenCPX was inspired by Prometheus. You can:
- Create a Prometheus exporter for compliance metrics
- Use OpenTelemetry to emit compliance signals

See the [collectors directory](../collectors/) for examples.

### How do I aggregate multiple vendors?

Query each vendor's `/cpx` endpoint and aggregate:

```bash
for vendor in vendors.txt; do
  curl -s "https://$vendor/cpx" >> all-vendors.json
done
```

Compliance platforms can then process this data.

## Adoption Questions

### How do I convince my team to adopt OpenCPX?

Key benefits:
- **Engineering**: Simple implementation (one endpoint)
- **Sales**: Competitive advantage, faster deals
- **Compliance**: Reduced audit prep time
- **Security**: Standardized evidence handling

### What if my auditor doesn't know OpenCPX?

OpenCPX produces standard JSON that any auditor can understand. The benefit is in how it's collected and mapped, not in the format itself.

### Are there case studies?

See the [examples directory](../spec/v1/examples/) for realistic implementations and the [implementation guide](implementation-guide.md) for detailed scenarios.

## Contributing Questions

### How do I contribute?

See [CONTRIBUTING.md](../community/CONTRIBUTING.md) for guidelines. Contributions welcome in:
- Code and SDKs
- Documentation
- Framework mappings
- Bug reports

### How do I propose a schema change?

1. Open a GitHub issue describing the change
2. Discuss with the Schema Working Group
3. Submit a PR if approved

### How do I join a working group?

Attend a meeting! Check the [meeting schedule](../community/meetings/) and join. All are welcome.

## Still Have Questions?

- Open a [GitHub Discussion](https://github.com/opencpx/OpenCPX/discussions)
- Join a [working group meeting](../community/meetings/)
- Read the [specification](../spec/v1/spec.md)
