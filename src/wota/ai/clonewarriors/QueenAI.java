package wota.ai.clonewarriors;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

/**
 * Consists of gatherers and warriors. Gatherers randomly look for sugar and 
 * don't communicate at all. Warriors randomly look for enemies and start an
 * joint attack on the enemy hills at a certain tick, when order 66 is called ;-)
 */
@AIInformation(creator = "Elrond1337", name = "Clone Warriors")
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
