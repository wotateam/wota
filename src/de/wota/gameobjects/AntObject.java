package de.wota.gameobjects;

import java.util.List;

import de.wota.Action;
import de.wota.Message;
import de.wota.Player;
import de.wota.ai.AntAI;
import de.wota.utility.Vector;

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
	private double health = GameWorldParameters.ANT_HEALTH_INIT;
	private double speed = GameWorldParameters.ANT_SPEED_INIT;
	/** Angriffspunkte */
	private double attack = GameWorldParameters.ANT_ATTACK_INIT;
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
		
		this.ant = new Ant(this); // das muss ganz am Ende passieren
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
		this.ai.setAnt(ant);
	}
	
	public void takesDamage(double attack) {
		health = health - attack;
	}
	
	/** Checks if AntObject has positive health. If not, die() is called */
	public boolean isDying() {
		if (health <= 0) {
			die();
			return true;
		}
		else
			return false;
	}
	
	private void die() {
		ai.die();
		action = ai.popAction();
		Message message = action.getMessage();
		if (message != null) {
			message.setSender(ant);
		}
		action.setMessage(message);
	}

	public void tick(List<Ant> visibleAnts, List<Sugar> visibleSugar,
			List<Message> incomingMessages) {
		ai.visibleAnts = visibleAnts;
		ai.visibleSugar = visibleSugar;
		ai.incomingMessages = incomingMessages;
		ai.tick();
		action = ai.popAction();
		// modify the action such that the actor is the right one
		Message message = action.getMessage();
		if (message != null) {
			message.setSender(ant);
		}
		// TODO not sure if set message is supposed to do something other than just setting the message in the future.
		// if not, get rid of this. 
		action.setMessage(message);
	}
	
	private static int getNewID() {
		idCounter++;
		return idCounter - 1;
	}

}
