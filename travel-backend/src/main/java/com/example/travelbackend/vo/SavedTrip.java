package com.example.travelbackend.vo;

/** A trip plan saved by a user. */
public record SavedTrip(String id, String createdAt, TripPlan plan) {
}
