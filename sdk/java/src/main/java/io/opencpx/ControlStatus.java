package io.opencpx;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Compliance state for a control.
 */
public enum ControlStatus {
    COMPLIANT("compliant"),
    PARTIAL("partial"),
    NON_COMPLIANT("non_compliant");

    private final String value;

    ControlStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
