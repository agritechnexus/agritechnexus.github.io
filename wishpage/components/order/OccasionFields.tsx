"use client";

import { motion } from "framer-motion";

interface OccasionFieldsProps {
  occasion: string;
  formData: Record<string, string | boolean>;
  setFormData: (data: Record<string, string | boolean>) => void;
}

const inputClass =
  "w-full px-4 py-3 border border-gray-200 rounded-xl bg-white text-text focus:border-emerald focus:ring-2 focus:ring-emerald/20 outline-none transition";
const labelClass = "block text-sm font-medium text-text mb-2";

export default function OccasionFields({
  occasion,
  formData,
  setFormData,
}: OccasionFieldsProps) {
  const update = (key: string, value: string | boolean) => {
    setFormData({ ...formData, [key]: value });
  };

  const renderInput = (
    key: string,
    label: string,
    type: string = "text",
    options?: { required?: boolean; placeholder?: string; readOnly?: boolean }
  ) => (
    <div>
      <label className={labelClass}>
        {label}
        {options?.required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <input
        type={type}
        value={(formData[key] as string) || ""}
        onChange={(e) => update(key, e.target.value)}
        placeholder={options?.placeholder || ""}
        readOnly={options?.readOnly}
        className={`${inputClass} ${options?.readOnly ? "bg-gray-50 cursor-not-allowed" : ""}`}
      />
    </div>
  );

  const renderSelect = (
    key: string,
    label: string,
    choices: string[],
    required?: boolean
  ) => (
    <div>
      <label className={labelClass}>
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <select
        value={(formData[key] as string) || ""}
        onChange={(e) => update(key, e.target.value)}
        className={inputClass}
      >
        <option value="">Select...</option>
        {choices.map((c) => (
          <option key={c} value={c}>
            {c}
          </option>
        ))}
      </select>
    </div>
  );

  const renderTextarea = (
    key: string,
    label: string,
    options?: { required?: boolean; placeholder?: string; rows?: number }
  ) => (
    <div className="md:col-span-2">
      <label className={labelClass}>
        {label}
        {options?.required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <textarea
        value={(formData[key] as string) || ""}
        onChange={(e) => update(key, e.target.value)}
        placeholder={options?.placeholder || ""}
        rows={options?.rows || 3}
        className={`${inputClass} resize-none`}
      />
    </div>
  );

  const renderToggle = (key: string, label: string) => (
    <div className="flex items-center gap-3">
      <button
        type="button"
        onClick={() => update(key, !formData[key])}
        className={`relative w-12 h-6 rounded-full transition-colors ${
          formData[key] ? "bg-emerald" : "bg-gray-300"
        }`}
      >
        <motion.div
          animate={{ x: formData[key] ? 24 : 2 }}
          transition={{ type: "spring", stiffness: 500, damping: 30 }}
          className="absolute top-1 w-4 h-4 bg-white rounded-full shadow"
        />
      </button>
      <span className="text-sm text-text">{label}</span>
    </div>
  );

  const renderOccasionFields = () => {
    switch (occasion) {
      case "birthday":
        return (
          <>
            {renderInput("recipientName", "Recipient Name", "text", { required: true, placeholder: "Who is the birthday for?" })}
            {renderInput("age", "Age (optional)", "number", { placeholder: "e.g. 25" })}
            {renderInput("dateOfBirth", "Date of Birth", "date")}
            {renderSelect("relationship", "Relationship", [
              "Friend",
              "Partner",
              "Parent",
              "Child",
              "Sibling",
              "Colleague",
              "Other",
            ])}
          </>
        );

      case "wedding":
        return (
          <>
            {renderInput("brideName", "Bride's Name", "text", { required: true })}
            {renderInput("groomName", "Groom's Name", "text", { required: true })}
            {renderInput("weddingDate", "Wedding Date", "date", { required: true })}
            {renderInput("venueName", "Venue Name", "text", { placeholder: "Optional" })}
            {renderTextarea("venueAddress", "Venue Address", { placeholder: "Full address (optional)" })}
            <div className="md:col-span-2">
              {renderToggle("rsvpEnabled", "Enable RSVP")}
            </div>
          </>
        );

      case "festival":
        return (
          <>
            {renderSelect(
              "festivalType",
              "Festival Type",
              ["Diwali", "Eid", "Christmas", "Ugadi", "Pongal", "Holi", "New Year"],
              true
            )}
            {renderInput("senderName", "Sender Name", "text", { required: true })}
            {renderInput("year", "Year", "text", { readOnly: true })}
          </>
        );

      case "baby-shower":
        return (
          <>
            {renderInput("parentNames", "Parent Name(s)", "text", { required: true, placeholder: "e.g. Priya & Rahul" })}
            {renderInput("expectedDate", "Expected Date", "date")}
            <div className="md:col-span-2">
              {renderToggle("genderReveal", "Include Gender Reveal")}
            </div>
            {formData.genderReveal &&
              renderSelect("gender", "Gender", ["Boy", "Girl"])}
            {renderSelect("theme", "Theme", [
              "Jungle Safari",
              "Floral Garden",
              "Minimal Elegant",
              "Royal",
            ])}
          </>
        );

      case "anniversary":
        return (
          <>
            {renderInput("coupleNames", "Couple Names", "text", { required: true, placeholder: "e.g. Arun & Meera" })}
            {renderInput("anniversaryNumber", "Anniversary Number", "text", { required: true, placeholder: "e.g. 25th" })}
            {renderInput("originalDate", "Original Date", "date")}
          </>
        );

      case "corporate":
        return (
          <>
            {renderInput("companyName", "Company Name", "text", { required: true })}
            {renderInput("employeeName", "Employee Name", "text", { required: true })}
            {renderSelect("eventType", "Event Type", [
              "Birthday",
              "Farewell",
              "Promotion",
              "Work Anniversary",
            ])}
          </>
        );

      default:
        // custom
        return (
          <>
            {renderInput("pageTitle", "Page Title", "text", { required: true, placeholder: "Give your page a title" })}
            {renderTextarea("description", "Description", { required: true, placeholder: "Describe what this page is about" })}
          </>
        );
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {renderOccasionFields()}

        {/* Common field for all occasions */}
        <div className="md:col-span-2">
          <label className={labelClass}>
            Your Name / From <span className="text-red-500 ml-1">*</span>
          </label>
          <input
            type="text"
            value={(formData.fromName as string) || ""}
            onChange={(e) => update("fromName", e.target.value)}
            placeholder="Your name or who this is from"
            className={inputClass}
          />
        </div>
      </div>
    </motion.div>
  );
}
