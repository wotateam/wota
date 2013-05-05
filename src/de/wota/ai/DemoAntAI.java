package de.wota.ai;

public class DemoAntAI extends AntAI {
	
	@Override
	public void die() {
		System.out.println("I'm dying!");
	}
	
	@Override
	public void tick() {
		System.out.println("Test: Rennt immer nach rechts");
		moveInDirection(0);
	}

}
