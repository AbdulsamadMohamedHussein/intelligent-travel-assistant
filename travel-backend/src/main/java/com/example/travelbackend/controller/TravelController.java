package com.example.travelbackend.controller;

import com.example.travelbackend.exception.BusinessException;
import com.example.travelbackend.service.ChatService;
import com.example.travelbackend.service.TripPlanService;
import com.example.travelbackend.vo.ChatRequest;
import com.example.travelbackend.vo.RecommendRequest;
import com.example.travelbackend.vo.Result;
import com.example.travelbackend.vo.TripPlan;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Travel planning and chat endpoints. Controllers only handle HTTP; logic lives in the services. */
@RestController
@RequestMapping("/api/travel")
public class TravelController {
    private static final int CHUNK_SIZE = 3;
    private static final long CHUNK_DELAY_MS = 20;

    private final TripPlanService tripPlanService;
    private final ChatService chatService;
    private final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "chat-stream");
        t.setDaemon(true);
        return t;
    });

    public TravelController(TripPlanService tripPlanService, ChatService chatService) {
        this.tripPlanService = tripPlanService;
        this.chatService = chatService;
    }

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.ok("Hello World!");
    }

    @GetMapping("/cities")
    public Result<List<String>> cities() {
        return Result.ok(tripPlanService.getSupportedCities());
    }

    @PostMapping("/recommend")
    public Result<TripPlan> recommend(@RequestBody RecommendRequest request) {
        return Result.ok(tripPlanService.recommend(request));
    }

    /** Streams the answer as Server-Sent Events: data:{"type":"chunk",...} ... data:{"done":true}. */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(60_000L);
        executor.execute(() -> {
            try {
                String reply = chatService.reply(request.message(), request.city());
                for (String chunk : chatService.toChunks(reply, CHUNK_SIZE)) {
                    emitter.send(SseEmitter.event().data(Map.of("type", "chunk", "content", chunk),
                            MediaType.APPLICATION_JSON));
                    Thread.sleep(CHUNK_DELAY_MS);
                }
                emitter.send(SseEmitter.event().data(Map.of("done", true), MediaType.APPLICATION_JSON));
                emitter.complete();
            } catch (BusinessException e) {
                try {
                    emitter.send(SseEmitter.event().data(Map.of("error", e.getMessage()), MediaType.APPLICATION_JSON));
                } catch (Exception ignored) {
                    // client already disconnected
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}
