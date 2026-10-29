import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import {
  LayoutDashboard,
  PlusCircle,
  Apple,
  Clock,
  BarChart3,
  User,
  Settings,
  X,
  Flame
} from 'lucide-react';

export default function Sidebar({ isOpen, onClose, userProfile }) {
  const navItems = [
    { name: 'Dashboard', path: '/', icon: LayoutDashboard },
    { name: 'Add Meal', path: '/add-meal', icon: PlusCircle },
    { name: 'Food Database', path: '/foods', icon: Apple },
    { name: 'History', path: '/history', icon: Clock },
    { name: 'Reports', path: '/reports', icon: BarChart3 },
    { name: 'Profile', path: '/profile', icon: User },
    { name: 'Settings', path: '/settings', icon: Settings },
  ];

  return (
    <>
      {/* Mobile Drawer Backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-40 lg:hidden transition-opacity"
          onClick={onClose}
        />
      )}

      {/* Sidebar Container */}
      <aside
        className={`fixed top-0 bottom-0 left-0 z-50 w-64 bg-white border-r border-slate-200/90 flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Header */}
        <div className="h-16 flex items-center justify-between px-6 border-b border-slate-100">
          <Link to="/" className="flex items-center space-x-2.5" onClick={onClose}>
            <span className="w-9 h-9 rounded-xl bg-brand-500 text-white flex items-center justify-center text-xl shadow-sm">
              🥗
            </span>
            <div>
              <span className="font-extrabold text-lg text-slate-800 tracking-tight block leading-tight">
                Nutri<span className="text-brand-600">Track</span>
              </span>
              <span className="text-[10px] text-slate-400 font-medium tracking-wider uppercase block">
                Dietary & Calorie Log
              </span>
            </div>
          </Link>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 lg:hidden"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation Menu Links */}
        <nav className="flex-1 px-3.5 py-4 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={onClose}
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3.5 py-2.5 rounded-xl font-medium text-sm transition-all duration-150 ${
                    isActive
                      ? 'bg-brand-50 text-brand-700 font-semibold shadow-xs'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                {({ isActive }) => (
                  <>
                    <Icon className={`w-5 h-5 ${isActive ? 'text-brand-600' : 'text-slate-400'}`} />
                    <span>{item.name}</span>
                  </>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Quick Add Meal CTA Button */}
        <div className="px-4 py-3">
          <Link
            to="/add-meal"
            onClick={onClose}
            className="w-full flex items-center justify-center space-x-2 py-2.5 px-4 bg-brand-600 hover:bg-brand-700 text-white rounded-xl font-semibold text-sm shadow-sm transition active:scale-[0.98]"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Log a Meal</span>
          </Link>
        </div>

        {/* User Profile Card at Bottom */}
        <div className="p-3.5 border-t border-slate-100 bg-slate-50/60">
          <Link
            to="/profile"
            onClick={onClose}
            className="flex items-center space-x-3 p-2 rounded-xl hover:bg-white transition group"
          >
            <div className="w-10 h-10 rounded-full bg-brand-100 text-brand-800 font-bold text-sm flex items-center justify-center ring-2 ring-brand-500/20">
              {userProfile?.name ? userProfile.name.charAt(0).toUpperCase() : 'P'}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-slate-800 truncate group-hover:text-brand-700">
                {userProfile?.name || 'Purushottam'}
              </p>
              <div className="flex items-center space-x-1 text-xs text-slate-500">
                <Flame className="w-3 h-3 text-brand-500" />
                <span>{userProfile?.dailyCalorieGoal ? Math.round(userProfile.dailyCalorieGoal) : 2200} kcal goal</span>
              </div>
            </div>
          </Link>
        </div>
      </aside>
    </>
  );
}
