/**
 * 
 */
package wota.ai.pwahs05; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;

import java.util.LinkedList;


/**
 * Put a description of you AI here.
 */
// Here, you may use spaces, etc., unlike in the package path wota.ai.YOUR_AI_NAME:
@AIInformation(creator = "Anonymous", name = "Anonymous's AI")
public class ScoutAI extends TalkingAntAI {
	/* 
	 * tick() gets called in every step of the game.
	 * You have to call methods of AntAI to specify
	 * the desired action.
	 * 
 	 * you have access to
	 * the Ants you see: 								visibleAnts
	 * the sources of sugar you see: 					visibleSugar
	 * the Hills you see: 								visibleHills
	 * 
	 * you can move using one of the methods starting
	 * with move. For example to move ahead (in the
	 * direction of the last tick) call					moveAhead()
	 * 
	 * to attack other ants use methods starting with	attack(otherAnt)
	 * attack, e.g.		
	 * 
	 * if you want a List containing only the hostile	
	 * ants you can see, call							visibleEnemies()
	 * 
	 * communication is possible with					talk(content)
	 * where content is an integer value with is
	 * contained in the message
	 * 
	 * To measure the distance between two objects
	 * (you must be able to see both of them), call		vectorBetween(start, end).length()
	 * 
	 * to get information about yourself, for example
	 * your health points								self.health
	 * 
	 * to obtain random numbers use	SeededRandomizer
	 * e.g. a random elment of {0,1,2}					SeededRandomizer.getInt(3)
	 * 
	 * to iterate over a list (e.g. visibleAnts) use	for (Ant ant : visibleAnts) {
	 * 														// ant is an element of visibleAnts
	 * 													}
	 * 
	 * A full list of possible actions and how to get information is available at 
	 * doc/de/wota/gameobjects/AntAI.html
	 */
	
	boolean fight = false;
	boolean run = false;
	
	double rundir = 0;
	
	@Override
	public void first_cry(){
		if (time == 1){		//was just born, figure out time and directions:
			
			//find time:
			if (audibleHillMessage != null){
				if (audibleHillMessage.content < 0) {
					initialTime = audibleHillMessage.content - HillAI.initTime;
				}
			}
			
			dir = Math.asin(self.caste.SIGHT_RANGE / parameters.SIZE_X) * 360 / Math.PI
					+ (self.id * 90) % 360;
			
			//initialize hills:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills.add(new LinkedList<Snapshot>());
				indices[i] = 0;
			}
			
			//add my hill:
			for(Hill h: visibleHills){
				if (h.playerID == self.playerID){
					hills.get(HillAI.HILL_IND).add(visibleHills.get(0));					
				}
			}
			nr_hill = 0;		
		}
	}
	
	
}
