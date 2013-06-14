package wota.gameobjects;

import wota.utility.Vector;

/** Snapshot of an Element of the World
 *  used to pass Instances like Ant, Hill, Sugar
 *  as Target to e.g. moveTo(Snapshot target)
 */
public abstract class Snapshot {
	abstract Vector getPosition();

}
