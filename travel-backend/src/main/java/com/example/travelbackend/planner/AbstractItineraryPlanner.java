package com.example.travelbackend.planner;

import com.example.travelbackend.model.City;
import com.example.travelbackend.model.DayItinerary;
import com.example.travelbackend.model.FreeTimePlace;
import com.example.travelbackend.model.Place;
import com.example.travelbackend.model.TimeSlot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Template Method pattern: the algorithm for filling the days is fixed here,
 * subclasses only decide the ORDER in which places are considered.
 */
public abstract class AbstractItineraryPlanner implements ItineraryPlanner {

    protected abstract Comparator<Place> ordering();

    @Override
    public List<DayItinerary> plan(City city, int days) {
        List<Place> daytime = new ArrayList<>(city.getDaytimePlaces());
        List<Place> evening = new ArrayList<>(city.getEveningPlaces());
        daytime.sort(ordering());
        evening.sort(ordering());

        Set<Place> used = new HashSet<>();
        List<DayItinerary> result = new ArrayList<>();
        for (int day = 1; day <= days; day++) {
            Place morning = pick(daytime, used, TimeSlot.MORNING, city.getName());
            Place afternoon = pick(daytime, used, TimeSlot.AFTERNOON, city.getName());
            Place night = pick(evening, used, TimeSlot.EVENING, city.getName());
            result.add(new DayItinerary(day, morning, afternoon, night));
        }
        return result;
    }

    /** Prefer an unused place tagged for this slot, then an ANY place, then anything left, else free time. */
    private Place pick(List<Place> pool, Set<Place> used, TimeSlot preferred, String cityName) {
        for (TimeSlot wanted : new TimeSlot[]{preferred, TimeSlot.ANY}) {
            for (Place p : pool) {
                if (!used.contains(p) && p.getBestTime() == wanted) {
                    used.add(p);
                    return p;
                }
            }
        }
        for (Place p : pool) {
            if (!used.contains(p)) {
                used.add(p);
                return p;
            }
        }
        return new FreeTimePlace(cityName, preferred);
    }
}
