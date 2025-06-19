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
  const [showScorecard, setShowScorecard] = useState(false);
  //const [scoreData, setScoreData] = useState(null); // { score, strengths, weaknesses }
  const [scoreData, setScoreData] = useState({
  score: 0,
  strengths: [],
  weaknesses: []
});


  const backendBaseUrl = 'https://atharvpandey13-2006-github-io-interview-1-kq9g.onrender.com';

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
      const res = await fetch(`${backendBaseUrl}/api/interview/startInterview?role=${encodeURIComponent(role)}`, {
        method: 'GET',
        credentials: 'include'
      });
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
      if (questionIndex >= 5) {
        setShowScorecard(true);
        const res = await fetch(`${backendBaseUrl}/api/interview/score`, {
          credentials: 'include'
        });
        const data = await res.json();
        console.log("SCORE DATA RECEIVED:", data);
        setScoreData(data);
        return;
      }

      const res = await fetch(`${backendBaseUrl}/api/interview/nextQuestion?role=${encodeURIComponent(role)}&questionIndex=${questionIndex}`, {
        method: 'GET',
        credentials: 'include'
      });

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
        credentials: 'include',
        body: JSON.stringify({ answer, question, role })
      });
      if (!res.ok) throw new Error('Failed to submit answer');
      const data = await res.json(); // ✅ FIXED
  setFeedback(data.feedback);    // ✅ feedback paragraph only
  setScoreData({
    score: data.score,
    strengths: data.strengths,
    weaknesses: data.weaknesses
  });                            // ✅ full evaluation
} catch (e) {
  setError(e.message);
} finally {
  setLoading(false);
}

  //     const feedbackText = await res.text();
  //     setFeedback(feedbackText);
  //   } catch (e) {
  //     setError(e.message);
  //   } finally {
  //     setLoading(false);
  //   }
  // };

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
      ) : showScorecard && scoreData ? (
        <div className="mt-6 p-6 rounded-xl bg-gray-50 border-l-4 border-indigo-600 shadow">
          <h2 className="text-2xl font-bold text-indigo-700 mb-4">🏆 Interview Summary</h2>
          <p className="text-xl mb-2">💯 Score: <span className="font-bold">{scoreData.score}/{scoreData.maxScore}</span></p>

          <div className="mb-4">
            <h3 className="font-semibold text-green-700">✔️ Strengths</h3>
            <ul className="list-disc list-inside text-gray-700">
              {(scoreData?.strengths || []).map((s, i) => <li key={i}>{s}</li>)}

            </ul>
          </div>

          <div className="mb-4">
            <h3 className="font-semibold text-red-700">⚠️ Areas to Improve</h3>
            <ul className="list-disc list-inside text-gray-700">
              {(scoreData?.weaknesses || []).map((w, i) => <li key={i}>{w}</li>)}
            </ul>
          </div>

          <div className="flex gap-4 mt-6">
            <button className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700" onClick={() => window.location.reload()}>🔁 Replay Interview</button>
            <button className="bg-green-600 text-white px-5 py-2 rounded hover:bg-green-700" onClick={() => window.open(`${backendBaseUrl}/api/interview/downloadPdf`, '_blank')}>📥 Download PDF</button>
          </div>
        </div>
      ) : (
        <div className="space-y-6">
          <div className="bg-indigo-50 border-l-4 border-indigo-600 p-4 rounded">
            <h2 className="text-xl font-semibold text-indigo-700">Question:</h2>
            <p className="mt-2 text-gray-800">{question}</p>
          </div>

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

          {feedback && (
            <div className="p-4 bg-green-50 border-l-4 border-green-600 rounded mt-4">
              <h4 className="font-semibold text-green-700">AI Feedback:</h4>
              <p className="mt-2 whitespace-pre-line text-gray-700">{feedback}</p>
            </div>
          )}

          {error && <p className="text-red-600 font-semibold">{error}</p>}
        </div>
      )}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
