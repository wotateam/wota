package de.wota.gameobjects;

import de.wota.utility.Vector;

public class MessageObject extends GameObject {
	public MessageObject(Vector position, Ant sender, int content) {
		super(position);
		this.sender = sender;
		this.content = content;
		
		message = new Message(this);
	}
	
	private final int content;
	private final Ant sender; 

	private final Message message;
	
	public Message getMessage() {
		return message;
	}

	public int getContent() {
		return content;
	}

	public Ant getSender() {
		return sender;
	}
}
