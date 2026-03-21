import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Zap, Menu, X } from 'lucide-react';
import { useState } from 'react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  return (
    <nav className="fixed top-0 w-full z-40 glass">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <Zap size={18} className="text-white" />
            </div>
            <span className="text-xl font-bold">ListingAI</span>
          </Link>

          <div className="hidden md:flex items-center gap-6">
            <a href="/#features" className="text-dark-200 hover:text-white transition">Features</a>
            <a href="/#pricing" className="text-dark-200 hover:text-white transition">Pricing</a>
            {user ? (
              <>
                <Link to="/dashboard" className="text-dark-200 hover:text-white transition">Dashboard</Link>
                <Link to="/dashboard/generate" className="btn-primary text-sm py-2 px-4">Generate Listing</Link>
                <button onClick={() => { logout(); navigate('/'); }} className="text-dark-300 hover:text-white text-sm">Logout</button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-dark-200 hover:text-white transition">Login</Link>
                <Link to="/signup" className="btn-primary text-sm py-2 px-4">Get Started Free</Link>
              </>
            )}
          </div>

          <button className="md:hidden text-dark-200" onClick={() => setOpen(!open)}>
            {open ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        {open && (
          <div className="md:hidden py-4 border-t border-dark-700 space-y-3">
            <a href="/#features" className="block text-dark-200 hover:text-white" onClick={() => setOpen(false)}>Features</a>
            <a href="/#pricing" className="block text-dark-200 hover:text-white" onClick={() => setOpen(false)}>Pricing</a>
            {user ? (
              <>
                <Link to="/dashboard" className="block text-dark-200 hover:text-white" onClick={() => setOpen(false)}>Dashboard</Link>
                <Link to="/dashboard/generate" className="block btn-primary text-sm py-2 px-4 text-center" onClick={() => setOpen(false)}>Generate Listing</Link>
              </>
            ) : (
              <>
                <Link to="/login" className="block text-dark-200 hover:text-white" onClick={() => setOpen(false)}>Login</Link>
                <Link to="/signup" className="block btn-primary text-sm py-2 px-4 text-center" onClick={() => setOpen(false)}>Get Started Free</Link>
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  );
}
