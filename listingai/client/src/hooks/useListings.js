import { useState } from 'react';
import api from '../api/axios';

export function useListings() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const generateListing = async (data) => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.post('/listings/generate', data);
      return res.data.listing;
    } catch (err) {
      setError(err.response?.data?.error || 'Generation failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const generateMulti = async (data) => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.post('/listings/generate-multi', data);
      return res.data.listing;
    } catch (err) {
      setError(err.response?.data?.error || 'Generation failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const generateDemo = async (data) => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.post('/listings/demo', data);
      return res.data.listing;
    } catch (err) {
      setError(err.response?.data?.error || 'Generation failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const getHistory = async (page = 1) => {
    const res = await api.get(`/listings/history?page=${page}`);
    return res.data;
  };

  const deleteListing = async (id) => {
    await api.delete(`/listings/${id}`);
  };

  return { generateListing, generateMulti, generateDemo, getHistory, deleteListing, loading, error };
}
