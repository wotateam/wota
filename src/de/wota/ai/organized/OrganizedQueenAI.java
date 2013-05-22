package de.wota.ai.organized;

import de.wota.ai.organized.GathererAI;
import de.wota.ai.solitary.SoldierAI;
import de.wota.gameobjects.Caste;
import de.wota.gameobjects.QueenAI;
import de.wota.utility.SeededRandomizer;

public class OrganizedQueenAI extends QueenAI {
	public static final int SUGAR_DIRECTION_START = 1000;
	public static final int ENEMY_HILL_START = 2000;
	@Override
	public void tick() throws Exception {
		double choice = SeededRandomizer.nextDouble();
		if (choice < 1.0) {
			createAnt(Caste.Gatherer, GathererAI.class);
		} else if (choice >= 0.7) {
			createAnt(Caste.Soldier, SoldierAI.class);
		} 
	}
}
