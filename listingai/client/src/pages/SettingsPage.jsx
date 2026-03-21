import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import api from '../api/axios';
import toast from 'react-hot-toast';

export default function SettingsPage() {
  const { user, setUser } = useAuth();
  const [name, setName] = useState(user?.name || '');
  const [loading, setLoading] = useState(false);

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await api.put('/users/profile', { name });
      setUser(res.data.user);
      toast.success('Profile updated');
    } catch {
      toast.error('Update failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold mb-1">Settings</h1>
        <p className="text-dark-400 text-sm">Manage your account settings</p>
      </div>

      <form onSubmit={handleSave} className="card space-y-4">
        <Input label="Name" value={name} onChange={e => setName(e.target.value)} />
        <Input label="Email" value={user?.email || ''} disabled />
        <Input label="Plan" value={user?.plan?.toUpperCase() || 'FREE'} disabled />
        <Button type="submit" loading={loading}>Save Changes</Button>
      </form>
    </div>
  );
}
