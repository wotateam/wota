package de.wota.gameobjects;

import de.wota.Player;
import de.wota.utility.Vector;

public class HillObject extends GameObject {
	private Hill hill;
	private Player player;
	private double food;
	
	public HillObject(Vector position, Player player) {
		super(position);
		this.hill = new Hill(this);
		this.player = player;
	}
	
	public Player getPlayer() {
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
