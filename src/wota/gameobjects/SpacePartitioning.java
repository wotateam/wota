package wota.gameobjects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wota.utility.Modulo;
import wota.utility.Vector;


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
public class SpacePartitioning {
	public final Parameters parameters;
	
	public SpacePartitioning(double minimumCellSize, Parameters parameters) {
		this.parameters = parameters;
		this.minimumCellSize = minimumCellSize;
		numberOfHorizontalCells = (int) Math.round(Math.floor(parameters.SIZE_X/minimumCellSize));
		numberOfVerticalCells = (int) Math.round(Math.floor(parameters.SIZE_Y/minimumCellSize));
		cellWidth = parameters.SIZE_X / numberOfHorizontalCells;
		cellHeight = parameters.SIZE_Y / numberOfVerticalCells;
		
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
	}
	
	public void addAntObject(AntObject antObject) {
		addT(antObject, Cell.antObjectsField);
	}
	
	public void addAntCorpseObject(AntCorpseObject antCorpseObject) {
		addT(antCorpseObject, Cell.antCorpseObjectsField);
	}
	
	public void addHillObject(HillObject hillObject) {
		addT(hillObject, Cell.hillObjectsField);
	}

	public void addSugarObject(SugarObject sugarObject) {
		addT(sugarObject, Cell.sugarObjectsField);
	}
	
	public void addAntMessage(AntMessage antMessage) {
		addT(antMessage, Cell.antMessagesField);
	}	
	
	public void addHillMessage(HillMessage hillMessage) {
		addT(hillMessage, Cell.hillMessagesField);
	}
	
	private <T extends GameObject> void addT(T t, GameObjectListField<T> field) {
		field.get(coordinatesToCell(t.getPosition())).add(t);
	}
	
	// WARNING: The methods for removing objects depend on the object being in the correct cell.
	public void removeAntObject(AntObject antObject) {
		removeT(antObject, Cell.antObjectsField);
	}
	
	public void removeAntCorpseObject(AntCorpseObject antCorpseObject) {
		removeT(antCorpseObject, Cell.antCorpseObjectsField);
	}
	
	public void removeHillObject(HillObject hillObject) {
		removeT(hillObject, Cell.hillObjectsField);
	}

	public void removeSugarObject(SugarObject sugarObject) {
		removeT(sugarObject, Cell.sugarObjectsField);
	}
	
	private <T extends GameObject> void removeT(T t, GameObjectListField<T> field) {
		field.get(coordinatesToCell(t.getPosition())).remove(t);
	}
	
	public void update() {
		for (int i = 1; i < numberOfHorizontalCells + 1; i++) {
			for (int j = 1; j < numberOfVerticalCells + 1; j++) {
				Cell cell = cells[i][j];
				update(cell, i, j, Cell.antObjectsField);
			}
		}
	}
	
	public void discardAntMessages() {
		for (int i = 1; i < numberOfHorizontalCells + 1; i++) {
			for (int j = 1; j < numberOfVerticalCells + 1; j++) {
				Cell cell = cells[i][j];
				cell.antMessages.clear();
			}
		}
	}
	
	public void discardHillMessages() {
		for (int i = 1; i < numberOfHorizontalCells + 1; i++) {
			for (int j = 1; j < numberOfVerticalCells + 1; j++) {
				Cell cell = cells[i][j];
				cell.hillMessages.clear();
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
				if (parameters.distance(t.getPosition(), center) < radius) {
					listOfTsInsideCircle.add(t);
				}
			}
		}
		
		return listOfTsInsideCircle;
	}
	
	public List<AntObject> antObjectsInsideCircle(double radius, Vector center) {
		return TsInsideCircle(radius, center, Cell.antObjectsField);
	}
	
	public List<AntCorpseObject> antCorpseObjectsInsideCircle(double radius, Vector center) {
		return TsInsideCircle(radius, center, Cell.antCorpseObjectsField);
	}
	
	public List<HillObject> hillObjectsInsideCircle(double radius, Vector center) {
		return TsInsideCircle(radius, center, Cell.hillObjectsField);
	}
	
	public List<SugarObject> sugarObjectsInsideCircle(double radius, Vector center) {
		return TsInsideCircle(radius, center, Cell.sugarObjectsField);
	}
	
	public List<AntMessage> antMessagesInsideCircle(double radius, Vector center) {
		return TsInsideCircle(radius, center, Cell.antMessagesField);
	}
	
	public List<HillMessage> hillMessagesInsideCircle(double radius, Vector center) {
		return TsInsideCircle(radius, center, Cell.hillMessagesField);
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
	
	private final double minimumCellSize;
	private int numberOfHorizontalCells;
	private int numberOfVerticalCells;
	private final double cellWidth;
	private final double cellHeight;
	
	private final Cell[][] cells;
	
	
	private static class Cell {
		public final List<AntObject> antObjects = new LinkedList<AntObject>();
		private static final GameObjectListField<AntObject> antObjectsField = new GameObjectListField<AntObject>() {
			@Override
			public List<AntObject> get(Cell cell) {
				return cell.antObjects;
			}
		};
		
		public final List<AntCorpseObject> antCorpseObjects = new LinkedList<AntCorpseObject>();
		private static final GameObjectListField<AntCorpseObject> antCorpseObjectsField = new GameObjectListField<AntCorpseObject>() {
			@Override
			public List<AntCorpseObject> get(Cell cell) {
				return cell.antCorpseObjects;
			}
		};
		
		public final List<HillObject> hillObjects = new LinkedList<HillObject>();
		private static final GameObjectListField<HillObject> hillObjectsField = new GameObjectListField<HillObject>() {
			@Override
			public List<HillObject> get(Cell cell) {
				return cell.hillObjects;
			}
		};
		
		public final List<SugarObject> sugarObjects = new LinkedList<SugarObject>();
		private static final GameObjectListField<SugarObject> sugarObjectsField = new GameObjectListField<SugarObject>() {
			@Override
			public List<SugarObject> get(Cell cell) {
				return cell.sugarObjects;
			}
		};
		
		public final List<AntMessage> antMessages = new LinkedList<AntMessage>();
		private static final GameObjectListField<AntMessage> antMessagesField = new GameObjectListField<AntMessage>() {
			@Override
			public List<AntMessage> get(Cell cell) {
				return cell.antMessages;
			}
		};
		
		public final List<HillMessage> hillMessages = new LinkedList<HillMessage>();
		private static final GameObjectListField<HillMessage> hillMessagesField = new GameObjectListField<HillMessage>() {
			@Override
			public List<HillMessage> get(Cell cell) {
				return cell.hillMessages;
			}
		};
	}
	
	public int totalNumberOfAntObjects() {
		int n = 0;
		for (int i=0; i<numberOfHorizontalCells; i++) {
			for (int j=0; j<numberOfVerticalCells; j++) {
				n += cells[i][j].antObjects.size();
			}
		}
		return n;
	}

	static abstract class GameObjectListField<T extends GameObject> {
		public abstract List<T> get(Cell cell);
	}
}
