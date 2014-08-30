package com.iteye.weimingtom.fastfire.port.file;

import android.os.Debug;
import android.util.Log;

public class FFFLog {
	private final static String TAG = "FFFLog";
	
	public final static void trace(String str) {
		Log.e(TAG, str);
	}
	
	public final static void traceMemory(String str) {
		Log.e(TAG, str + ", getNativeHeapSize " + Debug.getNativeHeapSize());
	}
}
