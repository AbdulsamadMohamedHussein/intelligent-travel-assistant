package com.example.travelbackend.model;

import java.util.List;

/** A destination city with its cost levels, climate information and places (composition). */
public class City {
    private final String name;
    private final String province;
    private final int hotelPerNight;
    private final int transportPerDay;
    private final int foodPerDay;
    private final String bestSeason;
    private final String climate;
    private final String tallestBuilding;
    private final List<String> tips;
    private final List<String> warnings;
    private final List<Place> places;

    public City(String name, String province, int hotelPerNight, int transportPerDay, int foodPerDay,
                String bestSeason, String climate, String tallestBuilding,
                List<String> tips, List<String> warnings, List<Place> places) {
        this.name = name;
        this.province = province;
        this.hotelPerNight = hotelPerNight;
        this.transportPerDay = transportPerDay;
        this.foodPerDay = foodPerDay;
        this.bestSeason = bestSeason;
        this.climate = climate;
        this.tallestBuilding = tallestBuilding;
        this.tips = List.copyOf(tips);
        this.warnings = List.copyOf(warnings);
        this.places = List.copyOf(places);
    }

    /** Places usable in the morning or afternoon slots. */
    public List<Place> getDaytimePlaces() {
        return places.stream().filter(p -> p.getBestTime() != TimeSlot.EVENING).toList();
    }

    /** Places usable in the evening slot (night views, shows, dinners). */
    public List<Place> getEveningPlaces() {
        return places.stream().filter(p -> p.getBestTime() == TimeSlot.EVENING).toList();
    }

    public List<Attraction> getAttractions() {
        return places.stream().filter(p -> p instanceof Attraction).map(p -> (Attraction) p).toList();
    }

    public List<FoodSpot> getFoodSpots() {
        return places.stream().filter(p -> p instanceof FoodSpot).map(p -> (FoodSpot) p).toList();
    }

    public String getName() { return name; }
    public String getProvince() { return province; }
    public int getHotelPerNight() { return hotelPerNight; }
    public int getTransportPerDay() { return transportPerDay; }
    public int getFoodPerDay() { return foodPerDay; }
    public String getBestSeason() { return bestSeason; }
    public String getClimate() { return climate; }
    public String getTallestBuilding() { return tallestBuilding; }
    public List<String> getTips() { return tips; }
    public List<String> getWarnings() { return warnings; }
    public List<Place> getPlaces() { return places; }
}
