"use client";

import React from "react";
import clsx from "clsx";

interface InputOption {
  value: string;
  label: string;
}

interface InputProps {
  label?: string;
  type?: string;
  value?: string;
  onChange?: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => void;
  placeholder?: string;
  error?: string;
  required?: boolean;
  name?: string;
  textarea?: boolean;
  rows?: number;
  disabled?: boolean;
  className?: string;
  options?: InputOption[];
}

const baseInputStyle: React.CSSProperties = {
  width: "100%",
  background: "#FFFFFF",
  border: "1px solid #E5E7EB",
  borderRadius: "12px",
  padding: "14px 16px",
  fontSize: "1rem",
  fontFamily: "'DM Sans', sans-serif",
  color: "#0F1A14",
  outline: "none",
  transition: "border-color 0.2s, box-shadow 0.2s",
};

const focusStyle: React.CSSProperties = {
  borderColor: "#0D6E4F",
  boxShadow: "0 0 0 3px rgba(13, 110, 79, 0.15)",
};

const errorBorderStyle: React.CSSProperties = {
  borderColor: "#EF4444",
};

export default function Input({
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  error,
  required = false,
  name,
  textarea = false,
  rows = 4,
  disabled = false,
  className,
  options,
}: InputProps) {
  const [focused, setFocused] = React.useState(false);

  const computedStyle: React.CSSProperties = {
    ...baseInputStyle,
    ...(focused && !error ? focusStyle : {}),
    ...(error ? errorBorderStyle : {}),
    ...(disabled ? { opacity: 0.6, cursor: "not-allowed" } : {}),
  };

  const handleFocus = () => setFocused(true);
  const handleBlur = () => setFocused(false);

  const sharedProps = {
    name,
    value,
    onChange,
    placeholder,
    required,
    disabled,
    onFocus: handleFocus,
    onBlur: handleBlur,
    style: computedStyle,
  };

  const renderField = () => {
    if (options && options.length > 0) {
      return (
        <select {...sharedProps} onChange={onChange as React.ChangeEventHandler<HTMLSelectElement>}>
          {placeholder && (
            <option value="" disabled>
              {placeholder}
            </option>
          )}
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      );
    }

    if (textarea) {
      return (
        <textarea
          {...sharedProps}
          rows={rows}
          onChange={onChange as React.ChangeEventHandler<HTMLTextAreaElement>}
          style={{ ...computedStyle, resize: "vertical" }}
        />
      );
    }

    return (
      <input
        {...sharedProps}
        type={type}
        onChange={onChange as React.ChangeEventHandler<HTMLInputElement>}
      />
    );
  };

  return (
    <div className={clsx("flex flex-col gap-1.5", className)}>
      {label && (
        <label
          htmlFor={name}
          style={{
            fontFamily: "'DM Sans', sans-serif",
            fontSize: "0.85rem",
            color: "#6B7280",
            fontWeight: 500,
          }}
        >
          {label}
          {required && <span style={{ color: "#EF4444", marginLeft: "2px" }}>*</span>}
        </label>
      )}
      {renderField()}
      {error && (
        <span
          style={{
            fontFamily: "'DM Sans', sans-serif",
            fontSize: "0.8rem",
            color: "#EF4444",
          }}
        >
          {error}
        </span>
      )}
    </div>
  );
}
