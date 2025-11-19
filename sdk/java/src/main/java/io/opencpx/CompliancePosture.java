package io.opencpx;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Overall compliance status.
 */
public enum CompliancePosture {
    COMPLIANT("compliant"),
    PARTIALLY_COMPLIANT("partially_compliant"),
    NON_COMPLIANT("non_compliant"),
    UNKNOWN("unknown");

    private final String value;

    CompliancePosture(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
