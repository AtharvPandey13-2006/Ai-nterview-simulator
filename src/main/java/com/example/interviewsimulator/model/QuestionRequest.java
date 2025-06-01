package com.example.interviewsimulator.model;

public class QuestionRequest {
    private String question;

    public QuestionRequest() {}

    public QuestionRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
