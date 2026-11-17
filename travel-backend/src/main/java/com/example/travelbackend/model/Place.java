package com.example.travelbackend.model;

/**
 * Abstract base class of everything a traveller can do in a city.
 * Subclasses decide how their cost is classified and described (polymorphism).
 */
public abstract class Place {
    private final String name;
    private final String cityName;
    private final int cost;
    private final String duration;
    private final TimeSlot bestTime;
    private final String transportation;
    private final String description;
    private final int priority;

    protected Place(String name, String cityName, int cost, String duration, TimeSlot bestTime,
                    String transportation, String description, int priority) {
        this.name = name;
        this.cityName = cityName;
        this.cost = cost;
        this.duration = duration;
        this.bestTime = bestTime;
        this.transportation = transportation;
        this.description = description;
        this.priority = priority;
    }

    /** Which budget line this place's cost belongs to. */
    public abstract CostCategory getCostCategory();

    /** Human readable cost text shown in the app. */
    public abstract String describeCost();

    public String getName() { return name; }
    public String getCityName() { return cityName; }
    public int getCost() { return cost; }
    public String getDuration() { return duration; }
    public TimeSlot getBestTime() { return bestTime; }
    public String getTransportation() { return transportation; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + ", " + cityName + "]";
    }
}
