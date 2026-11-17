package com.example.travelbackend.repository;

import com.example.travelbackend.model.Attraction;
import com.example.travelbackend.model.City;
import com.example.travelbackend.model.FoodSpot;
import com.example.travelbackend.model.Place;
import com.example.travelbackend.model.TimeSlot;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Loads cities and places from pipe-separated text files in src/main/resources/data at startup. */
@Repository
public class CsvCityRepository implements CityRepository {
    private static final String CITIES_FILE = "data/cities.txt";
    private static final String PLACES_FILE = "data/places.txt";

    private final Map<String, City> cities = new LinkedHashMap<>();

    public CsvCityRepository() {
        Map<String, List<Place>> placesByCity = readPlaces();
        for (String[] f : readRows(CITIES_FILE, 10)) {
            String name = f[0];
            List<Place> places = placesByCity.getOrDefault(name, List.of());
            City city = new City(name, f[1], Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]),
                    f[5], f[6], "-".equals(f[7]) ? null : f[7], split(f[8]), split(f[9]), places);
            cities.put(normalize(name), city);
        }
    }

    @Override
    public List<City> findAll() {
        return List.copyOf(cities.values());
    }

    @Override
    public Optional<City> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cities.get(normalize(name)));
    }

    /** Lower-case and drop apostrophes so "Xi'an", "xi'an" and "xian" all match. */
    public static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("[\\u2018\\u2019'`\\s]", "");
    }

    private Map<String, List<Place>> readPlaces() {
        Map<String, List<Place>> result = new LinkedHashMap<>();
        int priority = 0;
        for (String[] f : readRows(PLACES_FILE, 8)) {
            priority++;
            int cost = Integer.parseInt(f[3]);
            TimeSlot slot = TimeSlot.parse(f[5]);
            Place place = switch (f[1]) {
                case "A" -> new Attraction(f[2], f[0], cost, f[4], slot, f[6], f[7], priority);
                case "F" -> new FoodSpot(f[2], f[0], cost, f[4], slot, f[6], f[7], priority);
                default -> throw new IllegalStateException("Unknown place type '" + f[1] + "' for " + f[2]);
            };
            result.computeIfAbsent(f[0], k -> new ArrayList<>()).add(place);
        }
        return result;
    }

    private List<String[]> readRows(String resource, int columns) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalStateException("Missing resource: " + resource);
        }
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                if (fields.length != columns) {
                    throw new IllegalStateException(resource + " line " + lineNo + ": expected " + columns
                            + " columns but found " + fields.length);
                }
                rows.add(fields);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + resource, e);
        }
        return rows;
    }

    private List<String> split(String text) {
        return Arrays.stream(text.split(";")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
