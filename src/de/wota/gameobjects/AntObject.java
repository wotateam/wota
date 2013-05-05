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
	protected final AntAI ai;
	public final int id;
	private double health;
	private double speed;
	private int sugarCarry = 0;

	/** Angriffspunkte */
	private double attack;
	private Action action;
	final private Ant.Caste caste;
	final public Player player;
	
	public AntObject(Vector position, Ant.Caste caste, Class<? extends AntAI> antAIClass, Player player) {
		super(position);
		this.player = player;
		this.id = getNewID();
		AntAI antAI = null;
		try {
			antAI = antAIClass.newInstance(); 
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not create AntAI -> exit");
			System.exit(1);
		}
		
		this.caste = caste;
		
		// set parameters
		switch (caste) {
		case GATHERER:
			health = GameWorldParameters.Gatherer.ANT_HEALTH_INIT;
			speed = GameWorldParameters.Gatherer.ANT_SPEED;
			attack = GameWorldParameters.Gatherer.ANT_ATTACK;
			break;
		
		case SOLDIER:
			health = GameWorldParameters.Soldier.ANT_HEALTH_INIT;
			speed = GameWorldParameters.Soldier.ANT_SPEED;
			attack = GameWorldParameters.Soldier.ANT_ATTACK;
			break;
			
		case QUEEN:
			health = GameWorldParameters.Queen.ANT_HEALTH_INIT;
			speed = GameWorldParameters.Queen.ANT_SPEED;
			attack = GameWorldParameters.Queen.ANT_ATTACK;
			break;
		}
		
		this.ai = antAI;
		
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
	
	public int getSugarCarry() {
		return sugarCarry;
	}
	
	public void createAnt() {
		ant = new Ant(this);
		this.ai.setAnt(ant);
	}
	
	public void takesDamage(double attack) {
		health = health - attack;
	}
	
	public void picksUpSugar(int amount) {
		sugarCarry = Math.min(GameWorldParameters.MAX_SUGAR_CARRY, sugarCarry + amount);
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
