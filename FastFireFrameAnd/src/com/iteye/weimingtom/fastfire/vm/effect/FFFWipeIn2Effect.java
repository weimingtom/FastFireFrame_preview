package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFWipeIn2Effect extends FFFEffect {
	public FFFWipeIn2Effect(FFFMainWin win, FFFImage dst, FFFImage src, FFFRectangle rect) {
		super(win, 1000 / 20, dst, src, rect);
	}
	
	@Override 
	public boolean step() {
		boolean result = Dst.wipeIn2(Src, EffectRect, EffectCnt++);
		Window.repaintView(EffectRect);
		return result;
	}
}
