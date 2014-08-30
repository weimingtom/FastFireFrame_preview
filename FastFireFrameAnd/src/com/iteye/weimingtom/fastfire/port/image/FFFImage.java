package com.iteye.weimingtom.fastfire.port.image;

import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff;
import android.os.Debug;

import com.iteye.weimingtom.fastfire.model.FFFConfig;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.file.FFFFile;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;

public class FFFImage {
	private final static boolean USE_AVOIDXFERMODE = false;
	
	public Bitmap mImage;
	private Paint paint, bitmapPaint, fadePaint;
	private Canvas g;
	private FFFResource mRes;
	
	private AvoidXfermode mode1;
	private PorterDuffXfermode mode2;
	private LightingColorFilter[] filters;	
	private Paint paintMix;
	
	public FFFImage(FFFResource res, int width, int height) {
		mRes = res;
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		
		fadePaint = new Paint();
		fadePaint.setAntiAlias(true);
		fadePaint.setDither(true);
		fadePaint.setFilterBitmap(true);		
		
		bitmapPaint = new Paint();
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setDither(true);
		bitmapPaint.setFilterBitmap(true);
		
		if (USE_AVOIDXFERMODE) {
	        paintMix = new Paint();
	        paintMix.setStyle(Paint.Style.FILL);
	        paintMix.setAntiAlias(true);
	        paintMix.setDither(true);
	        paintMix.setFilterBitmap(true);
	        
	        mode1 = new AvoidXfermode(Color.WHITE, 0, AvoidXfermode.Mode.TARGET);
	        mode2 = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
	        filters = new LightingColorFilter[256];
	        for (int i = 0; i < 256; i++) {
	        	filters[i] = new LightingColorFilter(0xFFFFFFFF, (i << 16) | (i << 8) | (i));
	        }
        }
		
		if (width == 0 && height == 0) {
			return;
		}
		create(width, height);
	}
	
	public boolean create(int width, int height) {
		mImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		g = new Canvas(mImage);
        g.drawColor(Color.TRANSPARENT);
        FFFLog.traceMemory("FFImage.create" + width + "," + height);
		return true;
	}
	
	public boolean loadFile(FFFFile file, int ox, int oy) {
		if (!file.isOk()) {
			throw new RuntimeException("FFFImage::loadFile 失败!" + file.getFileName());
		}
		//FIMXE:
		if (mImage == null) {
			mImage = Bitmap.createBitmap(file.mImage.getWidth(), file.mImage.getHeight(), Bitmap.Config.ARGB_8888);
		} else {
			mImage.eraseColor(Color.TRANSPARENT);
		}
		g = new Canvas(mImage);
		FFFLog.traceMemory("FFFImage::loadFile -> file.bitmap == " + file.mImage);
		g.drawBitmap(file.mImage, ox, oy, bitmapPaint);
		return true;
	}
	
	public void recycle() {
		if (mImage != null) {
			mImage.recycle();
			mImage = null;
			FFFLog.traceMemory("FFFImage::recycle");
		}
	}
	
	public int getWidth() { 
		return mImage.getWidth(); 
	}
	
	public int getHeight() { 
		return mImage.getHeight();
	}
	
	public void clear() {
		mImage.eraseColor(Color.TRANSPARENT);
	}

	public boolean loadImage(String name, int ox, int oy) {
		String path = FFFConfig.CGPATH + name;
		FFFFile file = new FFFFile(mRes, path);
		if (!file.isOk()) {
			file.close();
			return false;
		}
		boolean ret = loadFile(file, ox, oy);
		file.close();
		return ret;
	}

	public boolean loadRule(String name, int ox, int oy) {
		String path = FFFConfig.RULEPATH + name;
		FFFFile file = new FFFFile(mRes, path);
		if (!file.isOk()) {
			file.close();
			return false;
		}
		boolean ret = loadFile(file, ox, oy);
		file.close();
		return ret;
	}
	
	public void fillRect(FFFRectangle rect, int color) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(0xff000000 | color);
		g.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, paint);
	}
	
	public void copy(FFFImage image, FFFRectangle rect) {
		g.drawBitmap(image.mImage, 
			new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
			new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
			bitmapPaint);
	}
	
	public void mixImage(FFFImage image, FFFRectangle rect, int trans_color) {
		g.drawBitmap(image.mImage, 
			new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
			new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
			bitmapPaint);
	}
	
	public void drawRect(FFFRectangle rect, int color) {
		int width = rect.width;
		int height = rect.height;
		fillRect(new FFFRectangle(rect.x, rect.y, width, 1), color);
		fillRect(new FFFRectangle(rect.x, rect.y, 1, height), color);
		fillRect(new FFFRectangle(rect.x + rect.width - 1, rect.y, 1, height), color);
		fillRect(new FFFRectangle(rect.x, rect.y + rect.height - 1, width, 1), color);
	}
	
	public void fillHalfToneRect(FFFRectangle rect0) {
		if (rect0 == null || rect0.isEmpty()) {
			return;
		}
		FFFRectangle rectImage = new FFFRectangle(
			0, 0, mImage.getWidth(), mImage.getHeight());
		FFFRectangle rect = rect0.intersection(rectImage);
		if (rect == null || rect.isEmpty()) {
			return;
		}
		int[] pixcels = new int[rect.width * rect.height];
		/**
		 * FIXME: NOTE rect must not be empty OR 
		 *  overflow image rect
		 */
		mImage.getPixels(pixcels, 0, rect.width,
				rect.x, rect.y, rect.width, rect.height);
		for (int i = 0; i < pixcels.length; i++) {
			int p = pixcels[i];
			int b = (p & 0x000000FF);
			int g = (p & 0x0000FF00) >>> 8;
			int r = (p & 0x00FF0000) >>> 16;
			int a = (p & 0xFF000000) >>> 24;
			b /= 2;
			g /= 2;
			r /= 2;
			pixcels[i] = (a << 24) | (r << 16) | (g << 8) | b;
		}
		mImage.setPixels(pixcels, 0, rect.width,
				rect.x, rect.y, rect.width, rect.height);
	}

	public void drawFrameRect(FFFRectangle rect, int color) {
		drawRect(new FFFRectangle(rect.x, rect.y + 1, rect.width, rect.height - 2), color);
		drawRect(new FFFRectangle(rect.x + 1, rect.y, rect.width - 2, rect.height), color);
		fillHalfToneRect(new FFFRectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4));
	}
	
	public void drawText(FFFFont hFont, int x1, int y1, String str, int color) {
		//FIXME:NO font
		paint.setColor(0xff000000 | color);
		float textSize = 0.5f;
		for (int i = 0; i < 1000; i++) {
			paint.setTextSize(textSize);
			if (-paint.ascent() + paint.descent() > FFFConfig.MessageFont) {
				textSize -= 0.5f;
				break;
			} else {
				textSize += 0.5f;
			}
		}
		paint.setTextSize(textSize);
		// FIMXE:(x1, y1)坐标为文字的左上角
		paint.setTextAlign(Paint.Align.LEFT);
		float h = -paint.ascent() + paint.descent();
		if (false) {
			g.drawText(str, x1, y1 + h, paint);
		} else {
			for (int i = 0; i < str.length(); i++) {
				g.drawText(str, i, i + 1, x1 + i * FFFConfig.MessageFont, y1 + h, paint);
			}
		}
	}
	
	public void wipeIn(FFFImage image, FFFRectangle rect, int count) {	
		count = count % 8;
		if (count < 0) {
			count = count + 8;
		}
		FFFRectangle rect2 = rect.cloneRect();
		if (rect2.x < 0)
			rect2.x = 0;
		if (rect2.x + rect2.width > image.getWidth())
			rect2.width = image.getWidth() - rect2.x;
		if (rect2.x + rect2.width > this.getWidth())
			rect2.width = this.getWidth() - rect2.x;
		int w = rect2.width;
		int y = 0;
		int pixels_position = 0;
		while (pixels_position < rect2.width * rect2.height) {
			if (y % 8 <= count) {
				g.drawBitmap(image.mImage,
						new Rect(rect.x, rect.y + y, rect.x + rect.width, rect.y + y + 1), 
						new Rect(rect.x, rect.y + y, rect.x + rect.width, rect.y + y + 1), 
						bitmapPaint);
				pixels_position += w;
			} else {
				pixels_position += w;
			}
			y++;
		}
	}
	
	public void wipeOut(FFFRectangle rect, int count) {
		FFFRectangle rect2 = rect.cloneRect();
		if (rect2.x < 0)
			rect2.y = 0;
		if (rect2.x + rect2.width > this.getWidth())
			rect2.width = this.getWidth() - rect2.x;
		int w = rect2.width;
		int y = 0;
		count = count % 8;
		int pixels_position = 0;
		while (pixels_position < rect2.width * rect2.height) {
			if (y % 8 == count) {
				fadePaint.setColor(Color.BLACK);
				g.drawRect(
						new Rect(rect.x, rect.y + y, rect.x + rect.width, rect.y + y + 1), 
						fadePaint);
				pixels_position += w;
			} else {
				pixels_position += w;
			}
			y++;
		}
	}
	
	public boolean wipeIn2(FFFImage image, FFFRectangle rect, int count) {
		int width = rect.width;
		int height = rect.height;
		boolean update = false;
		int npos = count * 4;
		for (int y = 0; y < height; y += 32) {
			if (npos >= 0 && npos < 32) {
				int ypos = y + npos;
				copy(image, new FFFRectangle(0, ypos, width, 4));
				update = true;
			}
			npos -= 4;
		}
		return update;
	}
	
	public boolean wipeOut2(FFFRectangle rect, int count) {
		int width = rect.width;
		int height = rect.height;
		boolean update = false;
		int npos = count * 4;
		for (int y = 0; y < height; y += 32) {
			if (npos >= 0 && npos < 32) {
				int ypos = y + npos;
				fillRect(new FFFRectangle(0, ypos, width, 4), 0);
				update = true;
			}
			npos -= 4;
		}
		return update;
	}
	
	public void fadeCvt(FFFImage image, FFFRectangle rect, int[] cvt) {
		int[] pixels = new int[rect.width * rect.height];
		image.mImage.getPixels(pixels, 0, rect.width,
				rect.x, rect.y, rect.width, rect.height);
		int[] pixels_bg = new int[rect.width * rect.height];
		mImage.getPixels(pixels_bg, 0, rect.width,
				rect.x, rect.y, rect.width, rect.height);
		int[] pixels2 = new int[rect.width * rect.height];
		int pixels_position = 0; 
		int pixels_bg_position = 0;
		int pixels2_postion = 0;
		while (pixels_position < pixels.length && 
			pixels_bg_position < pixels_bg.length) {
			int p = pixels[pixels_position++];
			int p2 = pixels_bg[pixels_bg_position++];
			int a = (p & 0xFF000000) >>> 24;
			if (a == 0) {
				pixels2[pixels2_postion++] = p2;
			} else {
				int b = (p & 0x000000FF);
				int g = (p & 0x0000FF00) >>> 8;
				int r = (p & 0x00FF0000) >>> 16;
				b = cvt[b];
				g = cvt[g];
				r = cvt[r];
				pixels2[pixels2_postion++] = ((a << 24) | (r << 16) | (g << 8) | b);
			}
		}
		mImage.setPixels(pixels2, 0, rect.width,
			rect.x, rect.y, rect.width, rect.height);
	}
	
	public void fadeFromBlack(FFFImage image, FFFRectangle rect, int count) {
		if (false) {
			int[] cvt = new int[256];
			count++;
			for (int i = 0; i < 256; i++) {
				cvt[i] = ((i * count) / 16) & 0xFF;
			}
			fadeCvt(image, rect, cvt);
		} else {
			fadePaint.setColor(Color.BLACK);
			g.drawRect(
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					fadePaint);
			bitmapPaint.setAlpha((count + 1) * (256 / 16) - 1);
			g.drawBitmap(image.mImage,
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					bitmapPaint);
			bitmapPaint.setAlpha(0xff);
		}
	}
	
	public void fadeToBlack(FFFImage image, FFFRectangle rect, int count) {
		if (false) {
			int[] cvt = new int[256];
			count = 15 - count;
			for (int i = 0; i < 256; i++) {
				cvt[i] = ((i * count) / 16) & 0xFF;
			}
			fadeCvt(image, rect, cvt);
		} else {
			g.drawBitmap(image.mImage,
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					bitmapPaint);
			fadePaint.setColor(Color.BLACK);
			fadePaint.setAlpha((count + 1) * (256 / 16) - 1);
			g.drawRect(
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					fadePaint);
			fadePaint.setAlpha(0xff);
		}
	}
	
	public void fadeFromWhite(FFFImage image, FFFRectangle rect, int count) {
		if (false) {
			int[] cvt = new int[256];
			count++;
			int	level = 255 * (16 - count);
			for (int i = 0; i < 256; i++) {
				cvt[i] = ((i * count + level) / 16) & 0xFF;
			}
			fadeCvt(image, rect, cvt);
		} else {
			fadePaint.setColor(Color.WHITE);
			g.drawRect(
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					fadePaint);
			bitmapPaint.setAlpha((count + 1) * (256 / 16) - 1);
			g.drawBitmap(image.mImage,
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					bitmapPaint);
			bitmapPaint.setAlpha(0xff);
		}
	}

	public void fadeToWhite(FFFImage image, FFFRectangle rect, int count) {
		if (false) {
			int[] cvt = new int[256];
			count = 15 - count;
			int level = 255 * (16 - count);
			for (int i = 0; i < 256; i++) {
				cvt[i] = ((i * count + level) / 16) & 0xFF;
			}
			fadeCvt(image, rect, cvt);
		} else {
			g.drawBitmap(image.mImage,
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					bitmapPaint);
			fadePaint.setColor(Color.WHITE);
			fadePaint.setAlpha((count + 1) * (256 / 16) - 1);
			g.drawRect(
					new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
					fadePaint);
			fadePaint.setAlpha(0xff);
		}
	}
	
	private static int[] BitMask = {
		0x2080,	// 0010 0000 1000 0000
		0xa0a0,	// 1010 0000 1010 0000
		0xa1a4,	// 1010 0001 1010 0100
		0xa5a5,	// 1010 0101 1010 0101
		0xada7,	// 1010 1101 1010 0111
		0xafaf,	// 1010 1111 1010 1111
		0xefbf,	// 1110 1111 1011 1111
		0xffff,	// 1111 1111 1111 1111
	};
	private static int[] XMask = {
		0xf000, 0x0f00, 0x00f0, 0x000f,
	};
	private static int[] YMask = {
		0x8888, 0x4444, 0x2222, 0x1111,
	};
	
	public void mix(FFFImage image, FFFRectangle rect, int count, FFFImage mixImage, FFFImage maskImage) {		
		if (mixImage == null || !USE_AVOIDXFERMODE) {
			count = count % 8;
			if (count < 0) {
				count = count + 8;
			}
			FFFLog.trace("FFFImage::mix()" + count);
			
			FFFRectangle rect2 = rect.cloneRect();
			if (rect2.x < 0)
				rect2.x = 0;
			if (rect2.x + rect2.width > image.getWidth())
				rect2.width = image.getWidth() - rect2.x;
			if (rect2.x + rect2.width > this.getWidth())
				rect2.width = this.getWidth() - rect2.x;
			int[] pixels = new int[rect2.width * rect2.height];
			mImage.getPixels(pixels, 0, rect2.width, 
					rect2.x, rect2.y, rect2.width, rect2.height);
			int[] img_pixels = new int[rect2.width * rect2.height];
			image.mImage.getPixels(img_pixels, 0, rect2.width, 
					rect2.x, rect2.y, rect2.width, rect2.height);
			int[] pixels2 = new int[rect2.width * rect2.height];
			int w = rect2.width;
			int pixels_position = 0;
			int img_pixels_position = 0;
			int pixels2_position = 0;
			for (int y = 0; pixels_position < pixels.length; y++) {
				for (int x = 0; x < w; x++) {
					int mask = (int)(BitMask[count]) & (int)(YMask[y & 3]);
					int p = pixels[pixels_position++];
					int p2 = img_pixels[img_pixels_position++];	
					if ((p2 & 0xFF000000) == 0) {
						pixels2[pixels2_position++] = p;
					} else if ((mask & XMask[x & 3]) != 0) {
						pixels2[pixels2_position++] = p2;
					} else {
						pixels2[pixels2_position++] = p;
					}
				}
			}
			mImage.setPixels(pixels2, 0, rect.width,
					rect.x, rect.y, rect.width, rect.height);			
		
		} else if (USE_AVOIDXFERMODE) {
			count = count % 8;
			if (count < 0) {
				count = count + 8;
			}
			FFFLog.trace("FFFImage::mix() accel version" + count);
			
			int threadhold = (count + 1) * (256 / 8) - 1;
	        g.drawBitmap(this.mImage, 
	        		new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
		        	new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height),
		        	paintMix);
	        int sc = g.saveLayer(0, 0, this.getWidth(), this.getHeight(), null, Canvas.ALL_SAVE_FLAG);
	        
	        paintMix.setColorFilter(filters[threadhold]);
	        g.drawBitmap(mixImage.mImage, 
	        		new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
		        	new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height),
		        	paintMix);
	        paintMix.setColorFilter(null);
	        
	        paintMix.setXfermode(mode1);
	        g.drawBitmap(maskImage.mImage, 
	        		new Rect(0, 0, rect.width, rect.height), 
		        	new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height),
		        	paintMix);
	        
	        paintMix.setXfermode(mode2);
	        g.drawBitmap(image.mImage, 
	        		new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
		        	new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height),
		        	paintMix);
	        
	        paintMix.setXfermode(null);
	        g.restoreToCount(sc);
			
		}
	}
}
