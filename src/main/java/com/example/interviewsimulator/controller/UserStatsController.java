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

   @GetMapping("/{email}")
public ResponseEntity<UserStats> getStats(@PathVariable String email, OAuth2AuthenticationToken authentication) {
    UserStats stats = service.findByEmail(email);

    if (stats == null) {
        stats = new UserStats();
        stats.setEmail(email);

        String name = (authentication != null && authentication.getPrincipal() != null)
            ? authentication.getPrincipal().getAttribute("name")
            : null;

        stats.setName(name != null ? name : "Unknown User");

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
