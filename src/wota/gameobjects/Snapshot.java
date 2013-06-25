package wota.gameobjects;

import wota.utility.Vector;

/** Snapshot of an Element of the World
 *  used to pass Instances like Ant, Hill, Sugar
 *  as Target to e.g. moveTo(Snapshot target)
 */
public interface Snapshot {
	public Vector getPosition();
	
	/** e.g. are the AntObjects identical for two Ants */
	public boolean hasSameOriginal(Snapshot other);
}
