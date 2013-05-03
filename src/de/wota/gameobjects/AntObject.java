package de.wota.gameobjects;

import java.util.List;

import de.wota.Action;
import de.wota.Message;
import de.wota.Vector;
import de.wota.ai.AntAI;

/**
 * Interne Darstellung von Ants. Enthält alle Informationen.
 * Im Gegensatz dazu enthält Ant nur die Informationen, welche die KI sehen darf.
 * @author pascal
 */
public class AntObject extends GameObject{
	
	private static int idCounter = 0;
	private Ant ant;
	private final AntAI ai;
	public final int id;
	private double health;
	private double speed;
	/** Angriffspunkte */
	private double attack;
	private Action action;
	final private Ant.Caste caste;
	
	public AntObject(Vector position, Ant.Caste caste, Class<? extends AntAI> antAIClass) {
		super(position);
		this.id = getNewID();
		AntAI antAI = null;
		try {
			antAI = antAIClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not create AntAI -> exit");
			System.exit(1);
		}
		
		this.ai = antAI;
		this.caste = caste;
		this.ant = new Ant(this);
	}

	public AntAI getAI() {
		return ai;
	}
	
	public Ant getAnt() {
		return ant;
	}
	
	public Ant.Caste getCaste() {
		return caste;
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
		this.ai.self = ant;
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

	public void tick(List<Ant> visibleAnts, List<Sugar> visibleSugar) {
		ai.visibleAnts = visibleAnts;
		ai.visibleSugar = visibleSugar;
		ai.tick();
		action = ai.popAction();
		// modify the action such that the actor is the right one
		Message message = action.getMessage();
		message.setSender(ant);
		action.setMessage(message);
	}
	
	private static int getNewID() {
		idCounter++;
		return idCounter - 1;
	}
}
