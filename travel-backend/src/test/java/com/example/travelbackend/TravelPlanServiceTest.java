package com.example.travelbackend;

import com.example.travelbackend.exception.BusinessException;
import com.example.travelbackend.planner.EconomyPlanner;
import com.example.travelbackend.planner.StandardPlanner;
import com.example.travelbackend.repository.CsvCityRepository;
import com.example.travelbackend.service.BudgetCalculator;
import com.example.travelbackend.service.TripPlanService;
import com.example.travelbackend.vo.DayPlan;
import com.example.travelbackend.vo.RecommendRequest;
import com.example.travelbackend.vo.TripPlan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TravelPlanServiceTest {
    private final CsvCityRepository repository = new CsvCityRepository();
    private final TripPlanService service = new TripPlanService(repository, new BudgetCalculator(),
            new StandardPlanner(), new EconomyPlanner());

    @Test
    void loadsAllEightCities() {
        assertEquals(8, repository.findAll().size());
    }

    @Test
    void findsCityIgnoringCaseAndApostrophe() {
        assertTrue(repository.findByName("xian").isPresent());
        assertTrue(repository.findByName("XI'AN").isPresent());
        assertFalse(repository.findByName("Atlantis").isPresent());
    }

    @Test
    void planHasOneEntryPerDayAndConsistentBudget() {
        TripPlan plan = service.recommend(new RecommendRequest("Beijing", 6000.0, 3));
        assertEquals(3, plan.dailyItinerary().size());
        assertEquals("Beijing", plan.city());
        assertEquals(6000, plan.totalBudget());
        assertEquals(plan.budgetBreakdown().total(), plan.estimatedCost());
        assertEquals(plan.totalBudget() - plan.estimatedCost(), plan.remaining());
        assertEquals("Forbidden City", plan.dailyItinerary().get(0).morning().spot());
    }

    @Test
    void lowBudgetSwitchesToEconomyAndWarns() {
        TripPlan generous = service.recommend(new RecommendRequest("Shanghai", 20000.0, 4));
        TripPlan tight = service.recommend(new RecommendRequest("Shanghai", 500.0, 4));
        assertEquals("Standard", generous.strategy());
        assertEquals("Economy", tight.strategy());
        assertTrue(tight.estimatedCost() < generous.estimatedCost());
        assertTrue(tight.remaining() < 0);
        assertFalse(tight.warnings().isEmpty());
    }

    @Test
    void oneDayTripHasNoAccommodation() {
        TripPlan plan = service.recommend(new RecommendRequest("Chengdu", 1000.0, 1));
        assertEquals(0, plan.budgetBreakdown().accommodation());
    }

    @Test
    void everyCitySupportsEveryTripLength() {
        for (String city : service.getSupportedCities()) {
            for (int days = 1; days <= 7; days++) {
                TripPlan plan = service.recommend(new RecommendRequest(city, 50000.0, days));
                assertEquals(days, plan.dailyItinerary().size());
                for (DayPlan d : plan.dailyItinerary()) {
                    assertFalse(d.morning().spot().isBlank());
                    assertFalse(d.afternoon().spot().isBlank());
                    assertFalse(d.evening().spot().isBlank());
                }
            }
        }
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(BusinessException.class, () -> service.recommend(new RecommendRequest("", 1000.0, 3)));
        assertThrows(BusinessException.class, () -> service.recommend(new RecommendRequest("Beijing", 0.0, 3)));
        assertThrows(BusinessException.class, () -> service.recommend(new RecommendRequest("Beijing", null, 3)));
        assertThrows(BusinessException.class, () -> service.recommend(new RecommendRequest("Beijing", 1000.0, 0)));
        assertThrows(BusinessException.class, () -> service.recommend(new RecommendRequest("Beijing", 1000.0, 8)));
        assertThrows(BusinessException.class, () -> service.recommend(new RecommendRequest("Atlantis", 1000.0, 3)));
    }
}
