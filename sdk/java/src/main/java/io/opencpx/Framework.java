package io.opencpx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Compliance framework evaluation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Framework {

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("status")
    private FrameworkStatus status;

    @JsonProperty("score")
    private double score;

    @JsonProperty("last_audit")
    private String lastAudit;

    @JsonProperty("auditor")
    private String auditor;

    @JsonProperty("report_ref")
    private String reportRef;

    @JsonProperty("certificate_ref")
    private String certificateRef;

    @JsonProperty("controls")
    private List<Control> controls;

    public Framework() {
    }

    public Framework(String name, FrameworkStatus status, double score) {
        this.name = name;
        this.status = status;
        this.score = score;
        this.controls = new ArrayList<>();
    }

    public Framework addControl(Control control) {
        if (this.controls == null) {
            this.controls = new ArrayList<>();
        }
        this.controls.add(control);
        return this;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public FrameworkStatus getStatus() {
        return status;
    }

    public void setStatus(FrameworkStatus status) {
        this.status = status;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getLastAudit() {
        return lastAudit;
    }

    public void setLastAudit(String lastAudit) {
        this.lastAudit = lastAudit;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getReportRef() {
        return reportRef;
    }

    public void setReportRef(String reportRef) {
        this.reportRef = reportRef;
    }

    public String getCertificateRef() {
        return certificateRef;
    }

    public void setCertificateRef(String certificateRef) {
        this.certificateRef = certificateRef;
    }

    public List<Control> getControls() {
        return controls;
    }

    public void setControls(List<Control> controls) {
        this.controls = controls;
    }
}
