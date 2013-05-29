package de.wota.gameobjects;

import java.util.List;

import de.wota.utility.SeededRandomizer;
import de.wota.utility.Vector;
import de.wota.gameobjects.Parameters;

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
	private int ticksToLive;
	
	/** amount of sugar carried now */
	private int sugarCarry = 0;
	
	/** number of ticks Ant is unable to act (ai.tick doesn't get called */
	private int freezeTime = 0;
	
	/** Angriffspunkte */
	private Action action;
	private final Caste caste;
	public final GameWorld.Player player;
	boolean isAttacking = false;
	
	public AntObject(Vector position, Caste caste, Class<? extends AntAI> antAIClass, GameWorld.Player player, Parameters parameters) {
		super(position, parameters);
		
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
		antAI.setParameters(parameters);
		this.caste = caste;
		// set parameters
		health = caste.INITIAL_HEALTH;
		speed = caste.SPEED;
		
		this.ai = antAI;
		this.ai.setAntObject(this);
		
		resetTicksToLive();
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
		double takenDamage;
		if (isCarrying()) {
			takenDamage = parameters.VULNERABILITY_WHILE_CARRYING * attack;
		}
		else {
			takenDamage = attack;
		}
			
		health = health - takenDamage;
	}
	
	public void pickUpSugar(SugarObject sugarObject) {
		int oldAmountOfSugarCarried = sugarCarry;
		sugarCarry = Math.min(caste.MAX_SUGAR_CARRY, sugarCarry + sugarObject.getAmount());
		
		// freeze if some sugar got picked up.
		if (sugarCarry - oldAmountOfSugarCarried > 0) {
			freezeTime = sugarObject.getTicksToWait();
		}
		
		// be careful! calling reduceAmount increases sugarObject.ticksToWait
		sugarObject.reduceAmount(sugarCarry - oldAmountOfSugarCarried);
		
	}
	
	/** sets amount of carried sugar to 0 */
	public void dropSugar() {
		sugarCarry = 0;
	}
	
	/** Checks if AntObject has positive health and has been at its hill less than 
	 *  than TICKS_TO_LIVE ticks ago. */
	public boolean isDead() {
		return (health <= 0) || (ticksToLive < 0);
	}
	
	/** calls ai.tick(), handles exceptions and saves the action */
	public void tick(List<Ant> visibleAnts, List<Sugar> visibleSugar, 
			List<Hill> visibleHills, List<Message> incomingMessages) {
		ai.visibleAnts = visibleAnts;
		ai.visibleSugar = visibleSugar;
		ai.visibleHills = visibleHills;
		ai.audibleMessages = incomingMessages;
		
		ticksToLive--;
		
		if (freezeTime == 0) {
			try {
				ai.tick();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			freezeTime--;
		}
		
		action = ai.popAction();
	}
	
	public boolean isCarrying() {
		return sugarCarry > 0;
	}

	@Override
	public void move(Vector moveVector) {
		if (moveVector.length() != 0) {
			double angleError = parameters.ANGLE_ERROR_PER_DISTANCE * moveVector.length() 
					* 2 * (SeededRandomizer.nextDouble() - 0.5); 
			super.move(Vector.fromPolar(moveVector.length(), moveVector.angle() + angleError));
		} else {
			super.move(moveVector);
		}
	}
	
	/** get new id for antObject */
	private static int getNewID() {
		idCounter++;
		return idCounter - 1;
	}

	public void resetTicksToLive() {
		ticksToLive = parameters.TICKS_TO_LIVE;
	}

}
