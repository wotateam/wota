package de.wota.gameobjects;

import java.awt.geom.Point2D;

/**
 * Ein Zuckerhaufen.
 * @author pascal
 *
 */
public class Sugar extends GameObject {
	
	private double amount;
	
	public Sugar(double amount, Point2D.Double position) {
		super(position);
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
}
