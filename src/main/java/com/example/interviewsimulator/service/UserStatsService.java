package com.example.interviewsimulator.service;

import com.example.interviewsimulator.model.UserStats;
import com.example.interviewsimulator.repository.UserStatsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private String detectTopic(String question) {
    question = question.toLowerCase();

    if (question.contains("css") || question.contains("flex") || question.contains("grid")) return "CSS";
    if (question.contains("javascript") || question.contains("js") || question.contains("closure")) return "JavaScript";
    if (question.contains("react") || question.contains("component")) return "React";
    if (question.contains("algorithm") || question.contains("time complexity") || question.contains("sort")) return "Algorithms";
    if (question.contains("html")) return "HTML";
    
    return "General";
}

   public void updateInterviewStats(String email, UserStats.InterviewRecord record) {
    UserStats stats = repository.findByEmail(email);

    if (stats == null) {
        stats = new UserStats();
        stats.setEmail(email);
        stats.setName("Unknown User");
        stats.setPastInterviews(new ArrayList<>());
    }

    if (stats.getPastInterviews() == null) {
        stats.setPastInterviews(new ArrayList<>());
    }

    // Add new interview record
    stats.getPastInterviews().add(record);

    // ✅ Calculate progress (average score × 10 to get %)
    double totalScore = stats.getPastInterviews().stream()
        .mapToDouble(UserStats.InterviewRecord::getScore)
        .sum();
    double avgScore = totalScore / stats.getPastInterviews().size();
    stats.setProgress(avgScore * 10);

    // ✅ Set average response time (mocked for now)
    stats.setAvgResponseTime(6.5); // You can calculate this dynamically later

    // ✅ Calculate skill levels
    Map<String, Integer> skillMap = new HashMap<>();
    for (UserStats.InterviewRecord ir : stats.getPastInterviews()) {
        String topic = detectTopic(ir.getQuestion());
        int score = (int) Math.round(ir.getScore());

        // If multiple attempts exist for same topic, take average
        skillMap.merge(topic, score, (prev, newScore) -> (prev + newScore) / 2);
    }
    stats.setSkillLevels(skillMap);

    System.out.println("✅ Added interview for: " + email);
    System.out.println("➡️ Progress: " + stats.getProgress());
    System.out.println("📊 SkillLevels: " + skillMap);

    repository.save(stats);
}

}
