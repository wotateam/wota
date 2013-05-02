package de.wota;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import de.wota.ai.Ant;
import de.wota.ai.DummyHillAI;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.Sugar;
import de.wota.test.DummyAntAI;

/**
 * Enthält alle Elemente der Spielwelt.
 * 
 * @author pascal
 */
public class GameWorld {

	public final List<Player> players = new LinkedList<Player>();
	private LinkedList<Sugar> sugars;

	public void tick() {
		
		// create Ants for all AntObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				antObject.createAnt();
			}
		}
		
		// call tick for all AntObjects
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				LinkedList<Ant> visibleAnts = new LinkedList<Ant>();
				// TODO pass visibleAnts
				antObject.tick(visibleAnts);
			}
		}

		// execute all actions
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeAction(antObject);
			}
		}
	}

	/** führt die Aktion für das AntObject aus */
	private void executeAction(AntObject actor) {
		Action action = actor.getAction();
		
		// Attack
		// TODO add collateral damage
		Ant targetAnt = action.getAttackTarget();
		AntObject target = getAntObjectById(targetAnt.ID);
		if (target != null) {
			target.takesDamage(actor.getAttack());
		}
		else {
			System.err.println("unexpected case in GameWorld.executeAction(AntObject antObject)");
		}
		
		// Movement
		// TODO Movement
		
		// Messages
		// TODO Messages
	}

	private AntObject getAntObjectById(int id) {
		// TODO use dictionary or something!
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				if (antObject.ID == id) {
					return antObject;
				}
			}
		}
		return null;
	}
	
	public static GameWorld testWorld() throws InstantiationException, IllegalAccessException {
		GameWorld world = new GameWorld();
		for (int i = 0; i < 2; i++) {
			Player player = new Player(DummyAntAI.class, DummyHillAI.class, new Point2D.Double(100+i*200,100+i*200));
			for (int j = 0; j < 10; j++) {
				int id = i*10+j;
				AntObject antObject = new AntObject(new Point2D.Double(j * 10, 20 + i * 20), id);
				player.antObjects.add(antObject);
			}
			world.players.add(player);
		}
		return world;
	}
}
