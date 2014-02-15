package wota.gamemaster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
public class GameWorldFactory {
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
	public GameWorldFactory(long seed, Parameters parameters, SimulationParameters simulationParameters) {
		this.parameters = parameters;
		this.simulationParameters = simulationParameters;
		SeededRandomizer.resetSeed(seed);

		aiLoader = new AILoader("./");
	}
	
	private int numberOfFinishedGames = 0; // not counting reversed positions if HOME_AND_AWAY 
	private boolean swapPlayers = false;
	private	long seed;
	/**
	 * Depending on settings, create the next game world.
	 * 
	 * See the comments in settings.txt for the meaning of the settings.
	 * @return The next game to be played and null if all games have been played. 
	 */
	public GameWorld nextGameWorld() {
		if (!simulationParameters.TOURNAMENT) {
			if (numberOfFinishedGames < simulationParameters.NUMBER_OF_GAMES) {
				if (!swapPlayers) {
					seed = SeededRandomizer.getSeed();
					swapPlayers = true;
					return constructGameWorld(false, seed);
				} else { // swapPlayers
					swapPlayers = false;
					numberOfFinishedGames++;						
					long oldSeed = seed;
					seed = SeededRandomizer.nextLong();
					
					if (simulationParameters.HOME_AND_AWAY) {	
						return constructGameWorld(true, oldSeed);
					}
				}
			} else {
				return null;
			}
		} 
		return null;
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
		GameWorldFactory simulationFactory = new GameWorldFactory(new Random().nextLong());
		
		// overrides the first gameWorld 
		GameWorld gw = simulationFactory.constructTestGameWorld(positionFirstPlayer, positionSecondPlayer,
								  positionsFirstAI, positionsSecondAI, firstAntAI, secondAntAI);
		Simulation simulation = new Simulation();
		return simulation;
	}
	*/

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
			Vector hillPosition = randomPosition.hillPosition(hillPositions);
			hillPositions.add(hillPosition);
		}
		if (swapPlayers) {
			Collections.reverse(hillPositions);
		}
		
		for (int i=0; i<simulationParameters.AI_PACKAGE_NAMES.length; i++) {
			aiName = simulationParameters.AI_PACKAGE_NAMES[i];
			GameWorld.Player player = gameWorld.new Player(hillPositions.get(i), aiLoader.loadHill(aiName));
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
}
