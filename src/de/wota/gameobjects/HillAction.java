package de.wota.gameobjects;

import java.util.LinkedList;
import java.util.List;

import de.wota.gameobjects.AntOrder;

/** Abgeleitete Action Klasse für die HillAI. 
 *  
 * @author pascal
 *
 */
public class HillAction extends Action {
	private List<AntOrder> antOrders;
	
	public HillAction(AntOrder antOrder) {
		antOrders = new LinkedList<AntOrder>();
		antOrders.add(antOrder);
	}
	
	public void addOrder(AntOrder antOrder) {
		antOrders.add(antOrder);
	}
	
	public List<AntOrder> getAntOrders() {
		return antOrders;
	}
}
