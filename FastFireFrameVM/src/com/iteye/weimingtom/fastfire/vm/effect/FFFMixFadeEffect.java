package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFMixFadeEffect extends FFFEffect {
	private FFFImage mixImage, maskImage;
	
	public FFFMixFadeEffect(FFFMainWin win, FFFImage dst, FFFImage src, FFFRectangle rect, FFFImage mixImage, FFFImage maskImage) {
		super(win, 1000 / 8, dst, src, rect);
		this.mixImage = mixImage;
		this.maskImage = maskImage;
	}
	
	@Override 
	public boolean step() {
		Dst.mix(Src, EffectRect, EffectCnt, mixImage, maskImage);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 8)
			return false;
		return true;
	}
}
