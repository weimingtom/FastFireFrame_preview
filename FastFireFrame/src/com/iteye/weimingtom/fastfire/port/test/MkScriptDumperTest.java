package com.iteye.weimingtom.fastfire.port.test;

import java.nio.ByteBuffer;

import com.iteye.weimingtom.fastfire.mkscript.MkScriptDumper;
import com.iteye.weimingtom.fastfire.mkscript.parser.Lexer;
import com.iteye.weimingtom.fastfire.port.file.FFFFile;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.file.FFFResource;

public class MkScriptDumperTest {
	private FFFResource res;
	
	private void test1() {
		FFFFile fileReader = new FFFFile(res, /*"main.txt");*/"sample3.txt");
		while (true) {
			String str = fileReader.getString();
			if (str == null) {
				break;
			}
			FFFLog.trace(str);
			FFFLog.trace("line:" + fileReader.getLineNo());
			//Lexer lexer;
			new Lexer(str);
		}
		fileReader.close();
	}
	
	private void test2() {
		MkScriptDumper makeScript = new MkScriptDumper(res);
		makeScript.readScript("sample3.txt");
		makeScript.dumpBuffer();
		makeScript.close();
	}
	
	private void test3() {
		ByteBuffer buffer = ByteBuffer.allocate(255);
		buffer.clear();
		for (int i = 0; i < 30; i++) {
			buffer.put((byte) 0);
		}
		FFFLog.trace("capacity:" + buffer.capacity());
		FFFLog.trace("arrayOffset:" + buffer.arrayOffset());
		FFFLog.trace("array().length:" + buffer.array().length);
		FFFLog.trace("position:" + buffer.position());
		
		FFFLog.trace("out => " + ("; t".charAt(0) == ';'));
	}
	
	public MkScriptDumperTest() {
		res = new FFFResource();
		res.init(null);
		res.loadClassText("main.txt", res.getClass(), "main.txt");
		res.loadClassText("sample1old.txt", res.getClass(), "sample1old.txt");
		res.loadClassText("sample3.txt", res.getClass(), "sample3.txt");
		
		test1();
		test2();
		test3();
		
		res.unloadAll();
	}
	
	public static void main(String[] args) {
		new MkScriptDumperTest();
	}
}
