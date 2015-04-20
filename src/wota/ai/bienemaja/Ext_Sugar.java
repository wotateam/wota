package wota.ai.bienemaja;


import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.*;
import wota.utility.Modulo;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

public class Ext_Sugar extends AntAI {

	private Snapshot snap;
	private int timestamp=Integer.MIN_VALUE;
	private int ants=0;
	private int food=Integer.MAX_VALUE;
	private int soldiers=0;
	private double distance=0;
	private double opp_distance=0;
	private boolean visited=false;
	private boolean existence=true;
	public Ext_Sugar(Snapshot a) {
		snap=a;
		// TODO Auto-generated constructor stub
	}
	
	public Ext_Sugar(Snapshot a,int n) {
		snap=a;
		timestamp=n;
		// TODO Auto-generated constructor stub
	}
	public Ext_Sugar(Snapshot a, boolean bool, int b) {
		snap=a;
		timestamp=b;
		existence=bool;
		// TODO Auto-generated constructor stub
	}
	public Ext_Sugar(Snapshot a, int b, int c) {
		snap=a;
		timestamp=b;
		ants=c;
		// TODO Auto-generated constructor stub
	}
	public Ext_Sugar(Snapshot a,int b, int c, int d) {
		snap=a;
		timestamp=b;
		ants=c;
		soldiers=d;
		// TODO Auto-generated constructor stub
	}
	
	public int getfood(){
		return food;
	}
	public void setfood(int a){
		food=a;
	}
	public double getdistance(){
		return distance;
	}
	public void setdistance(double a){
		distance=a;
	}
	public double getopp_distance(){
		return opp_distance;
	}
	public void setopp_distance(double	 a){
		opp_distance=a;
	}
	public int gettimestamp(){
		return timestamp;
	}
	
	public void settimestamp(int a){
		timestamp=a;
	}
	
	public Snapshot getsnapshot(){
		return snap;
	}

	public int getants(){
		return ants;
	}
	public int getsoldiers(){
		return soldiers;
	}
	public void setvisited(boolean b){
		visited=b;
	}
	public boolean getvisited(){
		return visited;
	}
	public void setexistence(boolean b){
		existence=b;
	}
	public boolean getexistence(){
		return existence;
	}
	public void setsoldiers(int a){
		soldiers=a;
	}
	
	public int getmaxants(){
		return 1+ (int) (3*snap.getPosition().length()/100);
	//	return 4;
	}
	

	public void setants(int a){
		ants=a;
	}

	@Override
	public void tick() throws Exception {
		// TODO Auto-generated method stub
		
	}


}
