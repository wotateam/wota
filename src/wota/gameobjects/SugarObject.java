package wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import wota.utility.SeededRandomizer;
import wota.utility.Vector;


/**
 * A sugar source. 
 * 
 * Sugar can be picked up by first come first serve principle.
 *
 */
public class SugarObject extends GameObject {
	
	private int amount; 
	private Sugar sugar;
	private List<AntObject> pickUpCandidates = new LinkedList<AntObject>();
	private int ticksUntilNextPickUp = 0;
	
	public SugarObject(Vector position, Parameters parameters) {
		super(position, parameters);
		amount = parameters.INITIAL_SUGAR_IN_SOURCE;
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
	
	public void tick() {
		if (ticksUntilNextPickUp == 0) {
			if (!pickUpCandidates.isEmpty()) {
				final int winningIndex = SeededRandomizer.getInt(pickUpCandidates.size());
				AntObject receivingAntObject = pickUpCandidates.get(winningIndex);
				receivingAntObject.pickUpSugar(this);
				ticksUntilNextPickUp = parameters.TICKS_BETWEEN_PICK_UPS_AT_SOURCE;
			}
		} else if (ticksUntilNextPickUp > 0) {
			ticksUntilNextPickUp -= 1;
		}
		pickUpCandidates.clear();
	}
	
	public double getRadius() {
		return parameters.INITIAL_SUGAR_RADIUS * Math.sqrt((double) amount / parameters.INITIAL_SUGAR_IN_SOURCE);
	}

	public void addSugarCandidate(AntObject antObject) {
		pickUpCandidates.add(antObject);
	}

	public void decreaseSugar(int amountToPickUp) {
		amount -= amountToPickUp;
	}
}
