package wota.gameobjects;

import wota.utility.Vector;
/**
 * This is the place where the food is collected and new ants are born. 
 *
 */
public class Hill implements Snapshot{
	/** the amount of available food */
	public double food;
	public HillObject hillObject;
	public final int playerID;
	
	Hill(HillObject hillObject) {
		this.hillObject = hillObject;
		this.playerID = hillObject.getPlayer().id();
		this.food = hillObject.getStoredFood();
	}

	public Vector getPosition() {
		return hillObject.getPosition();
	}
	
	/* (non-Javadoc)
	 * @see wota.gameobjects.Snapshot#hasSameOriginal(wota.gameobjects.Snapshot)
	 */
	@Override
	public boolean hasSameOriginal(Snapshot other) {
		if (other instanceof Hill) {
			return ((Hill) other).hillObject.equals(this.hillObject);
		}
		else {
			return false;
		}
	}
	
}
