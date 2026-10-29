import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Plus, Trash2, Edit3, Apple, PlusCircle, AlertCircle, X } from 'lucide-react';
import { fetchFoods, createCustomFood, updateCustomFood, deleteCustomFood } from '../api/client';

export default function FoodDatabasePage({ showToast }) {
  const [foods, setFoods] = useState([]);
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [loading, setLoading] = useState(true);

  // Modal State for Add / Edit
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFood, setEditingFood] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    category: 'Custom',
    caloriesPer100g: '',
    proteinPer100g: '',
    carbsPer100g: '',
    fatPer100g: '',
  });

  useEffect(() => {
    loadCatalog();
  }, []);

  const loadCatalog = async () => {
    try {
      setLoading(true);
      const data = await fetchFoods();
      setFoods(data || []);
    } catch (err) {
      showToast?.('Error loading foods: ' + err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  // Categories list
  const categories = ['All', ...new Set(foods.map((f) => f.category || 'General'))];

  const filteredFoods = foods.filter((f) => {
    const matchesSearch =
      f.name.toLowerCase().includes(search.toLowerCase()) ||
      (f.category && f.category.toLowerCase().includes(search.toLowerCase()));
    const matchesCat = selectedCategory === 'All' || f.category === selectedCategory;
    return matchesSearch && matchesCat;
  });

  const handleOpenAdd = () => {
    setEditingFood(null);
    setFormData({
      name: '',
      category: 'Custom',
      caloriesPer100g: '',
      proteinPer100g: '',
      carbsPer100g: '',
      fatPer100g: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (food) => {
    if (!food.isCustom) {
      showToast?.('Built-in system foods are protected and cannot be modified.', 'error');
      return;
    }
    setEditingFood(food);
    setFormData({
      name: food.name,
      category: food.category || 'Custom',
      caloriesPer100g: food.caloriesPer100g,
      proteinPer100g: food.proteinPer100g,
      carbsPer100g: food.carbsPer100g,
      fatPer100g: food.fatPer100g,
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (food) => {
    if (!food.isCustom) {
      showToast?.('Built-in foods cannot be deleted to preserve catalog integrity.', 'error');
      return;
    }
    if (!window.confirm(`Are you sure you want to delete '${food.name}'?`)) return;

    try {
      await deleteCustomFood(food.id);
      showToast?.(`'${food.name}' deleted successfully.`, 'success');
      loadCatalog();
    } catch (err) {
      showToast?.(err.message, 'error');
    }
  };

  const handleModalSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      name: formData.name.trim(),
      category: formData.category.trim() || 'Custom',
      caloriesPer100g: Number(formData.caloriesPer100g),
      proteinPer100g: Number(formData.proteinPer100g),
      carbsPer100g: Number(formData.carbsPer100g),
      fatPer100g: Number(formData.fatPer100g),
    };

    if (!payload.name) {
      showToast?.('Food name cannot be empty.', 'error');
      return;
    }
    if (payload.caloriesPer100g < 0 || payload.proteinPer100g < 0 || payload.carbsPer100g < 0 || payload.fatPer100g < 0) {
      showToast?.('Nutrition metrics cannot be negative.', 'error');
      return;
    }
    if (payload.proteinPer100g + payload.carbsPer100g + payload.fatPer100g > 100) {
      showToast?.('Sum of protein, carbs, and fat cannot exceed 100g.', 'error');
      return;
    }

    try {
      if (editingFood) {
        await updateCustomFood(editingFood.id, payload);
        showToast?.('Custom food updated successfully.', 'success');
      } else {
        await createCustomFood(payload);
        showToast?.(`Custom food '${payload.name}' added successfully!`, 'success');
      }
      setIsModalOpen(false);
      loadCatalog();
    } catch (err) {
      showToast?.(err.message, 'error');
    }
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-slate-800 tracking-tight">Food Database</h1>
          <p className="text-slate-500 text-sm mt-0.5">
            Browse 30+ verified food items or register your own custom recipes.
          </p>
        </div>

        <button
          onClick={handleOpenAdd}
          className="flex items-center justify-center space-x-2 px-4 py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl text-sm font-semibold shadow-xs transition active:scale-95 shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>Add Custom Food</span>
        </button>
      </div>

      {/* Search & Category Filter Pills */}
      <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200/80 shadow-card space-y-3">
        <div className="relative">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search foods by name or category..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
          />
        </div>

        <div className="flex items-center space-x-1.5 overflow-x-auto pb-1 pt-1 scrollbar-none">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition ${
                selectedCategory === cat
                  ? 'bg-slate-800 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* Food Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        {filteredFoods.map((food) => (
          <div
            key={food.id}
            className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200/80 shadow-card hover:shadow-soft transition-all duration-200 flex flex-col justify-between"
          >
            <div>
              <div className="flex items-start justify-between gap-2 mb-2">
                <div className="flex-1">
                  <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full bg-slate-100 text-slate-600 inline-block mb-1">
                    {food.category || 'General'}
                  </span>
                  <h3 className="font-bold text-slate-800 text-base leading-snug">{food.name}</h3>
                </div>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full shrink-0 ${
                    food.isCustom
                      ? 'bg-blue-100 text-blue-700'
                      : 'bg-slate-100 text-slate-500'
                  }`}
                >
                  {food.isCustom ? 'Custom' : 'Built-in'}
                </span>
              </div>

              {/* Nutrition breakdown per 100g */}
              <div className="grid grid-cols-4 gap-1.5 py-3 border-y border-slate-100 my-3 text-center">
                <div className="bg-emerald-50/70 p-2 rounded-xl">
                  <span className="text-[10px] text-emerald-700 font-medium block">Calories</span>
                  <span className="font-bold text-slate-800 text-xs sm:text-sm">
                    {Math.round(food.caloriesPer100g)} <span className="text-[9px] font-normal">kcal</span>
                  </span>
                </div>
                <div className="bg-blue-50/70 p-2 rounded-xl">
                  <span className="text-[10px] text-blue-700 font-medium block">Protein</span>
                  <span className="font-bold text-slate-800 text-xs sm:text-sm">
                    {food.proteinPer100g.toFixed(1)} <span className="text-[9px] font-normal">g</span>
                  </span>
                </div>
                <div className="bg-amber-50/70 p-2 rounded-xl">
                  <span className="text-[10px] text-amber-700 font-medium block">Carbs</span>
                  <span className="font-bold text-slate-800 text-xs sm:text-sm">
                    {food.carbsPer100g.toFixed(1)} <span className="text-[9px] font-normal">g</span>
                  </span>
                </div>
                <div className="bg-rose-50/70 p-2 rounded-xl">
                  <span className="text-[10px] text-rose-700 font-medium block">Fat</span>
                  <span className="font-bold text-slate-800 text-xs sm:text-sm">
                    {food.fatPer100g.toFixed(1)} <span className="text-[9px] font-normal">g</span>
                  </span>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-between pt-2">
              <Link
                to={`/add-meal`}
                className="flex items-center space-x-1.5 text-xs font-semibold text-brand-600 hover:text-brand-700"
              >
                <PlusCircle className="w-4 h-4" />
                <span>Log This Food</span>
              </Link>

              {food.isCustom && (
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleOpenEdit(food)}
                    className="p-1.5 text-slate-400 hover:text-slate-700 rounded-lg transition"
                    title="Edit custom food"
                  >
                    <Edit3 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(food)}
                    className="p-1.5 text-rose-400 hover:text-rose-600 rounded-lg transition"
                    title="Delete custom food"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      {filteredFoods.length === 0 && (
        <div className="bg-white rounded-2xl p-10 text-center border border-slate-200/80">
          <p className="text-slate-500 font-medium">No food items found matching "{search}".</p>
        </div>
      )}

      {/* Modal Dialog: Add / Edit Custom Food */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-lg text-slate-800">
                {editingFood ? 'Edit Custom Food' : 'Add Custom Food Item'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-600"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleModalSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">Food Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Protein Smoothie"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">Category</label>
                <input
                  type="text"
                  placeholder="e.g. Beverages, Snacks"
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Calories (kcal/100g) *</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    placeholder="150"
                    value={formData.caloriesPer100g}
                    onChange={(e) => setFormData({ ...formData, caloriesPer100g: e.target.value })}
                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Protein (g/100g) *</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    placeholder="20"
                    value={formData.proteinPer100g}
                    onChange={(e) => setFormData({ ...formData, proteinPer100g: e.target.value })}
                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Carbs (g/100g) *</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    placeholder="10"
                    value={formData.carbsPer100g}
                    onChange={(e) => setFormData({ ...formData, carbsPer100g: e.target.value })}
                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Fat (g/100g) *</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    placeholder="5"
                    value={formData.fatPer100g}
                    onChange={(e) => setFormData({ ...formData, fatPer100g: e.target.value })}
                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end space-x-3 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-slate-200 text-sm font-medium text-slate-600 hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-sm font-bold shadow-xs transition"
                >
                  {editingFood ? 'Save Changes' : 'Add Food'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
