package de.wota.statistics;

public abstract class AbstractLogger {
	public abstract void log(LogEventType event);

	public enum LogEventType
	{
		TICK, VICTORY
	}
}
