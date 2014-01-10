/**
 * 
 */
package wota.gameobjects;

/**
 * Base class for messages. Used by HillMessage and AntMessage
 */
public abstract class Message {
	
	/** information carried by the message */
	public final int content;
	
	/** Ant if one is tranfered as message content */
	public final Ant contentAnt;
	
	/** Sugar if one is tranfered as message content */
	public final Sugar contentSugar;	
	
	/** Hill if one is tranfered as message content */
	public final Hill contentHill;
	
	public Message(MessageObject messageObject) {
		this.content = messageObject.content;
		
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
	
	/** Prints the message but does not contain sender. You can use AntMessage/HillMessage instead. */
	@Override
	public String toString() {
		return new String(content +
						  " Ant: " + this.contentAnt +
						  " Sugar: " + contentSugar +
						  " Hill: " + contentHill);
	}
	
}
