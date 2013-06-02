package de.wota.ai.dummy;

import de.wota.gameobjects.AntAI;
import de.wota.utility.SeededRandomizer;

public class MoveAI extends AntAI {
	
	@Override
	public void tick() {
		if (visibleSugar.size() != 0) {
			moveToward(visibleSugar.get(0));
		}
		else {
			moveInDirection(SeededRandomizer.getInt(30));
		}
	}
	
}
