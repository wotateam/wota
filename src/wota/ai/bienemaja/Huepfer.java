/**
 * 
 */
package wota.ai.bienemaja; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Put a description of you AI here.
 */
public class Huepfer extends MyAntAI {

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
	 * e.g. a random elment of {0,1,2}					random.getInt(3)
	 * 
	 * to iterate over a list (e.g. visibleAnts) use	for (Ant ant : visibleAnts) {
	 * 														// ant is an element of visibleAnts
	 * 													}
	 * 
	 * A full list of possible actions and how to get information is available at 
	 * doc/wota/gameobjects/AntAI.html
	 */
	@Override
	public void tick() throws Exception {
		// sample AI which moves with at constant angle 27 degrees until it finds some sugar source
		for(Ant ant: visibleAnts){
			if(ant.caste==Caste.Scout && ant.playerID==self.playerID){
				initialised=false;
			}
		}
		dowhatcanbedone();
		
		if(initialised==false){
			initialised=true;
			if(random.nextInt(2)==0){
				if(random.nextInt(2)==0){
					if(random.nextInt(2)==0){
						v.y=(2*self.caste.SIGHT_RANGE-1.);
						v.x=parameters.SIZE_X;
					}else{
						v.y=-(2*self.caste.SIGHT_RANGE-1.);
						v.x=parameters.SIZE_X;
					}
				}else{
					if(random.nextInt(2)==0){
						v.y=(2*self.caste.SIGHT_RANGE-1.);
						v.x=-parameters.SIZE_X;
					}else{
						v.y=-(2*self.caste.SIGHT_RANGE-1.);
						v.x=-parameters.SIZE_X;
					}
				}
			}else{
				if(random.nextInt(2)==0){
					if(random.nextInt(2)==0){
						v.x=(2*self.caste.SIGHT_RANGE-1.);
						v.y=parameters.SIZE_Y;
					}else{
						v.x=-(2*self.caste.SIGHT_RANGE-1.);
						v.y=parameters.SIZE_Y;
					}
				}else{
					if(random.nextInt(2)==0){
						v.x=(2*self.caste.SIGHT_RANGE-1.);
						v.y=-parameters.SIZE_Y;
					}else{
						v.x=-(2*self.caste.SIGHT_RANGE-1.);
						v.y=-parameters.SIZE_Y;
					}
				}
			}
		}

		moveInDirection(v.angle());
		say(0);
	}

}
