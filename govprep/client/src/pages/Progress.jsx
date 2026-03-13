import { useState, useEffect } from 'react';
import api from '../api/axios';

const statusLabels = {
  not_started: 'Not Started',
  in_progress: 'In Progress',
  revised: 'Revised',
  confident: 'Confident',
};

const statusColors = {
  not_started: 'bg-gray-200',
  in_progress: 'bg-blue-500',
  revised: 'bg-yellow-500',
  confident: 'bg-green-500',
};

const statusButtonColors = {
  not_started: 'bg-gray-100 text-gray-600',
  in_progress: 'bg-blue-100 text-blue-700',
  revised: 'bg-yellow-100 text-yellow-700',
  confident: 'bg-green-100 text-green-700',
};

export default function Progress() {
  const [progress, setProgress] = useState(null);
  const [expandedSubject, setExpandedSubject] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/progress')
      .then(res => setProgress(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const updateTopicStatus = async (subjectName, topicName, newStatus) => {
    try {
      const res = await api.patch(
        `/progress/${encodeURIComponent(subjectName)}/${encodeURIComponent(topicName)}`,
        { status: newStatus }
      );
      setProgress(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const getSubjectProgress = (subject) => {
    const total = subject.topics.length;
    if (total === 0) return 0;
    const weights = { not_started: 0, in_progress: 0.25, revised: 0.7, confident: 1 };
    const score = subject.topics.reduce((sum, t) => sum + (weights[t.status] || 0), 0);
    return Math.round((score / total) * 100);
  };

  const getOverallProgress = () => {
    if (!progress) return 0;
    const allTopics = progress.subjects.flatMap(s => s.topics);
    if (allTopics.length === 0) return 0;
    const weights = { not_started: 0, in_progress: 0.25, revised: 0.7, confident: 1 };
    const score = allTopics.reduce((sum, t) => sum + (weights[t.status] || 0), 0);
    return Math.round((score / allTopics.length) * 100);
  };

  const getNeglectedSubjects = () => {
    if (!progress) return [];
    const now = new Date();
    return progress.subjects.filter(s => {
      const lastTouched = s.topics.reduce((latest, t) => {
        const d = new Date(t.lastUpdated);
        return d > latest ? d : latest;
      }, new Date(0));
      const daysSince = Math.floor((now - lastTouched) / (1000 * 60 * 60 * 24));
      return daysSince > 7;
    }).map(s => s.name);
  };

  if (loading) return <div className="flex items-center justify-center h-screen">Loading...</div>;
  if (!progress) return <div className="flex items-center justify-center h-screen text-gray-500">No progress data</div>;

  const neglected = getNeglectedSubjects();
  const overall = getOverallProgress();
  const allTopics = progress.subjects.flatMap(s => s.topics);
  const untouched = allTopics.filter(t => t.status === 'not_started').length;
  const untouchedPercent = allTopics.length > 0 ? Math.round((untouched / allTopics.length) * 100) : 0;

  return (
    <div className="px-6 py-6 pb-safe">
      <h1 className="text-xl font-bold mb-1">Progress</h1>
      <p className="text-gray-500 text-sm mb-4">Overall: {overall}% ready</p>

      {/* Alerts */}
      {untouchedPercent > 30 && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
          <p className="text-red-700 text-sm font-medium">
            ⚠️ {untouchedPercent}% syllabus untouched
          </p>
        </div>
      )}

      {neglected.length > 0 && (
        <div className="bg-orange-50 border border-orange-200 rounded-lg p-3 mb-4">
          <p className="text-orange-700 text-sm font-medium">
            ⚠️ Neglected: {neglected.join(', ')}
          </p>
        </div>
      )}

      {/* Subject list */}
      <div className="space-y-3">
        {progress.subjects.map(subject => {
          const pct = getSubjectProgress(subject);
          const isExpanded = expandedSubject === subject.name;

          return (
            <div key={subject.name} className="border border-gray-200 rounded-lg overflow-hidden">
              <button
                onClick={() => setExpandedSubject(isExpanded ? null : subject.name)}
                className="w-full p-4 text-left flex items-center justify-between"
              >
                <div className="flex-1 mr-4">
                  <p className="font-medium">{subject.name}</p>
                  <div className="mt-2 w-full bg-gray-200 rounded-full h-2">
                    <div
                      className={`h-2 rounded-full transition-all ${pct >= 70 ? 'bg-green-500' : pct >= 40 ? 'bg-yellow-500' : 'bg-red-500'}`}
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                </div>
                <span className="text-sm font-semibold text-gray-600">{pct}%</span>
              </button>

              {isExpanded && (
                <div className="border-t border-gray-100 p-4 space-y-3">
                  {subject.topics.map(topic => (
                    <div key={topic.name} className="flex items-center justify-between">
                      <span className="text-sm flex-1">{topic.name}</span>
                      <select
                        value={topic.status}
                        onChange={e => updateTopicStatus(subject.name, topic.name, e.target.value)}
                        className={`text-xs px-2 py-1 rounded-lg font-medium ${statusButtonColors[topic.status]} border-0 cursor-pointer`}
                      >
                        <option value="not_started">Not Started</option>
                        <option value="in_progress">In Progress</option>
                        <option value="revised">Revised</option>
                        <option value="confident">Confident</option>
                      </select>
                    </div>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
