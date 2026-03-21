import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import Sidebar from './components/layout/Sidebar';
import Loader from './components/common/Loader';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import DashboardPage from './pages/DashboardPage';
import GeneratorPage from './pages/GeneratorPage';
import BulkUploadPage from './pages/BulkUploadPage';
import HistoryPage from './pages/HistoryPage';
import PricingPage from './pages/PricingPage';
import SettingsPage from './pages/SettingsPage';

function ProtectedRoute() {
  const { user, loading } = useAuth();
  if (loading) return <div className="min-h-screen flex items-center justify-center"><Loader size="lg" /></div>;
  if (!user) return <Navigate to="/login" replace />;
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 pt-20 px-4 sm:px-6 lg:px-8 pb-12 max-w-5xl">
        <Outlet />
      </main>
    </div>
  );
}

function PublicLayout() {
  return (
    <>
      <Outlet />
      <Footer />
    </>
  );
}

export default function App() {
  return (
    <div className="min-h-screen bg-dark-950 text-white">
      <Navbar />
      <Routes>
        <Route element={<PublicLayout />}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
        </Route>
        <Route path="/dashboard" element={<ProtectedRoute />}>
          <Route index element={<DashboardPage />} />
          <Route path="generate" element={<GeneratorPage />} />
          <Route path="bulk" element={<BulkUploadPage />} />
          <Route path="history" element={<HistoryPage />} />
          <Route path="pricing" element={<PricingPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}
