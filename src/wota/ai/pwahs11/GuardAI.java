/**
 * 
 */
package wota.ai.pwahs11;
import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.Vector;

@AIInformation(creator = "pwahs", name = "pwahs' Guard 11")
public class GuardAI extends TalkingAntAI {
	
	Snapshot target_sugar = null;
	Snapshot target_hill = null;
	
	enum GuardAIStatus {
		WAITING_FOR_ORDERS,
		GUARDING,
		WAYLAYING
	}
	
	GuardAIStatus status = GuardAIStatus.WAITING_FOR_ORDERS;
	
	@Override
	public void tick() throws Exception {
		super.tick();
		do_stuff();
	}
	
	private void do_stuff() {
		switch(status) {
		case GUARDING:
			if (target_sugar == null) {
				int index = hills[HillAI.SUGAR_IND].size();
				if (index > 0) {
					target_sugar = hills[HillAI.SUGAR_IND].get(self.id % index).s;
				}
			}
			if (hills[HillAI.HILL_IND].size() > 1) {
				status = GuardAIStatus.WAYLAYING;
				do_stuff();
				break;
			}
			move_there_while_attacking(target_sugar==null ? null : target_sugar.getPosition());
			break;
		case WAYLAYING:
			Vector target = null;
			if (target_sugar == null) {
				int index = hills[HillAI.SUGAR_IND].size();
				if (index > 0) {
					target_sugar = hills[HillAI.SUGAR_IND].get(self.id % index).s;
					status = GuardAIStatus.GUARDING;
					do_stuff();
					break;
				}
			} else {
				if (hills[HillAI.HILL_IND].size() > 1) {
					Vector ant_street = vectorBetween(target_sugar, hills[HillAI.HILL_IND].get(1).s);
					double killing_time = Caste.Gatherer.INITIAL_HEALTH / (self.caste.ATTACK * parameters.VULNERABILITY_WHILE_CARRYING);
					double killing_distance = killing_time * Caste.Gatherer.SPEED_WHILE_CARRYING_SUGAR;
					if (ant_street.length() < killing_distance) killing_distance = ant_street.length() - Caste.Gatherer.SIGHT_RANGE; 
					target = Vector.add(target_sugar.getPosition(), 
								ant_street.scale(1 - killing_distance / ant_street.length()));
				}
			}
			move_there_while_attacking(target==null ? null : target);
			break;
		case WAITING_FOR_ORDERS:
			hit_enemies_with_sugar();
			for(SnapshotMessagePair sm: hills[HillAI.SUGAR_IND]) {
				if (sm.m.id == self.id) {
					target_sugar = sm.s;
					status = GuardAIStatus.GUARDING;
					do_stuff();
					break;
				}
			}
			break;
		default:
			break;
		}
	}
	
//	private boolean overwhelmed() {
//		double own_power = 0;
//		for(Ant ant: visibleFriends()) {
//			if (ant.sugarCarry == 0) own_power += ant.health * ant.caste.ATTACK;
//		}
//		double enemy_power = 0;
//		for(Ant ant: visibleEnemies()) {
//			if (ant.sugarCarry == 0) enemy_power += ant.health * ant.caste.ATTACK;
//		}
//		return (enemy_power > own_power);
//	}
	
	private void move_there_while_attacking(Vector target) {
		Ant enemy = getWeakestReachable(visibleEnemies());
		if (enemy != null) {
			moveToward(shift_aside(enemy.getPosition()));
			attack(enemy);
		} else {
			enemy = getWeakest(visibleEnemies());
			if (enemy != null && (target_sugar == null 
					|| vectorTo(target_sugar).length() < self.caste.SIGHT_RANGE)) {
				moveToward(shift_aside(enemy.getPosition()));
				attack(enemy);
			} else {
				if(target != null) {
					moveToward(shift_aside(target));
				}
			}
		}
	}
		
	private void hit_enemies_with_sugar() {
		if (visibleEnemies().size()>0 && self.caste != Caste.Scout){
			Ant enemy = getWeakest(visibleEnemies());
			if (enemy.sugarCarry>0 && self.sugarCarry == 0){
				moveToward(shift_aside(enemy.position));
				attack(enemy);
			}
		}
	}
	
	public Vector shift_aside(Vector pos){
		double factor = HillAI.COMBAT_RADIUS;
		factor *= parameters.ATTACK_RANGE;
		return Vector.add(pos, 
				(new Vector(Math.sin(dir),Math.cos(dir))).scale(factor));
	}
	
	@Override
	public void foundsugar(Snapshot sugar) {
		//do nothing		
	}
	
	@Override
	public void nosugar(Snapshot sugar){
		if (sugar.hasSameOriginal(target_sugar)) {
			target_sugar = null;
		}
	}
		
	public boolean gatherer_nearby(){
		for(Ant a: visibleFriends()){
			if (a.caste == Caste.Gatherer) return true;
		}
		for(Ant a: visibleEnemies()){
			if (a.sugarCarry > 0) return true;
		}
		return false;
	}
	
	public boolean smallerSoldierNearby() {
		for (Ant a: visibleFriends()){
			if (a.caste == self.caste && a.id < self.id) {
				return true;
			}
		}
		return false;
	}
}
