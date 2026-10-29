import React from 'react';
import { Link } from 'react-router-dom';
import { Menu, Plus, Calendar } from 'lucide-react';

export default function Navbar({ onOpenSidebar, userProfile }) {
  const todayFormatted = new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
  }).format(new Date());

  return (
    <header className="sticky top-0 z-30 h-16 bg-white/95 backdrop-blur-md border-b border-slate-200/80 px-4 lg:px-8 flex items-center justify-between">
      {/* Left: Mobile Menu Toggle & Brand Logo */}
      <div className="flex items-center space-x-3">
        <button
          onClick={onOpenSidebar}
          className="p-2 -ml-2 rounded-xl text-slate-600 hover:text-slate-900 hover:bg-slate-100 lg:hidden focus:outline-hidden"
          aria-label="Open navigation menu"
        >
          <Menu className="w-6 h-6" />
        </button>

        <Link to="/" className="flex items-center space-x-2 lg:hidden">
          <span className="text-xl">🥗</span>
          <span className="font-extrabold text-slate-800 tracking-tight text-base">
            Nutri<span className="text-brand-600">Track</span>
          </span>
        </Link>
      </div>

      {/* Middle/Right: Date Badge & Quick Add Action */}
      <div className="flex items-center space-x-3">
        {/* Date Display Pill */}
        <div className="flex items-center space-x-1.5 px-3 py-1.5 rounded-full bg-slate-100/90 text-xs font-medium text-slate-600">
          <Calendar className="w-3.5 h-3.5 text-brand-600" />
          <span>Today, {todayFormatted}</span>
        </div>

        {/* Quick Add Meal Button */}
        <Link
          to="/add-meal"
          className="flex items-center space-x-1.5 py-1.5 px-3 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-xs font-semibold shadow-xs transition active:scale-95"
        >
          <Plus className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">Add Meal</span>
        </Link>

        {/* User Avatar shortcut on mobile/tablet */}
        <Link to="/profile" className="lg:hidden">
          <div className="w-8 h-8 rounded-full bg-brand-100 text-brand-800 font-bold text-xs flex items-center justify-center ring-2 ring-brand-500/20">
            {userProfile?.name ? userProfile.name.charAt(0).toUpperCase() : 'P'}
          </div>
        </Link>
      </div>
    </header>
  );
}
