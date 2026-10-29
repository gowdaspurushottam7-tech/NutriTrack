import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, PlusCircle, Apple, Clock } from 'lucide-react';

export default function BottomNav() {
  const items = [
    { name: 'Dashboard', path: '/', icon: LayoutDashboard },
    { name: 'Add Meal', path: '/add-meal', icon: PlusCircle, isPrimary: true },
    { name: 'Food DB', path: '/foods', icon: Apple },
    { name: 'History', path: '/history', icon: Clock },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t border-slate-200/80 px-2 py-1.5 lg:hidden pb-[max(0.5rem,env(safe-area-inset-bottom))]">
      <div className="grid grid-cols-4 items-center">
        {items.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex flex-col items-center justify-center py-1 rounded-xl transition active:scale-95 ${
                  isActive
                    ? 'text-brand-600 font-bold'
                    : 'text-slate-500 hover:text-slate-800'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <div
                    className={`p-1 rounded-full ${
                      item.isPrimary && isActive
                        ? 'bg-brand-100 text-brand-700'
                        : ''
                    }`}
                  >
                    <Icon className={`w-5 h-5 ${isActive ? 'stroke-[2.5]' : 'stroke-2'}`} />
                  </div>
                  <span className="text-[10px] mt-0.5 tracking-tight font-medium">
                    {item.name}
                  </span>
                </>
              )}
            </NavLink>
          );
        })}
      </div>
    </div>
  );
}
