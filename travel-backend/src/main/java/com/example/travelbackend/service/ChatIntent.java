package com.example.travelbackend.service;

import java.util.List;
import java.util.Set;

/** What the user is asking about. Order of the constants is the order of matching. */
public enum ChatIntent {
    TALLEST_BUILDING(List.of("tallest", "highest building", "skyscraper")),
    WEATHER(List.of("weather", "climate", "temperature", "rain", "season", "best time", "when to visit")),
    FOOD(List.of("food", "eat", "restaurant", "dish", "cuisine", "hotpot", "snack", "dinner", "lunch")),
    BUDGET(List.of("budget", "cost", "how much", "price", "expensive", "cheap")),
    PLACES(List.of("visit", "place", "places", "attraction", "attractions", "sight", "sights", "see",
            "landmark", "things to do", "best")),
    THANKS(List.of("thanks", "thank")),
    GREETING(List.of("hello", "hi", "hey", "good morning", "good evening")),
    HELP(List.of("help", "what can you do")),
    UNKNOWN(List.of());

    private final List<String> keywords;

    ChatIntent(List<String> keywords) {
        this.keywords = keywords;
    }

    /** Single words must match whole words; phrases are matched as substrings. */
    boolean matches(String lowerText, Set<String> words) {
        for (String k : keywords) {
            if (k.contains(" ") ? lowerText.contains(k) : words.contains(k)) {
                return true;
            }
        }
        return false;
    }
}
