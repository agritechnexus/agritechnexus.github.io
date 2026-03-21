export default function Button({ children, variant = 'primary', className = '', disabled, loading, ...props }) {
  const base = 'font-semibold py-3 px-6 rounded-lg transition-all duration-200 inline-flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed';
  const variants = {
    primary: 'bg-primary hover:bg-primary-dark text-white',
    secondary: 'border border-dark-400 hover:border-primary text-white',
    ghost: 'text-dark-200 hover:text-white hover:bg-dark-700',
  };

  return (
    <button className={`${base} ${variants[variant]} ${className}`} disabled={disabled || loading} {...props}>
      {loading && <span className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />}
      {children}
    </button>
  );
}
