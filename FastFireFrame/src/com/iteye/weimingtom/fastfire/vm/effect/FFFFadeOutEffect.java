package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFFadeOutEffect extends FFFEffect {
	private static final int STEP = 16;

	public FFFFadeOutEffect(FFFMainWin win, FFFImage dst, FFFImage src) {
		super(win, 1000 / STEP, dst, src);
	}
	
	@Override 
	public boolean step() {
		Dst.fadeToBlack(Src, EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= STEP)
			return false;
		return true;
	}
}
