package de.wota.ai.solitary;

import de.wota.gamemaster.AIInformation;
import de.wota.gameobjects.Caste;
import de.wota.gameobjects.QueenAI;
import de.wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "Dummy")
public class SolitaryQueenAI extends QueenAI {
	
	@Override
	public void tick() {
		//System.out.println("Player: " + self.playerID + " has Queen has " + self.health);
		//createAnt(Caste.Gatherer, MoveAI.class);
		if (SeededRandomizer.nextInt(2) == 0) {
			createAnt(Caste.Gatherer, GathererAI.class);
		}
		else {
			createAnt(Caste.Soldier, SoldierAI.class);
		}
	}
}
