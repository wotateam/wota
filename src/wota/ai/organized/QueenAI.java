package wota.ai.organized;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wota.ai.organized.GathererAI;
import wota.ai.solitary.SoldierAI;
import wota.gameobjects.Ant;
import wota.gameobjects.Caste;
import wota.gameobjects.Message;
import wota.utility.SeededRandomizer;


public class QueenAI extends wota.gameobjects.QueenAI {
	public static final int SUGAR_DIRECTION_START = 1000;
	public static final int ENEMY_HILL_START = 2000;
	
	public LinkedList<int[]> sugarDirections = new LinkedList<int[]>();
	private static final int SUGAR_DIRECTION_REPETITIONS = 1;
	private static final int TALK_RADIUS = 5;
	
	@Override
	public void tick() throws Exception {
		double choice = SeededRandomizer.getDouble();
		if (choice < 0.5) {
			createAnt(Caste.Gatherer, GathererAI.class);
		} else if (choice >= 0.5) {
			createAnt(Caste.Soldier, SoldierAI.class);
		} 
		
		for (Message message : audibleMessages) {
			if (message.sender.caste == Caste.Gatherer) {
				int[] newDirectionRepetitionsLeftPair = new int[2];
				newDirectionRepetitionsLeftPair[0] = message.content;
				newDirectionRepetitionsLeftPair[1] = SUGAR_DIRECTION_REPETITIONS;
				sugarDirections.add(newDirectionRepetitionsLeftPair);
			} 
		}
		
		boolean gathererCloseEnough = false;
		for (Ant ant : visibleAnts) {
			if (ant.caste == Caste.Gatherer /*&& vectorTo(ant).length() <= TALK_RADIUS*/) {
				gathererCloseEnough = true;
				break;
			}
		}
		
		if (sugarDirections.size() > 0 && gathererCloseEnough) {
			int i = SeededRandomizer.getInt(sugarDirections.size());
			
			int[] directionRepetitionsLeftPair = sugarDirections.get(i);
			talk(SUGAR_DIRECTION_START + directionRepetitionsLeftPair[0]);
			directionRepetitionsLeftPair[1]--;
			
			if (directionRepetitionsLeftPair[1] <= 0) {
				sugarDirections.remove(i);
			} 
		} 
		// FIXME angles mod 360?
	}
}