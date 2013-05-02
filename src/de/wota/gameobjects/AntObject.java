package de.wota.gameobjects;

import java.awt.geom.Point2D;
import java.util.List;

import de.wota.Action;
import de.wota.ai.Ant;
import de.wota.ai.AntAI;

/**
 * Interne Darstellung von Ants. Enthält alle Informationen.
 * Im Gegensatz dazu enthält Ant nur die Informationen, welche die KI sehen darf.
 * @author pascal
 *
 */
public class AntObject extends GameObject{
	
	private Ant ant;
	private AntAI ai;
	public final int ID;
	private double health;
	private double speed;
	/** Angriffspunkte */
	private double attack;
	private Action action;
	
	public AntObject(Point2D.Double position, int ID) {
		super(position);
		this.ID = ID;
	}
	
	public AntAI getAI() {
		return ai;
	}
	
	public Ant getAnt() {
		return ant;
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
	
	public Action getAction() {
		return action;
	}
	
	public void createAnt() {
		ant = new Ant(this);
	}
	
	public void takesDamage(double attack) {
		health = health - attack;
		if (health <= 0) {
			die();
		}
	}
	
	private void die() {
		// TODO write AntObject.die()
	}

	public void tick(List<Ant> visibleAnts) {
		ai.tick(ant, visibleAnts);
		action = ai.popAction();
	}
}
