package io.opencpx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Organization information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization {

    @JsonProperty("name")
    private String name;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("contact")
    private String contact;

    public Organization() {
    }

    public Organization(String name) {
        this.name = name;
    }

    public Organization(String name, String domain, String contact) {
        this.name = name;
        this.domain = domain;
        this.contact = contact;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
