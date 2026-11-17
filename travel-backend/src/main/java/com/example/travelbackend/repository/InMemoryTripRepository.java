package com.example.travelbackend.repository;

import com.example.travelbackend.vo.SavedTrip;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Keeps saved trips in memory, grouped by user id. Data is lost when the server restarts. */
@Repository
public class InMemoryTripRepository implements TripRepository {
    private final Map<String, List<SavedTrip>> store = new HashMap<>();

    @Override
    public synchronized SavedTrip save(String userId, SavedTrip trip) {
        store.computeIfAbsent(userId, k -> new ArrayList<>()).add(trip);
        return trip;
    }

    @Override
    public synchronized List<SavedTrip> findAll(String userId) {
        List<SavedTrip> trips = new ArrayList<>(store.getOrDefault(userId, List.of()));
        java.util.Collections.reverse(trips); // newest first
        return trips;
    }

    @Override
    public synchronized Optional<SavedTrip> findById(String userId, String id) {
        return store.getOrDefault(userId, List.of()).stream().filter(t -> t.id().equals(id)).findFirst();
    }

    @Override
    public synchronized boolean delete(String userId, String id) {
        List<SavedTrip> trips = store.get(userId);
        return trips != null && trips.removeIf(t -> t.id().equals(id));
    }
}
