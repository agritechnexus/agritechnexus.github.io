import { useState, useRef } from 'react';
import { Upload, FileSpreadsheet } from 'lucide-react';
import Button from '../common/Button';
import MarketplaceSelector from './MarketplaceSelector';
import LanguageSelector from './LanguageSelector';
import api from '../../api/axios';
import toast from 'react-hot-toast';

export default function BulkUploadForm() {
  const [file, setFile] = useState(null);
  const [marketplace, setMarketplace] = useState('Amazon IN');
  const [language, setLanguage] = useState('English');
  const [loading, setLoading] = useState(false);
  const fileRef = useRef();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return toast.error('Please select a CSV file');

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('marketplace', marketplace);
      formData.append('language', language);

      const res = await api.post('/bulk/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      toast.success(`Bulk job started! Processing ${res.data.totalProducts} products.`);
      setFile(null);
    } catch (err) {
      toast.error(err.response?.data?.error || 'Upload failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div
        className="border-2 border-dashed border-dark-600 rounded-xl p-8 text-center cursor-pointer hover:border-primary/50 transition"
        onClick={() => fileRef.current?.click()}
      >
        <input
          ref={fileRef}
          type="file"
          accept=".csv"
          className="hidden"
          onChange={(e) => setFile(e.target.files?.[0] || null)}
        />
        {file ? (
          <div className="flex items-center justify-center gap-3">
            <FileSpreadsheet size={24} className="text-primary" />
            <span className="text-dark-200">{file.name}</span>
          </div>
        ) : (
          <>
            <Upload size={32} className="text-dark-500 mx-auto mb-3" />
            <p className="text-dark-300 mb-1">Click to upload CSV file</p>
            <p className="text-xs text-dark-500">Required columns: productName, category, features (separated by ;)</p>
          </>
        )}
      </div>

      <MarketplaceSelector selected={marketplace} onChange={setMarketplace} />
      <LanguageSelector value={language} onChange={setLanguage} />

      <Button type="submit" loading={loading} className="w-full">
        <Upload size={18} /> Start Bulk Generation
      </Button>
    </form>
  );
}
