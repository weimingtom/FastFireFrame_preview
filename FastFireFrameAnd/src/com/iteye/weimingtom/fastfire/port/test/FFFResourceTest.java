package com.iteye.weimingtom.fastfire.port.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TableLayout.LayoutParams;

import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;;

public class FFFResourceTest extends Activity {
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new LayoutParams(
        	LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setContentView(layout);
        
		FFFResource res = new FFFResource();
		res.init(null);
		res.loadClassText("main.txt", res.getClass(), "main.txt");
		FFFLog.trace(res.textMap.get("main.txt"));
		res.unloadAll();
	}
}
