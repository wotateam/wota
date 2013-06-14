package wota.gameobjects;

import wota.gameobjects.Hill;
import wota.utility.Vector;

public class HillObject extends GameObject {
	private Hill hill;
	private GameWorld.Player player;
	private double storedFood;
	public HillObject(Vector position, GameWorld.Player player, Parameters parameters) {
		super(position, parameters);
		this.player = player;
		this.storedFood = parameters.STARTING_FOOD;
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
