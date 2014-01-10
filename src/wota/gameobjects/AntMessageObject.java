package wota.gameobjects;

import wota.utility.Vector;

/** Physical interpretation of messages send by Ants.
 *  User AIs should only see Message not MessageObject 
 *  
 *  contains position, sender and content of messages. 
 */
public class AntMessageObject extends MessageObject {
	
	/** Ant which sends the message */
	public final Ant sender; 

	/** Message instance which contains the information visible to other ants */
	private final AntMessage message;
	
	public AntMessageObject(Vector position, Ant sender, int content, 
							Snapshot snapshot, Parameters parameters) {
		super(position, content, snapshot, parameters);
		this.sender = sender;
		
		message = new AntMessage(this);
	}
	
	/** returns Message instance which contains the information visible to other ants */
	public AntMessage getMessage() {
		return message;
	}
}
