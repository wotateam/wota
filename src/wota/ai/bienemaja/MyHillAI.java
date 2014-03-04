/**
 * 
 */
package wota.ai.bienemaja;

import java.util.LinkedList;
import java.util.List;

import wota.gameobjects.Ant;
import wota.gameobjects.AntMessage;
import wota.gameobjects.Caste;
import wota.gameobjects.Hill;
import wota.gameobjects.Message;
import wota.gameobjects.Sugar;
import wota.utility.Modulo;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 *
 */
public abstract class MyHillAI extends wota.gameobjects.HillAI  {

	/*
	 * your Queen is not able to move but can
	 * communicate and create new ants. 
	 * 
	 * You can create new ants with				createAnt(caste, antAIClass)		
	 * e.g. if you want a gatherer and the AI
	 * you want use is called SuperGathererAI	createAnt(Caste.Gatherer, SuperGathererAI.class)
	 * 
	 */
	int gatherer=0;
	int scout=0;
	int soldier=0;
	
	public Vector torus(Vector v){
		while(v.x< -parameters.SIZE_X/2){
			v.x+=parameters.SIZE_X;
		}
		while(v.x>parameters.SIZE_X/2){
			v.x-=parameters.SIZE_X;
		}
		while(v.y<-parameters.SIZE_Y/2){
			v.y+=parameters.SIZE_Y;
		}
		while(v.y> parameters.SIZE_Y/2){
			v.y-=parameters.SIZE_Y;
		}
		return v;
	}
	
	double scal=7;
	int counterrem=5;
	int counterzero=5;
	
	int temp=0;
	
	
	LinkedList<Ext_Sugar> sugarlist=new LinkedList<Ext_Sugar>();
	LinkedList<Ext_Sugar> hilllist= new LinkedList<Ext_Sugar>();
	Ext_Sugar mysugar=null;
	Ext_Sugar myhill=null;
	int time=0;
	
	
	
	int numbfriend=0;
	int numbenemy=0;
	int numbclosfriend=0;
	int numbclosenemy=0;
	
	void setneighbours(){
	
		numbfriend=0;
		numbenemy=0;
		numbclosfriend=0;
		numbclosenemy=0;
		for(Ant ant: visibleAnts){
			if(ant.playerID!=self.playerID){
				if(ant.sugarCarry==0){
					if(ant.caste==Caste.Soldier) numbenemy+=2;
					if(ant.caste==Caste.Gatherer) numbenemy+=1;
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<parameters.ATTACK_RANGE){
					if(ant.caste==Caste.Soldier) numbclosenemy+=2;
					if(ant.caste==Caste.Gatherer) numbclosenemy+=1;
				}
				
			}
			if(ant.playerID==self.playerID){
				if(ant.sugarCarry==0){
					if(ant.caste==Caste.Soldier) numbfriend+=2;
				//	if(ant.caste==Caste.Gatherer) numbfriend+=1;
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<parameters.ATTACK_RANGE){
					if(ant.caste==Caste.Soldier) numbclosfriend+=2;
				//	if(ant.caste==Caste.Gatherer) numbclosfriend+=1;
				}
			}
		}
	}
	
	
	double direction=SeededRandomizer.getDouble()*360;
	
	public int checkexistence(Ext_Sugar sugar, LinkedList<Ext_Sugar> list){
		int index=-1;
		for(int i=0; i<list.size(); i++){
			if(Vector.subtract(list.get(i).getsnapshot().getPosition(),sugar.getsnapshot().getPosition()).length()<2*parameters.INITIAL_SUGAR_RADIUS){
				index=i;
			}
		}
		return index;
	}
	
	public int insertionposition(Ext_Sugar sugar, LinkedList<Ext_Sugar> list){
		int index=0;
		if(list.size()==0){
			return index;
		}else{
			while(index<list.size() && list.get(index).getdistance()<sugar.getdistance()){
				index++;
			}
		}
		return index;
	}
		
	public void insert(Ext_Sugar sugar, LinkedList<Ext_Sugar> list){
		int index=checkexistence(sugar, list);
		if(index!=-1){
			if(sugar.gettimestamp()>list.get(index).gettimestamp()){
				sugar.setvisited(list.get(index).getvisited());
				list.set(index, sugar);
			}
		}else{
			index=insertionposition(sugar, list);
			list.add(index, sugar);
		}
	}
	

	
	
	
	public void insertheard(List<AntMessage> audibleAntMessages){
		for (AntMessage message : audibleAntMessages) {
			if(Modulo.mod(message.content,100)==11){
				Ext_Sugar sugar = new Ext_Sugar(message.contentSugar, Modulo.mod(message.content, 100));
				sugar.setdistance(torus(Vector.subtract(sugar.getsnapshot().getPosition(), self.getPosition())).length());
				insert(sugar, sugarlist);
			}
			if(Modulo.mod(message.content,100)==13){
				Ext_Sugar sugar = new Ext_Sugar(message.contentSugar, Modulo.mod(message.content, 100));
				sugar.setexistence(false);
				sugar.setdistance(torus(Vector.subtract(sugar.getsnapshot().getPosition(), self.getPosition())).length());
				insert(sugar, sugarlist);
			}
		}
	}
	

	

	
	
	public double f(double x){
		double result=0;
		if(x>parameters.ATTACK_RANGE){
			result=x*x;
		}
		return result;
	}
	


	
	public void dowhatcanbedone(){
		time++;
		if(SeededRandomizer.getDouble()<0.1) direction+=10*SeededRandomizer.getDouble();
		setneighbours();
		insertheard(audibleAntMessages);
	}
	
	public double acceptance(int x){
		double s=1.;
		if(x<100) s=1.0;
		if(100<=x && 1000>x) s=1.0-x/1000.;
		if(x>=1000) s=0.0;
		return s;
	}
	
	public void say(int message){
		if(time%2==0){
			talk(time*100+15);
		}else{
			if(message==0){
					if(SeededRandomizer.getDouble()<0.1 && hilllist.size()>1){
						Ext_Sugar ext_hill=hilllist.get(SeededRandomizer.getInt(hilllist.size()-1)+1);
						talk(13,ext_hill.getsnapshot());
					}else{
						if(sugarlist.size()>0){
							boolean accepted=false;
							int tries=0;
							int index=0;
							while(accepted==false && tries<20){
								tries++;
								index=SeededRandomizer.getInt(sugarlist.size());
								if(acceptance(time-sugarlist.get(index).gettimestamp())>SeededRandomizer.getDouble()){
									accepted=true;
								}
							}
							if(sugarlist.get(index).getexistence()){
								talk(100*sugarlist.get(index).gettimestamp()+11,sugarlist.get(index).getsnapshot());
							}else{
								talk(100*sugarlist.get(index).gettimestamp()+13,sugarlist.get(index).getsnapshot());
							}			
						}
					}	
				}else{
					talk(message);
				}
			}
	}
}
