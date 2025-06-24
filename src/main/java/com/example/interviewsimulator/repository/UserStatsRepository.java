package com.example.interviewsimulator.repository;

import com.example.interviewsimulator.model.UserStats;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserStatsRepository extends MongoRepository<UserStats, String> {
    UserStats findByEmail(String email);
}
