/**
 * 
 */
package de.wota.gamemaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Class used to start the game.
 */
public class Wota {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		
//		use this constructor to obtain exactly the same game run.
//		long specialSeed = 42;
//		SimulationInstance inst = new SimulationInstance(specialSeed);
		
//		use this constructor to obtain different games each run.
		SimulationInstance inst = new SimulationInstance();
		
		Simulation sim = new Simulation(inst, true);
		sim.runSimulation();
	}
}
