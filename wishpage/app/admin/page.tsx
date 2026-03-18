"use client";

import { useState, useMemo } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  LayoutDashboard,
  Package,
  Palette,
  Settings,
  IndianRupee,
  ShoppingCart,
  Globe,
  TrendingUp,
  User,
  Search,
  Menu,
  X,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";

type Tab = "overview" | "orders";

const stats = [
  { label: "Total Revenue", value: "₹45,600", change: "+12%", icon: IndianRupee },
  { label: "Orders Today", value: "8", change: "+3", icon: ShoppingCart },
  { label: "Active Sites", value: "156", change: "+5", icon: Globe },
  { label: "Conversion Rate", value: "3.2%", change: "+0.4%", icon: TrendingUp },
];

const revenueData = [
  { day: "Mon", amount: 4200 },
  { day: "Tue", amount: 3800 },
  { day: "Wed", amount: 5100 },
  { day: "Thu", amount: 4600 },
  { day: "Fri", amount: 6200 },
  { day: "Sat", amount: 7800 },
  { day: "Sun", amount: 5400 },
];

const orders = [
  { id: "WP-1001", customer: "Ravi Kumar", email: "ravi@email.com", occasion: "Birthday", plan: "Premium", amount: 499, status: "Delivered", createdAt: "2 hours ago" },
  { id: "WP-1002", customer: "Priya Sharma", email: "priya@email.com", occasion: "Wedding", plan: "Deluxe", amount: 999, status: "Generating", createdAt: "30 min ago" },
  { id: "WP-1003", customer: "Arjun Reddy", email: "arjun@email.com", occasion: "Anniversary", plan: "Premium", amount: 499, status: "Delivered", createdAt: "5 hours ago" },
  { id: "WP-1004", customer: "Sara Ahmed", email: "sara@email.com", occasion: "Birthday", plan: "Basic", amount: 199, status: "Paid", createdAt: "1 hour ago" },
  { id: "WP-1005", customer: "Neha Gupta", email: "neha@email.com", occasion: "Baby Shower", plan: "Deluxe", amount: 999, status: "Delivered", createdAt: "1 day ago" },
  { id: "WP-1006", customer: "Amir Khan", email: "amir@email.com", occasion: "Eid", plan: "Premium", amount: 499, status: "Deploying", createdAt: "15 min ago" },
  { id: "WP-1007", customer: "Sneha Patel", email: "sneha@email.com", occasion: "Corporate", plan: "Basic", amount: 199, status: "Failed", createdAt: "3 hours ago" },
  { id: "WP-1008", customer: "Vikram Singh", email: "vikram@email.com", occasion: "Birthday", plan: "Premium", amount: 499, status: "Delivered", createdAt: "6 hours ago" },
  { id: "WP-1009", customer: "Pooja Devi", email: "pooja@email.com", occasion: "Wedding", plan: "Deluxe", amount: 999, status: "Delivered", createdAt: "1 day ago" },
  { id: "WP-1010", customer: "Rahul Joshi", email: "rahul@email.com", occasion: "Graduation", plan: "Basic", amount: 199, status: "Delivered", createdAt: "2 days ago" },
];

const statusColors: Record<string, string> = {
  Delivered: "bg-emerald/10 text-emerald",
  Generating: "bg-blue-50 text-blue-600",
  Deploying: "bg-yellow-50 text-yellow-600",
  Paid: "bg-purple-50 text-purple-600",
  Failed: "bg-red-50 text-red-600",
};

const planColors: Record<string, string> = {
  Basic: "bg-gray-100 text-gray-600",
  Premium: "bg-emerald/10 text-emerald",
  Deluxe: "bg-gold/10 text-gold",
};

const ITEMS_PER_PAGE = 10;

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState<Tab>("overview");
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [occasionFilter, setOccasionFilter] = useState("all");
  const [planFilter, setPlanFilter] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const filteredOrders = useMemo(() => {
    return orders.filter((order) => {
      const query = searchQuery.toLowerCase();
      const matchesSearch =
        !query ||
        order.customer.toLowerCase().includes(query) ||
        order.email.toLowerCase().includes(query) ||
        order.id.toLowerCase().includes(query);
      const matchesStatus = statusFilter === "all" || order.status === statusFilter;
      const matchesOccasion = occasionFilter === "all" || order.occasion === occasionFilter;
      const matchesPlan = planFilter === "all" || order.plan === planFilter;
      return matchesSearch && matchesStatus && matchesOccasion && matchesPlan;
    });
  }, [searchQuery, statusFilter, occasionFilter, planFilter]);

  const totalPages = Math.max(1, Math.ceil(filteredOrders.length / ITEMS_PER_PAGE));
  const paginatedOrders = filteredOrders.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE
  );

  const navItems = [
    { icon: LayoutDashboard, label: "Overview", tab: "overview" as Tab, disabled: false },
    { icon: Package, label: "Orders", tab: "orders" as Tab, disabled: false },
    { icon: Palette, label: "Templates", tab: null, disabled: true },
    { icon: Settings, label: "Settings", tab: null, disabled: true },
  ];

  const maxRevenue = Math.max(...revenueData.map((d) => d.amount));

  const handleTabChange = (tab: Tab) => {
    setActiveTab(tab);
    setSidebarOpen(false);
  };

  const SidebarContent = () => (
    <div className="flex flex-col h-full py-8 px-6">
      <div className="flex items-center mb-10">
        <span className="font-heading text-emerald font-bold text-xl">WishPage</span>
        <span className="text-xs bg-emerald/20 text-emerald-glow rounded px-2 py-0.5 ml-2">
          Admin
        </span>
      </div>

      <nav className="flex flex-col gap-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = !item.disabled && activeTab === item.tab;
          return (
            <button
              key={item.label}
              onClick={() => item.tab && !item.disabled && handleTabChange(item.tab)}
              disabled={item.disabled}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all cursor-pointer text-left ${
                item.disabled
                  ? "text-white/20 cursor-not-allowed"
                  : isActive
                  ? "bg-emerald/10 text-emerald-glow"
                  : "text-white/50 hover:text-white/80 hover:bg-white/5"
              }`}
            >
              <Icon className="w-5 h-5" />
              {item.label}
            </button>
          );
        })}
      </nav>

      <div className="mt-auto">
        <span className="text-white/20 text-xs">v1.0.0</span>
      </div>
    </div>
  );

  const OrdersTable = ({ data, showActions = false }: { data: typeof orders; showActions?: boolean }) => (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="bg-gray-50">
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Order ID
            </th>
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Customer
            </th>
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Occasion
            </th>
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Plan
            </th>
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Amount
            </th>
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Status
            </th>
            <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
              Time
            </th>
            {showActions && (
              <th className="px-6 py-3 text-left text-xs uppercase text-text-muted tracking-wider font-medium">
                Actions
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {data.map((order) => (
            <tr
              key={order.id}
              className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors"
            >
              <td className="px-6 py-4 text-sm font-medium text-text">{order.id}</td>
              <td className="px-6 py-4">
                <div className="text-sm font-medium text-text">{order.customer}</div>
                <div className="text-xs text-text-muted">{order.email}</div>
              </td>
              <td className="px-6 py-4 text-sm text-text">{order.occasion}</td>
              <td className="px-6 py-4">
                <span
                  className={`text-xs font-semibold px-2.5 py-1 rounded-full ${
                    planColors[order.plan] || "bg-gray-100 text-gray-600"
                  }`}
                >
                  {order.plan}
                </span>
              </td>
              <td className="px-6 py-4 text-sm font-mono text-text">₹{order.amount}</td>
              <td className="px-6 py-4">
                <span
                  className={`text-xs font-semibold px-2.5 py-1 rounded-full ${
                    statusColors[order.status] || "bg-gray-100 text-gray-600"
                  }`}
                >
                  {order.status}
                </span>
              </td>
              <td className="px-6 py-4 text-sm text-text-muted">{order.createdAt}</td>
              {showActions && (
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    <button className="text-emerald text-sm hover:underline">View</button>
                    {order.status === "Failed" && (
                      <button className="text-blue-600 text-sm hover:underline">
                        Regenerate
                      </button>
                    )}
                  </div>
                </td>
              )}
            </tr>
          ))}
          {data.length === 0 && (
            <tr>
              <td
                colSpan={showActions ? 8 : 7}
                className="px-6 py-12 text-center text-text-muted text-sm"
              >
                No orders found matching your filters.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );

  return (
    <div className="fixed inset-0 z-40 flex bg-cream min-h-screen">
      {/* Mobile sidebar overlay */}
      <AnimatePresence>
        {sidebarOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 z-50 md:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}
      </AnimatePresence>

      {/* Sidebar - desktop */}
      <aside className="hidden md:block w-64 bg-dark min-h-screen fixed left-0 top-0 z-50">
        <SidebarContent />
      </aside>

      {/* Sidebar - mobile */}
      <AnimatePresence>
        {sidebarOpen && (
          <motion.aside
            initial={{ x: -256 }}
            animate={{ x: 0 }}
            exit={{ x: -256 }}
            transition={{ type: "spring", damping: 25, stiffness: 300 }}
            className="fixed left-0 top-0 w-64 bg-dark min-h-screen z-50 md:hidden"
          >
            <button
              onClick={() => setSidebarOpen(false)}
              className="absolute top-4 right-4 text-white/50 hover:text-white"
            >
              <X className="w-5 h-5" />
            </button>
            <SidebarContent />
          </motion.aside>
        )}
      </AnimatePresence>

      {/* Main Content */}
      <main className="md:ml-64 flex-1 min-h-screen bg-cream p-4 md:p-8 w-full overflow-y-auto">
        {/* Mobile top bar */}
        <div className="flex md:hidden items-center mb-6">
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-2 rounded-lg hover:bg-white/80 transition-colors"
          >
            <Menu className="w-6 h-6 text-text" />
          </button>
          <span className="font-heading text-emerald font-bold text-lg ml-3">WishPage</span>
          <span className="text-xs bg-emerald/20 text-emerald-glow rounded px-2 py-0.5 ml-2">
            Admin
          </span>
        </div>

        {/* Top bar */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <p className="text-text-muted text-sm">Welcome back</p>
            <h1 className="font-heading text-2xl font-bold text-text">Dashboard</h1>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-sm text-text-muted hidden sm:block">Admin</span>
            <div className="w-10 h-10 rounded-full bg-emerald/10 flex items-center justify-center">
              <User className="w-5 h-5 text-emerald" />
            </div>
          </div>
        </div>

        <AnimatePresence mode="wait">
          {activeTab === "overview" && (
            <motion.div
              key="overview"
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -12 }}
              transition={{ duration: 0.25 }}
            >
              {/* Stats Row */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {stats.map((stat, i) => {
                  const Icon = stat.icon;
                  return (
                    <motion.div
                      key={stat.label}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.3, delay: i * 0.08 }}
                      className="bg-white rounded-[20px] border border-gray-100 shadow-card p-6"
                    >
                      <div className="flex justify-between items-start">
                        <div className="w-10 h-10 rounded-full bg-emerald/10 flex items-center justify-center">
                          <Icon className="w-5 h-5 text-emerald" />
                        </div>
                        <span className="text-xs font-semibold px-2 py-1 rounded-full bg-emerald-glow/10 text-emerald-glow">
                          {stat.change}
                        </span>
                      </div>
                      <div className="font-heading text-2xl font-bold text-text mt-3">
                        {stat.value}
                      </div>
                      <div className="text-text-muted text-sm mt-1">{stat.label}</div>
                    </motion.div>
                  );
                })}
              </div>

              {/* Revenue Chart */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, delay: 0.35 }}
                className="bg-white rounded-[20px] border border-gray-100 shadow-card p-6 mb-8"
              >
                <h2 className="font-semibold text-lg text-text mb-6">Revenue (Last 7 Days)</h2>
                <svg viewBox="0 0 700 240" className="w-full" role="img" aria-label="Revenue bar chart">
                  {revenueData.map((d, i) => {
                    const barHeight = (d.amount / maxRevenue) * 160;
                    const x = i * 100 + 20;
                    const y = 200 - barHeight;
                    return (
                      <g key={d.day}>
                        <rect
                          x={x}
                          y={y}
                          width={60}
                          height={barHeight}
                          rx={8}
                          fill="#0D6E4F"
                          className="transition-all duration-200 hover:fill-[#2A8F6A] cursor-pointer"
                        />
                        <text
                          x={x + 30}
                          y={220}
                          textAnchor="middle"
                          className="fill-text-muted text-xs"
                          fontSize="12"
                        >
                          {d.day}
                        </text>
                        <text
                          x={x + 30}
                          y={y - 8}
                          textAnchor="middle"
                          className="fill-text-muted"
                          fontSize="11"
                        >
                          ₹{(d.amount / 1000).toFixed(1)}k
                        </text>
                      </g>
                    );
                  })}
                </svg>
              </motion.div>

              {/* Recent Orders */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, delay: 0.45 }}
                className="bg-white rounded-[20px] border border-gray-100 shadow-card overflow-hidden"
              >
                <div className="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
                  <h2 className="font-semibold text-lg text-text">Recent Orders</h2>
                  <button
                    onClick={() => setActiveTab("orders")}
                    className="text-emerald text-sm cursor-pointer hover:underline"
                  >
                    View All
                  </button>
                </div>
                <OrdersTable data={orders.slice(0, 5)} />
              </motion.div>
            </motion.div>
          )}

          {activeTab === "orders" && (
            <motion.div
              key="orders"
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -12 }}
              transition={{ duration: 0.25 }}
            >
              {/* Filters */}
              <div className="flex flex-wrap gap-4 mb-6">
                <div className="flex-1 min-w-[200px] relative">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" />
                  <input
                    type="text"
                    placeholder="Search by name, email, or order ID..."
                    value={searchQuery}
                    onChange={(e) => {
                      setSearchQuery(e.target.value);
                      setCurrentPage(1);
                    }}
                    className="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-text placeholder:text-text-muted/60 focus:outline-none focus:ring-2 focus:ring-emerald/20 focus:border-emerald transition-all"
                  />
                </div>
                <select
                  value={statusFilter}
                  onChange={(e) => {
                    setStatusFilter(e.target.value);
                    setCurrentPage(1);
                  }}
                  className="bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm text-text focus:outline-none focus:ring-2 focus:ring-emerald/20 focus:border-emerald transition-all"
                >
                  <option value="all">All Statuses</option>
                  <option value="Paid">Paid</option>
                  <option value="Generating">Generating</option>
                  <option value="Deploying">Deploying</option>
                  <option value="Delivered">Delivered</option>
                  <option value="Failed">Failed</option>
                </select>
                <select
                  value={occasionFilter}
                  onChange={(e) => {
                    setOccasionFilter(e.target.value);
                    setCurrentPage(1);
                  }}
                  className="bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm text-text focus:outline-none focus:ring-2 focus:ring-emerald/20 focus:border-emerald transition-all"
                >
                  <option value="all">All Occasions</option>
                  <option value="Birthday">Birthday</option>
                  <option value="Wedding">Wedding</option>
                  <option value="Anniversary">Anniversary</option>
                  <option value="Baby Shower">Baby Shower</option>
                  <option value="Corporate">Corporate</option>
                  <option value="Festival">Festival</option>
                </select>
                <select
                  value={planFilter}
                  onChange={(e) => {
                    setPlanFilter(e.target.value);
                    setCurrentPage(1);
                  }}
                  className="bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm text-text focus:outline-none focus:ring-2 focus:ring-emerald/20 focus:border-emerald transition-all"
                >
                  <option value="all">All Plans</option>
                  <option value="Basic">Basic</option>
                  <option value="Premium">Premium</option>
                  <option value="Deluxe">Deluxe</option>
                </select>
              </div>

              {/* Orders Table */}
              <div className="bg-white rounded-[20px] border border-gray-100 shadow-card overflow-hidden">
                <OrdersTable data={paginatedOrders} showActions />

                {/* Pagination */}
                <div className="flex justify-between items-center px-6 py-4 border-t border-gray-100">
                  <span className="text-sm text-text-muted">
                    Showing {filteredOrders.length === 0 ? 0 : (currentPage - 1) * ITEMS_PER_PAGE + 1}-
                    {Math.min(currentPage * ITEMS_PER_PAGE, filteredOrders.length)} of{" "}
                    {filteredOrders.length} orders
                  </span>
                  <div className="flex gap-2">
                    <button
                      onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                      disabled={currentPage === 1}
                      className="px-4 py-2 border border-gray-200 rounded-lg text-sm text-text disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors flex items-center gap-1"
                    >
                      <ChevronLeft className="w-4 h-4" />
                      Previous
                    </button>
                    <button
                      onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                      disabled={currentPage >= totalPages}
                      className="px-4 py-2 border border-gray-200 rounded-lg text-sm text-text disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors flex items-center gap-1"
                    >
                      Next
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </main>
    </div>
  );
}
