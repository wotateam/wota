package de.wota.ai;

import java.util.Random;

import de.wota.gameobjects.AntAI;

public class MoveAI extends AntAI {
	
	@Override
	public void tick() {
		Random random = new Random();
		if (visibleSugar.size() != 0) {
			moveTowards(visibleSugar.get(0));
		}
		else {
			moveInDirection(random.nextInt(30));
		}
	}
	
}
