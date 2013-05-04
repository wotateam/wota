package de.wota.gameobjects;

import de.wota.Vector;


/**
 * Ein Zuckerhaufen. 
 * Sollte für die KI nicht sichtbar sein.
 * @author pascal
 *
 */
public class SugarObject extends GameObject {
	
	private double amount;
	private Sugar sugar;
	
	public SugarObject(double amount, Vector position) {
		super(position);
		this.amount = amount;
		this.sugar = new Sugar(this);
	}
	
	public void createSugar() {
		this.sugar = new Sugar(this);
	}
	
	public Sugar getSugar() {
		return sugar;
	}
	
	public double getAmount() {
		return amount;
	}
}
