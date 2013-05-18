package de.wota.gameobjects;

import java.util.List;

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
	protected double health;
	private double speed;
	/** amount of sugar carried now */
	private int sugarCarry = 0;

	/** Angriffspunkte */
	private double attack;
	private Action action;
	private final Caste caste;
	public final GameWorld.Player player;
	
	public AntObject(Vector position, Caste caste, Class<? extends AntAI> antAIClass, GameWorld.Player player) {
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
		health = caste.INITIAL_HEALTH;
		speed = caste.SPEED;
		
		this.ai = antAI;
		this.ai.setAntObject(this);
	}

	public AntAI getAI() {
		return ai;
	}
	
	public Ant getAnt() {
		return ant;
	}
	
	public Caste getCaste() {
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
	
	public void pickUpSugar(SugarObject sugarObject) {
		// TODO change to unspecific caste
		int oldAmountOfSugarCarried = sugarCarry;
		sugarCarry = Math.min(caste.MAX_SUGAR_CARRY, sugarCarry + sugarObject.getAmount());
		sugarObject.reduceAmount(sugarCarry - oldAmountOfSugarCarried);
	}
	
	/** sets amount of carried sugar to 0 */
	public void dropSugar() {
		sugarCarry = 0;
	}
	
	/** Checks if AntObject has positive health. */
	public boolean isDying() {
		return (health <= 0);
	}
	
	/** calls ai.tick(), handles exceptions and saves the action */
	public void tick(List<Ant> visibleAnts, List<Sugar> visibleSugar, 
			List<Hill> visibleHills, List<Message> incomingMessages) {
		ai.visibleAnts = visibleAnts;
		ai.visibleSugar = visibleSugar;
		ai.visibleHills = visibleHills;
		ai.incomingMessages = incomingMessages;
		
		try {
			ai.tick();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		action = ai.popAction();
	}

	/** get new id for antObject */
	private static int getNewID() {
		idCounter++;
		return idCounter - 1;
	}

}
