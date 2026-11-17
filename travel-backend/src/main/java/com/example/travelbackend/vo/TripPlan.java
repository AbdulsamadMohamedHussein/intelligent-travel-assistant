package com.example.travelbackend.vo;

import java.util.List;

/** The complete recommendation returned to the app. */
public record TripPlan(String city, int days, int totalBudget, int estimatedCost, int remaining,
                       String strategy, List<DayPlan> dailyItinerary, BudgetBreakdown budgetBreakdown,
                       List<String> tips, List<String> warnings) {
}
