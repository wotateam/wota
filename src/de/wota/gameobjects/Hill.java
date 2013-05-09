package de.wota.gameobjects;

import de.wota.utility.Vector;

public class Hill extends Snapshot{
	/** the amount of available food */
	private double food;
	private HillObject hillObject;
	
	public Hill(HillObject hillObject) {
		this.hillObject = hillObject;
		food = 0;
	}
	
	public double getFood() {
		return food;
	}

	@Override
	Vector getCoordinates() {
		return hillObject.getPosition();
	}
	
}
