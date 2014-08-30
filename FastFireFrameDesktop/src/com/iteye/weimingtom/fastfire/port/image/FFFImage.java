package com.iteye.weimingtom.fastfire.port.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.iteye.weimingtom.fastfire.model.FFFConfig;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.file.FFFFile;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;

public class FFFImage {	
	public BufferedImage mImage;
	private FFFResource mRes;
	
	public FFFImage(FFFResource res, int width, int height) {
		mRes = res;
		if (width == 0 && height == 0) {
			return;
		}
		create(width, height);
	}
	
	public boolean create(int width, int height) {
		mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return true;
	}
	
	public boolean loadFile(FFFFile file, int ox, int oy) {
		if (!file.isOk()) {
			throw new RuntimeException("FFFImage::loadFile 失败!" + file.getFileName());
		}
		//FIMXE:
		if (mImage == null) {
			mImage = new BufferedImage(file.mImage.getWidth(), file.mImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		} else {
			mImage = new BufferedImage(mImage.getWidth(), mImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		FFFLog.trace("FFFImage::loadFile -> file.bitmap == " + file.mImage);
		Graphics g = mImage.getGraphics();
		g.drawImage(file.mImage, ox, oy, null);
		g.dispose();
		return true;
	}
	
	public void recycle() {
		
	}
	
	public int getWidth() { 
		return mImage.getWidth(); 
	}
	
	public int getHeight() { 
		return mImage.getHeight();
	}
	
	public void clear() {
		Graphics g = mImage.getGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, mImage.getWidth(), mImage.getHeight());
		g.dispose();
	}

	public boolean loadImage(String name, int ox, int oy) {
		String path = FFFConfig.CGPATH + name;
		FFFFile file = new FFFFile(mRes, path);
		if (!file.isOk())
			return false;
		return loadFile(file, ox, oy);
	}

	public boolean loadRule(String name, int ox, int oy) {
		String path = FFFConfig.RULEPATH + name;
		FFFFile file = new FFFFile(mRes, path);
		if (!file.isOk())
			return false;
		return loadFile(file, ox, oy);
	}
	
	public void fillRect(FFFRectangle rect, int color) {
		Graphics g = mImage.getGraphics();
		g.setColor(new Color(color));
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g.dispose();
	}
	
	public void copy(FFFImage image, FFFRectangle rect) {
		Graphics g = mImage.getGraphics();
		g.drawImage(image.mImage, 
			rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 
			rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 
			null);
		g.dispose();
	}
	
	public void mixImage(FFFImage image, FFFRectangle rect, int trans_color) {
		Graphics g = mImage.getGraphics();
		g.drawImage(image.mImage, 
				rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 
				rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 
				null);
		g.dispose();
	}
	
	public void drawRect(FFFRectangle rect, int color) {
		int width = rect.width;
		int height = rect.height;
		fillRect(new FFFRectangle(rect.x, rect.y, width, 1), color);
		fillRect(new FFFRectangle(rect.x, rect.y, 1, height), color);
		fillRect(new FFFRectangle(rect.x + rect.width - 1, rect.y, 1, height), color);
		fillRect(new FFFRectangle(rect.x, rect.y + rect.height - 1, width, 1), color);
	}
	
	public void fillHalfToneRect(FFFRectangle rect) {
		int[] pixcels = new int[rect.width * rect.height];
		mImage.getRGB(rect.x, rect.y, rect.width, rect.height, 
				pixcels, 0, rect.width);
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
		mImage.setRGB(rect.x, rect.y, rect.width, rect.height, 
				pixcels, 0, rect.width);
	}

	public void drawFrameRect(FFFRectangle rect, int color) {
		drawRect(new FFFRectangle(rect.x, rect.y + 1, rect.width, rect.height - 2), color);
		drawRect(new FFFRectangle(rect.x + 1, rect.y, rect.width - 2, rect.height), color);
		fillHalfToneRect(new FFFRectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4));
	}
	
	public void drawText(FFFFont hFont, int x1, int y1, String str, int color) {
		Graphics g = mImage.getGraphics();
		//FIXME:NO font
		g.setColor(new Color(color));
		int fontSize = 0;
		while (true) {
			fontSize++;
			g.setFont(new Font(null, 0, fontSize));
			if (g.getFontMetrics().getHeight() > FFFConfig.MessageFont) {
				break;
			}
		}
		g.setFont(new Font(null, 0, fontSize - 1));
		// FIMXE:(x1, y1)坐标为文字的左上角
		FontMetrics m = g.getFontMetrics();
		int h = m.getAscent();// + m.getDescent() + m.getLeading();
		//int h = m.getHeight();
		g.drawString(str, x1, y1 + h);
		g.dispose();
	}
	
	public void wipeIn(FFFImage image, FFFRectangle rect, int count) {
		Graphics g = mImage.getGraphics();		
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
		int[] pixels = new int[rect2.width * rect2.height];
		mImage.getRGB(rect2.x, rect2.y, rect2.width, rect2.height, pixels, 0, rect2.width);
		int[] img_pixels = new int[rect2.width * rect2.height];
		image.mImage.getRGB(rect2.x, rect2.y, rect2.width, rect2.height, img_pixels, 0, rect2.width);
		int[] pixels2 = new int[rect2.width * rect2.height];
		int w = rect2.width;
		int y = 0;
		int pixels_position = 0;
		int pixels2_position = 0;
		int img_pixels_position = 0;
		while (pixels_position < pixels.length) {
			if (y % 8 <= count) {
				//FFFLog.trace("draw " + y + "/" + count + "," + (rect.x) + "," + (rect.y + y) + "," + (rect.x + rect.width) + "," + (rect.y + y + 1));
				g.drawImage(image.mImage,
						rect.x, rect.y + y, rect.x + rect.width, rect.y + y + 1, 
						rect.x, rect.y + y, rect.x + rect.width, rect.y + y + 1, 
						null);
				//System.arraycopy(img_pixels, img_pixels_position, pixels2, pixels2_position, w);
				pixels2_position += w;
				img_pixels_position += w;
				pixels_position += w;
			} else {
				//System.arraycopy(pixels, pixels_position, pixels2, pixels2_position, w);
				pixels2_position += w;
				img_pixels_position += w;
				pixels_position += w;
			}
			y++;
		}
		//_bmd.setRGB(rect2.x, rect2.y, rect2.width, rect2.height, pixels2, 0, rect2.width);
		g.dispose();
	}
	
	public void wipeOut(FFFRectangle rect, int count) {
		FFFRectangle rect2 = rect.cloneRect();
		if (rect2.x < 0)
			rect2.y = 0;
		if (rect2.x + rect2.width > this.getWidth())
			rect2.width = this.getWidth() - rect2.x;
		int[] pixels = new int[rect2.width * rect2.height];
		mImage.getRGB(rect2.x, rect2.y, rect2.width, rect2.height, pixels, 0, rect2.width);
		int[] pixels2 = new int[rect2.width * rect2.height];
		int w = rect2.width;
		int y = 0;
		count = count % 8;
		int[] temp2 = new int[w];
		for (int i = 0; i < w; i++) {
			temp2[i] = 0xFF000000;
		}
		int pixels_position = 0;
		int pixels2_position = 0;
		while (pixels_position < pixels.length) {
			if (y % 8 == count) {
				System.arraycopy(temp2, 0, pixels2, pixels2_position, w);
				pixels2_position += w;
				pixels_position += w;
			} else {
				System.arraycopy(pixels, pixels_position, pixels2, pixels2_position, w);
				pixels2_position += w;
				pixels_position += w;
			}
			y++;
		}
		mImage.setRGB(rect2.x, rect2.y, rect2.width, rect2.height, pixels2, 0, rect2.width);		
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
		image.mImage.getRGB(rect.x, rect.y, rect.width, rect.height, pixels, 0, rect.width);
		int[] pixels_bg = new int[rect.width * rect.height];
		mImage.getRGB(rect.x, rect.y, rect.width, rect.height, pixels_bg, 0, rect.width);
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
		mImage.setRGB(rect.x, rect.y, rect.width, rect.height, pixels2, 0, rect.width);
	}
	
	public void fadeFromBlack(FFFImage image, FFFRectangle rect, int count) {
		int[] cvt = new int[256];
		count++;
		for (int i = 0; i < 256; i++) {
			cvt[i] = ((i * count) / 16) & 0xFF;
		}
		fadeCvt(image, rect, cvt);
	}
	
	public void fadeToBlack(FFFImage image, FFFRectangle rect, int count) {
		int[] cvt = new int[256];
		count = 15 - count;
		for (int i = 0; i < 256; i++) {
			cvt[i] = ((i * count) / 16) & 0xFF;
		}
		fadeCvt(image, rect, cvt);
	}
	
	public void fadeFromWhite(FFFImage image, FFFRectangle rect, int count) {
		int[] cvt = new int[256];
		count++;
		int	level = 255 * (16 - count);
		for (int i = 0; i < 256; i++) {
			cvt[i] = ((i * count + level) / 16) & 0xFF;
		}
		fadeCvt(image, rect, cvt);
	}

	public void fadeToWhite(FFFImage image, FFFRectangle rect, int count) {
		int[] cvt = new int[256];
		count = 15 - count;
		int level = 255 * (16 - count);
		for (int i = 0; i < 256; i++) {
			cvt[i] = ((i * count + level) / 16) & 0xFF;
		}
		fadeCvt(image, rect, cvt);
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
		int[] pixels = new int[rect2.width * rect2.height];
		mImage.getRGB(rect2.x, rect2.y, rect2.width, rect2.height, pixels, 0, rect2.width);
		int[] img_pixels = new int[rect2.width * rect2.height];
		image.mImage.getRGB(rect2.x, rect2.y, rect2.width, rect2.height, img_pixels, 0, rect2.width);
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
		mImage.setRGB(rect.x, rect.y, rect.width, rect.height, pixels2, 0, rect.width);
	}
}
