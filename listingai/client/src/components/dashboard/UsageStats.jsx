import { useEffect, useState } from 'react';
import { Zap, TrendingUp, FileText, Clock } from 'lucide-react';
import api from '../../api/axios';

export default function UsageStats() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    api.get('/users/profile').then(res => setStats(res.data)).catch(() => {});
  }, []);

  if (!stats) return null;

  const { user, usage } = stats;
  const pct = usage.limit === 'unlimited' ? 10 : Math.round((usage.used / usage.limit) * 100);

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div className="card">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center">
            <Zap size={20} className="text-primary" />
          </div>
          <div>
            <p className="text-xs text-dark-400">Plan</p>
            <p className="font-semibold capitalize">{user.plan}</p>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-10 h-10 bg-blue-500/10 rounded-lg flex items-center justify-center">
            <FileText size={20} className="text-blue-400" />
          </div>
          <div>
            <p className="text-xs text-dark-400">Listings Used</p>
            <p className="font-semibold">{usage.used} / {usage.limit}</p>
          </div>
        </div>
        <div className="w-full bg-dark-700 rounded-full h-2">
          <div className="bg-primary rounded-full h-2 transition-all" style={{ width: `${Math.min(pct, 100)}%` }} />
        </div>
      </div>

      <div className="card">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-green-500/10 rounded-lg flex items-center justify-center">
            <TrendingUp size={20} className="text-green-400" />
          </div>
          <div>
            <p className="text-xs text-dark-400">Remaining</p>
            <p className="font-semibold">{usage.limit === 'unlimited' ? 'Unlimited' : usage.limit - usage.used}</p>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-purple-500/10 rounded-lg flex items-center justify-center">
            <Clock size={20} className="text-purple-400" />
          </div>
          <div>
            <p className="text-xs text-dark-400">Resets On</p>
            <p className="font-semibold">{new Date(usage.resetDate).toLocaleDateString('en-IN')}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
