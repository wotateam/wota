package de.wota.gamemaster;

/**
 * Class for all loggers.
 * Thinkable loggers print debugging messages to the console
 * or simply count events for the statistic
 * 
 * log gets called by e.g. GameWorld
 */
public abstract class AbstractLogger {
	public abstract void log(LogEventType event);

	public enum LogEventType
	{
		PLAYER_REGISTERED, TICK
	}
}
