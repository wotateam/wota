package de.wota.gameobjects;

import de.wota.gameobjects.Hill;
import de.wota.utility.Vector;

public class HillObject extends GameObject {
	private Hill hill;
	private GameWorld.Player player;
	private double storedFood = Parameters.STARTING_FOOD;
	
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

	public double getStoredFood() {
		return storedFood;
	}

	public void changeStoredFoodBy(int deltaFood) {
		storedFood += deltaFood;
	}
}
