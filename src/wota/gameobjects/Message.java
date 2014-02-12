/**
 * 
 */
package wota.gameobjects;

import wota.utility.Vector;

/**
 * Base class for Messages. 
 * Does not contain a sender since this is specified in 
 * derived classes AntMessage or HillMessage. 
 */
public abstract class Message extends GameObject {
	
	/** information carried by the message */
	public final int content;
	
	/** Snapshot is one is transfered as message content 
	 *  If this is an Ant/Sugar/Hill the corresponding field
	 *  contentAnt/Sugar/Hill will be set so you don't have to
	 *  typecast. */
	public final Snapshot contentSnapshot; 
	
	/** Ant if one is tranfered as message content */
	public final Ant contentAnt;
	
	/** Sugar if one is tranfered as message content */
	public final Sugar contentSugar;	
	
	/** Hill if one is tranfered as message content */
	public final Hill contentHill;
	
	public Message(Vector position, int content, Snapshot snapshot, Parameters parameters) {
		super(position, parameters);
		this.content = content;
		this.contentSnapshot = snapshot;
		
		if (contentSnapshot instanceof Ant) {
			contentAnt = (Ant) contentSnapshot;
			contentSugar = null;
			contentHill = null;
		}
		else if (contentSnapshot instanceof Sugar) {
			contentAnt = null;
			contentSugar = (Sugar) contentSnapshot;
			contentHill = null;
		}
		else if (contentSnapshot instanceof Hill) {
			contentAnt = null;
			contentSugar = null;
			contentHill = (Hill) contentSnapshot;
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
