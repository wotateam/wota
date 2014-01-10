/**
 * 
 */
package wota.ai.dichterunddenker; /* <-- change this to de.wota.ai.YOURNAME
 							  * make sure your file is in the folder /de/wota/ai/YOURNAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.utility.SeededRandomizer;

/**
 * Put a describtion of you AI here.
 */
public class FranzKafka extends AntAI {

	public int dir=0;
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
	@Override
	public void tick() throws Exception {
		// sample AI which moves with at constant angle 27 degrees until it finds some sugar source
		// note: it won't leave the sugar source after it reached it.
		if (dir==0 && audibleHillMessage != null) {
			dir=2*audibleHillMessage.content;
		}
		if(SeededRandomizer.getDouble()<0.02){
			dir=dir+20;
		}
		
		if(visibleEnemies().size()>0){
			int counter=0;
			for(Ant ant:visibleFriends()){
				if(vectorTo(ant).length()<10){
					counter++;
				}
			}
			if(counter<4){
				Ant bestant=visibleEnemies().get(0);
				Ant worstant=visibleEnemies().get(0);
				int enemies=0;
				boolean angriff=false;
				boolean sugar=false;
				for(Ant ant:visibleEnemies()){
					if(ant.sugarCarry>0){
						sugar=true;
						angriff=true;
						if(vectorTo(ant).length()<vectorTo(bestant).length()){
							bestant=ant;
						}
					}
					if(ant.caste!=self.caste && sugar==false){
						angriff=true;
						if(vectorTo(ant).length()<vectorTo(bestant).length()){
							bestant=ant;
						}
					}
					if(ant.caste==self.caste){
						if(vectorTo(ant).length()<30){
							enemies++;
						}
						if(vectorTo(ant).length()<vectorTo(worstant).length()){
							worstant=ant;
						}
					}
					
				}	
				if(enemies>1){
					angriff=false;
				}
				if(angriff){
					moveToward(bestant);
					attack(bestant);
				}else{
					moveInDirection(vectorTo(worstant).angle()-180);
				}
			}else{
				moveInDirection(SeededRandomizer.getInt(360));
			}
		}else{
			moveInDirection(dir/2);
		}
		
		
		
	}

}
