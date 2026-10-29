import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Search, Plus, Sparkles, Check, ArrowLeft } from 'lucide-react';
import { fetchFoods, addMeal } from '../api/client';

export default function AddMealPage({ showToast }) {
  const location = useLocation();
  const navigate = useNavigate();

  // Parse query params for pre-selected meal type or food
  const searchParams = new URLSearchParams(location.search);
  const initialType = searchParams.get('type') || 'Breakfast';

  const [mealType, setMealType] = useState(initialType);
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [searchQuery, setSearchQuery] = useState('');
  const [foods, setFoods] = useState([]);
  const [selectedFood, setSelectedFood] = useState(null);
  const [quantity, setQuantity] = useState(100);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadFoodList();
  }, []);

  const loadFoodList = async (query = '') => {
    try {
      setLoading(true);
      const data = await fetchFoods(query);
      setFoods(data || []);
      if (!selectedFood && data && data.length > 0) {
        setSelectedFood(data[0]);
      }
    } catch (err) {
      showToast?.('Could not load food catalog: ' + err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    const val = e.target.value;
    setSearchQuery(val);
    loadFoodList(val);
  };

  // Live nutritional calculations
  const calcNutrient = (nutrientPer100g) => {
    if (!nutrientPer100g || quantity <= 0) return 0;
    return (nutrientPer100g * quantity) / 100.0;
  };

  const previewCalories = selectedFood ? calcNutrient(selectedFood.caloriesPer100g) : 0;
  const previewProtein = selectedFood ? calcNutrient(selectedFood.proteinPer100g) : 0;
  const previewCarbs = selectedFood ? calcNutrient(selectedFood.carbsPer100g) : 0;
  const previewFat = selectedFood ? calcNutrient(selectedFood.fatPer100g) : 0;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFood) {
      showToast?.('Please choose a food item from the list.', 'error');
      return;
    }
    if (quantity <= 0) {
      showToast?.('Please specify a positive quantity in grams.', 'error');
      return;
    }
    if (quantity > 5000) {
      showToast?.('Quantity exceeds 5,000 grams limit.', 'error');
      return;
    }

    try {
      setSubmitting(true);
      const payload = {
        date,
        mealType,
        foodId: selectedFood.id,
        foodName: selectedFood.name,
        quantityGrams: Number(quantity),
      };
      await addMeal(payload);
      showToast?.(`Successfully logged ${selectedFood.name} (${Math.round(previewCalories)} kcal)!`, 'success');
      navigate('/');
    } catch (err) {
      showToast?.('Failed to save meal: ' + err.message, 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 pb-12">
      {/* Header */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate(-1)}
          className="p-2 -ml-2 rounded-xl text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition flex items-center space-x-1"
        >
          <ArrowLeft className="w-5 h-5" />
          <span className="text-sm font-medium">Back</span>
        </button>
        <h1 className="text-xl font-bold text-slate-800">Add Meal Entry</h1>
        <div className="w-16"></div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* 1. Meal Category Selection Tabs */}
        <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200/80 shadow-card">
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2.5">
            1. Select Meal Category
          </label>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            {[
              { type: 'Breakfast', icon: '🌅' },
              { type: 'Lunch', icon: '☀️' },
              { type: 'Dinner', icon: '🌙' },
              { type: 'Snacks', icon: '🍎' },
            ].map((m) => (
              <button
                type="button"
                key={m.type}
                onClick={() => setMealType(m.type)}
                className={`flex items-center justify-center space-x-2 py-3 px-3 rounded-xl border font-semibold text-sm transition ${
                  mealType === m.type
                    ? 'border-brand-600 bg-brand-50 text-brand-700 shadow-xs ring-1 ring-brand-600'
                    : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'
                }`}
              >
                <span>{m.icon}</span>
                <span>{m.type}</span>
              </button>
            ))}
          </div>

          <div className="mt-4">
            <label className="block text-xs font-medium text-slate-500 mb-1">Date</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="w-full sm:w-auto px-3.5 py-2 rounded-xl border border-slate-200 text-sm font-medium focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
              required
            />
          </div>
        </div>

        {/* 2. Food Item Selector & Search */}
        <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200/80 shadow-card space-y-3">
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">
            2. Choose Food Item
          </label>

          {/* Search bar */}
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search catalog (e.g. Rice, Oats, Paneer, Chicken)..."
              value={searchQuery}
              onChange={handleSearchChange}
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
            />
          </div>

          {/* Food Select Dropdown / Quick List */}
          <div className="max-h-52 overflow-y-auto divide-y divide-slate-100 border border-slate-100 rounded-xl">
            {foods.map((f) => (
              <div
                key={f.id}
                onClick={() => setSelectedFood(f)}
                className={`p-3 flex items-center justify-between cursor-pointer transition ${
                  selectedFood?.id === f.id
                    ? 'bg-brand-50/90 text-brand-900 font-semibold'
                    : 'hover:bg-slate-50 text-slate-700'
                }`}
              >
                <div>
                  <div className="text-sm">{f.name}</div>
                  <div className="text-xs text-slate-400 font-normal">
                    {f.category} • {f.caloriesPer100g} kcal per 100g
                  </div>
                </div>
                {selectedFood?.id === f.id && (
                  <Check className="w-5 h-5 text-brand-600 shrink-0" />
                )}
              </div>
            ))}
          </div>
        </div>

        {/* 3. Quantity & Portion in Grams */}
        <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200/80 shadow-card space-y-3">
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">
            3. Quantity Consumed (Grams)
          </label>

          <div className="flex items-center space-x-3">
            <input
              type="number"
              min="1"
              max="5000"
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
              className="w-full text-lg font-bold px-4 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
              required
            />
            <span className="text-sm font-semibold text-slate-500">grams</span>
          </div>

          {/* Quick Increment Pill Buttons */}
          <div className="flex flex-wrap gap-2 pt-1">
            {[50, 100, 150, 200, 250].map((amt) => (
              <button
                type="button"
                key={amt}
                onClick={() => setQuantity(amt)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold border transition ${
                  quantity === amt
                    ? 'bg-slate-800 text-white border-slate-800'
                    : 'bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100'
                }`}
              >
                {amt}g
              </button>
            ))}
          </div>
        </div>

        {/* 4. Live Nutrition Calculation Preview Card */}
        <div className="bg-brand-900 text-white rounded-2xl p-5 shadow-soft space-y-3">
          <div className="flex items-center space-x-2 text-brand-300 text-xs font-semibold uppercase tracking-wider">
            <Sparkles className="w-4 h-4" />
            <span>Automatic Nutrition Calculation</span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-1">
            <div className="bg-brand-800/60 rounded-xl p-3 border border-brand-700/50">
              <span className="text-xs text-brand-200 block">Total Calories</span>
              <span className="text-xl font-extrabold text-white">
                {previewCalories.toFixed(0)} <span className="text-xs font-normal">kcal</span>
              </span>
            </div>
            <div className="bg-brand-800/60 rounded-xl p-3 border border-brand-700/50">
              <span className="text-xs text-blue-200 block">Protein</span>
              <span className="text-xl font-extrabold text-blue-300">
                {previewProtein.toFixed(1)} <span className="text-xs font-normal">g</span>
              </span>
            </div>
            <div className="bg-brand-800/60 rounded-xl p-3 border border-brand-700/50">
              <span className="text-xs text-amber-200 block">Carbohydrates</span>
              <span className="text-xl font-extrabold text-amber-300">
                {previewCarbs.toFixed(1)} <span className="text-xs font-normal">g</span>
              </span>
            </div>
            <div className="bg-brand-800/60 rounded-xl p-3 border border-brand-700/50">
              <span className="text-xs text-rose-200 block">Healthy Fat</span>
              <span className="text-xl font-extrabold text-rose-300">
                {previewFat.toFixed(1)} <span className="text-xs font-normal">g</span>
              </span>
            </div>
          </div>
        </div>

        {/* Action Button */}
        <button
          type="submit"
          disabled={submitting}
          className="w-full py-3.5 px-6 rounded-2xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-base shadow-sm transition active:scale-[0.99] disabled:opacity-50 flex items-center justify-center space-x-2"
        >
          <Plus className="w-5 h-5" />
          <span>{submitting ? 'Saving Meal...' : `Log ${mealType}`}</span>
        </button>
      </form>
    </div>
  );
}
