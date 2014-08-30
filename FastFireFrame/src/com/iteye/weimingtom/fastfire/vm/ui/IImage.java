package com.iteye.weimingtom.fastfire.vm.ui;

import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.file.FFFFile;
import com.iteye.weimingtom.fastfire.port.image.FFFFont;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.port.window.FFFWindow;

public interface IImage {
	boolean create(int width, int height);
	boolean loadFile(FFFFile file, int ox, int oy);
	int getWidth();
	int getHeight();
	void clear();
	boolean loadImage(String name, int ox, int oy);
	void fillRect(FFFRectangle rect, int color);
	void copy(FFFImage image, FFFRectangle rect);
	void mixImage(FFFImage image, FFFRectangle rect, int trans_color);
	void drawRect(FFFRectangle rect, int color);
	void fillHalfToneRect(FFFRectangle rect);
	void drawFrameRect(FFFRectangle rect, int color);
	void drawText(FFFFont hFont, int x1, int y1, String str, int color);
	void wipeIn(FFFImage image, FFFRectangle rect, int count);
	void wipeOut(FFFRectangle rect, int count);
	boolean wipeIn2(FFFImage image, FFFRectangle rect, int count);
	boolean wipeOut2(FFFRectangle rect, int count);
	void fadeCvt(FFFImage image, FFFRectangle rect, int[] cvt);
	void fadeFromBlack(FFFImage image, FFFRectangle rect, int count);
	void fadeToBlack(FFFImage image, FFFRectangle rect, int count);
	void fadeFromWhite(FFFImage image, FFFRectangle rect, int count);
	void fadeToWhite(FFFImage image, FFFRectangle rect, int count);
	void mix(FFFImage image, FFFRectangle rect, int count);
}
