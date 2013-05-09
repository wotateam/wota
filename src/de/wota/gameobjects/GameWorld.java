package de.wota.gameobjects;

import java.lang.Math;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.wota.Message;
import de.wota.gameobjects.SpacePartioning;
import de.wota.gameobjects.GameWorldParameters;
import de.wota.gameobjects.caste.Caste;

import de.wota.statistics.AbstractLogger;

import de.wota.utility.Vector;
import de.wota.Action;
import de.wota.AntOrder;
import de.wota.Player;

/**
 * Enthält alle Elemente der Spielwelt.
 * 
 * @author pascal
 */
public class GameWorld {

	public final List<Player> players = new LinkedList<Player>();
	private LinkedList<SugarObject> sugarObjects = new LinkedList<SugarObject>();
	private LinkedList<Message> messages = new LinkedList<Message>();

	private List<AbstractLogger> registeredLoggers = new LinkedList<AbstractLogger>();

	private static double maximumSight() {
		double maximum = 0;
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

	private SpacePartioning spacePartioning = new SpacePartioning(
			GameWorldParameters.SIZE_X, GameWorldParameters.SIZE_Y,
			maximumSight());

	public void addPlayer(Player player) {
		notifyLoggers(AbstractLogger.LogEventType.PLAYER_REGISTERED);

		players.add(player);
	}

	public void tick() {
		notifyLoggers(AbstractLogger.LogEventType.TICK);

		// create Ants for all AntObjects and the QueenObject and sets them in
		// the AntAI (the latter happens in AntObject.createAnt() )
		// also create Sugar for SugarObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				antObject.createAnt();
			}
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
				LinkedList<Sugar> visibleSugar = new LinkedList<Sugar>();
				LinkedList<Message> audibleMessages = new LinkedList<Message>();

				for (AntObject visibleAntObject : spacePartioning
						.antObjectsInsideCircle(
								antObject.getCaste().SIGHT_RANGE,
								antObject.getPosition())) {
					visibleAnts.add(visibleAntObject.getAnt());
				}

				for (SugarObject visibleSugarObject : spacePartioning
						.sugarObjectsInsideCircle(
								antObject.getCaste().SIGHT_RANGE,
								antObject.getPosition())) {
					visibleSugar.add(visibleSugarObject.getSugar());
				}

				for (MessageObject audibleMessageObject : spacePartioning
						.messageObjectsInsideCircle(
								antObject.getCaste().HEARING_RANGE,
								antObject.getPosition())) {
					audibleMessages.add(audibleMessageObject.getMessage());
				}
				antObject.tick(visibleAnts, visibleSugar, audibleMessages);
			}
		}

		// Includes discarding the MessageObject instances.
		spacePartioning.update();

		// execute all actions, ants get created
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeAction(antObject);
			}
			// order does matter since the queen creates new ants!
			executeAntOrders(player.queenObject); 
		}

		// Let ants die!
		for (Player player : players) {
			for (Iterator<AntObject> antObjectIter = player.antObjects
					.iterator(); antObjectIter.hasNext();) {
				AntObject maybeDead = antObjectIter.next();
				if (maybeDead.isDying()) {
					// hat neue Aktionen erzeugt.
					executeLastWill(maybeDead);
					antObjectIter.remove();
					spacePartioning.removeAntObject(maybeDead);
				}
			}
		}		
	}

	private void executeAntOrders(QueenObject queenObject) {
		List<AntOrder> antOrders = queenObject.getAntOrders();
		for (AntOrder antOrder : antOrders) {
			AntObject antObject = new AntObject(
					queenObject.player.hillObject.getPosition(),
					antOrder.getCaste(), antOrder.getAntAIClass(),
					queenObject.player);
			addAntObject(antObject, queenObject.player);
		}
	}

	public void addAntObject(AntObject antObject, Player player) {
		player.antObjects.add(antObject);
		spacePartioning.addAntObject(antObject);
	}

	/** führt die Aktion für das AntObject aus */
	private void executeAction(AntObject actor) {
		Action action = actor.getAction();

		if (action == null) {
			System.err.println("Action sollte nicht null sein! -> Exit");
			System.exit(1);
		}

		// Attack
		// TODO add collateral damage
		Ant targetAnt = action.getAttackTarget();
		if (targetAnt != null) {
			// TODO check if target is in range.
			AntObject target = targetAnt.antObject;
			target.takesDamage(actor.getAttack());
		}

		// Pick up sugar
		Sugar sugarSource = action.getSugarSource();
		if (sugarSource != null) {
			int amount = Math.min(
					actor.getCaste().MAX_SUGAR_CARRY - actor.getSugarCarry(),
					sugarSource.amount);
			actor.picksUpSugar(amount);
			sugarSource.sugarObject.reduceAmount(amount);
		}

		// Movement
		// executeMovement(actor, action);
		actor.move(Vector.fromPolar(action.getMovementDistance(),
				action.getMovementDirection()));

		// Messages
		handleMessages(actor, action);
	}

	/** wird nur aufgerufen bevor die Ant stirbt -> kein Angriff mehr */
	private void executeLastWill(AntObject actor) {
		Action action = actor.getAction();

		// Messages
		handleMessages(actor, action);
	}
	
	/** tests if victory condition is fulfilled and notifies the Logger
	 * Victory condition: is the queen alive? */
	public Player checkVictoryCondition() {
		List<Player> possibleWinners = new LinkedList<Player>(players);
		for (Player player : players) {
			if (player.queenObject.isDead()) {
				possibleWinners.remove(player);
			}
		}
		if (possibleWinners.size() == 1) {
			return possibleWinners.get(0);
		}
		else
			return null;
	}

	private void handleMessages(AntObject actor, Action action) {
		if (action.getMessageObject() != null) {
			spacePartioning.addMessageObject(action.getMessageObject());
			Message message = action.getMessageObject().getMessage();
			if (GameWorldParameters.DEBUG)
				System.out.println("\"" + message.content + "\" sagt "
						+ message.sender + ".");
		}
	}

	public void registerLogger(AbstractLogger logger) {
		registeredLoggers.add(logger);
	}

	private void notifyLoggers(AbstractLogger.LogEventType event) {
		for (AbstractLogger logger : registeredLoggers)
			logger.log(event);
	}
}
