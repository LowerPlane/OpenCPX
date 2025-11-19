# OpenCPX OpenTelemetry Metrics Format

This document defines the OpenTelemetry (OTEL) metric format for exposing OpenCPX compliance data. Using OTEL enables integration with a wide variety of observability backends and standardized telemetry collection.

## Overview

OpenCPX metrics can be exported using the OpenTelemetry SDK and transmitted via OTLP (OpenTelemetry Protocol) to any compatible backend (Jaeger, Zipkin, Datadog, New Relic, etc.).

## Metric Instruments

### Gauge Metrics

Gauges are used for values that can go up or down, such as compliance scores.

```yaml
name: opencpx.compliance.score
description: Overall compliance score for a framework (0.0 to 1.0)
unit: "1"
instrument: Gauge
attributes:
  - framework: string
  - organization: string
```

### Counter Metrics

Counters are used for monotonically increasing values.

```yaml
name: opencpx.controls.evaluated.total
description: Total number of controls evaluated
unit: "{control}"
instrument: Counter
attributes:
  - framework: string
  - result: compliant|non_compliant|partial|not_applicable
```

## Core Metrics Specification

### Compliance Score Metrics

```yaml
metrics:
  - name: opencpx.compliance.score
    description: Overall compliance score for a framework
    unit: "1"
    instrument: Gauge
    attributes:
      framework: string
      organization: string

  - name: opencpx.compliance.status
    description: Compliance status enumeration
    unit: "1"
    instrument: Gauge
    attributes:
      framework: string
      status: compliant|partial|non_compliant

  - name: opencpx.compliance.posture
    description: Overall compliance posture score
    unit: "1"
    instrument: Gauge
    attributes:
      organization: string
```

### Control Metrics

```yaml
metrics:
  - name: opencpx.controls.total
    description: Total number of controls in a framework
    unit: "{control}"
    instrument: UpDownCounter
    attributes:
      framework: string

  - name: opencpx.controls.by_status
    description: Number of controls by status
    unit: "{control}"
    instrument: UpDownCounter
    attributes:
      framework: string
      status: compliant|non_compliant|partial|not_applicable

  - name: opencpx.control.status
    description: Individual control compliance status
    unit: "1"
    instrument: Gauge
    attributes:
      framework: string
      control_id: string
      control_title: string
      category: string
```

### Evidence Metrics

```yaml
metrics:
  - name: opencpx.evidence.total
    description: Total evidence items
    unit: "{item}"
    instrument: UpDownCounter
    attributes:
      framework: string
      type: document|screenshot|config|log

  - name: opencpx.evidence.age
    description: Age of evidence in days
    unit: "d"
    instrument: Histogram
    attributes:
      framework: string
    boundaries: [7, 30, 60, 90, 180, 365]

  - name: opencpx.evidence.expiring
    description: Evidence items expiring within threshold
    unit: "{item}"
    instrument: UpDownCounter
    attributes:
      framework: string
      days_until_expiry: string  # "7", "30", "90"
```

### Risk Metrics

```yaml
metrics:
  - name: opencpx.risk.score
    description: Current risk score
    unit: "1"
    instrument: Gauge
    attributes:
      framework: string

  - name: opencpx.findings.open
    description: Number of open findings
    unit: "{finding}"
    instrument: UpDownCounter
    attributes:
      framework: string
      severity: critical|high|medium|low

  - name: opencpx.findings.age
    description: Age of open findings in days
    unit: "d"
    instrument: Histogram
    attributes:
      framework: string
      severity: critical|high|medium|low
    boundaries: [1, 7, 14, 30, 60, 90]
```

### Timestamp Metrics

```yaml
metrics:
  - name: opencpx.last_updated
    description: Unix timestamp of last update
    unit: "s"
    instrument: Gauge
    attributes:
      framework: string

  - name: opencpx.audit.last
    description: Unix timestamp of last audit
    unit: "s"
    instrument: Gauge
    attributes:
      framework: string
      audit_type: internal|external

  - name: opencpx.audit.next
    description: Unix timestamp of next audit
    unit: "s"
    instrument: Gauge
    attributes:
      framework: string
```

## SDK Implementation Examples

### Go Implementation

```go
package main

import (
    "context"
    "go.opentelemetry.io/otel"
    "go.opentelemetry.io/otel/attribute"
    "go.opentelemetry.io/otel/metric"
)

func initComplianceMetrics() {
    meter := otel.Meter("opencpx")

    // Compliance score gauge
    complianceScore, _ := meter.Float64ObservableGauge(
        "opencpx.compliance.score",
        metric.WithDescription("Overall compliance score for a framework"),
        metric.WithUnit("1"),
    )

    // Register callback to update metrics
    meter.RegisterCallback(
        func(ctx context.Context, o metric.Observer) error {
            // Fetch current compliance data
            score := getComplianceScore("SOC2")
            o.ObserveFloat64(complianceScore, score,
                metric.WithAttributes(
                    attribute.String("framework", "SOC2"),
                    attribute.String("organization", "acme-corp"),
                ))
            return nil
        },
        complianceScore,
    )

    // Controls counter
    controlsEvaluated, _ := meter.Int64Counter(
        "opencpx.controls.evaluated.total",
        metric.WithDescription("Total number of controls evaluated"),
        metric.WithUnit("{control}"),
    )

    // Record control evaluation
    controlsEvaluated.Add(ctx, 1,
        metric.WithAttributes(
            attribute.String("framework", "SOC2"),
            attribute.String("result", "compliant"),
        ))
}
```

### Python Implementation

```python
from opentelemetry import metrics
from opentelemetry.sdk.metrics import MeterProvider
from opentelemetry.sdk.metrics.export import PeriodicExportingMetricReader

# Initialize meter
meter = metrics.get_meter("opencpx")

# Create compliance score gauge
compliance_score = meter.create_observable_gauge(
    name="opencpx.compliance.score",
    description="Overall compliance score for a framework",
    unit="1",
    callbacks=[get_compliance_score_callback]
)

def get_compliance_score_callback(options):
    """Callback to fetch current compliance scores"""
    scores = fetch_compliance_scores()
    for framework, score in scores.items():
        yield metrics.Observation(
            value=score,
            attributes={
                "framework": framework,
                "organization": "acme-corp"
            }
        )

# Create controls counter
controls_evaluated = meter.create_counter(
    name="opencpx.controls.evaluated.total",
    description="Total number of controls evaluated",
    unit="{control}"
)

# Record evaluation
controls_evaluated.add(
    1,
    attributes={
        "framework": "SOC2",
        "result": "compliant"
    }
)

# Create findings histogram
findings_age = meter.create_histogram(
    name="opencpx.findings.age",
    description="Age of open findings in days",
    unit="d"
)

# Record finding age
findings_age.record(
    15,  # 15 days old
    attributes={
        "framework": "SOC2",
        "severity": "medium"
    }
)
```

### JavaScript/TypeScript Implementation

```typescript
import { metrics } from '@opentelemetry/api';
import { MeterProvider } from '@opentelemetry/sdk-metrics';

const meter = metrics.getMeter('opencpx');

// Compliance score gauge
const complianceScore = meter.createObservableGauge(
  'opencpx.compliance.score',
  {
    description: 'Overall compliance score for a framework',
    unit: '1',
  }
);

complianceScore.addCallback((observableResult) => {
  const scores = getComplianceScores();
  for (const [framework, score] of Object.entries(scores)) {
    observableResult.observe(score, {
      framework,
      organization: 'acme-corp',
    });
  }
});

// Controls counter
const controlsEvaluated = meter.createCounter(
  'opencpx.controls.evaluated.total',
  {
    description: 'Total number of controls evaluated',
    unit: '{control}',
  }
);

// Record evaluation
controlsEvaluated.add(1, {
  framework: 'SOC2',
  result: 'compliant',
});

// Evidence age histogram
const evidenceAge = meter.createHistogram(
  'opencpx.evidence.age',
  {
    description: 'Age of evidence in days',
    unit: 'd',
  }
);

evidenceAge.record(45, {
  framework: 'SOC2',
});
```

## Resource Attributes

Standard resource attributes for OpenCPX metrics:

```yaml
resource:
  attributes:
    service.name: "my-saas-app"
    service.version: "1.2.3"
    deployment.environment: "production"
    opencpx.version: "v1"
    organization.id: "org-12345"
    organization.name: "Acme Corp"
```

## Semantic Conventions

### Attribute Naming

| Attribute | Type | Description |
|-----------|------|-------------|
| `framework` | string | Compliance framework (SOC2, ISO27001, etc.) |
| `control_id` | string | Control identifier |
| `control_title` | string | Human-readable control title |
| `category` | string | Control category |
| `status` | string | Compliance status |
| `severity` | string | Finding severity |
| `organization` | string | Organization identifier |

### Status Values

```yaml
status:
  - compliant
  - non_compliant
  - partial
  - not_applicable
  - in_progress
  - not_evaluated
```

### Severity Values

```yaml
severity:
  - critical
  - high
  - medium
  - low
  - informational
```

## OTLP Export Configuration

### Environment Variables

```bash
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_HEADERS=Authorization=Bearer token123
OTEL_RESOURCE_ATTRIBUTES=service.name=my-saas-app,deployment.environment=production
OTEL_METRICS_EXPORTER=otlp
OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=delta
```

### Collector Configuration

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:
    timeout: 10s
    send_batch_size: 1000

exporters:
  prometheus:
    endpoint: "0.0.0.0:8889"

  otlp/datadog:
    endpoint: "https://api.datadoghq.com"
    headers:
      DD-API-KEY: ${DD_API_KEY}

service:
  pipelines:
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [prometheus, otlp/datadog]
```

## Best Practices

1. **Use semantic naming**: Follow OpenTelemetry semantic conventions
2. **Keep cardinality in check**: Avoid high-cardinality attributes
3. **Set appropriate boundaries**: Use meaningful histogram boundaries
4. **Include units**: Always specify metric units
5. **Add descriptions**: Provide clear descriptions for all metrics
6. **Use resource attributes**: Set service and organization context at resource level

## Integration Examples

### Datadog

```python
from opentelemetry.exporter.otlp.proto.grpc.metric_exporter import OTLPMetricExporter

exporter = OTLPMetricExporter(
    endpoint="https://api.datadoghq.com",
    headers={"DD-API-KEY": os.environ["DD_API_KEY"]}
)
```

### New Relic

```python
exporter = OTLPMetricExporter(
    endpoint="https://otlp.nr-data.net:4317",
    headers={"api-key": os.environ["NEW_RELIC_LICENSE_KEY"]}
)
```

### Grafana Cloud

```python
exporter = OTLPMetricExporter(
    endpoint="https://otlp-gateway-prod-us-central-0.grafana.net/otlp",
    headers={"Authorization": f"Basic {base64_credentials}"}
)
```
