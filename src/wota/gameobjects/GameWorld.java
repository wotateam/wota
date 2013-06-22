package wota.gameobjects;

import java.lang.Math;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wota.gamemaster.AILoader;
import wota.gamemaster.Logger;
import wota.gamemaster.RandomPosition;
import wota.gamemaster.SimulationParameters;
import wota.gamemaster.StatisticsLogger;
import wota.utility.Vector;



/**
 * Enthält alle Elemente der Spielwelt.
 * 
 * @author pascal
 */
public class GameWorld {
	private final List<Player> players = new LinkedList<Player>();
	private final LinkedList<SugarObject> sugarObjects = new LinkedList<SugarObject>();
	
	private Logger logger;

	private SpacePartitioning spacePartitioning;
	
	private final Parameters parameters;
	
	private int tickCount = 0;
	private final RandomPosition randomPosition;
	
	public GameWorld(Parameters parameters, RandomPosition randomPosition) {
		this.parameters = parameters;
		this.randomPosition = randomPosition;
		spacePartitioning = new SpacePartitioning(maximumSight(), parameters);
	}
	
	private static double maximumSight() {
		double maximum = 0.0;
		for (Caste caste : Caste.values()) {
			if (caste.SIGHT_RANGE > maximum) {
				maximum = caste.SIGHT_RANGE;
			}
			if (caste.HEARING_RANGE > maximum) {
				maximum = caste.HEARING_RANGE;
			}
		}
		return maximum;
	}
	
	public void createRandomSugarObject() {
		List<Vector> hillPositions = new LinkedList<Vector>();
		for (Player player : players) {
			hillPositions.add(player.hillObject.getPosition());
		}
		
		List<Vector> sugarPositions = new LinkedList<Vector>();
		for (SugarObject sugarObject : sugarObjects) {
			sugarPositions.add(sugarObject.getPosition());
		}
		
		SugarObject sugarObject = new SugarObject(randomPosition.sugarPosition(hillPositions, sugarPositions),
												  parameters);
		addSugarObject(sugarObject);
	}
	
	public void addSugarObject(SugarObject sugarObject) {
		sugarObjects.add(sugarObject);
		spacePartitioning.addSugarObject(sugarObject);
	}
	
	/** Do not modify the list! Use addSugarObject instead */
	public List<SugarObject> getSugarObjects() {
		return sugarObjects;
	}
		
	public void addPlayer(Player player) {
		players.add(player);
	}

	private static int nextPlayerId = 0;
	
	public class Player {
		public final List<AntObject> antObjects = new LinkedList<AntObject>();
		public final HillObject hillObject;
		public final QueenObject queenObject;

		public final String name;
		public final String creator;

		private final int id;
		
		public boolean hasLost = false;

		public int id() {
			return id;
		}

		// TODO make this private and change addPlayer
		public Player(Vector position, Class<? extends QueenAI> queenAIClass) {
			hillObject = new HillObject(position, this, parameters);
			spacePartitioning.addHillObject(hillObject);
			queenObject = new QueenObject(position, queenAIClass, this, parameters);
			
			antObjects.add(queenObject);		
		
			name = AILoader.getAIName(queenAIClass);
			creator = AILoader.getAICreator(queenAIClass);

			id = nextPlayerId;
			nextPlayerId++;
		}
		
		public void addAntObject(AntObject antObject) {
			antObjects.add(antObject);
			spacePartitioning.addAntObject(antObject);
		}
		
		public int numAnts(Caste caste) {
			int num = 0;
			for (AntObject antObject : antObjects) {
				if (antObject.getCaste() == caste) {
					num++;
				}
			}
			return num;
		}
		
	}
	
	public void tick() {		
		tickCount++;

		// create Ants for all AntObjects and the QueenObject and sets them in
		// the AntAI (the latter happens in AntObject.createAnt() )
		// also create Sugar for SugarObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				antObject.createAnt();
			}
			player.hillObject.createHill();
		}

		for (SugarObject sugarObject : sugarObjects) {
			sugarObject.createSugar();
		}
		
		// The MessageObjects don't need a "createMessage", because one can
		// construct the Message instance when the
		// MessageObject instance is constructed.

		// call tick for all AntObjects
		for (Player player : players) {			
			for (AntObject antObject : player.antObjects) {
				List<Ant> visibleAnts = new LinkedList<Ant>();
				List<Sugar> visibleSugar = new LinkedList<Sugar>();
				List<Hill> visibleHills = new LinkedList<Hill>();
				List<Message> audibleMessages = new LinkedList<Message>();

				for (AntObject visibleAntObject : 
					spacePartitioning.antObjectsInsideCircle(antObject.getCaste().SIGHT_RANGE, antObject.getPosition())) {
					if (visibleAntObject != antObject) {
						visibleAnts.add(visibleAntObject.getAnt());
					}
				}

				for (SugarObject visibleSugarObject : 
					spacePartitioning.sugarObjectsInsideCircle(antObject.getCaste().SIGHT_RANGE, antObject.getPosition())) {
					visibleSugar.add(visibleSugarObject.getSugar());
				}
				
				for (HillObject visibleHillObject :
					spacePartitioning.hillObjectsInsideCircle(antObject.getCaste().SIGHT_RANGE, antObject.getPosition())) {
					visibleHills.add(visibleHillObject.getHill());
				}				
				
				for (MessageObject audibleMessageObject : 
					spacePartitioning.messageObjectsInsideCircle(antObject.getCaste().HEARING_RANGE, antObject.getPosition())) {
					if (audibleMessageObject.getSender().playerID == antObject.player.id) {
						audibleMessages.add(audibleMessageObject.getMessage());
					}
				}
				antObject.tick(visibleAnts, visibleSugar, visibleHills, audibleMessages);
			}
		}
		// Only do this now that we used last tick's message objects.
		spacePartitioning.discardMessageObjects();
		
		// execute all actions, ants get created
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeActionExceptMovement(antObject);
			} 
		}
		
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeMovement(antObject);
			}

			// order does matter since the queen creates new ants!
			executeAntOrders(player.queenObject);
		}
		
		// Needs to go before removing dead ants, because they need to be in 
		// the correct cell to be removed.
		spacePartitioning.update(); 
				

		// Let ants die!
		for (Player player : players) {
			for (Iterator<AntObject> antObjectIter = player.antObjects.iterator();
					antObjectIter.hasNext();) {
				AntObject maybeDead = antObjectIter.next();
				if (maybeDead.isDead()) {
					antObjectIter.remove();
					antDies(maybeDead);
				}
			}
		}
		
		int removedSugarObjects = removeSugarAndDecreaseTicksToWait();
		for (int i=0; i<removedSugarObjects; i++) {
			createRandomSugarObject();
		}
		
	}

	public void antDies(AntObject almostDead) {
		spacePartitioning.removeAntObject(almostDead);
		almostDead.die();
		logger.antDied(almostDead);
	}
	
	/**
	 * iterates through sugarObjects and
	 * 1) removes the empty ones
	 * 2) decreases their ticksToWait by calling tick()
	 * 
	 * @return 
	 * 			The number of removed SugarObjects
	 */
	private int removeSugarAndDecreaseTicksToWait() {
		int nRemovedSugarObjects = 0;
		for (Iterator<SugarObject> sugarObjectIter = sugarObjects.iterator();
				sugarObjectIter.hasNext();) {
			SugarObject sugarObject = sugarObjectIter.next();
			
			sugarObject.tick();
			
			// remove if empty 
			if (sugarObject.getAmount() <= 0) {
				sugarObject.getsRemoved();
				sugarObjectIter.remove();
				spacePartitioning.removeSugarObject(sugarObject);
				nRemovedSugarObjects++;
			}
		}
		return nRemovedSugarObjects;
	}

	private void executeAntOrders(QueenObject queenObject) {
		List<AntOrder> antOrders = queenObject.getAntOrders();
		Iterator<AntOrder> iterator = antOrders.iterator();
		final Player player = queenObject.player;
		
		while (iterator.hasNext()) {
			AntOrder antOrder = iterator.next();
		
			if (parameters.ANT_COST <= player.hillObject.getStoredFood()) {
				player.hillObject.changeStoredFoodBy(-parameters.ANT_COST);
				
				AntObject newAntObject = new AntObject(
						queenObject.player.hillObject.getPosition(),
						antOrder.getCaste(), antOrder.getAntAIClass(),
						queenObject.player, parameters);
				createAntObject(queenObject, newAntObject);
			}
		}
	}

	/**
	 * @param queenObject  Queen which created this AntObject
	 * @param newAntObject freshly created AntObject
	 */
	private void createAntObject(QueenObject queenObject, AntObject newAntObject) {
		queenObject.player.addAntObject(newAntObject);
		logger.antCreated(newAntObject);
	}

	/** Führt die Aktion für das AntObject aus. 
	 * Beinhaltet Zucker abliefern.
	 *  */
	private void executeActionExceptMovement(AntObject actor) {
		Action action = actor.getAction();

		// Attack
		Ant targetAnt = action.attackTarget;
		if (targetAnt != null 
				&& parameters.distance(targetAnt.antObject.getPosition(), actor.getPosition()) 
				   <= parameters.ATTACK_RANGE) {
			
			AntObject target = targetAnt.antObject;
			actor.setAttackTarget(target);
			
			// collateral damage, including damage to target:
			// the formula how the damage decreases with distance yields full damage for distance 0.
			// the radius of the area of effect equals ATTACK_RANGE
			for (AntObject closeAntObject : spacePartitioning.antObjectsInsideCircle(parameters.ATTACK_RANGE, target.getPosition())) {
				if (closeAntObject.player != actor.player) {
					closeAntObject.takesDamage(actor.getCaste().ATTACK*
							fractionOfDamageInDistance(parameters.distance(closeAntObject.getPosition(),target.getPosition())));
				}
			}
			
		}
		else {
			actor.setAttackTarget(null);
		}

		// Drop sugar at the hill and reset ticksToLive if inside the hill.
		// Optimization: Use space partitioning for dropping sugar at the hill, don't test for all ants.
		if (parameters.distance(actor.player.hillObject.getPosition(), actor.getPosition())
				<= parameters.HILL_RADIUS) {
			actor.player.hillObject.changeStoredFoodBy(actor.getSugarCarry());
			logger.antCollectedFood(actor.player, actor.getSugarCarry());
			actor.dropSugar();
		}
		
		// or drop sugar if desired
		if (action.dropItem == true) {
			actor.dropSugar();
		}
		
		// Pick up sugar
		Sugar sugar = action.sugarTarget;
		if (sugar != null) {
			if (parameters.distance(actor.getPosition(),sugar.sugarObject.getPosition())
					<= sugar.sugarObject.getRadius()) {
				actor.pickUpSugar(sugar.sugarObject);
			}
		}
		
		// Messages
		handleMessages(actor, action);
	}
	
	private double fractionOfDamageInDistance(double distance) {
		double fraction = 1 - distance / parameters.ATTACK_RANGE;
		return Math.max(fraction, 0); 
	}
	
	private static void executeMovement(AntObject actor) {
		Action action = actor.getAction();

		if (actor.getAttackTarget() != null) {
			actor.move(action.movement.boundLengthBy(actor.getCaste().SPEED_WHILE_ATTACKING));
		} else if (actor.getSugarCarry() > 0) {
			actor.move(action.movement.boundLengthBy(actor.getCaste().SPEED_WHILE_CARRYING_SUGAR));
		} else {
			actor.move(action.movement.boundLengthBy(actor.getCaste().SPEED));
		}
	}
	
	/** check the victory condition after this amount of ticks */
	private static int DONT_CHECK_VICTORY_CONDITION_BEFORE = 100;
	
	public boolean allPlayersDead() {
		if (tickCount < DONT_CHECK_VICTORY_CONDITION_BEFORE) {
			return false;
		}
		
		int nPlayersAlive = players.size();
		for (Player player : players) {
			if ( (player.antObjects.size() == 1 && !player.queenObject.isDead() ) ||
					player.antObjects.size() == 0) {
				nPlayersAlive--;
			}
		}
		return nPlayersAlive == 0;
	}
	
	public Player getWinner() {
		if (tickCount < DONT_CHECK_VICTORY_CONDITION_BEFORE) {
			return null;
		}
		
		double totalAnts = 0;
		for (Player player : players) {
			totalAnts += player.antObjects.size();
		}
		
		for (Player player : players) {
			if (player.antObjects.size() / totalAnts >= parameters.FRACTION_OF_ALL_ANTS_NEEDED_FOR_VICTORY) {
				return player;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets all players who currently have the most ants.
	 * @return List of players who currently have the most ants of all players.
	 */
	public List<Player> getPlayersWithMostAnts() {
		List<Player> playersWithMostAnts = new LinkedList<Player>();
		for (Player player : players) {
			if (playersWithMostAnts.isEmpty()) {
				playersWithMostAnts.add(player);
			}
			else {
				int antsPlayer = player.antObjects.size();
				int antsNeeded = playersWithMostAnts.get(0).antObjects.size();
				if (antsPlayer > antsNeeded) {
					playersWithMostAnts.clear();
					playersWithMostAnts.add(player);
				}
				else if (antsPlayer == antsNeeded) {
					playersWithMostAnts.add(player);
				}
			}
		}
		return playersWithMostAnts;
	}

	private void handleMessages(AntObject actor, Action action) {
		if (action.messageObject != null) {
			spacePartitioning.addMessageObject(action.messageObject);
			Message message = action.messageObject.getMessage();
		}
	}

	public int totalNumberOfAntObjects() {
		int n = 0;
		for (Player player : players) {
			n += player.antObjects.size();
		}
		return n;
	}
	
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players); 
	}

	/**
	 * @return number of passed ticks
	 */
	public int tickCount() {
		return tickCount;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
