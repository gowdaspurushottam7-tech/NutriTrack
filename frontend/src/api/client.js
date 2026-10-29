/**
 * NutriTrack REST API Client.
 * Automatically resolves the backend server IP so that both desktop browsers
 * and mobile phones connected via local Wi-Fi connect seamlessly without hardcoded localhost.
 */

const getApiBaseUrl = () => {
  if (import.meta.env.VITE_API_URL) {
    return import.meta.env.VITE_API_URL;
  }
  const hostname = window.location.hostname || 'localhost';
  return `http://${hostname}:8080/api`;
};

export const API_BASE_URL = getApiBaseUrl();

async function request(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  try {
    const res = await fetch(url, config);
    if (!res.ok) {
      let errorMsg = `Server error (${res.status})`;
      try {
        const errorJson = await res.json();
        if (errorJson.message) errorMsg = errorJson.message;
      } catch (_) {}
      throw new Error(errorMsg);
    }
    const json = await res.json();
    return json.data;
  } catch (err) {
    console.error(`API request failed [${url}]:`, err.message);
    throw err;
  }
}

// --- Dashboard API ---
export const fetchDashboard = () => request('/dashboard');

// --- Food Database API ---
export const fetchFoods = (query = '') => {
  const q = query ? `?query=${encodeURIComponent(query)}` : '';
  return request(`/foods${q}`);
};

export const fetchFoodById = (id) => request(`/foods/${id}`);

export const createCustomFood = (foodData) => request('/foods', {
  method: 'POST',
  body: JSON.stringify(foodData),
});

export const updateCustomFood = (id, foodData) => request(`/foods/${id}`, {
  method: 'PUT',
  body: JSON.stringify(foodData),
});

export const deleteCustomFood = (id) => request(`/foods/${id}`, {
  method: 'DELETE',
});

// --- Meals & History API ---
export const fetchMeals = (date = '', type = '') => {
  const params = new URLSearchParams();
  if (date) params.append('date', date);
  if (type) params.append('type', type);
  const q = params.toString() ? `?${params.toString()}` : '';
  return request(`/meals${q}`);
};

export const addMeal = (mealData) => request('/meals', {
  method: 'POST',
  body: JSON.stringify(mealData),
});

export const updateMeal = (id, mealData) => request(`/meals/${id}`, {
  method: 'PUT',
  body: JSON.stringify(mealData),
});

export const deleteMeal = (id) => request(`/meals/${id}`, {
  method: 'DELETE',
});

export const fetchHistory = (date = '', type = '') => {
  const params = new URLSearchParams();
  if (date) params.append('date', date);
  if (type) params.append('type', type);
  const q = params.toString() ? `?${params.toString()}` : '';
  return request(`/history${q}`);
};

// --- Reports API ---
export const fetchDailyReport = (date = '') => {
  const q = date ? `?date=${encodeURIComponent(date)}` : '';
  return request(`/reports/daily${q}`);
};

export const fetchWeeklyReport = (endDate = '') => {
  const q = endDate ? `?endDate=${encodeURIComponent(endDate)}` : '';
  return request(`/reports/weekly${q}`);
};

export const getExportCsvUrl = (type = 'weekly', date = '') => {
  return `${API_BASE_URL}/reports/export/csv?type=${type}${date ? `&date=${date}` : ''}`;
};

export const getExportTxtUrl = (type = 'weekly', date = '') => {
  return `${API_BASE_URL}/reports/export/txt?type=${type}${date ? `&date=${date}` : ''}`;
};

// --- Profile API ---
export const fetchProfile = () => request('/profile');

export const updateProfile = (profileData) => request('/profile', {
  method: 'PUT',
  body: JSON.stringify(profileData),
});
