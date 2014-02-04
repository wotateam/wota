package wota.testing;

import java.util.LinkedList;

import wota.gamemaster.Simulation;
import wota.gamemaster.SimulationFactory;
import wota.utility.Vector;


/*
 * Run in the debugger and observe the health of the ants. Suggestion: Breakpoint in GameWorld.tick.
 * Player 1's ants 1 and 2 should die at the same time, the third should not die at all and have 
 * two-thirds of its health remaining.
 */
public class CollateralDamage {

	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		Vector hillPositionPlayer1 = new Vector(0,0);
		Vector hillPositionPlayer2 = new Vector(0,0);
		Vector antPosition1 = new Vector(100,100);
		Vector antPosition2 = new Vector(100,100);
		Vector antPosition3 = new Vector(105,100);
		LinkedList<Vector> positions1 = new LinkedList<Vector>();
		positions1.add(antPosition1);
		positions1.add(antPosition2);
		positions1.add(antPosition3);
		LinkedList<Vector> positions2 = new LinkedList<Vector>();
		Vector attackerPosition = new Vector(100,115);
		positions2.add(attackerPosition);
		SimulationFactory inst = SimulationFactory.createTestSimulation(hillPositionPlayer1,
															   hillPositionPlayer2,
															   positions1,
															   positions2,
															   wota.ai.donothing.DoNothingAI.class,
															   wota.testing.SitAndHitAI.class);
		
		Simulation sim = new Simulation(inst);
		sim.runSimulation();
		System.out.println("seed: " + inst.getSeed() + "l");
	}
	*/

}
