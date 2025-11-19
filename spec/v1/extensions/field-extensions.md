# OpenCPX Field Extensions

OpenCPX supports vendor-specific extensions to accommodate custom compliance requirements and additional metadata that may not be covered by the core specification.

## Extension Naming Convention

All vendor extensions must follow the naming convention `x-{vendor}-{field}` to avoid conflicts with core specification fields and other vendor extensions.

### Examples

```
x-aws-region
x-azure-subscription
x-vanta-integration-id
x-drata-workspace
```

## Extension Placement

Extensions can be added at multiple levels in the CPX payload:

### 1. Root Level Extensions

Add custom metadata about the overall compliance posture:

```json
{
  "version": "v1",
  "compliance_posture": "compliant",
  "x-vendor-internal-id": "org-12345",
  "x-vendor-last-sync": "2025-01-15T10:30:00Z"
}
```

### 2. Framework Level Extensions

Add vendor-specific framework metadata:

```json
{
  "frameworks": [
    {
      "name": "SOC2",
      "status": "compliant",
      "x-vendor-audit-firm": "Deloitte",
      "x-vendor-report-id": "RPT-2025-001"
    }
  ]
}
```

### 3. Control Level Extensions

Add control-specific vendor data:

```json
{
  "controls": [
    {
      "id": "CC6.1",
      "status": "compliant",
      "x-vendor-test-frequency": "quarterly",
      "x-vendor-automation-level": "full"
    }
  ]
}
```

### 4. Evidence Level Extensions

Add evidence-specific metadata:

```json
{
  "evidence_refs": [
    {
      "type": "presigned_url",
      "url": "https://...",
      "x-vendor-classification": "confidential",
      "x-vendor-retention-days": 365
    }
  ]
}
```

## Common Extension Patterns

### Audit Metadata Extensions

```json
{
  "x-vendor-auditor": "string",
  "x-vendor-audit-date": "ISO8601 datetime",
  "x-vendor-audit-type": "Type I | Type II | Internal",
  "x-vendor-next-audit": "ISO8601 datetime"
}
```

### Integration Extensions

```json
{
  "x-vendor-source-system": "string",
  "x-vendor-sync-timestamp": "ISO8601 datetime",
  "x-vendor-sync-status": "success | partial | failed",
  "x-vendor-record-count": "integer"
}
```

### Risk Extensions

```json
{
  "x-vendor-risk-score": "number (0-100)",
  "x-vendor-risk-level": "low | medium | high | critical",
  "x-vendor-risk-factors": ["array", "of", "factors"],
  "x-vendor-mitigation-status": "string"
}
```

### Automation Extensions

```json
{
  "x-vendor-automated": "boolean",
  "x-vendor-last-scan": "ISO8601 datetime",
  "x-vendor-scanner-version": "string",
  "x-vendor-findings-count": "integer"
}
```

## Extension Schema Validation

While extensions are not validated by the core CPX schema, vendors should:

1. **Document their extensions** - Provide clear documentation for consumers
2. **Use consistent types** - Follow JSON type conventions
3. **Version extensions** - Use semantic versioning if schemas change
4. **Provide defaults** - Document default values for optional extensions

### Extension Documentation Template

```yaml
extension_name: x-vendor-example-field
vendor: Example Corp
version: 1.0.0
description: Description of what this extension provides
type: string | number | boolean | object | array
required: false
default: null
example: "example-value"
```

## Reserved Extension Prefixes

The following prefixes are reserved for specific vendors or purposes:

| Prefix | Reserved For |
|--------|--------------|
| `x-cpx-` | OpenCPX working group experimental features |
| `x-aws-` | Amazon Web Services |
| `x-azure-` | Microsoft Azure |
| `x-gcp-` | Google Cloud Platform |
| `x-vanta-` | Vanta |
| `x-drata-` | Drata |
| `x-secureframe-` | Secureframe |

## Best Practices

### Do

- Use descriptive field names
- Include timestamps for time-sensitive data
- Provide both human-readable and machine-readable values when appropriate
- Document all extensions in your implementation guide

### Don't

- Override core specification fields
- Use extensions for data that should be in the core spec (propose an RFC instead)
- Include sensitive data (credentials, PII) in extensions
- Use extensions to circumvent schema validation

## Proposing New Core Fields

If you find yourself using the same extension across multiple implementations, consider proposing it as a core specification field:

1. Open an issue in the OpenCPX repository
2. Describe the use case and benefits
3. Provide example implementations
4. Participate in working group discussions

See [docs/governance/rfc-process.md](../../docs/governance/rfc-process.md) for the full RFC process.
