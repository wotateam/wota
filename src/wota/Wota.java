/**
 * 
 */
package wota;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.SwingUtilities;

import wota.gamemaster.*;
import wota.gameobjects.Parameters;
import wota.graphics.StatisticsView;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Class used to start the game.
 */
public class Wota {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		
		String settingsFile = "settings.txt";
		if (args.length >= 1) {
			settingsFile = args[0];
		}
		
		String parametersFile = "parameters.txt";
		if (args.length >= 2) {
			parametersFile = args[1];
		}
		
		if (args.length >= 3) {
			System.err.println("too many inputs.\n expected input: [settings file] [parameters file]");
			return;
		}
		
		SimulationParameters simulationParameters = readSimulationParameters(settingsFile);
		Parameters parameters = constructParameters(parametersFile,
				 simulationParameters.TOURNAMENT ? 2 : simulationParameters.AI_PACKAGE_NAMES.length);
		
//		use this constructor to obtain exactly the same game run.
//		long specialSeed = 42;
//		GameWorldFactory factory = new GameWorldFactory(specialSeed);


//		use this constructor to obtain different games each run.
		GameWorldFactory gameWorldFactory = new GameWorldFactory((new Random()).nextLong(), parameters, simulationParameters);
		
//		use this for debugging
		/*
		Vector hillPositionPlayer1 = new Vector(100,100);
		Vector hillPositionPlayer2 = new Vector(600,600);
		Vector antPosition1 = new Vector(123,456);
		Vector antPosition2 = new Vector(234,567);
		LinkedList<Vector> positions1 = new LinkedList<Vector>();
		positions1.add(antPosition1);
		positions1.add(antPosition2);
		LinkedList<Vector> positions2 = new LinkedList<Vector>();
		Vector antPosition3 = new Vector(384,648);
		positions2.add(antPosition3);
		SimulationInstance inst = SimulationInstance.createTestSimulationInstance(hillPositionPlayer1,
															   hillPositionPlayer2,
															   positions1,
															   positions2,
															   wota.ai.bvb.Mao.class,
															   wota.ai.bvb.Mao.class);
		*/
		
		Simulation simulation = new Simulation(simulationParameters, gameWorldFactory);
		simulation.runSimulation();
	}
	

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
}
