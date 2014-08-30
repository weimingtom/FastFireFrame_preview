package com.iteye.weimingtom.fastfire.port.window;

import com.iteye.weimingtom.fastfire.model.FFFPoint;

public class FFFWindowAdapter {
	private FFFWindow mWindow;
	
	public void setWindow(FFFWindow window) {
		this.mWindow = window;
	}

	public FFFWindow getWindow() {
		return this.mWindow;
	}
	
	protected void onLButtonUp(FFFPoint point) {
		
	}
	
	protected void onLButtonDown(FFFPoint point) {
		
	}

	protected void onRButtonUp(FFFPoint point) {
		
	}

	protected void onMouseMove(FFFPoint point) {
		
	}

	protected boolean onIdle(int count) {
		return false;
	}
	
	protected boolean onCreate() {
		return true;
	}
	
	/**
	 * FIXME:unnecessary
	 */
	protected void onPaint() {
		
	}
	
	protected void onDestroy() {
		
	}
	
	protected void onKeyDown(int key) {
		
	}
}
