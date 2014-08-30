package com.iteye.weimingtom.fastfire.vm.ui;

import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;

public interface IWindow {
	void onLButtonUp(FFFPoint point);
	void onLButtonDown(FFFPoint point);
	void onRButtonUp(FFFPoint point);
	void onMouseMove(FFFPoint point);
	boolean onIdle(int count);
	boolean onCreate();
	void onPaint();
	void onDestroy();
	void onKeyDown(int key);
	void messageBox(String str);
	void draw(FFFImage image, FFFRectangle rect);
}
