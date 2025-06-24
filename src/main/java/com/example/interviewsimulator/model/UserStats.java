package com.example.interviewsimulator.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "user_stats")
public class UserStats {

    @Id
    private String email; // Unique user identifier from Google OAuth
    private String name;
    private double progress; // e.g., 65.0 (%)
    private double avgResponseTime; // e.g., 3.5 seconds

    private Map<String, Integer> skillLevels; // JS: 75, Algo: 60, etc.

    private List<InterviewRecord> pastInterviews;

    @Data
    public static class InterviewRecord {
        private String question;
        private String answer;
        private double score; // Out of 10
        private String date;  // Optional: "2025-06-24"
    }
}
