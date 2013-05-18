package de.wota.gameobjects;

import de.wota.utility.Vector;


/**
 * Ein Zuckerhaufen. 
 * Sollte für die KI nicht sichtbar sein.
 * @author pascal
 *
 */
public class SugarObject extends GameObject {
	
	private int amount;
	private Sugar sugar;
	
	public SugarObject(int amount, Vector position) {
		super(position);
		this.amount = amount;
	}
	
	public void createSugar() {
		this.sugar = new Sugar(this);
	}
	
	public Sugar getSugar() {
		return sugar;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void reduceAmount(int reduction) {
		amount = Math.max(amount - reduction, 0);
	}
	
	public double getRadius() {
		return GameWorldParameters.INITIAL_SUGAR_RADIUS * amount / GameWorldParameters.INITIAL_SUGAR;
	}
}
