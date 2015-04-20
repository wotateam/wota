/**
 * 
 */
package wota.ai.pwahs11; /* <-- change this to wota.ai.YOUR_AI_NAME
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
	Snapshot avoid = null;
	boolean got_hit_with_sugar = false;
	Vector siege_position = null;
	
	boolean run;
	
	enum SugarAIStatus {
		SCOUTING,
		QUEUEING,
		RETURNING_HOME,
		FETCHING_SUGAR,
		RUNNING_AWAY,
		SIEGE
	};
	SugarAIStatus status = SugarAIStatus.SCOUTING;
	
	@Override
	public void tick() throws Exception{
		super.tick();
		if (self.health < last_health - HillAI.EPS && self.sugarCarry > 0) got_hit_with_sugar = true;
		do_stuff();
		last_health = self.health;
		if (time > HillAI.SIEGE_TIME) {
			dir += 1;
			if (dir >= 360) dir -= 360;
		}
	}
	
	private void do_stuff() {
		prepare_siege();
		switch(status){
		case SCOUTING:
			moveInDirection(dir);
			hit_enemies_with_sugar();
			decide_action();
			break;
		case FETCHING_SUGAR:
			if (target_sugar == null) {
				decide_action();
				do_stuff();
			} else {
				if (vectorTo(target_sugar).length() < self.caste.SIGHT_RANGE) {
					status = SugarAIStatus.QUEUEING;
					do_stuff();
				} else {
					moveToward(target_sugar);
					hit_enemies_with_sugar();
				}
			}
			break;
		case QUEUEING:
			if (self.sugarCarry > 0) {
				status = SugarAIStatus.RETURNING_HOME;
				do_stuff();
				break;
			}
			if (target_sugar == null) {
				decide_action();
				do_stuff();
				break;
			} 
			if (visibleSugar.isEmpty()) {
				avoid = target_sugar;
				decide_action();
				do_stuff();
				break;
			}
			if (wait_in_queue()) {
				moveToward(visibleSugar.get(0));
				pickUpSugar(visibleSugar.get(0));
			} else {
				Ant enemy = getWeakestReachable(visibleEnemies());
				if (enemy != null) {
					moveToward(shift_aside(enemy.position));
					attack(enemy);
				} else {
					enemy = getWeakest(visibleEnemies());
					if (enemy != null) {
						moveToward(shift_aside(enemy.position));
						attack(enemy);						
					} else {
						moveToward(shift_aside(target_sugar.getPosition()));
					}
				}
			}
			break;
		case RETURNING_HOME:
			if (self.sugarCarry == 0) {
				decide_action();
				do_stuff();
				break;
			}
			moveHome();
			if (under_heavy_attack()) {
				status = SugarAIStatus.RUNNING_AWAY;
				do_stuff();
			}
			break;
		case RUNNING_AWAY:
			dropSugar();
			moveInDirection(dir);
			if (visibleEnemies().size() == 0) {
				decide_action();
				do_stuff();
			}
			break;
		case SIEGE:
			moveToward(siege_position);
			Ant enemy = getWeakestReachable(visibleEnemies());
			if (enemy != null) {
				attack(enemy);
			} else {
				enemy = getWeakest(visibleEnemies());
				MySnapshot snap = new MySnapshot(siege_position);
				if (enemy!=null && vectorBetween(snap, enemy).length() < self.caste.SIGHT_RANGE) {
					moveToward(enemy);
					attack(enemy);
				}
			}
			break;
		default:
			decide_action();
			do_stuff();
			break;
		}
	}
	
	private void prepare_siege() {
		if (time < HillAI.SIEGE_TIME || hills[HillAI.HILL_IND].size() != 2) return;
		if (time == HillAI.END_SIEGE) {
			decide_action();
		}
		if (time >= HillAI.END_SIEGE) return;
		dropSugar();
		Vector castle = hills[HillAI.HILL_IND].get(1).s.getPosition();
		double angle = dir / 180.0 * Math.PI;
		double dist = parameters.ATTACK_RANGE * (HillAI.SIEGE_DIST + (self.id % HillAI.SIEGE_LAYERS));
		Vector displacement = (new Vector(Math.cos(angle), Math.sin(angle))).scale(dist);
		siege_position = Vector.add(castle, displacement);
		status = SugarAIStatus.SIEGE;
	}
	
	private boolean under_heavy_attack() {
		if (last_health - HillAI.EPS <= self.health || visibleEnemies().isEmpty()) {
			return false;
		}
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
		if (health_loss < sugar_health
			&& safe_sugar / parameters.ANT_COST * Caste.Gatherer.INITIAL_HEALTH > health_loss){
			//keep going
			return false;
		}else{
			//run
			return true;
		}
	}
	
	private boolean wait_in_queue() {
		int count_older_ants = 0;
		if (got_hit_with_sugar) {
			for (Ant ant: visibleEnemies()) {
				if (ant.sugarCarry == 0 && vectorTo(ant).length() <= parameters.ATTACK_RANGE) return false;
			}
		}
		for(Ant ant: visibleFriends()) {
			if (ant.caste == Caste.Gatherer && ant.id < self.id && ant.sugarCarry == 0) {
				count_older_ants++;
			}
		}
		if (count_older_ants < HillAI.QUEUELENGTH) return true;
		if (count_older_ants >= HillAI.WAITLENGTH) {
			avoid = target_sugar;
			decide_action();
		}
		return false;
	}
	
	private void decide_action() {
		int num_piles = parameters.NUMBER_OF_PLAYERS * parameters.SUGAR_SOURCES_PER_PLAYER;
		if (time <= HillAI.GREEDY_GATHERING) {
			Snapshot target = find_optimal_sugar_hill(avoid);
			if (target != null) {
				status = SugarAIStatus.FETCHING_SUGAR;
				target_sugar = target;
			} else {
				status = SugarAIStatus.SCOUTING;
			}
		} else {
			if (self.id % num_piles >= hills[HillAI.SUGAR_IND].size()) {
				status = SugarAIStatus.SCOUTING;
			} else {
				status = SugarAIStatus.FETCHING_SUGAR;
				target_sugar = hills[HillAI.SUGAR_IND].get(self.id % num_piles).s;
				if (target_sugar.hasSameOriginal(avoid)) {
					target_sugar = hills[HillAI.SUGAR_IND].get((self.id + 1) % hills[HillAI.SUGAR_IND].size()).s;
				}
			}
		}
	}
	
	private void hit_enemies_with_sugar() {
		if (visibleEnemies().size()>0 && self.caste != Caste.Scout){
			Ant enemy = getWeakest(visibleEnemies());
			if (enemy.sugarCarry>0 && self.sugarCarry == 0){
				moveToward(shift_a_bit_aside(enemy.position));
				attack(enemy);
			}
		}
	}
	
	public void handle_sugar(){
		if (self.sugarCarry>0) {	//have sugar => go home:
			moveHome();
		} else {					//don't have sugar:
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
			if (visibleSugar.size() == 0) {		//don't see sugar:
				//if you know about sugar:
				if (hills[HillAI.SUGAR_IND].size() > 0) {
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
	public void foundsugar(Snapshot sugar) {
		//do nothing	
	}
	public void nosugar(Snapshot sugar){
		if (sugar.hasSameOriginal(target_sugar)) {
			target_sugar = null;
		}
	}
	
	public Vector shift_a_bit_aside(Vector pos) {
		double factor = HillAI.COMBAT_RADIUS / 2;
		factor *= parameters.ATTACK_RANGE;
		return Vector.add(pos, 
				(new Vector(Math.sin(dir),Math.cos(dir))).scale(factor));
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
//			boolean military_is_here = false;
//			for(Ant a: visibleFriends()){
//				if (a.caste == self.caste){
//					if (a.id < self.id) count++;
//				}
//				if (a.caste == Caste.Soldier) {
//					military_is_here = true;
//				}
//			}
//			if (military_is_here) count++;
//			if (count < HillAI.GUARDLENGTH && !visibleEnemies().isEmpty()) {
//				Ant enemy = getWeakestReachable(visibleEnemies());
//				if (enemy!=null) attack(enemy); 
//				int num_hills = hills[HillAI.HILL_IND].size();
//				if (num_hills > 1) {
//					Snapshot hill = hills[HillAI.HILL_IND].get(self.id % (num_hills-1) + 1).s;
//					Vector target = Vector.add(sugar.getPosition(),vectorBetween(self, hill).scaleTo(parameters.ATTACK_RANGE * HillAI.COMBAT_RADIUS));
//					moveToward(target);
//				} 
//			} else
			if (count < HillAI.QUEUELENGTH && (!got_hit_with_sugar || visibleEnemies().isEmpty())) {
				moveToward(sugar);
				pickUpSugar(sugar);
			} else if (count < HillAI.WAITLENGTH + time * HillAI.TIME_FACTOR) { //wait till you can go into queue
				Vector target = shift_aside(sugar.getPosition());
				moveToward(target);
				Ant enemy = getWeakestReachable(visibleEnemies());
				if (enemy!=null) attack(enemy); 
			} else{
				//go somewhere else:
				for(ListIterator<SnapshotMessagePair> it = hills[HillAI.SUGAR_IND].listIterator();
						it.hasNext() ; ) {
					SnapshotMessagePair snap = it.next();
					if (snap.s.hasSameOriginal(target_sugar)) {
						if (it.hasNext()){
							target_sugar = it.next().s;
						} else {
							target_sugar = hills[HillAI.SUGAR_IND].getFirst().s;
						}
						moveToward(target_sugar);
						break;
					}
				}
			}
		} else {
			Ant enemy = getWeakest(visibleEnemies());
			moveToward(shift_aside(enemy.position));
			attack(enemy);
		}
	}
	
	public Snapshot find_optimal_sugar_hill(Snapshot except){
		if (hills[HillAI.SUGAR_IND].size()==0) return null;
		
		//if (hills.get(QueenAI.SUGAR_IND).size()==1) return hills.get(QueenAI.SUGAR_IND).get(0);
		Snapshot best_sugar = hills[HillAI.SUGAR_IND].get(0).s;
		if (best_sugar.hasSameOriginal(except)) best_sugar = null;
			
		for(SnapshotMessagePair sugar: hills[HillAI.SUGAR_IND]){
			if (!sugar.s.hasSameOriginal(except)){
				if (walking_distance(best_sugar) > walking_distance(sugar.s)){
					best_sugar = sugar.s;
				}
			}
		}
		return best_sugar;
	}
	
	public double walking_distance(Snapshot sugarhill){
		if (sugarhill == null) return Double.MAX_VALUE;
		double d = vectorBetween(self, sugarhill).length() / self.caste.SPEED;
		d += vectorBetween(sugarhill, hills[HillAI.HILL_IND].getFirst().s).length()
				/ self.caste.SPEED_WHILE_CARRYING_SUGAR;
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
