package wota.ai.bonnmath;

import wota.gameobjects.Parameters;
import wota.utility.Vector;

public class MyVector extends Vector {
	
	protected Parameters parameters;
	
	public MyVector(Vector v) {
		super(v);
		// TODO Auto-generated constructor stub
	}
	
	public MyVector torus() {
		while(x<-parameters.SIZE_X/2){
			x+=parameters.SIZE_X;
		}
		while(x>-parameters.SIZE_X/2){
			x-=parameters.SIZE_X;
		}
		while(y<-parameters.SIZE_Y/2){
			y+=parameters.SIZE_Y;
		}
		while(y>-parameters.SIZE_Y/2){
			y-=parameters.SIZE_Y;
		}
		return new MyVector(this);
	}
}
