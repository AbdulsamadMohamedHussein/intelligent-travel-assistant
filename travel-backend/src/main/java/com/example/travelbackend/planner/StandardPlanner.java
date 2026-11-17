package com.example.travelbackend.planner;

import com.example.travelbackend.model.Place;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/** Most popular places first, mid-range hotels. */
@Component
public class StandardPlanner extends AbstractItineraryPlanner {

    @Override
    public String getName() {
        return "Standard";
    }

    @Override
    public double getHotelFactor() {
        return 1.0;
    }

    @Override
    protected Comparator<Place> ordering() {
        return Comparator.comparingInt(Place::getPriority);
    }
}
