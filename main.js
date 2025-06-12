const { useState, useEffect, useRef } = React;

function App() {
  const [role, setRole] = useState('');
  const [interviewStarted, setInterviewStarted] = useState(false);
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [isRecording, setIsRecording] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [questionIndex, setQuestionIndex] = useState(0);
  const recognitionRef = useRef(null);

  const backendBaseUrl = 'https://atharvpandey13-2006-github-io-interview-z01i.onrender.com';

  useEffect(() => {
    if (!('webkitSpeechRecognition' in window || 'SpeechRecognition' in window)) {
      setError('Speech Recognition API not supported in this browser.');
      return;
    }

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = true;
    recognition.lang = 'en-US';

    recognition.onstart = () => setIsRecording(true);
    recognition.onend = () => setIsRecording(false);
    recognition.onerror = (event) => setError("Speech Recognition error: " + event.error);
    recognition.onresult = (event) => {
      let finalTranscript = '';
      for (let i = event.resultIndex; i < event.results.length; ++i) {
        if (event.results[i].isFinal) {
          finalTranscript += event.results[i][0].transcript;
        }
      }
      setAnswer(finalTranscript);
    };

    recognitionRef.current = recognition;
  }, []);

  const startInterview = async () => {
    setError(null);
    setQuestionIndex(1);
    if (!role) {
      setError('Please select a role before starting the interview.');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${backendBaseUrl}/api/interview/startInterview?role=${encodeURIComponent(role)}`);
      if (!res.ok) throw new Error('Failed to start interview');
      const questionText = await res.text();
      setQuestion(questionText);
      setInterviewStarted(true);
      setFeedback(null);
      setAnswer('');
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const getNextQuestion = async () => {
    setError(null);
    setFeedback(null);
    setAnswer('');
    setLoading(true);
    try {
      const res = await fetch(`${backendBaseUrl}/api/interview/nextQuestion?role=${encodeURIComponent(role)}&questionIndex=${questionIndex}`);
      if (!res.ok) throw new Error('Failed to fetch next question');
      const nextQ = await res.text();
      setQuestion(nextQ);
      setQuestionIndex(prev => prev + 1);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const submitAnswer = async () => {
    setError(null);
    if (!answer.trim()) {
      setError('Please provide an answer before submitting.');
      return;
    }
    setLoading(true);
  try {
    const res = await fetch(`${backendBaseUrl}/api/interview/submitAnswer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include', // 🔥 Include session cookie
      body: JSON.stringify({ answer, question, role })
    });
    if (!res.ok) throw new Error('Failed to submit answer');
    const feedbackText = await res.text();
    setFeedback(feedbackText);
  } catch (e) {
    setError(e.message);
  } finally {
    setLoading(false);
  }
};

   
  const toggleRecording = () => {
    setError(null);
    if (!recognitionRef.current) return;
    if (isRecording) {
      recognitionRef.current.stop();
    } else {
      setAnswer('');
      recognitionRef.current.start();
    }
  };

  return (
    <div className="bg-white p-6 rounded-xl shadow-2xl w-full max-w-4xl mx-auto">
      <h1 className="text-4xl font-extrabold mb-8 text-center text-indigo-700">🎙️ AI Interview Simulator</h1>

      {!interviewStarted ? (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
          {['Frontend Developer', 'Backend Developer', 'Software Engineer'].map((r) => (
            <button
              key={r}
              onClick={() => setRole(r)}
              className={`p-6 rounded-lg border-2 ${role === r ? 'border-indigo-600 bg-indigo-50' : 'border-gray-200'} hover:shadow-md transition`}
            >
              <h2 className="text-lg font-semibold">{r}</h2>
              <p className="text-sm mt-2 text-gray-500">Simulate questions for {r}</p>
            </button>
          ))}

          <button
            onClick={startInterview}
            className="col-span-full bg-indigo-600 hover:bg-indigo-700 text-white py-3 px-6 rounded font-semibold mt-6"
            disabled={loading}
          >
            {loading ? 'Loading...' : '🚀 Start Interview'}
          </button>

          {error && <p className="col-span-full mt-2 text-red-600 font-semibold">{error}</p>}
        </div>
      ) : questionIndex > 5 ? (
        <div className="text-center space-y-6">
          <h2 className="text-3xl font-bold text-indigo-700">🏁 Interview Completed</h2>
          <div className="bg-indigo-100 p-6 rounded-xl shadow-md max-w-md mx-auto space-y-4">
            <p className="text-lg"><strong>Role:</strong> {role}</p>
            <p className="text-lg"><strong>Total Questions:</strong> {questionIndex}</p>
            <p className="text-lg"><strong>Questions Answered:</strong> {questionIndex}</p>
            <p className="text-lg"><strong>Mock Score:</strong> <span className="text-green-600 font-semibold text-xl">8.5 / 10</span></p>
          </div>

          <div className="flex flex-col sm:flex-row justify-center gap-4">
            <button
              className="bg-yellow-500 hover:bg-yellow-600 text-white py-2 px-5 rounded font-semibold"
              onClick={() => alert("Coming soon: PDF Download")}
            >
              📄 Download Feedback
            </button>

            <button
              className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-5 rounded font-semibold"
              onClick={() => {
                setInterviewStarted(false);
                setQuestion('');
                setFeedback(null);
                setAnswer('');
                setQuestionIndex(0);
              }}
            >
              🔄 Restart Interview
            </button>
          </div>
        </div>
      ) : (
        <div className="space-y-6">
          {/* Chat UI */}
          <div className="space-y-4">
            <div className="flex items-start gap-2">
              <img src="https://cdn-icons-png.flaticon.com/512/4712/4712109.png" className="w-10 h-10 rounded-full" />
              <div className="bg-indigo-100 p-3 rounded-xl max-w-xl shadow">
                <p className="text-gray-800">{question}</p>
              </div>
            </div>

            {answer && (
              <div className="flex items-start justify-end gap-2">
                <div className="bg-green-100 p-3 rounded-xl max-w-xl shadow">
                  <p className="text-gray-800 whitespace-pre-line">{answer}</p>
                </div>
                <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" className="w-10 h-10 rounded-full" />
              </div>
            )}

            {feedback && (
              <div className="flex items-start gap-2">
                <img src="https://cdn-icons-png.flaticon.com/512/4712/4712109.png" className="w-10 h-10 rounded-full" />
                <div className="bg-yellow-100 p-3 rounded-xl max-w-xl shadow">
                  <p className="text-gray-800 whitespace-pre-line">{feedback}</p>
                </div>
              </div>
            )}
          </div>

          {/* Textarea + buttons */}
          <div>
            <label className="block mb-1 font-semibold text-gray-700">Your Answer:</label>
            <textarea
              rows="5"
              className="w-full border border-gray-300 rounded p-2"
              value={answer}
              onChange={e => setAnswer(e.target.value)}
              placeholder="Speak or type your answer here..."
            />
          </div>

          <div className="flex flex-wrap gap-4">
            <button
              onClick={toggleRecording}
              className={`px-5 py-2 rounded font-semibold ${isRecording ? 'bg-red-600' : 'bg-green-600'} text-white hover:opacity-90`}
            >
              🎤 {isRecording ? 'Stop Recording' : 'Start Recording'}
            </button>

            <button
              onClick={submitAnswer}
              className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-5 rounded font-semibold"
              disabled={loading}
            >
              {loading ? 'Submitting...' : '📤 Submit Answer'}
            </button>

            <button
              onClick={getNextQuestion}
              className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 px-5 rounded font-semibold"
              disabled={loading}
            >
              {loading ? 'Loading...' : '➡️ Next Question'}
            </button>
          </div>

          {error && <p className="text-red-600 font-semibold">{error}</p>}
        </div>
      )}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
