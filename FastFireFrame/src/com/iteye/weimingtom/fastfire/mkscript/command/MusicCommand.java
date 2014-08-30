package com.iteye.weimingtom.fastfire.mkscript.command;

public class MusicCommand extends Command {
	public int number;
	
	public MusicCommand(byte type) {
		super(type);
	}
	
	@Override
	public String toString() {
		return "[MusicCommand] { number: " + number +
			" }";
	}
}
