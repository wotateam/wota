/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/**
 *  Like AntMessageObject but for messages send by the hill.
 */
public class HillMessageObject extends MessageObject {

	/** Hill which sends the message */
	public final Hill sender;

	/** Message instance which contains the information visible to other ants */
	private final HillMessage message;
	
	public HillMessageObject(Vector position, Hill sender, int content, Snapshot snapshot, Parameters parameters) {
		super(position, content, snapshot, parameters);
		this.sender = sender;
		
		message = new HillMessage(this);
	}
	
	/** returns Message instance which contains the information visible to other ants */
	public HillMessage getMessage() {
		return message;
	}
	
}
