package com.example.interviewsimulator.repository;

import com.example.interviewsimulator.model.InterviewResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InterviewResponseRepository extends MongoRepository<InterviewResponse, String> {}

