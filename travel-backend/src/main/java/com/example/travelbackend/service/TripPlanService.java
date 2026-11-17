package com.example.travelbackend.service;

import com.example.travelbackend.exception.BusinessException;
import com.example.travelbackend.model.City;
import com.example.travelbackend.model.DayItinerary;
import com.example.travelbackend.model.Place;
import com.example.travelbackend.planner.EconomyPlanner;
import com.example.travelbackend.planner.ItineraryPlanner;
import com.example.travelbackend.planner.StandardPlanner;
import com.example.travelbackend.repository.CityRepository;
import com.example.travelbackend.vo.BudgetBreakdown;
import com.example.travelbackend.vo.DayPlan;
import com.example.travelbackend.vo.RecommendRequest;
import com.example.travelbackend.vo.SpotSlot;
import com.example.travelbackend.vo.TripPlan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Core business logic: validate the request, choose a planner, and assemble the recommendation. */
@Service
public class TripPlanService {
    public static final int MIN_DAYS = 1;
    public static final int MAX_DAYS = 7;
    private static final double MAX_BUDGET = 1_000_000;

    private final CityRepository cityRepository;
    private final BudgetCalculator budgetCalculator;
    private final ItineraryPlanner standardPlanner;
    private final ItineraryPlanner economyPlanner;

    public TripPlanService(CityRepository cityRepository, BudgetCalculator budgetCalculator,
                           StandardPlanner standardPlanner, EconomyPlanner economyPlanner) {
        this.cityRepository = cityRepository;
        this.budgetCalculator = budgetCalculator;
        this.standardPlanner = standardPlanner;
        this.economyPlanner = economyPlanner;
    }

    public List<String> getSupportedCities() {
        return cityRepository.findAllNames();
    }

    public TripPlan recommend(RecommendRequest request) {
        validate(request);
        City city = cityRepository.findByName(request.city()).orElseThrow(() -> new BusinessException(
                "City '" + request.city() + "' is not supported yet. Supported cities: "
                        + String.join(", ", cityRepository.findAllNames()) + "."));
        int days = request.days();
        int budget = (int) Math.round(request.budget());

        Candidate chosen = evaluate(city, days, standardPlanner);
        boolean economyApplied = false;
        if (chosen.cost() > budget) {
            Candidate economy = evaluate(city, days, economyPlanner);
            if (economy.cost() < chosen.cost()) {
                chosen = economy;
                economyApplied = true;
            }
        }
        return assemble(city, days, budget, chosen, economyApplied);
    }

    /** Estimated cost per person of a standard plan; used by the chat assistant. */
    public int estimateStandardCost(City city, int days) {
        return evaluate(city, days, standardPlanner).cost();
    }

    private void validate(RecommendRequest r) {
        if (r == null || r.city() == null || r.city().isBlank()) {
            throw new BusinessException("Please choose a destination city.");
        }
        if (r.budget() == null || r.budget().isNaN() || r.budget() <= 0) {
            throw new BusinessException("Budget must be greater than 0.");
        }
        if (r.budget() > MAX_BUDGET) {
            throw new BusinessException("Budget is too large. Please enter an amount up to 1,000,000.");
        }
        if (r.days() == null || r.days() < MIN_DAYS || r.days() > MAX_DAYS) {
            throw new BusinessException("Days must be between " + MIN_DAYS + " and " + MAX_DAYS + ".");
        }
    }

    private Candidate evaluate(City city, int days, ItineraryPlanner planner) {
        List<DayItinerary> itinerary = planner.plan(city, days);
        BudgetBreakdown breakdown = budgetCalculator.calculate(city, itinerary, planner.getHotelFactor());
        return new Candidate(planner, itinerary, breakdown);
    }

    private TripPlan assemble(City city, int days, int budget, Candidate chosen, boolean economyApplied) {
        int cost = chosen.cost();
        int remaining = budget - cost;

        List<String> tips = new ArrayList<>();
        if (economyApplied) {
            tips.add("To fit your budget, this plan uses budget hotels and puts free or cheap attractions first.");
        }
        tips.addAll(city.getTips());
        tips.add("All amounts are rough estimates per person in RMB. Real prices change with the season.");
        if (days == 1) {
            tips.add("A one-day trip needs no hotel, so accommodation is 0.");
        }
        if (remaining > cost / 2 && remaining > 0) {
            tips.add("You have about \u00a5" + remaining + " left. Consider a better hotel or extra paid experiences.");
        }

        List<String> warnings = new ArrayList<>(city.getWarnings());
        if (remaining < 0) {
            warnings.add(0, "Your budget is \u00a5" + (-remaining) + " below the estimated minimum cost of \u00a5" + cost
                    + ". Consider increasing the budget or reducing the number of days.");
        }

        List<DayPlan> dayPlans = chosen.itinerary().stream()
                .map(d -> new DayPlan(d.getDay(), toSlot(d.getMorning()), toSlot(d.getAfternoon()), toSlot(d.getEvening())))
                .toList();

        return new TripPlan(city.getName(), days, budget, cost, remaining, chosen.planner().getName(),
                dayPlans, chosen.breakdown(), tips, warnings);
    }

    private SpotSlot toSlot(Place p) {
        return new SpotSlot(p.getName(), p.getCostCategory().name().toLowerCase(), p.getDuration(),
                p.describeCost(), p.getTransportation(), p.getDescription());
    }

    private record Candidate(ItineraryPlanner planner, List<DayItinerary> itinerary, BudgetBreakdown breakdown) {
        int cost() {
            return breakdown.total();
        }
    }
}
