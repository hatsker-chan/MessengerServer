package org.example.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(@JsonProperty("user") User sender, @JsonProperty("text") String text,@JsonProperty("chat_id") int chatId) {}