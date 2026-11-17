package com.example.travelbackend.planner;

import com.example.travelbackend.model.Place;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/** Cheapest places first (popularity breaks ties), budget hotels. */
@Component
public class EconomyPlanner extends AbstractItineraryPlanner {

    @Override
    public String getName() {
        return "Economy";
    }

    @Override
    public double getHotelFactor() {
        return 0.6;
    }

    @Override
    protected Comparator<Place> ordering() {
        return Comparator.comparingInt(Place::getCost).thenComparingInt(Place::getPriority);
    }
}
