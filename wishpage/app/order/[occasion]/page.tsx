"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import { ArrowLeft } from "lucide-react";

import StepIndicator from "@/components/order/StepIndicator";
import OccasionFields from "@/components/order/OccasionFields";
import PhotoUploader from "@/components/order/PhotoUploader";
import ColorThemePicker from "@/components/order/ColorThemePicker";
import MusicSelector from "@/components/order/MusicSelector";
import PlanSelector from "@/components/order/PlanSelector";

const PLAN_PRICES: Record<string, number> = {
  basic: 199,
  premium: 499,
  deluxe: 999,
};

const MAX_PHOTOS: Record<string, number> = {
  basic: 5,
  premium: 15,
  deluxe: 100,
};

const inputClass =
  "w-full px-4 py-3 border border-gray-200 rounded-xl bg-white text-text focus:border-emerald focus:ring-2 focus:ring-emerald/20 outline-none transition";
const labelClass = "block text-sm font-medium text-text mb-2";

// Required fields per occasion for Step 1 validation
const REQUIRED_FIELDS: Record<string, string[]> = {
  birthday: ["recipientName", "fromName"],
  wedding: ["brideName", "groomName", "weddingDate", "fromName"],
  festival: ["festivalType", "senderName", "fromName"],
  "baby-shower": ["parentNames", "fromName"],
  anniversary: ["coupleNames", "anniversaryNumber", "fromName"],
  corporate: ["companyName", "employeeName", "fromName"],
  custom: ["pageTitle", "description", "fromName"],
};

export default function OrderPage() {
  const params = useParams();
  const router = useRouter();
  const occasion = (params.occasion as string) || "custom";

  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [formData, setFormData] = useState<Record<string, string | boolean>>(
    occasion === "festival" ? { year: "2026" } : {}
  );
  const [photos, setPhotos] = useState<File[]>([]);
  const [selectedColor, setSelectedColor] = useState("#0D6E4F");
  const [selectedMusic, setSelectedMusic] = useState("No Music");
  const [selectedPlan, setSelectedPlan] = useState("premium");
  const [message, setMessage] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [agreedToTerms, setAgreedToTerms] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isShaking, setIsShaking] = useState(false);

  // Reset festival year when occasion changes
  useEffect(() => {
    if (occasion === "festival" && !formData.year) {
      setFormData((prev) => ({ ...prev, year: "2026" }));
    }
  }, [occasion, formData.year]);

  const formatOccasion = (occ: string) =>
    occ.replace(/-/g, " ").replace(/\b\w/g, (c) => c.toUpperCase());

  const triggerShake = () => {
    setIsShaking(true);
    setTimeout(() => setIsShaking(false), 500);
  };

  const validateStep1 = (): boolean => {
    const required = REQUIRED_FIELDS[occasion] || REQUIRED_FIELDS.custom;
    const newErrors: Record<string, string> = {};
    required.forEach((field) => {
      if (!formData[field] || (typeof formData[field] === "string" && !(formData[field] as string).trim())) {
        newErrors[field] = "This field is required";
      }
    });
    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) {
      triggerShake();
      return false;
    }
    return true;
  };

  const validateStep2 = (): boolean => {
    const newErrors: Record<string, string> = {};
    if (!message.trim()) {
      newErrors.message = "Please write a message";
    }
    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) {
      triggerShake();
      return false;
    }
    return true;
  };

  const goNext = () => {
    if (step === 1 && validateStep1()) {
      setStep(2);
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else if (step === 2 && validateStep2()) {
      setStep(3);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  };

  const goBack = () => {
    if (step === 2) setStep(1);
    else if (step === 3) setStep(2);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handlePay = () => {
    alert("Payment integration coming soon! Redirecting...");
    router.push("/status/demo-order-123");
  };

  // Get recipient name from formData for summary
  const getRecipient = () => {
    switch (occasion) {
      case "birthday":
        return (formData.recipientName as string) || "-";
      case "wedding":
        return `${formData.brideName || ""} & ${formData.groomName || ""}`.trim() || "-";
      case "festival":
        return (formData.festivalType as string) || "-";
      case "baby-shower":
        return (formData.parentNames as string) || "-";
      case "anniversary":
        return (formData.coupleNames as string) || "-";
      case "corporate":
        return (formData.employeeName as string) || "-";
      default:
        return (formData.pageTitle as string) || "-";
    }
  };

  const slideVariants = {
    enter: { x: 50, opacity: 0 },
    center: { x: 0, opacity: 1 },
    exit: { x: -50, opacity: 0 },
  };

  return (
    <div className="min-h-screen bg-cream py-8 px-4">
      <div className="max-w-[720px] mx-auto">
        {/* Back link */}
        <Link
          href="/"
          className="inline-flex items-center gap-2 text-text-muted hover:text-emerald transition-colors mb-6"
        >
          <ArrowLeft size={18} />
          <span className="text-sm">Back to home</span>
        </Link>

        {/* Title */}
        <h1 className="font-heading text-2xl md:text-3xl text-text mb-2">
          Create Your {formatOccasion(occasion)} Page
        </h1>

        {/* Step Indicator */}
        <StepIndicator currentStep={step} />

        {/* Step Content */}
        <AnimatePresence mode="wait">
          {step === 1 && (
            <motion.div
              key="step1"
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{ duration: 0.3 }}
              className="bg-white rounded-2xl border border-gray-100 shadow-card p-6 mb-6"
            >
              <h2 className="font-heading text-lg text-text mb-4">
                {formatOccasion(occasion)} Details
              </h2>

              <OccasionFields
                occasion={occasion}
                formData={formData}
                setFormData={setFormData}
              />

              {Object.keys(errors).length > 0 && (
                <p className="text-red-500 text-sm mt-4">
                  Please fill in all required fields.
                </p>
              )}
            </motion.div>
          )}

          {step === 2 && (
            <motion.div
              key="step2"
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{ duration: 0.3 }}
              className="space-y-6 mb-6"
            >
              {/* Message */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6">
                <h2 className="font-heading text-lg text-text mb-4">
                  Your Message
                </h2>
                <textarea
                  value={message}
                  onChange={(e) => {
                    if (e.target.value.length <= 1000) setMessage(e.target.value);
                  }}
                  placeholder="Write a heartfelt message..."
                  rows={5}
                  className={`${inputClass} resize-none`}
                />
                <div className="flex justify-between mt-2">
                  {errors.message && (
                    <p className="text-red-500 text-sm">{errors.message}</p>
                  )}
                  <p className="text-xs text-text-muted ml-auto">
                    {message.length}/1000
                  </p>
                </div>
              </div>

              {/* Photos */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6">
                <h2 className="font-heading text-lg text-text mb-4">
                  Photos
                </h2>
                <PhotoUploader
                  photos={photos}
                  setPhotos={setPhotos}
                  maxPhotos={MAX_PHOTOS[selectedPlan]}
                />
              </div>

              {/* Color Theme */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6">
                <ColorThemePicker
                  selectedColor={selectedColor}
                  setSelectedColor={setSelectedColor}
                />
              </div>

              {/* Music */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6">
                <MusicSelector
                  selectedMusic={selectedMusic}
                  setSelectedMusic={setSelectedMusic}
                  isLocked={selectedPlan === "basic"}
                />
              </div>
            </motion.div>
          )}

          {step === 3 && (
            <motion.div
              key="step3"
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{ duration: 0.3 }}
              className="space-y-6 mb-6"
            >
              {/* Plan Selector */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6">
                <h2 className="font-heading text-lg text-text mb-4">
                  Choose Your Plan
                </h2>
                <PlanSelector
                  selectedPlan={selectedPlan}
                  setSelectedPlan={setSelectedPlan}
                />
              </div>

              {/* Order Summary */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6">
                <h2 className="font-heading text-lg text-text mb-4">
                  Order Summary
                </h2>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-text-muted">Occasion</span>
                    <span className="text-text font-medium">{formatOccasion(occasion)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-text-muted">Recipient</span>
                    <span className="text-text font-medium">{getRecipient()}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-text-muted">Plan</span>
                    <span className="text-text font-medium capitalize">{selectedPlan}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-text-muted">Photos</span>
                    <span className="text-text font-medium">{photos.length}</span>
                  </div>
                  <div className="border-t pt-3 flex justify-between">
                    <span className="text-text font-semibold">Total</span>
                    <span className="text-emerald font-heading text-xl font-bold">
                      &#8377;{PLAN_PRICES[selectedPlan]}
                    </span>
                  </div>
                </div>
                <p className="text-xs text-text-muted mt-4 text-center">
                  Your site will be ready in under 60 seconds after payment!
                </p>
              </div>

              {/* Contact Info */}
              <div className="bg-white rounded-2xl border border-gray-100 shadow-card p-6 space-y-4">
                <h2 className="font-heading text-lg text-text mb-2">
                  Contact Details
                </h2>
                <div>
                  <label className={labelClass}>
                    Email <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="your@email.com"
                    className={inputClass}
                  />
                </div>
                <div>
                  <label className={labelClass}>
                    Phone <span className="text-red-500">*</span>
                  </label>
                  <div className="flex">
                    <span className="inline-flex items-center px-3 py-3 border border-r-0 border-gray-200 rounded-l-xl bg-gray-50 text-text-muted text-sm">
                      +91
                    </span>
                    <input
                      type="tel"
                      value={phone}
                      onChange={(e) => setPhone(e.target.value.replace(/\D/g, "").slice(0, 10))}
                      placeholder="9876543210"
                      className={`${inputClass} rounded-l-none`}
                    />
                  </div>
                </div>

                {/* Terms checkbox */}
                <label className="flex items-start gap-3 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={agreedToTerms}
                    onChange={(e) => setAgreedToTerms(e.target.checked)}
                    className="mt-1 w-4 h-4 rounded border-gray-300 text-emerald focus:ring-emerald"
                  />
                  <span className="text-sm text-text-muted">
                    I agree to the{" "}
                    <span className="text-emerald underline">terms of service</span>
                  </span>
                </label>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Navigation Buttons */}
        <div className="flex items-center justify-between gap-4">
          {step > 1 && (
            <motion.button
              type="button"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              onClick={goBack}
              className="px-6 py-3 rounded-full border-2 border-gray-200 text-text font-medium hover:border-emerald/30 transition-colors"
            >
              &larr; Back
            </motion.button>
          )}

          {step < 3 && (
            <motion.button
              type="button"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              animate={isShaking ? { x: [0, -8, 8, -8, 8, 0] } : {}}
              transition={isShaking ? { duration: 0.4 } : {}}
              onClick={goNext}
              className="ml-auto px-8 py-3 rounded-full bg-gradient-to-r from-emerald to-emerald-light text-white font-semibold shadow-md hover:shadow-lg transition-shadow"
            >
              Next &rarr;
            </motion.button>
          )}

          {step === 3 && (
            <motion.button
              type="button"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              animate={isShaking ? { x: [0, -8, 8, -8, 8, 0] } : {}}
              transition={isShaking ? { duration: 0.4 } : {}}
              onClick={handlePay}
              disabled={!agreedToTerms || !email.trim() || !phone.trim()}
              className="flex-1 py-4 rounded-full bg-gradient-to-r from-emerald to-emerald-light text-white font-bold text-lg shadow-md hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:shadow-md"
            >
              Pay &#8377;{PLAN_PRICES[selectedPlan]} &rarr;
            </motion.button>
          )}
        </div>
      </div>
    </div>
  );
}
