"use client";

import React, { createContext, useCallback, useContext, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { CheckCircle, XCircle } from "lucide-react";

interface ToastMessage {
  id: number;
  message: string;
  type: "success" | "error";
}

interface ToastContextValue {
  toast: (message: string, type?: "success" | "error") => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

let toastId = 0;

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const removeToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const toast = useCallback(
    (message: string, type: "success" | "error" = "success") => {
      const id = ++toastId;
      setToasts((prev) => [...prev, { id, message, type }]);
      setTimeout(() => removeToast(id), 3000);
    },
    [removeToast]
  );

  return (
    <ToastContext.Provider value={{ toast }}>
      {children}
      <div
        style={{
          position: "fixed",
          bottom: "24px",
          right: "24px",
          zIndex: 9999,
          display: "flex",
          flexDirection: "column",
          gap: "8px",
          pointerEvents: "none",
        }}
      >
        <AnimatePresence>
          {toasts.map((t) => (
            <motion.div
              key={t.id}
              initial={{ opacity: 0, x: 80 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 80 }}
              transition={{ type: "spring", stiffness: 400, damping: 30 }}
              onClick={() => removeToast(t.id)}
              style={{
                pointerEvents: "auto",
                display: "flex",
                alignItems: "center",
                gap: "10px",
                padding: "14px 20px",
                borderRadius: "12px",
                background: "#FFFFFF",
                boxShadow: "0 8px 30px rgba(0, 0, 0, 0.12)",
                fontFamily: "'DM Sans', sans-serif",
                fontSize: "0.9rem",
                color: "#0F1A14",
                cursor: "pointer",
                minWidth: "260px",
                maxWidth: "380px",
              }}
            >
              {t.type === "success" ? (
                <CheckCircle size={20} color="#0D6E4F" strokeWidth={2.5} />
              ) : (
                <XCircle size={20} color="#EF4444" strokeWidth={2.5} />
              )}
              <span style={{ flex: 1 }}>{t.message}</span>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
    </ToastContext.Provider>
  );
}

export function useToast(): ToastContextValue {
  const ctx = useContext(ToastContext);
  if (!ctx) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return ctx;
}
