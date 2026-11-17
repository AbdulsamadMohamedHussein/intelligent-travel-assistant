package com.example.travelbackend.vo;

/** One morning / afternoon / evening entry as shown in the app. */
public record SpotSlot(String spot, String category, String duration, String ticket,
                       String transportation, String description) {
}
