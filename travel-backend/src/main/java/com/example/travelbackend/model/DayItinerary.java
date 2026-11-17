package com.example.travelbackend.model;

import java.util.List;

/** One day of a plan: a place for each of the three time slots. */
public class DayItinerary {
    private final int day;
    private final Place morning;
    private final Place afternoon;
    private final Place evening;

    public DayItinerary(int day, Place morning, Place afternoon, Place evening) {
        this.day = day;
        this.morning = morning;
        this.afternoon = afternoon;
        this.evening = evening;
    }

    public int getDay() { return day; }
    public Place getMorning() { return morning; }
    public Place getAfternoon() { return afternoon; }
    public Place getEvening() { return evening; }

    public List<Place> getPlaces() {
        return List.of(morning, afternoon, evening);
    }
}
