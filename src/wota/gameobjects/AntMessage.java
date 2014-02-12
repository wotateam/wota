package wota.gameobjects;

import wota.utility.Vector;

/** 
 *  Class for Messages send by Ants.
 *  contains position, sender and content of messages. 
 *  
 *  See Message for further details.
 */
public class AntMessage extends Message {
	
	/** Ant which has send the message */
	public final Ant sender; 

	public AntMessage(Vector position, Ant sender, int content, 
							Snapshot snapshot, Parameters parameters) {
		super(position, content, snapshot, parameters);
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return new String(sender + ": " + super.toString());
	}
}
