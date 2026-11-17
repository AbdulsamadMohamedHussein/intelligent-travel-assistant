package com.example.travelbackend.repository;

import com.example.travelbackend.model.City;

import java.util.List;
import java.util.Optional;

/** Abstraction over where city data comes from (text file today, a database tomorrow). */
public interface CityRepository {
    List<City> findAll();

    Optional<City> findByName(String name);

    default List<String> findAllNames() {
        return findAll().stream().map(City::getName).toList();
    }
}
