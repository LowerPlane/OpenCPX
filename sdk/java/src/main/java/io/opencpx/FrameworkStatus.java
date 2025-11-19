package io.opencpx;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Compliance state for a framework.
 */
public enum FrameworkStatus {
    COMPLIANT("compliant"),
    PARTIAL("partial"),
    NON_COMPLIANT("non_compliant");

    private final String value;

    FrameworkStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
