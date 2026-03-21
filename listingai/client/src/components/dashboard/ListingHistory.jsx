import { useState, useEffect } from 'react';
import { Trash2, Eye, ChevronLeft, ChevronRight } from 'lucide-react';
import { useListings } from '../../hooks/useListings';
import ListingOutput from '../generator/ListingOutput';
import Modal from '../common/Modal';
import toast from 'react-hot-toast';

export default function ListingHistory() {
  const { getHistory, deleteListing } = useListings();
  const [data, setData] = useState({ listings: [], total: 0, page: 1, pages: 1 });
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchHistory = async (page = 1) => {
    setLoading(true);
    try {
      const res = await getHistory(page);
      setData(res);
    } catch {
      toast.error('Failed to load history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchHistory(); }, []);

  const handleDelete = async (id) => {
    try {
      await deleteListing(id);
      toast.success('Listing deleted');
      fetchHistory(data.page);
    } catch {
      toast.error('Delete failed');
    }
  };

  if (loading) return <div className="text-center py-12 text-dark-400">Loading...</div>;
  if (!data.listings.length) return <div className="text-center py-12 text-dark-400">No listings yet. Generate your first one!</div>;

  return (
    <>
      <div className="space-y-3">
        {data.listings.map((listing) => (
          <div key={listing._id} className="card flex items-center justify-between">
            <div className="flex-1 min-w-0">
              <p className="font-medium truncate">{listing.productName}</p>
              <div className="flex items-center gap-3 mt-1">
                <span className="text-xs text-dark-400">{new Date(listing.createdAt).toLocaleDateString('en-IN')}</span>
                <span className="text-xs text-primary">{listing.generatedListings?.length || 0} marketplace(s)</span>
                {listing.generatedListings?.[0]?.seoScore && (
                  <span className="text-xs text-green-400">SEO: {listing.generatedListings[0].seoScore}</span>
                )}
              </div>
            </div>
            <div className="flex items-center gap-2 ml-4">
              <button onClick={() => setSelected(listing)} className="p-2 text-dark-400 hover:text-white transition">
                <Eye size={18} />
              </button>
              <button onClick={() => handleDelete(listing._id)} className="p-2 text-dark-400 hover:text-red-400 transition">
                <Trash2 size={18} />
              </button>
            </div>
          </div>
        ))}
      </div>

      {data.pages > 1 && (
        <div className="flex items-center justify-center gap-4 mt-6">
          <button disabled={data.page <= 1} onClick={() => fetchHistory(data.page - 1)} className="btn-secondary py-2 px-3 disabled:opacity-30">
            <ChevronLeft size={18} />
          </button>
          <span className="text-sm text-dark-400">Page {data.page} of {data.pages}</span>
          <button disabled={data.page >= data.pages} onClick={() => fetchHistory(data.page + 1)} className="btn-secondary py-2 px-3 disabled:opacity-30">
            <ChevronRight size={18} />
          </button>
        </div>
      )}

      <Modal isOpen={!!selected} onClose={() => setSelected(null)} title={selected?.productName}>
        {selected && <ListingOutput listing={selected} />}
      </Modal>
    </>
  );
}
