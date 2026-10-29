import React from 'react';
import { CheckCircle2, AlertCircle, X } from 'lucide-react';

export default function NotificationToast({ notification, onClose }) {
  if (!notification) return null;

  const isSuccess = notification.type === 'success';

  return (
    <div className="fixed top-4 right-4 z-50 max-w-sm w-full mx-auto px-4 pointer-events-none transition-all duration-300">
      <div
        className={`pointer-events-auto rounded-2xl p-4 shadow-lg border flex items-start space-x-3 backdrop-blur-md ${
          isSuccess
            ? 'bg-emerald-50/95 border-emerald-200 text-emerald-900'
            : 'bg-rose-50/95 border-rose-200 text-rose-900'
        }`}
      >
        <span className="shrink-0 mt-0.5">
          {isSuccess ? (
            <CheckCircle2 className="w-5 h-5 text-emerald-600" />
          ) : (
            <AlertCircle className="w-5 h-5 text-rose-600" />
          )}
        </span>
        <div className="flex-1 text-sm font-medium">
          {notification.message}
        </div>
        <button
          onClick={onClose}
          className="shrink-0 p-1 text-slate-400 hover:text-slate-700 rounded-lg"
        >
          <X className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
