import BulkUploadForm from '../components/generator/BulkUploadForm';
import { useAuth } from '../hooks/useAuth';
import { Link } from 'react-router-dom';

export default function BulkUploadPage() {
  const { user } = useAuth();

  if (!['pro', 'agency'].includes(user?.plan)) {
    return (
      <div className="text-center py-16">
        <h2 className="text-xl font-bold mb-2">Bulk Upload — Pro Feature</h2>
        <p className="text-dark-400 mb-6">Upgrade to Pro or Agency plan to access bulk CSV upload.</p>
        <Link to="/dashboard/pricing" className="btn-primary">View Plans</Link>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold mb-1">Bulk Upload</h1>
        <p className="text-dark-400 text-sm">Upload a CSV file to generate listings in bulk</p>
      </div>
      <div className="card">
        <BulkUploadForm />
      </div>
    </div>
  );
}
