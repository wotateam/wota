package de.wota.testing;

import java.util.Random;

import de.wota.ai.AntAI;

public class MoveAI extends AntAI {

	@Override
	public void die() {
		talk(5);
	}
	
	@Override
	public void tick() {
		Random random = new Random();
		moveInDirection(random.nextInt(20));
		talk(1);
	}
	
}
