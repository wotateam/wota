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
			createdAnts[player.id()] 	= 0;
			diedAnts[player.id()]    	= 0;
			collectedFood[player.id()]	= 0;
		}
	}
	
	public void antCreated(AntObject antObject) {
		createdAnts[antObject.player.id()]++;
	}

	public void antDied(AntObject antObject) {
		diedAnts[antObject.player.id()]++;
	}
	
	public void antCollectedFood(Player player, int amount) {
		collectedFood[player.id()] += amount;
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
