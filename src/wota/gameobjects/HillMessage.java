/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/** 
 *  Class for Messages send by Hills.
 *  contains position, sender and content of messages. 
 *  
 *  See Message for further details.
 */
public class HillMessage extends Message {

	/** Hill which has send this message */
	public final Hill sender;

	public HillMessage(Vector position, Hill sender, int content, Snapshot snapshot, Parameters parameters) {
		super(position, content, snapshot, parameters);
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return new String(sender + ": " + super.toString());
	}
	
}
