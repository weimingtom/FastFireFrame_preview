package com.iteye.weimingtom.fastfire.vm.action;

public class FFFGameLoadAction extends FFFGameLoadSaveAction {

	@Override
	protected void doLoadSave() {
		mParent.loadGame(Selection);
	}
}
