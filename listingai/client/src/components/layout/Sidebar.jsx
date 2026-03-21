import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Wand2, Upload, History, CreditCard, Settings, Zap } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const links = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Overview', end: true },
  { to: '/dashboard/generate', icon: Wand2, label: 'Generate' },
  { to: '/dashboard/bulk', icon: Upload, label: 'Bulk Upload' },
  { to: '/dashboard/history', icon: History, label: 'History' },
  { to: '/dashboard/pricing', icon: CreditCard, label: 'Plans' },
  { to: '/dashboard/settings', icon: Settings, label: 'Settings' },
];

export default function Sidebar() {
  const { user } = useAuth();

  return (
    <aside className="hidden lg:flex flex-col w-64 bg-dark-900 border-r border-dark-700 min-h-screen pt-20 px-4">
      <div className="mb-6 px-3">
        <div className="flex items-center gap-2 mb-1">
          <span className="text-sm font-medium text-dark-200">{user?.name}</span>
        </div>
        <span className="text-xs text-dark-400 capitalize">{user?.plan} Plan</span>
      </div>

      <nav className="space-y-1 flex-1">
        {links.map(({ to, icon: Icon, label, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all ${
                isActive ? 'bg-primary/10 text-primary font-medium' : 'text-dark-300 hover:text-white hover:bg-dark-800'
              }`
            }
          >
            <Icon size={18} />
            {label}
          </NavLink>
        ))}
      </nav>

      <div className="p-3 mb-4 card bg-gradient-to-br from-primary/10 to-primary/5 border-primary/20">
        <div className="flex items-center gap-2 mb-2">
          <Zap size={16} className="text-primary" />
          <span className="text-sm font-medium">Usage</span>
        </div>
        <p className="text-xs text-dark-300">
          {user?.usage?.listingsThisMonth || 0} listings this month
        </p>
      </div>
    </aside>
  );
}
