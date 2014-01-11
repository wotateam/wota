/**
 * 
 */
package wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

/** 
 * Basisclass for hillAis by the user.
 */
public abstract class HillAI extends AI {
	// see Note [Visibility] in AntAI
	
	/** Reference to Hill itself */
	protected Hill self; // user AI may have changed this value! Use antObject instead.
	
	protected void createAnt(Caste caste, Class<? extends AntAI> antAIClass) {
		AntOrder antOrder = new AntOrder(caste, antAIClass);
		antOrders.add(antOrder);
	}
	
	/*
	 *  Pascal: The constructor must not have any parameters or
	 *  newInstance() will not work. 
	 */
	public HillAI() { 
		antOrders = new LinkedList<AntOrder>();
	}
	
	/** Send message of type int */
	protected void talk(int content) {
		talk(content, null);
	}
	
	/** Send message of combined int with Snaphshot (Ant, Hill, Sugar, ...) */
	protected void talk(int content, Snapshot snapshot) {
		message = new HillMessageObject(self.getPosition(), self, content, snapshot, parameters);
	}

	// ------------------------------------------------------------------------
	// End of methods and fields relevant to AI writers.
	// ------------------------------------------------------------------------
		

	/** List of Ants which should be born next tick */
	private List<AntOrder> antOrders;
	private HillMessageObject message;

	/** HillObject includes all information of this Hill */
	private HillObject hillObject;

	/** CAUTION! This will delete the message object in HillAI */
	HillMessageObject popMessage() {
		HillMessageObject returnMessage = message;
		message = null;
		return returnMessage;
	}
	
	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 */
	void setHill(Hill hill) {
		self = hill;
	}
	
	/** CAUTION! THIS METHOD DELETES THE AntOrders */
	List<AntOrder> popAntOrders() {
		List<AntOrder> returnAntOrders = antOrders;
		antOrders = new LinkedList<AntOrder>();
		return returnAntOrders;
	}

	void setHillObject(HillObject hillObject) {
		this.hillObject = hillObject;
		setPosition ( hillObject.getPosition() );
	}
}
