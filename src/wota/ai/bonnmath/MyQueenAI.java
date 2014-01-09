package wota.ai.bonnmath;

import java.util.LinkedList;

import wota.utility.Modulo;
import wota.utility.Vector;

public abstract class MyQueenAI extends wota.gameobjects.QueenAI {
	public int vecttoint(Vector position){
		return (int)(Math.round(parameters.SIZE_Y)*Math.round(position.x+parameters.SIZE_X/2)+Math.round(position.y+parameters.SIZE_Y/2));
	}
	public Vector torus(Vector v){
		while(v.x<-parameters.SIZE_X/2){
			v.x+=parameters.SIZE_X;
		}
		while(v.x>parameters.SIZE_X/2){
			v.x-=parameters.SIZE_X;
		}
		while(v.y<-parameters.SIZE_Y/2){
			v.y+=parameters.SIZE_Y;
		}
		while(v.y>parameters.SIZE_Y/2){
			v.y-=parameters.SIZE_Y;
		}
		return v;
	}
	
	public  Vector inttovect(int n){
		Vector position=new Vector(0,0);
		position.x= n/Math.round(parameters.SIZE_Y)-parameters.SIZE_X/2;
		position.y= Modulo.mod(n,(int)(Math.round(parameters.SIZE_Y)))-parameters.SIZE_Y/2;
		return position;
	}
	
	public int vecttotalk(Vector position, int amount){
		return 10*(vecttoint(position)+(int)(Math.round(parameters.SIZE_X)*Math.round(parameters.SIZE_Y)*amount));
	}
	
	public Haufen talktovect(int message){
		int amount=message/(int)(Math.round(parameters.SIZE_X)*Math.round(parameters.SIZE_Y)*10);
		message=message-amount*(int)(Math.round(parameters.SIZE_X)*Math.round(parameters.SIZE_Y)*10);
		message=message/10;
		Vector position=inttovect(message);
		return new Haufen(position,amount);
	}
	
	public int vorhandencheck(Haufen haufen, LinkedList<Haufen> haufenlist){
		int index=-1;
		for(int i=0; i<haufenlist.size(); i++){
			if(Vector.subtract(haufenlist.get(i).getPosition(),haufen.getPosition()).length()<1){
				index=i;
			}
		}
		return index;
	}
	
	public int insertionposition(Haufen haufen, LinkedList<Haufen> haufenlist){
		int index=0;
		for(int i=0; i<haufenlist.size(); i++){
			if(haufenlist.get(i).getPosition().length()<haufen.getPosition().length()){
				index=i;
			}
		}
		return index;
	}
	

}
