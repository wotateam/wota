package de.wota;

import java.awt.Point;

/**
 * Klasse für Nachrichten.
 * @author pascal
 *
 */
public class Message {
	private int content;
	private Ant sender;
	private Point position;
	
	public Message(int content) {
		this.content = content;
	}
	
	public int getContent() {
		return content;
	}
	
	public Ant getSender() {
		return sender;
	}
	
	public Point getPosition() {
		return position;
	}
}
