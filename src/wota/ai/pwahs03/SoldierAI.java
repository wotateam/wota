/**
 * 
 */
package wota.ai.pwahs03; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the
								}else{
									assignment = SwatStatus.ATTACKING;
								} same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.ai.pwahs03.HillAI.SwatStatus;
import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

import java.util.List;
import java.util.LinkedList;


/**
 * Put a description of you AI here.
 */
// Here, you may use spaces, etc., unlike in the package path wota.ai.YOUR_AI_NAME:
@AIInformation(creator = "Anonymous", name = "Anonymous's AI")
public class SoldierAI extends AntAI {
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
	
	int time = 0;
	int initialTime = 0;
	double dir = -1;
	
	List<LinkedList<Snapshot>> hills = new LinkedList<LinkedList<Snapshot>>();
	int []indices = new int[HillAI.NR_HILLS];
	int nr_hill = 0;
	
	Snapshot target = null;
	boolean leading = false;
	
	boolean shouted = false;
	
	Ant leader = null;
	
	SwatStatus assignment = SwatStatus.GUARDING; 
	
	@Override
	public void tick() throws Exception {
				
		time++;
		
		if (leading) leadertick();
		else fightertick();
	}
	
	public void leadertick() throws Exception {
		
		/*System.out.printf("Leader here, id = %d, time = 00%d, hills known: %d, sugar known: %d\n",
							self.id, time + initialTime, hills.get(QueenAI.HILL_IND).size(),
							hills.get(QueenAI.SUGAR_IND).size());
		*/
		first_cry();
		
		listen();
		
		give_orders();
		
		if (target!=null) move();
		 
		attack();
	}
	
	public void fightertick() throws Exception {
		
		//System.out.printf("Fighter here, id = %d, time = 00%d, leaderid = %d\n",self.id, time+initialTime, (leader!=null?leader.id:-1));
		
		first_cry();
		
		listen();
		
		get_orders();

		if (target!=null) move();
		
		attack();
		
		check_promotion();
	}
	
	public void move(){
		
		Vector pos = target.getPosition();
		if (!leading){
			
			double factor = HillAI.COMBAT_RADIUS;
//			if (assignment == SwatStatus.ATTACKING){
//				factor *= self.caste.SIGHT_RANGE;
//			}else{
				factor *= parameters.ATTACK_RANGE;
//			}
			pos = Vector.add(pos, 
					(new Vector(Math.sin(dir),Math.cos(dir))).scale(factor));
		}
		moveToward(pos);
		
	}
	
	public void attack(){
		
		if (visibleEnemies().size() > 0){
			Ant enemy = getWeakest(visibleEnemies());
			attack(enemy);
		}
		
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
									assignment = SwatStatus.GUARDING;
								}else{
									assignment = SwatStatus.ATTACKING;
								}
							}
						}
						if (target == null) {
							target = m.contentSugar;
							assignment = SwatStatus.PATROLLING;
						}
					}
				}
				break;
			}
		}
	}
	
	public void give_orders(){
		switch(assignment){
		case PATROLLING:
			if (!HillAI.contained(hills.get(HillAI.SUGAR_IND), target)
				&& !HillAI.contained(hills.get(HillAI.FULLSUGAR_IND), target)){
				if (!target.hasSameOriginal(hills.get(HillAI.HILL_IND).get(0))){
					target_sugar();
				}else{
					if (vectorBetween(self, target).length() < self.caste.SIGHT_RANGE
						&& visibleEnemies().size()==0){
						target_sugar();
					}
				}
			}else{
				if (vectorBetween(self, target).length() < self.caste.SIGHT_RANGE
						&& visibleEnemies().size()==0){
					target_sugar();
				}
			}
			break;
		case GUARDING: //check if we should switch:
			boolean hear_leader = false;
			if (time > 50){
				for(AntMessage m: audibleAntMessages){
					if ((m.content == HillAI.NEW_LEADER /*|| m.content == QueenAI.NEW_TARGET*/)
							&& m.sender.id!=self.id) {
						hear_leader = true;
						break;
					}
				}
				if (hear_leader){
					if (random.getDouble()<HillAI.SUGAR_PROB){
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
			if (!HillAI.contained(hills.get(HillAI.HILL_IND),target)){
				target_hill();
			}
			break;
		}
		
		if (gatherer_nearby()) target_enemy();
		
		talk(HillAI.NEW_TARGET, target);
		
		//return;
	}
	
	public void target_sugar(){
		int num_sugar = hills.get(HillAI.SUGAR_IND).size();
		if (num_sugar==0) {
			target_home();
		}else{
			target = hills.get(HillAI.SUGAR_IND).get(
					random.getInt(num_sugar));
		}
	}
	
	public void target_hill(){
		int num_hills = hills.get(HillAI.HILL_IND).size();
		if (num_hills==1) {
			target_home();
		}else{
			target = hills.get(HillAI.HILL_IND).get(
					random.getInt(num_hills-1)+1);
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
	
	public void listen(){
		for(AntMessage m: audibleAntMessages){
			switch(m.content){
			case HillAI.SUGAR:
				if (!HillAI.contained(hills.get(HillAI.NOSUGAR_IND),m.contentSugar)){
					HillAI.add_if_not_contained(hills.get(HillAI.SUGAR_IND),m.contentSugar);
				}
				break;
			case HillAI.FULLSUGAR:
				if (!HillAI.contained(hills.get(HillAI.NOSUGAR_IND),m.contentSugar)){
					if (HillAI.add_if_not_contained(hills.get(HillAI.FULLSUGAR_IND),m.contentSugar)){
						HillAI.delete(hills.get(HillAI.SUGAR_IND),m.contentSugar);
					}
				}
				break;
			case HillAI.NOSUGAR:
				if (HillAI.add_if_not_contained(hills.get(HillAI.NOSUGAR_IND),m.contentSugar)){
					HillAI.delete(hills.get(HillAI.SUGAR_IND),m.contentSugar);
					HillAI.delete(hills.get(HillAI.FULLSUGAR_IND),m.contentSugar);
				}
				break;
			case HillAI.HILL:
				HillAI.add_if_not_contained(hills.get(HillAI.HILL_IND),m.contentHill);
				break;
			}
			//System.out.printf("%d: %d\n", self.id, m.content);
		}
	}
	
	public void first_cry(){
		if (time == 1){		//was just born, figure out time and directions:
			
			//find time:
			if (audibleHillMessage != null) {
				if (audibleHillMessage.content < 0) {
					initialTime = audibleHillMessage.content - HillAI.initTime;
				}
			}
			if (initialTime < 5) {
				//in the beginning, figure out direction:
				int count = 0;
				int total = 0;
				for(Ant a: visibleFriends()){
					if (a.caste == Caste.Soldier){
						total++;
						if (a.id < self.id) count++;
					}
				}
				
				dir = (360*count)/(total+1);
				moveInDirection(dir);
			}else{//later, just go some random direction:
				dir = random.getDouble()*360;
			}
			
			//initialize hills:
			for(int i = 0 ; i < HillAI.NR_HILLS ; ++i) {
				hills.add(new LinkedList<Snapshot>());
				indices[i] = 0;
			}
			
			//add my hill:
			for(Hill h: visibleHills){
				if (h.playerID == self.playerID){
					hills.get(HillAI.HILL_IND).add(visibleHills.get(0));					
				}
			}
			
			nr_hill = 0;
			
			target = visibleHills.get(0);
			
		}
	}
	
	public Ant getWeakest(List<Ant> enemies) {
		Ant weakest = null;
		double health = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			double factor = 1;
			if (enemy.sugarCarry > 0) factor = 1.0 / parameters.VULNERABILITY_WHILE_CARRYING;
			if (enemy.health * factor < health) {
				health = enemy.health * factor;
				weakest = enemy;
			}
		}
		return weakest;
	}
}
