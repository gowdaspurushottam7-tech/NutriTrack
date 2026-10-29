import React, { useState } from 'react';
import {
  Settings, Info, Server, Monitor, Smartphone, Wifi,
  Database, Shield, GitBranch, ExternalLink, Copy, CheckCircle
} from 'lucide-react';

// ── Helpers ───────────────────────────────────────────────────────────────────
const API_BASE = (() => {
  const host = window.location.hostname;
  const isLocal = host === 'localhost' || host === '127.0.0.1';
  return isLocal ? 'http://localhost:8080' : `http://${host}:8080`;
})();

function InfoRow({ label, value, mono, copyable }) {
  const [copied, setCopied] = useState(false);
  const copy = () => {
    navigator.clipboard.writeText(value).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 1800);
    });
  };
  return (
    <div className="flex items-start justify-between py-3 border-b border-slate-50 last:border-0 gap-4">
      <span className="text-sm text-slate-500 shrink-0 min-w-[140px]">{label}</span>
      <div className="flex items-center gap-2">
        <span className={`text-sm font-medium text-slate-700 text-right break-all ${mono ? 'font-mono bg-slate-100 px-2 py-0.5 rounded text-xs' : ''}`}>
          {value}
        </span>
        {copyable && (
          <button onClick={copy} className="text-slate-400 hover:text-emerald-600 transition-colors shrink-0">
            {copied ? <CheckCircle size={14} className="text-emerald-500" /> : <Copy size={14} />}
          </button>
        )}
      </div>
    </div>
  );
}

function Card({ title, icon: Icon, iconColor = 'text-emerald-500', children }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
      <h3 className="font-semibold text-slate-700 flex items-center gap-2 mb-4 pb-3 border-b border-slate-100">
        <Icon size={16} className={iconColor} /> {title}
      </h3>
      {children}
    </div>
  );
}

// ── Main Component ────────────────────────────────────────────────────────────
export default function SettingsPage() {
  const frontendUrl = `http://${window.location.hostname}:5173`;
  const backendUrl  = API_BASE;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Settings</h1>
          <p className="text-slate-500 text-sm mt-0.5">Application information &amp; access details</p>
        </div>
        <div className="w-12 h-12 rounded-2xl bg-slate-100 flex items-center justify-center">
          <Settings size={22} className="text-slate-600" />
        </div>
      </div>

      {/* App info */}
      <Card title="Application Info" icon={Info}>
        <div>
          <InfoRow label="App Name"    value="NutriTrack" />
          <InfoRow label="Version"     value="1.0.0" />
          <InfoRow label="Description" value="Dietary Calorie Intake & Nutrition Log System" />
          <InfoRow label="Developer"   value="B.Tech CSE (IoT) Academic Project" />
          <InfoRow label="Tech Stack"  value="Java 17 · Spring Boot 3 · React 18 · Tailwind CSS 3" />
          <InfoRow label="Build Tool"  value="Apache Maven 3.9.6" />
        </div>
      </Card>

      {/* Server URLs */}
      <Card title="Server Addresses" icon={Server} iconColor="text-blue-500">
        <div>
          <InfoRow label="Frontend URL" value={frontendUrl}  mono copyable />
          <InfoRow label="Backend API"  value={backendUrl}   mono copyable />
          <InfoRow label="API Health"   value={`${backendUrl}/api/dashboard`} mono copyable />
        </div>
        <a
          href={`${backendUrl}/api/dashboard`}
          target="_blank" rel="noreferrer"
          className="mt-4 inline-flex items-center gap-1.5 text-xs text-emerald-600 hover:text-emerald-700 font-semibold"
        >
          <ExternalLink size={13} /> Test API connection
        </a>
      </Card>

      {/* Mobile access guide */}
      <Card title="Mobile Device Access" icon={Smartphone} iconColor="text-violet-500">
        <ol className="space-y-3 text-sm text-slate-600 list-none">
          <li className="flex gap-3">
            <span className="w-6 h-6 rounded-full bg-violet-100 text-violet-600 flex items-center justify-center text-xs font-bold shrink-0">1</span>
            Make sure your laptop and phone are on the <strong>same Wi-Fi network</strong>.
          </li>
          <li className="flex gap-3">
            <span className="w-6 h-6 rounded-full bg-violet-100 text-violet-600 flex items-center justify-center text-xs font-bold shrink-0">2</span>
            Find your laptop's IP: open <code className="bg-slate-100 px-1.5 py-0.5 rounded text-xs">cmd</code> → type <code className="bg-slate-100 px-1.5 py-0.5 rounded text-xs">ipconfig</code> → look for <em>IPv4 Address</em>.
          </li>
          <li className="flex gap-3">
            <span className="w-6 h-6 rounded-full bg-violet-100 text-violet-600 flex items-center justify-center text-xs font-bold shrink-0">3</span>
            On your phone, open a browser and go to <code className="bg-slate-100 px-1.5 py-0.5 rounded text-xs">http://YOUR_IP:5173</code>
          </li>
          <li className="flex gap-3">
            <span className="w-6 h-6 rounded-full bg-violet-100 text-violet-600 flex items-center justify-center text-xs font-bold shrink-0">4</span>
            Allow ports <strong>5173</strong> and <strong>8080</strong> through Windows Firewall if prompted.
          </li>
        </ol>

        {/* Windows Firewall instructions */}
        <div className="mt-4 p-4 bg-amber-50 border border-amber-100 rounded-xl text-xs text-amber-800 space-y-1">
          <p className="font-semibold flex items-center gap-1"><Wifi size={12} /> Windows Firewall Quick Fix</p>
          <p>Open PowerShell as Administrator and run:</p>
          <code className="block bg-amber-100 rounded p-2 mt-1 font-mono text-amber-900 text-xs leading-5">
            netsh advfirewall firewall add rule name="NutriTrack Frontend" dir=in action=allow protocol=TCP localport=5173<br />
            netsh advfirewall firewall add rule name="NutriTrack Backend" dir=in action=allow protocol=TCP localport=8080
          </code>
        </div>
      </Card>

      {/* Data storage */}
      <Card title="Data Storage" icon={Database} iconColor="text-amber-500">
        <div>
          <InfoRow label="Storage Type" value="Local file system (Java serialization)" />
          <InfoRow label="Users file"   value="data/users.dat"  mono />
          <InfoRow label="Meals file"   value="data/meals.dat"  mono />
          <InfoRow label="Foods file"   value="data/foods.dat"  mono />
          <InfoRow label="Backup"       value="Copy the data/ folder to back up all records" />
        </div>
        <p className="mt-3 text-xs text-slate-400">
          All data is stored locally on your computer. No internet connection is required to use NutriTrack.
        </p>
      </Card>

      {/* Architecture */}
      <Card title="Architecture" icon={GitBranch} iconColor="text-slate-500">
        <div className="bg-slate-50 rounded-xl p-4 font-mono text-xs text-slate-600 space-y-0.5 leading-6">
          <p>React Frontend (Vite · Tailwind · Recharts)</p>
          <p className="text-slate-400 pl-4">↓  REST API (HTTP/JSON)</p>
          <p>Spring Boot Backend (port 8080)</p>
          <p className="text-slate-400 pl-4">↓</p>
          <p>NutritionService · NutritionCalculator</p>
          <p className="text-slate-400 pl-4">↓</p>
          <p>FoodDatabase · FileManager</p>
          <p className="text-slate-400 pl-4">↓</p>
          <p>data/ (users.dat · meals.dat · foods.dat)</p>
        </div>
        <p className="text-xs text-slate-400 mt-3 flex items-center gap-1">
          <Shield size={12} /> The original Java Swing desktop app is fully preserved and can still be launched via <code className="bg-slate-100 px-1 rounded">run.bat</code>.
        </p>
      </Card>

      {/* Displays */}
      <Card title="Supported Devices" icon={Monitor} iconColor="text-emerald-500">
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs text-slate-600">
          {[
            '📱 360px mobile',
            '📱 390px iPhone',
            '📱 430px large phone',
            '📟 768px tablet',
            '📟 1024px tablet',
            '🖥️ 1440px desktop',
          ].map(d => (
            <div key={d} className="bg-slate-50 rounded-xl px-3 py-2.5 font-medium">{d}</div>
          ))}
        </div>
      </Card>
    </div>
  );
}
