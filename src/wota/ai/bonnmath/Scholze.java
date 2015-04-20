/**
 * 
 */
package wota.ai.bonnmath; /* <-- change this to de.wota.ai.YOURNAME
 							  * make sure your file is in the folder /de/wota/ai/YOURNAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.gamemaster.AIInformation;
import wota.gameobjects.AntAI;
import wota.gameobjects.Caste;
import wota.gameobjects.Sugar;
import wota.gameobjects.Hill;
import wota.utility.SeededRandomizer;
import wota.ai.bonnmath.*;

import java.util.LinkedList;
import java.util.List;

import wota.utility.Modulo;
import wota.utility.Vector;
/**
 * Put a describtion of you AI here.
 */

@AIInformation(creator = "Simon", name = "Scholze")
public class Scholze extends MyAntAI {

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

	double radius = Caste.Scout.SIGHT_RANGE;
	boolean	goout = true;
	boolean goround = false;
	boolean goback = false;
	boolean communicate = false;
	LinkedList<Haufen> sugarlist=new LinkedList<Haufen>();
	LinkedList<Haufen> gegnerlist= new LinkedList<Haufen>();
	int indexofsugar=0;
	
	
	@Override
	public void tick() throws Exception {
		for(Hill hill: visibleHills){
			if(hill.playerID!=self.playerID && gegnerlist.size()==0){
				Haufen newhaufen=new Haufen(Vector.subtract(vectorTo(hill),vectorToHome()),0);
				gegnerlist.add(0,newhaufen);
			}
		}
		for(Sugar sugar : visibleSugar){
			Haufen neuerhaufen=new Haufen(torus(Vector.subtract(vectorTo(sugar),vectorToHome())),sugar.amount);
			if(vorhandencheck(neuerhaufen,sugarlist)<0){
				int index=insertionposition(neuerhaufen, sugarlist);
				sugarlist.add(index,neuerhaufen);
			}else{
				sugarlist.set(vorhandencheck(neuerhaufen,sugarlist),neuerhaufen);
			}
		}
		/*
		Haufen Bufferhaufen=new Haufen(vectorToHome(),0);
		boolean nochexistent=false;
		for(Haufen haufen : sugarlist){
			if(Vector.add(haufen.getPosition(),vectorToHome()).length()<100){
				for(Sugar sugar : visibleSugar){
					if(Vector.subtract(Vector.add(haufen.getPosition(),vectorToHome()),vectorTo(sugar)).length()<10){
						nochexistent=true;
					}
				}
				if(nochexistent==false){
					Bufferhaufen=haufen;
				}
			}
		}
		if(nochexistent==false){
			sugarlist.remove(Bufferhaufen);
		}
		*/
		
	
		if(goout){
			moveInDirection(0);
			if(vectorToHome().length()>radius){
				goround=true;
				goout=false;
			}
		}
		if(goround){
			if(vectorToHome().length()<radius+10){
				moveInDirection(vectorToHome().angle()-90);
			}else{
				moveInDirection(vectorToHome().angle()-80);
			}
			if(mod(vectorToHome().angle(),360)<180 && (vectorToHome().x+radius)*(vectorToHome().x+radius)+vectorToHome().y*vectorToHome().y<1000){
				goround=false;
				goback=true;
			}
		}
		
		if(goback){
			moveInDirection(vectorToHome().angle(),vectorToHome().length());
			if(vectorToHome().length()<3){
				goback=false;
				communicate=true;
			}
		}
		if(communicate){
			if(indexofsugar<sugarlist.size()){
				Haufen haufen=sugarlist.get(indexofsugar);
				talk((int) (10*(Math.round(parameters.SIZE_Y)*Math.round(haufen.getPosition().x+parameters.SIZE_X/2))+10*(Math.round(haufen.getPosition().y)+Math.round(parameters.SIZE_Y/2)))+1);
				indexofsugar++;
			}else{
				indexofsugar=0;
				if(gegnerlist.size()>0){
					Haufen haufen=gegnerlist.get(0);
					talk((int) (10*Math.round(parameters.SIZE_X)*Math.round(parameters.SIZE_Y)*haufen.getamount()+10*Math.round(parameters.SIZE_Y)*(Math.round(haufen.getPosition().x+parameters.SIZE_X/2))+10*(Math.round(haufen.getPosition().y+parameters.SIZE_Y/2))+2));
				}
				communicate=false;
				goout=true;
				radius+=2*Caste.Scout.SIGHT_RANGE-10;
				if((radius+self.caste.SIGHT_RANGE)*(radius+self.caste.SIGHT_RANGE)>(parameters.SIZE_X*parameters.SIZE_X+parameters.SIZE_Y*parameters.SIZE_Y)/4){
					radius=Caste.Scout.SIGHT_RANGE;
				}
			}
		}
		
		
	}




}
