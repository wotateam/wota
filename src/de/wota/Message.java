package de.wota;

import java.awt.Point;

/**
 * Klasse für Nachrichten.
 * @author pascal
 *
 */
public class Message {
	private int content;
	private AntObject sender;
	private Point position;
	
	public Message(int content) {
		this.content = content;
	}
	
	public int getContent() {
		return content;
	}
	
	public AntObject getSender() {
		return sender;
	}
	
	public Point getPosition() {
		return position;
	}
}
