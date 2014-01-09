/**
 * 
 */
package wota.ai.pwahs03;

import java.security.Policy.Parameters;
import java.util.LinkedList;
import java.util.List;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;

import wota.gamemaster.AIInformation;
import wota.gameobjects.Ant;
import wota.gameobjects.Caste;
import wota.gameobjects.Message;
import wota.gameobjects.Snapshot;


/**
 * Scouts are doing a random walk with 100 ticks per step. They and the Gatherer share
 * all information on sugar hills, empty sugar hills and Ant hills of the enemy by continuously
 * shouting one of these informations. Then half the Gatherers go to the "optimal" sugar hill,
 * and the other half distribute themselves randomly among all sugar hills.
 * When they reach a hill, only some (QueenAI.QUEUELENGTH) ants queue in, 
 * the others form a circle around the hill and attack any enemy in reach. (always the weakest).
 * If an enemy attacks a gatherer which does not hold sugar, it runs away in the opposite direction.
 * If it does hold sugar, it runs away only if it would not be able to bring the sugar home before dying
 * (at current rate of attack.)
 * If it decides to run, it first drops the sugar.
 * 
 * Soldiers: They operate in small groups (SWAT teams), and have a leader each. Only the leader listen to the
 * information that is communicated by gatherers and scouts and decide, which target shall be chosen.
 * The other team members just listen to their designated leader. If the leader gets killed,
 * the soldier with smallest ID gets promoted. The leader is always in the center and the other team members form a circle
 * of radius parameters.ATTACK_RANGE * QueenAI.COMBAT_RADIUS, to avoid splash damage as much as possible.
 * Those SWAT teams have an assignment: Initially, they guard the home hill. As soon as a new team was created,
 * they either start patrolling the known sugar hills, or attacking the enemy hills.
 */
@AIInformation(creator = "Philipp", name = "pwahs03")
public class QueenAI extends wota.gameobjects.QueenAI {

	/*
	 * your Queen is not able to move but can
	 * communicate and create new ants. 
	 * 
	 * You can create new ants with				createAnt(caste, antAIClass)		
	 * e.g. if you want a gatherer and the AI
	 * you want use is called SuperGathererAI	createAnt(Caste.Gatherer, SuperGathererAI.class)
	 * 
	 */
	int time = 0;
	int antCount = 0;
	
	public static final int initTime = -10000;

	public static final int FIGHT = 1;
	public static final int number_fighters = 50;
	
	public static final int SUGAR_IND = 0;
	public static final int FULLSUGAR_IND = 1;
	public static final int NOSUGAR_IND = 2;
	public static final int HILL_IND = 3;
	public static final int NR_HILLS = 4;
	
	public static final int HILL_OFFSET = 3;
	public static final int SUGAR = HILL_OFFSET+SUGAR_IND;
	public static final int FULLSUGAR = HILL_OFFSET+FULLSUGAR_IND;
	public static final int NOSUGAR = HILL_OFFSET+NOSUGAR_IND;
	public static final int HILL = HILL_OFFSET+HILL_IND;
	
	public static final int SHOUT_LAST_NOSUGAR = 3;
	
	public static final int NEW_LEADER = 10;
	public static final int NEW_TARGET = 11;
	public static final int NEED_NEW_LEADER = 12;
	
	public static final double SUGAR_PROB = 0.6;
	
	public static final int QUEUELENGTH = 2;
	public static final int WAITLENGTH = 1000;
	
	public static final int STOP_LOOKING = 0;
	
	public static final int SWAT_TEAM_SIZE = 4;
	
	public static final double COMBAT_RADIUS = 0.9;
	public static final double EPS = 0.005;
	
	enum SwatStatus{
		GUARDING,
		PATROLLING,
		ATTACKING
	};
	
	@Override
	public void tick() throws Exception {
		time++;
		
		boolean fight = false;
		for(Message m: audibleMessages){
			if (m.content==QueenAI.FIGHT){
				fight = true;
			}
		}
		
		double myfood = visibleHills.get(0).food;
		
		while(myfood >= parameters.ANT_COST){
			if (antCount%10==3 && antCount>20){
				if (myfood >= QueenAI.SWAT_TEAM_SIZE*parameters.ANT_COST){
					for(int i=0; i < QueenAI.SWAT_TEAM_SIZE;i++){
						myfood-=parameters.ANT_COST;
						createAnt(Caste.Soldier, SoldierAI.class);
						antCount++;
					}
				} else break;
			}else{
				myfood -= parameters.ANT_COST;
				if (!fight && (antCount % 7 != 6 || antCount > 40)){
					createAnt(Caste.Gatherer, SugarAI.class);
				}else{
					createAnt(Caste.Scout, ScoutAI.class);
				}
				antCount++;
			}
			
		}

		
		talk(time + QueenAI.initTime);
		
	}
	
	public static boolean contained(LinkedList<Snapshot> list, Snapshot shot){
		for(Snapshot snap: list){
			if (snap.hasSameOriginal(shot)) return true;
		}
		return false;
	}
	
	public static boolean add_if_not_contained(LinkedList<Snapshot> list, Snapshot shot){
		if (!contained(list,shot)){
			list.addLast(shot);
			return true;
		}
		return false;
	}
	
	public static void delete(LinkedList<Snapshot> list, Snapshot shot){
		List<Snapshot> marked_for_deletion = new LinkedList<Snapshot>();
		for(Snapshot snap: list){
			if (snap.hasSameOriginal(shot)) marked_for_deletion.add(snap);
		}
		list.removeAll(marked_for_deletion);
	}
	
	public Ant getWeakest(List<Ant> enemies) {
		Ant weakest = null;
		double health = Double.MAX_VALUE;
		for (Ant enemy : enemies) {
			double factor = 1;
			if (enemy.sugarCarry > 0) factor = parameters.VULNERABILITY_WHILE_CARRYING;
			if (enemy.health * factor < health) {
				health = enemy.health * factor;
				weakest = enemy;
			}
		}
		return weakest;
	}

}
