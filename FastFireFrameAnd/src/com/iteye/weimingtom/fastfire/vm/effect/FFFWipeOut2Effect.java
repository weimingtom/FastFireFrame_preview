package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFWipeOut2Effect extends FFFEffect {
	public FFFWipeOut2Effect(FFFMainWin win, FFFImage dst, FFFImage src) {
		super(win, 1000 / 20, dst, src);
	}
	
	@Override 
	public boolean step() {
		boolean result = Dst.wipeOut2(EffectRect, EffectCnt++);
		Window.repaintView(EffectRect);
		return result;
	}
}
