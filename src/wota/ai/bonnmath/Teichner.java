/**
 * 
 */
package wota.ai.bonnmath; /* <-- change this to wota.ai.YOURNAME
 							  * make sure your file is in the folder /de/wota/ai/YOURNAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */
import wota.gameobjects.AntAI;
import wota.gameobjects.Sugar;
import wota.utility.SeededRandomizer;
import wota.gamemaster.AIInformation;

import wota.gameobjects.*;


import java.util.LinkedList;
import java.util.List;

import wota.utility.Modulo;
import wota.utility.Vector;
/**
 * Put a describtion of you AI here.
 */
public class Teichner extends AntAI {
	double direction=0;
	boolean position=false;
	boolean boss=true;
	int value=-1;
	int geduld=0;
	int wait=0;
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
	 * doc/de/wota/gameobjects/AntAI.html
	 */
	@Override
	public void tick() throws Exception {
		if(random.nextDouble()<0.03){
			direction=random.nextDouble()*360.0;
		}
		if(position==false && value==-1){
			value=random.nextInt(100000);
			talk(10*value+7);
		}
		if(position==false){
			for(AntMessage message : audibleAntMessages){
				if(message.sender.caste == Caste.Soldier && mod(message.content,10)==7){
					position=true;
					if(message.content>value*10+7){
						boss=false;
					}
				}
			}
		}
		if(boss && position){
			talk(7);
			if(visibleEnemies().size()>0){
				if(vectorTo(closest(visibleEnemies())).length()<parameters.ATTACK_RANGE){
					geduld=0;
					attack(closest(visibleEnemies()));
					moveToward(closest(visibleEnemies()),vectorTo(closest(visibleEnemies())).length());
				}else{
					if(geduld<30){
						moveInDirection(vectorTo(closest(visibleEnemies())).angle());
						geduld++;
					}else{
						moveInDirection(vectorTo(closest(visibleEnemies())).angle()+180);
						geduld++;
						if(geduld>40){
							geduld=0;
						}
					}
				}
			}else{
				moveInDirection(direction);
			}
			
			
			
		}
		Ant bossy=self;
		if(position && boss==false ){
			for(AntMessage message : audibleAntMessages){
				if((bossy==self || vectorTo(bossy).length()>vectorTo(message.sender).length())&&message.sender.caste == Caste.Soldier && mod(message.content,10)==7){
					bossy=message.sender;
				}
			}
			if(bossy==self){
				wait++;
				attack(closest(visibleEnemies()));
				if(wait==2){
					wait=0;
					boss=true;
					position=false;
				}
			}
			if(vectorTo(bossy).length()>self.caste.HEARING_RANGE/4){
				moveInDirection(vectorTo(bossy).angle());
			}else{
				attack(closest(visibleEnemies()));
				if(vectorTo(closest(visibleFriends())).length()<parameters.ATTACK_RANGE/3){
					moveInDirection(vectorTo(closest(visibleFriends())).angle(), parameters.ATTACK_RANGE/3-vectorTo(closest(visibleFriends())).length());
				}
			}
		}
		
	}

}
