const LANGUAGES = ['English', 'Hindi', 'Telugu', 'Tamil', 'Kannada', 'Bengali', 'Marathi'];

export default function LanguageSelector({ value, onChange }) {
  return (
    <div>
      <label className="block text-sm font-medium text-dark-200 mb-1.5">Language</label>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="input-field"
      >
        {LANGUAGES.map(lang => (
          <option key={lang} value={lang}>{lang}</option>
        ))}
      </select>
    </div>
  );
}
