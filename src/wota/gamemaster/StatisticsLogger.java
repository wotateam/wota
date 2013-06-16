/**
 * 
 */
package wota.gamemaster;

import wota.gameobjects.*;
import wota.gameobjects.GameWorld.Player;

import java.util.List;

/**
 *  Simple logger which simply counts events
 */
public class StatisticsLogger implements Logger{
	
	private int createdAnts[];
	private int diedAnts[];
	private int collectedFood[];
	
	public StatisticsLogger(List<Player> players) {
		createdAnts 	= new int[players.size()];
		diedAnts    	= new int[players.size()];
		collectedFood 	= new int[players.size()];
		
		for (Player player : players) {
			createdAnts[player.getId()] 	= 0;
			diedAnts[player.getId()]    	= 0;
			collectedFood[player.getId()]	= 0;
		}
	}
	
	@Override
	public void antCreated(AntObject antObject) {
		createdAnts[antObject.player.getId()]++;
	}

	@Override
	public void antDied(AntObject antObject) {
		diedAnts[antObject.player.getId()]++;
	}
	
	@Override
	public void antCollectedFood(Player player, int amount) {
		collectedFood[player.getId()] += amount;
	}
	
	public int[] createdAnts() {
		return createdAnts;
	}
	
	public int[] diedAnts() {
		return diedAnts;
	}
	
	public int[] collectedFood() {
		return collectedFood;
	}
}
