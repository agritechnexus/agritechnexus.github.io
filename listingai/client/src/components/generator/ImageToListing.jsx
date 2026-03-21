import { useState, useRef } from 'react';
import { ImageIcon, Upload } from 'lucide-react';
import Button from '../common/Button';
import MarketplaceSelector from './MarketplaceSelector';
import LanguageSelector from './LanguageSelector';
import api from '../../api/axios';
import toast from 'react-hot-toast';

export default function ImageToListing({ onResult }) {
  const [preview, setPreview] = useState(null);
  const [file, setFile] = useState(null);
  const [marketplace, setMarketplace] = useState('Amazon IN');
  const [language, setLanguage] = useState('English');
  const [loading, setLoading] = useState(false);
  const fileRef = useRef();

  const handleFile = (e) => {
    const f = e.target.files?.[0];
    if (f) {
      setFile(f);
      const reader = new FileReader();
      reader.onload = (ev) => setPreview(ev.target.result);
      reader.readAsDataURL(f);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return toast.error('Please select an image');

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('image', file);
      formData.append('marketplace', marketplace);
      formData.append('language', language);

      const res = await api.post('/listings/from-image', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      onResult?.(res.data);
      toast.success('Listing generated from image!');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Image processing failed');
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
        <input ref={fileRef} type="file" accept="image/*" className="hidden" onChange={handleFile} />
        {preview ? (
          <img src={preview} alt="Preview" className="max-h-48 mx-auto rounded-lg" />
        ) : (
          <>
            <ImageIcon size={32} className="text-dark-500 mx-auto mb-3" />
            <p className="text-dark-300 mb-1">Upload a product image</p>
            <p className="text-xs text-dark-500">AI will analyze the image and generate a listing</p>
          </>
        )}
      </div>

      <MarketplaceSelector selected={marketplace} onChange={setMarketplace} />
      <LanguageSelector value={language} onChange={setLanguage} />

      <Button type="submit" loading={loading} className="w-full">
        <Upload size={18} /> Generate from Image
      </Button>
    </form>
  );
}
