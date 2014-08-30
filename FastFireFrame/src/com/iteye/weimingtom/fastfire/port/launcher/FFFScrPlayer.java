package com.iteye.weimingtom.fastfire.port.launcher;

import com.iteye.weimingtom.fastfire.port.window.FFFWindow;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFScrPlayer {
	public static void main(String[] args) {
		FFFWindow win = new FFFWindow();
		win.setAdapter(new FFFMainWin());
		win.start();
	}
}
