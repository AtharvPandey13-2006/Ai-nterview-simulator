package com.example.interviewsimulator.controller;

import com.example.interviewsimulator.model.AnswerRequest;
import com.example.interviewsimulator.model.QuestionRequest;
import com.example.interviewsimulator.service.GeminiService;
import com.example.interviewsimulator.model.GeminiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://atharvpandey13-2006.github.io", "http://127.0.0.1:5501","https://golden-swan-a56b79.netlify.app"})
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/ask")
    public String askAI(@RequestBody QuestionRequest request) {
        return geminiService.askGemini(request.getQuestion());
    }

    @GetMapping("/startInterview")
    public String startInterview(@RequestParam String role) {
        String prompt = "Start a mock interview for the role of a " + role + ". Ask a question.ONLY QUESTION NOT A SINGLE EXTRA WORD";
        return geminiService.askGemini(prompt);
    }
@PostMapping(value = "/submitAnswer", produces = "application/json")
@ResponseBody
public GeminiResponse submitAnswer(@RequestBody AnswerRequest request, HttpSession session) {
    String prompt = 
    "You must ONLY return a valid JSON object. Do not explain anything. Do not wrap it in triple backticks or markdown.\n\n"
    + "You are acting as an AI interviewer for the role of " + request.getRole() + ".\n"
    + "Ask behavioural and role based question . Here is the question I asked: \"" + request.getQuestion() + "\"\n"
    + "Here is the candidate's answer: \"" + request.getAnswer() + "\"\n"
    + "Please evaluate this answer and give:\n"
    + "1. A score out of 10\n"
    + "2. A list of strengths\n"
    + "3. A list of weaknesses\n"
    + "4. A brief feedback paragraph\n"
    + "Return this in JSON format like:\n"
    + "{ \"score\": 8, \"strengths\": [\"Clear explanation\"], \"weaknesses\": [\"Too short\"], \"feedback\": \"You explained clearly but missed some edge cases.\" }";

    // String prompt = "You are acting as an AI interviewer for the role of " + request.getRole() + ".\n"
    //         + "Here is the question I asked: \"" + request.getQuestion() + "\"\n"
    //         + "Here is the candidate's answer: \"" + request.getAnswer() + "\"\n"
    //         + "Please evaluate this answer and give:\n"
    //         + "1. A score out of 10\n"
    //         + "2. A list of strengths\n"
    //         + "3. A list of weaknesses\n"
    //         + "4. A brief feedback paragraph\n"
    //         + "Return this in JSON format like: { \"score\": 8, \"strengths\": [\"Clear explanation\"], \"weaknesses\": [\"Too short\"], \"feedback\": \"You explained clearly but missed some edge cases.\" }";

    String raw = geminiService.askGemini(prompt).trim();

// Remove triple backticks if present
if (raw.startsWith("```")) {
    int startIndex = raw.indexOf("{");
    int endIndex = raw.lastIndexOf("}");
    if (startIndex != -1 && endIndex != -1) {
        raw = raw.substring(startIndex, endIndex + 1);
    }
}

    System.out.println("AI RAW RESPONSE: " + raw);

    ObjectMapper mapper = new ObjectMapper();
    GeminiResponse geminiResponse;
    try {
        geminiResponse = mapper.readValue(raw, GeminiResponse.class);
    } catch (IOException e) {
        geminiResponse = new GeminiResponse(5, List.of(), List.of(), "AI returned invalid format.");
    }

    List<GeminiResponse> feedbackList = (List<GeminiResponse>) session.getAttribute("feedbackList");
    if (feedbackList == null) feedbackList = new ArrayList<>();
    feedbackList.add(geminiResponse);
    session.setAttribute("feedbackList", feedbackList);

    return geminiResponse; // ✅ returning full JSON object, not just String
}

    

    @GetMapping("/nextQuestion")
    public String getNextQuestion(@RequestParam String role, @RequestParam(defaultValue = "0") int questionIndex) {
        String prompt = "You are conducting a mock interview for the role of " + role + ". "
                + "Ask the " + (questionIndex + 1) + "th question in the interview. "
                + "ONLY QUESTION, NOT A SINGLE EXTRA WORD.";

        return geminiService.askGemini(prompt);
    }

    @GetMapping("/score")
    public ResponseEntity<Map<String, Object>> getScore(HttpSession session) {
        List<GeminiResponse> feedbackList = (List<GeminiResponse>) session.getAttribute("feedbackList");

        if (feedbackList == null || feedbackList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No answers found"));
        }

        int totalScore = feedbackList.stream().mapToInt(GeminiResponse::getScore).sum();
        Set<String> strengths = new HashSet<>();
        Set<String> weaknesses = new HashSet<>();

        for (GeminiResponse response : feedbackList) {
            strengths.addAll(response.getStrengths());
            weaknesses.addAll(response.getWeaknesses());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("score", totalScore);
        response.put("strengths", new ArrayList<>(strengths));
        response.put("weaknesses", new ArrayList<>(weaknesses));
        response.put("totalQuestions", feedbackList.size());
        response.put("maxScore", feedbackList.size() * 10);

        return ResponseEntity.ok(response);
    }

    // @GetMapping("/redirect-after-login")
    // public void redirectAfterLogin(HttpServletResponse response) throws IOException {
    //     response.setHeader("Access-Control-Allow-Origin", "https://golden-swan-a56b79.netlify.app");
    //     response.setHeader("Access-Control-Allow-Credentials", "true");
    //     response.sendRedirect("https://golden-swan-a56b79.netlify.app/interview");
    // }

    @GetMapping("/redirect-after-login")
public void redirectAfterLogin(HttpServletResponse response, OAuth2AuthenticationToken token) throws IOException {
    String email = token.getPrincipal().getAttribute("email");
    response.sendRedirect("https://golden-swan-a56b79.netlify.app/interview?email=" + email);

    response.setHeader("Access-Control-Allow-Origin", "https://golden-swan-a56b79.netlify.app");
    response.setHeader("Access-Control-Allow-Credentials", "true");

    String html = "<!DOCTYPE html>" +
                  "<html><head><meta charset='UTF-8'><title>Redirecting...</title></head><body>" +
                  "<script>" +
                  "sessionStorage.setItem('userEmail', '" + email + "');" +
                  "window.location.href = 'https://golden-swan-a56b79.netlify.app/interview';" +
                  "</script>" +
                  "</body></html>";

    response.setContentType("text/html");
    response.getWriter().write(html);
}

}
