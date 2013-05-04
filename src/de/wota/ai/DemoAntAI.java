package de.wota.ai;

public class DemoAntAI extends AntAI {

	@Override
	public void tick() {
		System.out.println("Test: Rennt immer nach rechts");
		moveInDirection(0);
	}

}
