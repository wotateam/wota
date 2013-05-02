package de.wota;

import de.wota.ai.Ant;
import de.wota.gameobjects.AntObject;

/**
 * Klasse für Nachrichten.
 * Weiß den Sender (AntObject), darf ihn aber nicht preisgeben! Stattdessen wird das aktuelle Ant
 * abgefragt und zurückgegeben. 
 * @author pascal
 */
public class Message {
	private int content;
	private AntObject sender;
	
	public Message(int content, AntObject sender) {
		this.content = content;
		this.sender = sender;
	}
	
	public int getContent() {
		return content;
	}
	
	public Ant getTalkingAnt() {
		return sender.getAnt();
	}
}
