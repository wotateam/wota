package de.wota.testing;

import de.wota.ai.QueenAI;
import de.wota.gameobjects.caste.Caste;

public class DummyQueenAI extends QueenAI {
	
	@Override
	public void tick() {
		createAnt(Caste.Gatherer, MoveAI.class);
	}
}
