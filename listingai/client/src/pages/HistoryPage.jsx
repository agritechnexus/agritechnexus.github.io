import ListingHistory from '../components/dashboard/ListingHistory';

export default function HistoryPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold mb-1">Listing History</h1>
        <p className="text-dark-400 text-sm">View and manage your generated listings</p>
      </div>
      <ListingHistory />
    </div>
  );
}
