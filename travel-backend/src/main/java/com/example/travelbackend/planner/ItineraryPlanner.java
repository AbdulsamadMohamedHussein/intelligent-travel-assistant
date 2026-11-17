package com.example.travelbackend.planner;

import com.example.travelbackend.model.City;
import com.example.travelbackend.model.DayItinerary;

import java.util.List;

/** Strategy interface: different planners can build an itinerary in different ways. */
public interface ItineraryPlanner {
    String getName();

    /** Multiplier applied to the hotel price (1.0 = mid-range, below 1.0 = budget hotels). */
    double getHotelFactor();

    List<DayItinerary> plan(City city, int days);
}
