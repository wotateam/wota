package wota.ai.dummy;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

@AIInformation(creator = "Wota Team", name = "Dummy")
public class HillAI extends wota.gameobjects.HillAI {
	
	@Override
	public void tick() {
		createAnt(Caste.Gatherer, MoveAI.class);
	}
}
