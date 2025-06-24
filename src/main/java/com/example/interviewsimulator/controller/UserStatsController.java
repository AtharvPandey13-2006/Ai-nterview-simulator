package com.example.interviewsimulator.controller;

import com.example.interviewsimulator.model.UserStats;
import com.example.interviewsimulator.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserStatsController {

    @Autowired
    private UserStatsService service;

    @GetMapping("/{email}")
    public UserStats getStats(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @PostMapping("/")
    public UserStats saveStats(@RequestBody UserStats stats) {
        return service.save(stats);
    }

    @PostMapping("/{email}/addInterview")
    public void addInterview(@PathVariable String email, @RequestBody UserStats.InterviewRecord record) {
        service.updateInterviewStats(email, record);
    }
}
