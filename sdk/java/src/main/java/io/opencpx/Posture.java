package io.opencpx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the complete OpenCPX compliance posture.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Posture {

    public static final String VERSION = "v1";

    @JsonProperty("version")
    private String version = VERSION;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("compliance_posture")
    private CompliancePosture compliancePosture;

    @JsonProperty("organization")
    private Organization organization;

    @JsonProperty("frameworks")
    private List<Framework> frameworks = new ArrayList<>();

    @JsonProperty("evidence_refs")
    private List<Object> evidenceRefs;

    @JsonProperty("extensions")
    private Map<String, Object> extensions;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public Posture() {
        this.timestamp = Instant.now();
        this.compliancePosture = CompliancePosture.UNKNOWN;
    }

    public Posture(CompliancePosture posture) {
        this.timestamp = Instant.now();
        this.compliancePosture = posture;
    }

    // Builder methods
    public Posture setPosture(CompliancePosture posture) {
        this.compliancePosture = posture;
        return this;
    }

    public Posture setOrganization(Organization org) {
        this.organization = org;
        return this;
    }

    public Posture addFramework(Framework framework) {
        this.frameworks.add(framework);
        return this;
    }

    public Posture addExtension(String key, Object value) {
        if (this.extensions == null) {
            this.extensions = new HashMap<>();
        }
        this.extensions.put(key, value);
        return this;
    }

    public Posture addEvidenceRef(Object ref) {
        if (this.evidenceRefs == null) {
            this.evidenceRefs = new ArrayList<>();
        }
        this.evidenceRefs.add(ref);
        return this;
    }

    /**
     * Calculate overall posture based on frameworks.
     */
    public CompliancePosture calculateOverallPosture() {
        if (frameworks.isEmpty()) {
            return CompliancePosture.UNKNOWN;
        }

        boolean allCompliant = frameworks.stream()
                .allMatch(f -> f.getStatus() == FrameworkStatus.COMPLIANT);
        boolean anyCompliant = frameworks.stream()
                .anyMatch(f -> f.getStatus() == FrameworkStatus.COMPLIANT);

        if (allCompliant) {
            return CompliancePosture.COMPLIANT;
        }
        if (anyCompliant) {
            return CompliancePosture.PARTIALLY_COMPLIANT;
        }
        return CompliancePosture.NON_COMPLIANT;
    }

    /**
     * Convert to JSON string.
     */
    public String toJson() throws Exception {
        return objectMapper.writeValueAsString(this);
    }

    /**
     * Convert to formatted JSON string.
     */
    public String toJsonPretty() throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    // Getters and setters
    public String getVersion() {
        return version;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public CompliancePosture getCompliancePosture() {
        return compliancePosture;
    }

    public Organization getOrganization() {
        return organization;
    }

    public List<Framework> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<Framework> frameworks) {
        this.frameworks = frameworks;
    }

    public List<Object> getEvidenceRefs() {
        return evidenceRefs;
    }

    public void setEvidenceRefs(List<Object> evidenceRefs) {
        this.evidenceRefs = evidenceRefs;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
}
