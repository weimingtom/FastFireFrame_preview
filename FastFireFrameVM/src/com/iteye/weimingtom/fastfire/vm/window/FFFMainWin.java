package com.iteye.weimingtom.fastfire.vm.window;

import java.nio.ByteBuffer;

import com.iteye.weimingtom.fastfire.mkscript.MkScriptDumper;
import com.iteye.weimingtom.fastfire.mkscript.MkScriptType;
import com.iteye.weimingtom.fastfire.model.FFFConfig;
import com.iteye.weimingtom.fastfire.model.FFFDataTitle;
import com.iteye.weimingtom.fastfire.model.FFFMenuItem;
import com.iteye.weimingtom.fastfire.model.FFFParams;
import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.audio.FFFCDAudio;
import com.iteye.weimingtom.fastfire.port.audio.FFFMci;
import com.iteye.weimingtom.fastfire.port.audio.FFFWaveOut;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;
import com.iteye.weimingtom.fastfire.port.image.FFFFont;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;
import com.iteye.weimingtom.fastfire.port.window.FFFWindow;
import com.iteye.weimingtom.fastfire.port.window.FFFWindowAdapter;
import com.iteye.weimingtom.fastfire.vm.action.FFFAction;
import com.iteye.weimingtom.fastfire.vm.action.FFFGameLoadAction;
import com.iteye.weimingtom.fastfire.vm.action.FFFGameSaveAction;
import com.iteye.weimingtom.fastfire.vm.action.FFFScriptAction;
import com.iteye.weimingtom.fastfire.vm.effect.FFFEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFFadeInEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFFadeOutEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFFlashEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFMixFadeEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFShakeEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFWhiteInEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFWhiteOutEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFWipeInEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFWipeIn2Effect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFWipeOutEffect;
import com.iteye.weimingtom.fastfire.vm.effect.FFFWipeOut2Effect;

public class FFFMainWin extends FFFWindowAdapter {
	private static final boolean NO_FIX_UPDATE_BUG = true;

	private static final int MusicCD = 0;
	private static final int MusicOff = 1;

	private static final int MAX_MENU_ITEM = 8;
	private static final int MAX_MENU_TEXT = 60;
	private static final int MAX_SAVE_TEXT = 62;
	
	private static final int None = 0;
	private static final int Left = 1;
	private static final int Right = 2;
	private static final int Both = 3;
	private static final int Center = 4;
	
	private static final int MSG_W = FFFConfig.MessageFont * FFFConfig.MessageWidth / 2 + 20;
	private static final int MSG_H = (FFFConfig.MessageFont + 2) * FFFConfig.MessageLine + 14;
	private static final int MSG_X = (FFFConfig.WindowWidth - MSG_W) / 2;
	private static final int MSG_Y = FFFConfig.WindowHeight - MSG_H - 8;
	private static final int MSG_TEXTOFFSET_X = MSG_X + 10;
	private static final int MSG_TEXTOFFSET_Y = MSG_Y + 7;
	private static final int MSG_TEXTSPACE_Y = 2;
	private static final int WAITMARK_X = FFFConfig.MessageWidth - 2;
	private static final int WAITMARK_Y = FFFConfig.MessageLine - 1;
	
	private static final int MENU_X = MSG_X; //20;
	private static final int MENU_Y = MSG_Y - 2;
	private static final int MENU_WIDTH = (MAX_MENU_TEXT + 2) * FFFConfig.MessageFont / 2;
	private static final int MENU_HEIGHT = (MAX_MENU_ITEM + 1) * FFFConfig.MessageFont;
	private static final int MENU_MIN_WIDTH = FFFConfig.MessageFont * FFFConfig.MenuWidth;
	private static final int MENU_FRAME_WIDTH = 10;
	private static final int MENU_FRAME_HEIGHT = 10;
	private static final int MENU_ITEM_SPACE = 30;
	private static final int MENU_ITEM_HEIGHT = FFFConfig.MessageFont + MENU_ITEM_SPACE;

	private static final int SAVE_ITEM_HEIGHT = 32;
	private static final int SAVE_ITEM_SPACE = 4;
	private static final int SAVE_ITEM_INTERVAL = SAVE_ITEM_HEIGHT + SAVE_ITEM_SPACE;
	private static final int SAVE_W = 400;
	private static final int SAVE_H = SAVE_ITEM_INTERVAL * FFFParams.PARAMS_MAX_SAVE + SAVE_ITEM_HEIGHT;
	private static final int SAVE_X = (FFFConfig.WindowWidth - SAVE_W) / 2;
	private static final int SAVE_Y = (FFFConfig.WindowHeight - SAVE_H) / 2;
	private static final int SAVE_TEXT_OFFSET_X = SAVE_X + 10;
	private static final int SAVE_TEXT_OFFSET_Y = SAVE_Y + 8;
	private static final int SAVE_TITLE_WIDTH = 72;

	private static final int TextMessage = 1 << 0;
	private static final int TextWaitMark = 1 << 1;
	private static final int MenuFrame = 1 << 2;
	private static final int SaveTitle = 1 << 3;
	
	private static final int MenuItemFirst = 4;
	private static final int SaveItemFirst = 12;
	
	private static int menuItem(int n) { 
		return 1 << (MenuItemFirst + n); 
	}
	
	private static int saveItem(int n) { 
		return 1 << (SaveItemFirst + n); 
	}
	
	private static final FFFRectangle[] POSITION = {
		new FFFRectangle(0, 0, 0, 0),
	    new FFFRectangle(0, 0, FFFConfig.WindowWidth / 2, FFFConfig.WindowHeight),
	    new FFFRectangle(FFFConfig.WindowWidth / 2, 0, FFFConfig.WindowWidth / 2, FFFConfig.WindowHeight),
	    new FFFRectangle(0, 0, FFFConfig.WindowWidth, FFFConfig.WindowHeight),
	    new FFFRectangle(FFFConfig.WindowWidth / 4, 0, FFFConfig.WindowWidth / 2, FFFConfig.WindowHeight),
	};
	
	private FFFFont hFont = new FFFFont();
	private int musicMode;
	
	private FFFMci music;
	private FFFCDAudio cdaudio = new FFFCDAudio();
	private int musicNo;
	private FFFWaveOut wave = new FFFWaveOut();
	
	private FFFParams loadParam = new FFFParams();

	private FFFAction mAction;
	private FFFAction nopAction = new FFFAction();
	private FFFScriptAction scriptAction;
	private FFFGameLoadAction gameLoadAction = new FFFGameLoadAction(); 
	private FFFGameSaveAction gameSaveAction = new FFFGameSaveAction();
	
	private FFFResource res;
	private FFFImage viewImage;
	private FFFImage mixedImage;
	private FFFImage backLayer;
	private FFFImage overlapLayer;
	private FFFImage mixImage;
	private FFFImage maskImage;
	private int overlapFlags;
	private boolean textDisplay;
	private boolean waitMarkShowing;
	
	private FFFRectangle invalidRect = new FFFRectangle(0, 0, 0, 0); // 无效区域
	private FFFRectangle textRect = new FFFRectangle(MSG_X, MSG_Y, MSG_W, MSG_H);
	private FFFRectangle waitMarkRect = new FFFRectangle(msgX(WAITMARK_X), msgY(WAITMARK_Y),
			FFFConfig.MessageFont, FFFConfig.MessageFont);
	private FFFRectangle menuRect = new FFFRectangle(0, 0, 0, 0);
	private FFFRectangle overlapBounds = new FFFRectangle(0, 0, 0, 0);
	private boolean backShow;
	private boolean overlapShow;
	private boolean textShow;
	private boolean menuShow;
	private boolean saveShow;
	private FFFRectangle saveRect = new FFFRectangle(SAVE_X, SAVE_Y, SAVE_W, SAVE_H);
	private int bgColor;
	
	private FFFEffect viewEffect;
	private int timePeriod;
	
	private String[] msgBuffer = new String[FFFConfig.MessageLine];
	
	private int curX;
	private int curY;
	
	private FFFMenuItem[] menuBuffer = new FFFMenuItem[FFFMainWin.MAX_MENU_ITEM];
	private int menuCount;
	
	private boolean isSaveMenu;
	private FFFDataTitle[] dataTitle = new FFFDataTitle[FFFParams.PARAMS_MAX_SAVE];
	
	public void repaintView(FFFRectangle rect) {
		this.getWindow().draw(viewImage, rect);
	}
	
	public void copyAndRepaint(FFFRectangle rect) {
		//FIXME:
		//Rectangle allRactangle = new Rectangle(320, 0, 320, 480);
		FFFLog.trace("FFFMainWin::copyAndRepaint rect == " + rect.x + " " + rect.y + " " + rect.width + " " + rect.height);
		viewImage.copy(mixedImage, rect);
		if (NO_FIX_UPDATE_BUG) {
			repaintView(/*allRactangle*/rect);
		}
	}
	
	public int getMenuSelect(FFFPoint point) {
		if (point.x < menuRect.x + MENU_FRAME_WIDTH || 
			point.y < menuRect.y + MENU_FRAME_HEIGHT || 
			point.x >= menuRect.x + menuRect.width - MENU_FRAME_WIDTH || 
			point.y >= menuRect.y + menuRect.height - MENU_FRAME_HEIGHT)
			return -1;
		return (point.y - menuRect.y - MENU_FRAME_WIDTH) / MENU_ITEM_HEIGHT;
	}
	
	public void wipeIn(FFFRectangle rect) {
		wipeIn(rect, -1);
	}
	
	public void wipeIn(FFFRectangle rect, int pattern) {
		FFFRectangle rect2 = rect.cloneRect();
		updateView(false);
		switch (pattern) {
		case 1:
			viewEffect = new FFFWipeInEffect(this, viewImage, mixedImage, rect2);
			break;
		
		default:
			viewEffect = new FFFWipeIn2Effect(this, viewImage, mixedImage, rect2);
			break;
		}
	}
	
	public void wipeIn2() {
		wipeIn2(1);
	}
	
	public void wipeIn2(int pattern) {
		wipeIn(new FFFRectangle(0, 0, FFFConfig.WindowWidth, FFFConfig.WindowHeight), pattern);
	}
	
	public void wipeOut(int pattern) {
		hideMessageWindow();
		switch (pattern) {
		case 1:
			viewEffect = new FFFWipeOutEffect(this, viewImage, mixedImage);
			break;
		
		default:
			viewEffect = new FFFWipeOut2Effect(this, viewImage, mixedImage);
			break;
		}
		hideAllLayer(FFFConfig.BlackPixel);
	}
	
	public void fadeIn() {
		updateView(false);
		viewEffect = new FFFFadeInEffect(this, viewImage, mixedImage);
	}
	
	public void fadeOut() {
		hideMessageWindow();
		viewEffect = new FFFFadeOutEffect(this, viewImage, mixedImage);
		hideAllLayer(FFFConfig.BlackPixel);
	}
	
	public void cutIn(FFFRectangle rect) {
		FFFRectangle rect2 = rect.cloneRect();
		updateView(false);
		copyAndRepaint(rect2);
	}
	
	public void cutIn2() {
		cutIn(new FFFRectangle(0, 0, FFFConfig.WindowWidth, FFFConfig.WindowHeight));
	}
	
	public void cutOut(boolean white) {
		hideMessageWindow();
		hideAllLayer(white? FFFConfig.WhitePixel: FFFConfig.BlackPixel);
		invalidate(POSITION[Both]);
		updateView();
	}
	
	public void whiteIn() {
		updateView(false);
		viewEffect = new FFFWhiteInEffect(this, viewImage, mixedImage);
	}
	
	public void whiteOut() {
		hideMessageWindow();
		viewEffect = new FFFWhiteOutEffect(this, viewImage, mixedImage);
		hideAllLayer(FFFConfig.WhitePixel);
	}
	
	public void mixFade(FFFRectangle rect) {
		FFFRectangle rect2 = rect.cloneRect();
		updateView(false);
		viewEffect = new FFFMixFadeEffect(this, viewImage, mixedImage, rect2, mixImage, maskImage);
	}
	
	public void shake() {
		viewEffect = new FFFShakeEffect(this, viewImage);
	}
	
	public void flash() {
		viewEffect = new FFFFlashEffect(this, viewImage);
	}
	
	public void stopWipe() {
		viewEffect = null;
	}
	
	public boolean isLoadOK() {
		return mAction.isScriptRunning() && scriptAction.isSaveLoadOK();
	}
	
	public boolean isSaveOK() {
		return mAction.isScriptRunning() && scriptAction.isSaveLoadOK();
	}
	
	public FFFMainWin() {
		
	}

	@Override 
	public void onLButtonUp(FFFPoint point) {
		mAction.onActionLButtonUp(point);
	}

	@Override 
	public void onLButtonDown(FFFPoint point) {
		mAction.onActionLButtonDown(point);
	}

	@Override 
	public void onRButtonUp(FFFPoint point) {
		mAction.onActionRButtonUp(point);
	}

	@Override 
	public void onMouseMove(FFFPoint point) {
		mAction.onActionMouseMove(point);
	}
	
	@Override
	public void onKeyDown(int key) {
		mAction.onActionKeyDown(key);
	}

	@Override 
	public boolean onIdle(int count) {
		if (viewEffect != null) {
			if (viewEffect.step2(System.currentTimeMillis())) {
				return true;
			}
			stopWipe();
			mAction.onActionWipeDone();
		}
		if (mAction != null) {
			return mAction.onActionIdleAction();
		} else {
			return false;
		}
	}
	
	public void sleep(int i) {
		//FIXME:
	}
	
	@Override 
	public boolean onCreate() {
		FFFLog.traceMemory("FFFMainWin::onCreate");
		
		res = new FFFResource();
		viewImage = new FFFImage(this.res, 0, 0);
		mixedImage = new FFFImage(this.res, 0, 0);
		backLayer = new FFFImage(this.res, 0, 0);
		overlapLayer = new FFFImage(this.res, 0, 0);
		mixImage = new FFFImage(this.res, 0, 0);
		maskImage = new FFFImage(this.res, 0, 0);
		
		this.mAction = nopAction;
		this.scriptAction = new FFFScriptAction(res);
		for (int i = 0; i < menuBuffer.length; i++) {
			menuBuffer[i] = new FFFMenuItem();
		}
		
		curX = curY = 0;
		overlapFlags = 0;
		bgColor = FFFConfig.BlackPixel;
		textDisplay = false;
		waitMarkShowing = false;
		
		overlapBounds.setRect(0, 0, 0, 0);
		backShow = false;
		overlapShow = false;
		textShow = false;
		menuShow = false;
		saveShow = false;
		
		hFont = null;

		musicMode = MusicCD;
		music = cdaudio;
		musicNo = 0;
		
		viewEffect = null;
		
		res.init(getWindow());
		//FIXME:
		if (false) {
			res.loadData("data/main.scr", "data/sample3.dat");
		} else {
			res.loadText("data/sample3.txt", "data/sample3.txt");
			MkScriptDumper makeScript = new MkScriptDumper(res);
			makeScript.readScript("data/sample3.txt");
			ByteBuffer bytes = makeScript.duplicateBuffer();
			makeScript.close();
			res.loadBytes("data/main.scr", bytes);
		}
		res.loadImage("cgdata/bg001", "cgdata/bg001.JPG");
		res.loadImage("cgdata/bg002", "cgdata/bg002.JPG");
		res.loadImage("cgdata/bg003", "cgdata/bg003.JPG");
		res.loadImage("cgdata/sino211", "cgdata/SINO211.png");
		res.loadImage("cgdata/sino412", "cgdata/SINO412.png");
		res.loadImage("cgdata/megu111", "cgdata/MEGU111.png");
		res.loadImage("cgdata/megu221", "cgdata/MEGU221.png");
		res.loadImage("cgdata/megu222", "cgdata/MEGU222.png");
		res.loadImage("cgdata/megu223", "cgdata/MEGU223.png");
		res.loadImage("rule/mix", "rule/mix.png");
		res.loadImage("rule/wipe", "rule/wipe.png");
		
		FFFLog.traceMemory("FFFMainWin::onCreate viewImage " + (viewImage != null));
		FFFLog.traceMemory("FFFMainWin::onCreate mixedImage " + (mixedImage != null));
		FFFLog.traceMemory("FFFMainWin::onCreate backLayer " + (backLayer != null));
		FFFLog.traceMemory("FFFMainWin::onCreate overlapLayer " + (overlapLayer != null));
		FFFLog.traceMemory("FFFMainWin::onCreate maskImage " + (maskImage != null));
		
		if (!viewImage.create(FFFConfig.WindowWidth, FFFConfig.WindowHeight) || 
			!mixedImage.create(FFFConfig.WindowWidth, FFFConfig.WindowHeight) || 
			!backLayer.create(FFFConfig.WindowWidth, FFFConfig.WindowHeight) || 
			!overlapLayer.create(FFFConfig.WindowWidth, FFFConfig.WindowHeight) ||
			!maskImage.create(FFFConfig.WindowWidth, FFFConfig.WindowHeight)) {
			this.getWindow().messageBox("内存无法配置。\n" +
				"请先关闭其他应用程序，在重新执行这个程序。");
			return false;
		}
		viewImage.clear();
		mixImage.loadRule("mix", 0, 0);
		if ((hFont = new FFFFont()) == null) {
			this.getWindow().messageBox("找不到宋体。");
			return false;			
		}
		setAction(FFFConfig.ActionNop);
		onFirstAction();
		return true;
	}
	
	public void onFirstAction() {
		if (music != null) {
			if (!music.open() && musicMode == MusicCD) {
				musicMode = MusicOff;
				music = null;
			}
		}
		wave.open();
		startMainMenu();
	}
	
	public void destroyWindow() {
		//FIXME:
	}
	
	public void onClose() {
		
	}
	
	public FFFRectangle getRcPaint() {
		return new FFFRectangle(0, 0, FFFConfig.WindowWidth, FFFConfig.WindowHeight);
	}
	
	@Override 
	public void onPaint() {
		this.getWindow().draw(viewImage, getRcPaint());
	}
	
	@Override 
	public void onDestroy() {
		if (viewImage != null) {
			viewImage.recycle();
			viewImage = null;
			FFFLog.traceMemory("onDestroy viewImage");
		}
		if (mixedImage != null) {
			mixedImage.recycle();
			mixedImage = null;
			FFFLog.traceMemory("onDestroy mixedImage");
		}
		if (backLayer != null) {
			backLayer.recycle();
			backLayer = null;
			FFFLog.traceMemory("onDestroy backLayer");
		}
		if (overlapLayer != null) {
			overlapLayer.recycle();
			overlapLayer = null;
			FFFLog.traceMemory("onDestroy overlapLayer");
		}
		if (mixImage != null) {
			mixImage.recycle();
			mixImage = null;
			FFFLog.traceMemory("onDestroy mixImage");
		}
		if (maskImage != null) {
			maskImage.recycle();
			maskImage = null;
			FFFLog.traceMemory("onDestroy maskImage");
		}
		if (music != null) {
			music.stop();
			music.close();
			music = null;
		}
		res.unloadAll();
		super.onDestroy();
		FFFLog.traceMemory("end super.onDestroy");
		System.gc();
	}
	
	public void onTimer(int id) {
//		KillTimer(id);
		mAction.onActionTimedOut(id);
	}
	
	public void onCommand(int notifyCode, int id, FFFWindow ctrl) {
		
	}
	
	public void onInitSubMenu(Object hMenu, int id) {
		
	}
	
	public void onMciNotify(int flag, int id) {
		
	}
	
	public void changeMusicMode(int mode) {
		if (musicMode != mode) {		
			musicMode = mode;
			if (music != null) {
				music.stop();
				music.close();
				music = null;
			}
			switch (musicMode) {
			case MusicCD:
				music = cdaudio;
				if (!music.open()) {
					musicMode = MusicOff;
					music = null;
				}
				break;
			}
			if (music != null && musicNo > 0) {
				music.play(musicNo);
			}
		}
	}
	
	public boolean setAction(int action) {
		FFFLog.trace("FFFMainWin::setAction() " + action);
		return setAction(action, 0);
	}
	
	public boolean setAction(int action, int param) {
		stopWipe();
		switch (action) {
		case FFFConfig.ActionScriptDone:
		case FFFConfig.ActionScript:
			stopMusic();
			break;
		}
		switch (action) {
		case FFFConfig.ActionNop:
			mAction = nopAction;
			nopAction.initialize(this);
			break;

		case FFFConfig.ActionScriptDone:
			startMainMenu();
			break;
		
		case FFFConfig.ActionScriptSetup:
			scriptAction.setup(loadParam);
			// no break
		
		case FFFConfig.ActionScript:
			mAction = scriptAction;
			break;
		
		case FFFConfig.ActionGameLoad:
			showLoadSaveMenu(false);
			gameLoadAction.initialize(this);
			mAction.onActionPause();
			mAction = gameLoadAction;
			break;
		
		case FFFConfig.ActionGameSave:
			showLoadSaveMenu(true);
			gameSaveAction.initialize(this);
			mAction.onActionPause();
			mAction = gameSaveAction;
			break;
		}
		return true;
	}
	
	public boolean startScript(String name, int mode) {
		scriptAction.initialize(this, mode);
		if (!scriptAction.load(name))
			return false;
		setAction(FFFConfig.ActionScript);
		return true;
	}
	
	public void startMainMenu() {
		if (!startScript("main", MkScriptType.MODE_SYSTEM))
			destroyWindow();
	}
	
	public void writeMessage(String msg) {
		formatMessage(msg);
		waitMarkShowing = true;
		showMessageWindow();
	}
	
	public void hideWaitMark() {
		if (waitMarkShowing) {
			waitMarkShowing = false;
			if (textShow) {
				mixing(waitMarkRect, TextWaitMark);
				copyAndRepaint(waitMarkRect);
			}
		}
	}
	
	public void openMenu() {
		//FIXME:
		int maxlen = MENU_MIN_WIDTH;
		menuRect.y = MENU_Y - ((MENU_FRAME_HEIGHT * 2) + menuCount * MENU_ITEM_HEIGHT
			- MENU_ITEM_SPACE);
		menuRect.x = MENU_X;
		menuRect.height = MENU_Y - menuRect.y;
		menuRect.width = (MENU_FRAME_WIDTH * 2) + maxlen;
//		menuRect.width = MENU_X + (MENU_FRAME_WIDTH * 2) + maxlen - menuRect.x;
		menuShow = true;
		FFFLog.trace("FFFMainWin::openMenu == " + menuRect.x + " " + menuRect.y + " " + menuRect.width + " " + menuRect.height);
		mixing(menuRect);
		if (NO_FIX_UPDATE_BUG) {
			copyAndRepaint(menuRect);
		}
	}
	
	public void selectMenu(int index, boolean select) {
		if (index >= 0) {
			menuBuffer[index].color = select? FFFConfig.RedPixel: FFFConfig.WhitePixel;
			FFFRectangle r = new FFFRectangle(
				menuRect.x + MENU_FRAME_WIDTH,
				menuRect.y + MENU_FRAME_HEIGHT + MENU_ITEM_HEIGHT * index,
				menuRect.width - MENU_FRAME_WIDTH * 2,
				/*FFFConfig.MessageFont*/MENU_ITEM_HEIGHT
			);
			mixing(r, menuItem(index));
			copyAndRepaint(r);
		}
	}
	
	public void showMessageWindow() {
		textDisplay = true;
		textShow = true;
		invalidate(textRect);
		updateView();
	}
	
	public void hideMessageWindow() {
		hideMessageWindow(true);
	}
	
	public void hideMessageWindow(boolean update) {
		textDisplay = false;
		if (textShow) {
			textShow = false;
			invalidate(textRect);
			if (update)
				updateView();
		}
	}
	
	public void flipMessageWindow() {
		if (textDisplay) {
			textShow = textShow? false: true;
			invalidate(textRect);
			updateView();
		}
	}
	
	public void showOverlapLayer(int pos) {
		if (overlapShow) {
			if ((overlapFlags == Center && pos != Center) ||
			    (overlapFlags != Center && pos == Center)) {	
				FFFLog.trace("FFFMainWin::showOverlapLayer 显示在中间，删除所有之前显示的图形");
				invalidate(POSITION[overlapFlags]);
				overlapFlags = None;
				overlapBounds.setRect(0, 0, 0, 0);
			}
		}
		overlapFlags |= pos;
		overlapBounds = POSITION[overlapFlags];
		overlapShow = true;
		FFFLog.trace("FFFMainWin::showOverlapLayer Invalidate == " + POSITION[pos]);
		invalidate(POSITION[pos]);
	}
	
	public void hideOverlapLayer(int pos) {
		if (overlapShow) {	
			if ((overlapFlags == Center && pos != Center) ||
			    (overlapFlags != Center && pos == Center)) {	
				invalidate(POSITION[overlapFlags]);
				overlapFlags = None;
				overlapBounds.setRect(0, 0, 0, 0);
			}
		}
		overlapFlags &= ~pos;
		overlapBounds = POSITION[overlapFlags];
		if (overlapFlags == None)
			overlapShow = false;
		invalidate(POSITION[pos]);
	}
	
	public void hideMenuWindow() {
		hideMenuWindow(true);
	}
	
	public void hideMenuWindow(boolean update) {
		if (menuShow) {
			menuShow = false;
			invalidate(menuRect);
			if (update)
				updateView();
		}
	}
	
	public int getMenuItemCount() { 
		return menuCount; 
	}
	
	public int getMenuAnser(int index) { 
		return menuBuffer[index].anser; 
	}
	
	public void hideAllLayer(int pix) {
		bgColor = pix;
		backShow = false;
		overlapShow = false;
		overlapFlags = None;
		overlapBounds.setRect(0, 0, 0, 0);
	}
	
	
	public void mixing(FFFRectangle rect) {
		mixing(rect, 0xFFFFFFFF);
	}
	
	public void mixing(FFFRectangle rect, int flags) {
		if (backShow) {
			mixedImage.copy(backLayer, rect);
		} else {
			mixedImage.fillRect(rect, bgColor);
		}
		if (overlapShow) {
			FFFLog.trace("FFFMainWin::mixing, OverlapShow" + overlapBounds.intersection(rect));
			mixedImage.mixImage(overlapLayer, overlapBounds.intersection(rect), 0x00FF00);
		}
		if (saveShow) {
			if ((flags & SaveTitle) != 0) {
				mixedImage.drawFrameRect(new FFFRectangle(SAVE_X, SAVE_Y, SAVE_TITLE_WIDTH, SAVE_ITEM_HEIGHT), 0xFFFFFF);
				mixedImage.drawText(hFont, SAVE_TEXT_OFFSET_X, SAVE_TEXT_OFFSET_Y,
					isSaveMenu? "存档": "装入", FFFConfig.WhitePixel);
			}
			for (int i = 0; i < FFFParams.PARAMS_MAX_SAVE; i++) {
				if ((flags & saveItem(i)) != 0) {
					int	y = (i + 1) * SAVE_ITEM_INTERVAL;
					mixedImage.drawFrameRect(new FFFRectangle(SAVE_X, SAVE_Y + y, SAVE_W, SAVE_ITEM_HEIGHT),
						dataTitle[i].color);
					mixedImage.drawText(hFont, SAVE_TEXT_OFFSET_X, SAVE_TEXT_OFFSET_Y + y,
						dataTitle[i].title, dataTitle[i].color);
				}
			}
		} else {
			if (textShow) {
				if ((flags & TextMessage) != 0) {
					//FIXME:
					FFFLog.trace("FFFMainWin::mixing: TextRect == " + textRect.x + " " + textRect.y + " " + textRect.width + " " + textRect.height);
					mixedImage.drawFrameRect(textRect, 0xFFFFFF);
					for (int i = 0; i < FFFConfig.MessageLine; i++) {
						FFFLog.trace("FFFMainWin::mixing DrawText " + msgX(0) + "," + msgY(i) + "," + msgBuffer[i]);
						mixedImage.drawText(hFont, msgX(0), msgY(i), msgBuffer[i], FFFConfig.WhitePixel);
					}
				} else {
					FFFRectangle temp = textRect.intersection(rect);
					if (temp.isEmpty()) {
						temp.setRect(0, 0, 0, 0); //FIXME:Java可能会产生负数的宽高值
					}
					FFFLog.trace("FFFMainWin::mixing: FillHalfToneRect " + temp.x + " " + temp.y + " " + temp.width + " " + temp.height);
					mixedImage.fillHalfToneRect(temp);
				}
				if (waitMarkShowing && (flags & TextWaitMark) != 0)
					mixedImage.drawText(hFont, msgX(WAITMARK_X), msgY(WAITMARK_Y), "▼", FFFConfig.WhitePixel);
			}
			if (menuShow) {
				if ((flags & MenuFrame) != 0) {
					mixedImage.drawFrameRect(menuRect, 0xFFFFFF);
				} else {
					mixedImage.fillHalfToneRect(menuRect.intersection(rect));
				}
				for (int i = 0; i < menuCount; i++) {
					if ((flags & menuItem(i)) != 0) {
						mixedImage.drawText(hFont,
							menuRect.x + MENU_FRAME_WIDTH,
							menuRect.y + MENU_FRAME_HEIGHT + MENU_ITEM_HEIGHT * i,
							menuBuffer[i].text, menuBuffer[i].color);
					}
				}
			}
		}
	}
	
	public boolean updateView() {
		return updateView(true);
	}
	
	public boolean updateView(boolean repaint) {
		if (!invalidRect.isEmpty()) {	
			if (NO_FIX_UPDATE_BUG) {
				mixing(invalidRect);
				if (repaint) {
					copyAndRepaint(invalidRect);
				}
			}
			invalidRect.setRect(0, 0, 0, 0);
			return true;
		}
		return false;
	}
	
	public boolean loadImageBack(String name) {
		backShow = true;
		invalidate(POSITION[Both]);
		return backLayer.loadImage(name, 0, 0);
	}
	
	public boolean loadImageOverlap(String name) {
		return loadImageOverlap(name, FFFMainWin.Both);
	}
	
	public boolean loadImageOverlap(String name, int pos) {
		showOverlapLayer(pos);
		return overlapLayer.loadImage(name, POSITION[pos].x, POSITION[pos].y);
	}
	
	public boolean clearImageBack() {
		backShow = false;
		invalidate(POSITION[Both]);
		return true;
	}
	
	public boolean kinsoku(String p) {
		//FIXME:
		return false;
	}
	
	public static final int STR_LIMIT =	(FFFConfig.MessageWidth - 2);
	public static final int STR_WIDTH =	(STR_LIMIT - 2);
	
	public void clearMessage() {
		hideMessageWindow();
		curX = curY = 0;
		for (int i = 0; i < FFFConfig.MessageLine; i++) {
			msgBuffer[i] = "";
		}
	}
	
	public int formatMessageOld(String msg) {
		//FIXME:
		curX = curY = 0;
		String[] lines = msg.split("\n", FFFConfig.MessageLine);
		for (int i = 0; i < FFFConfig.MessageLine; i++) {
			if (i < lines.length && lines[i] != null) {
				msgBuffer[i] = lines[i];
				FFFLog.trace("formatMessage" + " " + i + " " + msgBuffer[i]);
				curY++;
			} else {
				FFFLog.trace("formatMessage" + " " + i);
				msgBuffer[i] = "";
			}
		}
		FFFLog.trace("formatMessage CurY == " + curY);
		return curY;
	}

	//FIXME:
	public int formatMessage(String msg) {
		curX = curY = 0;
		for (int i = 0; i < FFFConfig.MessageLine; i++) {
			msgBuffer[i] = "";
		}
		for (int i = 0; i < msg.length() && curY < FFFConfig.MessageLine;) {
			if (msg.charAt(i) == '\n') {
				i++;
				curX = 0;
				curY++;
			} else {
				if (curX > STR_LIMIT) {
					curX = 0;
					curY++;
				}
				char ch = msg.charAt(i);
				msgBuffer[curY] += ch;
				i++;
				curX += 2;
			}
		}
		if (curX > 0 && curY < FFFConfig.MessageLine) {
			curY++;
		}
		return curY;
	}
	
	public void setMenuItem(String str, int anser) {
		//FIXME:
		int n = str.length();
		menuBuffer[menuCount].text = str;
		menuBuffer[menuCount].anser = anser;
		menuBuffer[menuCount].length = n;
		menuBuffer[menuCount].color = FFFConfig.WhitePixel;
		menuCount++;
	}
	
	public void clearMenuItemCount() { 
		menuCount = 0; 
	}
	
	public void loadGame(int no) {
		if (!loadParam.load(no)) {
			this.getWindow().messageBox("无法读取。");
			return;
		}
		scriptAction.initialize(this, MkScriptType.MODE_SCENARIO);
		if (scriptAction.load(loadParam.last_script)) {
			hideMessageWindow(false);
			hideMenuWindow(false);
			if (saveShow) {
				saveShow = false;
				invalidate(saveRect);
			}
			updateView();
			setAction(FFFConfig.ActionScriptSetup);
		}
	}
	
	public void saveGame(int no, int flags) {
		if (!scriptAction.Params.save(no)) {
			this.getWindow().messageBox("无法存储。");
			return;
		}
		cancelLoadSaveMenu(flags);
	}
	
	public void showLoadSaveMenu(boolean isSave) {
		isSaveMenu = isSave;
		saveShow = true;
		for (int i = 0; i < FFFParams.PARAMS_MAX_SAVE; i++) {
			FFFParams param = new FFFParams();
			if (param.load(i)) {
				dataTitle[i].activate = true;
				dataTitle[i].title = (i + 1) + ": " + 
					param.save_month + "/" + param.save_date + " " +
					param.save_hour + ":" + param.save_minute;
			} else {
				dataTitle[i].activate = isSaveMenu? true: false;
				dataTitle[i].title = (i + 1) + ": -- no data --";
			}
			dataTitle[i].color = dataTitle[i].activate? FFFConfig.WhitePixel: FFFConfig.GrayPixel;
		}
		invalidate(saveRect);
		if (textShow)
			invalidate(textRect);
		if (menuShow)
			invalidate(menuRect);
		updateView();
	}
	
	public void hideLoadSaveMenu() {
		saveShow = false;
		invalidate(saveRect);
		if (textShow)
			invalidate(textRect);
		if (menuShow)
			invalidate(menuRect);
		updateView();
	}
	
	public void cancelLoadSaveMenu(int flags) {
		hideLoadSaveMenu();
		mAction = scriptAction;
		mAction.onActionResume();
		if ((flags & FFFConfig.IS_TIMEDOUT) != 0) {
			mAction.onActionTimedOut(FFFConfig.TimerSleep);
		}
	}
	
	public void selectLoadSaveMenu(int index, boolean select) {
		if (index >= 0) {
			dataTitle[index].color = select? FFFConfig.RedPixel: FFFConfig.WhitePixel;
			int y = index * SAVE_ITEM_INTERVAL + SAVE_ITEM_INTERVAL;
			FFFRectangle rect = new FFFRectangle(
				SAVE_X, SAVE_Y + y, SAVE_W, SAVE_ITEM_HEIGHT);
			mixing(rect, saveItem(index));
			copyAndRepaint(rect);
		}
	}
	
	public int getLoadSaveSelect(FFFPoint point) {
		if (point.x >= SAVE_X && point.x < SAVE_X + SAVE_W && 
			point.y >= SAVE_Y + SAVE_ITEM_INTERVAL) {
			int index = point.y - SAVE_Y - SAVE_ITEM_INTERVAL;
			if (index % SAVE_ITEM_INTERVAL < SAVE_ITEM_HEIGHT) {
				index /= SAVE_ITEM_INTERVAL;
				if (index < FFFParams.PARAMS_MAX_SAVE && dataTitle[index].activate)
					return index;
			}
		}
		return -1;
	}
	
	public int nextLoadSaveSelect(int index) {
		for (int i = 1; i <= FFFParams.PARAMS_MAX_SAVE; i++) {
			int next = (index + i) % FFFParams.PARAMS_MAX_SAVE;
			if (dataTitle[next].activate)
				return next;
		}
		return -1;
	}
	
	public int prevLoadSaveSelect(int index) {
		for (int i = FFFParams.PARAMS_MAX_SAVE - 1; i > 0; i--) {
			int prev = (index + i) % FFFParams.PARAMS_MAX_SAVE;
			if (dataTitle[prev].activate)
				return prev;
		}
		return -1;
	}
	
	public boolean startMusic(int no) {
		if (musicNo != no) {
			musicNo = no;
			if (music != null) {
				music.stop();
				return music.play(no);
			}
		}
		return true;
	}
	
	public boolean restartMusic() {
		if (music != null)
			return music.replay();
		return true;
	}
	
	public boolean stopMusic() {
		musicNo = 0;
		if (music != null)
			return music.stop();
		return true;
	}
	
	public boolean startWave(String name) {
		String path;
		path = FFFConfig.WAVEPATH + name + ".wav";
		return wave.play2(path);
	}
	
	public void invalidate(FFFRectangle rect) { 
		FFFLog.trace("FFFMainWin::invalidate() InvalidRect == " + invalidRect.x + "," + invalidRect.y + "," + invalidRect.width + "," + invalidRect.height);
		FFFLog.trace("FFFMainWin::invalidate() rect " + rect.x + "," + rect.y + "," + rect.width + "," + rect.height);
		if (invalidRect.isEmpty()) {
			invalidRect = rect.cloneRect();
		} else if (rect.isEmpty()) {
			//InvalidRect = InvalidRect;
		} else {
			invalidRect = invalidRect.union(rect); 
		}
		FFFLog.trace("FFFMainWin::invalidate() InvalidRect(2) == " + invalidRect.x + "," + invalidRect.y + "," + invalidRect.width + "," + invalidRect.height);
	}
	
	public int msgX(int x) { 
		return x * FFFConfig.MessageFont / 2 + FFFMainWin.MSG_TEXTOFFSET_X; 
	}
	
	public int msgY(int y) { 
		return y * (FFFConfig.MessageFont + FFFMainWin.MSG_TEXTSPACE_Y) + FFFMainWin.MSG_TEXTOFFSET_Y; 
	}
	
	public void clearBack() { 
		clearImageBack(); 
	}
	
	public void clearCenter() { 
		hideOverlapLayer(FFFMainWin.Center); 
	}
	
	public void clearLeft() { 
		hideOverlapLayer(FFFMainWin.Left); 
	}
	
	public void clearRight() { 
		hideOverlapLayer(FFFMainWin.Right); 
	}
	
	public void clearOverlap() { 
		hideOverlapLayer(FFFMainWin.Both); 
	}
	
	public boolean loadImageLeft(String name) { 
		return loadImageOverlap(name, FFFMainWin.Left); 
	}
	
	public boolean loadImageRight(String name) { 
		return loadImageOverlap(name, FFFMainWin.Right); 
	}
	
	public boolean loadImageCenter(String name) { 
		FFFLog.trace("FFFMainWin::loadImageCenter");
		return loadImageOverlap(name, FFFMainWin.Center); 
	}
	
	public FFFRectangle getInvalidRect() { 
		return invalidRect; 
	}
}
