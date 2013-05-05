package de.wota;

import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;

/**
 * Klasse für Nachrichten.
 * @author pascal
 */
public class Message {
	private int content;
	private Ant sender;
	
	public Message(int content) {
		this.content = content;
	}
	
	public int getContent() {
		return content;
	}
	
	public Ant getTalkingAnt() {
		return sender;
	}
	
	public void setSender(Ant ant) {
		sender = ant;
	}
}
