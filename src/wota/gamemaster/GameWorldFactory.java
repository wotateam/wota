package wota.gamemaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.GameWorld;
import wota.gameobjects.Parameters;
import wota.gameobjects.SugarObject;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;


/**
 * Reads Parameters and creates a bunch of Simulation Objects
 */
public class GameWorldFactory {
	private final AILoader aiLoader;

	private final Parameters parameters;
	private final SimulationParameters simulationParameters;
	
	private final List<int[]> matchings;
	
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
		this.seed = seed;

		aiLoader = new AILoader("./");
		
		int n = simulationParameters.AI_PACKAGE_NAMES.length;
		if (simulationParameters.TOURNAMENT) {
			matchings = generateSubsets(2, n);
		}
		else {
			matchings = generateSubsets(n, n);
		}
	}
	
	/** index of current match. Starts at 0 in every round. */
	private int iMatch = 0;
	/** not counting reversed positions if HOME_AND_AWAY */
	private int numberOfFinishedRounds = 0; 
	/** indicates if the home game has been played already */
	private boolean playedHome = false;
	private	long seed;
	/**
	 * Depending on settings, create the next game world.
	 * 
	 * See the comments in settings.txt for the meaning of the settings.
	 * @return The next game to be played and null if all games have been played. 
	 */
	public GameWorld nextGameWorld() {
		GameWorld gw = null;
		if (iMatch == matchings.size()) {
			iMatch = 0; // start over again
			numberOfFinishedRounds++;
		}
		if (numberOfFinishedRounds < simulationParameters.NUMBER_OF_ROUNDS) {
			if (!playedHome) {
				//seed = SeededRandomizer.getSeed();
				playedHome = true;
				gw =  constructGameWorld(matchings.get(iMatch),false, seed);
			} else { // swapPlayers
				playedHome = false;
				long oldSeed = seed;
				seed = SeededRandomizer.nextLong();
				
				if (simulationParameters.HOME_AND_AWAY) {	
					gw = constructGameWorld(matchings.get(iMatch),true, oldSeed);
					iMatch++;
				}
				else {
					iMatch++;
					return nextGameWorld(); 
				}
			}
		} else { // no more games to play
			return null;
		}
		return gw;
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
	 * @param iActivePlayers: indices of active players in AI_PACKAGE_NAMES
	 * @param swapPlayers: reverse the positions of the players = swap for two players
	 */
	private GameWorld constructGameWorld(int[] iActivePlayers, boolean swapPlayers, long seed) {
		SeededRandomizer.resetSeed(seed);
		
		RandomPosition randomPosition = new RandomPosition(parameters);
		GameWorld gameWorld = new GameWorld(parameters, randomPosition, seed);
		
		List<Vector> hillPositions = new LinkedList<Vector>();
		String aiName;
		
		for (int i=0; i<iActivePlayers.length; i++) {
			Vector hillPosition = randomPosition.hillPosition(hillPositions);
			hillPositions.add(hillPosition);
		}
		if (swapPlayers) {
			Collections.reverse(hillPositions);
		}
		
		for (int i=0; i<iActivePlayers.length; i++) {
			aiName = simulationParameters.AI_PACKAGE_NAMES[iActivePlayers[i]];
			GameWorld.Player player = gameWorld.new Player(hillPositions.get(i), aiLoader.loadHill(aiName));
			gameWorld.addPlayer(player);
		}

		if (swapPlayers) {
			Collections.reverse(hillPositions); // need to reverse again for sugar positions. 
			// we can't just swap players because of their colors.
		}
		
		List<Vector> sugarPositions = new LinkedList<Vector>();
		for (int i = 0; i < hillPositions.size(); i++) {
			for (int j = 0; j < parameters.SUGAR_SOURCES_PER_PLAYER ; j++) {
				Vector sugarPosition = randomPosition.startingSugarPosition(hillPositions.get(i), sugarPositions, hillPositions);
				sugarPositions.add(sugarPosition);
				gameWorld.addSugarObject(new SugarObject(sugarPosition, parameters));
			}
		}
		return gameWorld;
	}
	
	/**
	 * returns a List with all k element subsets of {0, 1, ..., n}. 
	 * e.g. n = 3, k = 2 a possible output is (up to permutation):
	 * {{0, 1}, {0, 2}, {1, 2}}.
	 * 
	 * Only implemented for k = n and k = 2 !
	 */
	private static List<int[]> generateSubsets(int k, int n) {
		if (k != n && k != 2) {
			System.err.println("generateSubsets is only implemented for k = 2 or k = n.");
			return null;	
		}
		ArrayList<int[]> list = new ArrayList<int[]>();
		if (k == n) {
			int[] all = new int[n];
			for (int i=0; i<n; i++) {
				all[i] = i;
			}
			list.add(all);
		}
		if (k == 2 && k != n) {
			int[] sub = new int[k];
			for (int i=0; i<n; i++) {
				sub[0] = i;
				for (int j=i+1; j<n; j++) {
					sub[1] = j;
					list.add(sub.clone());
				}
			}
		}
		return list;
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
