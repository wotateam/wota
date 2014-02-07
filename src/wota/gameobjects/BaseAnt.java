/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/**
 *
 */
public abstract class BaseAnt implements Snapshot {

	
	/** Caste which this ant belongs to */
	public final Caste caste;
	
	/** id of this ant */
	public final int id;
	
	/** id of the player which this ant belongs to */
	public final int playerID;
	
	/** position of the Ant in absolute coordinates */
	public final Vector position;
	
	/** returns the vector of this ant */
	public Vector getPosition() {
		return position;
	}
	
	public BaseAnt(BaseAntObject baseAntObject) {
		caste = baseAntObject.caste;
		id = baseAntObject.id;
		playerID = baseAntObject.player.id();
		position = baseAntObject.getPosition();
	}

}
