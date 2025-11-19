# Security Best Practices

This guide covers security considerations when implementing OpenCPX.

## Core Principles

### 1. Never Expose Raw Evidence

The `/cpx` endpoint should return references to evidence, not the evidence itself.

**Don't do this:**
```json
{
  "evidence": {
    "iam_policies": [
      { "name": "admin", "permissions": [...] }
    ]
  }
}
```

**Do this:**
```json
{
  "evidence_refs": [
    "https://evidence.company.com/iam-export.json"
  ]
}
```

### 2. Use Time-Limited Access

Presigned URLs should expire:
- **Default**: 1 hour
- **Maximum**: 24 hours
- **Best practice**: Generate fresh URLs per request

```json
{
  "evidence_refs": [{
    "type": "presigned_url",
    "url": "https://s3.aws.com/...?signature=...",
    "expires": "2024-01-15T13:00:00Z"
  }]
}
```

### 3. Principle of Least Privilege

- Only expose what's necessary
- Scope access to specific files, not buckets
- Use separate credentials for evidence access

## Authentication Patterns

### Public Endpoints

For public compliance data (trust centers):
- No authentication required
- Expose only public information
- Rate limit to prevent abuse

```go
// Public endpoint
mux.HandleFunc("/cpx", publicHandler)
```

### Protected Endpoints

For sensitive or customer-specific data:

**API Key:**
```go
func protectedHandler(w http.ResponseWriter, r *http.Request) {
    apiKey := r.Header.Get("X-API-Key")
    if !validateAPIKey(apiKey) {
        http.Error(w, "Unauthorized", 401)
        return
    }
    // Serve posture
}
```

**OAuth/JWT:**
```go
func protectedHandler(w http.ResponseWriter, r *http.Request) {
    token := r.Header.Get("Authorization")
    claims, err := validateJWT(token)
    if err != nil {
        http.Error(w, "Unauthorized", 401)
        return
    }
    // Use claims to filter posture
}
```

### Customer-Specific Data

For multi-tenant applications:

```go
func customerHandler(w http.ResponseWriter, r *http.Request) {
    customerID := r.URL.Query().Get("customer_id")

    // Verify customer has access
    if !hasAccess(r.Context(), customerID) {
        http.Error(w, "Forbidden", 403)
        return
    }

    // Return customer-specific posture
    posture := getPostureForCustomer(customerID)
}
```

## Evidence Security

### Presigned URLs

Use cloud provider presigned URLs:

**AWS S3:**
```python
import boto3
from botocore.config import Config

s3 = boto3.client('s3', config=Config(signature_version='s3v4'))

url = s3.generate_presigned_url(
    'get_object',
    Params={
        'Bucket': 'evidence-bucket',
        'Key': 'soc2/report.pdf'
    },
    ExpiresIn=3600  # 1 hour
)
```

**Google Cloud Storage:**
```python
from google.cloud import storage

client = storage.Client()
bucket = client.bucket('evidence-bucket')
blob = bucket.blob('soc2/report.pdf')

url = blob.generate_signed_url(
    version='v4',
    expiration=timedelta(hours=1),
    method='GET'
)
```

### Hash Verification

Include hashes for integrity verification:

```json
{
  "evidence_refs": [{
    "url": "https://...",
    "hash": "sha256:a1b2c3d4e5f6...",
    "size_bytes": 1048576
  }]
}
```

Consumers should verify:
```python
import hashlib

def verify_evidence(content, expected_hash):
    actual_hash = hashlib.sha256(content).hexdigest()
    return f"sha256:{actual_hash}" == expected_hash
```

### Access Logging

Log all evidence access:

```python
def log_evidence_access(request, evidence_ref):
    logger.info({
        "event": "evidence_access",
        "user": request.user,
        "evidence_url": evidence_ref["url"],
        "timestamp": datetime.utcnow().isoformat(),
        "ip_address": request.remote_addr,
        "user_agent": request.headers.get("User-Agent")
    })
```

## Data Classification

### What to Expose Publicly

- Framework compliance status
- Overall scores
- Audit dates and auditors
- Certificate references
- Public policy documents

### What to Protect

- Detailed control evidence
- Internal policies
- Customer-specific data
- Security configurations
- Vulnerability reports

### What Never to Expose

- Credentials or secrets
- Customer PII
- Internal IP addresses
- Unredacted security findings

## Rate Limiting

Prevent abuse with rate limiting:

```go
import "golang.org/x/time/rate"

var limiter = rate.NewLimiter(rate.Every(time.Second), 10)

func rateLimitedHandler(w http.ResponseWriter, r *http.Request) {
    if !limiter.Allow() {
        http.Error(w, "Too Many Requests", 429)
        return
    }
    // Serve posture
}
```

## Input Validation

### Query Parameters

Validate all input:

```python
def get_posture(request):
    format = request.args.get('format', 'json')
    if format not in ['json', 'yaml']:
        abort(400, 'Invalid format')

    customer_id = request.args.get('customer_id')
    if customer_id and not customer_id.isalnum():
        abort(400, 'Invalid customer_id')
```

### Path Parameters

For endpoints like `/cpx/{tenant}`:

```go
func handler(w http.ResponseWriter, r *http.Request) {
    tenant := chi.URLParam(r, "tenant")

    // Validate tenant ID format
    if !isValidTenantID(tenant) {
        http.Error(w, "Invalid tenant", 400)
        return
    }

    // Check authorization
    if !canAccessTenant(r.Context(), tenant) {
        http.Error(w, "Forbidden", 403)
        return
    }
}
```

## HTTPS/TLS

Always use HTTPS:

- Enforce TLS 1.2+
- Use valid certificates
- Implement HSTS
- Redirect HTTP to HTTPS

```go
func main() {
    // Redirect HTTP to HTTPS
    go http.ListenAndServe(":80", http.HandlerFunc(redirectToHTTPS))

    // Serve HTTPS
    http.ListenAndServeTLS(":443", "cert.pem", "key.pem", handler)
}
```

## CORS Configuration

Configure CORS appropriately:

```go
func corsMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        // Restrict to known domains
        origin := r.Header.Get("Origin")
        if isAllowedOrigin(origin) {
            w.Header().Set("Access-Control-Allow-Origin", origin)
        }

        w.Header().Set("Access-Control-Allow-Methods", "GET")
        w.Header().Set("Access-Control-Allow-Headers", "Authorization")

        next.ServeHTTP(w, r)
    })
}
```

## Security Headers

Add security headers:

```go
func securityHeaders(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("X-Content-Type-Options", "nosniff")
        w.Header().Set("X-Frame-Options", "DENY")
        w.Header().Set("Content-Security-Policy", "default-src 'none'")
        w.Header().Set("Cache-Control", "no-store")

        next.ServeHTTP(w, r)
    })
}
```

## Audit Trail

Maintain an audit trail:

```python
def get_posture(request):
    # Log the request
    audit_log.info({
        "action": "cpx_endpoint_accessed",
        "timestamp": datetime.utcnow().isoformat(),
        "client_ip": request.remote_addr,
        "user_agent": request.headers.get("User-Agent"),
        "authenticated_user": getattr(request, "user", None),
        "parameters": dict(request.args)
    })

    # Generate and return posture
    return generate_posture()
```

## Security Checklist

### Implementation

- [ ] Use HTTPS only
- [ ] Implement authentication for sensitive data
- [ ] Use presigned URLs for evidence
- [ ] Set short expiration times
- [ ] Include hash verification
- [ ] Validate all inputs
- [ ] Implement rate limiting
- [ ] Add security headers
- [ ] Log all access

### Evidence Handling

- [ ] Never expose raw evidence in API
- [ ] Use time-limited access tokens
- [ ] Scope access to specific files
- [ ] Log evidence retrieval
- [ ] Verify integrity with hashes

### Monitoring

- [ ] Monitor for unusual access patterns
- [ ] Alert on authentication failures
- [ ] Track evidence access
- [ ] Review logs regularly

## Reporting Security Issues

If you find a security vulnerability in OpenCPX:

1. **Do not** open a public issue
2. Email security@opencpx.io
3. Include detailed reproduction steps
4. Allow time for a fix before disclosure

We follow responsible disclosure practices and will credit reporters.
