import React, { useState } from 'react';
import { ANDROID_PROJECT_FILES, AndroidFileRecord } from '../androidCodebase';
import { FileCode, Copy, Check, Folder, Search, Terminal, Layers } from 'lucide-react';

export const CodeExplorer: React.FC = () => {
  const [selectedFile, setSelectedFile] = useState<AndroidFileRecord>(ANDROID_PROJECT_FILES[0]);
  const [copied, setCopied] = useState<boolean>(false);
  const [search, setSearch] = useState<string>('');

  const filteredFiles = ANDROID_PROJECT_FILES.filter(f =>
    f.path.toLowerCase().includes(search.toLowerCase()) ||
    f.description.toLowerCase().includes(search.toLowerCase())
  );

  const handleCopy = () => {
    navigator.clipboard.writeText(selectedFile.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="w-full max-w-6xl mx-auto h-[780px] bg-[#0A0A0F] border border-[#2A2A38] rounded-2xl flex flex-col overflow-hidden shadow-2xl">
      {/* Top Header */}
      <div className="px-5 py-3.5 bg-[#12121A] border-b border-[#2A2A38] flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2.5">
          <Layers className="text-[#E50914]" size={20} />
          <div>
            <h2 className="text-sm font-mono font-bold text-white tracking-wide">
              EXPLORADOR DE CÓDIGO NATIVO ANDROID (KOTLIN & JETPACK COMPOSE)
            </h2>
            <p className="text-xs text-gray-400">
              Todos os arquivos Kotlin, Manifest, Gradle e Assets prontos para Android Studio
            </p>
          </div>
        </div>

        <div className="relative">
          <Search size={14} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-gray-500" />
          <input
            type="text"
            placeholder="Filtrar arquivos..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-8 pr-3 py-1 bg-[#1A1A24] border border-[#2A2A38] rounded-lg text-xs font-mono text-white placeholder-gray-500 focus:outline-none focus:border-[#E50914] w-52"
          />
        </div>
      </div>

      {/* Main split view: File list on left, Editor on right */}
      <div className="flex-1 flex flex-col md:flex-row overflow-hidden">
        {/* Left Sidebar */}
        <div className="w-full md:w-80 border-r border-[#2A2A38] bg-[#0E0E14] flex flex-col overflow-y-auto">
          <div className="p-3 text-[11px] font-mono font-bold text-gray-400 border-b border-[#1A1A24] flex items-center justify-between">
            <span>ARQUIVOS DO PROJETO ({filteredFiles.length})</span>
            <span className="text-[#00F0FF]">com.darkz.live</span>
          </div>

          <div className="p-2 space-y-1">
            {filteredFiles.map((file) => {
              const isSelected = selectedFile.path === file.path;
              return (
                <button
                  key={file.path}
                  onClick={() => setSelectedFile(file)}
                  className={`w-full text-left p-2.5 rounded-lg text-xs font-mono transition-colors flex items-start gap-2.5 ${
                    isSelected
                      ? 'bg-[#E50914]/20 border border-[#E50914] text-white'
                      : 'hover:bg-[#1A1A24] text-gray-400 hover:text-white border border-transparent'
                  }`}
                >
                  <FileCode
                    size={16}
                    className={`mt-0.5 shrink-0 ${
                      isSelected
                        ? 'text-[#FF1A2A]'
                        : file.category === 'kotlin'
                        ? 'text-[#00F0FF]'
                        : file.category === 'manifest'
                        ? 'text-[#FFB800]'
                        : 'text-gray-400'
                    }`}
                  />
                  <div className="overflow-hidden">
                    <div className="font-semibold truncate text-[11px] text-gray-200">
                      {file.path.split('/').pop()}
                    </div>
                    <div className="text-[10px] text-gray-500 truncate">
                      {file.path}
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Right Code Display */}
        <div className="flex-1 flex flex-col bg-[#050507] overflow-hidden">
          {/* File Tab Bar */}
          <div className="px-4 py-2.5 bg-[#12121A] border-b border-[#2A2A38] flex items-center justify-between">
            <div className="flex items-center gap-2 overflow-hidden">
              <span className="text-xs font-mono text-gray-400">{selectedFile.path}</span>
              <span className="text-[10px] px-2 py-0.5 rounded bg-[#1C1C28] text-[#00F0FF] font-mono uppercase">
                {selectedFile.language}
              </span>
            </div>

            <button
              onClick={handleCopy}
              className="flex items-center gap-1.5 px-3 py-1.5 bg-[#1C1C28] hover:bg-[#2A2A38] text-white rounded text-xs font-mono border border-[#383848] transition-colors"
            >
              {copied ? <Check size={14} className="text-[#00FF66]" /> : <Copy size={14} />}
              <span>{copied ? 'Copiado!' : 'Copiar Código'}</span>
            </button>
          </div>

          <div className="px-4 py-1.5 bg-[#0F0F16] border-b border-[#1A1A24] text-[11px] font-mono text-gray-400">
            ℹ️ {selectedFile.description}
          </div>

          {/* Code Viewer with Line Numbers */}
          <div className="flex-1 p-4 overflow-auto font-mono text-xs leading-relaxed text-gray-200 bg-[#050507]">
            <pre className="overflow-x-auto whitespace-pre">
              <code>{selectedFile.content}</code>
            </pre>
          </div>
        </div>
      </div>
    </div>
  );
};
