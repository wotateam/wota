package de.wota.gamemaster;

public abstract class AbstractLogger {
	public abstract void log(LogEventType event);

	public enum LogEventType
	{
		PLAYER_REGISTERED, TICK
	}
}
