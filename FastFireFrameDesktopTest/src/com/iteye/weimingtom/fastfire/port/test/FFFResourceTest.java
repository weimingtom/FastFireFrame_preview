package com.iteye.weimingtom.fastfire.port.test;

import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;;

public class FFFResourceTest {
	public static final void main(String[] args) {
		FFFResource res = new FFFResource();
		res.init(null);
		res.loadClassText("main.txt", res.getClass(), "main.txt");
		FFFLog.trace(res.textMap.get("main.txt"));
		res.unloadAll();
	}
}
