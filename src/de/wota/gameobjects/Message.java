package de.wota.gameobjects;

/** like MessageObject, but only contains the information which can
 *  be visible to other ants. 
 */
public class Message {
	/** information carried by the message */
	public final int content;
	
	/** Ant which sends this message */
	public final Ant sender;
	
	public Message(MessageObject messageObject) {
		this.content = messageObject.getContent();
		this.sender = messageObject.getSender();
	}
}
