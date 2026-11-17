package com.example.travelbackend.vo;

/** Body of POST /api/travel/chat. city is an optional context hint from the app. */
public record ChatRequest(String message, String city) {
}
