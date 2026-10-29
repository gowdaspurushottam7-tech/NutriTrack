import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ReferenceLine,
  Cell
} from 'recharts';
import { PlusCircle, ArrowRight, Utensils, Award, Sparkles, RefreshCw } from 'lucide-react';
import { fetchDashboard } from '../api/client';
import MetricCard from '../components/MetricCard';

export default function DashboardPage({ showToast }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await fetchDashboard();
      setData(res);
    } catch (err) {
      setError(err.message || 'Could not load dashboard data.');
      showToast?.('Error loading dashboard: ' + err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  if (loading && !data) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <div className="w-10 h-10 border-4 border-brand-200 border-t-brand-600 rounded-full animate-spin"></div>
        <p className="text-slate-500 font-medium text-sm">Loading nutrition dashboard...</p>
      </div>
    );
  }

  if (error && !data) {
    return (
      <div className="bg-rose-50 border border-rose-200 rounded-2xl p-6 text-center max-w-lg mx-auto my-8">
        <p className="text-rose-700 font-semibold mb-2">Failed to connect to backend API</p>
        <p className="text-rose-600 text-sm mb-4">{error}</p>
        <button
          onClick={loadDashboard}
          className="px-4 py-2 bg-rose-600 text-white rounded-xl text-sm font-medium hover:bg-rose-700 transition"
        >
          Try Again
        </button>
      </div>
    );
  }

  // Prepare chart data from weeklyCalories map
  const chartData = data?.weeklyCalories
    ? Object.entries(data.weeklyCalories).map(([day, val]) => ({
        day,
        calories: Math.round(val),
      }))
    : [];

  const calorieGoal = data?.calorieGoal || 2200;

  return (
    <div className="space-y-6 pb-12">
      {/* 1. Welcome Greeting Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white rounded-2xl p-5 sm:p-6 border border-slate-200/80 shadow-card">
        <div>
          <div className="flex items-center space-x-2 text-xs font-semibold text-brand-700 uppercase tracking-wider mb-1">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Daily Health Overview</span>
          </div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-slate-800 tracking-tight">
            {data?.greeting || 'Good Day'}, {data?.userName || 'User'}!
          </h1>
          <p className="text-slate-500 text-sm mt-0.5">
            You've logged <strong className="text-slate-700">{data?.totalMealsToday || 0} meals</strong> today. Keep maintaining your targets!
          </p>
        </div>

        <div className="flex items-center space-x-2 shrink-0">
          <button
            onClick={loadDashboard}
            className="p-2.5 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 transition"
            title="Refresh Data"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
          <Link
            to="/add-meal"
            className="flex-1 sm:flex-none flex items-center justify-center space-x-2 px-4 py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-sm font-semibold shadow-xs transition active:scale-95"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Log Meal</span>
          </Link>
        </div>
      </div>

      {/* 2. Four Daily Nutrition Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="Calories Consumed"
          icon="🔥"
          consumed={data?.caloriesConsumed || 0}
          goal={data?.calorieGoal || 2200}
          unit="kcal"
          color="emerald"
          percentage={data?.calorieCompletionPercentage || 0}
        />
        <MetricCard
          title="Protein"
          icon="🥩"
          consumed={data?.proteinConsumed || 0}
          goal={data?.proteinGoal || 120}
          unit="g"
          color="blue"
          percentage={data?.proteinCompletionPercentage || 0}
        />
        <MetricCard
          title="Carbohydrates"
          icon="🌾"
          consumed={data?.carbsConsumed || 0}
          goal={data?.carbGoal || 250}
          unit="g"
          color="amber"
          percentage={data?.carbCompletionPercentage || 0}
        />
        <MetricCard
          title="Healthy Fat"
          icon="🥑"
          consumed={data?.fatConsumed || 0}
          goal={data?.fatGoal || 65}
          unit="g"
          color="rose"
          percentage={data?.fatCompletionPercentage || 0}
        />
      </div>

      {/* 3. Middle Section: Today's Meal Summary & Health Targets */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Today's Meals Breakdown (2 Cols) */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-5 sm:p-6 border border-slate-200/80 shadow-card">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-2">
              <Utensils className="w-5 h-5 text-brand-600" />
              <h2 className="font-bold text-slate-800 text-base">Today's Meal Breakdown</h2>
            </div>
            <Link to="/history" className="text-xs font-semibold text-brand-600 hover:text-brand-700 flex items-center space-x-1">
              <span>View History</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            {[
              { type: 'Breakfast', icon: '🌅', count: data?.breakfastCount || 0 },
              { type: 'Lunch', icon: '☀️', count: data?.lunchCount || 0 },
              { type: 'Dinner', icon: '🌙', count: data?.dinnerCount || 0 },
              { type: 'Snacks', icon: '🍎', count: data?.snacksCount || 0 },
            ].map((m) => (
              <div
                key={m.type}
                className="bg-slate-50/80 rounded-xl p-3.5 border border-slate-200/60 flex flex-col justify-between"
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xl">{m.icon}</span>
                  <Link
                    to={`/add-meal?type=${m.type}`}
                    className="w-6 h-6 rounded-full bg-white border border-slate-200 text-brand-600 font-bold flex items-center justify-center text-xs hover:bg-brand-50"
                    title={`Add to ${m.type}`}
                  >
                    +
                  </Link>
                </div>
                <div>
                  <h4 className="text-xs font-medium text-slate-500">{m.type}</h4>
                  <p className="text-base font-bold text-slate-800 mt-0.5">
                    {m.count} <span className="text-xs font-normal text-slate-400">items</span>
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Health & Biometric Highlights Card */}
        <div className="bg-white rounded-2xl p-5 sm:p-6 border border-slate-200/80 shadow-card flex flex-col justify-between">
          <div>
            <div className="flex items-center space-x-2 mb-3">
              <Award className="w-5 h-5 text-brand-600" />
              <h2 className="font-bold text-slate-800 text-base">Health & Targets</h2>
            </div>

            <div className="space-y-3">
              <div className="flex justify-between items-center p-3 rounded-xl bg-slate-50 border border-slate-100">
                <span className="text-xs font-medium text-slate-500">Remaining Calories</span>
                <span className="font-bold text-brand-700 text-sm">
                  {Math.round(data?.remainingCalories || 0)} kcal
                </span>
              </div>

              <div className="flex justify-between items-center p-3 rounded-xl bg-slate-50 border border-slate-100">
                <span className="text-xs font-medium text-slate-500">Goal Completion</span>
                <span className="font-bold text-slate-800 text-sm">
                  {(data?.calorieCompletionPercentage || 0).toFixed(1)}%
                </span>
              </div>

              <div className="flex justify-between items-center p-3 rounded-xl bg-slate-50 border border-slate-100">
                <span className="text-xs font-medium text-slate-500">Body Mass Index (BMI)</span>
                <span className="font-bold text-blue-700 text-sm">
                  {(data?.bmi || 0).toFixed(1)} <span className="text-xs font-normal text-slate-500">({data?.bmiCategory || 'Normal'})</span>
                </span>
              </div>
            </div>
          </div>

          <Link
            to="/profile"
            className="mt-4 w-full text-center py-2 px-3 rounded-xl border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition"
          >
            Manage Targets & Profile →
          </Link>
        </div>
      </div>

      {/* 4. Weekly Calorie Intake Recharts Bar Chart */}
      <div className="bg-white rounded-2xl p-5 sm:p-6 border border-slate-200/80 shadow-card">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-6">
          <div>
            <h2 className="font-bold text-slate-800 text-base">7-Day Calorie Consumption Trend</h2>
            <p className="text-slate-400 text-xs mt-0.5">Comparing actual daily intake with your {Math.round(calorieGoal)} kcal target.</p>
          </div>
          <div className="flex items-center space-x-4 text-xs font-medium">
            <span className="flex items-center space-x-1.5 text-slate-600">
              <span className="w-3 h-3 rounded-xs bg-brand-500 inline-block"></span>
              <span>Intake</span>
            </span>
            <span className="flex items-center space-x-1.5 text-slate-600">
              <span className="w-3 h-0.5 bg-rose-500 inline-block"></span>
              <span>Goal ({calorieGoal} kcal)</span>
            </span>
          </div>
        </div>

        <div className="h-64 sm:h-72 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 5 }}>
              <XAxis dataKey="day" stroke="#94a3b8" fontSize={12} tickLine={false} />
              <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} />
              <Tooltip
                formatter={(val) => [`${val} kcal`, 'Calories']}
                contentStyle={{ borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}
              />
              <ReferenceLine y={calorieGoal} stroke="#ef4444" strokeDasharray="4 4" label={{ value: 'Target', position: 'insideTopRight', fill: '#ef4444', fontSize: 11 }} />
              <Bar dataKey="calories" radius={[8, 8, 0, 0]}>
                {chartData.map((entry, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={entry.calories > calorieGoal * 1.1 ? '#f59e0b' : '#22c55e'}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* 5. Recent Food Logs Preview */}
      <div className="bg-white rounded-2xl p-5 sm:p-6 border border-slate-200/80 shadow-card">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-bold text-slate-800 text-base">Recent Food Logs</h2>
          <Link to="/history" className="text-xs font-semibold text-brand-600 hover:text-brand-700 flex items-center space-x-1">
            <span>All Records</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {data?.recentMeals && data.recentMeals.length > 0 ? (
          <div className="divide-y divide-slate-100">
            {data.recentMeals.map((m) => (
              <div key={m.id} className="py-3 flex items-center justify-between text-sm">
                <div className="flex items-center space-x-3">
                  <div className="w-9 h-9 rounded-xl bg-brand-50 text-brand-700 flex items-center justify-center font-bold text-xs shrink-0">
                    {m.mealType.substring(0, 1)}
                  </div>
                  <div>
                    <h4 className="font-semibold text-slate-800 text-sm leading-tight">{m.foodName}</h4>
                    <p className="text-slate-400 text-xs mt-0.5">
                      {m.mealType} • {Math.round(m.quantityGrams)}g
                    </p>
                  </div>
                </div>
                <div className="text-right">
                  <span className="font-bold text-slate-800 block text-sm">{Math.round(m.calories)} kcal</span>
                  <span className="text-slate-400 text-xs">{m.protein.toFixed(1)}g protein</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-slate-400 text-sm py-4 text-center">No meals logged yet today.</p>
        )}
      </div>
    </div>
  );
}
