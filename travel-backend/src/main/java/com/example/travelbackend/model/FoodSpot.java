package com.example.travelbackend.model;

/** A restaurant or food street. Its cost is the average meal price per person. */
public class FoodSpot extends Place {

    public FoodSpot(String name, String cityName, int averageMeal, String duration, TimeSlot bestTime,
                    String transportation, String description, int priority) {
        super(name, cityName, averageMeal, duration, bestTime, transportation, description, priority);
    }

    @Override
    public CostCategory getCostCategory() {
        return CostCategory.FOOD;
    }

    @Override
    public String describeCost() {
        return "About \u00a5" + getCost() + " per person";
    }
}
