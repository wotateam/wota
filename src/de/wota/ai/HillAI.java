package de.wota.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import de.wota.AntOrder;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.HillObject;


public abstract class HillAI extends BaseAI {
	protected Hill self;
	//private HillObject hillObject; // only to pass information to e.g. message objects
	private List<AntOrder> antOrders;
	
	protected void createAnt(Ant.Caste caste, Class<? extends AntAI> antAIClass) {
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
	
	/** CAUTION! USER AI MAY HAVE CHANGED THIS! */
	public Hill getHill() {
		return self;
	}
}
