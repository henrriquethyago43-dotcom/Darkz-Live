import React, { useState } from 'react';
import JSZip from 'jszip';
import { Download, CheckCircle2, Terminal, AlertCircle, PackageCheck, Smartphone, Cpu } from 'lucide-react';
import { ANDROID_PROJECT_FILES } from '../androidCodebase';

export const ProjectExporter: React.FC = () => {
  const [isExporting, setIsExporting] = useState<boolean>(false);
  const [downloadCompleted, setDownloadCompleted] = useState<boolean>(false);

  const handleDownloadZip = async () => {
    setIsExporting(true);
    try {
      const zip = new JSZip();

      // Add all project files into the zip
      for (const file of ANDROID_PROJECT_FILES) {
        zip.file(file.path, file.content);
      }

      // Add root README
      zip.file(
        'README.md',
        `# DARKZ LIVE — Android Nativo (Kotlin & Jetpack Compose)\n\nProjeto pronto para importação direta no Android Studio.\n\nPara compilar o APK:\n./gradlew assembleDebug\n\nO APK será gerado em: app/build/outputs/apk/debug/app-debug.apk`
      );

      // Generate the ZIP blob
      const blob = await zip.generateAsync({ type: 'blob' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'darkz-live-android-project.zip';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      setDownloadCompleted(true);
      setTimeout(() => setDownloadCompleted(false), 5000);
    } catch (err) {
      console.error('Falha ao gerar ZIP do projeto Android:', err);
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto p-6 bg-[#0A0A0F] border border-[#2A2A38] rounded-2xl shadow-2xl text-white">
      {/* Title */}
      <div className="flex items-center gap-3 pb-5 border-b border-[#1F1F2C]">
        <div className="w-12 h-12 rounded-xl bg-[#E50914]/20 border border-[#E50914] flex items-center justify-center text-[#E50914]">
          <PackageCheck size={26} />
        </div>
        <div>
          <h2 className="text-lg font-mono font-bold text-white tracking-wide">
            DOWNLOAD DO PROJETO ANDROID STUDIO (.ZIP)
          </h2>
          <p className="text-xs text-gray-400">
            Pacote completo com gradle, Kotlin, Jetpack Compose, Manifest e assets para gerar o APK
          </p>
        </div>
      </div>

      {/* Main Download Action */}
      <div className="my-6 p-6 bg-[#12121A] border border-[#2A2A38] rounded-xl flex flex-col md:flex-row items-center justify-between gap-4">
        <div>
          <div className="text-sm font-mono font-bold text-[#00F0FF]">
            PROJETO 100% ANDROID NATIVO PRONTO
          </div>
          <div className="text-xs text-gray-300 mt-1">
            Inclui: <code className="text-[#FF1A2A]">settings.gradle.kts</code>, <code className="text-[#FF1A2A]">app/build.gradle.kts</code>, <code className="text-[#FF1A2A]">AndroidManifest.xml</code>, todas as classes Kotlin de <code className="text-[#00F0FF]">com.darkz.live</code>.
          </div>
        </div>

        <button
          onClick={handleDownloadZip}
          disabled={isExporting}
          className="w-full md:w-auto px-6 py-3.5 bg-gradient-to-r from-[#E50914] to-[#B8000A] hover:brightness-110 active:scale-95 text-white font-mono font-bold text-xs tracking-wider rounded-lg border border-white/40 flex items-center justify-center gap-2 transition-all shadow-[0_0_20px_rgba(229,9,20,0.4)] disabled:opacity-50"
        >
          {isExporting ? (
            <>
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              <span>GERANDO ZIP...</span>
            </>
          ) : downloadCompleted ? (
            <>
              <CheckCircle2 size={16} className="text-[#00FF66]" />
              <span>DOWNLOAD CONCLUÍDO!</span>
            </>
          ) : (
            <>
              <Download size={16} />
              <span>BAIXAR PROJETO ANDROID (.ZIP)</span>
            </>
          )}
        </button>
      </div>

      {/* Verification & Build Guide */}
      <div className="space-y-4 text-xs font-mono">
        <h3 className="text-xs font-bold text-[#FF1A2A] tracking-wider uppercase flex items-center gap-2">
          <Terminal size={14} /> Passo a Passo para Compilar o APK no Android Studio
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <div className="p-3 bg-[#12121A] border border-[#2A2A38] rounded-lg">
            <div className="text-[#00F0FF] font-bold text-xs mb-1">1. DESCOMPACTAR</div>
            <p className="text-gray-400 text-[11px]">
              Extraia o arquivo <span className="text-white">darkz-live-android-project.zip</span> em uma pasta no seu computador.
            </p>
          </div>

          <div className="p-3 bg-[#12121A] border border-[#2A2A38] rounded-lg">
            <div className="text-[#00F0FF] font-bold text-xs mb-1">2. ABRIR NO ANDROID STUDIO</div>
            <p className="text-gray-400 text-[11px]">
              No Android Studio (Jellyfish/Koala/Ladybug), clique em <span className="text-white">File &gt; Open</span> e selecione a pasta extraída.
            </p>
          </div>

          <div className="p-3 bg-[#12121A] border border-[#2A2A38] rounded-lg">
            <div className="text-[#00F0FF] font-bold text-xs mb-1">3. GERAR APK</div>
            <p className="text-gray-400 text-[11px]">
              Clique no menu <span className="text-white">Build &gt; Build Bundle(s) / APK(s) &gt; Build APK(s)</span> ou execute <span className="text-[#00FF66]">./gradlew assembleDebug</span>.
            </p>
          </div>
        </div>

        {/* Requirements Spec Card */}
        <div className="p-4 bg-[#14141E] border border-[#252535] rounded-xl flex items-start gap-3">
          <Smartphone size={18} className="text-[#00F0FF] mt-0.5 shrink-0" />
          <div className="text-[11px] text-gray-300 space-y-1">
            <div className="font-bold text-white">REQUISITOS DO AMBIENTE ANDROID</div>
            <div>• <span className="text-gray-400">MinSdk:</span> 26 (Android 8.0 Oreo) | <span className="text-gray-400">TargetSdk:</span> 34 (Android 14)</div>
            <div>• <span className="text-gray-400">JDK Recomendado:</span> Java 17 ou 21</div>
            <div>• <span className="text-gray-400">Compose Compiler:</span> Integrado no Kotlin 2.0.21 via plugin oficial</div>
            <div>• <span className="text-gray-400">Instalação no celular:</span> Ative &quot;Fontes Desconhecidas&quot; ou use o comando <code className="text-[#00F0FF]">adb install app-debug.apk</code></div>
          </div>
        </div>
      </div>
    </div>
  );
};
