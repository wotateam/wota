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
public class Willi extends MyAntAI {

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
		boolean D=(atarget==self);
		boolean E=(closenemy==self);
		double prefdir=direction;
		
		
		if(B){
			if(E){
				prefdir=torus(Vector.subtract(mysugar.getsnapshot().getPosition(), self.getPosition())).angle();
			}else{
				prefdir=getdir(torus(Vector.subtract(mysugar.getsnapshot().getPosition(),self.getPosition())).angle(),vectorTo(closenemy).angle(),vectorTo(closenemy).length());
			}
		}else{
			if(!E) prefdir=getdir(direction,vectorTo(closenemy).angle(),vectorTo(closenemy).length());
		}
		
		
		
		if(!A){
			if(E){
				moveHome();
			}else{
				if(vectorToHome().length()*closenemy.caste.ATTACK/self.caste.SPEED_WHILE_CARRYING_SUGAR< Math.min(self.health,self.caste.INITIAL_HEALTH)){
					moveHome();
				}else{
					moveInDirection(getdir(vectorToHome().angle(),vectorTo(closenemy).angle(),vectorTo(closenemy).length()));
					if(vectorTo(closenemy).length()<parameters.ATTACK_RANGE+closenemy.caste.SPEED) dropSugar();
				}
			}
			
		}else{
			if(!D){
				moveToward(atarget);
				if(vectorTo(atarget).length()<parameters.ATTACK_RANGE) attack(atarget);
			}else{
				if(!C){
					moveToward(mysugar.getsnapshot());
				}else{
					if(!E && torus(vectorTo(closenemy)).length()-alpha*parameters.ATTACK_RANGE<0){	//parameters.TICKS_SUGAR_PICKUP*closenemy.caste.SPEED){	
						moveInDirection(Modulo.mod(vectorTo(closenemy).angle()+180., 360.));	
						if(ctarget!=self){
							attack(ctarget);
						}
					}else{
						moveToward(closest(visibleSugar));
						if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius ) pickUpSugar(closest(visibleSugar));
					}
				}
			}
		}
	
		
		say(0);
	}

}
