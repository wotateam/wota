package wota.gamemaster;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import wota.gameobjects.GameWorld;
import wota.gameobjects.GameWorld.Player;
import wota.graphics.GameView;
import wota.graphics.StatisticsView;


/**
 * Contains the main loop that calls tick() and updates view and
 * statistics. Also handels keyboard.
 * 
 */
public class Simulation {
	private final boolean isGraphical; // only saves typing

	private final int width;
	private final int height;

	private final GameWorldFactory gameWorldFactory;
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
		
	public Simulation(SimulationParameters simulationParameters, 
					  GameWorldFactory gameWorldFactory) {
		isGraphical = simulationParameters.IS_GRAPHICAL; 
		framesPerSecond = simulationParameters.FRAMES_PER_SECOND;
		ticksPerSecond = simulationParameters.INITIAL_TICKS_PER_SECOND;
		
		width = simulationParameters.DISPLAY_WIDTH;
		height = simulationParameters.DISPLAY_HEIGHT;
		
		this.gameWorldFactory = gameWorldFactory;
		
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
		GameWorld gameWorld;
		
		while (!abortRequested) {
			gameWorld = gameWorldFactory.nextGameWorld();
			if (gameWorld == null) {
				break;
			}
			
			System.out.println("seed next game: " + gameWorld.seed);			
			
			StatisticsLogger logger = new StatisticsLogger(gameWorld.getPlayers());
			gameWorld.setLogger(logger);
			
			//lazy initialization
			if (statisticsView == null)
				statisticsView = new StatisticsView(gameWorld, logger);
			else
				statisticsView.setGameWorld(gameWorld, logger);
	        statisticsView.run();
	        
	        if (isGraphical) {
	        	try {
	        		Display.setTitle("Wota");
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
				
				// only check if is not graphical since we want the
				// maximal number of updates possible
				if (ticksToDo() > SKIP_TICKS_THRESHOLD && isGraphical) {
					resetReferenceValues();
				}
				
				// now update simulation if tick event 
				if ((!isGraphical || ticksToDo() > 0) && !paused) {
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

					if (isGraphical) { // calc graphics
						if (!paused) {
							gameView.render();						
						}
						 // must be called in any case to catch keyboard input
						abortRequested = Display.isCloseRequested();
						Display.update();
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
			List<Player> winner = gameWorld.getWinner();
			List<Player> loosers = new ArrayList<Player>(gameWorld.getPlayers());
			loosers.removeAll(winner);
			
			if (winner.size() == 0) {
				System.err.println("winner should not be empty.");
			}
			else if (winner.size() == 1) {
				resultCollection.addGame(getNames(winner), null, getNames(loosers));
			}
			else {
				// multiple winners count as draw. But we could still have loosers. 
				resultCollection.addGame(null, getNames(winner), getNames(loosers));
			}
			
			System.out.println("seed last game: " + gameWorld.seed);

			statisticsView.destroyContents();
			if (isGraphical) {
				Display.destroy();
			}
		} // last gameWorld done
		System.out.println(resultCollection);
	}
	
	/**
	 * Advance the game world by one tick.
	 * Ask gameworld for winners and print statistics.
	 */
	private void tick(GameWorld gameWorld) {
		gameWorld.tick();
		
		String gameOverMessage = "";
		
		List<Player> winner = gameWorld.getWinner();
		if (winner.size() == 1) {
			if (gameWorld.tickCount() >= gameWorld.parameters.MAX_TICKS_BEFORE_END) {
				gameOverMessage = "The game was stopped since the maximum number of ticks is reached.\n";
			}
			gameOverMessage = winner.get(0) + " has won the game in tick "
					+ gameWorld.tickCount();
		}
		else if (winner.size() > 1) {
			for (GameWorld.Player aWinner : winner) {
				gameOverMessage += aWinner + " ";
			}
			gameOverMessage += "have won the game in tick " + gameWorld.tickCount();
		}
		running = (winner.size() == 0);

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
				gameView.setDrawSightRange(!gameView.isDrawSightRange());
				break;
			case Keyboard.KEY_M:
				gameView.setDrawMessages(!gameView.isDrawMessages());
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

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the frameCount
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * @param frameCount the frameCount to set
	 */
	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

}
