package com.example.interviewsimulator.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GeminiResponse {

    @JsonProperty("score")
    private int score;

    @JsonProperty("strengths")
    private List<String> strengths;

    @JsonProperty("weaknesses")
    private List<String> weaknesses;

    @JsonProperty("feedback")
    private String feedback;

    // Constructors
    public GeminiResponse() {}

    public GeminiResponse(int score, List<String> strengths, List<String> weaknesses, String feedback) {
        this.score = score;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.feedback = feedback;
    }

    // Getters & Setters
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
