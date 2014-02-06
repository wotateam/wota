package wota.gamemaster;

import java.util.LinkedList;
import java.util.List;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import wota.gameobjects.GameWorld;
import wota.gameobjects.GameWorld.Player;
import wota.graphics.GameView;
import wota.graphics.StatisticsView;
import wota.gameobjects.Parameters;
import wota.utility.SeededRandomizer;


/**
 * Contains the main loop that calls tick() and updates view and
 * statistics. Also handels keyboard.
 * 
 */
public class Simulation {
	private final boolean isGraphical; // only saves typing

	private final int width;
	private final int height;

	private final List<GameWorld> gameWorlds; // list of all gameworlds that should be simulated
	//private GameWorld gameWorld;
	private GameView gameView;
	private StatisticsView statisticsView;
	
	private ResultCollection resultCollection;

	private double framesPerSecond;
	private double ticksPerSecond;
	
	private double measuredFramesPerSecond;
	private double measuredTicksPerSecond;
	
	/** 
	 * reference values for call of tick().
	 * Should be independent of measuring system. 
	 * Otherwise some artifacts pop up.
	 */
	private long referenceTime = System.nanoTime();
	private int referenceTickCount = 0;
	private int referenceFrameCount = 0;
	
	/** time between two measurements of FPS / TPS in seconds */
	private final double MEASUREMENT_INTERVAL = 2.0; 
	
	/** ticks are skipped if delayed by this number */
	private final int SKIP_TICKS_THRESHOLD = 3;
	
	/** time of simulation start in nano seconds. */
	private long startTime; 
	
	private boolean running = false;
	private boolean paused = false;
	private boolean abortRequested = false;
	
	private int frameCount;
	
	/** maximum number of ticks before the game ends regardless of victory condition **/
	private final int maxTicksBeforeEnd;
	
	/**
	 * Creates a runnable simulation from a SimulationInstance with the option
	 * for visualization.
	 * 
	 * @param inst the instance to be simulated
	 * @param isGraphical true, if the simulation should be graphical
	 */
	public Simulation(SimulationParameters simParameters, 
					  List<GameWorld> gameWorlds) {
		isGraphical = simParameters.IS_GRAPHICAL; 
		framesPerSecond = simParameters.FRAMES_PER_SECOND;
		ticksPerSecond = simParameters.INITIAL_TICKS_PER_SECOND;
		maxTicksBeforeEnd = simParameters.MAX_TICKS_BEFORE_END;
		
		width = simParameters.DISPLAY_WIDTH;
		height = simParameters.DISPLAY_HEIGHT;
		
		this.gameWorlds = gameWorlds;
		
		resultCollection = new ResultCollection();
	}
	
	/**
	 * Start the simulation and keep the view up to date.
	 * 1. Update the Graphics at the rate framesPerSecond
	 * 2. Update the Simulation at the rate ticksPerSecond 
	 * do nothing in the remaining time or if times get in conflict only update the graphics.
	 * 
	 * keyboard/mouse input should be fetched before graphics in every loop. 
	 */
	public void runSimulation() {

		for (int iGW=0; iGW<gameWorlds.size() && !abortRequested; iGW++) {
			GameWorld gameWorld = gameWorlds.get(iGW);
			
			SeededRandomizer.resetSeed(gameWorld.seed);
			System.out.println("seed next game: " + gameWorld.seed);			
			
			StatisticsLogger logger = new StatisticsLogger(gameWorld.getPlayers());
			gameWorld.setLogger(logger);
			
			statisticsView = new StatisticsView(gameWorld, logger);
	        statisticsView.run();
	        
	        if (isGraphical) {
	        	try {
					Display.setDisplayMode(new DisplayMode(width, height));
					Display.create(new PixelFormat(8,0,0,0));
				} catch (LWJGLException e) {
					e.printStackTrace();
					System.exit(0);
				}
	        	
	        	createKeyboard();
				gameView = new GameView(gameWorld, width, height);
				gameView.setup();
	        }

			running = true;
			startTime = System.nanoTime();
	        
			resetReferenceValues();
			long lastMeasurementTime = System.nanoTime(); // time for TPS / FPS measurements
			int measureFrameCount = 0; // Frame counter to determine TPS
			int measureTickCount = 0; // Tick counter to determine FPS
			
			// events for graphics update and tick are created uniformly. Call them with priority on graphics
			while (running && !abortRequested) { 			
				if (isGraphical) {
					handleKeyboardInputs();
				}
				
				if (ticksToDo() > SKIP_TICKS_THRESHOLD && isGraphical) {
					resetReferenceValues();
				}
				
				// now update simulation if tick event 
				if (ticksToDo() > 0 && !paused) {
					tick(gameWorld);
					measureTickCount++;
					referenceTickCount++;
				}
					
				// Update Graphics if event for graphic update is swept.
				if (framesToDo() > 0) {
					statisticsView.refresh();
					frameCount++;
					measureFrameCount++;
					referenceFrameCount++;

					if (isGraphical && !paused) { // calc graphics
						gameView.render();						
						abortRequested = Display.isCloseRequested();
						Display.update(); // must be called in any case to catch keyboard input
					}
				}

				// measurements of FPS / TPS 
				long timeDiff = System.nanoTime() - lastMeasurementTime;
				if (timeDiff > 1.e9*MEASUREMENT_INTERVAL) {
					measuredFramesPerSecond = measureFrameCount * 1.e9 / timeDiff;
					measuredTicksPerSecond = measureTickCount * 1.e9 / timeDiff;
					
					measureFrameCount = 0;
					measureTickCount = 0;
					
					lastMeasurementTime = System.nanoTime();
					
					System.out.format("Frames per second: %.1f\n",
						measuredFramesPerSecond);
					System.out.format("Ticks per second: %.1f\n",
							measuredTicksPerSecond);
				}
			}
			
			// game done. Add to stats.
			Player winner = gameWorld.getWinner();
			if (winner == null) {
				resultCollection.addGame(null, getNames(gameWorld.getPlayers()), null);
			}
			else {
				List<Player> active = new java.util.ArrayList<Player>(gameWorld.getPlayers());
				active.remove(winner);
				resultCollection.addGame(new String[] {winner.name}, null, getNames(active));
			}
			
			System.out.println("seed last game: " + gameWorld.seed);

			statisticsView.frame.dispose();
			if (isGraphical) {
				Display.destroy();
			}
		} // last gameWorld done
		System.out.println(resultCollection);
	}
	
	/**
	 * Advance the game world by one tick and check for victory / end after fixed number of ticks.
	 */
	private void tick(GameWorld gameWorld) {
		gameWorld.tick();

		GameWorld.Player winner = gameWorld.getWinner();
		running = false; // set to true if none of the victory conditions actually apply
		String gameOverMessage = "";
		if (winner != null) {
			gameOverMessage = winner + " has won the game in tick "
					+ gameWorld.tickCount();
		} 
		else if (gameWorld.allPlayersDead()) {
			gameOverMessage = "Draw! Nobody has won the game after " + gameWorld.tickCount() + " ticks.";
		}
		// End the game after fixed number of ticks - players with most ants win.
		else if (gameWorld.tickCount() >= maxTicksBeforeEnd) {
			List<GameWorld.Player> winners = gameWorld.getPlayersWithMostAnts();
			if (winners.size() == gameWorld.getPlayers().size()) {
				gameOverMessage = "Draw! Game was stopped after " + gameWorld.tickCount() + " ticks. All players have the same number of ants.";
			}
			else {
				gameOverMessage = "Game was stopped after " + gameWorld.tickCount() + " ticks. The following player(s) have won:";
				for (GameWorld.Player aWinner : winners) {
					gameOverMessage += aWinner;
				}
			}
		}
		else {
			running = true;
		}
		if (running == false) {
			System.out.println(statisticsView);
			System.out.println(gameOverMessage);
		}
		
	}
	
	private static void createKeyboard() {
		try {
			Keyboard.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void handleKeyboardInputs() {
		
		// Kedboard.poll() checks for keyboard input, buffered
		Keyboard.poll();
		
		while (Keyboard.next()) {
			// only consider KeyDown Events
			if ( !Keyboard.getEventKeyState()) {
				continue;
			}
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_ESCAPE:
				running = false;
				break;
			case Keyboard.KEY_S:
				gameView.drawSightRange = !gameView.drawSightRange;
				break;
			case Keyboard.KEY_M:
				gameView.drawMessages = !gameView.drawMessages;
				break;
			case Keyboard.KEY_PERIOD:
				ticksPerSecond *= 1.3;
				resetReferenceValues();
				break;
			case Keyboard.KEY_COMMA:
				ticksPerSecond /= 1.3;
				resetReferenceValues();
				break;
			case Keyboard.KEY_P:
				paused = !paused;
				break;
			}
		}
	}
	
	private int framesToDo() {
		return (int)((System.nanoTime() - referenceTime) / 1.e9 * framesPerSecond) -
		  referenceFrameCount + 1;
	}
	
	private int ticksToDo() {
		return (int)((System.nanoTime() - referenceTime) / 1.e9 * ticksPerSecond) -
		referenceTickCount + 1;
	}
	
	private void resetReferenceValues() {
		referenceTime = System.nanoTime();
		referenceTickCount = 0;
		referenceFrameCount = 0;
	}
	
	private static String[] getNames(List<Player> player) {
		String[] playerNames = new String[player.size()];
		for (int iActive=0; iActive<player.size(); iActive++) {
			playerNames[iActive] = player.get(iActive).name;
		}
		return playerNames;
	}
}
