package de.wota.ai;

import de.wota.gamemaster.AIInformation;
import de.wota.gameobjects.Caste;
import de.wota.gameobjects.QueenAI;

@AIInformation(creator = "WotA-Team", name = "DummyQueen")
public class DummyQueenAI extends QueenAI {
	
	@Override
	public void tick() {
		//createAnt(Caste.Gatherer, MoveAI.class);
		createAnt(Caste.Gatherer, SolitaryAI.class);
	}
}
