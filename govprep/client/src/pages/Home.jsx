import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import ProgressRing from '../components/ProgressRing';

export default function Home() {
  const { user, refreshUser, logout } = useAuth();
  const [plan, setPlan] = useState(null);
  const [todayPlan, setTodayPlan] = useState(null);
  const [progress, setProgress] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [planRes, progressRes] = await Promise.all([
        api.get('/plan'),
        api.get('/progress')
      ]);
      setPlan(planRes.data);
      setProgress(progressRes.data);

      // Find today's plan
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const todayEntry = planRes.data.days.find(d => {
        const dd = new Date(d.date);
        dd.setHours(0, 0, 0, 0);
        return dd.getTime() === today.getTime();
      });
      setTodayPlan(todayEntry || null);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const markDayDone = async () => {
    if (!todayPlan) return;
    try {
      const dateStr = new Date(todayPlan.date).toISOString().split('T')[0];
      await api.patch(`/plan/${dateStr}`, { status: 'done' });
      await refreshUser();
      loadData();
    } catch (err) {
      console.error(err);
    }
  };

  // Calculate readiness
  const calcReadiness = () => {
    if (!progress) return 0;
    const allTopics = progress.subjects.flatMap(s => s.topics);
    if (allTopics.length === 0) return 0;
    const weights = { not_started: 0, in_progress: 0.25, revised: 0.7, confident: 1 };
    const total = allTopics.reduce((sum, t) => sum + (weights[t.status] || 0), 0);
    return (total / allTopics.length) * 100;
  };

  // Days until exam
  const daysLeft = () => {
    if (!user?.examDate) return 0;
    const today = new Date();
    const exam = new Date(user.examDate);
    return Math.max(0, Math.ceil((exam - today) / (1000 * 60 * 60 * 24)));
  };

  if (loading) return <div className="flex items-center justify-center h-screen">Loading...</div>;

  const readiness = calcReadiness();

  return (
    <div className="px-6 py-6 pb-safe">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl font-bold">Hi, {user?.name?.split(' ')[0]}!</h1>
          <p className="text-gray-500 text-sm">{user?.targetExam}</p>
        </div>
        <button onClick={logout} className="text-sm text-gray-400 hover:text-gray-600">Logout</button>
      </div>

      {/* Exam countdown */}
      <div className="bg-blue-50 rounded-xl p-4 mb-6 text-center">
        <p className="text-4xl font-bold text-blue-600">{daysLeft()}</p>
        <p className="text-blue-600 text-sm font-medium">days left</p>
      </div>

      {/* Streak & XP */}
      <div className="flex gap-4 mb-6">
        <div className="flex-1 bg-orange-50 rounded-xl p-4 text-center">
          <p className="text-2xl font-bold text-orange-500">🔥 {user?.streak?.count || 0}</p>
          <p className="text-xs text-orange-600 font-medium">Day Streak</p>
        </div>
        <div className="flex-1 bg-purple-50 rounded-xl p-4 text-center">
          <p className="text-2xl font-bold text-purple-600">⚡ {user?.xp || 0}</p>
          <p className="text-xs text-purple-600 font-medium">XP Points</p>
        </div>
      </div>

      {/* Readiness Ring */}
      <div className="flex flex-col items-center mb-6">
        <ProgressRing percent={readiness} />
        <p className="text-sm text-gray-500 mt-2">Exam Readiness</p>
      </div>

      {/* Today's Focus */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold mb-3">Today's Focus</h2>
        {todayPlan ? (
          <div className="space-y-2">
            {todayPlan.subjects.map(subject => (
              <div key={subject} className={`flex items-center justify-between p-4 rounded-lg border ${
                todayPlan.status === 'done' ? 'bg-green-50 border-green-200' : 'bg-white border-gray-200'
              }`}>
                <span className="font-medium">{subject}</span>
                {todayPlan.status === 'done' && <span className="text-green-600 font-bold">✓</span>}
              </div>
            ))}
            {todayPlan.status !== 'done' && (
              <button
                onClick={markDayDone}
                className="w-full mt-3 py-3 bg-green-600 text-white rounded-lg font-semibold text-base hover:bg-green-700"
              >
                Mark All Done ✓
              </button>
            )}
            {todayPlan.status === 'done' && (
              <p className="text-center text-green-600 font-medium mt-2">Great job today! 🎉</p>
            )}
          </div>
        ) : (
          <p className="text-gray-400 text-center py-4">No tasks for today</p>
        )}
      </div>

      {/* Badges */}
      {user?.badges?.length > 0 && (
        <div>
          <h2 className="text-lg font-semibold mb-3">Badges</h2>
          <div className="flex flex-wrap gap-2">
            {user.badges.map(badge => (
              <span key={badge} className="px-3 py-1 bg-yellow-100 text-yellow-800 rounded-full text-sm font-medium">
                🏅 {badge}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
