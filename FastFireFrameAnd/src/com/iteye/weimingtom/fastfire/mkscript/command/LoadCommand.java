package com.iteye.weimingtom.fastfire.mkscript.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class LoadCommand extends Command {
	public byte flag;
	public byte path_len;
	public String message;
	public int nMessageTail;
	public ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	public LoadCommand(byte type) {
		super(type);
	}

	@Override
	public String toString()
	{
		return "[LoadCommand] { flag: " + flag +
			", path_len: " + path_len +
			", message: " + message + 
			" }";
	}
	
	public int AddMessage(String msg, int limit) {
		this.message = msg;
		this.bytes.reset();
		try {
			this.bytes.write(this.message.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int n = this.bytes.size() % 4;
		this.nMessageTail = n >= 0 ? (4 - n) : 0;
		for (int i = 0; i < this.nMessageTail; i++) {
			this.bytes.write(0);
		}
		return this.bytes.size();
	}
}
