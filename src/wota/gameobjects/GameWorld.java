package wota.gameobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wota.gamemaster.AILoader;
import wota.gamemaster.Logger;
import wota.gamemaster.RandomPosition;
import wota.utility.Vector;



/**
 * Contains all elements of the game world.
 */
public class GameWorld {
	private final List<Player> players = new LinkedList<Player>();
	private final LinkedList<SugarObject> sugarObjects = new LinkedList<SugarObject>();
	
	private Logger logger;

	private SpacePartitioning spacePartitioning;
	
	public final Parameters parameters;
	
	public final long seed;
	
	private int tickCount = 0;
	private final RandomPosition randomPosition;
	
	public GameWorld(Parameters parameters, RandomPosition randomPosition, long seed) {
		this.seed = seed;
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
			hillPositions.add(player.getHillObject().getPosition());
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

	private int nextPlayerId = 0;
	
	public class Player {
		private final List<AntObject> antObjects = new LinkedList<AntObject>();
		private final List<AntCorpseObject> antCorpseObjects = new LinkedList<AntCorpseObject>();
		private final HillObject hillObject;

		public final String name;
		public final String creator;

		private final int id;

		public int id() {
			return id;
		}

		// TODO make this private and change addPlayer
		public Player(Vector position, Class<? extends HillAI> hillAIClass) {
			hillObject = new HillObject(position, this, hillAIClass, parameters);
			spacePartitioning.addHillObject(getHillObject());
					
			name = AILoader.getAIName(hillAIClass);
			creator = AILoader.getAICreator(hillAIClass);

			id = nextPlayerId;
			nextPlayerId++;
		}
		
		@Override
		public String toString() {
			return "AI " + (id +1) + " " + name + " written by " + creator;
		}
		
		public synchronized void addAntObject(AntObject antObject) {
			getAntObjects().add(antObject);
			spacePartitioning.addAntObject(antObject);
		}
		
		/**
		 * removes antObject from the antObjects list
		 * @param antObject antObject to remove
		 */
		public synchronized void removeAntObject(AntObject antObject) {
			getAntObjects().remove(antObject);
		}
				
		/**
		 * @param caste
		 * @return number of ants of given caste
		 * @throws NullPointerException if antObjects is null
		 */
		public synchronized int numAnts(Caste caste) throws NullPointerException{
			int num = 0;
			for (AntObject antObject : getAntObjects()) {
				if (antObject.caste == caste) {
					num++;
				}
			}
			return num;
		}

		/**
		 * @return antObjects
		 */
		public synchronized List<AntObject> getAntObjects() {
			return antObjects;
		}

		/**
		 * @return the antCorpseObjects
		 */
		public List<AntCorpseObject> getAntCorpseObjects() {
			return antCorpseObjects;
		}

		/**
		 * @return the hillObject
		 */
		public HillObject getHillObject() {
			return hillObject;
		}
		
	}
	
	public void tick() {		
		tickCount++;

		// create Ants for all AntObjects and the QueenObject and sets them in
		// the AntAI (the latter happens in AntObject.createAnt() )
		// also create Sugar for SugarObjects
		for (Player player : players) {
			for (AntObject antObject : player.getAntObjects()) {
				antObject.createAnt();
			}
			for (AntCorpseObject antCorpseObject : player.getAntCorpseObjects()) {
				antCorpseObject.createAntCorpse();
			}
			player.getHillObject().createHill();
		}

		for (SugarObject sugarObject : sugarObjects) {
			sugarObject.createSugar();
		}

		// call tick for all AntObjects
		for (Player player : players) {			
			for (AntObject antObject : player.getAntObjects()) {
				List<Ant> visibleAnts = new LinkedList<Ant>();
				List<AntCorpse> visibleCorpses = new LinkedList<AntCorpse>();
				List<AntMessage> audibleAntMessages = new LinkedList<AntMessage>();
				HillMessage audibleHillMessage = null;

				double sightRange = antObject.caste.SIGHT_RANGE;
				double hearingRange = antObject.caste.HEARING_RANGE;
				Vector position = antObject.getPosition();
				
				// DONT FORGET THAT EVERY CHANGE HAS TO HAPPEN FOR HILL AS WELL
				for (AntObject visibleAntObject : 
						spacePartitioning.antObjectsInsideCircle(sightRange, position)) {
					if (visibleAntObject != antObject) {
						visibleAnts.add(visibleAntObject.getAnt());
					}
				}

				for (AntCorpseObject visibleAntCorpseObject : 
					spacePartitioning.antCorpseObjectsInsideCircle(sightRange, position)) {
					visibleCorpses.add(visibleAntCorpseObject.getAntCorpse());
				}
				
				// add messages, also the ones which were send by this ant.
				for (AntMessage audibleAntMessage : 
						spacePartitioning.antMessagesInsideCircle(hearingRange, position)) {
					if (audibleAntMessage.sender.playerID == player.id() &&
						audibleAntMessage.sender.id != antObject.id ) {	// cannot hear own messages
						audibleAntMessages.add(audibleAntMessage);
					}
				}
				
				for (HillMessage audibleHillMessageCandidate :
						spacePartitioning.hillMessagesInsideCircle(hearingRange, position)) {
					if (audibleHillMessageCandidate.sender.playerID == player.id()) {
						audibleHillMessage = audibleHillMessageCandidate;
					}
				}

				List<Sugar> visibleSugar = new LinkedList<Sugar>();
				List<Hill> visibleHills = new LinkedList<Hill>();
				
				for (SugarObject visibleSugarObject : 
						spacePartitioning.sugarObjectsInsideCircle(sightRange, position)) {
					visibleSugar.add(visibleSugarObject.getSugar());
				}
				
				for (HillObject visibleHillObject :
						spacePartitioning.hillObjectsInsideCircle(sightRange, position)) {
					visibleHills.add(visibleHillObject.getHill());
				}				
				
				antObject.tick(visibleAnts, visibleCorpses, visibleSugar, visibleHills, audibleAntMessages, audibleHillMessage);
			}
			
			// call tick for all corpses
			for (AntCorpseObject antCorpseObject : player.getAntCorpseObjects()) {			
				antCorpseObject.tick();
			}
			
			// and now for the hill. Sorry for the awful duplication of code but I couldn't see a way without 
			// lots of work
			
			// Sugar sources and hills are not seen by hills.

			List<Ant> visibleAnts = new LinkedList<Ant>();
			List<AntCorpse> visibleCorpses = new LinkedList<AntCorpse>();
			List<AntMessage> audibleAntMessages = new LinkedList<AntMessage>();

			double sightRange = player.getHillObject().caste.SIGHT_RANGE;
			double hearingRange = player.getHillObject().caste.HEARING_RANGE;
			Vector position = player.getHillObject().getPosition();
			
			for (AntObject visibleAntObject : 
					spacePartitioning.antObjectsInsideCircle(sightRange, position)) {
					visibleAnts.add(visibleAntObject.getAnt());
			}

			for (AntCorpseObject visibleAntCorpseObject : 
				spacePartitioning.antCorpseObjectsInsideCircle(sightRange, position)) {
				visibleCorpses.add(visibleAntCorpseObject.getAntCorpse());
			}		
			
			for (AntMessage audibleAntMessage : 
					spacePartitioning.antMessagesInsideCircle(hearingRange, position)) {
				if (audibleAntMessage.sender.playerID == player.id()) {
					audibleAntMessages.add(audibleAntMessage);
				}
			}
			
			player.getHillObject().tick(visibleAnts, visibleCorpses, audibleAntMessages);

		}
		// Only do this now that we used last tick's message objects.
		spacePartitioning.discardAntMessages();
		spacePartitioning.discardHillMessages();
		
		// execute all actions, ants get created
		for (Player player : players) {
			for (AntObject antObject : player.getAntObjects()) {
				executeActionExceptMovement(antObject);
			} 
		}
		
		for (Player player : players) {
			for (AntObject antObject : player.getAntObjects()) {
				executeMovement(antObject);
			}

			// order does matter since the hill creates new ants!
			handleHillMessage(player.getHillObject(), player.getHillObject().getMessage());
			executeAntOrders(player.getHillObject());
		}
		
		// The sugar objects needs to go after the ant actions, because now
		// the ants which receive sugar are chosen.
		for (SugarObject sugarObject : sugarObjects) {
			sugarObject.tick();
		}
		
		// Needs to go before removing dead ants, because they need to be in 
		// the correct cell to be removed.
		spacePartitioning.update(); 
				

		// Let ants die! (i.e. turn them into corpses) and let corpses disappear
		LinkedList<AntObject> antObjectsToRemove = new LinkedList<AntObject>();
		LinkedList<AntCorpseObject> corpsesToRemove = new LinkedList<AntCorpseObject>();
		for (Player player : players) {
			antObjectsToRemove.clear();
			corpsesToRemove.clear();
			for (AntObject antObject : player.getAntObjects()) {
				if (antObject.isDead()) {
					antDies(antObject);
					antObjectsToRemove.add(antObject);
				}
			}
			for(AntObject deadAnt : antObjectsToRemove)
				player.removeAntObject(deadAnt);
			
			for (AntCorpseObject antCorpseObject : player.getAntCorpseObjects()) {
				if (antCorpseObject.isDecayed()) {
					corpsesToRemove.add(antCorpseObject);
					antCorpseDisappears(antCorpseObject);
				}
			}
			player.getAntCorpseObjects().removeAll(corpsesToRemove);
		}
		
		int removedSugarObjects = removeSugarObjects();
		for (int i=0; i<removedSugarObjects; i++) {
			createRandomSugarObject();
		}
		
	}

	public void antDies(AntObject almostDead) {
		//almostDead.die();
		logger.antDied(almostDead);
		spacePartitioning.removeAntObject(almostDead);
		
		AntCorpseObject freshCorpse = new AntCorpseObject(almostDead);
		almostDead.player.getAntCorpseObjects().add(freshCorpse);
		spacePartitioning.addAntCorpseObject(freshCorpse);
	}
	
	public void antCorpseDisappears(AntCorpseObject almostDecayed) {
		spacePartitioning.removeAntCorpseObject(almostDecayed);
	}
	
	/**
	 * iterates through sugarObjects and removes the empty ones
	 * 
	 * @return 
	 * 			The number of removed SugarObjects
	 */
	private int removeSugarObjects() {
		int nRemovedSugarObjects = 0;
		for (Iterator<SugarObject> sugarObjectIter = sugarObjects.iterator();
				sugarObjectIter.hasNext();) {
			SugarObject sugarObject = sugarObjectIter.next();

			// remove if empty 
			if (sugarObject.getAmount() <= 0) {
				spacePartitioning.removeSugarObject(sugarObject);
				sugarObjectIter.remove();
				nRemovedSugarObjects++;
			}
		}
		return nRemovedSugarObjects;
	}

	private void executeAntOrders(HillObject hillObject) {
		List<AntOrder> antOrders = hillObject.getAntOrders();
		Iterator<AntOrder> iterator = antOrders.iterator();
		final Player player = hillObject.getPlayer();

		while (iterator.hasNext()) {
			AntOrder antOrder = iterator.next();
		
			if (parameters.ANT_COST <= player.getHillObject().getStoredFood()) {
				player.getHillObject().changeStoredFoodBy(-parameters.ANT_COST);
				
				AntObject newAntObject = new AntObject(
						hillObject.getPlayer().getHillObject().getPosition(),
						antOrder.getCaste(), antOrder.getAntAIClass(),
						hillObject.getPlayer(), parameters);
				createAntObject(hillObject, newAntObject);
			}
		}
	}

	/**
	 * @param hillObject  Hill which created this AntObject
	 * @param newAntObject freshly created AntObject
	 */
	private void createAntObject(HillObject hillObject, AntObject newAntObject) {
		hillObject.getPlayer().addAntObject(newAntObject);
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
					closeAntObject.takesDamage(actor.caste.ATTACK*
							fractionOfDamageInDistance(parameters.distance(closeAntObject.getPosition(),target.getPosition())));
				}
			}
			
		}
		else {
			actor.setAttackTarget(null);
		}

		// Drop sugar at the hill.
		// Optimization: Use space partitioning for dropping sugar at the hill, don't test for all ants.
		if (parameters.distance(actor.player.getHillObject().getPosition(), actor.getPosition())
				<= parameters.HILL_RADIUS && actor.getSugarCarry() > 0) {
			actor.player.getHillObject().changeStoredFoodBy(actor.getSugarCarry());
			logger.antCollectedFood(actor.player, actor.getSugarCarry());
			actor.dropSugar();
		}
		
		// or drop sugar if desired
		if (action.dropItem == true) {
			actor.dropSugar();
		}
		
		// Try picking up sugar
		Sugar sugar = action.sugarTarget;
		if (sugar != null) {
			if (parameters.distance(actor.getPosition(),sugar.sugarObject.getPosition())
					<= sugar.sugarObject.getRadius()) {
				sugar.sugarObject.addSugarCandidate(actor);
			}
		}
		
		// Messages
		handleAntMessages(actor, action);
	}
	
	private double fractionOfDamageInDistance(double distance) {
		double fraction = 1 - distance / parameters.ATTACK_RANGE;
		return Math.max(fraction, 0); 
	}
	
	private void handleHillMessage(HillObject actor, HillMessage message) {
		if (message != null) {
			spacePartitioning.addHillMessage(message);
		}
	}

	private void handleAntMessages(AntObject actor, Action action) {
		if (action.antMessage != null) {
			spacePartitioning.addAntMessage(action.antMessage);
		}
	}

	private static void executeMovement(AntObject actor) {
		Action action = actor.getAction();

		if (actor.getAttackTarget() != null) {
			actor.move(action.movement.boundLengthBy(actor.caste.SPEED_WHILE_ATTACKING));
		} else if (actor.getSugarCarry() > 0) {
			actor.move(action.movement.boundLengthBy(actor.caste.SPEED_WHILE_CARRYING_SUGAR));
		} else {
			actor.move(action.movement.boundLengthBy(actor.caste.SPEED));
		}
	}
	
	public boolean allPlayersDead() {
		if (tickCount < parameters.DONT_CHECK_VICTORY_CONDITION_BEFORE) {
			return false;
		}
		
		int nPlayersAlive = players.size();
		for (Player player : players) {
			if ( player.getAntObjects().size() == 0 ) {
				nPlayersAlive--;
			}
		}
		return nPlayersAlive == 0;
	}
	
	/**
	 * Produces a list of players who have won the game.
	 * Returns an empty list if and only if no one has won so far.  
	 * @return 
	 */
	public List<Player> getWinner() {
		List<Player> winnerList = new ArrayList<Player>();

		if (tickCount < parameters.DONT_CHECK_VICTORY_CONDITION_BEFORE) {
			return winnerList;
		}
		
		double totalAnts = 0;
		for (Player player : players) {
			totalAnts += player.getAntObjects().size();
		}
		
		if (totalAnts == 0) {
			return new ArrayList<Player>(players);
		}
		
		for (Player player : players) {
			if (player.getAntObjects().size() / totalAnts >= parameters.FRACTION_OF_ALL_ANTS_NEEDED_FOR_VICTORY) {
				winnerList.add(player);
				return winnerList;
			}
		}
		
		if (tickCount >= parameters.MAX_TICKS_BEFORE_END) {
			return playersWithMostAnts();
		}
		
		return winnerList;
	}
	
	/**
	 * Gets all players who currently have the most ants.
	 * @return List of players who currently have the most ants of all players.
	 */
	private List<Player> playersWithMostAnts() {
		List<Player> playersWithMostAnts = new LinkedList<Player>();
		for (Player player : players) {
			if (playersWithMostAnts.isEmpty()) {
				playersWithMostAnts.add(player);
			}
			else {
				int antsPlayer = player.getAntObjects().size();
				int antsNeeded = playersWithMostAnts.get(0).getAntObjects().size();
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

	public int totalNumberOfAntObjects() {
		int n = 0;
		for (Player player : players) {
			n += player.getAntObjects().size();
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
