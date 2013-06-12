package de.wota.ai.dummy;

import de.wota.gamemaster.AIInformation;
import de.wota.gameobjects.Caste;
import de.wota.utility.SeededRandomizer;

@AIInformation(creator = "WotA-Team", name = "DummyQueen")
public class QueenAI extends de.wota.gameobjects.QueenAI {
	
	@Override
	public void tick() {
		createAnt(Caste.Gatherer, MoveAI.class);
	}
}
