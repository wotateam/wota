package wota.ai.bonnmath;

import wota.gameobjects.*;
import wota.utility.Vector;

class Haufen {

	private Vector position;
	private int amount;
	private int ants=0;
	private int soldiers=0;
	public Haufen(Vector vect,int a) {
		position=vect;
		amount=a;
		// TODO Auto-generated constructor stub
	}
	public Haufen(Vector vect,int a, int b) {
		position=vect;
		amount=a;
		ants=b;
		// TODO Auto-generated constructor stub
	}
	
	public Vector getPosition(){
		return position;
	}
	public int getamount(){
		return amount;
	}
	public int getants(){
		return ants;
	}
	public int getsoldiers(){
		return soldiers;
	}
	public void setsoldiers(int a){
		soldiers=a;
	}
	public int getmaxants(){
		return 1+ (int) (3*position.length()/100);
	//	return 4;
	}
	public void setamount(int a){
		amount=a;
	}

	public void setants(int a){
		ants=a;
	}

}
