/**
 * 
 */
package wota.ai.buerohengst;

import java.util.*;

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.SeededRandomizer;

/**
 *  Collects all information and organizes all Ants
 */
@AIInformation(creator = "Elrond1337", name = "Bürohengst")
public class HillAI extends wota.gameobjects.HillAI {

	public static final int SUGAR_IS_THERE = 0;
	public static final int SUGAR_IS_NOT_THERE = 1;
	public static final int AWAITING_ORDERS = 2;
	public static final int ATTACK = 3;
	public static final int FOUND_LEADER = 4;
	
	private Queue<Sugar> knownSugar = new LinkedList<Sugar>();
	//private Map<Sugar, List<Ant> > antDivision;
	private Queue<Ant> awaitsOrders = new LinkedList<Ant>();
		
	private Class<? extends AntAI> nextAnt = SWAT_Leader.class;
	
	@Override
	public void tick() throws Exception {
		if (nextAnt == SWAT_Leader.class) {
			if (self.food >= (SWAT_Leader.TEAM_SIZE+1)*parameters.ANT_COST) {
				createAnt(Caste.Soldier, SWAT_Leader.class);
				for (int i=0; i<SWAT_Leader.TEAM_SIZE; i++) {
					createAnt(Caste.Soldier, SWAT_Member.class);
				}
				nextAnt = StupidWorker.class;
			}
		} 
		else if (nextAnt == StupidWorker.class) {
			if (self.food >= 3*parameters.ANT_COST) {
				createAnt(Caste.Gatherer, StupidWorker.class);
				createAnt(Caste.Gatherer, StupidWorker.class);
				createAnt(Caste.Gatherer, StupidWorker.class);
			}
			nextAnt = SWAT_Leader.class;
		}
		
		for (AntMessage message : audibleAntMessages) {
			switch (message.content) {
			case AWAITING_ORDERS: 
				awaitsOrders.add(message.sender);
				break;
			case SUGAR_IS_THERE:
				if ( !containsSameOriginal(knownSugar, message.contentSugar)) {
					knownSugar.add(message.contentSugar);
				}
				break;
			case SUGAR_IS_NOT_THERE:
				if ( containsSameOriginal(knownSugar, message.contentSugar)) {
				knownSugar.remove(message.contentSugar);
				}
				break;
			case FOUND_LEADER:
				break;
			case ATTACK:
				break;
			default:
				System.out.println("unexpected message" + message);
			}
		}
		
		if ( !awaitsOrders.isEmpty() ) {
			Ant worker = awaitsOrders.poll();
			
			if ( !knownSugar.isEmpty() ) {
				Sugar sugar = knownSugar.peek();
				talk(worker.id, sugar);
			}
			else {
				talk(worker.id, null);
			}
		}
		
	}

	/** 
	 * returns true if list contains an element with the same original
	 * as snapshot
	 * @param list
	 * @param snapshot
	 * @return
	 */
	private static boolean containsSameOriginal(Queue<? extends Snapshot> list, Snapshot snapshot) {
		for (Snapshot snap : list) {
			if (snap.hasSameOriginal(snapshot)) {
				return true;
			}
		}
		return false;
	}
}
