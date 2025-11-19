# OpenCPX Implementation Guide

This guide walks you through implementing OpenCPX in your SaaS product, from minimal viable implementation to enterprise-grade deployment.

---

## Before You Start

### What You'll Need

1. **Compliance data**: Framework status, control mapping, evidence locations
2. **API endpoint**: Ability to add `/cpx` to your existing API
3. **Evidence storage**: S3, GCS, or equivalent for evidence files
4. **1-8 hours**: Depending on implementation depth

### What You Won't Need

- New databases or infrastructure
- Expensive compliance tools
- Dedicated compliance engineering team

---

## Scenario 1: Startup with SOC 2 Type II

**Situation**: You're a 20-person B2B SaaS startup. You just completed your first SOC 2 Type II audit. Customers keep asking for compliance documentation.

**Goal**: Replace email requests with an API endpoint.

### Step 1: Create the minimal response

```json
{
  "version": "v1",
  "timestamp": "2024-01-15T12:00:00Z",
  "compliance_posture": "compliant",
  "frameworks": [
    {
      "name": "SOC2",
      "version": "Type II",
      "status": "compliant",
      "score": 1.0,
      "last_audit": "2024-01-10",
      "auditor": "Your Audit Firm",
      "report_ref": "https://trust.yourcompany.com/soc2-2024.pdf"
    }
  ]
}
```

### Step 2: Add the endpoint

**Node.js/Express**:
```javascript
app.get('/cpx', (req, res) => {
  res.json({
    version: 'v1',
    timestamp: new Date().toISOString(),
    compliance_posture: 'compliant',
    frameworks: [{
      name: 'SOC2',
      version: 'Type II',
      status: 'compliant',
      score: 1.0,
      last_audit: '2024-01-10',
      report_ref: 'https://trust.yourcompany.com/soc2-2024.pdf'
    }]
  });
});
```

**Python/Flask**:
```python
from flask import jsonify
from datetime import datetime

@app.route('/cpx')
def compliance_telemetry():
    return jsonify({
        'version': 'v1',
        'timestamp': datetime.utcnow().isoformat() + 'Z',
        'compliance_posture': 'compliant',
        'frameworks': [{
            'name': 'SOC2',
            'version': 'Type II',
            'status': 'compliant',
            'score': 1.0,
            'last_audit': '2024-01-10',
            'report_ref': 'https://trust.yourcompany.com/soc2-2024.pdf'
        }]
    })
```

**Go**:
```go
func ctsHandler(w http.ResponseWriter, r *http.Request) {
    response := map[string]interface{}{
        "version":           "v1",
        "timestamp":         time.Now().UTC().Format(time.RFC3339),
        "compliance_posture": "compliant",
        "frameworks": []map[string]interface{}{
            {
                "name":       "SOC2",
                "version":    "Type II",
                "status":     "compliant",
                "score":      1.0,
                "last_audit": "2024-01-10",
                "report_ref": "https://trust.yourcompany.com/soc2-2024.pdf",
            },
        },
    }
    json.NewEncoder(w).Encode(response)
}
```

### Step 3: Test it

```bash
curl https://api.yourcompany.com/cpx | jq
```

**Time invested**: 1 hour

---

## Scenario 2: Growing Company with Multiple Frameworks

**Situation**: You're a 100-person company. You have SOC 2, ISO 27001, and are working toward GDPR. Enterprise customers want detailed control-level information.

**Goal**: Provide control-level compliance data with evidence references.

### Step 1: Map your controls

Create a compliance data structure (could be in your database or a config file):

```javascript
const complianceData = {
  soc2: {
    status: 'compliant',
    score: 1.0,
    controls: [
      {
        id: 'CC1.1',
        title: 'Demonstrates Commitment to Integrity',
        status: 'compliant',
        evidence: ['code-of-conduct.pdf', 'ethics-training.csv']
      },
      {
        id: 'CC6.1',
        title: 'Logical Access Controls',
        status: 'compliant',
        evidence: ['iam-policy.json', 'access-review-q1.pdf']
      }
      // ... more controls
    ]
  },
  iso27001: {
    status: 'compliant',
    score: 1.0,
    controls: [
      {
        id: 'A.5.1',
        title: 'Policies for information security',
        status: 'compliant',
        evidence: ['infosec-policy.pdf']
      }
      // ... more controls
    ]
  },
  gdpr: {
    status: 'partial',
    score: 0.75,
    controls: [
      {
        id: 'Article 5',
        title: 'Principles relating to processing',
        status: 'compliant',
        evidence: ['privacy-policy.pdf']
      },
      {
        id: 'Article 17',
        title: 'Right to erasure',
        status: 'partial',
        reason: 'Automated deletion in development',
        remediation_date: '2024-03-01'
      }
    ]
  }
};
```

### Step 2: Generate evidence URLs

```javascript
const AWS = require('aws-sdk');
const s3 = new AWS.S3();

async function getPresignedUrl(key) {
  return s3.getSignedUrlPromise('getObject', {
    Bucket: 'your-evidence-bucket',
    Key: key,
    Expires: 604800 // 7 days
  });
}

async function buildEvidenceRefs(evidenceFiles) {
  return Promise.all(evidenceFiles.map(async (file) => {
    const url = await getPresignedUrl(file);
    return {
      type: 'presigned_url',
      url: url,
      expires: new Date(Date.now() + 604800000).toISOString()
    };
  }));
}
```

### Step 3: Build the full response

```javascript
app.get('/cpx', async (req, res) => {
  const frameworks = [];

  for (const [name, data] of Object.entries(complianceData)) {
    const controls = await Promise.all(data.controls.map(async (control) => {
      const result = {
        id: control.id,
        title: control.title,
        status: control.status
      };

      if (control.evidence) {
        result.evidence_refs = await buildEvidenceRefs(control.evidence);
      }

      if (control.reason) {
        result.reason = control.reason;
        result.remediation_date = control.remediation_date;
      }

      return result;
    }));

    frameworks.push({
      name: name.toUpperCase(),
      status: data.status,
      score: data.score,
      controls: controls
    });
  }

  res.json({
    version: 'v1',
    timestamp: new Date().toISOString(),
    compliance_posture: calculateOverallPosture(frameworks),
    frameworks: frameworks
  });
});

function calculateOverallPosture(frameworks) {
  const hasNonCompliant = frameworks.some(f => f.status === 'non_compliant');
  const hasPartial = frameworks.some(f => f.status === 'partial');

  if (hasNonCompliant) return 'non_compliant';
  if (hasPartial) return 'partially_compliant';
  return 'compliant';
}
```

**Time invested**: 1 day

---

## Scenario 3: Enterprise with Prometheus Monitoring

**Situation**: You're a 500-person company with a mature compliance program. You want to expose compliance metrics to your monitoring stack and alert on compliance drift.

**Goal**: Prometheus metrics endpoint with Grafana dashboards.

### Step 1: Add the metrics endpoint

```javascript
const promClient = require('prom-client');

// Create metrics
const complianceScore = new promClient.Gauge({
  name: 'opencpx_compliance_score',
  help: 'Compliance score for a framework',
  labelNames: ['framework']
});

const controlStatus = new promClient.Gauge({
  name: 'opencpx_control_status',
  help: 'Control compliance status (1=compliant, 0.5=partial, 0=non_compliant)',
  labelNames: ['framework', 'control_id']
});

const lastAudit = new promClient.Gauge({
  name: 'opencpx_last_audit_timestamp',
  help: 'Timestamp of last audit',
  labelNames: ['framework']
});

// Update metrics from your compliance data
function updateMetrics() {
  complianceScore.set({ framework: 'SOC2' }, 1.0);
  complianceScore.set({ framework: 'ISO27001' }, 1.0);
  complianceScore.set({ framework: 'GDPR' }, 0.75);

  controlStatus.set({ framework: 'SOC2', control_id: 'CC6.1' }, 1);
  controlStatus.set({ framework: 'GDPR', control_id: 'Article17' }, 0.5);

  lastAudit.set({ framework: 'SOC2' }, Date.parse('2024-01-10') / 1000);
}

// Metrics endpoint
app.get('/cpx/metrics', (req, res) => {
  updateMetrics();
  res.set('Content-Type', promClient.register.contentType);
  res.end(promClient.register.metrics());
});
```

### Step 2: Configure Prometheus

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'opencts'
    static_configs:
      - targets: ['your-api:3000']
    metrics_path: '/cpx/metrics'
    scrape_interval: 1h  # Compliance data doesn't change frequently
```

### Step 3: Create alerts

```yaml
# alert_rules.yml
groups:
  - name: compliance
    rules:
      - alert: ComplianceScoreDrop
        expr: opencpx_compliance_score < 0.9
        for: 1h
        labels:
          severity: warning
        annotations:
          summary: "Compliance score dropped below 90%"
          description: "{{ $labels.framework }} score is {{ $value }}"

      - alert: AuditExpiring
        expr: (time() - opencpx_last_audit_timestamp) > 31536000  # 1 year
        for: 1d
        labels:
          severity: critical
        annotations:
          summary: "Audit report expiring"
          description: "{{ $labels.framework }} audit is over 1 year old"
```

### Step 4: Grafana dashboard

```json
{
  "panels": [
    {
      "title": "Compliance Scores",
      "type": "gauge",
      "targets": [
        {
          "expr": "opencpx_compliance_score",
          "legendFormat": "{{ framework }}"
        }
      ],
      "options": {
        "minValue": 0,
        "maxValue": 1,
        "thresholds": [
          { "value": 0, "color": "red" },
          { "value": 0.8, "color": "yellow" },
          { "value": 0.95, "color": "green" }
        ]
      }
    }
  ]
}
```

**Time invested**: 1 week

---

## Scenario 4: Healthcare Company with HIPAA

**Situation**: You're a healthcare SaaS handling PHI. You need to demonstrate HIPAA compliance to covered entities.

**Goal**: Detailed HIPAA control mapping with BAA tracking.

### Special considerations for healthcare

1. **Never expose PHI through CTS** — Not even metadata
2. **Track Business Associate Agreements** — Show you're managing subprocessors
3. **Map to HIPAA's specific structure** — Administrative, Physical, Technical safeguards

### Example response

```json
{
  "version": "v1",
  "timestamp": "2024-01-15T12:00:00Z",
  "compliance_posture": "compliant",
  "organization": {
    "name": "HealthTech Inc",
    "hipaa_role": "Business Associate",
    "baa_available": true
  },
  "frameworks": [
    {
      "name": "HIPAA",
      "version": "2013 Omnibus Rule",
      "status": "compliant",
      "score": 1.0,
      "controls": [
        {
          "id": "164.308(a)(1)",
          "title": "Security Management Process",
          "category": "Administrative Safeguards",
          "status": "compliant",
          "evidence_refs": ["https://evidence.../risk-assessment.pdf"]
        },
        {
          "id": "164.310(a)(1)",
          "title": "Facility Access Controls",
          "category": "Physical Safeguards",
          "status": "compliant",
          "implementation_notes": "Cloud-only. AWS GovCloud (HIPAA eligible)."
        },
        {
          "id": "164.312(a)(1)",
          "title": "Access Control",
          "category": "Technical Safeguards",
          "status": "compliant",
          "evidence_refs": ["https://evidence.../access-controls.pdf"]
        },
        {
          "id": "164.314(a)",
          "title": "Business Associate Contracts",
          "category": "Organizational Requirements",
          "status": "compliant",
          "evidence_refs": ["https://evidence.../baa-inventory.json"]
        }
      ]
    }
  ],
  "extensions": {
    "hipaa": {
      "phi_processing": true,
      "subprocessors": [
        {
          "name": "AWS",
          "baa_signed": true,
          "services": ["RDS", "S3", "CloudWatch"]
        },
        {
          "name": "Twilio",
          "baa_signed": true,
          "services": ["SMS notifications"]
        }
      ],
      "encryption": {
        "at_rest": "AES-256",
        "in_transit": "TLS 1.3"
      }
    }
  }
}
```

---

## Scenario 5: Handling Compliance Gaps Transparently

**Situation**: You have some compliance gaps you're actively fixing. You want to be transparent without scaring away customers.

**Goal**: Show gaps with clear remediation plans.

### The wrong approach

```json
{
  "compliance_posture": "compliant"
}
```

This will backfire when auditors find the gaps. Loss of trust is worse than transparency.

### The right approach

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
          "id": "CC7.2",
          "title": "Monitors System Components",
          "status": "partial",
          "reason": "SIEM integration 80% complete. Currently using CloudWatch with manual review.",
          "remediation_date": "2024-03-01",
          "evidence_refs": ["https://evidence.../monitoring-roadmap.pdf"]
        },
        {
          "id": "CC9.2",
          "title": "Vendor Risk Management",
          "status": "non_compliant",
          "reason": "3 of 25 vendors missing annual assessment. Assessments scheduled for February.",
          "remediation_date": "2024-02-28",
          "evidence_refs": []
        }
      ]
    }
  ]
}
```

### Why this works

- **Customers see you're honest** — Builds trust
- **Auditors don't waste time** — They know what to expect
- **Sales can address concerns** — "We're at 85% and will be 100% by March"
- **You have accountability** — Remediation dates are on record

---

## Common Implementation Mistakes

### Mistake 1: Hardcoding timestamps

**Bad**:
```javascript
timestamp: "2024-01-15T12:00:00Z"
```

**Good**:
```javascript
timestamp: new Date().toISOString()
```

### Mistake 2: Forgetting to update on compliance changes

Set up a process to update CTS when:
- Audit reports are received
- Controls change status
- Remediation is completed
- New frameworks are added

### Mistake 3: Exposing sensitive data

**Never include**:
- Internal IP addresses
- Employee names or emails (beyond contact)
- Specific vulnerability details
- Credentials or tokens
- Detailed system architecture

### Mistake 4: Not versioning

Always include `version: "v1"`. It costs nothing and prevents breaking changes later.

### Mistake 5: Inconsistent framework names

Pick a naming convention and stick to it:
- `SOC2` not `soc2` or `SOC 2` or `SOC2-Type-II`
- Use `version` field for Type I/II distinction

---

## Testing Your Implementation

### Schema validation

Use the OpenCPX JSON Schema to validate your response:

```bash
npm install ajv
```

```javascript
const Ajv = require('ajv');
const ajv = new Ajv();

const schema = require('./cpx-schema.json');
const validate = ajv.compile(schema);

const yourResponse = await fetch('http://localhost:3000/cpx').then(r => r.json());

if (validate(yourResponse)) {
  console.log('Valid OpenCPX response!');
} else {
  console.log('Validation errors:', validate.errors);
}
```

### Manual testing checklist

- [ ] Response is valid JSON
- [ ] `timestamp` is current (not hardcoded)
- [ ] All `score` values are between 0 and 1
- [ ] `status` matches `score` logic
- [ ] Evidence URLs are accessible (or correctly protected)
- [ ] No sensitive data exposed
- [ ] CORS configured for browser access (if needed)

---

## Next Steps

1. **Start minimal** — Get `/cpx` live with basic data today
2. **Add controls** — Map your compliance controls over the next week
3. **Add evidence** — Connect presigned URLs to your evidence storage
4. **Add monitoring** — Set up Prometheus/OTEL if you use them
5. **Tell customers** — Let them know they can query `/cpx`

---

## Getting Help

- **Spec questions**: See [spec.md](../spec/v1/spec.md)
- **Examples**: See [examples/](../spec/v1/examples/)
- **Community**: Join the OpenCPX working group

---

MIT License © 2025 OpenCPX Working Group
