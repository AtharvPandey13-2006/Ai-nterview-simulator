🚀 AI-Powered Interview Simulator 🎤
An interactive, AI-driven mock interview web app that helps you practice DSA and behavioral questions in real time — with instant feedback powered by GPT.


🔍 Overview
This project simulates real interview environments using AI and voice input.
Users choose a role (e.g., Frontend Developer), and the app:

Asks AI-generated DSA & behavioral questions

Lets users answer via voice

Uses speech-to-text

Provides instant feedback and suggestions

🧠 Key Features
✅ Role Selection: Frontend, Backend, Full-Stack, etc.
✅ Dynamic Questions: Fetched in real-time from OpenAI
✅ Voice-Based Interaction: Speak your answers
✅ AI Evaluation: Instant feedback based on your response
✅ Suggestions: How to improve your answers
✅ Tech Interview Simulator: Real-world practice in a browser

🖼️ Demo
🔗 Live Preview: https://atharvpandey13-2006.github.io/AtharvPandey13-2006.github.io-interview/

🛠️ Tech Stack
Frontend	Backend	AI Services	Others
React.js	Spring Boot	OpenAI GPT API	GitHub Pages
HTML, CSS	REST API	Web Speech API (STT)	Render
JavaScript	MongoDB (Optional)	—	Docker

🧱 Project Structure

.
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── backend/
│   ├── InterviewController.java
│   ├── InterviewService.java
│   └── application.properties
└── README.md


🧪 How to Run Locally
🔧 Backend (Spring Boot)

cd backend
./mvnw clean install
./mvnw spring-boot:run

Make sure to:

Add OpenAI API key in application.properties

Allow CORS in your controller:

@CrossOrigin(origins = "https://atharvpandey13-2006.github.io")

🌐 Frontend (Static HTML)

cd frontend

# Open index.html directly or serve using Live Server
📦 APIs
GET /api/interview/startInterview?role=frontend
Input: Role (e.g., frontend)

Output: AI-generated question

POST /api/interview/submitAnswer
Input: Role + Answer

Output: Evaluation + Score + Suggestions

📌 Future Improvements
✅ Show interview score/progress bar

✅ Export feedback as PDF

✅ Add multiple rounds with increasing difficulty

✅ User authentication (JWT)

✅ Save past sessions

🧑‍💻 Built By
Atharv Pandey
Computer Science Student | Backend Developer | AI Enthusiast
🔗 LinkedIn -> https://www.linkedin.com/in/atharv-pandey-336372284/
🌐 Portfolio -> https://atharvpandey13-2006.github.io/Portfolio/

⭐️ Show Your Support
If you like this project:

🌟 Star this repo
🍴 Fork it
🐛 Raise issues or PRs
