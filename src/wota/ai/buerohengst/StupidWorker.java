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
@AIInformation(creator = "Elrond1337", name = "StupidWorker")
public class StupidWorker extends AntAI {
	
	enum State {WaitForMessage, LookForSugar, CollectSugar,
		        BringSugarBack, ReportNonExistantSugar, AskForOrders};
	
	private State state = State.AskForOrders;
	private Sugar sugarTarget;
	private int randomDirection;
	
	@Override
	public void tick() throws Exception {
		switch(state) {
		case CollectSugar: tickCollectSugar(); break;
		case LookForSugar: tickLookForSugar(); break;
		case WaitForMessage: tickWaitForMessage(); break;
		case BringSugarBack: tickBringSugarBack(); break;
		case ReportNonExistantSugar: tickReportNonExistantSugar(); break;
		case AskForOrders: tickAskForOrders(); break;
		}
	}

	private void tickAskForOrders() {
		talk(HillAI.AWAITING_ORDERS);
		state = State.WaitForMessage;
	}

	private void tickReportNonExistantSugar() {
		moveHome();
		if (isAtHome()) {
			reportNonExistantSugar(sugarTarget);
			state = State.AskForOrders;
		}
	}
	
	public void tickWaitForMessage() throws Exception {
		HillMessage message = audibleHillMessage;

		if (message != null) {
			if (message.content != self.id) {
				return;
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
		else {
			return;
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
		talk(HillAI.SUGAR_IS_THERE, sugar);
	}
	
	private void reportNonExistantSugar(Sugar sugar) {
		talk(HillAI.SUGAR_IS_NOT_THERE, sugar);
	}
	
	/** goes to sugarTarget and picks it up. */
	private void tickCollectSugar() {
		if (self.sugarCarry != 0) {
			state = State.BringSugarBack;
		}
		else {
			if (isAt(sugarTarget)) {
				boolean found = false;
				for (Sugar sugar : visibleSugar) {
					if (sugar.hasSameOriginal(sugarTarget)) {
						found = true;
					}
				}
				if (!found) {
					state = State.ReportNonExistantSugar;
				}
				else {
					pickUpSugar(sugarTarget);
				}
			}
			else {
				moveToward(sugarTarget);
			}
		}
	}
	
	private void tickBringSugarBack() {
		moveHome();
		if (self.sugarCarry == 0) {
			state = State.AskForOrders;
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
	
	private boolean isAt(Snapshot target) {
		return vectorTo(target).length() < 1.e-6;
	}
	
	private boolean isAtHome() {
		return vectorToHome().length() < 1.e-6;
	}
}
