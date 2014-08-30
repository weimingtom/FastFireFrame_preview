package com.iteye.weimingtom.fastfire.port.window;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;

public class FFFWindow extends FastFireFrameAndGameView {
	private final static boolean DEBUGTRACE = true;
	
	private final static String TAG = "FFFWindow";
	
	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = 480;
	private static final String WINDOW_TITLE = "FFFWindow";
	
	private Bitmap dc;
	private Canvas dcCanvas;
	private Paint dcCanvasPaint;
	private Paint debugTextPaint;
	
	private FFFWindowAdapter adapter;
	
	public FFFWindow(Context context) {
		super(context);
		init();
	}

	public FFFWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public FFFWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		dcCanvasPaint = new Paint();
		dcCanvasPaint.setAntiAlias(true);
		dcCanvasPaint.setDither(true);
		dcCanvasPaint.setFilterBitmap(true);
		
		debugTextPaint = new Paint();
		debugTextPaint.setColor(Color.RED);
		float scale = this.getResources().getDisplayMetrics().scaledDensity;
		debugTextPaint.setTextSize(18 * scale);
	}
	
	public void setAdapter(FFFWindowAdapter adapter) {
		this.adapter = adapter;
		if (this.adapter != null) {
			this.adapter.setWindow(this);
		}
	}
	
	@Override
	protected void preload() {
		dc = Bitmap.createBitmap(WINDOW_WIDTH, WINDOW_HEIGHT, Bitmap.Config.ARGB_8888);
		dcCanvas = new Canvas(dc);
		if (adapter != null) {
			adapter.onCreate();
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
//		if (false) {
//			close();
//		}
	}
	
//	public void onDestoryActivity() {
//		close();
//	}
	
	@Override
	protected void close() {
		super.close();
		if (adapter != null) {
			adapter.onDestroy();
		}
		Bitmap tempDC = dc;
		dc = null;
		dcCanvas = null;
		if (tempDC != null && !tempDC.isRecycled()) {
			tempDC.recycle();
			FFFLog.trace("tempDC.recycle()");
		}
	}
	
	@Override
	protected void onTimer() {
		super.onTimer();
		if (adapter != null) {
			adapter.onIdle(0);
		}
	}
	
	@Override 
    public boolean onTouchEvent(MotionEvent e) {
        int x;
        int y;
        int action = e.getAction() & MotionEvent.ACTION_MASK;
    	x = (int)e.getX();
    	y = (int)e.getY();
        switch (action) {
        case MotionEvent.ACTION_UP:
        	if (adapter != null) {
        		adapter.onMouseMove(calcPoint(x, y));
        		adapter.onLButtonUp(calcPoint(x, y));
        	}
        	break;
        	
        case MotionEvent.ACTION_DOWN:
        	if (adapter != null) {
        		adapter.onLButtonDown(calcPoint(x, y));
        		adapter.onMouseMove(calcPoint(x, y));
        	}
        	break;
        	
        case MotionEvent.ACTION_MOVE:
        	if (adapter != null) {
        		adapter.onMouseMove(calcPoint(x, y));
        	}
        	break;
        }
        return true;
    }
	
	public void messageBox(String str) {
		Log.e(TAG, str);
	}
	
	@Override
	protected void onSurfaceDrawLoaded(Canvas canvas) {
		final float x = 0;
		final float y = 0;
		float w = (float)this.getWidth();
		float h = (float)this.getHeight();

		float ow;
		float oh;
		float ox;
		float oy;
		if (w > 0 && h > 0 && dc != null) {
			if ((float)WINDOW_WIDTH / w > (float)WINDOW_HEIGHT / h) {
				oh = (float)WINDOW_HEIGHT / (float)WINDOW_WIDTH * w;
                ow = w;
            } else {
            	ow = (float)WINDOW_WIDTH / (float)WINDOW_HEIGHT * h;
                oh = (int)(h);
            }
			ox = x + w / 2 - ow / 2;
			oy = y + h / 2 - oh / 2;
			canvas.drawBitmap(dc,
					new Rect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT),
					new RectF(ox, oy, ox + ow, oy + oh),  
					dcCanvasPaint);
			if (DEBUGTRACE) {
				
				canvas.drawText("w=" + w + ",h=" + h + ",ox=" + ox + ",oy=" + oy + ",ow=" + ow + ",oh=" + oh, 0, (float)h, debugTextPaint);
			}
		}
	}
	
	public void draw(FFFImage image, FFFRectangle rect) {
		if (dc != null && dcCanvas != null) {
			dcCanvas.drawBitmap(image.mImage, 
				new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height), 
				new Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height),
				dcCanvasPaint);
			this.repaint();
		}
	}
	
	private FFFPoint calcPoint(int mx, int my) {
		FFFPoint result = null;
		final float x = 0;
		final float y = 0;
		float w = (float)this.getWidth();
		float h = (float)this.getHeight();

		float ow;
		float oh;
		float ox;
		float oy;
		if (w > 0 && h > 0) {
			if ((float)WINDOW_WIDTH / w > (float)WINDOW_HEIGHT / h) {
				oh = (float)WINDOW_HEIGHT / (float)WINDOW_WIDTH * w;
                ow = w;
            } else {
            	ow = (float)WINDOW_WIDTH / (float)WINDOW_HEIGHT * h;
                oh = h;
            }
			ox = x + w / 2 - ow / 2;
			oy = y + h / 2 - oh / 2;
			
			float scale = (float)WINDOW_WIDTH / ow;
			int rx = (int)((mx - ox) * scale);
			int ry = (int)((my - oy) * scale);
			result = new FFFPoint(rx, ry);
		}
		return result;
	}
}

