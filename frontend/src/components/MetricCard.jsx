import React from 'react';

export default function MetricCard({ title, icon, consumed, goal, unit = 'g', color = 'emerald', percentage = 0 }) {
  const colorMap = {
    emerald: {
      bg: 'bg-emerald-50',
      text: 'text-emerald-700',
      border: 'border-emerald-200',
      bar: 'bg-emerald-500',
      badge: 'bg-emerald-100 text-emerald-800',
    },
    blue: {
      bg: 'bg-blue-50',
      text: 'text-blue-700',
      border: 'border-blue-200',
      bar: 'bg-blue-600',
      badge: 'bg-blue-100 text-blue-800',
    },
    amber: {
      bg: 'bg-amber-50',
      text: 'text-amber-700',
      border: 'border-amber-200',
      bar: 'bg-amber-500',
      badge: 'bg-amber-100 text-amber-800',
    },
    rose: {
      bg: 'bg-rose-50',
      text: 'text-rose-700',
      border: 'border-rose-200',
      bar: 'bg-rose-500',
      badge: 'bg-rose-100 text-rose-800',
    },
  };

  const scheme = colorMap[color] || colorMap.emerald;
  const clampedProgress = Math.min(100, Math.max(0, percentage));
  const isOverflow = percentage > 100;

  return (
    <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-card hover:shadow-soft transition-all duration-200">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center space-x-2">
          <span className={`w-8 h-8 rounded-xl flex items-center justify-center text-base ${scheme.bg} ${scheme.text}`}>
            {icon}
          </span>
          <h3 className="font-semibold text-slate-700 text-sm">{title}</h3>
        </div>
        <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${scheme.badge}`}>
          {percentage.toFixed(0)}%
        </span>
      </div>

      <div className="flex items-baseline space-x-1 mb-3">
        <span className="text-2xl font-bold tracking-tight text-slate-800">
          {Math.round(consumed).toLocaleString()}
        </span>
        <span className="text-slate-400 font-medium text-sm">
          / {Math.round(goal).toLocaleString()} {unit}
        </span>
      </div>

      {/* Progress Track */}
      <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
        <div
          className={`h-full rounded-full transition-all duration-500 ease-out ${scheme.bar} ${
            isOverflow ? 'opacity-90 ring-1 ring-amber-400' : ''
          }`}
          style={{ width: `${clampedProgress}%` }}
        />
      </div>

      <div className="flex justify-between items-center mt-2 text-[11px] text-slate-400">
        <span>Consumed</span>
        <span>Target: {Math.round(goal)} {unit}</span>
      </div>
    </div>
  );
}
