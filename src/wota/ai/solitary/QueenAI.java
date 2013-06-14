package wota.ai.solitary;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "SolitaryAI")
public class QueenAI extends wota.gameobjects.QueenAI {
	
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
