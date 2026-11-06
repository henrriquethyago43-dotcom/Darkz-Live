import React from 'react';
import { Wifi, BatteryMedium, Signal } from 'lucide-react';

interface AndroidFrameProps {
  children: React.ReactNode;
}

export const AndroidFrame: React.FC<AndroidFrameProps> = ({ children }) => {
  return (
    <div className="relative mx-auto flex flex-col items-center">
      {/* Outer Phone Shell */}
      <div className="relative w-[380px] h-[780px] max-h-[92vh] bg-[#0A0A0F] rounded-[48px] p-3 shadow-[0_0_50px_rgba(229,9,20,0.25)] border-[3px] border-[#252530] flex flex-col overflow-hidden">
        {/* Side physical buttons (visual details) */}
        <div className="absolute -left-[5px] top-[140px] w-[3px] h-[35px] bg-[#333] rounded-l" />
        <div className="absolute -left-[5px] top-[185px] w-[3px] h-[35px] bg-[#333] rounded-l" />
        <div className="absolute -right-[5px] top-[160px] w-[3px] h-[45px] bg-[#E50914] rounded-r shadow-[0_0_8px_#E50914]" />

        {/* Screen Bezel & Display Area */}
        <div className="relative w-full h-full bg-[#050507] rounded-[38px] overflow-hidden flex flex-col border border-[#1A1A24]">
          {/* Android 13 Status Bar */}
          <div className="h-7 w-full px-5 flex items-center justify-between text-[11px] font-mono text-gray-400 bg-[#050507] z-30 select-none">
            <span className="font-semibold text-white tracking-wider">19:50</span>
            
            {/* Center Front Camera Punchhole */}
            <div className="w-3.5 h-3.5 rounded-full bg-black border border-[#222] shadow-inner" />

            <div className="flex items-center gap-1.5 text-gray-300">
              <Signal size={12} />
              <span className="text-[9px] font-bold text-[#00F0FF]">5G</span>
              <Wifi size={12} />
              <div className="w-5 h-2.5 border border-gray-400 rounded-sm p-[1px] flex items-center">
                <div className="w-full h-full bg-[#00FF66] rounded-[1px]" />
              </div>
            </div>
          </div>

          {/* Screen Content */}
          <div className="flex-1 w-full overflow-hidden flex flex-col relative">
            {children}
          </div>

          {/* Android Gesture Navigation Bar Pill */}
          <div className="h-4 w-full bg-[#050507] flex items-center justify-center z-30 select-none">
            <div className="w-28 h-1 bg-white/40 rounded-full" />
          </div>
        </div>
      </div>
    </div>
  );
};
