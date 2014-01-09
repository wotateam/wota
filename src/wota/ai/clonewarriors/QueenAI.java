package wota.ai.clonewarriors;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

@AIInformation(creator = "Pascal", name = "Clone Warriors")
public class QueenAI extends wota.gameobjects.QueenAI {
	
	int num_tick = 0;
		
	@Override
	public void tick() {
		num_tick++;
		//createAnt(Caste.Gatherer, MoveAI.class);
		if (SeededRandomizer.getInt(5) <= 3) {
			createAnt(Caste.Gatherer, GathererAI.class);
		}
		else {
			createAnt(Caste.Soldier, SoldierAI.class);
		}
		
		talk(num_tick);
	}
}
