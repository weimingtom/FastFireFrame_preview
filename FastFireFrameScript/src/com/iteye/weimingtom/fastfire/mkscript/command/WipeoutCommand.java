package com.iteye.weimingtom.fastfire.mkscript.command;

public class WipeoutCommand extends Command {
	public byte pattern;

	public WipeoutCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return "[WipeoutCommand] { pattern: " + pattern +
			" }";
	}
}
