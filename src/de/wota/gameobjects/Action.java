package de.wota.gameobjects;

import de.wota.utility.Vector;

/**
 * Describes the planned action determined in tick().
 * May contain a message, information about movement/attack/
 * items to grab.
 *
 */
public class Action {
	public static final int NO_MESSAGE = -1;
		
	/** Ant which should be attacked */
	public Ant attackTarget;
	
	/** sugarSource where sugar should be picked up */
	public Sugar sugarTarget;
	
	/** Vector in which Ant wants to move */
	public Vector movement;
	
	// See comment for setMessageObject.
	private MessageObject messageObject;

	/** empty message object = do nothing */
	public Action() {
		attackTarget = null;
		sugarTarget = null;
		messageObject = null;
		movement = new Vector(0,0); 
	}
	
	/**
	 * In contrast to other setters, this setter is not intended to be called by methods callable
	 * from AntAI.tick. Instead, it is intended to be called afterwards using messageContent.
	 * @param messageObject
	 */
	public void setMessageObject(MessageObject messageObject) {
		this.messageObject = messageObject;
	}
	
	public MessageObject getMessageObject() {
		return messageObject;
	}
}
