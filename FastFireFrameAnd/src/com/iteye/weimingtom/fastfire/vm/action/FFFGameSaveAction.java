package com.iteye.weimingtom.fastfire.vm.action;

public class FFFGameSaveAction extends FFFGameLoadSaveAction{

	@Override
	protected void doLoadSave() {
		mParent.saveGame(Selection, Flags);
	}
}
