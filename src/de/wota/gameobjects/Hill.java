package de.wota.gameobjects;

import de.wota.utility.Vector;
/**
 * This is the place where the food is collected and new ants are born. 
 *
 */
public class Hill extends Snapshot{
	/** the amount of available food */
	public double food;
	public HillObject hillObject;
	
	public Hill(HillObject hillObject) {
		this.hillObject = hillObject;
		food = 0;
	}

	@Override
	Vector getPosition() {
		return hillObject.getPosition();
	}
	
}
