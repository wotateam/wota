package de.wota.gameobjects;

import java.awt.geom.Point2D;

/**
 * Base class for all objects which are part of the game.
 * @author Daniel
 *
 */
public class GameObject {

	private Point2D.Double position;

	public GameObject(Point2D.Double position) {
		setPosition(position);
	}
	
	public void setPosition(Point2D.Double position) {
		this.position = position;
	}

	public Point2D.Double getPosition() {
		return position;
	}

}
