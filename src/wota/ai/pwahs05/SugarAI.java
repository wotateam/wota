/**
 * 
 */
package wota.ai.pwahs05; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import java.util.ListIterator;

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.Vector;


/**
 * Put a description of you AI here.
 */
@AIInformation(creator = "Anonymous", name = "Anonymous's AI")
public class SugarAI extends TalkingAntAI {

	double last_health = -1;
	
	Snapshot target_sugar = null;
	
	boolean got_hit_with_sugar = false;
	
	boolean run;
	
	@Override
	public void other_stuff() {
		run_away();
	//	System.out.printf("ID=%d, known sugar hills: %d\n", self.id, hills.get(HillAI.SUGAR_IND).size());
	}
		
	@Override
	public void handle_sugar(){
		if (just_looking()){
			if (time+initialTime <= HillAI.FORWARD){
				//do nothing
			} else {
				moveHome();
			}
		} else if (self.sugarCarry>0) {	//have sugar => go home:
			moveHome();
		} else {					//dont have sugar:
			if (target_sugar != null){
				if (vectorBetween(self,target_sugar).length() < self.caste.SIGHT_RANGE){
					if (visibleSugar.size() == 0) {
						target_sugar = null;
					}
					else {
						think_about_picking_up(closest(visibleSugar));
					}
				} else {
					moveToward(target_sugar);
					return;
				}
			} 
			if (visibleSugar.size() == 0) {		//dont see sugar:
				//if you know about sugar:
				if (hills.get(HillAI.SUGAR_IND).size() > 0) {
					Snapshot target = find_optimal_sugar_hill(null);
					if (target != null) {
						target_sugar = target;
						moveToward(target);
					}
				}
			}
			else {		//see sugar:
				Sugar sugar = closest(visibleSugar);
				target_sugar = sugar;
				moveToward(sugar); 
				think_about_picking_up(sugar);
				
			}
		}
	}
	
	public boolean just_looking(){
		return false;
		//return (self.id % 2 != 0 && initialTime + time <= HillAI.BACKWARD);
	}
	
	@Override
	public void nosugar(Snapshot sugar){
		if (sugar.hasSameOriginal(target_sugar)) {
			target_sugar = null;
		}
	}
	
	public Vector shift_aside(Vector pos){
		double factor = HillAI.COMBAT_RADIUS;
		factor *= parameters.ATTACK_RANGE;
		return Vector.add(pos, 
				(new Vector(Math.sin(dir),Math.cos(dir))).scale(factor));
	}
	
	public void think_about_picking_up(Sugar sugar){
		int count = 0;
		if (visibleEnemies().size()>=0){
			boolean military_is_here = false;
			for(Ant a: visibleFriends()){
				if (a.caste == self.caste){
					if (a.id < self.id) count++;
				}
				if (a.caste == Caste.Soldier) {
					military_is_here = true;
				}
			}
			if (military_is_here) count++;
			if (count < HillAI.GUARDLENGTH && !visibleEnemies().isEmpty()) {
				Ant enemy = getWeakestReachable(visibleEnemies());
				if (enemy!=null) attack(enemy); 
				int num_hills = hills.get(HillAI.HILL_IND).size();
				if (num_hills>1) {
					Snapshot hill = hills.get(HillAI.HILL_IND).get(self.id % (num_hills-1) + 1);
					Vector target = Vector.add(sugar.getPosition(),vectorBetween(self, hill).scaleTo(parameters.ATTACK_RANGE * HillAI.COMBAT_RADIUS));
					moveToward(target);
				} 
			} else if (count < HillAI.QUEUELENGTH && (!got_hit_with_sugar || visibleEnemies().isEmpty())) {
				moveToward(sugar);
				pickUpSugar(sugar);
			} else if (count < HillAI.WAITLENGTH) { //wait till you can go into queue
				Vector target = shift_aside(sugar.getPosition());
				moveToward(target);
				Ant enemy = getWeakestReachable(visibleEnemies());
				if (enemy!=null) attack(enemy); 
			} else{
				//go somewhere else:
				for(ListIterator<Snapshot> it = hills.get(HillAI.SUGAR_IND).listIterator();
						it.hasNext() ; ) {
					Snapshot snap = it.next();
					if (snap.hasSameOriginal(target_sugar)) {
						if (it.hasNext()){
							target_sugar = it.next();
						} else {
							target_sugar = hills.get(HillAI.SUGAR_IND).getFirst();
						}
						moveToward(target_sugar);
						break;
					}
				}
			}
		} else {
			Ant enemy = getWeakest(visibleEnemies());
			moveToward(enemy);
			attack(enemy);
		}
	}
	
	public Snapshot find_optimal_sugar_hill(Sugar except){
		if (hills.get(HillAI.SUGAR_IND).size()==0) return null;
		
		//if (hills.get(QueenAI.SUGAR_IND).size()==1) return hills.get(QueenAI.SUGAR_IND).get(0);
		Snapshot best_sugar = hills.get(HillAI.SUGAR_IND).get(0);
		if (best_sugar.hasSameOriginal(except)) best_sugar = null;
			
		for(Snapshot sugar: hills.get(HillAI.SUGAR_IND)){
			if (!sugar.hasSameOriginal(except)){
				if (walking_distance(best_sugar) > walking_distance(sugar)){
					best_sugar = sugar;
				}
			}
		}
		return best_sugar;
	}
	
	public double walking_distance(Snapshot sugarhill){
		if (sugarhill == null) return Double.MAX_VALUE;
		double d = vectorBetween(self, sugarhill).length() * self.caste.SPEED;
		d += vectorBetween(sugarhill, hills.get(HillAI.HILL_IND).getFirst()).length()
				* self.caste.SPEED_WHILE_CARRYING_SUGAR;
		return d;
	}
	
	public static double adjust_in_direction(double dir1, double dir2){
		return 0.6 * dir1 + 0.4 * dir2;
	}
	
	public void run_away(){
		/*
		
		run = false;
		for(Ant enemy: visibleEnemies()){
			if (enemy.caste == Caste.Soldier){
				dropSugar();
				moveInDirection((-1) * vectorBetween(self,enemy).angle());
				//moveHome();
				run = true;
			}
		}
		
		if (run) talk(QueenAI.FIGHT);*/
		
		Ant enemy = getClosest(visibleEnemies());
		
		if (enemy == null){
			run = false;
		}else{
			if (run){
				moveInDirection((-1) * vectorBetween(self,enemy).angle());
				//moveHome();
				if (self.sugarCarry > 0) got_hit_with_sugar = true;
				
				dropSugar();
				/*if (vectorToHome().length()<self.caste.SIGHT_RANGE) {
					if (visibleEnemies().size()>0){
						attack(getWeakest(visibleEnemies()));
					}
				}*/
			}else if (last_health > self.health + HillAI.EPS){
				int sugar_sum = self.sugarCarry;
				double sugar_health = self.health;
				for (Ant friend: visibleFriends()) {
					if (friend.sugarCarry > 0) {
						sugar_sum += friend.sugarCarry;
						sugar_health += friend.health;
					}
				}
				double turns_to_go = vectorToHome().length() / self.caste.SPEED_WHILE_CARRYING_SUGAR;
				double health_loss = turns_to_go * (last_health - self.health);
				double safe_sugar = sugar_sum * (sugar_health - health_loss) / sugar_health;
				if (self.sugarCarry > 0
					&& health_loss < sugar_health
					&& safe_sugar / parameters.ANT_COST * Caste.Gatherer.INITIAL_HEALTH > health_loss){
					//keep going
					run = false;
				}else{
					//moveInDirection((-1) * vectorBetween(self,enemy).angle());
					dropSugar();
					run = true;
					double ownStrength = self.health * self.caste.ATTACK;
					for (Ant a : visibleFriends()) {
						ownStrength += a.health * a.caste.ATTACK;
					}
					double theirStrength = 0;
					for (Ant a : visibleEnemies()) {
						theirStrength += a.health * a.caste.ATTACK;
					}
					if (ownStrength < theirStrength) run = true;
					else {
						Ant victim = getWeakestReachable(visibleEnemies());
						moveToward(getWeakest(visibleEnemies()));
						if (victim != null) attack(victim);
						
					}
				}
			}
		}
		
		last_health = self.health;
	}
	
}
