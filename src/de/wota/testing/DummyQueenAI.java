package de.wota.testing;

import de.wota.ai.QueenAI;
import de.wota.gameobjects.Ant;

public class DummyQueenAI extends QueenAI {
	
	@Override
	public void tick() {
		createAnt(Ant.Caste.GATHERER, MoveAI.class);
		System.out.println("ich bin die Queen");
	}

	
}
