import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import BottomNav from './components/BottomNav';
import NotificationToast from './components/NotificationToast';
import DashboardPage from './pages/DashboardPage';
import AddMealPage from './pages/AddMealPage';
import FoodDatabasePage from './pages/FoodDatabasePage';
import HistoryPage from './pages/HistoryPage';
import ReportsPage from './pages/ReportsPage';
import ProfilePage from './pages/ProfilePage';
import SettingsPage from './pages/SettingsPage';
import { fetchProfile } from './api/client';

/**
 * Root application component.
 *
 * Layout structure:
 *   ┌──────────────────────────────────────────┐
 *   │  Sidebar (lg: always visible, sm: drawer)│
 *   │  ┌────────────────────────────────────┐  │
 *   │  │ Navbar (sticky top header)         │  │
 *   │  ├────────────────────────────────────┤  │
 *   │  │ <main>  Page content area          │  │
 *   │  ├────────────────────────────────────┤  │
 *   │  │ BottomNav (mobile only, fixed)     │  │
 *   │  └────────────────────────────────────┘  │
 *   └──────────────────────────────────────────┘
 */
export default function App() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [notification, setNotification] = useState(null);
  const [profile, setProfile] = useState(null);

  // Load user profile on mount — used by Sidebar + Navbar
  useEffect(() => {
    fetchProfile()
      .then(res => setProfile(res?.data ?? res))
      .catch(() => {/* backend might not be running yet — silently ignore */});
  }, []);

  /**
   * showToast — call this from any page to show a toast notification.
   * @param {string} message  - Text to display
   * @param {'success'|'error'|'info'} type - Toast variant
   */
  const showToast = (message, type = 'success') => {
    setNotification({ message, type, id: Date.now() });
    setTimeout(() => setNotification(null), 4000);
  };

  return (
    <BrowserRouter>
      <div className="flex h-screen overflow-hidden bg-slate-50">

        {/* ── Left Sidebar ────────────────────────────────────────── */}
        <Sidebar
          isOpen={sidebarOpen}
          onClose={() => setSidebarOpen(false)}
          userProfile={profile}
        />

        {/* ── Main content column ─────────────────────────────────── */}
        {/* lg:ml-64 offsets for the fixed sidebar on large screens   */}
        <div className="flex-1 flex flex-col overflow-hidden lg:ml-64">

          {/* Sticky top navbar */}
          <Navbar
            onOpenSidebar={() => setSidebarOpen(true)}
            userProfile={profile}
          />

          {/* Scrollable page content */}
          {/* pb-20 leaves room for the fixed BottomNav on mobile */}
          <main className="flex-1 overflow-y-auto p-4 sm:p-6 pb-20 lg:pb-6">
            <Routes>
              <Route path="/"          element={<DashboardPage   showToast={showToast} />} />
              <Route path="/add-meal"  element={<AddMealPage     showToast={showToast} />} />
              <Route path="/foods"     element={<FoodDatabasePage showToast={showToast} />} />
              <Route path="/history"   element={<HistoryPage     showToast={showToast} />} />
              <Route path="/reports"   element={<ReportsPage     showToast={showToast} />} />
              <Route
                path="/profile"
                element={
                  <ProfilePage
                    showToast={showToast}
                    onProfileUpdate={(updated) => setProfile(updated)}
                  />
                }
              />
              <Route path="/settings"  element={<SettingsPage />} />
              {/* Catch-all — redirect unknown paths to dashboard */}
              <Route path="*"          element={<Navigate to="/" replace />} />
            </Routes>
          </main>

          {/* Fixed bottom nav — visible only on mobile (hidden on lg) */}
          <BottomNav />
        </div>

        {/* ── Toast notification overlay ───────────────────────────── */}
        <NotificationToast
          notification={notification}
          onClose={() => setNotification(null)}
        />
      </div>
    </BrowserRouter>
  );
}
