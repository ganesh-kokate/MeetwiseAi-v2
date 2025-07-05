package com.meetwise.summariser.controllers;

import com.meetwise.summariser.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/summarize")
public class Controller {

    private final Services services;

    @Autowired
    public Controller(Services services) {
        this.services = services;
    }

    @PostMapping
    public Flux<String> summarize(@RequestBody String message) {
        return services.summurize(message);
    }

    @GetMapping
    public Mono<String> getSumaryById(@RequestParam String meetId)
    {
        return services.summurizeByMeetId(meetId);
    }

    @GetMapping("/test")
    public String test()
    {
        return "test working";
    }
}
