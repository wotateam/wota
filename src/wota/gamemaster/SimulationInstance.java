package wota.gamemaster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;


/**
 * Contains all the information needed for one round of simulation.
 */
public class SimulationInstance {
	private final String[] aiNames;
	private final AILoader aiLoader;
	private GameWorld gameWorld;
	private final long seed;

	private final Parameters parameters;
	
	/**
	 * Create an instance from a seed used to
	 * generate a map and initialize the RNGs.
	 * 
	 * Participating ais are read from the file "settings.txt"
	 * 
	 * Game parameters are read from "parameters.txt"

	 * @param seed
	 *            initial seed of the RNG
	 */
	public SimulationInstance(long seed) {
		this.seed = seed;

		aiLoader = new AILoader("./");
		
		this.aiNames = readAIs("settings.txt");
		parameters = readParameters("parameters.txt");
		
		constructGameWorld();
	}
	
	/**
	 * Create an instance from a randomly choosen seed
	 * used to generate a map and initialize the RNGs.
	 * 
	 * Participating ais are read from the file "settings.txt"
	 * 
	 * Game parameters are read from "parameters.txt"
	 */
	public SimulationInstance() {
		this((new Random()).nextLong());
	}

	/**
	 * creates a Simulation Instance for testing purposes. the
	 * QueenAI is a dummy queen doing nothing for both players and
	 * all Ants are created from the specified AntAI.
	 * 
	 * @param positionFirstPlayer position of the first players hill
	 * @param positionsFirstAI list of positions of the ants of the first ai
	 * @param firstAntAI AntAI of all Ants of the first player
	 */
	public static SimulationInstance createTestSimulationInstance(Vector positionFirstPlayer,
																  Vector positionSecondPlayer,
																  List<Vector> positionsFirstAI, 
																  List<Vector> positionsSecondAI,
																  Class <? extends AntAI> firstAntAI,
																  Class <? extends AntAI> secondAntAI) {
		SimulationInstance simulationInstance = new SimulationInstance(new Random().nextLong());
		
		// overrides the first gameWorld 
		simulationInstance.constructTestGameWorld(positionFirstPlayer, positionSecondPlayer,
								  positionsFirstAI, positionsSecondAI, firstAntAI, secondAntAI);
		return simulationInstance;
	}

	/**
	 * Read AI names from file where ai names are specified after "aiString"
	 * @param filename name of the file. Standard is: setting.txt
	 * @return String array with ai names
	 */
	private static String[] readAIs(String filename) {
		Properties propertiesForSettings = new Properties();
		try {
			propertiesForSettings.load(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println(" while trying to read ai names: " + filename + " not found.");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		String aiString = propertiesForSettings.getProperty("aiString");
		String[] ais = aiString.split(",");
		for (int i=0; i<ais.length; i++) {
			ais[i] = ais[i].trim();
			System.out.println("AI #" + (i+1) + " " + ais[i] );
		}
		return ais;
	}
	
	/**
	 * Read Parameters from file
	 * @param filename name of the file, standard is parameters.txt
	 * @return freshly generated Parameters instance
	 */
	private static Parameters readParameters(String filename) {
		Properties propertiesForParameters = new Properties();
		try {
			propertiesForParameters.load(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("While trying to read parameters: " + filename + " not found.");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return new Parameters(propertiesForParameters);
	}

	/**
	 * Deterministically constructs and populates a GameWorld instance with
	 * hills and resources from the given seed.
	 */
	private void constructGameWorld() {
		gameWorld = new GameWorld(parameters);

		SeededRandomizer.resetSeed(seed);

		for (String aiName : aiNames) {
			GameWorld.Player player = gameWorld.new Player(new Vector(
					SeededRandomizer.getInt(700),
					SeededRandomizer.getInt(700)), aiLoader.loadQueen(aiName));
			gameWorld.addPlayer(player);
		}

		for (int i = 0; i < parameters.N_SUGAR_SOURCES; i++) {
			gameWorld.createRandomSugarObject();
		}
		/*
		// add queens
		for (Player player : world.getPlayers()) {
			player.
		}*/
	}
	
	private void constructTestGameWorld(Vector posFirstPlayer,
										Vector posSecondPlayer,
										List<Vector> positionsFirstAI, 
										List<Vector> positionsSecondAI,
										Class <? extends AntAI> firstAntAI,
										Class <? extends AntAI> secondAntAI) {
		gameWorld = new GameWorld(parameters);
		
		SeededRandomizer.resetSeed(seed);
		
		GameWorld.Player firstPlayer = gameWorld.new Player(posFirstPlayer, aiLoader.loadQueen("donothing"));
		GameWorld.Player secondPlayer = gameWorld.new Player(posSecondPlayer, aiLoader.loadQueen("donothing"));
		gameWorld.addPlayer(firstPlayer);
		gameWorld.addPlayer(secondPlayer);
		
		for (Vector position : positionsFirstAI) {
			firstPlayer.addAntObject(new AntObject(position, Caste.Gatherer, firstAntAI, firstPlayer, parameters));
		}
		for (Vector position : positionsSecondAI) {
			secondPlayer.addAntObject(new AntObject(position, Caste.Gatherer, secondAntAI, secondPlayer, parameters));
		}
	}

	public int getNumPlayers() {
		return aiNames.length;
	}

	public Parameters getParameters() {
		return parameters;
	}
	
	public long getSeed() {
		return seed;
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}
}
