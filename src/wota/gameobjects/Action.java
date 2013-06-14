package wota.gameobjects;

import wota.utility.Vector;

/**
 * Describes the planned action determined in tick().
 * May contain
 * - a message
 * - information about movement/attack/items to grab.
 * - If items should be dropped.
 */
public class Action {		
	/** Ant which should be attacked */
	public Ant attackTarget = null;
	
	/** sugarSource where sugar should be picked up */
	public Sugar sugarTarget = null;
	
	/** Vector in which Ant wants to move */
	public Vector movement = new Vector (0,0);
	
	// See comment for setMessageObject.
	public MessageObject messageObject = null;
	
	/** true if sugar/... should be dropped. */
	public boolean dropItem = false;

	/** empty message object = do nothing */
	public Action() {
	}
	
}
