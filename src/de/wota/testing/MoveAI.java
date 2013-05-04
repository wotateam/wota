package de.wota.testing;

import de.wota.ai.AntAI;

public class MoveAI extends AntAI {

	@Override
	public void die() {
		talk(5);
	}
	
	@Override
	public void tick() {
		moveInDirection(45);
		talk(1);
	}
	
}
