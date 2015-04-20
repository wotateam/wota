package wota.ai.pwahs11;

import wota.gameobjects.Snapshot;
import wota.utility.Vector;

public class MySnapshot implements Snapshot {
	Vector position;
	
	MySnapshot(Vector pos){
		position = pos;
	}
	
	@Override
	public boolean hasSameOriginal(Snapshot other){
		return false;
	}
	
	@Override
	public Vector getPosition(){
		return position;
	}
}

