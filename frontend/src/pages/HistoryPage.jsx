import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, Calendar, Trash2, Edit3, X, Filter } from 'lucide-react';
import { fetchHistory, updateMeal, deleteMeal } from '../api/client';

export default function HistoryPage({ showToast }) {
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [selectedType, setSelectedType] = useState('All');
  const [historyData, setHistoryData] = useState(null);
  const [loading, setLoading] = useState(true);

  // Edit State
  const [editingEntry, setEditingEntry] = useState(null);
  const [editQty, setEditQty] = useState('');
  const [editType, setEditType] = useState('Breakfast');

  useEffect(() => {
    loadHistoryData(selectedDate, selectedType);
  }, [selectedDate, selectedType]);

  const loadHistoryData = async (date, type) => {
    try {
      setLoading(true);
      const data = await fetchHistory(date, type === 'All' ? '' : type);
      setHistoryData(data);
    } catch (err) {
      showToast?.('Error loading history: ' + err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const shiftDate = (days) => {
    const current = new Date(selectedDate);
    current.setDate(current.getDate() + days);
    setSelectedDate(current.toISOString().split('T')[0]);
  };

  const handleOpenEdit = (entry) => {
    setEditingEntry(entry);
    setEditQty(entry.quantityGrams);
    setEditType(entry.mealType);
  };

  const handleSaveEdit = async (e) => {
    e.preventDefault();
    if (Number(editQty) <= 0) {
      showToast?.('Quantity must be greater than zero grams.', 'error');
      return;
    }

    try {
      await updateMeal(editingEntry.id, {
        quantityGrams: Number(editQty),
        mealType: editType,
      });
      showToast?.('Meal entry updated successfully.', 'success');
      setEditingEntry(null);
      loadHistoryData(selectedDate, selectedType);
    } catch (err) {
      showToast?.(err.message, 'error');
    }
  };

  const handleDelete = async (entry) => {
    if (!window.confirm(`Delete ${entry.foodName} (${entry.mealType}) from history?`)) return;

    try {
      await deleteMeal(entry.id);
      showToast?.('Meal entry deleted.', 'success');
      loadHistoryData(selectedDate, selectedType);
    } catch (err) {
      showToast?.(err.message, 'error');
    }
  };

  const entries = historyData?.entries || [];

  return (
    <div className="space-y-6 pb-12">
      {/* 1. Header with Date Navigation Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-slate-800 tracking-tight">Nutrition History</h1>
          <p className="text-slate-500 text-sm mt-0.5">
            Audit, filter, and modify your logged meals across any calendar date.
          </p>
        </div>

        {/* Date Stepper Controls */}
        <div className="flex items-center space-x-2 bg-white rounded-2xl p-1.5 border border-slate-200/80 shadow-xs">
          <button
            onClick={() => shiftDate(-1)}
            className="p-2 rounded-xl text-slate-600 hover:bg-slate-100 transition"
            title="Previous Day"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="px-2.5 py-1 text-sm font-semibold text-slate-800 bg-transparent border-0 focus:ring-0 cursor-pointer"
          />

          <button
            onClick={() => shiftDate(1)}
            className="p-2 rounded-xl text-slate-600 hover:bg-slate-100 transition"
            title="Next Day"
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          <button
            onClick={() => setSelectedDate(new Date().toISOString().split('T')[0])}
            className="px-3 py-1 rounded-xl text-xs font-bold text-brand-700 bg-brand-50 hover:bg-brand-100 transition ml-1"
          >
            Today
          </button>
        </div>
      </div>

      {/* 2. Daily Summary Totals Ribbon */}
      <div className="grid grid-cols-2 sm:grid-cols-5 gap-3 bg-white rounded-2xl p-4 sm:p-5 border border-slate-200/80 shadow-card text-center">
        <div className="p-2 bg-slate-50 rounded-xl">
          <span className="text-[11px] font-medium text-slate-400 block">Total Meals</span>
          <span className="text-base font-extrabold text-slate-800">{historyData?.totalMeals || 0}</span>
        </div>
        <div className="p-2 bg-emerald-50 rounded-xl">
          <span className="text-[11px] font-medium text-emerald-700 block">Calories</span>
          <span className="text-base font-extrabold text-slate-800">
            {Math.round(historyData?.totalCalories || 0)} <span className="text-xs font-normal">kcal</span>
          </span>
        </div>
        <div className="p-2 bg-blue-50 rounded-xl">
          <span className="text-[11px] font-medium text-blue-700 block">Protein</span>
          <span className="text-base font-extrabold text-slate-800">
            {(historyData?.totalProtein || 0).toFixed(1)} <span className="text-xs font-normal">g</span>
          </span>
        </div>
        <div className="p-2 bg-amber-50 rounded-xl">
          <span className="text-[11px] font-medium text-amber-700 block">Carbohydrates</span>
          <span className="text-base font-extrabold text-slate-800">
            {(historyData?.totalCarbs || 0).toFixed(1)} <span className="text-xs font-normal">g</span>
          </span>
        </div>
        <div className="p-2 bg-rose-50 rounded-xl col-span-2 sm:col-span-1">
          <span className="text-[11px] font-medium text-rose-700 block">Healthy Fat</span>
          <span className="text-base font-extrabold text-slate-800">
            {(historyData?.totalFat || 0).toFixed(1)} <span className="text-xs font-normal">g</span>
          </span>
        </div>
      </div>

      {/* 3. Meal Category Filter Pills */}
      <div className="flex items-center space-x-2 overflow-x-auto pb-1">
        {['All', 'Breakfast', 'Lunch', 'Dinner', 'Snacks'].map((type) => (
          <button
            key={type}
            onClick={() => setSelectedType(type)}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition ${
              selectedType === type
                ? 'bg-slate-800 text-white shadow-xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
            }`}
          >
            {type}
          </button>
        ))}
      </div>

      {/* 4. Meal Records: Responsive Cards on Mobile, Table on Desktop */}
      {entries.length > 0 ? (
        <div className="space-y-3">
          {/* Desktop Table View */}
          <div className="hidden md:block bg-white rounded-2xl border border-slate-200/80 shadow-card overflow-hidden">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 font-semibold text-xs">
                <tr>
                  <th className="py-3 px-4">Meal Category</th>
                  <th className="py-3 px-4">Food Item</th>
                  <th className="py-3 px-4 text-right">Quantity</th>
                  <th className="py-3 px-4 text-right">Calories</th>
                  <th className="py-3 px-4 text-right">Protein</th>
                  <th className="py-3 px-4 text-right">Carbs</th>
                  <th className="py-3 px-4 text-right">Fat</th>
                  <th className="py-3 px-4 text-center">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {entries.map((entry) => (
                  <tr key={entry.id} className="hover:bg-slate-50/70 transition">
                    <td className="py-3 px-4">
                      <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-700">
                        {entry.mealType}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-semibold text-slate-800">{entry.foodName}</td>
                    <td className="py-3 px-4 text-right text-slate-600">{Math.round(entry.quantityGrams)}g</td>
                    <td className="py-3 px-4 text-right font-bold text-slate-800">{Math.round(entry.calories)} kcal</td>
                    <td className="py-3 px-4 text-right text-blue-700 font-medium">{entry.protein.toFixed(1)}g</td>
                    <td className="py-3 px-4 text-right text-amber-700 font-medium">{entry.carbs.toFixed(1)}g</td>
                    <td className="py-3 px-4 text-right text-rose-700 font-medium">{entry.fat.toFixed(1)}g</td>
                    <td className="py-3 px-4 text-center">
                      <div className="flex items-center justify-center space-x-2">
                        <button
                          onClick={() => handleOpenEdit(entry)}
                          className="p-1.5 text-slate-400 hover:text-slate-700 rounded-lg transition"
                          title="Edit"
                        >
                          <Edit3 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(entry)}
                          className="p-1.5 text-rose-400 hover:text-rose-600 rounded-lg transition"
                          title="Delete"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Mobile Card View (< 768px) */}
          <div className="md:hidden space-y-3">
            {entries.map((entry) => (
              <div
                key={entry.id}
                className="bg-white rounded-2xl p-4 border border-slate-200/80 shadow-card space-y-3"
              >
                <div className="flex items-start justify-between">
                  <div>
                    <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full bg-brand-50 text-brand-700 inline-block mb-1">
                      {entry.mealType}
                    </span>
                    <h3 className="font-bold text-slate-800 text-base">{entry.foodName}</h3>
                    <p className="text-xs text-slate-400 mt-0.5">{Math.round(entry.quantityGrams)} grams</p>
                  </div>
                  <div className="text-right">
                    <span className="text-lg font-extrabold text-slate-800 block">
                      {Math.round(entry.calories)} <span className="text-xs font-normal">kcal</span>
                    </span>
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-2 py-2 border-t border-slate-100 text-center text-xs">
                  <div className="bg-blue-50/70 p-1.5 rounded-lg">
                    <span className="text-[10px] text-blue-600 block">Protein</span>
                    <span className="font-bold text-slate-800">{entry.protein.toFixed(1)}g</span>
                  </div>
                  <div className="bg-amber-50/70 p-1.5 rounded-lg">
                    <span className="text-[10px] text-amber-600 block">Carbs</span>
                    <span className="font-bold text-slate-800">{entry.carbs.toFixed(1)}g</span>
                  </div>
                  <div className="bg-rose-50/70 p-1.5 rounded-lg">
                    <span className="text-[10px] text-rose-600 block">Fat</span>
                    <span className="font-bold text-slate-800">{entry.fat.toFixed(1)}g</span>
                  </div>
                </div>

                <div className="flex items-center justify-end space-x-2 pt-1">
                  <button
                    onClick={() => handleOpenEdit(entry)}
                    className="flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-slate-100 text-slate-700 text-xs font-semibold hover:bg-slate-200"
                  >
                    <Edit3 className="w-3.5 h-3.5" />
                    <span>Edit</span>
                  </button>
                  <button
                    onClick={() => handleDelete(entry)}
                    className="flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-rose-50 text-rose-700 text-xs font-semibold hover:bg-rose-100"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                    <span>Delete</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="bg-white rounded-2xl p-10 text-center border border-slate-200/80">
          <Calendar className="w-10 h-10 text-slate-300 mx-auto mb-2" />
          <p className="text-slate-600 font-semibold">No meals recorded for this date.</p>
          <p className="text-slate-400 text-xs mt-1">
            Use the "Add Meal" button to record your intake.
          </p>
        </div>
      )}

      {/* Edit Entry Modal */}
      {editingEntry && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs">
          <div className="bg-white rounded-2xl max-w-sm w-full p-6 shadow-2xl border border-slate-200">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-base text-slate-800">Edit Meal Entry</h3>
              <button
                onClick={() => setEditingEntry(null)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-600"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Food Item</label>
                <div className="font-bold text-slate-800 text-sm bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                  {editingEntry.foodName}
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Meal Category</label>
                <select
                  value={editType}
                  onChange={(e) => setEditType(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 text-sm font-medium focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                >
                  <option value="Breakfast">Breakfast</option>
                  <option value="Lunch">Lunch</option>
                  <option value="Dinner">Dinner</option>
                  <option value="Snacks">Snacks</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-500 mb-1">Quantity (Grams)</label>
                <input
                  type="number"
                  min="1"
                  max="5000"
                  required
                  value={editQty}
                  onChange={(e) => setEditQty(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 text-sm font-medium focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                />
              </div>

              <div className="flex items-center justify-end space-x-2 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setEditingEntry(null)}
                  className="px-4 py-2 rounded-xl border border-slate-200 text-xs font-semibold text-slate-600 hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold shadow-xs transition"
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
