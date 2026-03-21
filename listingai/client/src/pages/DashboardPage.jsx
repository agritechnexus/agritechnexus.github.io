import { Link } from 'react-router-dom';
import { Wand2, Upload, History } from 'lucide-react';
import UsageStats from '../components/dashboard/UsageStats';

export default function DashboardPage() {
  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold mb-1">Dashboard</h1>
        <p className="text-dark-400 text-sm">Overview of your ListingAI usage</p>
      </div>

      <UsageStats />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Link to="/dashboard/generate" className="card group hover:border-primary/30 transition">
          <Wand2 size={24} className="text-primary mb-3" />
          <h3 className="font-semibold mb-1">Generate Listing</h3>
          <p className="text-sm text-dark-400">Create a new product listing with AI</p>
        </Link>
        <Link to="/dashboard/bulk" className="card group hover:border-primary/30 transition">
          <Upload size={24} className="text-blue-400 mb-3" />
          <h3 className="font-semibold mb-1">Bulk Upload</h3>
          <p className="text-sm text-dark-400">Upload CSV for batch generation</p>
        </Link>
        <Link to="/dashboard/history" className="card group hover:border-primary/30 transition">
          <History size={24} className="text-green-400 mb-3" />
          <h3 className="font-semibold mb-1">Listing History</h3>
          <p className="text-sm text-dark-400">View and manage past listings</p>
        </Link>
      </div>
    </div>
  );
}
