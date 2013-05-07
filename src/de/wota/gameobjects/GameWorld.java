package de.wota.gameobjects;

import java.lang.Math;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.wota.Message;
import de.wota.gameobjects.GameWorldParameters;

import de.wota.statistics.AbstractLogger;

import de.wota.utility.Vector;
import de.wota.utility.Modulo;
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

				antObject.tick(visibleAnts, visibleSugar, audibleMessages);
			}
			// TODO objekte richtig befüllen.
			LinkedList<Ant> visibleAnts = new LinkedList<Ant>();
			LinkedList<Sugar> visibleSugar = new LinkedList<Sugar>();
			LinkedList<Message> audibleMessages = new LinkedList<Message>();
			player.queenObject.tick(visibleAnts, visibleSugar, audibleMessages);
		}

		// execute all actions, ants get created
		for (Player player : players) {
			for (AntObject antObject : player.antObjects) {
				executeAction(antObject);
			}
			// order does matter since the hill creates new ants!
			executeAction(player.queenObject); 
		}
		
		// Let ants die!
		for (Player player : players) {
			//LinkedList<AntObject> antObjectsToDie = new LinkedList<AntObject>();

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
	
	private static void executeAction(QueenObject queen) {
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
/*
	private static void executeAction(HillObject hill) {
		// can only produce units

	}
	
	*/

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
	
	/**
	 * When determining which objects are visible to an ant, we would like to avoid 
	 * iterating over all objects. We subdivide the torus into cells and only look
	 * at the cells adjacent to the ant and the cell containing it.
	 * 
	 * The game takes place on a torus. Periodicity is implemented having the zeroth 
	 * row point to the same cells as the second to last and the last point to the same 
	 * cells as the first.
	 * 
	 * @author daniel
	 *
	 */
	private static class SpacePartioning {

		public SpacePartioning(double width, double height, double minimumCellSize, 
				List<Player> players, List<SugarObject> sugarObjects) {
			this.minimumCellSize = minimumCellSize;
			numberOfHorizontalCells = (int) Math.round(Math.floor(width/minimumCellSize));
			numberOfVerticalCells = (int) Math.round(Math.floor(height/minimumCellSize));
			cellWidth = width / numberOfHorizontalCells;
			cellHeight = height / numberOfVerticalCells;
			
			cells = new Cell[numberOfHorizontalCells + 2][numberOfVerticalCells + 2];
			for (int i = 1; i < numberOfHorizontalCells + 1; i++) {
				for (int j = 1; j < numberOfVerticalCells + 1; j++) {
					cells[i][j] = new Cell(); 
				}
			}
			
			for (int i = 0; i < numberOfHorizontalCells + 2; i++) {
				cells[i][0] = cells[i][numberOfVerticalCells];
				cells[i][numberOfVerticalCells+1] = cells[i][1];
			}
			
			for (int j = 0; j < numberOfVerticalCells + 2; j++) {
				cells[0][j] = cells[numberOfHorizontalCells][j];
				cells[numberOfHorizontalCells+1][j] = cells[1][j];
			}
			
			// populate cells
			for (Player player : players) {
				for (AntObject antObject : player.antObjects) {
					coordinatesToCell(antObject.getPosition()).antObjects.add(antObject);
				}
				coordinatesToCell(player.hillObject.getPosition()).hillObjects.add(player.hillObject);
			}
			
			for (SugarObject sugarObject : sugarObjects) {
				coordinatesToCell(sugarObject.getPosition()).sugarObjects.add(sugarObject);
			}
		}
		
		public void update() {
			for (int i = 1; i < numberOfHorizontalCells + 1; i++) {
				for (int j = 1; j < numberOfVerticalCells + 1; j++) {
					Cell cell = cells[i][j];
					update(cell, i, j, Cell.antObjectsField);
					update(cell, i, j, Cell.hillObjectsField);
					update(cell, i, j, Cell.sugarObjectsField);
				}
			}
		}
		
		private <T extends GameObject> void update(Cell cell, int i, int j, GameObjectListField<T> field) {
			List<T> listOfTs = field.get(cell);
			
			Iterator<T> iterator = listOfTs.iterator();
			while (iterator.hasNext()) {
				T t = iterator.next();
				Vector p = t.getPosition();
				int newI = coordinatesToCellXIndex(p);
				int newJ = coordinatesToCellYIndex(p);
				
				if (i != newI || j != newJ) {
					iterator.remove();
					field.get(cells[newI][newJ]).add(t);
				}
			}
		}
		
		// Relative coordinates of visible cells, i.e. the cell itself and the adjacent ones.
		private final int numberOfVisibleCells = 9;
		private final int[] deltaX = {0, 1, 1, 0, -1, -1, -1,  0,  1};
		private final int[] deltaY = {0, 0, 1, 1,  1,  0, -1, -1, -1};
		
		private <T extends GameObject> List<T> TsInsideCircle(double radius, Vector center, GameObjectListField<T> field) {
			if (radius > minimumCellSize) { 
				throw new Error("radius > minimumCellSize");
			}
			List<T> listOfTsInsideCircle = new LinkedList<T>();
			int x = coordinatesToCellXIndex(center);
			int y = coordinatesToCellYIndex(center);
			
			for (int i = 0; i < numberOfVisibleCells; i++) {
				for (T t : field.get(cells[x+deltaX[i]][y+deltaY[i]])) {
					if (GameWorldParameters.shortestDifferenceOnTorus(t.getPosition(),center).length() < radius) {
						listOfTsInsideCircle.add(t);
					}
				}
			}
			
			return listOfTsInsideCircle;
		}
		
		public List<AntObject> antObjectsInsideCircle(double radius, Vector center) {
			return TsInsideCircle(radius, center, Cell.antObjectsField);
		}
		
		public List<HillObject> hillObjectsInsideCircle(double radius, Vector center) {
			return TsInsideCircle(radius, center, Cell.hillObjectsField);
		}
		
		public List<SugarObject> sugarObjectsInsideCircle(double radius, Vector center) {
			return TsInsideCircle(radius, center, Cell.sugarObjectsField);
		}
		
		private final double minimumCellSize;
		private int numberOfHorizontalCells;
		private int numberOfVerticalCells;
		private final double cellWidth;
		private final double cellHeight;
		
		private final Cell[][] cells;
		
		
		private static class Cell {
			private final List<AntObject> antObjects = new LinkedList<AntObject>();
			private static final GameObjectListField<AntObject> antObjectsField = new GameObjectListField<AntObject>() {
				@Override
				public List<AntObject> get(Cell cell) {
					return cell.antObjects;
				}
			};
			
			private final List<HillObject> hillObjects = new LinkedList<HillObject>();
			private static final GameObjectListField<HillObject> hillObjectsField = new GameObjectListField<HillObject>() {
				@Override
				public List<HillObject> get(Cell cell) {
					return cell.hillObjects;
				}
			};
			
			private final List<SugarObject> sugarObjects = new LinkedList<SugarObject>();
			private static final GameObjectListField<SugarObject> sugarObjectsField = new GameObjectListField<SugarObject>() {
				@Override
				public List<SugarObject> get(Cell cell) {
					return cell.sugarObjects;
				}
			};
		}
		
		private static abstract class GameObjectListField<T extends GameObject> {
			public abstract List<T> get(Cell cell);
		}
		
		public final int coordinatesToCellXIndex(Vector p) {
			return Modulo.mod((int) Math.round(Math.floor(p.x/cellWidth)), numberOfHorizontalCells) + 1;
		}
		
		public final int coordinatesToCellYIndex(Vector p) {
			return Modulo.mod((int) Math.round(Math.floor(p.y/cellHeight)), numberOfVerticalCells) + 1;
		}
		
		public final Cell coordinatesToCell(Vector p) {
			return cells[coordinatesToCellXIndex(p)][coordinatesToCellYIndex(p)];
		}
	}
}
