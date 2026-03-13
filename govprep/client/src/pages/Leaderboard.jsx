import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export default function Leaderboard() {
  const { user } = useAuth();
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user?.targetExam) {
      api.get(`/leaderboard/${encodeURIComponent(user.targetExam)}`)
        .then(res => setLeaderboard(res.data))
        .catch(console.error)
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [user]);

  if (loading) return <div className="flex items-center justify-center h-screen">Loading...</div>;

  return (
    <div className="px-6 py-6 pb-safe">
      <h1 className="text-xl font-bold mb-1">Leaderboard</h1>
      <p className="text-gray-500 text-sm mb-6">{user?.targetExam} — Top 50</p>

      {leaderboard.length === 0 ? (
        <div className="text-center py-12 text-gray-400">
          <p className="text-4xl mb-2">🏆</p>
          <p>No users yet. Be the first!</p>
        </div>
      ) : (
        <div className="space-y-2">
          {leaderboard.map((entry) => (
            <div
              key={entry.rank}
              className={`flex items-center p-4 rounded-lg border-2 ${
                entry.isCurrentUser
                  ? 'border-blue-400 bg-blue-50'
                  : 'border-gray-100 bg-white'
              }`}
            >
              {/* Rank */}
              <div className="w-10 text-center">
                {entry.rank <= 3 ? (
                  <span className="text-xl">
                    {entry.rank === 1 ? '🥇' : entry.rank === 2 ? '🥈' : '🥉'}
                  </span>
                ) : (
                  <span className="text-lg font-bold text-gray-400">#{entry.rank}</span>
                )}
              </div>

              {/* Name */}
              <div className="flex-1 ml-3">
                <p className="font-semibold text-sm">
                  {entry.name}
                  {entry.isCurrentUser && <span className="text-blue-600 ml-1">(You)</span>}
                </p>
                <div className="flex gap-2 mt-1">
                  {entry.badges?.slice(0, 3).map(b => (
                    <span key={b} className="text-xs px-1.5 py-0.5 bg-yellow-100 text-yellow-700 rounded">
                      {b}
                    </span>
                  ))}
                </div>
              </div>

              {/* Stats */}
              <div className="text-right">
                <p className="font-bold text-blue-600">{entry.xp} XP</p>
                <p className="text-xs text-orange-500">🔥 {entry.streak}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
