import { useLocation, useNavigate } from 'react-router-dom';

const tabs = [
  { path: '/', label: 'Home', icon: '🏠' },
  { path: '/plan', label: 'Plan', icon: '📅' },
  { path: '/progress', label: 'Progress', icon: '📊' },
  { path: '/leaderboard', label: 'Rank', icon: '🏆' },
];

export default function BottomNav() {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 max-w-lg mx-auto">
      <div className="flex justify-around items-center h-16">
        {tabs.map(tab => {
          const active = location.pathname === tab.path;
          return (
            <button
              key={tab.path}
              onClick={() => navigate(tab.path)}
              className={`flex flex-col items-center justify-center w-full h-full text-xs transition-colors ${
                active ? 'text-blue-600 font-semibold' : 'text-gray-500'
              }`}
            >
              <span className="text-xl mb-0.5">{tab.icon}</span>
              {tab.label}
            </button>
          );
        })}
      </div>
    </nav>
  );
}
