package com.example.travelbackend.controller;

import com.example.travelbackend.service.TripService;
import com.example.travelbackend.vo.RecommendRequest;
import com.example.travelbackend.vo.Result;
import com.example.travelbackend.vo.SavedTrip;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Saved trips of one (anonymous) user, identified by the X-User-Id header the app generates. */
@RestController
@RequestMapping("/api/trips")
public class TripController {
    private static final String USER_HEADER = "X-User-Id";

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public Result<SavedTrip> save(@RequestHeader(value = USER_HEADER, defaultValue = "anonymous") String userId,
                                  @RequestBody RecommendRequest request) {
        return Result.ok(tripService.save(userId, request));
    }

    @GetMapping
    public Result<List<SavedTrip>> list(@RequestHeader(value = USER_HEADER, defaultValue = "anonymous") String userId) {
        return Result.ok(tripService.list(userId));
    }

    @GetMapping("/{id}")
    public Result<SavedTrip> get(@RequestHeader(value = USER_HEADER, defaultValue = "anonymous") String userId,
                                 @PathVariable String id) {
        return Result.ok(tripService.get(userId, id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(value = USER_HEADER, defaultValue = "anonymous") String userId,
                               @PathVariable String id) {
        tripService.delete(userId, id);
        return Result.ok();
    }
}
