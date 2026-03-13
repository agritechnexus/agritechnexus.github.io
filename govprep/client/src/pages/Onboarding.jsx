import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export default function Onboarding() {
  const [step, setStep] = useState(1);
  const [exams, setExams] = useState([]);
  const [selectedExam, setSelectedExam] = useState('');
  const [examDate, setExamDate] = useState('');
  const [syllabus, setSyllabus] = useState(null);
  const [ratings, setRatings] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { refreshUser } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/exams').then(res => setExams(res.data)).catch(() => {});
  }, []);

  const handleExamSelect = async (exam) => {
    setSelectedExam(exam);
    try {
      const res = await api.get(`/exams/${encodeURIComponent(exam)}/syllabus`);
      setSyllabus(res.data);
      const defaultRatings = {};
      res.data.subjects.forEach(s => { defaultRatings[s.name] = 'medium'; });
      setRatings(defaultRatings);
    } catch {
      setError('Failed to load syllabus');
    }
  };

  const handleComplete = async () => {
    setLoading(true);
    setError('');
    try {
      const subjects = Object.entries(ratings).map(([name, selfRating]) => ({ name, selfRating }));
      await api.post('/onboarding', { targetExam: selectedExam, examDate, subjects });
      await refreshUser();
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.msg || 'Onboarding failed');
    } finally {
      setLoading(false);
    }
  };

  const minDate = new Date().toISOString().split('T')[0];

  return (
    <div className="min-h-screen px-6 py-8">
      <h1 className="text-2xl font-bold mb-1">Setup Your Prep</h1>
      <p className="text-gray-500 mb-6">Step {step} of 3</p>

      {/* Progress bar */}
      <div className="w-full bg-gray-200 rounded-full h-2 mb-6">
        <div className="bg-blue-600 h-2 rounded-full transition-all" style={{ width: `${(step / 3) * 100}%` }} />
      </div>

      {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm">{error}</div>}

      {/* Step 1: Pick exam */}
      {step === 1 && (
        <div>
          <h2 className="text-lg font-semibold mb-4">Which exam are you preparing for?</h2>
          <div className="space-y-3">
            {exams.map(exam => (
              <button
                key={exam}
                onClick={() => { handleExamSelect(exam); setStep(2); }}
                className="w-full text-left px-4 py-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 text-base font-medium transition-colors"
              >
                {exam}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Step 2: Exam date */}
      {step === 2 && (
        <div>
          <h2 className="text-lg font-semibold mb-4">When is your exam?</h2>
          <input
            type="date"
            min={minDate}
            value={examDate}
            onChange={e => setExamDate(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg text-base focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <div className="flex gap-3 mt-6">
            <button onClick={() => setStep(1)} className="flex-1 py-3 border border-gray-300 rounded-lg font-semibold">Back</button>
            <button
              onClick={() => examDate && setStep(3)}
              disabled={!examDate}
              className="flex-1 py-3 bg-blue-600 text-white rounded-lg font-semibold disabled:opacity-50"
            >
              Next
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Rate subjects */}
      {step === 3 && syllabus && (
        <div>
          <h2 className="text-lg font-semibold mb-4">Rate your subjects</h2>
          <div className="space-y-4">
            {syllabus.subjects.map(s => (
              <div key={s.name} className="border border-gray-200 rounded-lg p-4">
                <p className="font-medium mb-2">{s.name}</p>
                <div className="flex gap-2">
                  {['weak', 'medium', 'strong'].map(level => (
                    <button
                      key={level}
                      onClick={() => setRatings(r => ({ ...r, [s.name]: level }))}
                      className={`flex-1 py-2 rounded-lg text-sm font-medium capitalize transition-colors ${
                        ratings[s.name] === level
                          ? level === 'weak' ? 'bg-red-500 text-white'
                          : level === 'medium' ? 'bg-yellow-500 text-white'
                          : 'bg-green-500 text-white'
                          : 'bg-gray-100 text-gray-600'
                      }`}
                    >
                      {level}
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
          <div className="flex gap-3 mt-6">
            <button onClick={() => setStep(2)} className="flex-1 py-3 border border-gray-300 rounded-lg font-semibold">Back</button>
            <button
              onClick={handleComplete}
              disabled={loading}
              className="flex-1 py-3 bg-blue-600 text-white rounded-lg font-semibold disabled:opacity-50"
            >
              {loading ? 'Setting up...' : 'Start Prep!'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
