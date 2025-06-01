package com.example.interviewsimulator.controller;

import com.example.interviewsimulator.model.AnswerRequest;
import com.example.interviewsimulator.model.QuestionRequest;
import com.example.interviewsimulator.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "http://127.0.0.1:5500") // allow your frontend origin
public class InterviewController {

    @Autowired
    private GeminiService geminiService;

    // POST endpoint: Used when sending custom questions from the frontend
    @PostMapping("/ask")
    public String askAI(@RequestBody QuestionRequest request) {
        return geminiService.askGemini(request.getQuestion());
    }

    // GET endpoint: Used when starting the interview for a specific role
    @GetMapping("/startInterview")
    public String startInterview(@RequestParam String role) {
        String prompt = "Start a mock interview for the role of a " + role + ". Ask a question.ONLY QUESTION NOT A SINGLE EXTRA WORD";
        return geminiService.askGemini(prompt);
    }

    @PostMapping(value = "/submitAnswer", produces = "text/html")
public String submitAnswer(@RequestBody AnswerRequest request) {
    String prompt = "You are acting as an AI interviewer for the role of " + request.getRole() + ".\n"
            + "Here is the question I asked: \"" + request.getQuestion() + "\"\n"
            + "Here is the candidate's answer: \"" + request.getAnswer() + "\"\n"
            + "Please evaluate this answer and provide constructive feedback, including what was good and what could be improved. "
            + "ONLY EVALUATION AND FEEDBACK. GIVE IN TWO SEPARATE BLOCKS: "
            + "**Evaluation:** followed by feedback, and then **Where Can Be Improved:** followed by suggestions. Don't use * in answer";

    String rawResponse = geminiService.askGemini(prompt);

    // Wrap the raw text response in basic HTML
    String htmlResponse = rawResponse
            .replace("**Evaluation:**", "Evaluation")
            .replace("**Where Can Be Improved:**", "Where Can Be Improved")
            .replace("* **", " ")
            ;

    return htmlResponse;
}

//     @PostMapping("/submitAnswer")
//     public String submitAnswer(@RequestBody AnswerRequest request) {
//     String prompt = "You are acting as an AI interviewer for the role of " + request.getRole() + ".\n"
//             + "Here is the question I asked: \"" + request.getQuestion() + "\"\n"
//             + "Here is the candidate's answer: \"" + request.getAnswer() + "\"\n"
//             + "Please evaluate this answer and provide constructive feedback, including what was good and what could be improved.ONLY EVALUATION AND FEEDBACK NOT A SINGLE EXTRA WORD....GIVE IN BLOCKS AND THESE SHOULD BE TWO BLOCK EVALUATION AND WHERE CAN BE IMPROVED ";

//     return geminiService.askGemini(prompt);
// }


}
