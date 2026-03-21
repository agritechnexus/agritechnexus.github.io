export default function Input({ label, error, className = '', ...props }) {
  return (
    <div className={className}>
      {label && <label className="block text-sm font-medium text-dark-200 mb-1.5">{label}</label>}
      <input className={`input-field ${error ? 'border-red-500' : ''}`} {...props} />
      {error && <p className="text-red-400 text-sm mt-1">{error}</p>}
    </div>
  );
}
