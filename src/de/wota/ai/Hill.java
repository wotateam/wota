package de.wota.ai;

import de.wota.gameobjects.HillObject;

public class Hill {
	/** the amount of available food */
	public final double food;
	
	public Hill(HillObject hillObject) {
		food = hillObject.getFood();
	}
	
	public double getFood() {
		return food;
	}
}
