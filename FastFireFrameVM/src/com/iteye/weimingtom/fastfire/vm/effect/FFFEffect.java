package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.model.FFFConfig;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public abstract class FFFEffect {
	private static final FFFRectangle default_rect = new FFFRectangle(0, 0, FFFConfig.WindowWidth, FFFConfig.WindowHeight);
	
	protected FFFMainWin Window;
	protected FFFImage Dst;
	protected FFFImage Src;

	protected int TimeBase;
	protected int EffectCnt;
	protected FFFRectangle EffectRect;
	protected long lastTime;
	
	public abstract boolean step();

	public FFFEffect(FFFMainWin win, int step, FFFImage dst) {
		this(win, step, dst, null, FFFEffect.default_rect);
	}
	
	public FFFEffect(FFFMainWin win, int step, FFFImage dst, FFFImage src) {
		this(win, step, dst, src, FFFEffect.default_rect);
	}
	
	public FFFEffect(FFFMainWin win, int step, FFFImage dst, FFFImage src, FFFRectangle rect) {
		Window = win;
		Dst = dst;
		Src = src;
		TimeBase = step;
		//FXIME:???
		EffectRect = rect.cloneRect(); 
		EffectCnt = 0;
		lastTime = 0;
	}
	
	public boolean step2(long time) {
		if (TimeBase <= time - lastTime) {
			lastTime = time;
			return step();
		}
		return true;
	}
}
