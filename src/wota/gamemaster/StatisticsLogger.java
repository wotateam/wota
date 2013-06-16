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
	
	public StatisticsLogger(List<Player> players) {
		createdAnts = new int[players.size()];
		diedAnts    = new int[players.size()];
		for (Player player : players) {
			createdAnts[player.getId()] = 0;
			diedAnts[player.getId()]    = 0;
		}
	}
	
	//private Vector<int> diedAnts;
	
	@Override
	public void AntCreated(AntObject antObject) {
		createdAnts[antObject.player.getId()]++;
	}

	@Override
	public void AntDied(AntObject antObject) {
		diedAnts[antObject.player.getId()]++;
	}
	
	public int[] createdAnts() {
		return createdAnts;
	}
	
	public int[] diedAnts() {
		return diedAnts;
	}
}
