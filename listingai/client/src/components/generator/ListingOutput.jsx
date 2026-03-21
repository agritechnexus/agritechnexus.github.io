import { useState } from 'react';
import { Copy, Check, RefreshCw, Download, Save } from 'lucide-react';
import toast from 'react-hot-toast';

function SeoGauge({ score }) {
  const radius = 40;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;
  const color = score >= 75 ? '#22c55e' : score >= 50 ? '#eab308' : '#ef4444';

  return (
    <div className="flex items-center gap-3">
      <svg width="100" height="100" className="-rotate-90">
        <circle cx="50" cy="50" r={radius} fill="none" stroke="#2a2a2a" strokeWidth="8" />
        <circle cx="50" cy="50" r={radius} fill="none" stroke={color} strokeWidth="8"
          strokeDasharray={circumference} strokeDashoffset={offset} strokeLinecap="round"
          className="gauge-fill" />
      </svg>
      <div>
        <p className="text-2xl font-bold" style={{ color }}>{score}</p>
        <p className="text-xs text-dark-400">SEO Score</p>
      </div>
    </div>
  );
}

function CopyButton({ text, label }) {
  const [copied, setCopied] = useState(false);
  const handleCopy = async () => {
    await navigator.clipboard.writeText(text);
    setCopied(true);
    toast.success(`${label || 'Text'} copied!`);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <button onClick={handleCopy} className="text-dark-400 hover:text-primary transition p-1">
      {copied ? <Check size={16} className="text-green-400" /> : <Copy size={16} />}
    </button>
  );
}

export default function ListingOutput({ listing, onRegenerate }) {
  const [activeTab, setActiveTab] = useState(0);

  if (!listing?.generatedListings?.length && !listing?.marketplace) return null;

  const listings = listing.generatedListings || [listing];

  const copyAll = (gl) => {
    const text = `Title: ${gl.title}\n\nBullet Points:\n${gl.bulletPoints?.map((b, i) => `${i + 1}. ${b}`).join('\n')}\n\nDescription:\n${gl.description}\n\nSearch Terms: ${gl.searchTerms || ''}`;
    navigator.clipboard.writeText(text);
    toast.success('Full listing copied!');
  };

  return (
    <div className="space-y-4">
      {listings.length > 1 && (
        <div className="flex gap-2 overflow-x-auto pb-2">
          {listings.map((gl, i) => (
            <button
              key={i}
              onClick={() => setActiveTab(i)}
              className={`px-4 py-2 rounded-lg text-sm font-medium whitespace-nowrap transition ${
                activeTab === i ? 'bg-primary text-white' : 'bg-dark-700 text-dark-300 hover:text-white'
              }`}
            >
              {gl.marketplace}
            </button>
          ))}
        </div>
      )}

      {listings.map((gl, i) => (
        <div key={i} className={i === activeTab ? 'block' : 'hidden'}>
          <div className="space-y-5">
            {/* Title */}
            <div className="card">
              <div className="flex items-center justify-between mb-2">
                <h4 className="text-sm font-medium text-dark-400">Title</h4>
                <div className="flex items-center gap-2">
                  <span className="text-xs text-dark-500">{gl.title?.length || 0} chars</span>
                  <CopyButton text={gl.title} label="Title" />
                </div>
              </div>
              <p className="text-white font-medium">{gl.title}</p>
            </div>

            {/* Bullet Points */}
            <div className="card">
              <h4 className="text-sm font-medium text-dark-400 mb-3">Bullet Points</h4>
              <ul className="space-y-2">
                {gl.bulletPoints?.map((bp, j) => (
                  <li key={j} className="flex items-start gap-2">
                    <span className="text-primary font-bold text-sm mt-0.5">{j + 1}.</span>
                    <p className="text-dark-200 text-sm flex-1">{bp}</p>
                    <CopyButton text={bp} label={`Bullet ${j + 1}`} />
                  </li>
                ))}
              </ul>
            </div>

            {/* Description */}
            <div className="card">
              <div className="flex items-center justify-between mb-2">
                <h4 className="text-sm font-medium text-dark-400">Description</h4>
                <CopyButton text={gl.description} label="Description" />
              </div>
              <p className="text-dark-200 text-sm whitespace-pre-line leading-relaxed">{gl.description}</p>
            </div>

            {/* Search Terms */}
            {gl.searchTerms && (
              <div className="card">
                <div className="flex items-center justify-between mb-2">
                  <h4 className="text-sm font-medium text-dark-400">Backend Search Terms</h4>
                  <CopyButton text={gl.searchTerms} label="Search Terms" />
                </div>
                <p className="text-dark-300 text-sm">{gl.searchTerms}</p>
              </div>
            )}

            {/* SEO Score & Tips */}
            <div className="card">
              <div className="flex items-start justify-between">
                <div>
                  <h4 className="text-sm font-medium text-dark-400 mb-3">SEO Analysis</h4>
                  <SeoGauge score={gl.seoScore || 0} />
                </div>
                {gl.seoTips?.length > 0 && (
                  <div className="flex-1 ml-6">
                    <h5 className="text-sm font-medium text-dark-400 mb-2">Tips</h5>
                    <ul className="space-y-1">
                      {gl.seoTips.map((tip, j) => (
                        <li key={j} className="text-xs text-dark-300 flex items-start gap-1.5">
                          <span className="text-primary">-</span> {tip}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            </div>

            {/* Actions */}
            <div className="flex flex-wrap gap-3">
              <button onClick={() => copyAll(gl)} className="btn-primary text-sm py-2 px-4 gap-2">
                <Copy size={16} /> Copy All
              </button>
              {onRegenerate && (
                <button onClick={onRegenerate} className="btn-secondary text-sm py-2 px-4 gap-2">
                  <RefreshCw size={16} /> Regenerate
                </button>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
