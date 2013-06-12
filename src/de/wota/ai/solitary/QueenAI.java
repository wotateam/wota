package de.wota.ai.solitary;

import de.wota.gamemaster.AIInformation;
import de.wota.gameobjects.Caste;
import de.wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "SolitaryAI")
public class QueenAI extends de.wota.gameobjects.QueenAI {
	
	@Override
	public void tick() {
		//System.out.println("Player: " + self.playerID + " has Queen has " + self.health);
		//createAnt(Caste.Gatherer, MoveAI.class);
		if (SeededRandomizer.getInt(5) <= 3) {
			createAnt(Caste.Gatherer, GathererAI.class);
		}
		else {
			createAnt(Caste.Soldier, SoldierAI.class);
		}
	}
}
