package wota.gameobjects;

import wota.utility.Vector;

/** Snapshot of an something in the game world.
 *  Used choose instances of Ant, Hill, Sugar
 *  as a target when using methods like moveTo(Snapshot target).
 */
public interface Snapshot {
	public Vector getPosition();
	
	/** Every tick, a new Snapshot instance is created for each game object. 
	 * Use this method for comparison if you saved a Snapshot instance. */
	public boolean hasSameOriginal(Snapshot other);
}
