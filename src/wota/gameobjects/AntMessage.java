package wota.gameobjects;

/** like AntMessageObject, but only contains the information which can
 *  be visible to other ants. 
 */
public class AntMessage extends Message {

	/** Ant which sends this message */
	public final Ant sender;
	
	public AntMessage(AntMessageObject messageObject) {
		super(messageObject);
		this.sender = messageObject.sender;
	}
	
	@Override
	public String toString() {
		return new String(sender + ": " + super.toString());
	}
}
