package de.wota.gameobjects;

import de.wota.Vector;


/**
 * Ein Zuckerhaufen.
 * @author pascal
 *
 */
public class Sugar extends GameObject {
	
	private double amount;
	
	public Sugar(double amount, Vector position) {
		super(position);
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
}
