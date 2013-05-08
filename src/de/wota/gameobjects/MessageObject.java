package de.wota.gameobjects;

import de.wota.Message;
import de.wota.utility.Vector;

public class MessageObject extends GameObject {

	private Message message;

	public MessageObject(Vector position) {
		super(position);
	}

	public void createMessage() {
		// TODO message = new Message(this);
	}
	
	public Message getMessage() {
		return message;
	}

}
