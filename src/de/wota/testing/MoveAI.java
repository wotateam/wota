package de.wota.testing;

import java.util.Random;

import de.wota.gameobjects.AntAI;

public class MoveAI extends AntAI {

	@Override
	public void die() {
		talk(5);
	}
	
	@Override
	public void tick() {
		Random random = new Random();
		if (visibleSugar.size() != 0) {
			moveTo(visibleSugar.get(0));
		}
		else {
			moveInDirection(random.nextInt(30));
		}
	}
	
}
