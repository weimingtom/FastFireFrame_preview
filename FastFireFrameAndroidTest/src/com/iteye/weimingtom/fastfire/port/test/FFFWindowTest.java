package com.iteye.weimingtom.fastfire.port.test;

import com.iteye.weimingtom.fastfire.port.window.FFFWindow;
import com.iteye.weimingtom.fastfire.port.window.FastFireFrameAndActivity;
import com.iteye.weimingtom.fastfire.port.window.FastFireFrameAndGameView;

public class FFFWindowTest extends FastFireFrameAndActivity {
    protected FastFireFrameAndGameView createGameView() {
    	return new FFFWindow(this);
    }
}
