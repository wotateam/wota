package de.wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import de.wota.Vector;
import de.wota.Action;
import de.wota.AntOrder;
import de.wota.Player;
import de.wota.ai.DemoAntAI;
import de.wota.ai.DummyHillAI;

/**
 * Enthält alle Elemente der Spielwelt.
 * 
 * @author pascal
 */
public class GameWorld {

	public final List<Player> players = new LinkedList<Player>();
	private LinkedList<Sugar> sugars;

	public void tick() {
		
		// create Ants for all AntObjects and sets them in the AntAI
		// (the latter happens in AntObject.createAnt() )
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				antObject.createAnt();
			}
		}
		
		// call tick for all AntObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				LinkedList<Ant> visibleAnts = new LinkedList<Ant>();
				LinkedList<Sugar> visibleSugar = new LinkedList<Sugar>();

				// TODO pass visibleAnts and visibleSugar
				antObject.tick(visibleAnts, visibleSugar);
			}
		}

		// execute all actions
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeAction(antObject);
			}
			// order does matter since the hill creates new ants!
			executeAction(player.hillObject); 
		}
	}

	private static void executeAction(HillObject hill) {
		// can only produce units
		List<AntOrder> antOrders = hill.getAntOrders();
		for (AntOrder antOrder : antOrders) {
			AntObject antObject = 
					new AntObject(
						hill.getPosition(),
						antOrder.getCaste(),
						antOrder.getAntAIClass()
					);
			hill.getPlayer().antObjects.add(antObject);
		}
	}
	
	/** führt die Aktion für das AntObject aus */
	private static void executeAction(AntObject actor) {
		Action action = actor.getAction();
		
		// Attack
		// TODO add collateral damage
		Ant targetAnt = action.getAttackTarget();
		// TODO check if target is in range.
		AntObject target = targetAnt.antObject;
		target.takesDamage(actor.getAttack());
		
		// Movement
		actor.move(Vector.fromPolar(action.getMovementDistance(), action.getMovementDirection()));
		
		// Messages
		// TODO Messages
	}
	
	public static GameWorld testWorld() throws InstantiationException, IllegalAccessException {
		GameWorld world = new GameWorld();
		for (int i = 0; i < 2; i++) {
			Player player = new Player(DummyHillAI.class, new Vector(100+i*200,100+i*200));
			for (int j = 0; j < 10; j++) {
				AntObject antObject = new AntObject(new Vector(j * 10, 20 + i * 20), Ant.Caste.GATHERER, DemoAntAI.class);
				player.antObjects.add(antObject);
			}
			world.players.add(player);
		}
		return world;
	}
}
