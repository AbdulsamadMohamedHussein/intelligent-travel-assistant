package com.example.travelbackend.service;

import com.example.travelbackend.exception.BusinessException;
import com.example.travelbackend.model.Attraction;
import com.example.travelbackend.model.City;
import com.example.travelbackend.model.FoodSpot;
import com.example.travelbackend.repository.CityRepository;
import com.example.travelbackend.repository.CsvCityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A rule-based travel assistant: detects the intent and the city in a message,
 * then builds an answer from the city data. No external AI service is needed.
 */
@Service
public class ChatService {
    private static final Pattern WORD = Pattern.compile("[a-z]+");
    private static final Pattern DAYS = Pattern.compile("(\\d+)\\s*-?\\s*(day|days)");

    private final CityRepository cityRepository;
    private final TripPlanService tripPlanService;

    public ChatService(CityRepository cityRepository, TripPlanService tripPlanService) {
        this.cityRepository = cityRepository;
        this.tripPlanService = tripPlanService;
    }

    public String reply(String message, String cityHint) {
        if (message == null || message.isBlank()) {
            throw new BusinessException("Please type a question.");
        }
        ChatIntent intent = detectIntent(message);
        Optional<City> city = detectCity(message);
        if (city.isEmpty() && cityHint != null) {
            city = cityRepository.findByName(cityHint);
        }

        switch (intent) {
            case GREETING:
                return "Hi! I'm your travel assistant. Ask me about attractions, food, weather, budgets or the "
                        + "tallest building in a city. I know: " + cityList() + ".";
            case THANKS:
                return "You're welcome! Ask me anything else about your trip.";
            case HELP:
                return "I can answer questions such as:\n\u2022 Best place to visit in Beijing\n\u2022 Food recommendation in Chengdu\n"
                        + "\u2022 Weather in Shanghai\n\u2022 How much for 3 days in Hangzhou\n\u2022 Guangzhou's tallest building\n"
                        + "Cities I know: " + cityList() + ".";
            case UNKNOWN:
                return "I'm not sure about that yet. Try asking about attractions, food, weather, budget or the tallest "
                        + "building, and mention a city. I know: " + cityList() + ".";
            default:
                break;
        }

        if (city.isEmpty()) {
            return "Which city do you mean? I currently cover: " + cityList() + ".";
        }
        City c = city.get();
        return switch (intent) {
            case PLACES -> placesAnswer(c);
            case FOOD -> foodAnswer(c);
            case WEATHER -> weatherAnswer(c);
            case TALLEST_BUILDING -> tallestAnswer(c);
            case BUDGET -> budgetAnswer(c, message);
            default -> "Ask me about attractions, food, weather or budget in " + c.getName() + ".";
        };
    }

    ChatIntent detectIntent(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        Set<String> words = new HashSet<>();
        Matcher m = WORD.matcher(lower);
        while (m.find()) {
            words.add(m.group());
        }
        for (ChatIntent intent : ChatIntent.values()) {
            if (intent.matches(lower, words)) {
                return intent;
            }
        }
        return ChatIntent.UNKNOWN;
    }

    Optional<City> detectCity(String message) {
        String normalized = CsvCityRepository.normalize(message);
        return cityRepository.findAll().stream()
                .filter(c -> normalized.contains(CsvCityRepository.normalize(c.getName())))
                .findFirst();
    }

    /** Splits a reply into small pieces so the controller can stream it like a typing effect. */
    public List<String> toChunks(String text, int size) {
        List<String> chunks = new ArrayList<>();
        int[] cps = text.codePoints().toArray();
        for (int i = 0; i < cps.length; i += size) {
            int len = Math.min(size, cps.length - i);
            chunks.add(new String(cps, i, len));
        }
        return chunks;
    }

    private String cityList() {
        return String.join(", ", cityRepository.findAllNames());
    }

    private String placesAnswer(City c) {
        StringBuilder sb = new StringBuilder("Top attractions in " + c.getName() + ":");
        int n = 0;
        for (Attraction a : c.getAttractions()) {
            if (n++ == 4) {
                break;
            }
            sb.append("\n\u2022 ").append(a.getName()).append(" (").append(a.describeCost()).append(", about ")
                    .append(a.getDuration()).append("): ").append(a.getDescription());
        }
        sb.append("\nBest time to go: ").append(c.getBestSeason()).append(".");
        return sb.toString();
    }

    private String foodAnswer(City c) {
        StringBuilder sb = new StringBuilder("Food to try in " + c.getName() + ":");
        int n = 0;
        for (FoodSpot f : c.getFoodSpots()) {
            if (n++ == 3) {
                break;
            }
            sb.append("\n\u2022 ").append(f.getName()).append(" (").append(f.describeCost()).append("): ")
                    .append(f.getDescription());
        }
        return sb.toString();
    }

    private String weatherAnswer(City c) {
        return c.getName() + " has this climate: " + c.getClimate() + "\nBest time to visit: " + c.getBestSeason()
                + ".\nI don't have live forecasts, so please check a weather app a few days before you travel.";
    }

    private String tallestAnswer(City c) {
        if (c.getTallestBuilding() == null) {
            return "I don't have reliable data on the tallest building in " + c.getName() + ".";
        }
        return "The tallest building in " + c.getName() + " is the " + c.getTallestBuilding() + ".";
    }

    private String budgetAnswer(City c, String message) {
        int days = 3;
        Matcher m = DAYS.matcher(message.toLowerCase(Locale.ROOT));
        if (m.find()) {
            days = Integer.parseInt(m.group(1));
        }
        if (days < TripPlanService.MIN_DAYS || days > TripPlanService.MAX_DAYS) {
            return "I can estimate trips of " + TripPlanService.MIN_DAYS + " to " + TripPlanService.MAX_DAYS + " days.";
        }
        int cost = tripPlanService.estimateStandardCost(c, days);
        return "A mid-range trip to " + c.getName() + " for " + days + " days costs about \u00a5" + cost
                + " per person, including hotel, food, local transport and tickets. Use the planner on the home page "
                + "for a full day-by-day breakdown.";
    }
}
