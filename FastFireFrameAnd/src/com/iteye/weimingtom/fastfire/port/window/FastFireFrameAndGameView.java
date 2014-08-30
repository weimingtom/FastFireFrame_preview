package com.iteye.weimingtom.fastfire.port.window;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

public class FastFireFrameAndGameView extends SurfaceView {
	private final static boolean D = true;
	private final static String TAG = "FastFireFrameAndGameView";
	
	private static final int PAUSE = 0;
    private static final int READY = 1;
	private int mode = READY;
	private boolean isInit = false;
	private int bgcolor = Color.WHITE;
	private Paint textPaintLoading;
	private final static int TIMER_DELAY = 1000;
	private final static int TIMER_SLEEP_DELAY = 1000 / 24;
	private long lastTimer;
    private TimerHandler mTimerHandler = new TimerHandler();
    protected long repaintCount = 0;
    private OnCreateTask mOnCreateTask;

	public FastFireFrameAndGameView(Context context) {
		super(context);
		init(context);
	}

	public FastFireFrameAndGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public FastFireFrameAndGameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		getHolder().addCallback(new SurfaceCallback());
		setFocusable(true);
		requestFocus();
		//used when loading, so created here
		textPaintLoading = new Paint();
		float scale = this.getResources().getDisplayMetrics().scaledDensity;
		textPaintLoading.setTextSize(18 * scale);
		textPaintLoading.setAntiAlias(true);
		textPaintLoading.setTextAlign(Paint.Align.CENTER);
		textPaintLoading.setColor(Color.BLACK);
	}
	
	private void onSurfaceCreated() {
		if (true) {
			this.isInit = true;
		} else {
			this.postDelayed(new Runnable() {
				@Override
				public void run() {
					isInit = true;
				}
			}, 500);
		}
	}

	private void onSurfaceCreated2() {
		if (false) {
			preload();
		} else {
			if (mOnCreateTask == null) {
				mOnCreateTask = new OnCreateTask(this);
				mOnCreateTask.execute();
			}
		}
	}
	
	private static final class OnCreateTask extends AsyncTask<Void, Void, Void> {
		private WeakReference<FastFireFrameAndGameView> mView;
		
		public OnCreateTask(FastFireFrameAndGameView view) {
			mView = new WeakReference<FastFireFrameAndGameView>(view);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mView.get().preload();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mView.get().mOnCreateTask = null;
		}
	}
	
    protected void preload() {
    	/*
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
    }
	
	private void onSurfaceChanged(int width, int height) {
		//start script ?
//		this.isInit = true;
	}
	
	protected void onSurfaceDraw(Canvas canvas) {
		canvas.save();
		canvas.drawColor(bgcolor);
		if (!this.isInit) {
			onSurfaceDrawLoaded(canvas);
		} else {
			String progressInfo = "Loading..";
			canvas.drawText(progressInfo, this.getWidth() / 2, this.getHeight() / 2, textPaintLoading);
		}
		canvas.restore();
	}
	
	protected void onSurfaceDrawLoaded(Canvas canvas) {
		String clockInfo = "Repaint : " + repaintCount;
		canvas.drawText(clockInfo, this.getWidth() / 2, this.getHeight() / 2, textPaintLoading);
	}
	
	public void onFirstStartedState() {
		if (D) {
			Log.e(TAG, "onFirstStartedState");
		}
		setMode(READY);
		onTimer();
	}
	
	public void onRestartedState() {
		if (D) {
			Log.e(TAG, "onRestartedState");
		}
		setMode(READY);
		onTimer();
		this.isInit = true;
	}
	
	public void onPauseState() {
		if (D) {
			Log.e(TAG, "onPauseState");
		}
		setMode(PAUSE);
	}
	
	public void onStopState() {
		if (D) {
			Log.e(TAG, "onStopState");
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (D) {
			Log.e(TAG, "onDetachedFromWindow");
		}
	}

	private void setMode(int newMode) {
		this.mode = newMode;
	}
	
	protected void repaint() {
		repaintCount++;
		Canvas canvas = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				return;
			}
			synchronized (surfaceHolder) {
				onSurfaceDraw(canvas);
			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private class SurfaceCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (D) {
				Log.e(TAG, "surfaceChanged");
			}
			onSurfaceChanged(width, height);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (D) {
				Log.e(TAG, "surfaceCreated");
			}
			onSurfaceCreated();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
	}
	
	protected void onTimer() {
		if (isInit) {
			repaint();
			this.onSurfaceCreated2();
			isInit = false;
		}
		if (mode == PAUSE) {
			return;
		}
        long now = System.currentTimeMillis();
        boolean isRepaint = false;
        if (now - lastTimer > TIMER_DELAY) {
        	isRepaint = true;
        	lastTimer = now;
        }
        if (isRepaint) {
        	repaint();
        }
        long delta = System.currentTimeMillis() - now;
        if (TIMER_SLEEP_DELAY > delta) {
        	mTimerHandler.sleep(TIMER_SLEEP_DELAY - delta);
        } else {
        	mTimerHandler.sleep(0);
        }
	}
	
    private class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	if (mode != PAUSE) {
        		FastFireFrameAndGameView.this.onTimer();
        	}
        }
        
        public void sleep(long delayMillis) {
        	this.removeMessages(0);
        	sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    
    protected void close() {
    	if (mOnCreateTask != null) {
    		try {
				mOnCreateTask.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}
    }
}
