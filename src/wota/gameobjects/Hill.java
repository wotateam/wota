package wota.gameobjects;

import wota.utility.Vector;
/**
 * This is the place where the food is collected and new ants are born. 
 *
 */
public class Hill extends Snapshot{
	/** the amount of available food */
	public double food;
	public HillObject hillObject;
	public final int playerID;
	
	public Hill(HillObject hillObject) {
		this.hillObject = hillObject;
		this.playerID = hillObject.getPlayer().getId();
		food = 0;
	}

	@Override
	Vector getPosition() {
		return hillObject.getPosition();
	}
	
}
