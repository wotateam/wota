package de.wota;

import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.MessageObject;

public class Message {
	public final int content;
	public final Ant sender;
	
	public Message(MessageObject messageObject) {
		this.content = messageObject.getContent();
		this.sender = messageObject.getSender();
	}
}
