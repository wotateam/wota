/**
 * 
 */
package wota.gameobjects;

/*
 * like HillMessageObject, but only contains the information which can
 * be visible to other ants. 
 */
public class HillMessage extends Message {

	/** Hill which sends this message */
	public final Hill sender;
	
	public HillMessage(HillMessageObject messageObject) {
		super(messageObject);
		this.sender = messageObject.sender;
	}
	
	@Override
	public String toString() {
		return new String(sender + ": " + super.toString());
	}
}
