/**
 * 
 */
package wota.ai.bonnmath; /* <-- change this to de.wota.ai.YOURNAME
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
@AIInformation(creator = "Simon", name = "Mueller")
public class Mueller extends MyAntAI {

	boolean hatzucker=false;
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
	 * e.g. a random elment of {0,1,2}					SeededRandomizer.getInt(3)
	 * 
	 * to iterate over a list (e.g. visibleAnts) use	for (Ant ant : visibleAnts) {
	 * 														// ant is an element of visibleAnts
	 * 													}
	 * 
	 * A full list of possible actions and how to get information is available at 
	 * doc/de/wota/gameobjects/AntAI.html
	 */

	LinkedList<Haufen> sugarlist=new LinkedList<Haufen>();
	
	@Override
	public void tick() throws Exception {
		// sample AI which moves with at constant angle 27 degrees until it finds some sugar source
		// note: it won't leave the sugar source after it reached it.
		for(Sugar sugar : visibleSugar){
			Haufen neuerhaufen=new Haufen(Vector.subtract(vectorTo(sugar),vectorToHome()),sugar.amount);
			if(vorhandencheck(neuerhaufen,sugarlist)==-1){
				int index=insertionposition(neuerhaufen, sugarlist);
				sugarlist.add(index,neuerhaufen);
			}else{
				sugarlist.set(vorhandencheck(neuerhaufen,sugarlist),neuerhaufen);
			}
		}
		if(vectorToHome().length()<Caste.Hill.HEARING_RANGE){
			if(zeiger>=sugarlist.size()){
				zeiger=0;
			}
			if(sugarlist.size()>0){
				talk(1+vecttotalk(sugarlist.get(zeiger).getPosition(),sugarlist.get(zeiger).getamount()));
			}
			zeiger++;
			if(zuckerloss){
				talk(2+vecttotalk(position,0));
			}
		}
		
		HillMessage message = audibleHillMessage;
		if(message != null){
			if(mod(message.content,10)==1 && hatzucker==false){
				position.x=message.content/((int)(Math.round(parameters.SIZE_Y))*10)-Math.round(parameters.SIZE_X)/2;
				position.y=mod(message.content/10,(int) (Math.round(parameters.SIZE_Y)))-parameters.SIZE_Y/2;
				hatzucker=true;
				zuckerloss=false;
			}
		}
		
		
		
		boolean kampf=false;
		boolean angriff=false;
		int nearbyenemy=0;
		Ant worstant=self;
		Ant bestant=self;
		for(Ant ant : visibleEnemies()){
			if(ant.sugarCarry>0 && ((vectorTo(ant).length()<vectorTo(bestant).length())||(bestant==self))){
				bestant=ant;
			}
			if(ant.caste!= Caste.Scout && ant.sugarCarry==0 && ((vectorTo(ant).length()<vectorTo(worstant).length())||(worstant==self))){
				worstant=ant;
				kampf=true;
			}
			if(ant.caste!= Caste.Scout && vectorTo(ant).length()<parameters.ATTACK_RANGE+ant.caste.SPEED-self.caste.SPEED && ant.sugarCarry==0){
				dropSugar();
				nearbyenemy++;
			}
		}
		if(nearbyenemy<5 && self.sugarCarry==0 && bestant!=self){
			angriff=true;
			kampf=true;
		}
		if(kampf){
			if(angriff){
				moveInDirection(vectorTo(bestant).angle());
				attack(bestant);
			}else{
				double targetdirection=0;
				if(hatzucker && self.sugarCarry==0){
					targetdirection=torus(Vector.add(position,vectorToHome())).angle();
				}else{
					targetdirection=vectorToHome().angle();
				}
				double lambda=0.98;
				if(vectorTo(worstant).length()>parameters.ATTACK_RANGE+worstant.caste.SPEED){
					lambda=0.8;
				}
				if(vectorTo(worstant).length()>self.caste.SIGHT_RANGE/2){
					lambda=0.5;
				}
				double movingdirection=mod(lambda*(vectorTo(worstant).angle()-180)+(1-lambda)*targetdirection,360);
				if(Math.abs(mod(movingdirection-vectorTo(worstant).angle()+90,360))<180){
					movingdirection=mod(movingdirection+180,360);
				}
				moveInDirection(movingdirection);
			}
		}
		
		
		if(kampf==false){
			if(hatzucker && self.sugarCarry==0){
				moveInDirection(torus(Vector.add(position, vectorToHome())).angle());
				if(torus(Vector.add(position, vectorToHome())).length()<15 && visibleSugar.size()==0){
					hatzucker=false;
					zuckerloss=true;
				}
				if(visibleSugar.size()>0 && torus(Vector.add(position, vectorToHome())).length()<15){
					moveInDirection(vectorTo(closest(visibleSugar)).angle(),vectorTo(closest(visibleSugar)).length());
					pickUpSugar(closest(visibleSugar));
				}
			}else{
				moveHome();
			}
		}
		
	}

}
