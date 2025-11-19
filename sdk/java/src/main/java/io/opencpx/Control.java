package io.opencpx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Single compliance control.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Control {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("status")
    private ControlStatus status;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("remediation_date")
    private String remediationDate;

    @JsonProperty("evidence_refs")
    private List<String> evidenceRefs;

    public Control() {
    }

    public Control(String id, ControlStatus status) {
        this.id = id;
        this.status = status;
    }

    public Control(String id, ControlStatus status, String title) {
        this.id = id;
        this.status = status;
        this.title = title;
    }

    public Control addEvidenceRef(String ref) {
        if (this.evidenceRefs == null) {
            this.evidenceRefs = new ArrayList<>();
        }
        this.evidenceRefs.add(ref);
        return this;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ControlStatus getStatus() {
        return status;
    }

    public void setStatus(ControlStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemediationDate() {
        return remediationDate;
    }

    public void setRemediationDate(String remediationDate) {
        this.remediationDate = remediationDate;
    }

    public List<String> getEvidenceRefs() {
        return evidenceRefs;
    }

    public void setEvidenceRefs(List<String> evidenceRefs) {
        this.evidenceRefs = evidenceRefs;
    }
}
