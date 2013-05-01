package de.wota;

import java.util.LinkedList;

/**
 * Enthält alle Elemente der Spielwelt.
 * @author pascal
 */
public class GameWorld {

	static final double MAX_MOVEMENT_DISTANCE = 5;
	static final double SIZE_X = 1000;
	static final double SIZE_Y = 1000;
	
	private LinkedList<AntObject> ants;
	private LinkedList<Suggar> suggars;
	
	public LinkedList<AntObject> getAnts() {
		return ants;
	}
	
	public void tick() {
		for (AntObject ant : ants) {
			ant.tick();
		}
		for (AntObject ant : ants) {
			Action action = ant.getAction();
			executeAction(ant, action);
		}
	}
	
	/** führt die Aktion für das AntObject aus */
	private void executeAction(AntObject ant, Action action) {
		// TODO executeAction schreiben
	}
}
