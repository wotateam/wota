package wota.gameobjects;

import wota.utility.Vector;


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
	/** number of ticks an ant will freeze when picking up */
	private int ticksToWait;
	
	public SugarObject(int amount, Vector position, Parameters parameters) {
		super(position, parameters);
		ticksToWait = parameters.TICKS_SUGAR_PICKUP;
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
		ticksToWait += parameters.TICKS_SUGAR_PICKUP;
	}
	
	public void tick() {
		if (ticksToWait > parameters.TICKS_SUGAR_PICKUP)
			ticksToWait--;
	}
	
	public int getTicksToWait() {
		if (ticksToWait < parameters.TICKS_SUGAR_PICKUP)
			System.out.println("unexpected behavior in SugarObject.getTicksToWait()");
		return ticksToWait;
	}
	
	public double getRadius() {
		return parameters.INITIAL_SUGAR_RADIUS * Math.sqrt((double) amount / parameters.INITIAL_SUGAR);
	}
}
