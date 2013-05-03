package de.wota.ai;

import com.sun.xml.internal.bind.v2.model.core.MaybeElement;

import de.wota.Action;
import de.wota.GameWorldParameters;
import de.wota.Message;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;


/** 
 * Basisklasse für die KI 
 */
public abstract class AntAI extends BaseAI{	
	public Ant self; // user AI may have changed this value!
	
	protected void attack(Ant target) {
		action.setAttackTarget(target);
	}
	
	protected void talk(int content) {
		Message message = new Message(content);
		action.setMessage(message);
	}
	
	protected void moveTo(double direction) {
		action.setMovementDirection(direction);
		action.setMovementDistance(GameWorldParameters.MAX_MOVEMENT_DISTANCE);
	}
	
	protected void moveTo(double direction, double distance) {
		action.setMovementDirection(direction);
		action.setMovementDistance(distance);
	}
	
	public void setAnt(Ant ant) {
		self = ant;
	}
	
	/**
	 * CAUTION! USER AI MAY HAVE CHANGED THIS
	 */
	public Ant getAnt() {
		return self;
	}
}
