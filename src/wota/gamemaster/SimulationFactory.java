package wota.gamemaster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;


/**
 * Reads Parameters and creates a bunch of Simulation Objects
 */
public class SimulationFactory {
	private final AILoader aiLoader;

	private final Parameters parameters;
	private final SimulationParameters simulationParameters;
	
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
	public SimulationFactory(long seed) {
		SeededRandomizer.resetSeed(seed);

		aiLoader = new AILoader("./");

		simulationParameters = readSimulationParameters("settings.txt");
		parameters = constructParameters("parameters.txt", simulationParameters.AI_PACKAGE_NAMES.length);
	}
	
	/**
	 * Create an instance from a randomly choosen seed
	 * used to generate a map and initialize the RNGs.
	 * 
	 * Participating ais are read from the file "settings.txt"
	 * 
	 * Game parameters are read from "parameters.txt"
	 */
	public SimulationFactory() {
		this((new Random()).nextLong());
	}
	
	/**
	 * creates a Simulation according to the mode specified in the parameters. 
	 * The Simulation itself can have multiple GameWorlds.
	 */
	public Simulation createSimulation() {
		List<GameWorld> gameWorlds = new ArrayList<GameWorld>();
		long seed = SeededRandomizer.getSeed();

		if (!simulationParameters.TOURNAMENT) {
			
			for (int i=0; i<simulationParameters.NUMBER_OF_GAMES; i++) {
				
				boolean swapPlayers = false;
				gameWorlds.add(constructGameWorld(swapPlayers, seed));
				
				if (simulationParameters.HOME_AND_AWAY) {
					swapPlayers = true;
					gameWorlds.add(constructGameWorld(swapPlayers, seed));
				}
				seed = SeededRandomizer.nextLong();
			}
		}
		Simulation simulation = new Simulation(simulationParameters, gameWorlds);
		return simulation;
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
	/*
	public static Simulation createTestSimulation(Vector positionFirstPlayer,
												  Vector positionSecondPlayer,
												  List<Vector> positionsFirstAI, 
												  List<Vector> positionsSecondAI,
												  Class <? extends AntAI> firstAntAI,
												  Class <? extends AntAI> secondAntAI) {
		SimulationFactory simulationFactory = new SimulationFactory(new Random().nextLong());
		
		// overrides the first gameWorld 
		GameWorld gw = simulationFactory.constructTestGameWorld(positionFirstPlayer, positionSecondPlayer,
								  positionsFirstAI, positionsSecondAI, firstAntAI, secondAntAI);
		Simulation simulation = new Simulation();
		return simulation;
	}
	*/

	/**
	 * Read settings including AI names from  a file.
	 * The ai names are specified in "AI_PACKAGE_NAMES".
	 * @param filename name of the file. Standard is: settings.txt
	 * @return String array with ai names
	 */
	private static SimulationParameters readSimulationParameters(String filename) {
		Properties propertiesForSimulationParameters = new Properties();
		try {
			propertiesForSimulationParameters.load(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println(" while trying to read simulation parameters: " + filename + " not found.");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return new SimulationParameters(propertiesForSimulationParameters);
	}
	
	/**
	 * Read Parameters from file
	 * @param filename name of the file, standard is parameters.txt
	 * @return freshly generated Parameters instance
	 */
	private static Parameters constructParameters(String filename, int numberOfPlayers) {
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
		return new Parameters(propertiesForParameters, numberOfPlayers);
	}

	/**
	 * Deterministically constructs and populates a GameWorld instance with
	 * hills and resources from the given seed.
	 * 
	 * @param swapPlayers: reverse the positions of the players = swap for two players
	 */
	private GameWorld constructGameWorld(boolean swapPlayers, long seed) {
		SeededRandomizer.resetSeed(seed);
		
		RandomPosition randomPosition = new RandomPosition(parameters);
		GameWorld gameWorld = new GameWorld(parameters, randomPosition, seed);
		
		List<Vector> hillPositions = new LinkedList<Vector>();
		String aiName;
		for (int i=0; i<simulationParameters.AI_PACKAGE_NAMES.length; i++) {
			
			// choose player name in normal order or reversed for swapPlayers == true
			if (!swapPlayers) {
				aiName = simulationParameters.AI_PACKAGE_NAMES[i];
			}
			else {
				aiName = simulationParameters.AI_PACKAGE_NAMES[simulationParameters.AI_PACKAGE_NAMES.length - i - 1];
			}
			
			Vector hillPosition = randomPosition.hillPosition(hillPositions);
			hillPositions.add(hillPosition);
			GameWorld.Player player = gameWorld.new Player(hillPosition, aiLoader.loadHill(aiName));
			gameWorld.addPlayer(player);
		}

		List<Vector> sugarPositions = new LinkedList<Vector>();
		for (int i = 0; i < simulationParameters.AI_PACKAGE_NAMES.length; i++) {
			for (int j = 0; j < parameters.SUGAR_SOURCES_PER_PLAYER ; j++) {
				Vector sugarPosition = randomPosition.startingSugarPosition(hillPositions.get(i), sugarPositions, hillPositions);
				sugarPositions.add(sugarPosition);
				gameWorld.addSugarObject(new SugarObject(sugarPosition, parameters));
			}
		}
		return gameWorld;
	}
	
	/*
	private GameWorld constructTestGameWorld(Vector posFirstPlayer,
										Vector posSecondPlayer,
										List<Vector> positionsFirstAI, 
										List<Vector> positionsSecondAI,
										Class <? extends AntAI> firstAntAI,
										Class <? extends AntAI> secondAntAI) {
		GameWorld gameWorld = new GameWorld(parameters, new RandomPosition(parameters));
				
		GameWorld.Player firstPlayer = gameWorld.new Player(posFirstPlayer, aiLoader.loadHill("donothing"));
		GameWorld.Player secondPlayer = gameWorld.new Player(posSecondPlayer, aiLoader.loadHill("donothing"));
		gameWorld.addPlayer(firstPlayer);
		gameWorld.addPlayer(secondPlayer);
		
		for (Vector position : positionsFirstAI) {
			firstPlayer.addAntObject(new AntObject(position, Caste.Gatherer, firstAntAI, firstPlayer, parameters));
		}
		for (Vector position : positionsSecondAI) {
			secondPlayer.addAntObject(new AntObject(position, Caste.Gatherer, secondAntAI, secondPlayer, parameters));
		}
		return gameWorld;
	}
	*/
	
	public Parameters getParameters() {
		return parameters;
	}

	public SimulationParameters getSimulationParameters() {
		return simulationParameters;
	}
}
