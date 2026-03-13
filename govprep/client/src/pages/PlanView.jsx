import { useState, useEffect } from 'react';
import api from '../api/axios';

export default function PlanView() {
  const [plan, setPlan] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/plan')
      .then(res => setPlan(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const formatDate = (dateStr) => {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { weekday: 'short', month: 'short', day: 'numeric' });
  };

  const isToday = (dateStr) => {
    const d = new Date(dateStr);
    const today = new Date();
    return d.toDateString() === today.toDateString();
  };

  const handleDayAction = async (dateStr, status) => {
    try {
      const date = new Date(dateStr).toISOString().split('T')[0];
      const res = await api.patch(`/plan/${date}`, { status });
      setPlan(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) return <div className="flex items-center justify-center h-screen">Loading...</div>;
  if (!plan) return <div className="flex items-center justify-center h-screen text-gray-500">No plan found</div>;

  // Show recent past + upcoming days
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const showDays = plan.days.filter(d => {
    const dd = new Date(d.date);
    dd.setHours(0, 0, 0, 0);
    const diffDays = (dd - today) / (1000 * 60 * 60 * 24);
    return diffDays >= -7; // show past 7 days + all future
  });

  return (
    <div className="px-6 py-6 pb-safe">
      <h1 className="text-xl font-bold mb-1">Study Plan</h1>
      <p className="text-gray-500 text-sm mb-6">{plan.days.length} days total</p>

      <div className="space-y-3">
        {showDays.map((day, i) => {
          const today_ = isToday(day.date);
          const statusColors = {
            done: 'border-green-300 bg-green-50',
            missed: 'border-red-300 bg-red-50',
            upcoming: today_ ? 'border-blue-400 bg-blue-50' : 'border-gray-200 bg-white',
          };

          return (
            <div key={i} className={`p-4 rounded-lg border-2 ${statusColors[day.status]}`}>
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center gap-2">
                  <span className="font-semibold text-sm">{formatDate(day.date)}</span>
                  {today_ && <span className="text-xs bg-blue-600 text-white px-2 py-0.5 rounded-full">TODAY</span>}
                </div>
                <span className={`text-sm font-medium ${
                  day.status === 'done' ? 'text-green-600' : day.status === 'missed' ? 'text-red-600' : 'text-gray-400'
                }`}>
                  {day.status === 'done' ? '✓ Done' : day.status === 'missed' ? '✗ Missed' : '○ Upcoming'}
                </span>
              </div>

              <div className="flex flex-wrap gap-2 mb-2">
                {day.subjects.map(s => (
                  <span key={s} className="text-sm px-2 py-1 bg-white rounded border border-gray-200">{s}</span>
                ))}
              </div>

              {today_ && day.status === 'upcoming' && (
                <div className="flex gap-2 mt-2">
                  <button
                    onClick={() => handleDayAction(day.date, 'done')}
                    className="flex-1 py-2 bg-green-600 text-white rounded-lg text-sm font-semibold"
                  >
                    Mark Done ✓
                  </button>
                  <button
                    onClick={() => handleDayAction(day.date, 'missed')}
                    className="flex-1 py-2 bg-red-100 text-red-600 rounded-lg text-sm font-semibold"
                  >
                    Skip
                  </button>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
