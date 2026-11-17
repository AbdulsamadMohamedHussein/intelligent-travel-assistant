package com.example.travelbackend.vo;

/** Estimated cost per person in RMB, split by category. */
public record BudgetBreakdown(int accommodation, int food, int transportation, int tickets, int other) {
    public int total() {
        return accommodation + food + transportation + tickets + other;
    }
}
