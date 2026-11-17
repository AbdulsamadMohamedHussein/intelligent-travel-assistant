package com.example.travelbackend.service;

import com.example.travelbackend.model.City;
import com.example.travelbackend.model.CostCategory;
import com.example.travelbackend.model.DayItinerary;
import com.example.travelbackend.model.Place;
import com.example.travelbackend.vo.BudgetBreakdown;
import org.springframework.stereotype.Component;

import java.util.List;

/** Estimates the cost per person of an itinerary. */
@Component
public class BudgetCalculator {
    /** Default dinner price when the evening slot is not a restaurant. */
    static final int DEFAULT_DINNER = 60;
    /** Souvenirs, tips and small extras, as a share of all other costs. */
    static final double OTHER_RATE = 0.08;

    public BudgetBreakdown calculate(City city, List<DayItinerary> plan, double hotelFactor) {
        int days = plan.size();
        int accommodation = (int) Math.round(city.getHotelPerNight() * hotelFactor * Math.max(0, days - 1));
        int transportation = city.getTransportPerDay() * days;
        int food = city.getFoodPerDay() * days;
        int tickets = 0;

        for (DayItinerary day : plan) {
            for (Place place : day.getPlaces()) {
                if (place.getCostCategory() == CostCategory.TICKETS) {
                    tickets += place.getCost();
                } else if (place.getCostCategory() == CostCategory.FOOD) {
                    food += place.getCost();
                }
            }
            if (day.getEvening().getCostCategory() != CostCategory.FOOD) {
                food += DEFAULT_DINNER;
            }
        }
        int subtotal = accommodation + transportation + food + tickets;
        int other = (int) Math.round(subtotal * OTHER_RATE);
        return new BudgetBreakdown(accommodation, food, transportation, tickets, other);
    }
}
