/**
 * 
 */
package de.wota;

import java.util.LinkedList;
import java.util.List;
import de.wota.gamemaster.*;
import de.wota.utility.Vector;

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
															   de.wota.ai.bvb.Mao.class,
															   de.wota.ai.bvb.Mao.class);
		*/
		
		Simulation sim = new Simulation(inst, true);
		sim.runSimulation();
		System.out.println("seed: " + inst.getSeed() + "l");
	}
}
