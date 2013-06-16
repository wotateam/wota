package wota.gamemaster;

import wota.gameobjects.*;
import wota.gameobjects.GameWorld.Player;

/**
 * Interface for all loggers.
 * Thinkable loggers print debugging messages to the console
 * or simply count events for the statistic
 * 
 * log gets called by e.g. GameWorld
 */

public interface Logger {
	
	public void AntCreated(AntObject antObject);
	public void AntDied(AntObject antObject);
}
