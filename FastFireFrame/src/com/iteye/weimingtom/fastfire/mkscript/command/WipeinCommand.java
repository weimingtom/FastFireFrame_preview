package com.iteye.weimingtom.fastfire.mkscript.command;

public class WipeinCommand extends Command {
	public byte pattern;

	public WipeinCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return "[WipeinCommand] { pattern: " + pattern +
			" }";
	}
}
