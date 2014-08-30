package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFWipeInEffect extends FFFEffect {
	public FFFWipeInEffect(FFFMainWin win, FFFImage dst, FFFImage src, FFFRectangle rect) {
		super(win, 1000 / 8, dst, src, rect);
	}
	
	@Override 
	public boolean step() {
		Dst.wipeIn(Src, EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 8) {
			return false;
		}
		return true;
	}
}
