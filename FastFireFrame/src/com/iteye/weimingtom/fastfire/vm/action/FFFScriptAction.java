package com.iteye.weimingtom.fastfire.vm.action;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.iteye.weimingtom.fastfire.mkscript.MkScriptBufferUtil;
import com.iteye.weimingtom.fastfire.mkscript.MkScriptType;
import com.iteye.weimingtom.fastfire.mkscript.command.CalcValueCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.ClearCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.Command;
import com.iteye.weimingtom.fastfire.mkscript.command.ExecCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.GotoCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.IfCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.LoadCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.MenuCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.MenuItemCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.ModeCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.MusicCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.SetValueCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.SleepCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.SoundCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.TextCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.UpdateCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.WipeinCommand;
import com.iteye.weimingtom.fastfire.mkscript.command.WipeoutCommand;
import com.iteye.weimingtom.fastfire.model.FFFConfig;
import com.iteye.weimingtom.fastfire.model.FFFParams;
import com.iteye.weimingtom.fastfire.model.FFFKey;
import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.model.FFFScriptData;
import com.iteye.weimingtom.fastfire.port.file.FFFFile;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;
import com.iteye.weimingtom.fastfire.vm.window.FFFMainWin;

public class FFFScriptAction extends FFFAction {
	private static final boolean NO_BYTEARRY_CLEAR = true;

	public static final int BreakGame = -1;
	public static final int Continue = 0;
	public static final int WaitNextIdle = 1;
	public static final int WaitKeyPressed = 2;
	public static final int WaitTimeOut = 3;
	public static final int WaitMenuDone = 4;
	public static final int WaitWipeDone = 5;
	public static final int WaitWaveDone = 6;
	
	protected static final int FileNoError = 0;
	protected static final int FileCannotOpen = 1;
	protected static final int NotEnoughMemory = 2;
	protected static final int FileCannotRead = 3;
	
	public FFFParams Params = new FFFParams();
	protected boolean Pressed;
	protected int MenuSelect;
	protected int MenuAnserAddr;
	protected int PlayMode;
	protected ByteBuffer script_buffer;// = new ByteArray();
	protected FFFScriptData current = new FFFScriptData();
	protected int position;
	protected int status;
	
	private FFFResource res;
	
	public FFFScriptAction(FFFResource res) {
		super(true);
		this.res = res;
	}
	
	public boolean isSaveLoadOK() {
		return PlayMode != MkScriptType.MODE_SYSTEM && 
			(status == WaitKeyPressed || status == WaitMenuDone);
	}
	
	protected Command getCommand() {
		script_buffer.position(current.commands + this.position);
		FFFLog.trace("FFFScriptAction::getCommand() at " + this.position + " in script_buffer" +
			" , length == " + script_buffer.array().length); 
		MkScriptBufferUtil.readCommand((byte)0, script_buffer);
		Command p = MkScriptBufferUtil.getCommand();
		this.position += p.size;
		FFFLog.trace("p.size == " + p.size + " this.position == " + this.position);
		return p;
	}
	
	protected void gotoCommand2(int next) {
		this.position = next;
	}
	
	protected String getString(int size) {
		FFFLog.trace("FFFScriptAction::getString() " + size);
		if (size == 0)
			return null;
		script_buffer.position(current.commands + this.position);
		//FIXME:
		byte[] bytes_temp = new byte[size];
		script_buffer.get(bytes_temp);
		int bytes_len = 0;
		for (bytes_len = 0; bytes_len < bytes_temp.length; bytes_len++) {
			if (bytes_temp[bytes_len] == 0) {
				break;
			}
		}
		byte[] bytes = new byte[bytes_len];
		System.arraycopy(bytes_temp, 0, bytes, 0, bytes_len);
		String p = null;
		try {
			p = new String(bytes, "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.position += size;
		return p;
	}
	
	protected int getValue(int value_addr) {
		return Params.value_tab[value_addr];
	}
	
	protected int getValue2(int addr, int flag) {
		if (flag != 0)
			return addr;
		return Params.value_tab[addr];
	}
	
	protected void setValue(int value_addr, int set_value) {
		Params.value_tab[value_addr] = set_value;
	}
	
	protected void calcValue(int value_addr, int calc_value) {
		Params.value_tab[value_addr] += calc_value;
	}
	
	protected void clearAllValues() {
		Params.clear();
	}
	
	@Override 
	public void initialize(FFFMainWin parent, int param1, int param2) {
		super.initialize(parent, param1, param2);
		status = Continue;
		Pressed = false;
		MenuSelect = -1;
		PlayMode = param1;
		Params.clear();
		script_buffer = null;
	}
	
	@Override 
	public void onActionPause() {
		switch (status) {
		case WaitMenuDone:
			if (MenuSelect >= 0) {
				mParent.selectMenu(MenuSelect, false);
				MenuSelect = -1;
			}
			break;
		}
	}
	
	public FFFPoint getCursorPos() {
		//FIXME:
		return null;
	}
	
	@Override 
	public void onActionResume() {
		switch (status) {
		case WaitMenuDone: {
				FFFPoint point = getCursorPos();
//				Parent.ScreenToClient(point);
				MenuSelect = mParent.getMenuSelect(point);
				if (MenuSelect >= 0)
					mParent.selectMenu(MenuSelect, true);
			}
			break;
		}
	}
	
	@Override 
	public void onActionLButtonDown(FFFPoint point) {
		switch (status) {
			case WaitMenuDone:
				Pressed = true;
				break;
		}
	}
	
	@Override 
	public void onActionLButtonUp(FFFPoint point) {
		switch (status) {
		case WaitKeyPressed:
			mParent.hideWaitMark();
			status = Continue;
			break;
		
		case WaitMenuDone:
			if (Pressed) {
				Pressed = false;
				onActionMouseMove(point);
				if (MenuSelect >= 0) {
					setValue(MenuAnserAddr, mParent.getMenuAnser(MenuSelect));
					mParent.hideMenuWindow();
					status = Continue;
				}
				MenuSelect = -1;
			}
			break;
		}
	}
	
	@Override 
	public void onActionRButtonDown(FFFPoint point) {
		switch (status) {
		case WaitKeyPressed:
			mParent.flipMessageWindow();
			break;
		}
	}
	
	@Override 
	public void onActionMouseMove(FFFPoint point) {
		switch (status) {
		case WaitMenuDone: {
				int sel = mParent.getMenuSelect(point);
				if (sel != MenuSelect) {
					mParent.selectMenu(MenuSelect, false);
					MenuSelect = sel;
					mParent.selectMenu(MenuSelect, true);
				}
			}
			break;
		}
	}
	
	@Override 
	public void onActionKeyDown(int key) {
		switch (key) {
		case FFFKey.ENTER:
		case FFFKey.SPACE:
			switch (status) {
			case WaitKeyPressed:
				mParent.hideWaitMark();
				status = Continue;
				break;
			
			case WaitMenuDone:
				if (MenuSelect >= 0) {
					setValue(MenuAnserAddr, mParent.getMenuAnser(MenuSelect));
					mParent.hideMenuWindow();
					status = Continue;
					MenuSelect = -1;
				}
				break;
			}
			break;

		case FFFKey.ESCAPE:
			switch (status) {
			case WaitKeyPressed:
				mParent.flipMessageWindow();
				break;
			}
			break;

		case FFFKey.UP:
			if (status == WaitMenuDone) {	
				mParent.selectMenu(MenuSelect, false);
				MenuSelect--;
				if (MenuSelect < 0)
					MenuSelect = mParent.getMenuItemCount() - 1;
				mParent.selectMenu(MenuSelect, true);
			}
			break;
			
		case FFFKey.DOWN:
			if (status == WaitMenuDone) {	
				mParent.selectMenu(MenuSelect, false);
				MenuSelect++;
				if (MenuSelect >= mParent.getMenuItemCount())
					MenuSelect = 0;
				mParent.selectMenu(MenuSelect, true);
			}
			break;
		}
	}
	
	@Override 
	public boolean onActionIdleAction() {
		if (status == Continue) {
			do {
				status = step();				
			} while (status == Continue);
			if (status == BreakGame) {			
				abort();
			} else if (status == WaitNextIdle) {	
				status = Continue;
				return true;
			} else if (status == WaitWipeDone) {	
				return true;
			}
		} else {
			//trace("NOTE:FFFScriptAction::IdleAction status != Continue");
		}
		return false;
	}
	
	@Override 
	public void onActionTimedOut(int timerId) {
		switch (timerId) {
		case FFFConfig.TimerSleep:
			if (status == WaitTimeOut)
				status = Continue;
			break;
		}
	}
	
	@Override 
	public void onActionWipeDone() {
		if (status == WaitWipeDone)
			status = Continue;
	}
	
	@Override 
	public void onActionWaveDone() {
		if (status == WaitWaveDone)
			status = Continue;
	}
	
	public void abort() {
		if (status == WaitMenuDone)
			mParent.hideMenuWindow();
		mParent.hideMessageWindow();
		
		status = BreakGame;
		if (NO_BYTEARRY_CLEAR) {
			script_buffer.position(0);
			script_buffer.clear();
		} else {
			//FIXME:
		}
		mParent.setAction(FFFConfig.ActionScriptDone);
	}
	
	public int loadFile(FFFResource res, String name) {
		FFFLog.trace("FFFScriptAction::loadFile " + name);
		String path = FFFConfig.SCRIPTPATH + name + ".scr";
		script_buffer = null;
		FFFFile file = new FFFFile(res, path);
		if (!file.isOk()) {
			file.close();
			return FileCannotOpen;
		}
		int length = file.getFileSize();
		script_buffer = ByteBuffer.allocate(length); //FIXME:
		script_buffer.order(ByteOrder.LITTLE_ENDIAN);
		if (file.read(script_buffer, length) != length) {
			file.close();
			return FileCannotRead;
		}
		file.close();
		return FileNoError;
	}
	
	public boolean load(String name) {
		if (name.length() > 16) {  //FIXME:
			Params.last_script = name.substring(0, 16);
		} else {
			Params.last_script = name;
		}
		FFFLog.trace("Params.last_script = " + Params.last_script);		
		switch (loadFile(res, name)) {
		case FileCannotOpen:
			mParent.getWindow().messageBox("脚本 [" + name + "] 无法开启。");
			return false;
		
		case NotEnoughMemory:
			mParent.getWindow().messageBox("内存不足, 无法读取脚本[" + name + "]。");
			return false;
		
		case FileCannotRead:
			mParent.getWindow().messageBox("无法读取脚本。[" + name + "]");
			return false;
		}
		FFFLog.trace("FFFScriptAction::load " + " " + "LoadFile(\"" + name + "\") success");
		byte[] bytes = new byte[8];
		script_buffer.position(0); // FIXME:
		script_buffer.get(bytes);
		//trace("FFFScriptAction::Load() magic[8] == " + Arrays.toString(bytes));
		String magic = null;
		try {
			magic = new String(bytes, "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int ncommand = script_buffer.getInt();
		FFFLog.trace("magic == " + magic);
		FFFLog.trace("ncommand == " + ncommand);
		if (!MkScriptType.SCRIPT_MAGIC.equals(magic)) {
			mParent.getWindow().messageBox("没有脚本数据。[" + name + "]");
			return false;
		}
		FFFLog.trace("FFFScriptAction::load " + "magic correct"); 
		current.ncommand = ncommand;
		current.commands = script_buffer.position();
		this.position = 0;
		return true;
	}
	
	public boolean setup(FFFParams param) {
		Params = param;
		this.position = Params.script_pos;
		if (current.ncommand < this.position) {
			mParent.getWindow().messageBox("读取的数据异常。");
			return false;
		}
		if (param.last_bgm != 0)
			mParent.startMusic(param.last_bgm);
		switch (param.show_flag) {
		case FFFParams.SHOWCG_IMAGE:
			//FIXME: .length() > 0
			if (param.last_bg.length() > 0)
				loadGraphic(param.last_bg, MkScriptType.POSITION_BACK);
			if (param.last_overlap.length() > 0) {
				loadGraphic(param.last_overlap, MkScriptType.POSITION_OVERLAP);
			} else if (param.last_center.length() > 0) {
				loadGraphic(param.last_center, MkScriptType.POSITION_CENTER);
			} else {
				if (param.last_left.length() > 0)
					loadGraphic(param.last_left, MkScriptType.POSITION_LEFT);
				if (param.last_right.length() > 0)
					loadGraphic(param.last_right, MkScriptType.POSITION_RIGHT);
			}
			status = wipeIn();
			break;

		case FFFParams.SHOWCG_BLACKNESS:
			cutOut();
			status = Continue;
			break;
		
		case FFFParams.SHOWCG_WHITENESS:
			cutOut(true);
			status = Continue;
			break;
		}
		return true;
	}
	
	public int step() {
		assert script_buffer != null;
		int last_pos = this.position;
		Command cmd = getCommand();
		switch (cmd.type) {
		case MkScriptType.SET_VALUE_CMD:
			setValue(((SetValueCommand)cmd).value_addr, ((SetValueCommand)cmd).set_value);
			break;
		
		case MkScriptType.CALC_VALUE_CMD:
			calcValue(((CalcValueCommand)cmd).value_addr, ((CalcValueCommand)cmd).add_value);
			break;

		case MkScriptType.TEXT_CMD:
			Params.script_pos = last_pos;
			mParent.writeMessage(getString(((TextCommand)cmd).msg_len));
			return WaitKeyPressed;
		
		case MkScriptType.CLEAR_TEXT_CMD:
			mParent.clearMessage();
			return WaitNextIdle;
		
		case MkScriptType.MUSIC_CMD:
			Params.last_bgm = ((MusicCommand)cmd).number;
			mParent.startMusic(((MusicCommand)cmd).number);
			break;
		
		case MkScriptType.STOPM_CMD:
			Params.last_bgm = 0;
			mParent.stopMusic();
			break;
		
		case MkScriptType.SOUND_CMD:
			if (mParent.startWave(getString(((SoundCommand)cmd).path_len)))
				return WaitWaveDone;
			return Continue;
		
		case MkScriptType.SLEEP_CMD:
			//Parent.SetTimer(FFFMainWin.TimerSleep, ((SleepCommand)cmd).time * 1000);
			return WaitTimeOut;
		
		case MkScriptType.GOTO_CMD:
			gotoCommand2(((GotoCommand)cmd).goto_label);
			break;
		
		case MkScriptType.IF_TRUE_CMD:
			if (getValue2(((IfCommand)cmd).value1, ((IfCommand)cmd).flag & 1) == 
				getValue2(((IfCommand)cmd).value2, ((IfCommand)cmd).flag & 2))
				gotoCommand2(((IfCommand)cmd).goto_label);
			break;
		
		case MkScriptType.IF_FALSE_CMD:
			if (getValue2(((IfCommand)cmd).value1, ((IfCommand)cmd).flag & 1) != 
				getValue2(((IfCommand)cmd).value2, ((IfCommand)cmd).flag & 2))
				gotoCommand2(((IfCommand)cmd).goto_label);
			break;
			
		case MkScriptType.IF_BIGGER_CMD:
			if (getValue2(((IfCommand)cmd).value1, ((IfCommand)cmd).flag & 1) > 
				getValue2(((IfCommand)cmd).value2, ((IfCommand)cmd).flag & 2))
				gotoCommand2(((IfCommand)cmd).goto_label);
			break;
		
		case MkScriptType.IF_SMALLER_CMD:
			if (getValue2(((IfCommand)cmd).value1, ((IfCommand)cmd).flag & 1) < 
				getValue2(((IfCommand)cmd).value2, ((IfCommand)cmd).flag & 2))
				gotoCommand2(((IfCommand)cmd).goto_label);
			break;
		
		case MkScriptType.IF_BIGGER_EQU_CMD:
			if (getValue2(((IfCommand)cmd).value1, ((IfCommand)cmd).flag & 1) >= 
				getValue2(((IfCommand)cmd).value2, ((IfCommand)cmd).flag & 2))
				gotoCommand2(((IfCommand)cmd).goto_label);
			break;
		
		case MkScriptType.IF_SMALLER_EQU_CMD:
			if (getValue2(((IfCommand)cmd).value1, ((IfCommand)cmd).flag & 1) <= 
				getValue2(((IfCommand)cmd).value2, ((IfCommand)cmd).flag & 2))
				gotoCommand2(((IfCommand)cmd).goto_label);
			break;
			
		case MkScriptType.MENU_INIT_CMD:
			Params.script_pos = last_pos;
			mParent.clearMenuItemCount();
			break;
		
		case MkScriptType.MENU_ITEM_CMD:
			mParent.setMenuItem(getString(((MenuItemCommand)cmd).label_len), ((MenuItemCommand)cmd).number);
			break;
		
		case MkScriptType.MENU_CMD:
			MenuSelect = -1;
			MenuAnserAddr = ((MenuCommand)cmd).value_addr;
			mParent.openMenu();
			return WaitMenuDone;
		
		case MkScriptType.EXEC_CMD:
			if (!load(getString(((ExecCommand)cmd).path_len)))
				return BreakGame;
			PlayMode = MkScriptType.MODE_SCENARIO;
			break;
		
		case MkScriptType.LOAD_CMD:
			return loadGraphic(getString(((LoadCommand)cmd).path_len), ((LoadCommand)cmd).flag);
		
		case MkScriptType.UPDATE_CMD:
			return updateImage(((UpdateCommand)cmd).flag);
		
		case MkScriptType.CLEAR_CMD:
			return clear(((ClearCommand)cmd).pos);
		
		case MkScriptType.CUTIN_CMD:
			return cutIn();
		
		case MkScriptType.CUTOUT_CMD:
			return cutOut();
		
		case MkScriptType.FADEIN_CMD:
			return fadeIn();
		
		case MkScriptType.FADEOUT_CMD:
			return fadeOut();
		
		case MkScriptType.WIPEIN_CMD:
			return wipeIn(((WipeinCommand)cmd).pattern);
		
		case MkScriptType.WIPEOUT_CMD:
			return wipeOut(((WipeoutCommand)cmd).pattern);
		
		case MkScriptType.WHITEIN_CMD:
			return whiteIn();
		
		case MkScriptType.WHITEOUT_CMD:
			return whiteOut();
		
		case MkScriptType.SHAKE_CMD:
			mParent.shake();
			return WaitWipeDone;
			
		case MkScriptType.FLASH_CMD:
			mParent.flash();
			return WaitWipeDone;
		
		case MkScriptType.MODE_CMD:
			PlayMode = ((ModeCommand)cmd).mode;
			break;
		
		case MkScriptType.SYS_LOAD_CMD:
			mParent.setAction(FFFConfig.ActionGameLoad);
			return WaitNextIdle;
		
		case MkScriptType.SYS_EXIT_CMD:
			System.exit(0);
			return WaitNextIdle;
		
		case MkScriptType.SYS_CLEAR_CMD:
			clearAllValues();
			break;
		
		case MkScriptType.END_CMD:
			return BreakGame;
		
		default:
			assert false;
			break;
		}
		return Continue;
	}
	
	public int loadGraphic(String file, int pos) {
		boolean result = false;
		switch (pos) {
		case MkScriptType.POSITION_BACK:
			FFFLog.trace("FFFScriptAction::loadGraphic POSITION_BACK :" + file);
			Params.clearOverlapCG();
			mParent.clearOverlap();
			// no break
		
		case MkScriptType.POSITION_BACKONLY:
			FFFLog.trace("FFFScriptAction::loadGraphic POSITION_BACKONLY :" + file);
			Params.setBackCG(file);
			result = mParent.loadImageBack(file);
			break;
		
		case MkScriptType.POSITION_CENTER:
			FFFLog.trace("FFFScriptAction::loadGraphic POSITION_CENTER :" + file);
			Params.setCenterCG(file);
			result = mParent.loadImageCenter(file);
			break;
		
		case MkScriptType.POSITION_LEFT:
			FFFLog.trace("FFFScriptAction::loadGraphic POSITION_LEFT :" + file);
			Params.setLeftCG(file);
			result = mParent.loadImageLeft(file);
			break;
		
		case MkScriptType.POSITION_RIGHT:
			FFFLog.trace("FFFScriptAction::loadGraphic POSITION_RIGHT :" + file);
			Params.setRightCG(file);
			result = mParent.loadImageRight(file);
			break;
		
		case MkScriptType.POSITION_OVERLAP:
			FFFLog.trace("FFFScriptAction::loadGraphic POSITION_OVERLAP :" + file);
			Params.setOverlapCG(file);
			result = mParent.loadImageOverlap(file);
			break;
		}
		if (!result) {
			mParent.getWindow().messageBox("文件无法读取。[" + file + "]");
			if (PlayMode == MkScriptType.MODE_SYSTEM) {
				System.exit(-1);
			}
			return BreakGame;
		}
		return Continue;
	}
	
	public int clear(int pos) {
		switch (pos) {
		case MkScriptType.POSITION_BACK:
			Params.clearOverlapCG();
			mParent.clearOverlap();
			// no break
		
		case MkScriptType.POSITION_BACKONLY:
			Params.clearBackCG();
			mParent.clearBack();
			break;
		
		case MkScriptType.POSITION_CENTER:
			Params.clearCenterCG();
			mParent.clearCenter();
			break;
		
		case MkScriptType.POSITION_LEFT:
			Params.clearLeftCG();
			mParent.clearLeft();
			break;
		
		case MkScriptType.POSITION_RIGHT:
			Params.clearRightCG();
			mParent.clearRight();
			break;
		
		case MkScriptType.POSITION_OVERLAP:
			Params.clearOverlapCG();
			mParent.clearOverlap();
			break;
		}
		return Continue;
	}
	
	public int updateImage(int flag) {
		Params.setShowFlag();
		FFFRectangle rect = mParent.getInvalidRect();
		if (rect.isEmpty())
			return Continue;
		switch (flag) {
		case MkScriptType.UPDATE_NOW:
			FFFLog.trace("FFFScriptAction::updateImage() UPDATE_NOW " + rect); 
			mParent.cutIn(rect);
			return WaitNextIdle;
			
		case MkScriptType.UPDATE_OVERLAP:
			//FIXME:
			FFFLog.trace("FFFScriptAction::updateImage() UPDATE_OVERLAP " + rect); 
			mParent.mixFade(rect);
			break;
			//Parent.CutIn(rect);
			//return WaitNextIdle;
			
		case MkScriptType.UPDATE_WIPE:
			FFFLog.trace("FFFScriptAction::updateImage() UPDATE_WIPE " + rect); 
			mParent.wipeIn(rect);
			break;
		}
		return WaitWipeDone;
	}
	
	public int fadeIn() {
		Params.setShowFlag();
		mParent.fadeIn();
		return WaitWipeDone;
	}
	
	public int fadeOut() {
		Params.resetShowFlag();
		mParent.fadeOut();
		return WaitWipeDone;
	}
	
	public int cutIn() {
		Params.setShowFlag();
		mParent.cutIn2();
		return WaitNextIdle;
	}

	public int cutOut() {
		return cutOut(false);
	}
	
	public int cutOut(boolean white) {
		Params.resetShowFlag(white);
		mParent.cutOut(white);
		return WaitNextIdle;
	}

	public int wipeIn() {
		return wipeIn(1);
	}
	
	public int wipeIn(int pattern) {
		Params.setShowFlag();
		mParent.wipeIn2(pattern);
		return WaitWipeDone;
	}

	public int wipeOut(int pattern) {
		Params.resetShowFlag();
		mParent.wipeOut(pattern);
		return WaitWipeDone;
	}
	
	public int whiteIn() {
		Params.setShowFlag();
		mParent.whiteIn();
		return WaitWipeDone;
	}

	public int whiteOut() {
		Params.resetShowFlag(true);
		mParent.whiteOut();
		return WaitWipeDone;
	}
}
