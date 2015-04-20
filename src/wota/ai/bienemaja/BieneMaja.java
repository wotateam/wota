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
 * a good bee
 */
public class BieneMaja extends MyAntAI {

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
	/*
		System.out.print(getdir(10.,20.,48.));
		System.out.print("\n");
		System.out.print(getdir(10.,20.,36.));
		System.out.print("\n");
		System.out.print(getdir(10,45.,48.));
		System.out.print("\n");
		System.out.print(getdir(10.,20.,34.));
	*/	
		
		moveInDirection(0);
		dowhatcanbedone();
		boolean A=(self.sugarCarry==0);
		boolean B=(mysugar!=null);
		boolean C=(B && torus(Vector.subtract(mysugar.getsnapshot().getPosition(),self.getPosition())).length()<2*parameters.INITIAL_SUGAR_RADIUS );
		boolean D=(btarget==self);
		boolean E=(closeenemy==self);
		boolean F=(self.health<10*Caste.Soldier.ATTACK);
		double prefdir=direction;
		// First division: test whether enough health
		if(!F){
			// set direction where ant wants to go (in case it is not carrying sugar
			if(B){
				if(E || friendforce>=enemyforce){
					prefdir=torus(Vector.subtract(mysugar.getsnapshot().getPosition(), self.getPosition())).angle();
				}else{
					prefdir=getdir(torus(Vector.subtract(mysugar.getsnapshot().getPosition(),self.getPosition())).angle(),vectorTo(closeenemy).angle(),vectorTo(closeenemy).length());
				}
			//	prefdir=torus(Vector.subtract(mysugar.getsnapshot().getPosition(), self.getPosition())).angle();
			}else{
				if(!E) prefdir=getdir(direction,vectorTo(closeenemy).angle(),vectorTo(closeenemy).length());
			}
			
			
			// Easy case carrying sugar
			if(!A){
				if(E){
					moveHome();
				}else{
					if(vectorToHome().length()*Caste.Soldier.ATTACK*parameters.VULNERABILITY_WHILE_CARRYING*(numbcloseenemysoldier+numbcloseenemygatherer)/(self.caste.SPEED_WHILE_CARRYING_SUGAR*2)< Math.min(self.health,self.caste.INITIAL_HEALTH/2)){
						moveHome();
					}else{
						moveInDirection(getdir(vectorToHome().angle(),vectorTo(closeenemy).angle(),vectorTo(closeenemy).length()));
						if(vectorTo(closeenemy).length()<parameters.ATTACK_RANGE+closeenemy.caste.SPEED) dropSugar();
					}
				}
			// not carrying sugar	
			}else{
				// easy case: good victim
				if(!D){
					if(atarget!=self){
						moveToward(atarget);
						if(vectorTo(atarget).length()<parameters.ATTACK_RANGE) attack(atarget);
					}else{
						moveToward(btarget);
					}
				}else{
					// did not yet reach sugar
					// in case you have more ants you probably have to be more agressive 
					// 
					if(!C && reachedsugar==false){
						moveInDirection(prefdir);
					// reached sugar
					}else{
						//enemy in sightrange
						if(!E){	//parameters.TICKS_SUGAR_PICKUP*closenemy.caste.SPEED){
							if(enemyforce<friendforce && torus(Vector.subtract(self.getPosition(), mysugar.getsnapshot().getPosition())).length()< beta*self.caste.SIGHT_RANGE){
								//getsugar tests whether there is a sugar in sightrange
								if(getsugar){
									moveToward(closest(visibleSugar));
									if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius && vectorTo(closeenemy).length()>parameters.ATTACK_RANGE+closeenemy.caste.SPEED){
										pickUpSugar(closest(visibleSugar));
									}
								}
								if(ctarget!=self){
									attack(ctarget);
									if(closefriend!=self && vectorTo(ctarget).length()<parameters.ATTACK_RANGE/1.7){
										moveInDirection(vectorTo(closefriend).angle()+180,self.caste.SPEED/3);
									}else{
										moveInDirection(vectorTo(ctarget).angle());
									}
								}else{
									moveToward(dtarget);
								}
								}else{
									moveInDirection(prefdir);
							//		moveInDirection(Modulo.mod(vectorTo(closeenemy).angle()+180., 360.));
								}
						// no enemy in sightrange	
						}else{
							if(!C){
								moveInDirection(prefdir);
							}else{
								if(visibleSugar.size()>0){
									moveToward(closest(visibleSugar));
									if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius ) pickUpSugar(closest(visibleSugar));		
								}
								
							}
						}
					}
				}
			}
		
			
			say(0);
		}else{
			if(closeenemy!=self && torus(vectorTo(closeenemy)).length()<parameters.ATTACK_RANGE+self.caste.SPEED+closeenemy.caste.SPEED){
				moveInDirection(mod(vectorTo(closeenemy).angle()+180,360));
			}else{
				if(atarget!=self){
					moveToward(atarget);
					attack(atarget);
				}else{
					if(btarget!=self){
						moveToward(btarget);
					}else{
						if(closeenemy!=self){
							moveToward(closeenemy);
						}else{
							moveInDirection(direction);
						}
					
					}
				}
			}
			
		}
	}

}


		/* Sicherung Kampfmodus
		 * if(!E && torus(vectorTo(closenemy)).length()-alpha*parameters.ATTACK_RANGE<0){	//parameters.TICKS_SUGAR_PICKUP*closenemy.caste.SPEED){
						if(numbenemy<numbfriend+1){
							if(ctarget!=self){
								attack(ctarget);
								if(closfriend!=self && vectorTo(ctarget).length()>parameters.ATTACK_RANGE/1.7){
									moveInDirection(vectorTo(closfriend).angle()+180,self.caste.SPEED/3);
								}else{
									moveInDirection(vectorTo(ctarget).angle());
								}
							}else{
								moveToward(dtarget);
							}
							}else{
								moveInDirection(Modulo.mod(vectorTo(closenemy).angle()+180., 360.));
							}		
					}else{
						moveToward(closest(visibleSugar));
						if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius ) pickUpSugar(closest(visibleSugar));
					}
				}
		 */

		/*
		dowhatcanbedone();
		boolean A=(self.sugarCarry==0);
		boolean B=(mysugar!=null);
		boolean C=(B && torus(Vector.add(mysugar.getsnapshot().getPosition(),vectorToHome())).length()<2*parameters.INITIAL_SUGAR_RADIUS );
		boolean D=(atarget==self);
		boolean E=(closenemy==self);
		double prefdir=direction;
		if(B){
			if(E){
				prefdir=torus(Vector.add(mysugar.getsnapshot().getPosition(), vectorToHome())).angle();
			}else{
				prefdir=getdir(torus(Vector.add(mysugar.getsnapshot().getPosition(), vectorToHome())).angle(),vectorTo(closenemy).angle(),vectorTo(closenemy).length());
			}
		}else{
			if(!E) prefdir=getdir(direction,vectorTo(closenemy).angle(),vectorTo(closenemy).length());
		}
		
		
		
		if(!A){
			if(E){
				moveHome();
			}else{
				moveInDirection(getdir(vectorToHome().angle(),vectorTo(closenemy).angle(),vectorTo(closenemy).length()));
				if(vectorTo(closenemy).length()<parameters.ATTACK_RANGE+closenemy.caste.SPEED) dropSugar();
			}
			
		}else{
			if(!D){
				moveToward(atarget);
				if(vectorTo(atarget).length()<parameters.ATTACK_RANGE) attack(atarget);
			}else{
				if(!C){
					moveInDirection(prefdir);
				}else{
					if(!E && torus(vectorTo(closenemy)).length()-parameters.ATTACK_RANGE<0){	//parameters.TICKS_SUGAR_PICKUP*closenemy.caste.SPEED){
						moveInDirection(Modulo.mod(vectorTo(closenemy).angle()+180., 360.));
					}else{
						moveToward(closest(visibleSugar));
						if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius ) pickUpSugar(closest(visibleSugar));
					}
				}
			}
		}
		/*
		if(mysugar!=null) {
			if(torus(Vector.add(mysugar.getsnapshot().getPosition(),vectorToHome())).length()>2*parameters.INITIAL_SUGAR_RADIUS || visibleSugar.size()==0){
				prefdir=torus(Vector.add(mysugar.getsnapshot().getPosition(),vectorToHome())).angle();
			}else{
				prefdir=vectorTo(closest((visibleSugar))).angle();
			}
		}
		if(self.sugarCarry==0){
			if(atarget!=self){
				moveToward(atarget);
				if(vectorTo(atarget).length()<parameters.ATTACK_RANGE) attack(atarget);
			}else{
				if(torus(Vector.add(mysugar.getsnapshot().getPosition(),vectorToHome())).length()>2*parameters.INITIAL_SUGAR_RADIUS || visibleSugar.size()==0){
					if(closenemy!=self){
						moveInDirection(getdir(prefdir, vectorTo(closenemy).angle(),vectorTo(closenemy).length()));
					}else{
						moveInDirection(prefdir);
					}
				}else{
					if(vectorTo(closenemy).length()<5*parameters.ATTACK_RANGE){
						moveToward(closest(visibleSugar));
						if(vectorTo(closest(visibleSugar)).length()<closest(visibleSugar).radius){
							pickUpSugar(closest(visibleSugar));
						}
					}else{
						moveInDirection(Modulo.mod(vectorTo(closenemy).angle()-360.,360.));
					}
				}
			}	
		}else{
			prefdir=vectorToHome().angle();
			if(closenemy!=self){
				moveInDirection(getdir(prefdir, vectorTo(closenemy).angle(),vectorTo(closenemy).length()));
				if(vectorTo(closenemy).length()<parameters.ATTACK_RANGE+closenemy.speed){
					dropSugar();
				}
			}else{
				moveInDirection(prefdir);
			}	
		}
*/

