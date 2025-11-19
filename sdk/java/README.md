# OpenCPX Java SDK

A lightweight Java SDK for implementing OpenCPX compliance posture endpoints.

## Installation

### Maven

```xml
<dependency>
    <groupId>io.opencpx</groupId>
    <artifactId>opencpx-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.opencpx:opencpx-sdk:1.0.0'
```

## Quick Start

### Basic Usage

```java
import io.opencpx.*;

// Create a posture
Posture posture = new Posture(CompliancePosture.COMPLIANT);

// Set organization
posture.setOrganization(new Organization(
    "Acme Corp",
    "acme.com",
    "security@acme.com"
));

// Add SOC 2 framework
Framework soc2 = new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0);
soc2.setVersion("Type II");
soc2.setLastAudit("2024-01-15");
soc2.setAuditor("BigFour Audit LLP");

// Add controls
soc2.addControl(new Control("CC1.1", ControlStatus.COMPLIANT, "Demonstrates Commitment to Integrity")
    .addEvidenceRef("https://evidence.acme.com/code-of-conduct.pdf"));

posture.addFramework(soc2);

// Convert to JSON
String json = posture.toJsonPretty();
System.out.println(json);
```

### Spring Boot Integration

```java
import io.opencpx.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CpxConfig {

    @Bean
    public CpxController cpxController() {
        return new CpxController(() -> {
            Posture posture = new Posture(CompliancePosture.COMPLIANT);

            posture.setOrganization(new Organization(
                "Acme Corp",
                "acme.com",
                "security@acme.com"
            ));

            Framework soc2 = new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0);
            posture.addFramework(soc2);

            return posture;
        });
    }
}
```

### Jakarta Servlet Integration

```java
import io.opencpx.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/cpx")
public class MyCpxServlet extends CpxServlet {

    public MyCpxServlet() {
        super(() -> {
            Posture posture = new Posture(CompliancePosture.COMPLIANT);
            posture.addFramework(new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0));
            return posture;
        });
    }
}
```

## Usage Patterns

### Dynamic Posture Generation

```java
@Bean
public CpxController cpxController(ComplianceService complianceService) {
    return new CpxController(() -> {
        // Query your database for current compliance status
        List<ComplianceControl> controls = complianceService.getControls();

        Posture posture = new Posture();

        Framework framework = new Framework("SOC2", FrameworkStatus.COMPLIANT, 0.95);
        for (ComplianceControl ctrl : controls) {
            framework.addControl(new Control(
                ctrl.getId(),
                ControlStatus.valueOf(ctrl.getStatus().toUpperCase()),
                ctrl.getTitle()
            ));
        }

        posture.addFramework(framework);
        posture.setPosture(posture.calculateOverallPosture());

        return posture;
    });
}
```

### Multiple Frameworks

```java
Posture posture = new Posture(CompliancePosture.PARTIALLY_COMPLIANT);

// SOC 2
posture.addFramework(new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0));

// ISO 27001
posture.addFramework(new Framework("ISO27001", FrameworkStatus.PARTIAL, 0.85));

// HIPAA
posture.addFramework(new Framework("HIPAA", FrameworkStatus.COMPLIANT, 1.0));
```

### Custom Extensions

```java
Map<String, Object> acmeExtension = new HashMap<>();
acmeExtension.put("customer_count", 5000);
acmeExtension.put("data_centers", Arrays.asList("us-east-1", "eu-west-1"));
acmeExtension.put("encryption", "AES-256-GCM");
acmeExtension.put("uptime_sla", 99.99);

posture.addExtension("acme", acmeExtension);
```

### Evidence References

```java
Map<String, Object> evidenceRef = new HashMap<>();
evidenceRef.put("type", "presigned_url");
evidenceRef.put("url", "https://s3.aws.com/evidence/audit.pdf?signature=...");
evidenceRef.put("expires", "2024-01-24T00:00:00Z");
evidenceRef.put("hash", "sha256:a1b2c3...");
evidenceRef.put("description", "Complete audit package");

posture.addEvidenceRef(evidenceRef);
```

## API Reference

### Classes

- `Posture` - Main compliance posture structure
- `Framework` - Compliance framework (SOC2, ISO27001, etc.)
- `Control` - Individual compliance control
- `Organization` - Organization metadata
- `CpxController` - Spring MVC controller
- `CpxServlet` - Jakarta Servlet

### Enums

- `CompliancePosture` - COMPLIANT, PARTIALLY_COMPLIANT, NON_COMPLIANT, UNKNOWN
- `FrameworkStatus` - COMPLIANT, PARTIAL, NON_COMPLIANT
- `ControlStatus` - COMPLIANT, PARTIAL, NON_COMPLIANT

## Testing Your Endpoint

```bash
# Get compliance posture
curl http://localhost:8080/cpx

# Pretty print
curl http://localhost:8080/cpx | jq

# Check specific framework
curl http://localhost:8080/cpx | jq '.frameworks[] | select(.name=="SOC2")'
```

## Requirements

- Java 11+
- Jackson 2.15+ (for JSON serialization)
- Spring Web 6.0+ (optional, for Spring Boot integration)
- Jakarta Servlet API 6.0+ (optional, for servlet containers)

## Building

```bash
mvn clean compile
mvn package
```

## License

MIT License
