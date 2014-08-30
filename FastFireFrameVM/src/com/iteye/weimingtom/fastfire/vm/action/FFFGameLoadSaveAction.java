package com.iteye.weimingtom.fastfire.vm.action;

import com.iteye.weimingtom.fastfire.model.FFFConfig;
import com.iteye.weimingtom.fastfire.model.FFFKey;
import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public abstract class FFFGameLoadSaveAction extends FFFAction {
	protected int Selection;
	protected boolean Pressed;
	protected boolean CancelPressed;
	protected int Flags;
	
	public FFFGameLoadSaveAction() {
		
	}
	
	@Override 
	public void initialize(FFFMainWin parent, int param1, int param2) {
		super.initialize(parent, param1, param2);
		Selection = -1;
		Pressed = false;
		CancelPressed = false;
		Flags = 0;
	}
	
	@Override
	public void onActionPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionLButtonDown(FFFPoint point) {
		Pressed = true;
	}

	@Override
	public void onActionLButtonUp(FFFPoint point) {
		Pressed = false;
		if (Selection >= 0) {
			doLoadSave();
		}
	}

	@Override
	public void onActionRButtonDown(FFFPoint point) {
		CancelPressed = true;
	}

	@Override
	public void onActionRButtonUp(FFFPoint point) {
		if (CancelPressed) {
			mParent.cancelLoadSaveMenu(Flags);
		}
	}

	@Override
	public void onActionMouseMove(FFFPoint point) {
		int sel = mParent.getLoadSaveSelect(point);
		if (sel != Selection) {
			mParent.selectLoadSaveMenu(Selection, false);
			Selection = sel;
			mParent.selectLoadSaveMenu(Selection, true);
		}
	}

	@Override
	public void onActionKeyDown(int key) {
		int sel;
		switch (key) {
			case FFFKey.ENTER:
			case FFFKey.SPACE: // 执行装入存储
				if (Selection >= 0)
					doLoadSave();
				break;
			
			case FFFKey.ESCAPE: // 取消
				mParent.cancelLoadSaveMenu(Flags);
				break;

			case FFFKey.UP:	// 选前一项
				{
					sel = mParent.prevLoadSaveSelect(Selection);
					if (sel != Selection) 
					{
						mParent.selectLoadSaveMenu(Selection, false);
						Selection = sel;
						mParent.selectLoadSaveMenu(Selection, true);
					}
				}
				break;

			case FFFKey.DOWN:
				{
					sel = mParent.nextLoadSaveSelect(Selection);
					if (sel != Selection) 
					{
						mParent.selectLoadSaveMenu(Selection, false);
						Selection = sel;
						mParent.selectLoadSaveMenu(Selection, true);
					}
				}
				break;
		}
	}

	@Override
	public void onActionTimedOut(int timerId) {
		switch (timerId) {
			case FFFConfig.TimerSleep:
				Flags |= FFFConfig.IS_TIMEDOUT;
				break;
		}
	}

	@Override
	public boolean onActionIdleAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onActionMusicDone(int music) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionWipeDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionWaveDone() {
		// TODO Auto-generated method stub
		
	}

	protected abstract void doLoadSave();
}
