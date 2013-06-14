package wota.ai.dummy;

import wota.gameobjects.AntAI;
import wota.utility.SeededRandomizer;

public class MoveAI extends AntAI {
	
	int i=SeededRandomizer.getInt(360);
	@Override
	public void tick() {
		moveInDirection(i);
	}
}
