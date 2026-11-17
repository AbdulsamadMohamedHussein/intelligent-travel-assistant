package com.example.travelbackend.model;

/** The part of the day a place is best visited. ANY means no strong preference. */
public enum TimeSlot {
    MORNING, AFTERNOON, EVENING, ANY;

    public static TimeSlot parse(String text) {
        return TimeSlot.valueOf(text.trim().toUpperCase());
    }
}
