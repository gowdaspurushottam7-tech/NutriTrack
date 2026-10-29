import React, { useState, useEffect, useCallback } from 'react';
import { fetchProfile, updateProfile } from '../api/client';
import {
  User, Ruler, Weight, Target, Activity, Flame,
  Dumbbell, Wheat, Droplets, Save, RefreshCw, Calculator
} from 'lucide-react';

// ── Mifflin-St Jeor BMR calculator ───────────────────────────────────────────
function calcBMR(weight, height, age, gender) {
  if (!weight || !height || !age) return 0;
  const base = 10 * weight + 6.25 * height - 5 * age;
  return gender?.toLowerCase() === 'female' ? base - 161 : base + 5;
}

function calcBMI(weight, height) {
  if (!weight || !height) return 0;
  const hm = height / 100;
  return weight / (hm * hm);
}

function bmiCategory(bmi) {
  if (bmi < 18.5) return { label: 'Underweight', color: 'text-sky-600 bg-sky-50' };
  if (bmi < 25)   return { label: 'Normal',       color: 'text-emerald-600 bg-emerald-50' };
  if (bmi < 30)   return { label: 'Overweight',   color: 'text-amber-600 bg-amber-50' };
  return               { label: 'Obese',          color: 'text-red-600 bg-red-50' };
}

// ── Input Field ───────────────────────────────────────────────────────────────
function Field({ label, name, type = 'text', value, onChange, min, max, step, icon: Icon, unit, readOnly }) {
  return (
    <div>
      <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">
        {label}
      </label>
      <div className="relative">
        {Icon && (
          <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
            <Icon size={15} className="text-slate-400" />
          </div>
        )}
        <input
          type={type}
          name={name}
          value={value ?? ''}
          onChange={onChange}
          min={min}
          max={max}
          step={step}
          readOnly={readOnly}
          className={`w-full ${Icon ? 'pl-9' : 'pl-3.5'} ${unit ? 'pr-12' : 'pr-3.5'}
            py-2.5 rounded-xl border text-sm text-slate-800 font-medium transition-colors
            ${readOnly
              ? 'bg-slate-50 border-slate-200 text-slate-500 cursor-default'
              : 'bg-white border-slate-200 hover:border-emerald-400 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 outline-none'
            }`}
        />
        {unit && (
          <span className="absolute inset-y-0 right-3 flex items-center text-xs text-slate-400 font-medium pointer-events-none">
            {unit}
          </span>
        )}
      </div>
    </div>
  );
}

function SelectField({ label, name, value, onChange, options, icon: Icon }) {
  return (
    <div>
      <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">
        {label}
      </label>
      <div className="relative">
        {Icon && (
          <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
            <Icon size={15} className="text-slate-400" />
          </div>
        )}
        <select
          name={name}
          value={value ?? ''}
          onChange={onChange}
          className="w-full pl-9 pr-8 py-2.5 rounded-xl border border-slate-200 bg-white text-sm text-slate-800 font-medium
            hover:border-emerald-400 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 outline-none appearance-none transition-colors"
        >
          {options.map(o => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
        <div className="absolute inset-y-0 right-3 flex items-center pointer-events-none">
          <svg className="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </div>
      </div>
    </div>
  );
}

// ── BiometricCard ─────────────────────────────────────────────────────────────
function BiometricCard({ bmi, bmr, gender, weight, height, age }) {
  if (!bmi && !bmr) return null;
  const { label, color } = bmiCategory(bmi);
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-4">
      <h3 className="font-semibold text-slate-700 flex items-center gap-2">
        <Activity size={16} className="text-emerald-500" /> Health Estimates
        <span className="text-xs text-slate-400 font-normal ml-auto">auto-calculated</span>
      </h3>
      <div className="grid grid-cols-2 gap-4">
        {/* BMI */}
        <div className="bg-slate-50 rounded-xl p-4">
          <p className="text-xs text-slate-500 mb-1">BMI</p>
          <p className="text-2xl font-bold text-slate-800">{bmi ? bmi.toFixed(1) : '—'}</p>
          {bmi > 0 && (
            <span className={`text-xs font-semibold px-2 py-0.5 rounded-full mt-1 inline-block ${color}`}>
              {label}
            </span>
          )}
        </div>
        {/* BMR */}
        <div className="bg-slate-50 rounded-xl p-4">
          <p className="text-xs text-slate-500 mb-1">BMR</p>
          <p className="text-2xl font-bold text-slate-800">{bmr ? Math.round(bmr) : '—'}</p>
          <p className="text-xs text-slate-400 mt-1">kcal / day</p>
        </div>
      </div>
      {bmr > 0 && (
        <p className="text-xs text-slate-400">
          BMR = calories your body burns at rest (Mifflin-St Jeor).
          Your TDEE (lightly active) ≈ <span className="font-semibold text-slate-600">{Math.round(bmr * 1.375)} kcal</span>
        </p>
      )}
    </div>
  );
}

// ── Main Component ────────────────────────────────────────────────────────────
export default function ProfilePage({ showToast, onProfileUpdate }) {
  const [form, setForm]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving]   = useState(false);
  const [dirty, setDirty]     = useState(false);

  // Derived values
  const bmi = form ? calcBMI(Number(form.weight), Number(form.height)) : 0;
  const bmr = form ? calcBMR(Number(form.weight), Number(form.height), Number(form.age), form.gender) : 0;

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetchProfile();
      setForm(res?.data ?? res);
    } catch (e) {
      showToast?.('Failed to load profile', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => { load(); }, [load]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
    setDirty(true);
  };

  // Auto-calculate goals from BMR
  const autoCalcGoals = () => {
    if (!bmr) { showToast?.('Fill in weight, height, age and gender first', 'error'); return; }
    const tdee    = Math.round(bmr * 1.375);
    const protein = Math.round((tdee * 0.25) / 4);
    const carbs   = Math.round((tdee * 0.50) / 4);
    const fat     = Math.round((tdee * 0.25) / 9);
    setForm(f => ({
      ...f,
      dailyCalorieGoal: tdee,
      dailyProteinGoal: protein,
      dailyCarbGoal:    carbs,
      dailyFatGoal:     fat,
    }));
    setDirty(true);
    showToast?.('Goals auto-calculated from your TDEE ✓', 'success');
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const updated = await updateProfile(form);
      const data = updated?.data ?? updated;
      setForm(data);
      onProfileUpdate?.(data);
      setDirty(false);
      showToast?.('Profile saved successfully ✓', 'success');
    } catch (err) {
      showToast?.('Failed to save profile', 'error');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-500 border-t-transparent" />
    </div>
  );

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">My Profile</h1>
          <p className="text-slate-500 text-sm mt-0.5">Manage your personal info and nutrition goals</p>
        </div>
        <div className="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center">
          <User size={22} className="text-emerald-600" />
        </div>
      </div>

      <form onSubmit={handleSave} className="space-y-6">
        {/* Personal Info */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-5">
          <h3 className="font-semibold text-slate-700 border-b border-slate-100 pb-3">Personal Information</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Field label="Full Name"    name="name"   value={form?.name}   onChange={handleChange} icon={User} />
            <Field label="Age"          name="age"    value={form?.age}    onChange={handleChange} type="number" min={1} max={120} icon={Activity} unit="yrs" />
            <SelectField
              label="Gender" name="gender" value={form?.gender} onChange={handleChange} icon={User}
              options={[
                { value: 'male',   label: 'Male' },
                { value: 'female', label: 'Female' },
                { value: 'other',  label: 'Other / Prefer not to say' },
              ]}
            />
            <Field label="Height"  name="height" value={form?.height} onChange={handleChange} type="number" min={50} max={250} step={0.1} icon={Ruler}  unit="cm" />
            <Field label="Weight"  name="weight" value={form?.weight} onChange={handleChange} type="number" min={20} max={500} step={0.1} icon={Weight} unit="kg" />
          </div>
        </div>

        {/* Health estimates */}
        <BiometricCard bmi={bmi} bmr={bmr} gender={form?.gender} weight={form?.weight} height={form?.height} age={form?.age} />

        {/* Nutrition Goals */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 space-y-5">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <h3 className="font-semibold text-slate-700">Daily Nutrition Goals</h3>
            <button
              type="button"
              onClick={autoCalcGoals}
              className="flex items-center gap-1.5 text-xs font-semibold text-emerald-600 hover:text-emerald-700 bg-emerald-50 hover:bg-emerald-100 px-3 py-1.5 rounded-lg transition-colors"
            >
              <Calculator size={13} /> Auto-Calculate
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Field label="Daily Calorie Goal" name="dailyCalorieGoal" value={form?.dailyCalorieGoal} onChange={handleChange} type="number" min={500} max={10000} icon={Flame}    unit="kcal" />
            <Field label="Protein Goal"       name="dailyProteinGoal" value={form?.dailyProteinGoal} onChange={handleChange} type="number" min={0}   max={500}   icon={Dumbbell} unit="g" />
            <Field label="Carbohydrate Goal"  name="dailyCarbGoal"    value={form?.dailyCarbGoal}    onChange={handleChange} type="number" min={0}   max={1000}  icon={Wheat}    unit="g" />
            <Field label="Fat Goal"           name="dailyFatGoal"     value={form?.dailyFatGoal}     onChange={handleChange} type="number" min={0}   max={300}   icon={Droplets} unit="g" />
          </div>
          <p className="text-xs text-slate-400">
            💡 Click <strong>Auto-Calculate</strong> to set goals based on your TDEE
            (BMR × 1.375 for light activity) with a 50/25/25 carb/protein/fat split.
          </p>
        </div>

        {/* Save button */}
        <div className="flex flex-col sm:flex-row gap-3">
          <button
            type="submit"
            disabled={saving || !dirty}
            className="flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-700 disabled:bg-emerald-300
              text-white px-8 py-3 rounded-xl text-sm font-semibold transition-colors shadow-sm"
          >
            {saving ? <><RefreshCw size={15} className="animate-spin" /> Saving…</> : <><Save size={15} /> Save Profile</>}
          </button>
          {dirty && (
            <button
              type="button"
              onClick={() => { load(); setDirty(false); }}
              className="flex items-center justify-center gap-2 bg-slate-100 hover:bg-slate-200 text-slate-600 px-6 py-3 rounded-xl text-sm font-semibold transition-colors"
            >
              <RefreshCw size={15} /> Discard Changes
            </button>
          )}
        </div>
      </form>
    </div>
  );
}
