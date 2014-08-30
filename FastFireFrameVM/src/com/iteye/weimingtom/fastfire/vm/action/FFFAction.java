package com.iteye.weimingtom.fastfire.vm.action;

import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFAction {
	private void error() {
		try {
			throw new RuntimeException("abstract function");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean scriptRunning;
	protected FFFMainWin mParent;
	protected int mParam1;
	protected int mParam2;
	
	public boolean isScriptRunning() { 
		return scriptRunning; 
	}
	
	public FFFAction() {
		this(false);
	}
	
	public FFFAction(boolean scriptRun) {
		scriptRunning = scriptRun;
	}
	
	public void initialize(FFFMainWin parent) {
		initialize(parent, 0, 0);
	}
	
	public void initialize(FFFMainWin parent, int param1) {
		initialize(parent, param1, 0);
	}	
	
	public void initialize(FFFMainWin parent, int param1, int param2) {
		this.mParent = parent;
		this.mParam1 = param1;
		this.mParam2 = param2;
	}
	
	public void onActionPause() {
		error();
	}
	
	public void onActionResume() {
		error();
	}
	
	public void onActionLButtonDown(FFFPoint point) {
		error();
	}
	
	public void onActionLButtonUp(FFFPoint point) {
		error();
	}
	
	public void onActionRButtonDown(FFFPoint point) {
		error();
	}
	
	public void onActionRButtonUp(FFFPoint point) {
		error();
	}
	
	public void onActionMouseMove(FFFPoint point) {
		error();
	}
	
	public void onActionKeyDown(int key) {
		error();
	}
	
	public void onActionTimedOut(int timerId) {
		error();
	}

	public boolean onActionIdleAction() {
		error();
		return false;
	}

	public void onActionMusicDone(int music) {
		error();
	}
	
	public void onActionWipeDone() {
		error();
	}
	
	public void onActionWaveDone() {
		error();
	}
}
