package de.wota;

import java.awt.geom.Point2D;

import de.wota.gameobjects.Ant;

/**
 * Klasse für Nachrichten.
 * Hat beim Erzeugen nur content. Sender muss später von GameWorld eingetragen werden.
 * @author pascal
 */
public class Message {
	private int content;
	private Ant sender;
	private Point2D.Double position;
	
	public Message(int content) {
		this.content = content;
	}
	
	public void setSender(Ant sender) {
		this.sender = sender;
		this.position = sender.getPosition();
	}
	
	public int getContent() {
		return content;
	}
	
	public Ant getSender() {
		return sender;
	}
	
	public Point2D.Double getPosition() {
		return position;
	}
}
