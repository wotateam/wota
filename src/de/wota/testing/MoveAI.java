package de.wota.testing;

import de.wota.ai.AntAI;

public class MoveAI extends AntAI {

	@Override
	public void tick() {
		moveTo(90);
	}
	
}