/**
 * 
 */
package wota.ai.pwahs05; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the
								}else{
									assignment = SwatStatus.ATTACKING;
								} same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.ai.pwahs05.HillAI.SwatStatus;
import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Put a description of you AI here.
 */
// Here, you may use spaces, etc., unlike in the package path wota.ai.YOUR_AI_NAME:
@AIInformation(creator = "Anonymous", name = "Anonymous's AI")
public class SoldierAI extends TalkingAntAI {
	
	Snapshot target = null;
	Snapshot hilltarget = null;
	boolean leading = false;
	
	boolean shouted = false;
	
	Ant leader = null;
	
	SwatStatus assignment = SwatStatus.ROAMING; 
	
	@Override
	public void tick() throws Exception {
				
		time++;
		
		if (leading) leadertick();
		else fightertick();
	}
	
	public void leadertick() throws Exception {
		
		first_cry();
		/*
		System.out.printf("Leader here, id = %d, dir = %f, time = 00%d, hills known: %d, sugar known: %d, target.position = %s\n",
				self.id, dir, time + initialTime, hills.get(HillAI.HILL_IND).size(),
				hills.get(HillAI.SUGAR_IND).size(), (target==null?"null":target.getPosition().toString()));
		*/
		if (time == 1) target = visibleHills.get(0);
		
		listen();
		
		shout();
		
		give_orders();
		
		if (target!=null) move();
		 
		attack();
	}
	
	public void fightertick() throws Exception {
		
		//System.out.printf("Fighter here, id = %d, time = 00%d, leaderid = %d\n",self.id, time+initialTime, (leader!=null?leader.id:-1));
		
		first_cry();
		
		if (time == 1) target = visibleHills.get(0);
		
		listen();
		
		shout();
		
		get_orders();

		if (target!=null) move();
		
		attack();
		
		check_promotion();
	}
	
	public Vector shift_aside(Vector pos){
		double factor = HillAI.COMBAT_RADIUS;
		factor *= parameters.ATTACK_RANGE;
		return Vector.add(pos, 
				(new Vector(Math.sin(dir),Math.cos(dir))).scale(factor));
	}
	
	public void move(){
		
		Vector pos = target.getPosition();
		if (!leading){
			pos = shift_aside(pos);
		}
		moveToward(pos);
		
	}
	
	public void attack(){
		
		Ant enemy = getWeakestReachable(visibleEnemies());
		if (enemy!=null) attack(enemy);
		
	}
	
	public void check_promotion(){		
		boolean see_leader = false;
		for(Ant ant: visibleFriends()){
			if (leader == null) break;
			if (leader.hasSameOriginal(ant)){
				see_leader = true;
			}
		}
		//System.out.printf("Check promotion... id = %d, leaderid = %d\n", self.id, (leader==null?-1:leader.id));
		if (!see_leader){
			if (shouted){
				int count = 0;
				for(AntMessage m: audibleAntMessages){
					if (m.content == HillAI.NEED_NEW_LEADER && m.sender.id < self.id) count++;
				}
				/*for(Ant ant: visibleFriends()){
					if (ant.id < self.id && ant.caste == Caste.Soldier) count++; 
				}*/
				if (count == 0) promote();
				shouted = false;
			}
			else{
				shouted = true;
				talk(HillAI.NEED_NEW_LEADER);
			}
		}
	}
	
	public void promote(){
		talk(HillAI.NEW_LEADER, leader);
		leader = null;
		leading = true;
	}
	
	public void get_orders(){
		for(AntMessage m: audibleAntMessages){
			
			switch(m.content){
			case HillAI.NEW_LEADER:
				if (leader == null){
					if(m.contentAnt == null) leader = m.sender;
				} else if (leader.hasSameOriginal(m.contentAnt)){
					leader = m.sender;
				}
				break;
			case HillAI.NEW_TARGET:
				if (leader != null){
					if (leader.hasSameOriginal(m.sender)){
						target = m.contentAnt;
						if (target == null) {
							target = m.contentHill;
							if (target != null){
								if (target.hasSameOriginal(hills.get(HillAI.HILL_IND).get(0))){
									assignment = SwatStatus.WAITING;
								}else{
									assignment = SwatStatus.ATTACKING;
								}
							}
						}
						if (target == null) {
							target = m.contentSugar;
							assignment = SwatStatus.PATROLLING;
							if (target == null) {
								target = m.contentSnapshot;
								assignment = SwatStatus.ROAMING;
							}
						}
					}
				}
				break;
			}
		}
	}
	
	@Override
	public void nosugar(Snapshot sugar){
		if (sugar.hasSameOriginal(hilltarget)) {
			hilltarget = null;
		}
	}
	
	public void give_orders(){
		switch(assignment){
		case PATROLLING:
	//		System.out.printf("id=%d, PATROLLING\n", self.id);
			if (!HillAI.contained(hills.get(HillAI.SUGAR_IND), target)){
				if (!target.hasSameOriginal(hills.get(HillAI.HILL_IND).get(0))){
					target_sugar();
				}else{
					if (vectorBetween(self, target).length() < self.caste.SIGHT_RANGE
						&& visibleEnemies().size()==0){
						target_sugar();
					}
				}
			}else{
				if (vectorBetween(self, target).length() < self.caste.SIGHT_RANGE) {
					if (smallerSoldierNearby()) {
						if (visibleEnemies().size()==0){
							target_sugar();
						} else {
							moveToward(getWeakest(visibleEnemies()));
							attack(getWeakestReachable(visibleEnemies()));
						}
					} else {
						assignment = SwatStatus.GUARDING;
						hilltarget = target;
					}
				}
			}
			break;
		case WAITING: //check if we should switch:
			System.out.printf("id=%d, WAITING\n", self.id);
			boolean hear_leader = false;
			if (time > 2){
				for(AntMessage m: audibleAntMessages){
					if ((m.content == HillAI.NEW_LEADER /*|| m.content == QueenAI.NEW_TARGET*/)
							&& m.sender.id!=self.id) {
						hear_leader = true;
						break;
					}
				}
				if (hear_leader){
					if (SeededRandomizer.nextDouble()<HillAI.SUGAR_PROB){
						assignment = SwatStatus.PATROLLING;
						target_sugar();
					}else{
						assignment = SwatStatus.ATTACKING;
						target_hill();
					}
				}else{	
					target_home();
				}
			}
			break;
		case ATTACKING:
			System.out.printf("id=%d, ATTACKING\n", self.id);
			if (!HillAI.contained(hills.get(HillAI.HILL_IND),target)){
				target_hill();
				hilltarget = target;
			}
			if (hilltarget!=null) if (vectorBetween(self,hilltarget).length() < self.caste.SIGHT_RANGE){
				assignment = SwatStatus.ARRIVING;
				target = new MySnapshot(shift_aside(target.getPosition()));
			}
			break;
		case ARRIVING:
			System.out.printf("id=%d, ARRIVING\n", self.id);	
			target = new MySnapshot(shift_aside(hilltarget.getPosition()));
			//just stay
			break;
		case ROAMING:
		//	System.out.printf("id=%d, ROAMING\n", self.id);
			target = null;
			if (hills.get(HillAI.SUGAR_IND).size() > 0) {
				assignment = SwatStatus.PATROLLING;
				target_sugar();
			} else {
				moveInDirection(dir);
				target = new MySnapshot(Vector.add(self.getPosition(), new Vector(Math.sin(dir),Math.cos(dir)).scaleTo(self.caste.SIGHT_RANGE)));
				//if (self.id==29) System.out.printf("ID=%d, dir=%f\n", self.id, dir);
				target_enemy();
			}
			break;
		case GUARDING:
	//		System.out.printf("ID=%d, GUARDING, dir=%f, hilltarget = %s\n", self.id, dir, hilltarget == null ? "null" : hilltarget.getPosition().toString());
			if (hilltarget == null) {
				assignment = SwatStatus.ROAMING;
				give_orders();
				return;
			}
			else{
				if (!HillAI.contained(hills.get(HillAI.SUGAR_IND), hilltarget)){
					assignment = SwatStatus.ROAMING;
					give_orders();
					return;
				} else {
					int num_hills = hills.get(HillAI.HILL_IND).size();
					if (num_hills>1) {
						Snapshot hill = hills.get(HillAI.HILL_IND).get(self.id % (num_hills-1) + 1);
						Vector target = Vector.add(hilltarget.getPosition(),vectorBetween(hilltarget, hill).scaleTo(parameters.ATTACK_RANGE * HillAI.COMBAT_RADIUS));
						moveToward(target);
				//		System.out.printf("target = %s\n", target==null?"null":target.toString());
					} else {
						moveToward(hilltarget);
						target = hilltarget;
					}
				}
			}
			break;
		}
		/*
		if (gatherer_nearby()) {
			switch (assignment) {
			case ATTACKING :
				if (HillAI.MAX_DISTANCE_TO_ATTACKED_HILL > vectorBetween(self, target).length())
					target_enemy();
				break;
			case GUARDING:
				if (visibleEnemies().size() > 0)
					if (HillAI.MAX_DISTANCE_TO_ATTACKED_HILL > vectorBetween(hilltarget, closest(visibleEnemies())).length())
						target_enemy();
				break;
			default:
				target_enemy();
				break;
			}
		}
		*/
		talk(HillAI.NEW_TARGET, target);
		
		//return;
	}
	
	public void target_sugar(){
		int num_sugar = hills.get(HillAI.SUGAR_IND).size();
		if (num_sugar==0) {
			target_home();
		}else{
			target = hills.get(HillAI.SUGAR_IND).get(
					SeededRandomizer.nextInt(num_sugar));
		}
	}
	
	public void target_hill(){
		int num_hills = hills.get(HillAI.HILL_IND).size();
		if (num_hills==1) {
			target_home();
		}else{
			target = hills.get(HillAI.HILL_IND).get(
					SeededRandomizer.nextInt(num_hills-1)+1);
			target = new MySnapshot(shift_aside(target.getPosition())); 
		}
	}
	
	public void target_home(){
		target = hills.get(HillAI.HILL_IND).get(0);
	}
	
	public void target_enemy(){
		if (visibleEnemies().size()>0){
			target = getWeakest(visibleEnemies());
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
