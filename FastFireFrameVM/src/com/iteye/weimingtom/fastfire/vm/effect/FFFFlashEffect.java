package com.iteye.weimingtom.fastfire.vm.effect;

import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFFlashEffect extends FFFEffect {
	public FFFFlashEffect(FFFMainWin win, FFFImage dst) {
		super(win, 1000 / 24, dst);
	}
	
	@Override 
	public boolean step() {
		return false;
	}
}
