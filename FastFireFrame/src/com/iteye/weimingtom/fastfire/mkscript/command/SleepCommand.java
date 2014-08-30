package com.iteye.weimingtom.fastfire.mkscript.command;

public class SleepCommand extends Command {
	public int time;
	
	public SleepCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return "[SleepCommand] { time: " + time +
			" }";
	}
}
