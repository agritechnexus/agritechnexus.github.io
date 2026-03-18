"use client";

import { useCallback, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Upload, X } from "lucide-react";

interface PhotoUploaderProps {
  photos: File[];
  setPhotos: (photos: File[]) => void;
  maxPhotos?: number;
}

export default function PhotoUploader({
  photos,
  setPhotos,
  maxPhotos = 5,
}: PhotoUploaderProps) {
  const inputRef = useRef<HTMLInputElement>(null);

  const addFiles = useCallback(
    (files: FileList | null) => {
      if (!files) return;
      const accepted = Array.from(files).filter((f) =>
        ["image/jpeg", "image/png", "image/webp"].includes(f.type)
      );
      const combined = [...photos, ...accepted].slice(0, maxPhotos);
      setPhotos(combined);
    },
    [photos, setPhotos, maxPhotos]
  );

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      addFiles(e.dataTransfer.files);
    },
    [addFiles]
  );

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  const removePhoto = (index: number) => {
    setPhotos(photos.filter((_, i) => i !== index));
  };

  return (
    <div>
      {/* Drop zone */}
      <div
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onClick={() => inputRef.current?.click()}
        className="border-2 border-dashed border-gray-300 rounded-2xl p-8 text-center cursor-pointer transition-colors hover:border-emerald hover:bg-emerald/5"
      >
        <Upload className="mx-auto mb-3 text-gray-400" size={32} />
        <p className="text-text font-medium">Drag photos here or click to browse</p>
        <p className="text-text-muted text-sm mt-1">
          Up to {maxPhotos >= 100 ? "Unlimited" : `${maxPhotos} photos`}
        </p>
        <input
          ref={inputRef}
          type="file"
          accept=".jpg,.jpeg,.png,.webp"
          multiple
          onChange={(e) => addFiles(e.target.files)}
          className="hidden"
        />
      </div>

      {/* Thumbnails */}
      {photos.length > 0 && (
        <div className="grid grid-cols-3 md:grid-cols-4 gap-3 mt-4">
          <AnimatePresence>
            {photos.map((file, index) => (
              <motion.div
                key={`${file.name}-${index}`}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.8 }}
                transition={{ duration: 0.2 }}
                className="relative w-full aspect-square rounded-xl overflow-hidden"
              >
                <img
                  src={URL.createObjectURL(file)}
                  alt={`Upload ${index + 1}`}
                  className="w-full h-full object-cover"
                />
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    removePhoto(index);
                  }}
                  className="absolute top-1 right-1 w-6 h-6 rounded-full bg-red-500 text-white flex items-center justify-center hover:bg-red-600 transition-colors"
                >
                  <X size={12} />
                </button>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}
    </div>
  );
}
