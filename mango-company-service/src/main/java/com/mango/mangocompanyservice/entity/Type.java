package com.mango.mangocompanyservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("REMOTE")
    REMOTE,
    @JsonProperty("IN_OFFICE")
    IN_OFFICE,
    @JsonProperty("HYBRID")
    HYBRID,
    @JsonProperty("FREELANCE")
    FREELANCE,
    @JsonProperty("CONTRACT")
    CONTRACT,
    @JsonProperty("INTERNSHIP")
    INTERNSHIP,
    @JsonProperty("PART_TIME")
    PART_TIME,
    @JsonProperty("FULL_TIME")
    FULL_TIME,
    @JsonProperty("TEMPORARY")
    TEMPORARY
}

