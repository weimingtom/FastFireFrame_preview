package com.iteye.weimingtom.fastfire.port.file;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class FFFFile {
	private FFFResource res;
	private String mFilename;
	private boolean isOK = false;
	// text
	private static final boolean DEBUG_GETSTRING = false;
	private String[] lines;
	private int currentLine;
	// image
	public Bitmap mImage;
	// binary
	private ByteBuffer mBytes;
	
	public FFFFile(FFFResource res, String file) {
		this.res = res;
		open(file);
	}
	
	private boolean open(String file) {
		//FIXME:ADD
		//添加，方便调试
		this.mFilename = file;
		if (FFFResource.USE_CACHE) {
			mImage = res.imageMap.get(file);
		} else {
			mImage = res.loadImageNoCache(file);
		}
		if (mImage != null) {
			FFFLog.trace("FFFFile::open() open image file success:" + file);
			isOK = true;
			return true;
		} else {
			//return false;
			mBytes = res.dataMap.get(file);
			if (mBytes != null) {
				FFFLog.trace("FFFFile::open() open hex file success:" + file);
				isOK = true;
				return true;
			} else {
				String content = res.textMap.get(file);
				if (content != null) {
					FFFLog.trace("FFFFile::open() open text file success:" + file);
					this.lines = content.split("\\n");
					this.currentLine = 0;
					isOK = true;
					return true;
				}
				return false;
			}
		}
	}
	
	public boolean close() {
		mFilename = null;
		isOK = false;
		lines = null;
		currentLine = 0;
		if (!FFFResource.USE_CACHE && mImage != null && !mImage.isRecycled()) {
			mImage.recycle();
		}
		mImage = null;
		mBytes = null;
		return true;
	}
	
	public boolean isOk() { 
		return this.isOK; 
	}
	
	//FIXME:
	public int read(ByteBuffer bytes, int length) {
		int pos = bytes.position();
		mBytes.position(0);
		byte[] b = new byte[length];
		mBytes.get(b, 0, length); //FIXME:
		bytes.put(b);
		return bytes.position() - pos;
	}
	
	//FIXME:
	public int getFileSize() {
		if (mBytes != null) {
			return mBytes.array().length; //FIXME:
		}
		return 0;
	}
	
	public String getLineString() {
		String str;
		if (this.currentLine >= this.lines.length) {
			str = null;
		} else {
			str = this.lines[this.currentLine];
			this.currentLine++;
		}
		return str;
	}
	
	public String getString() {
		String readBuffer = getLineString();
		if (readBuffer == null) {
			return null;
		}
		if (DEBUG_GETSTRING) {
			FFFLog.trace("_readBuffer:" + readBuffer);
			if (readBuffer.startsWith("goto")) {
				try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		while (readBuffer.length() > 0 && 
			(readBuffer.endsWith("\n") || 
			 readBuffer.endsWith("\r"))) {
			readBuffer = readBuffer.substring(0, readBuffer.length() - 1);
		}
		return readBuffer;
	}
	
	public String getFileName() {
		return this.mFilename; 
	}
	
	public int getLineNo() {
		return this.currentLine; 
	}
}
