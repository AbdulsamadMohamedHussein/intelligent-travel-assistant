package com.example.travelbackend.service;

import com.example.travelbackend.exception.BusinessException;
import com.example.travelbackend.repository.TripRepository;
import com.example.travelbackend.vo.RecommendRequest;
import com.example.travelbackend.vo.SavedTrip;
import com.example.travelbackend.vo.TripPlan;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Saves, lists and deletes trips for one user. */
@Service
public class TripService {
    static final int MAX_TRIPS_PER_USER = 50;

    private final TripRepository tripRepository;
    private final TripPlanService tripPlanService;

    public TripService(TripRepository tripRepository, TripPlanService tripPlanService) {
        this.tripRepository = tripRepository;
        this.tripPlanService = tripPlanService;
    }

    /** The plan is regenerated on the server, so the client cannot save made-up data. */
    public SavedTrip save(String userId, RecommendRequest request) {
        if (tripRepository.findAll(userId).size() >= MAX_TRIPS_PER_USER) {
            throw new BusinessException("You can save at most " + MAX_TRIPS_PER_USER + " trips. Please delete some first.");
        }
        TripPlan plan = tripPlanService.recommend(request);
        SavedTrip trip = new SavedTrip(UUID.randomUUID().toString(), Instant.now().toString(), plan);
        return tripRepository.save(userId, trip);
    }

    public List<SavedTrip> list(String userId) {
        return tripRepository.findAll(userId);
    }

    public SavedTrip get(String userId, String id) {
        return tripRepository.findById(userId, id).orElseThrow(() -> new BusinessException(404, "Trip not found."));
    }

    public void delete(String userId, String id) {
        if (!tripRepository.delete(userId, id)) {
            throw new BusinessException(404, "Trip not found.");
        }
    }
}
