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
		List<String> ais = new LinkedList<String>();
		
//		use ais.add to add AIs to the game
		ais.add("dummy.DummyQueenAI");
		ais.add("solitary.SolitaryQueenAI");
		ais.add("AggressiveQueenAI");
		ais.add("AggressiveQueenAI");
	
//		use this constructor to obtain exactly the same game run.
//		long specialSeed = 42;
//		SimulationInstance inst = new SimulationInstance(ais, specialSeed);
		
//		use this constructor to obtain different games each run.
		SimulationInstance inst = new SimulationInstance(ais);
		
		Simulation sim = new Simulation(inst, true);
		sim.runSimulation();
	}
}
