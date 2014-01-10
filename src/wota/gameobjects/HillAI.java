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
	/** List of Ants which should be born next tick */
	private List<AntOrder> antOrders;
	private HillMessageObject message;
	
	/** Reference to Hill itself */
	protected Hill self; // user AI may have changed this value! Use antObject instead.
	
	/** HillObject includes all information of this Hill */
	private HillObject hillObject;

	protected void createAnt(Caste caste, Class<? extends AntAI> antAIClass) {
		AntOrder antOrder = new AntOrder(caste, antAIClass);
		antOrders.add(antOrder);
	}
	
	/** CAUTION! THIS METHOD DELETES THE AntOrders */
	public List<AntOrder> popAntOrders() {
		List<AntOrder> returnAntOrders = antOrders;
		antOrders = new LinkedList<AntOrder>();
		return returnAntOrders;
	}
	
	/*
	 *  Pascal: The constructor must not have any parameters or
	 *  newInstance() will not work. 
	 */
	public HillAI() { 
		antOrders = new LinkedList<AntOrder>();
	}
	
	void setHillObject(HillObject hillObject) {
		this.hillObject = hillObject;
		setPosition ( hillObject.getPosition() );
	}
	
	/** Send message of type int */
	protected void talk(int content) {
		talk(content, null);
	}
	
	/** Send message of combined int with Snaphshot (Ant, Hill, Sugar, ...) */
	protected void talk(int content, Snapshot snapshot) {
		message = new HillMessageObject(self.getPosition(), self, content, snapshot, parameters);
	}

	/** CAUTION! This will delete the message object in HillAI */
	HillMessageObject popMessage() {
		HillMessageObject returnMessage = message;
		message = null;
		return returnMessage;
	}
	
	/** 
	 * DON'T USE THIS METHOD IF YOU ARE PROGRAMMING THE AI !
	 */
	public void setHill(Hill hill) {
		self = hill;
	}
	
}
