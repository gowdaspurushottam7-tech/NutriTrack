# 🥗 NutriTrack — Dietary Calorie Intake & Nutrition Log System

> A full-stack nutrition tracking application with a **Java Swing desktop app** and a **React + Spring Boot web app** — both built from the same Java business logic.

---

## 📋 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Prerequisites](#3-prerequisites)
4. [Running the Application](#4-running-the-application)
5. [Mobile Access (Same Wi-Fi)](#5-mobile-access-same-wi-fi)
6. [API Documentation](#6-api-documentation)
7. [Project Structure](#7-project-structure)
8. [Key Java Classes](#8-key-java-classes)
9. [Features](#9-features)
10. [Technology Stack](#10-technology-stack)

---

## 1. Project Overview

NutriTrack helps users maintain a healthy diet by tracking daily food consumption.

| Mode | Technology | How to launch |
|------|-----------|---------------|
| **Desktop (Swing)** | Java Swing | `run.bat` or double-click `DietaryCalorieSystem.jar` |
| **Web (Browser)** | React + Spring Boot | `run-backend.bat` + `run-frontend.bat` |

---

## 2. Architecture

```
React Frontend (Vite · Tailwind CSS · Recharts)  →  port 5173
         ↓  REST API (HTTP/JSON)
Spring Boot Backend  →  port 8080
         ↓
NutritionService  →  NutritionCalculator
         ↓
FoodDatabase  →  FileManager
         ↓
data/  (users.dat · meals.dat · foods.dat)   ← same files used by the Swing app
```

Both the desktop app and the web app share the **same data files** in the `data/` folder.

---

## 3. Prerequisites

All tools are already included in the project folder — **no global installation needed**.

| Tool | Location | Version |
|------|----------|---------|
| Oracle JDK | `oracleJdk-27/` | 27 |
| Apache Maven | `maven/` | 3.9.6 |
| Node.js | `nodejs/` | 20.18.0 |

> **Tip:** If you have JDK and Node.js installed globally, the batch scripts still work — they prefer the bundled versions.

---

## 4. Running the Application

### Option A — Desktop (Java Swing)

```batch
run.bat
```
Or just double-click `DietaryCalorieSystem.jar`.

---

### Option B — Web Application (Browser / Mobile)

You need **two terminals** running at the same time:

#### Terminal 1 — Start the Backend

```batch
run-backend.bat
```

Wait until you see:
```
Started NutriTrackWebApplication in X.XXX seconds
```

#### Terminal 2 — Start the Frontend

```batch
run-frontend.bat
```

Wait until you see:
```
  ➜  Local:   http://localhost:5173/
```

#### Open the App

Open your browser and navigate to: **http://localhost:5173**

---

## 5. Mobile Access (Same Wi-Fi)

To open NutriTrack on your phone or tablet:

1. Connect both your laptop and your phone to the **same Wi-Fi network**.

2. Find your laptop's local IP address:
   - Open Command Prompt (`cmd`)
   - Run: `ipconfig`
   - Look for **IPv4 Address** under your active network adapter (e.g., `192.168.1.105`)

3. On your phone's browser, open: `http://192.168.1.105:5173`

4. **If the page doesn't load**, allow the ports through Windows Firewall:
   ```batch
   REM Run as Administrator
   netsh advfirewall firewall add rule name="NutriTrack Frontend" dir=in action=allow protocol=TCP localport=5173
   netsh advfirewall firewall add rule name="NutriTrack Backend"  dir=in action=allow protocol=TCP localport=8080
   ```

---

## 6. API Documentation

Base URL: `http://localhost:8080`

All responses follow this format:
```json
{
  "success": true,
  "message": "OK",
  "data": { ... },
  "timestamp": "2024-01-01T12:00:00"
}
```

### Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard` | Full dashboard data: calories, macros, recent meals, weekly chart |

### Foods

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/foods` | List all foods (optional `?query=chicken`) |
| GET | `/api/foods/{id}` | Get a single food item |
| POST | `/api/foods` | Create a custom food |
| PUT | `/api/foods/{id}` | Update a custom food |
| DELETE | `/api/foods/{id}` | Delete a custom food |

**Food object:**
```json
{
  "id": "f1",
  "name": "Chicken Breast",
  "caloriesPerGram": 1.65,
  "proteinPerGram": 0.31,
  "carbsPerGram": 0.0,
  "fatPerGram": 0.036,
  "category": "Protein"
}
```

### Meals

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/meals` | List meals (optional `?date=2024-01-01&type=Breakfast`) |
| POST | `/api/meals` | Add a meal entry |
| PUT | `/api/meals/{id}` | Update a meal entry |
| DELETE | `/api/meals/{id}` | Delete a meal entry |
| GET | `/api/history` | History (optional `?date=2024-01-01&type=Lunch`) |

**Add meal request body:**
```json
{
  "foodId": "f1",
  "mealType": "Breakfast",
  "quantityGrams": 150,
  "date": "2024-01-01"
}
```

### Reports

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/daily` | Today's nutrition summary |
| GET | `/api/reports/weekly` | Last 7 days summary + daily map |
| GET | `/api/reports/export/csv` | Download CSV report |
| GET | `/api/reports/export/txt` | Download TXT report |

### Profile

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/profile` | Get user profile + BMI + BMR |
| PUT | `/api/profile` | Update user profile |

---

## 7. Project Structure

```
java project/
│
├── src/main/java/com/nutrition/
│   ├── NutriTrackWebApplication.java   ← Spring Boot entry point
│   │
│   ├── controller/                     ← REST API controllers
│   │   ├── DashboardController.java
│   │   ├── FoodController.java
│   │   ├── MealController.java
│   │   ├── ReportController.java
│   │   ├── ProfileController.java
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── dto/                            ← Data Transfer Objects
│   │   ├── ApiResponse.java
│   │   ├── DashboardDTO.java
│   │   ├── MealRequestDTO.java
│   │   └── ProfileDTO.java
│   │
│   ├── model/                          ← Existing Java models (unchanged)
│   │   ├── User.java
│   │   ├── Food.java
│   │   └── MealEntry.java
│   │
│   ├── service/                        ← Business logic (unchanged)
│   │   ├── INutritionService.java
│   │   ├── NutritionService.java
│   │   └── NutritionCalculator.java
│   │
│   ├── storage/                        ← File I/O (unchanged)
│   │   ├── FileManager.java
│   │   └── FoodDatabase.java
│   │
│   ├── report/                         ← Report generation (unchanged)
│   │   └── NutritionReport.java
│   │
│   └── ui/                             ← Java Swing UI (unchanged)
│       ├── MainFrame.java
│       └── ...
│
├── frontend/                           ← React web frontend
│   ├── src/
│   │   ├── api/client.js               ← All API call functions
│   │   ├── components/
│   │   │   ├── Sidebar.jsx
│   │   │   ├── Navbar.jsx
│   │   │   ├── BottomNav.jsx
│   │   │   ├── MetricCard.jsx
│   │   │   └── NotificationToast.jsx
│   │   ├── pages/
│   │   │   ├── DashboardPage.jsx
│   │   │   ├── AddMealPage.jsx
│   │   │   ├── FoodDatabasePage.jsx
│   │   │   ├── HistoryPage.jsx
│   │   │   ├── ReportsPage.jsx
│   │   │   ├── ProfilePage.jsx
│   │   │   └── SettingsPage.jsx
│   │   ├── App.jsx                     ← React Router setup
│   │   └── main.jsx                    ← React entry point
│   ├── package.json
│   └── vite.config.js
│
├── data/                               ← Shared local storage
│   ├── users.dat
│   ├── meals.dat
│   └── foods.dat
│
├── oracleJdk-27/                       ← Bundled JDK
├── maven/                              ← Bundled Maven
├── nodejs/                             ← Bundled Node.js
│
├── run.bat                             ← Launch desktop Swing app
├── run-backend.bat                     ← Launch Spring Boot backend
├── run-frontend.bat                    ← Launch React frontend
├── pom.xml                             ← Maven build file
└── DietaryCalorieSystem.jar            ← Compiled desktop app
```

---

## 8. Key Java Classes

| Class | Package | Purpose |
|-------|---------|---------|
| `User` | `model` | User profile data (name, age, height, weight, goals) |
| `Food` | `model` | Food item with nutrition per gram |
| `MealEntry` | `model` | A logged food entry with quantity and date |
| `FoodDatabase` | `storage` | In-memory food catalog with search |
| `FileManager` | `storage` | Serializes/deserializes data to `data/*.dat` files |
| `NutritionCalculator` | `service` | Calculates calories, protein, carbs, fat |
| `NutritionService` | `service` | Orchestrates all business operations |
| `NutritionReport` | `report` | Generates daily/weekly text reports |

---

## 9. Features

### Web Application
- 📊 **Dashboard** — Calorie ring, macro cards, weekly bar chart, recent meals
- ➕ **Add Meal** — Search food, select meal type, enter grams, see live nutrition preview
- 🍎 **Food Database** — Browse, search, filter, add/edit custom foods
- 📅 **History** — Navigate by date, filter by meal type, desktop table + mobile cards
- 📈 **Reports** — Daily summary + 7-day calorie/protein charts + CSV/TXT export
- 👤 **Profile** — Edit personal info, set nutrition goals, auto-calculate from BMR
- ⚙️ **Settings** — App info, server URLs, mobile access guide, data storage info

### Desktop App (Swing)
- All the above features in a native Java Swing window
- Works fully offline
- Same data files shared with web app

---

## 10. Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Desktop GUI | Java Swing | JDK 17+ |
| Backend | Spring Boot | 3.3.5 |
| Backend Language | Java | 17 (runs on JDK 27) |
| REST API | Spring Web (MVC) | 3.3.5 |
| Frontend | React | 18 |
| Frontend Build | Vite | 5 |
| CSS Framework | Tailwind CSS | 3 |
| Charts | Recharts | 2 |
| Icons | Lucide React | latest |
| Routing | React Router DOM | 6 |
| Data Storage | Java File I/O | — |
| Build Tool | Apache Maven | 3.9.6 |
| Node.js | Node.js | 20.18.0 |

---

*Built as a B.Tech CSE (IoT) 3rd-semester academic project.*
