package de.wota.gameobjects;

import java.awt.geom.Point2D;

import de.wota.Action;
import de.wota.ai.AntAI;

/**
 * Interne Darstellung von Ants.
 * 
 * @author pascal
 * 
 */
public class Ant extends GameObject {
	
	private AntAI ai;
	private double health;
	private double speed;
	/** Angriffspunkte */
	private double attack;
	private Action action;

	public Ant(Point2D.Double position) {
		super(position);
	}
	
	public AntAI getAI() {
		return ai;
	}

	public double getHealth() {
		return health;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAttack() {
		return attack;
	}

	public void tick() {
		ai.tick();
		// set appropriate sender etc. in action object
		action.setActor(this);
	}

	public Action getAction() {
		return action;
	}

}
