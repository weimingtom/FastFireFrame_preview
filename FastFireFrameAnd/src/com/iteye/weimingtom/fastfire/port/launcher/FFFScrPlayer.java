package com.iteye.weimingtom.fastfire.port.launcher;

import com.iteye.weimingtom.fastfire.port.window.FFFWindow;
import com.iteye.weimingtom.fastfire.port.window.FastFireFrameAndActivity;
import com.iteye.weimingtom.fastfire.port.window.FastFireFrameAndGameView;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFScrPlayer extends FastFireFrameAndActivity {
    protected FastFireFrameAndGameView createGameView() {
    	FFFWindow view = new FFFWindow(this);
    	view.setAdapter(new FFFMainWin());
    	return view;
    }
}
