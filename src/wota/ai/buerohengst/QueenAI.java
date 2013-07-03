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
@AIInformation(creator = "Pascal", name = "Bürohengst")
public class QueenAI extends wota.gameobjects.QueenAI {

	public static final int SUGAR_IS_THERE = 0;
	public static final int SUGAR_IS_NOT_THERE = 1;
	public static final int AWAITING_ORDERS = 2;
	public static final int ATTACK = 3;
	public static final int FOUND_LEADER = 4;
	
	private Queue<Sugar> knownSugar = new LinkedList<Sugar>();
	//private Map<Sugar, List<Ant> > antDivision;
	private Queue<Ant> awaitsOrders = new LinkedList<Ant>();
		
	@Override
	public void tick() throws Exception {
		if (visibleHills.get(0).food >= 6*parameters.ANT_COST) {
			createAnt(Caste.Soldier, SWAT_Leader.class);
			createAnt(Caste.Soldier, SWAT_Member.class);
			createAnt(Caste.Soldier, SWAT_Member.class);
			createAnt(Caste.Soldier, SWAT_Member.class);
			createAnt(Caste.Soldier, SWAT_Member.class);
			createAnt(Caste.Soldier, SWAT_Member.class);
		}
		
		for (Message message : audibleMessages) {
			if (message.sender.id == self.id) {
				continue;
			}
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
