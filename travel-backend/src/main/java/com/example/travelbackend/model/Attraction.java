package com.example.travelbackend.model;

/** A sight or activity that may charge an entrance ticket. */
public class Attraction extends Place {

    public Attraction(String name, String cityName, int ticketPrice, String duration, TimeSlot bestTime,
                      String transportation, String description, int priority) {
        super(name, cityName, ticketPrice, duration, bestTime, transportation, description, priority);
    }

    @Override
    public CostCategory getCostCategory() {
        return CostCategory.TICKETS;
    }

    @Override
    public String describeCost() {
        return getCost() == 0 ? "Free entry" : "Ticket about \u00a5" + getCost();
    }
}
