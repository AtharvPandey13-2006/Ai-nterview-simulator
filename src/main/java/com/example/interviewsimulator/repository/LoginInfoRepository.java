package com.example.interviewsimulator.repository;

import com.example.interviewsimulator.model.LoginInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginInfoRepository extends MongoRepository<LoginInfo, String> {
}
