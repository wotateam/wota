package de.wota.ai;

import java.util.List;

import de.wota.Action;
import de.wota.GameWorldParameters;
import de.wota.Message;
import de.wota.gameobjects.AntObject;


/** 
 * Basisklasse für die KI 
 */
public abstract class AntAI {	
	private Action action = new Action();
	private AntObject antObject; // only to pass information to e.g. message objects
	
	public abstract void tick(Ant self, List<Ant> visibleAnts);
	
	protected void talk(int content) {
		Message message = new Message(content, antObject);
		action.setMessage(message);
	}
	
	protected void attack(Ant target) {
		action.setAttackTarget(target);
	}
	
	protected void moveTo(double direction) {
		action.setMovementDirection(direction);
		action.setMovementDistance(GameWorldParameters.MAX_MOVEMENT_DISTANCE);
	}
	
	protected void moveTo(double direction, double distance) {
		action.setMovementDirection(direction);
		action.setMovementDistance(distance);
	}
	
	/** deletes the action */
	public Action popAction() {
		Action returnAction = action;
		action = new Action();
		return returnAction;
	}
}
