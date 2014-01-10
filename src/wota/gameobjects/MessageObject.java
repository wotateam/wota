/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/**
 * Base class for MessageObjects. 
 * Does not contain a sender. 
 */
public abstract class MessageObject extends GameObject {

	/** what is transfered in this message. Just an int for the moment. */
	public final int content;
	public final Snapshot snapshot;
	
	public MessageObject(Vector position, int content, Snapshot snapshot, Parameters parameters) {
		super(position, parameters);
		this.content = content;
		this.snapshot = snapshot;
	}
}
