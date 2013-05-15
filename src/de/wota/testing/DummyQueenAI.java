package de.wota.testing;

import de.wota.ai.AIInformation;
import de.wota.ai.QueenAI;
import de.wota.gameobjects.Caste;

@AIInformation(creator = "WotA-Team", name = "DummyQueen")
public class DummyQueenAI extends QueenAI {
	
	@Override
	public void tick() {
		//createAnt(Caste.Gatherer, MoveAI.class);
		createAnt(Caste.Gatherer, SolitaryAI.class);
	}
}
