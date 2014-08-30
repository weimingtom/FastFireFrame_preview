package com.iteye.weimingtom.fastfire.mkscript.command;

public class GotoCommand extends Command {
	public int goto_label;

	public GotoCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return "[GotoCommand] { }"; 
	}
}
