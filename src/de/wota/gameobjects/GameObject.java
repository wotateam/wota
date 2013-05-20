package de.wota.gameobjects;

import de.wota.utility.Vector;

/**
 * Base class for all objects which are part of the game.
 * @author Daniel
 *
 */
public class GameObject {

	private Vector position;
	
	public GameObject(Vector position) {
		setPosition(position);
		
	}
	
	public void setPosition(Vector position) {
		this.position = Parameters.normalize(position);
	}

	public Vector getPosition() {
		return position;
	}
	

	public void move(Vector moveVector) {
		setPosition(Vector.add(position,moveVector));
	}

}
