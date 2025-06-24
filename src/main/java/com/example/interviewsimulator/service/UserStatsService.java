package com.example.interviewsimulator.service;

import com.example.interviewsimulator.model.UserStats;
import com.example.interviewsimulator.repository.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStatsService {

    @Autowired
    private UserStatsRepository repository;

    public UserStats findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public UserStats save(UserStats stats) {
        return repository.save(stats);
    }

    public void updateInterviewStats(String email, UserStats.InterviewRecord record) {
        UserStats stats = repository.findByEmail(email);
        if (stats != null) {
            stats.getPastInterviews().add(record);
            repository.save(stats);
        }
    }
}
