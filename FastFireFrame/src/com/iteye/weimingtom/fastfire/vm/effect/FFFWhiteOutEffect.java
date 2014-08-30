package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFWhiteOutEffect extends FFFEffect {
	public FFFWhiteOutEffect(FFFMainWin win, FFFImage dst, FFFImage src) {
		super(win, 1000 / 16, dst, src);
	}
	
	@Override 
	public boolean step() {
		Dst.fadeToWhite(Src, EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 16) {
			return false;
		}
		return true;
	}
}
