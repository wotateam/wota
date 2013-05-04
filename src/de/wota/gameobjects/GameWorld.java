package de.wota.gameobjects;

import java.util.Random;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.wota.Message;
import de.wota.GameWorldParameters;
import de.wota.Vector;
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

	public void tick() {
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

				// TODO pass visibleAnts and visibleSugar and messages
				antObject.tick(visibleAnts, visibleSugar, audibleMessages);
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
		
		// Let ants die!
		for (Player player : players) {
			//LinkedList<AntObject> antObjectsToDie = new LinkedList<AntObject>();

			for (Iterator<AntObject> antObjectIter = player.antObjects.iterator(); antObjectIter.hasNext();) {
				if (antObjectIter.next().isDying()) {
					antObjectIter.remove();
				}
			}
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
	private void executeAction(AntObject actor) {
		Action action = actor.getAction();
		
		// TODO remove this test ----------------------
		Random random = new Random();
		actor.takesDamage(3*random.nextDouble());
		// --------------------------------------------
		
		// Attack
		// TODO add collateral damage
		Ant targetAnt = action.getAttackTarget();
		if (targetAnt != null) {
			// TODO check if target is in range.
			AntObject target = targetAnt.antObject;
			target.takesDamage(actor.getAttack());
		}
		
		// Movement
		actor.move(Vector.fromPolar(action.getMovementDistance(), action.getMovementDirection()));
		
		// Messages
		if (action.getMessage() != null)
			messages.add(action.getMessage());
	}
	
	/**
	 * Assumes that p1 and p2 are in the fundamental region.
	 * @param p1
	 * @param p2
	 * @return The shortest vector from p1 to a point equivalent to p2.
	 */
	public static Vector shortestDifferenceOnTorus(Vector p1, Vector p2) {
		Vector d = Vector.add(p1, Vector.scale(-1,p2));
		if (d.x > GameWorldParameters.SIZE_X) {
			d.x = GameWorldParameters.SIZE_X - d.x; 
		}
		if (d.y > GameWorldParameters.SIZE_Y) {
			d.y = GameWorldParameters.SIZE_Y - d.y; 
		}
		return d;
	}
	
	public class SpacePartioning {
		// Relative coordinates of visible cells, i.e. the cell itself and the adjacent ones.
		private final int numberOfVisibleCells = 9;
		private final int[] deltaX = {0, 1, 1, 0, -1, -1, -1,  0,  1};
		private final int[] deltaY = {0, 0, 1, 1,  1,  0, -1, -1, -1};
		
		public List<AntObject> antObjectsWithDistanceLessThanTo(double r, Vector p) {
			List<AntObject> listOfAntObjectsWithDistanceLessRThanToP = new LinkedList<AntObject>();
			int x = coordinatesToCellXIndex(p);
			int y = coordinatesToCellYIndex(p);
			
			for (int i = 0; i < numberOfVisibleCells; i++) {
				for (AntObject antObject : cells[x+deltaX[i]][y+deltaY[i]].antObjects) {
					if (shortestDifferenceOnTorus(antObject.getPosition(),p).length() < r) {
						listOfAntObjectsWithDistanceLessRThanToP.add(antObject);
					}
				}
			}
			
			return listOfAntObjectsWithDistanceLessRThanToP;
		}
		
		private final double width;
		private final double height;
		private final double minimumCellSize;
		private int numberOfHorizontalCells;
		private int numberOfVerticalCells;
		private final double cellWidth;
		private final double cellHeight;
		
		private final Cell[][] cells;
		
		public class Cell {
			private final List<AntObject> antObjects = new LinkedList<AntObject>();
			private final List<HillObject> hillObjects = new LinkedList<HillObject>();
			private final List<Sugar> sugarObjects = new LinkedList<Sugar>();
		}
		
		public SpacePartioning(double width, double height, double minimumCellSize) {
			this.width = width;
			this.height = height;
			this.minimumCellSize = minimumCellSize;
			numberOfHorizontalCells = (int) Math.round(Math.floor(width/minimumCellSize));
			numberOfVerticalCells = (int) Math.round(Math.floor(height/minimumCellSize));
			cellWidth = width / numberOfHorizontalCells;
			cellHeight = height / numberOfVerticalCells;
			
			// Periodicity is implemented having the zeroth row point to the same cells as  
			// the second to last and the last point to the same cells as the first.
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
			
			
		}
		
		private final int mod(int x, int m) {
			int r = x % m;
			if (r < 0) {
				return r + m;
			} else {
				return r;
			}
		}
		
		public final int coordinatesToCellXIndex(Vector p) {
			return mod((int) Math.round(Math.floor(p.x/cellWidth)), numberOfHorizontalCells) + 1;
		}
		
		public final int coordinatesToCellYIndex(Vector p) {
			return mod((int) Math.round(Math.floor(p.y/cellHeight)), numberOfVerticalCells) + 1;
		}
		
		public final Cell coordinatesToCell(Vector p) {
			return cells[coordinatesToCellXIndex(p)][coordinatesToCellYIndex(p)];
		}
	}
}
