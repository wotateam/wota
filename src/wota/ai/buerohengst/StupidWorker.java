/**
 * 
 */
package wota.ai.buerohengst; /* <-- change this to wota.ai.YOUR_AI_NAME
 							  * make sure your file is in the folder /de/wota/ai/YOUR_AI_NAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import wota.gamemaster.AIInformation;
import wota.gameobjects.*;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Put a describtion of you AI here.
 */
// Here, you may use spaces, etc., unlike in the package path wota.ai.YOUR_AI_NAME:
@AIInformation(creator = "Pascal", name = "StupidWorker")
public class StupidWorker extends AntAI {
	
	enum State {WaitForMessage, LookForSugar, CollectSugar, BringSugarBack};
	
	public static final int SUGAR_IS_THERE = 0;
	public static final int SUGAR_IS_NOT_THERE = 1;
	public static final int AWAITING_ORDERS = 2;
	
	private State state = State.WaitForMessage;
	private Sugar sugarTarget;
	private int randomDirection;
	
	@Override
	public void tick() throws Exception {
		switch(state) {
		case CollectSugar: tickCollectSugar(); break;
		case LookForSugar: tickLookForSugar(); break;
		case WaitForMessage: tickWaitForMessage(); break;
		case BringSugarBack: tickBringSugarBack(); break;
		}
	}

	boolean hasAskedForOrders = false;
	
	public void tickWaitForMessage() throws Exception {
		if ( !hasAskedForOrders ) {
			talk(AWAITING_ORDERS);
			hasAskedForOrders = true;
		}
		
		for (Message message : audibleMessages) {
			if (message.sender.caste != Caste.Queen) {
				continue;
			}
			if (message.content != self.id) {
				continue;
			}
			// message is determined for this ant
			sugarTarget = message.contentSugar;
			if (sugarTarget == null) {
				state = State.LookForSugar;
				randomDirection = SeededRandomizer.getInt(360);
				moveInDirection(randomDirection);
			}
			else {
				state = State.CollectSugar;
			}
		}
	}
	
	public void tickLookForSugar() throws Exception {
		if (visibleSugar.size() == 0) {
			moveAhead();
		}
		else {
			sugarTarget = visibleSugar.get(0);
			state = State.CollectSugar;
		}
	}
	
	private void reportExistantSugar(Sugar sugar) {
		talk(SUGAR_IS_THERE, sugar);
	}
	
	private void reportNonExistantSugar(Sugar sugar) {
		talk(SUGAR_IS_NOT_THERE, sugar);
	}
	
	private void tickCollectSugar() throws Exception {
		if (self.sugarCarry != 0) {
			state = State.BringSugarBack;
		}
		else {
			pickUpSugar(sugarTarget);
			moveToward(sugarTarget);
		}
	}
	
	private void tickBringSugarBack() {
		moveHome();
		if (self.sugarCarry == 0) {
			state = State.WaitForMessage;
			hasAskedForOrders = false;
			Hill hill = getOwnHill();
			if (hill != null) {
				reportExistantSugar(sugarTarget);
			}
			else {
				System.out.println("Hill should be visible here");
			}
		}
	}

	/** returns own queen or null if not visible */
	private Hill getOwnHill() {
		for (Hill hill : visibleHills) {
			if (hill.playerID == self.playerID) {
				return hill;
			}
		}
		return null; 
	}
	
}
