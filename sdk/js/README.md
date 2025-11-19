# OpenCPX JavaScript/TypeScript SDK

A lightweight JavaScript/TypeScript SDK for implementing OpenCPX compliance posture endpoints.

## Installation

```bash
npm install @opencpx/sdk
# or
yarn add @opencpx/sdk
```

## Quick Start

### Basic Usage

```typescript
import {
  Posture,
  CompliancePosture,
  FrameworkStatus,
  ControlStatus,
} from '@opencpx/sdk';

// Create a posture
const posture = new Posture(CompliancePosture.COMPLIANT);

// Set organization
posture.setOrganization({
  name: 'Acme Corp',
  domain: 'acme.com',
  contact: 'security@acme.com',
});

// Add SOC 2 framework
posture.addFramework({
  name: 'SOC2',
  status: FrameworkStatus.COMPLIANT,
  score: 1.0,
  version: 'Type II',
  last_audit: '2024-01-15',
  auditor: 'BigFour Audit LLP',
  controls: [
    {
      id: 'CC1.1',
      title: 'Demonstrates Commitment to Integrity',
      status: ControlStatus.COMPLIANT,
      evidence_refs: ['https://evidence.acme.com/code-of-conduct.pdf'],
    },
  ],
});

// Convert to JSON
console.log(posture.toJSON(2));
```

### Express Integration

```typescript
import express from 'express';
import {
  createExpressHandler,
  Posture,
  CompliancePosture,
  FrameworkStatus,
} from '@opencpx/sdk';

const app = express();

function getPosture(): Posture {
  const posture = new Posture(CompliancePosture.COMPLIANT);
  posture.addFramework({
    name: 'SOC2',
    status: FrameworkStatus.COMPLIANT,
    score: 1.0,
  });
  return posture;
}

app.get('/cpx', createExpressHandler(getPosture));

app.listen(8080, () => {
  console.log('Server running on port 8080');
});
```

### Using Middleware

```typescript
import express from 'express';
import { createExpressMiddleware, Posture, CompliancePosture } from '@opencpx/sdk';

const app = express();

function getPosture(): Posture {
  return new Posture(CompliancePosture.COMPLIANT);
}

// Add CPX middleware
app.use(createExpressMiddleware(getPosture));

// Your other routes
app.get('/', (req, res) => {
  res.send('Hello World');
});

app.listen(8080);
```

### Fastify Integration

```typescript
import Fastify from 'fastify';
import {
  createFastifyHandler,
  Posture,
  CompliancePosture,
  FrameworkStatus,
} from '@opencpx/sdk';

const fastify = Fastify();

function getPosture(): Posture {
  const posture = new Posture(CompliancePosture.COMPLIANT);
  posture.addFramework({
    name: 'SOC2',
    status: FrameworkStatus.COMPLIANT,
    score: 1.0,
  });
  return posture;
}

fastify.get('/cpx', createFastifyHandler(getPosture));

fastify.listen({ port: 8080 });
```

### Next.js API Route

```typescript
// pages/api/cpx.ts
import { createNextHandler, Posture, CompliancePosture } from '@opencpx/sdk';

function getPosture(): Posture {
  return new Posture(CompliancePosture.COMPLIANT);
}

export default createNextHandler(getPosture);
```

### Koa Integration

```typescript
import Koa from 'koa';
import Router from '@koa/router';
import { createKoaHandler, Posture, CompliancePosture } from '@opencpx/sdk';

const app = new Koa();
const router = new Router();

function getPosture(): Posture {
  return new Posture(CompliancePosture.COMPLIANT);
}

router.get('/cpx', createKoaHandler(getPosture));

app.use(router.routes());
app.listen(8080);
```

## Usage Patterns

### Async Provider

```typescript
async function getPosture(): Promise<Posture> {
  // Query your database for current compliance status
  const controls = await db.getComplianceControls();

  const posture = new Posture();

  const framework = {
    name: 'SOC2',
    status: FrameworkStatus.COMPLIANT,
    score: 0.95,
    controls: controls.map((ctrl) => ({
      id: ctrl.id,
      status: ctrl.status as ControlStatus,
      title: ctrl.title,
      evidence_refs: ctrl.evidenceUrls,
    })),
  };

  posture.addFramework(framework);
  posture.setPosture(posture.calculateOverallPosture());

  return posture;
}
```

### Multiple Frameworks

```typescript
const posture = new Posture(CompliancePosture.PARTIALLY_COMPLIANT);

// SOC 2
posture.addFramework({
  name: 'SOC2',
  status: FrameworkStatus.COMPLIANT,
  score: 1.0,
});

// ISO 27001
posture.addFramework({
  name: 'ISO27001',
  status: FrameworkStatus.PARTIAL,
  score: 0.85,
});

// HIPAA
posture.addFramework({
  name: 'HIPAA',
  status: FrameworkStatus.COMPLIANT,
  score: 1.0,
});
```

### Custom Extensions

```typescript
posture.addExtension('acme', {
  customer_count: 5000,
  data_centers: ['us-east-1', 'eu-west-1'],
  encryption: 'AES-256-GCM',
  uptime_sla: 99.99,
});
```

### Evidence References

```typescript
posture.evidence_refs = [
  {
    type: 'presigned_url',
    url: 'https://s3.aws.com/evidence/audit.pdf?signature=...',
    expires: '2024-01-24T00:00:00Z',
    hash: 'sha256:a1b2c3...',
    description: 'Complete audit package',
  },
];
```

### Helper Functions

```typescript
import { createFramework, createControl, FrameworkStatus, ControlStatus } from '@opencpx/sdk';

const framework = createFramework('SOC2', FrameworkStatus.COMPLIANT, 1.0);
framework.controls = [
  createControl('CC1.1', ControlStatus.COMPLIANT),
  createControl('CC6.1', ControlStatus.COMPLIANT),
];
```

## API Reference

### Classes

- `Posture` - Main compliance posture class

### Enums

- `CompliancePosture` - COMPLIANT, PARTIALLY_COMPLIANT, NON_COMPLIANT, UNKNOWN
- `FrameworkStatus` - COMPLIANT, PARTIAL, NON_COMPLIANT
- `ControlStatus` - COMPLIANT, PARTIAL, NON_COMPLIANT

### Interfaces

- `Organization` - Organization metadata
- `Framework` - Compliance framework
- `Control` - Individual compliance control
- `EvidenceRef` - Evidence reference with metadata

### Handler Functions

- `createExpressHandler(provider)` - Create Express route handler
- `createExpressMiddleware(provider)` - Create Express middleware
- `createFastifyHandler(provider)` - Create Fastify handler
- `createKoaHandler(provider)` - Create Koa handler
- `createNextHandler(provider)` - Create Next.js API handler
- `createHttpHandler(provider)` - Create Node.js HTTP handler

### Helper Functions

- `createFramework(name, status, score)` - Create a framework object
- `createControl(id, status)` - Create a control object

## Testing Your Endpoint

```bash
# Get compliance posture
curl http://localhost:8080/cpx

# Pretty print
curl http://localhost:8080/cpx | jq

# Check specific framework
curl http://localhost:8080/cpx | jq '.frameworks[] | select(.name=="SOC2")'
```

## TypeScript

This package is written in TypeScript and includes type definitions. All types are exported and can be imported directly:

```typescript
import type {
  PostureData,
  Framework,
  Control,
  Organization,
  EvidenceRef,
  PostureProvider,
} from '@opencpx/sdk';
```

## License

MIT License
