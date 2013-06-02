package de.wota.ai.dummy;

import de.wota.gameobjects.AntAI;
import de.wota.utility.SeededRandomizer;

public class MoveAI extends AntAI {
	
	int i=SeededRandomizer.getInt(360);
	@Override
	public void tick() {
		moveInDirection(i);
	}
}
