/**
 * 
 */
package wota.ai.bonnmath; /* <-- change this to wota.ai.YOURNAME
 							  * make sure your file is in the folder /de/wota/ai/YOURNAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import java.util.LinkedList;

import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.gameobjects.AntMessage;
import wota.gameobjects.Caste;
import wota.gameobjects.HillMessage;
import wota.gameobjects.Message;
import wota.gameobjects.Sugar;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;



/**
 * Put a describtion of you AI here.
 */
public class Rapoport extends MyAntAI {

	boolean hatzucker=false;
	boolean siege=false;
	boolean zuckerloss=false;
	int zeiger=0;
	Vector position=new Vector(0,0);
	
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
		// sample AI which moves with at constant angle 27 degrees until it finds some sugar source
		// note: it won't leave the sugar source after it reached it.
		
		
		HillMessage message = audibleHillMessage;
		if(message != null){
			if((mod(message.content,10)==1|| mod(message.content,10)==2) && hatzucker==false){
				if(mod(message.content,10)==2){
					siege=true;
				}
				position.x=message.content/((int)(Math.round(parameters.SIZE_Y))*10)-Math.round(parameters.SIZE_X)/2;
				position.y=mod(message.content/10,(int) (Math.round(parameters.SIZE_Y)))-parameters.SIZE_Y/2;
				hatzucker=true;
				zuckerloss=false;
			}
		}
		
		if(Vector.add(position, vectorToHome()).length()<15 && visibleSugar.size()==0 && (siege==false)){
			hatzucker=false;
			zuckerloss=true;
		}
		if(zuckerloss){
			talk(2+vecttotalk(position,0));
		}
		int closeenemies=0;
		int verycloseenemies=0;
		int closefriends=0;
		int	veryclosefriends=0;
		Vector centerofmass=new Vector(0,0);
		boolean sugarenemy=false;
		Ant victim=self;
		Ant worstant=self;
		Ant closestfriend=self;
		for(Ant ant : visibleFriends()){
			if(ant.caste==Caste.Soldier&& (vectorTo(ant).length()<vectorTo(closestfriend).length()||victim==self)){
				closestfriend=ant;
			}
			if(ant.caste==Caste.Soldier && vectorTo(ant).length()<3*parameters.ATTACK_RANGE){
				closefriends++;	
				centerofmass=Vector.add(centerofmass,vectorTo(ant));
			}
			if(ant.caste==Caste.Soldier && vectorTo(ant).length()<parameters.ATTACK_RANGE){
				veryclosefriends++;
			}
		}
		if(closefriends>0){
			centerofmass.x/=closefriends;
			centerofmass.y/=closefriends;
		}
		for(Ant ant : visibleEnemies()){
			if(ant.sugarCarry>0 && (vectorTo(ant).length()<vectorTo(victim).length()||victim==self)){
				victim=ant;
				sugarenemy=true;
			}
			if(ant.caste==Caste.Soldier && (vectorTo(ant).length()<vectorTo(worstant).length()||worstant==self)){
				worstant=ant;
			}
			if(ant.caste==Caste.Soldier && vectorTo(ant).length()<parameters.ATTACK_RANGE){
				verycloseenemies++;
			}
			if(ant.caste==Caste.Soldier && vectorTo(ant).length()<3*parameters.ATTACK_RANGE){
				closeenemies++;		
			}
		}
		if(sugarenemy){
			moveInDirection(vectorTo(victim).angle(),vectorTo(victim).length());
			attack(victim);
		}
		if(!sugarenemy && zuckerloss==false){
			if(Vector.add(vectorToHome(),position).length()>self.caste.SIGHT_RANGE){
				if(closeenemies<2){
					moveInDirection(torus(Vector.add(vectorToHome(),position)).angle());
				}else{
					moveInDirection(vectorTo(worstant).angle()+180);
				}
			}else{
				if(closeenemies>=closefriends && closeenemies>0){
					moveInDirection(vectorTo(worstant).angle()+180);
				}else{
					if(closeenemies>0){
						if(vectorTo(worstant).length()<parameters.ATTACK_RANGE){
							moveInDirection(centerofmass.angle());
						}else{
							moveInDirection(vectorTo(worstant).angle());
						}
						if(vectorTo(closestfriend).length()<parameters.ATTACK_RANGE/(1+verycloseenemies)){
							moveInDirection(vectorTo(closestfriend).angle(), parameters.ATTACK_RANGE/(1+verycloseenemies)-vectorTo(closestfriend).length());
						}
						attack(worstant);
					}else{
						if(hatzucker && veryclosefriends>2 && random.nextDouble()<0.1){
							hatzucker=false;
							position=new Vector(0,0);
						}
						moveInDirection(Vector.add(vectorToHome(), position).angle());
					}
					
				}
			}
		}
		if(zuckerloss){
			if(closeenemies>2){
				moveInDirection(vectorTo(worstant).angle()+180);
			}else{
				moveHome();
			}
		}
	}
	
}



