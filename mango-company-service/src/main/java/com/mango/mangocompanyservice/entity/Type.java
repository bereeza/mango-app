package com.mango.mangocompanyservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("Remote Work")
    REMOTE,
    @JsonProperty("In Office")
    IN_OFFICE,
    @JsonProperty("Hybrid Work")
    HYBRID,
    @JsonProperty("Freelance")
    FREELANCE,
    @JsonProperty("Contract Work")
    CONTRACT,
    @JsonProperty("Internship")
    INTERNSHIP,
    @JsonProperty("Part-Time Work")
    PART_TIME,
    @JsonProperty("Full-Time Work")
    FULL_TIME,
    @JsonProperty("Temporary Work")
    TEMPORARY
}

