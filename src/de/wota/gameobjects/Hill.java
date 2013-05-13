package de.wota.gameobjects;

import de.wota.utility.Vector;

public class Hill extends Snapshot{
	/** the amount of available food */
	public double food;
	public HillObject hillObject;
	
	public Hill(HillObject hillObject) {
		this.hillObject = hillObject;
		food = 0;
	}

	@Override
	Vector getCoordinates() {
		return hillObject.getPosition();
	}
	
}
