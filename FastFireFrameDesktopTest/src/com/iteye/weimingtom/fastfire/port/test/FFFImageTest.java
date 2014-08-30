package com.iteye.weimingtom.fastfire.port.test;

import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.port.window.FFFWindow;
import com.iteye.weimingtom.fastfire.port.window.FFFWindowAdapter;

/**
 * FIXME: testCDrawImage中WipeOut有问题（AS3版也有问题）
 * @author Administrator
 *
 */
public class FFFImageTest {
	private static class CTestWindow extends FFFWindowAdapter {
		private FFFResource res = new FFFResource();
		
		private FFFImage img1 = new FFFImage(res, 640, 480);
		private FFFImage img2 = new FFFImage(res, 640, 480);
		private FFFImage img3 = new FFFImage(res, 0, 0);
		
		private FFFImage mixImage = new FFFImage(res, 0, 0);
		private FFFImage maskImage = new FFFImage(res, 640, 480);
		
		private int wheel_degree = 0;
		
		@Override
		public boolean onCreate() {
			res.init(getWindow());
			res.loadImage("cgdata/bg001", "cgdata/bg001.JPG");
			res.loadImage("cgdata/ch001", "cgdata/MEGU111.png");
			res.loadImage("rule/mix", "rule/mix.png");
			res.loadImage("rule/wipe", "rule/wipe.png");
			
//			testCImage();
			testCDrawImage();
			//testFormatMessage();
			return super.onCreate();
		}
		
		private void testCImage() {
			img1.loadImage("bg001", 0, 0);
			img2.loadImage("ch001", 0, 0);
			mixImage.loadRule("mix", 0, 0);
			
//			img1.copy(img2, new FFFRectangle(0, 0, img2.getWidth(), img2.getHeight()));
//			img1.mixImage(img2, new FFFRectangle(160, 0, 320, 480), 0x00FF00);
//			img1.fillRect(new FFFRectangle(100, 100, 100, 100), 0xFF0000);
//			img1.drawRect(new FFFRectangle(100, 100, 100, 100), 0xFF0000);
//			img1.fillHalfToneRect(new FFFRectangle(100, 100, 100, 100));
//			img1.drawFrameRect(new FFFRectangle(100, 100, 100, 100), 0xFF0000);
			img1.drawFrameRect(new FFFRectangle(100, 100, 100, 100), 0xFFFFFF);
			
//			this.getBufGraph().setColor(Color.BLUE);
//			this.getBufGraph().fillRect(0, 0, 640, 480);
			this.getWindow().draw(img1, new FFFRectangle(0, 0, 640, 480));
		}
		
		/**
		 * 效果测试
		 * 注意检查异常（尤其是涉及ByteArray的复杂运算）
		 */
		private void testCDrawImage() {
			img3.loadImage("bg001", 0, 0);
			img2.loadImage("ch001", 160, 0);
			mixImage.loadRule("mix", 0, 0);
			//addChild(layer1);
			//layer1.graphics.clear();
			//img3.Copy2(img2);
			
			//img3.mixImage(img2, new FFFRectangle(160, 0, 320, 480), 0x00FF00);
			
			img3.drawText(null, 0, 100, "hello, world", 0x0000FF);
			
//			img3.wipeIn(img2, new FFFRectangle(0, 0, 640, 480), 0);
//			img3.wipeOut(new FFFRectangle(50, 0, 640, 480), 0);
//			img3.fadeToWhite(img2, new FFFRectangle(0, 0, 640, 480), 0);
//			img3.mix(img2, new FFFRectangle(0, 0, 640, 480), 0, mixImage, maskImage);
			img3.fadeToBlack(img2, new FFFRectangle(0, 0, 640, 480), 0);
			
//			this.getBufGraph().setColor(Color.BLUE);
//			this.getBufGraph().fillRect(0, 0, 640, 480);
			this.getWindow().draw(img3, new FFFRectangle(0, 0, 640, 480));
		}
		
		
		
		@Override
		protected void onLButtonDown(FFFPoint point) {
			super.onLButtonDown(point);
			FFFLog.trace("wheel_degree : " + wheel_degree);
			if (point.y < this.getWindow().getHeight()) {
				wheel_degree++;				
			} else {
				wheel_degree--;
			}
//			this.getBufGraph().setColor(Color.BLUE);
//			this.getBufGraph().fillRect(0, 0, 640, 480);
			img3.loadImage("bg001", 0, 0);
			img3.drawText(null, 0, 100, "hello, world", 0x0000FF);
			
//			img3.wipeIn(img2, new FFFRectangle(0, 0, 640, 480), wheel_degree);
//			img3.wipeOut(new FFFRectangle(0, 0, 640, 480), wheel_degree);
//			img3.fadeToWhite(img2, new FFFRectangle(0, 0, 640, 480), wheel_degree);
//			img3.mix(img2, new FFFRectangle(0, 0, 640, 480), wheel_degree, mixImage, maskImage);
			img3.fadeToBlack(img2, new FFFRectangle(0, 0, 640, 480), wheel_degree);
			
			this.getWindow().draw(img3, new FFFRectangle(0, 0, 640, 480));			
		}

		private void testFormatMessage() {
			String testStr = "“２楼已经去过了啦！\n去别的地方吧”";
			FFFLog.trace("original:" + testStr);
			int i = FormatMessage(testStr);
			//trace("FormatMessage :", );
		}
		
		private String[] MsgBuffer = new String[4];
		private int CurY = 0;
		private int CurX = 0;
		
		public int FormatMessage(String msg) {
			CurX = CurY = 0;
			String[] lines = msg.split("\n", 4);
			for (int i = 0; i < 4; i++) {
				if (i < lines.length && lines[i] != null) {
					MsgBuffer[i] = lines[i];
					FFFLog.trace("FormatMessage," + i + "," + MsgBuffer[i]);
					CurY++;
				} else {
					FFFLog.trace("FormatMessage," + i);
					MsgBuffer[i] = "";
				}
			}
			FFFLog.trace("FormatMessage CurY == " + CurY);
			return CurY;
		}
		
		@Override
		public boolean onIdle(int count) {
			return super.onIdle(count);
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			res.unloadAll();
		}
	}
	
	public static void main(String[] args) {
		FFFWindow win = new FFFWindow();
		win.setAdapter(new CTestWindow());
		win.start();
	}
}
