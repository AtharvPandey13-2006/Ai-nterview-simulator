package com.example.interviewsimulator.controller;

import com.example.interviewsimulator.model.UserStats;
import com.example.interviewsimulator.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserStatsController {

    @Autowired
    private UserStatsService service;
    
    
    @GetMapping("/me")
public ResponseEntity<UserStats> getCurrentUserStats(OAuth2AuthenticationToken authentication) {
    String email = authentication.getPrincipal().getAttribute("email");
    String name = authentication.getPrincipal().getAttribute("name");

    UserStats stats = service.findByEmail(email);
    if (stats == null) {
        stats = new UserStats();
        stats.setEmail(email);
        stats.setName(name != null ? name : "Unknown");
        stats = service.save(stats);
    }

    return ResponseEntity.ok(stats);
}


    @GetMapping("/{email}")
public ResponseEntity<UserStats> getStats(@PathVariable String email, OAuth2AuthenticationToken authentication) {
    UserStats stats = service.findByEmail(email);
    if (stats == null) {
        stats = new UserStats();
        stats.setEmail(email);

        // Try getting name from OAuth
        if (authentication != null && authentication.getPrincipal() != null) {
            String name = authentication.getPrincipal().getAttribute("name");
            stats.setName(name != null ? name : "Unknown User");
        } else {
            stats.setName("Unknown User");
        }

        stats = service.save(stats);
    }

    return ResponseEntity.ok(stats);
}


    @PostMapping("/")
    public ResponseEntity<UserStats> saveStats(@RequestBody UserStats stats) {
        UserStats saved = service.save(stats);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{email}/addInterview")
    public ResponseEntity<Void> addInterview(@PathVariable String email, @RequestBody UserStats.InterviewRecord record) {
        service.updateInterviewStats(email, record);
        return ResponseEntity.ok().build();
    }
}
