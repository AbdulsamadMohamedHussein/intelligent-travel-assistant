package com.example.travelbackend.repository;

import com.example.travelbackend.vo.SavedTrip;

import java.util.List;
import java.util.Optional;

/** Storage abstraction for saved trips (in memory now, could be MySQL later). */
public interface TripRepository {
    SavedTrip save(String userId, SavedTrip trip);

    List<SavedTrip> findAll(String userId);

    Optional<SavedTrip> findById(String userId, String id);

    boolean delete(String userId, String id);
}
