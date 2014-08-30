package com.iteye.weimingtom.fastfire.port.window;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TableLayout.LayoutParams;

public class FastFireFrameAndActivity extends Activity {
	private static final boolean D = true;
	private static final String TAG = "FastFireFrameAndActivity";
	
	private FastFireFrameAndGameView gameBoard;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitle();
        this.gameBoard = createGameView();
        FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new LayoutParams(
        	LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.addView(gameBoard);
        setContentView(layout);
		if (savedInstanceState == null) {
			onFirstStartedState();
        } else {
            boolean isFirstSaved = savedInstanceState.getBoolean("isFirstSaved");
            if (isFirstSaved) {
            	onRestartedState();
            } else {
                onPauseState(); //FIXME:
            }
        }
    }
    
    protected FastFireFrameAndGameView createGameView() {
    	return new FastFireFrameAndGameView(this);
    }
    
	@Override
	protected void onStop() {
		super.onStop();
		onStopState();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		onRestartedState();
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        onPauseState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isFirstSaved", true);
    }

    public void hideTitle() {
    	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
	private void onFirstStartedState() {
		if (D) {
			Log.e(TAG, "onFirstStartedState");
		}
		if (this.gameBoard != null) {
			this.gameBoard.onFirstStartedState();
		}
	}
    
	private void onRestartedState() {
		if (D) {
			Log.e(TAG, "onRestartedState");
		}
		if (this.gameBoard != null) {
			this.gameBoard.onRestartedState();
		}
	}
	
	private void onPauseState() {
		if (D) {
			Log.e(TAG, "onPauseState");
		}
		if (this.gameBoard != null) {
			this.gameBoard.onPauseState();
		}
	}
	
	private void onStopState() {
		if (D) {
			Log.e(TAG, "onStopState");
		}
		if (this.gameBoard != null) {
			this.gameBoard.onStopState();
		}
	}

	@Override
	protected void onDestroy() {
		if (D) {
			Log.e(TAG, "FastFireFrameAndActivity::onDestroy");
		}
		super.onDestroy();
		//FIXME:
		if (this.gameBoard != null) {
			this.gameBoard.close();
		}
	}
}
