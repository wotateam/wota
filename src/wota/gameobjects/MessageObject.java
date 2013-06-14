package wota.gameobjects;

import wota.utility.Vector;

/** Physical interpretation of messages send by Ants.
 *  User AIs should only see Message not MessageObject 
 *  
 *  contains position, sender and content of messages. 
 */
public class MessageObject extends GameObject {
	
	/** what is transfered in this message. Just an int for the moment. */
	private final int content;
	
	/** Ant which sends the message */
	private final Ant sender; 

	/** Message instance which contains the information visible to other ants */
	private final Message message;
	
	public MessageObject(Vector position, Ant sender, int content, Parameters parameters) {
		super(position, parameters);
		this.sender = sender;
		this.content = content;
		
		message = new Message(this);
	}
	
	/** returns Message instance which contains the information visible to other ants */
	public Message getMessage() {
		return message;
	}

	/** returns information carried by the message */
	public int getContent() {
		return content;
	}

	/** returns sender of this message. */
	public Ant getSender() {
		return sender;
	}
}
