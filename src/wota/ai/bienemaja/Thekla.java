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
import wota.utility.Modulo;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Put a description of you AI here.
 */
public class Thekla extends MyAntAI {

	
	int ticksperhill=100;
	int timespent=0;
	int maxsoldiers=2;
	boolean arrived=false;
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
	 * doc/wota/gameobjects/AntAI.html
	 */
	@Override
	public void tick() throws Exception {
		// sample AI which moves with at constant angle 27 degrees until it finds some sugar source
		// note: it won't leave the sugar source after it reached it.
		moveInDirection(0);
		dowhatcanbedone();
		boolean A=(self.sugarCarry==0);
		boolean B=(mysugar!=null);
		boolean C=(B && torus(Vector.subtract(mysugar.getsnapshot().getPosition(),self.getPosition())).length()<2*parameters.INITIAL_SUGAR_RADIUS );
		boolean D=(btarget==self);
		boolean E=(closeenemy==self);
		double prefdir=direction;
		
		if(arrived) timespent++;
		maxsoldiers=2+time/1000;
		if(timespent>ticksperhill && numbfriendsoldier>maxsoldiers){
			getnextsugar(0);
			timespent=0;
		}
		if(!D){
			if(atarget!=self){
				attack(atarget);
				moveToward(atarget);
			}else{
				moveToward(btarget);
			}
		}else{
			if(!B){
				moveInDirection(direction);
			}else{
				if(torus(Vector.subtract(self.getPosition(), mysugar.getsnapshot().getPosition())).length()>self.caste.SIGHT_RANGE-2*self.caste.SIGHT_RANGE){
					moveToward(mysugar.getsnapshot());
				}else{
					arrived=true;
					if(enemyforce<friendforce){
						if(ctarget!=self){
							attack(ctarget);
							if(closefriend!=self && vectorTo(ctarget).length()>parameters.ATTACK_RANGE/1.7){
								moveInDirection(vectorTo(closefriend).angle()+180,self.caste.SPEED/3);
							}else{
								moveInDirection(vectorTo(ctarget).angle());
							}
						}else{
							moveToward(dtarget);
						}
					}else{
						moveInDirection(Modulo.mod(vectorTo(closeenemy).angle()+180., 360.));
					}	
				}	
				
			}
		}		
		say(0);
	}

}
