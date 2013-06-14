package wota.ai.dummy;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "DummyQueen")
public class QueenAI extends wota.gameobjects.QueenAI {
	
	@Override
	public void tick() {
		createAnt(Caste.Gatherer, MoveAI.class);
	}
}
