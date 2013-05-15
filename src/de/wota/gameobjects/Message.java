package de.wota.gameobjects;


public class Message {
	public final int content;
	public final Ant sender;
	
	public Message(MessageObject messageObject) {
		this.content = messageObject.getContent();
		this.sender = messageObject.getSender();
	}
}
