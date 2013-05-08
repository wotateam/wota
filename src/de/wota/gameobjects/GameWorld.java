package de.wota.gameobjects;

import java.lang.Math;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.wota.Message;
import de.wota.gameobjects.SpacePartioning;
import de.wota.gameobjects.GameWorldParameters;

import de.wota.statistics.AbstractLogger;

import de.wota.utility.Vector;
import de.wota.Action;
import de.wota.AntOrder;
import de.wota.Player;
import de.wota.ai.AntAI;

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

	public void tick() {
		notifyLoggers(AbstractLogger.LogEventType.TICK);

		// create Ants for all AntObjects and sets them in the AntAI
		// (the latter happens in AntObject.createAnt() )
		// also create Sugar for SugarObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				antObject.createAnt();
			}
		}
		for (SugarObject sugarObject : sugarObjects) {
			sugarObject.createSugar();
		}
		
		// call tick for all AntObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				LinkedList<Ant> visibleAnts = new LinkedList<Ant>();
				LinkedList<Sugar> visibleSugar = new LinkedList<Sugar>();
				LinkedList<Message> audibleMessages = new LinkedList<Message>();

				// TODO objekte richtig befüllen.
				antObject.tick(visibleAnts, visibleSugar, audibleMessages);
			}
		}

		// execute all actions, ants get created
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeAction(antObject);
			}
			// order does matter since the hill creates new ants!
			executeAntOrders(player.queenObject); 
		}
		
		// Let ants die!
		for (Player player : players) {
			for (Iterator<AntObject> antObjectIter = player.antObjects.iterator(); antObjectIter.hasNext();) {
				AntObject maybeDead = antObjectIter.next();
				if (maybeDead.isDying()) {
					// hat neue Aktionen erzeugt.
					executeLastWill(maybeDead);
					antObjectIter.remove();
				}
			}
		}
	}
	
	private static void executeAntOrders(QueenObject queen) {
		List<AntOrder> antOrders = queen.getAntOrders();
		for (AntOrder antOrder : antOrders) {
			AntObject antObject = 
					new AntObject(
						queen.player.hillObject.getPosition(),
						antOrder.getCaste(),
						antOrder.getAntAIClass(),
						queen.player
					);
			queen.player.antObjects.add(antObject);
			
		}
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
			int amount = Math.min(actor.getCaste().MAX_SUGAR_CARRY - actor.getSugarCarry(), sugarSource.amount);
			actor.picksUpSugar(amount);
			sugarSource.sugarObject.reduceAmount(amount);
		}
		
		// Movement
		//executeMovement(actor, action);
		actor.move(Vector.fromPolar(action.getMovementDistance(), action.getMovementDirection()));
		
		// Messages
		handleMessages(actor, action);
	}
	
	/** wird nur aufgerufen bevor die Ant stirbt -> kein Angriff mehr */
	private void executeLastWill(AntObject actor) {
		Action action = actor.getAction();

		// Messages
		handleMessages(actor, action);
	}
	
	private void handleMessages(AntObject actor, Action action) {
		if (action.getMessage() != null) {
			Message message = action.getMessage();
			messages.add(message);
			if (GameWorldParameters.DEBUG)
				System.out.println("\"" + message.getContent() + "\" sagt " + message.getTalkingAnt() + ".");
		}
	}
	
	public void registerLogger(AbstractLogger logger)
	{
		registeredLoggers.add(logger);
	}
	
	private void notifyLoggers(AbstractLogger.LogEventType event)
	{
		for (AbstractLogger logger : registeredLoggers)
			logger.log(event);
	}
}
