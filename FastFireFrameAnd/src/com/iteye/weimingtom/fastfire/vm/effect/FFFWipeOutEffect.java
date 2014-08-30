package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFWipeOutEffect extends FFFEffect {
	public FFFWipeOutEffect(FFFMainWin win, FFFImage dst, FFFImage src) {
		super(win, 1000 / 8, dst, src);
	}
	
	@Override 
	public boolean step() {
		Dst.wipeOut(EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 8) {
			return false;
		}
		return true;
	}
}
