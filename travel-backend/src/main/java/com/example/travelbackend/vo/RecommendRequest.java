package com.example.travelbackend.vo;

/** Body of POST /api/travel/recommend and POST /api/trips. */
public record RecommendRequest(String city, Double budget, Integer days) {
}
