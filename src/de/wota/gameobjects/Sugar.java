package de.wota.gameobjects;

/**
 * Large amount of sugar which wants to be collected
 * @author pascal
 *
 */
public class Sugar {
	public final int amount;
	final SugarObject sugarObject;
	
	public Sugar(SugarObject sugarObject) {
		this.sugarObject = sugarObject;
		amount = sugarObject.getAmount();
	}
}
