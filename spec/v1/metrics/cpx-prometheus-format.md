# OpenCPX Prometheus Metrics Format

This document defines the Prometheus metric format for exposing OpenCPX compliance data. These metrics enable monitoring, alerting, and visualization of compliance posture through standard Prometheus tooling.

## Endpoint

Metrics should be exposed at `/cpx/metrics` or `/metrics` alongside your existing Prometheus metrics.

```
GET /cpx/metrics
Content-Type: text/plain; version=0.0.4
```

## Core Metrics

### Overall Compliance Score

```prometheus
# HELP opencpx_compliance_score Overall compliance score for a framework (0.0 to 1.0)
# TYPE opencpx_compliance_score gauge
opencpx_compliance_score{framework="SOC2"} 1.0
opencpx_compliance_score{framework="ISO27001"} 0.98
opencpx_compliance_score{framework="HIPAA"} 0.85
opencpx_compliance_score{framework="GDPR"} 1.0
```

### Compliance Status

```prometheus
# HELP opencpx_compliance_status Current compliance status (1=compliant, 0.5=partial, 0=non_compliant)
# TYPE opencpx_compliance_status gauge
opencpx_compliance_status{framework="SOC2"} 1
opencpx_compliance_status{framework="ISO27001"} 1
opencpx_compliance_status{framework="HIPAA"} 0.5
opencpx_compliance_status{framework="GDPR"} 1
```

### Control Counts

```prometheus
# HELP opencpx_controls_total Total number of controls in a framework
# TYPE opencpx_controls_total gauge
opencpx_controls_total{framework="SOC2"} 64
opencpx_controls_total{framework="ISO27001"} 114

# HELP opencpx_controls_compliant Number of compliant controls
# TYPE opencpx_controls_compliant gauge
opencpx_controls_compliant{framework="SOC2"} 64
opencpx_controls_compliant{framework="ISO27001"} 112

# HELP opencpx_controls_non_compliant Number of non-compliant controls
# TYPE opencpx_controls_non_compliant gauge
opencpx_controls_non_compliant{framework="SOC2"} 0
opencpx_controls_non_compliant{framework="ISO27001"} 2

# HELP opencpx_controls_not_applicable Number of not-applicable controls
# TYPE opencpx_controls_not_applicable gauge
opencpx_controls_not_applicable{framework="SOC2"} 0
opencpx_controls_not_applicable{framework="ISO27001"} 0
```

### Evidence Metrics

```prometheus
# HELP opencpx_evidence_total Total number of evidence items
# TYPE opencpx_evidence_total gauge
opencpx_evidence_total{framework="SOC2"} 156
opencpx_evidence_total{framework="ISO27001"} 234

# HELP opencpx_evidence_expiring_soon Number of evidence items expiring within 30 days
# TYPE opencpx_evidence_expiring_soon gauge
opencpx_evidence_expiring_soon{framework="SOC2"} 3
opencpx_evidence_expiring_soon{framework="ISO27001"} 5

# HELP opencpx_evidence_expired Number of expired evidence items
# TYPE opencpx_evidence_expired gauge
opencpx_evidence_expired{framework="SOC2"} 0
opencpx_evidence_expired{framework="ISO27001"} 0
```

### Timestamp Metrics

```prometheus
# HELP opencpx_last_updated_timestamp Unix timestamp of last compliance data update
# TYPE opencpx_last_updated_timestamp gauge
opencpx_last_updated_timestamp{framework="SOC2"} 1705320000
opencpx_last_updated_timestamp{framework="ISO27001"} 1705320000

# HELP opencpx_last_audit_timestamp Unix timestamp of last audit
# TYPE opencpx_last_audit_timestamp gauge
opencpx_last_audit_timestamp{framework="SOC2"} 1704067200
opencpx_last_audit_timestamp{framework="ISO27001"} 1704067200

# HELP opencpx_next_audit_timestamp Unix timestamp of next scheduled audit
# TYPE opencpx_next_audit_timestamp gauge
opencpx_next_audit_timestamp{framework="SOC2"} 1734220800
opencpx_next_audit_timestamp{framework="ISO27001"} 1750377600
```

## Control-Level Metrics

For detailed control-level monitoring:

```prometheus
# HELP opencpx_control_status Status of individual control (1=compliant, 0.5=partial, 0=non_compliant)
# TYPE opencpx_control_status gauge
opencpx_control_status{framework="SOC2",control="CC6.1",category="access_control"} 1
opencpx_control_status{framework="SOC2",control="CC6.2",category="access_control"} 1
opencpx_control_status{framework="SOC2",control="CC7.1",category="monitoring"} 1
opencpx_control_status{framework="ISO27001",control="A.9.1",category="access_control"} 1
opencpx_control_status{framework="ISO27001",control="A.12.4",category="logging"} 0.5

# HELP opencpx_control_evidence_count Number of evidence items for a control
# TYPE opencpx_control_evidence_count gauge
opencpx_control_evidence_count{framework="SOC2",control="CC6.1"} 5
opencpx_control_evidence_count{framework="SOC2",control="CC7.1"} 8
```

## Risk Metrics

```prometheus
# HELP opencpx_risk_score Current risk score (0-100, lower is better)
# TYPE opencpx_risk_score gauge
opencpx_risk_score{framework="SOC2"} 12
opencpx_risk_score{framework="ISO27001"} 18

# HELP opencpx_findings_total Total number of open findings by severity
# TYPE opencpx_findings_total gauge
opencpx_findings_total{framework="SOC2",severity="critical"} 0
opencpx_findings_total{framework="SOC2",severity="high"} 0
opencpx_findings_total{framework="SOC2",severity="medium"} 2
opencpx_findings_total{framework="SOC2",severity="low"} 5
```

## Vendor/Integration Metrics

```prometheus
# HELP opencpx_vendor_compliance_score Compliance score of third-party vendors
# TYPE opencpx_vendor_compliance_score gauge
opencpx_vendor_compliance_score{vendor="aws",framework="SOC2"} 1.0
opencpx_vendor_compliance_score{vendor="stripe",framework="PCI_DSS"} 1.0
opencpx_vendor_compliance_score{vendor="okta",framework="SOC2"} 1.0

# HELP opencpx_integration_sync_timestamp Last sync time with external systems
# TYPE opencpx_integration_sync_timestamp gauge
opencpx_integration_sync_timestamp{integration="vanta"} 1705319400
opencpx_integration_sync_timestamp{integration="aws_config"} 1705318800
```

## Example Prometheus Alerts

### Alert on Non-Compliance

```yaml
groups:
  - name: opencpx_alerts
    rules:
      - alert: ComplianceScoreDropped
        expr: opencpx_compliance_score < 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Compliance score dropped below 90%"
          description: "Framework {{ $labels.framework }} compliance score is {{ $value }}"

      - alert: ComplianceStatusNonCompliant
        expr: opencpx_compliance_status == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Framework is non-compliant"
          description: "Framework {{ $labels.framework }} is non-compliant"

      - alert: EvidenceExpiringSoon
        expr: opencpx_evidence_expiring_soon > 0
        for: 1h
        labels:
          severity: warning
        annotations:
          summary: "Evidence items expiring soon"
          description: "{{ $value }} evidence items expiring within 30 days for {{ $labels.framework }}"

      - alert: StaleComplianceData
        expr: time() - opencpx_last_updated_timestamp > 86400
        for: 1h
        labels:
          severity: warning
        annotations:
          summary: "Compliance data is stale"
          description: "Compliance data for {{ $labels.framework }} has not been updated in over 24 hours"

      - alert: CriticalFindingsOpen
        expr: opencpx_findings_total{severity="critical"} > 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Critical compliance findings open"
          description: "{{ $value }} critical findings open for {{ $labels.framework }}"
```

## Grafana Dashboard

Example PromQL queries for Grafana dashboards:

### Overall Compliance Score Panel

```promql
opencpx_compliance_score
```

### Compliance Trend (7 days)

```promql
opencpx_compliance_score[7d]
```

### Control Compliance Percentage

```promql
(opencpx_controls_compliant / opencpx_controls_total) * 100
```

### Time Since Last Update

```promql
time() - opencpx_last_updated_timestamp
```

### Findings by Severity

```promql
sum by (severity) (opencpx_findings_total)
```

## Label Guidelines

### Standard Labels

| Label | Description | Example Values |
|-------|-------------|----------------|
| `framework` | Compliance framework name | SOC2, ISO27001, HIPAA, GDPR |
| `control` | Control identifier | CC6.1, A.9.1, 164.312(a) |
| `category` | Control category | access_control, monitoring, encryption |
| `severity` | Finding severity | critical, high, medium, low |
| `vendor` | Third-party vendor name | aws, stripe, okta |
| `integration` | Integration system name | vanta, drata, splunk |

### Custom Labels

Vendors may add custom labels prefixed with their vendor name:

```prometheus
opencpx_compliance_score{framework="SOC2",acme_tenant="prod",acme_region="us-east-1"} 1.0
```

## Implementation Notes

1. **Metric naming**: All metrics should be prefixed with `opencpx_`
2. **Cardinality**: Be mindful of label cardinality, especially for control-level metrics
3. **Update frequency**: Metrics should be updated at least every 5 minutes
4. **Histogram/Summary**: Consider using histograms for timing metrics if needed

## Integration with Prometheus Operator

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: opencpx-metrics
spec:
  selector:
    matchLabels:
      app: my-saas-app
  endpoints:
    - port: http
      path: /cpx/metrics
      interval: 60s
```
