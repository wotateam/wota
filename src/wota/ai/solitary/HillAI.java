package wota.ai.solitary;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

@AIInformation(creator = "Wota Team", name = "SolitaryAI")
public class HillAI extends wota.gameobjects.HillAI {
	
	@Override
	public void tick() {
		int antsToProduce = (int) (self.food / parameters.ANT_COST);
		for (int i=0; i<antsToProduce; i++) {
			if (random.nextInt(5) <= 3) {
				createAnt(Caste.Gatherer, GathererAI.class);
			}
			else {
				createAnt(Caste.Soldier, SoldierAI.class);
			}
		}
	}
}
