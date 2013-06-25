package wota.gameobjects;

/** like MessageObject, but only contains the information which can
 *  be visible to other ants. 
 */
public class Message {
	/** information carried by the message */
	public final int content;
	
	/** Ant if one is tranfered as message content */
	public final Ant contentAnt;
	
	/** Sugar if one is tranfered as message content */
	public final Sugar contentSugar;	
	
	/** Hill if one is tranfered as message content */
	public final Hill contentHill;
	
	/** Ant which sends this message */
	public final Ant sender;
	
	public Message(MessageObject messageObject) {
		this.content = messageObject.getContent();
		this.sender = messageObject.getSender();
		
		if (messageObject.snapshot instanceof Ant) {
			contentAnt = (Ant) messageObject.snapshot;
			contentSugar = null;
			contentHill = null;
		}
		else if (messageObject.snapshot instanceof Sugar) {
			contentAnt = null;
			contentSugar = (Sugar) messageObject.snapshot;
			contentHill = null;
		}
		else if (messageObject.snapshot instanceof Hill) {
			contentAnt = null;
			contentSugar = null;
			contentHill = (Hill) messageObject.snapshot;
		}
		else {
			contentAnt = null;
			contentSugar = null;
			contentHill = null;
		}
	}
}
