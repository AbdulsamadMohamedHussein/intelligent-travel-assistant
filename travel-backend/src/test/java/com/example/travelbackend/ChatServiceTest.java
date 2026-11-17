package com.example.travelbackend;

import com.example.travelbackend.exception.BusinessException;
import com.example.travelbackend.planner.EconomyPlanner;
import com.example.travelbackend.planner.StandardPlanner;
import com.example.travelbackend.repository.CsvCityRepository;
import com.example.travelbackend.service.BudgetCalculator;
import com.example.travelbackend.service.ChatService;
import com.example.travelbackend.service.TripPlanService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatServiceTest {
    private final CsvCityRepository repository = new CsvCityRepository();
    private final ChatService chat = new ChatService(repository, new TripPlanService(repository,
            new BudgetCalculator(), new StandardPlanner(), new EconomyPlanner()));

    @Test
    void answersBestPlaces() {
        assertTrue(chat.reply("Best place to visit in Beijing", null).contains("Forbidden City"));
    }

    @Test
    void answersTallestBuilding() {
        assertTrue(chat.reply("Guangzhou's tallest building", null).contains("530"));
    }

    @Test
    void admitsWhenTallestBuildingIsUnknown() {
        assertTrue(chat.reply("tallest building in Hangzhou", null).contains("don't have reliable data"));
    }

    @Test
    void answersFoodWeatherAndBudget() {
        assertTrue(chat.reply("food recommendation in Chengdu", null).contains("Hot Pot"));
        assertTrue(chat.reply("weather in Shanghai", null).contains("Best time"));
        assertTrue(chat.reply("how much for 3 days in Hangzhou", null).contains("per person"));
    }

    @Test
    void usesCityHintWhenMessageHasNoCity() {
        assertTrue(chat.reply("what is the best food?", "Chongqing").contains("Chongqing"));
    }

    @Test
    void asksForCityWhenMissing() {
        assertTrue(chat.reply("best places to visit", null).contains("Which city"));
    }

    @Test
    void handlesGreetingUnknownAndEmpty() {
        assertTrue(chat.reply("hello", null).startsWith("Hi!"));
        assertTrue(chat.reply("asdf qwerty", null).contains("not sure"));
        assertThrows(BusinessException.class, () -> chat.reply("  ", null));
    }

    @Test
    void chunksJoinBackToOriginal() {
        String text = "Beijing \u00a5150 is great! \u4f60\u597d";
        assertEquals(text, String.join("", chat.toChunks(text, 3)));
    }
}
