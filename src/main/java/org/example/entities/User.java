package org.example.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(@JsonProperty("id") int id, @JsonProperty("nickname") String name) {
}

