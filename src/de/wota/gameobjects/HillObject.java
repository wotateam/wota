package de.wota.gameobjects;

import de.wota.gameobjects.Hill;
import de.wota.utility.Vector;

public class HillObject extends GameObject {
	private Hill hill;
	private GameWorld.Player player;
	private double food;
	
	public HillObject(Vector position, GameWorld.Player player) {
		super(position);
		this.player = player;
	}
	
	public GameWorld.Player getPlayer() {
		return player;
	}

	public void createHill() {
		this.hill = new Hill(this);
	}
	
	public Hill getHill() {
		return hill;
	}

	public double getFood() {
		return food;
	}
}
