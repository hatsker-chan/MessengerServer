package org.example.entities;

public record Message(User sender, String text, int chat_id) {}