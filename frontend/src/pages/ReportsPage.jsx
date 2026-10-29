import React, { useState, useEffect, useCallback } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  LineChart, Line, ReferenceLine, Legend
} from 'recharts';
import {
  fetchDailyReport, fetchWeeklyReport, getExportCsvUrl, getExportTxtUrl
} from '../api/client';
import {
  BarChart2, Calendar, Download, TrendingUp, TrendingDown,
  Flame, Dumbbell, Wheat, Droplets, RefreshCw, FileText, FileSpreadsheet
} from 'lucide-react';

// ── Helpers ──────────────────────────────────────────────────────────────────
const fmt = (n, d = 1) => (isNaN(n) ? '0' : Number(n).toFixed(d));

const PCT_COLOR = (pct) => {
  if (pct >= 100) return 'text-red-500';
  if (pct >= 80) return 'text-amber-500';
  return 'text-emerald-600';
};

const MACRO_CARDS = [
  { key: 'totalCalories', goalKey: 'calorieGoal',  label: 'Calories',      unit: 'kcal', icon: Flame,    color: 'bg-red-50 text-red-600',     bar: '#ef4444' },
  { key: 'totalProtein',  goalKey: 'proteinGoal',  label: 'Protein',       unit: 'g',    icon: Dumbbell, color: 'bg-blue-50 text-blue-600',   bar: '#3b82f6' },
  { key: 'totalCarbs',    goalKey: 'carbGoal',     label: 'Carbohydrates', unit: 'g',    icon: Wheat,    color: 'bg-amber-50 text-amber-600', bar: '#f59e0b' },
  { key: 'totalFat',      goalKey: 'fatGoal',      label: 'Fat',           unit: 'g',    icon: Droplets, color: 'bg-rose-50 text-rose-500',   bar: '#f43f5e' },
];

// Custom tooltip for recharts
const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white border border-slate-200 rounded-xl shadow-lg p-3 text-sm">
        <p className="font-semibold text-slate-700 mb-1">{label}</p>
        {payload.map((p, i) => (
          <p key={i} style={{ color: p.color }} className="font-medium">
            {p.name}: {fmt(p.value, 0)} {p.name === 'Calories' ? 'kcal' : 'g'}
          </p>
        ))}
      </div>
    );
  }
  return null;
};

// ── Sub-components ────────────────────────────────────────────────────────────
function MacroSummaryCard({ data, meta }) {
  const Icon = meta.icon;
  const val  = data?.[meta.key] ?? 0;
  const goal = data?.[meta.goalKey] ?? 0;
  const pct  = goal > 0 ? Math.min((val / goal) * 100, 120) : 0;
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5 flex flex-col gap-3">
      <div className="flex items-center gap-3">
        <div className={`${meta.color} rounded-xl p-2.5`}>
          <Icon size={18} />
        </div>
        <span className="text-sm font-semibold text-slate-600">{meta.label}</span>
      </div>
      <div className="flex items-end justify-between">
        <span className="text-2xl font-bold text-slate-800">{fmt(val, 0)}</span>
        <span className="text-xs text-slate-400">{meta.unit}</span>
      </div>
      {goal > 0 && (
        <>
          <div className="w-full bg-slate-100 rounded-full h-2">
            <div
              className="h-2 rounded-full transition-all duration-500"
              style={{ width: `${Math.min(pct, 100)}%`, backgroundColor: meta.bar }}
            />
          </div>
          <p className={`text-xs font-medium ${PCT_COLOR(pct)}`}>
            {fmt(pct, 0)}% of {fmt(goal, 0)} {meta.unit} goal
          </p>
        </>
      )}
    </div>
  );
}

function StatBadge({ label, value, sub, accent }) {
  return (
    <div className={`rounded-2xl p-4 flex flex-col gap-1 ${accent}`}>
      <span className="text-xs font-medium opacity-70">{label}</span>
      <span className="text-xl font-bold">{value}</span>
      {sub && <span className="text-xs opacity-60">{sub}</span>}
    </div>
  );
}

// ── Daily Tab ─────────────────────────────────────────────────────────────────
function DailyTab({ showToast }) {
  const [report, setReport]   = useState(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const r = await fetchDailyReport();
      setReport(r?.data ?? r);
    } catch (e) {
      showToast?.('Failed to load daily report', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => { load(); }, [load]);

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-500 border-t-transparent" />
    </div>
  );

  if (!report) return (
    <div className="text-center py-20 text-slate-400">No daily data available.</div>
  );

  const pct = report.calorieGoal > 0
    ? Math.min(((report.totalCalories / report.calorieGoal) * 100), 120)
    : 0;

  return (
    <div className="space-y-6">
      {/* Date + goal ring */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <p className="text-sm text-slate-500 flex items-center gap-1.5">
              <Calendar size={14} /> {report.date ?? 'Today'}
            </p>
            <h3 className="text-2xl font-bold text-slate-800 mt-1">Daily Summary</h3>
            <p className="text-sm text-slate-400 mt-0.5">{report.totalMeals ?? 0} meals logged</p>
          </div>
          {/* Calorie ring */}
          <div className="flex flex-col items-center">
            <div className="relative w-24 h-24">
              <svg viewBox="0 0 36 36" className="w-24 h-24 -rotate-90">
                <circle cx="18" cy="18" r="15.9" fill="none" stroke="#f1f5f9" strokeWidth="3.2" />
                <circle
                  cx="18" cy="18" r="15.9" fill="none"
                  stroke={pct >= 100 ? '#ef4444' : '#10b981'} strokeWidth="3.2"
                  strokeDasharray={`${Math.min(pct, 100)} ${100 - Math.min(pct, 100)}`}
                  strokeLinecap="round"
                />
              </svg>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span className="text-lg font-bold text-slate-800">{fmt(pct, 0)}%</span>
                <span className="text-xs text-slate-400">goal</span>
              </div>
            </div>
            <p className="text-xs text-slate-500 mt-1 text-center">
              {fmt(report.totalCalories, 0)} / {fmt(report.calorieGoal, 0)} kcal
            </p>
          </div>
        </div>
      </div>

      {/* Macro cards grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {MACRO_CARDS.map(m => <MacroSummaryCard key={m.key} data={report} meta={m} />)}
      </div>

      {/* Meal breakdown */}
      {report.mealBreakdown && (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          <h4 className="font-semibold text-slate-700 mb-4 text-sm uppercase tracking-wide">Meal Breakdown</h4>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            {Object.entries(report.mealBreakdown).map(([type, kcal]) => (
              <StatBadge
                key={type}
                label={type}
                value={`${fmt(kcal, 0)} kcal`}
                accent="bg-slate-50 text-slate-700"
              />
            ))}
          </div>
        </div>
      )}

      {/* Export buttons */}
      <div className="flex flex-wrap gap-3">
        <a
          href={getExportCsvUrl('daily')}
          download
          className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white px-5 py-2.5 rounded-xl text-sm font-semibold transition-colors shadow-sm"
        >
          <FileSpreadsheet size={16} /> Export CSV
        </a>
        <a
          href={getExportTxtUrl('daily')}
          download
          className="flex items-center gap-2 bg-slate-700 hover:bg-slate-800 text-white px-5 py-2.5 rounded-xl text-sm font-semibold transition-colors shadow-sm"
        >
          <FileText size={16} /> Export TXT
        </a>
      </div>
    </div>
  );
}

// ── Weekly Tab ────────────────────────────────────────────────────────────────
function WeeklyTab({ showToast }) {
  const [report, setReport]   = useState(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const r = await fetchWeeklyReport();
      setReport(r?.data ?? r);
    } catch (e) {
      showToast?.('Failed to load weekly report', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => { load(); }, [load]);

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-500 border-t-transparent" />
    </div>
  );

  if (!report) return (
    <div className="text-center py-20 text-slate-400">No weekly data available.</div>
  );

  // Build chart data from dailyCalorieMap
  const calMap  = report.dailyCalorieMap ?? {};
  const protMap = report.dailyProteinMap ?? {};
  const chartData = Object.entries(calMap)
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([date, calories]) => ({
      date: date.slice(5),   // "MM-DD"
      Calories: Math.round(calories),
      Protein: Math.round(protMap[date] ?? 0),
    }));

  const calorieGoal = report.calorieGoal ?? 2000;

  return (
    <div className="space-y-6">
      {/* Summary badges */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
        <StatBadge
          label="Avg Daily Calories"
          value={`${fmt(report.avgDailyCalories, 0)} kcal`}
          accent="bg-emerald-50 text-emerald-800"
        />
        <StatBadge
          label="Avg Daily Protein"
          value={`${fmt(report.avgDailyProtein ?? report.avgProtein, 1)} g`}
          accent="bg-blue-50 text-blue-800"
        />
        <StatBadge
          label="Highest Day"
          value={`${fmt(report.highestCalorieValue, 0)} kcal`}
          sub={report.highestCalorieDate}
          accent="bg-rose-50 text-rose-800"
        />
        <StatBadge
          label="Lowest Day"
          value={`${fmt(report.lowestCalorieValue, 0)} kcal`}
          sub={report.lowestCalorieDate}
          accent="bg-sky-50 text-sky-800"
        />
      </div>

      {/* Weekly calorie bar chart */}
      {chartData.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          <div className="flex items-center justify-between mb-4">
            <h4 className="font-semibold text-slate-700">7-Day Calorie Trend</h4>
            <span className="text-xs text-slate-400 flex items-center gap-1">
              <TrendingUp size={13} /> Goal: {calorieGoal} kcal
            </span>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={chartData} margin={{ top: 4, right: 4, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
              <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#94a3b8' }} />
              <YAxis tick={{ fontSize: 11, fill: '#94a3b8' }} />
              <Tooltip content={<CustomTooltip />} />
              <ReferenceLine y={calorieGoal} stroke="#f59e0b" strokeDasharray="4 2"
                label={{ value: 'Goal', position: 'insideTopRight', fontSize: 10, fill: '#f59e0b' }} />
              <Bar dataKey="Calories" fill="#10b981" radius={[6, 6, 0, 0]} maxBarSize={48} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Protein trend line chart */}
      {chartData.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          <h4 className="font-semibold text-slate-700 mb-4">7-Day Protein Trend</h4>
          <ResponsiveContainer width="100%" height={180}>
            <LineChart data={chartData} margin={{ top: 4, right: 4, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
              <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#94a3b8' }} />
              <YAxis tick={{ fontSize: 11, fill: '#94a3b8' }} />
              <Tooltip content={<CustomTooltip />} />
              <Line type="monotone" dataKey="Protein" stroke="#3b82f6" strokeWidth={2.5}
                dot={{ r: 4, fill: '#3b82f6' }} activeDot={{ r: 6 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Per-day table */}
      {chartData.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <h4 className="font-semibold text-slate-700 px-6 py-4 border-b border-slate-100">Daily Breakdown</h4>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-slate-50">
                <tr>
                  <th className="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Date</th>
                  <th className="text-right px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Calories</th>
                  <th className="text-right px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Protein</th>
                  <th className="text-right px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">vs Goal</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {chartData.map((row, i) => {
                  const pct = calorieGoal > 0 ? Math.round((row.Calories / calorieGoal) * 100) : 0;
                  return (
                    <tr key={i} className="hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-3 font-medium text-slate-700">{row.date}</td>
                      <td className="px-6 py-3 text-right font-semibold text-slate-800">{row.Calories} kcal</td>
                      <td className="px-6 py-3 text-right text-slate-600">{row.Protein} g</td>
                      <td className="px-4 py-3 text-right">
                        <span className={`text-xs font-bold ${PCT_COLOR(pct)}`}>{pct}%</span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Export */}
      <div className="flex flex-wrap gap-3">
        <a
          href={getExportCsvUrl('weekly')}
          download
          className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white px-5 py-2.5 rounded-xl text-sm font-semibold transition-colors shadow-sm"
        >
          <FileSpreadsheet size={16} /> Export CSV
        </a>
        <a
          href={getExportTxtUrl('weekly')}
          download
          className="flex items-center gap-2 bg-slate-700 hover:bg-slate-800 text-white px-5 py-2.5 rounded-xl text-sm font-semibold transition-colors shadow-sm"
        >
          <FileText size={16} /> Export TXT
        </a>
      </div>
    </div>
  );
}

// ── Main Component ────────────────────────────────────────────────────────────
export default function ReportsPage({ showToast }) {
  const [tab, setTab] = useState('daily');

  const tabs = [
    { id: 'daily',  label: 'Daily Report',  Icon: Calendar },
    { id: 'weekly', label: 'Weekly Report', Icon: BarChart2 },
  ];

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Reports</h1>
          <p className="text-slate-500 text-sm mt-0.5">Analyse your nutrition trends</p>
        </div>
        <TrendingUp className="text-emerald-500" size={28} />
      </div>

      {/* Tab switcher */}
      <div className="flex gap-2 bg-slate-100 p-1 rounded-xl w-fit">
        {tabs.map(({ id, label, Icon }) => (
          <button
            key={id}
            onClick={() => setTab(id)}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg text-sm font-semibold transition-all
              ${tab === id
                ? 'bg-white text-emerald-700 shadow-sm'
                : 'text-slate-500 hover:text-slate-700'}`}
          >
            <Icon size={15} /> {label}
          </button>
        ))}
      </div>

      {/* Tab content */}
      {tab === 'daily'  && <DailyTab  showToast={showToast} />}
      {tab === 'weekly' && <WeeklyTab showToast={showToast} />}
    </div>
  );
}
