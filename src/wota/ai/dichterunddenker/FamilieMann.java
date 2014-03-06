/**
 * 
 */
package wota.ai.dichterunddenker; /* <-- change this to de.wota.ai.YOURNAME
 							  * make sure your file is in the folder /de/wota/ai/YOURNAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import java.awt.peer.SystemTrayPeer;

import wota.gameobjects.Ant;
import wota.gameobjects.AntAI;
import wota.gameobjects.Caste;
import wota.utility.SeededRandomizer;
import wota.gameobjects.Hill;

/**
 * Put a describtion of you AI here.
 */
public class FamilieMann extends AntAI {
 
	public double	dir=random.getInt(360);
	public double	 mysugar = 0;
	public boolean 	ontheway = false;
	public boolean gehehause = false;
	public boolean 	   warte = false;
	public int 			time =12;
	public double 	unsugar=0;
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
		if(ontheway){
			time--;
		}
		if(time==0){
			unsugar=getLastMovementDirection();
			ontheway=false;
			mysugar=0;
		}
		if(visibleEnemies().size()>0){
			boolean fliehen=false;

			double minab=1000;
			Ant naherfeind=visibleEnemies().get(0);
			for(Ant ant:visibleEnemies()){
				if(ant.caste==Caste.Soldier ||(ant.caste == Caste.Gatherer && ant.sugarCarry==0)){
					if(vectorTo(ant).length()<minab){
						minab=vectorTo(ant).length();
						naherfeind=ant;
						fliehen=true;
					}
				}
			}
			if(fliehen){
				ontheway=false;
				time=35;
				moveInDirection(vectorTo(naherfeind).angle()-179);
				if(vectorTo(naherfeind).length()<25){
					dropSugar();
				}
			}else{
				if(self.sugarCarry>0){
					moveHome();
				}else{
					if(closest(visibleEnemies()).sugarCarry>0){
						moveToward(closest(visibleEnemies()));	
						attack(closest(visibleEnemies()));
						ontheway=false;
						time=35;
					}else{
						if(mysugar==0){
							moveInDirection(dir);
						}else{
							moveInDirection(mysugar);
						}
					}		
				}
			}
		}else{
			if(gehehause){
				moveHome();
				if(self.sugarCarry>0 && mysugar==0){
					mysugar=vectorToHome().angle()-180;
				}	
				if(self.sugarCarry==0){
					gehehause=false;
					if(mysugar !=0){
						ontheway=true;
						time=35;
					}
					
				}
			}else{
				if(ontheway){
					if(visibleSugar.size()==0){
						moveInDirection(mysugar);
					}else{
						moveToward((closest(visibleSugar)));
						if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius){
							pickUpSugar(closest(visibleSugar));
							gehehause=true;
							ontheway=false;
							time=35;
						}
						
					}
			
				}else{
					if(visibleSugar.size()==0){
						dir+=random.getInt(5);
						moveInDirection(dir);
					}else{
						moveToward((closest(visibleSugar)));
						if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius){
							pickUpSugar(closest(visibleSugar));
							gehehause=true;
							ontheway=false;
							time=12;
						}
						
					}
				}
			}
		}
		
	}

}
