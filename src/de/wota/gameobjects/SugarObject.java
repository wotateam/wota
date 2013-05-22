package de.wota.gameobjects;

import de.wota.utility.Vector;


/**
 * A hill of sugar. 
 * 
 * Sugar can be picked up by first come first serve principle:
 * SugarObject has a field ticksToWait which indicates the number of 
 * ticks an Ant freezes before the sugar is picked up. This number will
 * be increased by Parameters.TICKS_SUGAR_PICKUP when an Ant picks up some
 * sugar and decreased by one every tick.
 *
 */
public class SugarObject extends GameObject {
	
	private int amount;
	private Sugar sugar;
	/** number of ticks one ant will freeze when picking up */
	private int ticksToWait = Parameters.TICKS_SUGAR_PICKUP;
	
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
	
	/**
	 * reduce the amount of stored sugar and increase ticksToWait.
	 */
	public void reduceAmount(int reduction) {
		amount = Math.max(amount - reduction, 0);
		ticksToWait += Parameters.TICKS_SUGAR_PICKUP;
	}
	
	public void tick() {
		ticksToWait--;
	}
	
	public int getTicksToWait() {
		return ticksToWait;
	}
	
	public double getRadius() {
		return Parameters.INITIAL_SUGAR_RADIUS * Math.sqrt((double) amount / Parameters.INITIAL_SUGAR);
	}
}
