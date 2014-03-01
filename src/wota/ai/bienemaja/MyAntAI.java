/**
 * 
 */
package wota.ai.bienemaja; /* <-- change this to wota.ai.YOURNAME
 							  * make sure your file is in the folder /de/wota/ai/YOURNAME
 							  * and has the same name as the class (change TemplateAI to
 							  * the name of your choice) 
 							  */

import java.util.LinkedList;
import java.util.List;
import wota.ai.bienemaja.*;
import wota.gameobjects.*;
import wota.utility.Modulo;
import wota.utility.SeededRandomizer;
import wota.utility.Vector;

/**
 * Put a describtion of you AI here.
 */
public abstract class MyAntAI extends AntAI {
	/* 
	 * tick() gets called in every step of the game.
	 * You have to call methods of AntAI to specify
	 * the desired action.
	 * 
 	 * you have access to
	 * the Ants you see: 								visibleAnts
	 * the sources of sugar you see: 					visibleSugar
	 * the Hills you see: 								visibleHills
	 * 
	 * you can move using one of the methods starting
	 * with move. For example to move ahead (in the
	 * direction of the last tick) call					moveAhead()
	 * 
	 * to attack other ants use methods starting with	attack(otherAnt)
	 * attack, e.g.		
	 * 
	 * if you want a List containing only the hostile	
	 * ants you can see, call							visibleEnemies()
	 * 
	 * communication is possible with					talk(content)
	 * where content is an integer value with is
	 * contained in the message
	 * 
	 * To measure the distance between two objects
	 * (you must be able to see both of them), call		vectorBetween(start, end).length()
	 * 
	 * to get information about yourself, for example
	 * your health points								self.health
	 * 
	 * to obtain random numbers use	SeededRandomizer
	 * e.g. a random elment of {0,1,2}					SeededRandomizer.getInt(3)
	 * 
	 * to iterate over a list (e.g. visibleAnts) use	for (Ant ant : visibleAnts) {
	 * 														// ant is an element of visibleAnts
	 * 													}
	 * 
	 * A full list of possible actions and how to get information is available at 
	 * doc/de/wota/gameobjects/AntAI.html
	 */

	/*
	 * Message:
	 * 11 einzufügender Haufen
	 * 12 löschender Haufen
	 * 13 einzufügender Hill
	 * 15 Zeit
	 */
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

	
	double direction=SeededRandomizer.getDouble()*360;
	double scal=7;
	int counter=0;
	int counterrem=5;
	int counterzero=5;
	int timeforsugar=Integer.MAX_VALUE;
	int countergetsugar=0;
	double memorytime=0;
	
	LinkedList<Ext_Sugar> sugarlist=new LinkedList<Ext_Sugar>();
	LinkedList<Ext_Sugar> hilllist= new LinkedList<Ext_Sugar>();
	Ext_Sugar mysugar=null;
	Ext_Sugar myhill=null;
	int time=Integer.MIN_VALUE;
	
	
	Ant atarget=self;	// sugar target in range/sight
	Ant btarget=self;	// nearest sugar target
	Ant ctarget=self;	// sugar target in sight
	Ant dtarget=self;	// no sugar target in sight
	
	
	Ant closenemy=self;
	Ant closfriend=self;
	int numbfriend=0;
	int numbenemy=0;
	int numbclosfriend=0;
	int numbclosenemy=0;
	int numbsoldiers=0;
	double alpha=3.0;
	
	void setneighbours(){
		atarget=self;	// sugar target in range
		btarget=self;	// no sugar target in range
		ctarget=self;	// best normal target
		dtarget=self;	// no sugar targe in sight
		
		closenemy=self;	//not set
		closfriend=self; 
		numbfriend=0;
		numbenemy=0;
		numbclosfriend=0;
		numbclosenemy=0;
		numbsoldiers=0;
		double mindist=parameters.ATTACK_RANGE;
		double mindist2=self.caste.SIGHT_RANGE;
		double mindist3=3*parameters.ATTACK_RANGE;
		double mindist4=self.caste.SIGHT_RANGE;
		
		double frienddist1=self.caste.SIGHT_RANGE;
		for(Ant ant: visibleAnts){
			if(ant.playerID!=self.playerID){
				if(ant.sugarCarry>0 && vectorTo(ant).length()<parameters.ATTACK_RANGE && (ant.health<atarget.health || atarget==self)){
					atarget=ant;
				}
				if(ant.sugarCarry>0 && vectorTo(ant).length()<mindist2){
					btarget=ant;
					mindist2=vectorTo(ant).length();
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<alpha*parameters.ATTACK_RANGE){
					if(ant.caste==Caste.Soldier) numbenemy+=2;
					if(ant.caste==Caste.Gatherer) numbenemy+=1;
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<parameters.ATTACK_RANGE){
					if(ant.caste==Caste.Soldier) numbclosenemy+=2;
					if(ant.caste==Caste.Gatherer) numbclosenemy+=1;
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<parameters.ATTACK_RANGE && (ant.health<ctarget.health || ctarget==self)){
					ctarget=ant;
					mindist2=vectorTo(ant).length();
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<mindist3 && (ant.health<dtarget.health || dtarget==self)){
					dtarget=ant;
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<mindist4){
					closenemy=ant;
					mindist4=vectorTo(ant).length();
				}
			}
			if(ant.playerID==self.playerID){
				if(vectorTo(ant).length()<frienddist1){
					closfriend=ant;
					frienddist1=vectorTo(ant).length();
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<alpha*parameters.ATTACK_RANGE){
					if(ant.caste==Caste.Soldier){
						numbfriend+=2;
						numbsoldiers+=1;
					}
					if(ant.caste==Caste.Gatherer) numbfriend+=1;
				}
				if(ant.sugarCarry==0 && vectorTo(ant).length()<parameters.ATTACK_RANGE){
					if(ant.caste==Caste.Soldier) numbclosfriend+=2;
					if(ant.caste==Caste.Gatherer) numbclosfriend+=1;
				}
			}
		}
	}
	
	
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
	
	public int getId(int a){
		return Modulo.mod(a/100,10);
	}
	
	public void insertheardhill(){
		HillMessage message=audibleHillMessage;
		if(message!=null){
			if(Modulo.mod(message.content,100)==15){
				time=message.content/100;
			}
			if(Modulo.mod(message.content,100)==11){
				Ext_Sugar sugar = new Ext_Sugar(message.contentSugar, Modulo.mod(message.content, 100));
				sugar.setdistance(torus(Vector.subtract(sugar.getsnapshot().getPosition(), myhill.getsnapshot().getPosition())).length());
				insert(sugar, sugarlist);
			}
			if(Modulo.mod(message.content,100)==13){
				Ext_Sugar sugar = new Ext_Sugar(message.contentSugar, Modulo.mod(message.content, 100));
				sugar.setexistence(false);
				if(mysugar!=null){
					if(torus(Vector.subtract(sugar.getsnapshot().getPosition(),mysugar.getsnapshot().getPosition())).length()<2*parameters.INITIAL_SUGAR_RADIUS)getnextsugar(1.);
				}
				sugar.setdistance(torus(Vector.subtract(sugar.getsnapshot().getPosition(), myhill.getsnapshot().getPosition())).length());
				insert(sugar, sugarlist);
			}
		}
	}
	
	public void insertheard(List<AntMessage> audibleAntMessages){
		for (AntMessage message : audibleAntMessages) {
			if(Modulo.mod(message.content,100)==15){
				time=message.content/100;
			}
			if(Modulo.mod(message.content,100)==11){
				Ext_Sugar sugar = new Ext_Sugar(message.contentSugar, Modulo.mod(message.content, 100));
				sugar.setdistance(torus(Vector.subtract(sugar.getsnapshot().getPosition(), myhill.getsnapshot().getPosition())).length());
				sugar.setfood(message.contentSugar.amount);
				insert(sugar, sugarlist);
			}
			if(Modulo.mod(message.content,100)==13){
				Ext_Sugar sugar = new Ext_Sugar(message.contentSugar, Modulo.mod(message.content, 100));
				sugar.setexistence(false);
				if(mysugar!=null){
					if(torus(Vector.subtract(sugar.getsnapshot().getPosition(),mysugar.getsnapshot().getPosition())).length()<2*parameters.INITIAL_SUGAR_RADIUS)getnextsugar(1);
				}
				sugar.setdistance(torus(Vector.subtract(sugar.getsnapshot().getPosition(), myhill.getsnapshot().getPosition())).length());
				insert(sugar, sugarlist);
			}
		}
	}
	

	
	public void insertseensugar(List<wota.gameobjects.Sugar> vissugarlist){
		for(Sugar sugar: vissugarlist){
			Ext_Sugar ext_sugar=new Ext_Sugar(sugar, time);
			ext_sugar.setfood(sugar.amount);
			ext_sugar.setdistance(torus(Vector.subtract(ext_sugar.getsnapshot().getPosition(), myhill.getsnapshot().getPosition())).length());
			insert(ext_sugar,sugarlist);
		}
		if(sugarlist.size()>0){
			for(Ext_Sugar ext_sugar: sugarlist){
				if(torus(Vector.subtract(ext_sugar.getsnapshot().getPosition(),self.getPosition())).length()<self.caste.SIGHT_RANGE-parameters.INITIAL_SUGAR_RADIUS){
					boolean seen=false;
					for(Sugar sugar: vissugarlist){
						if(torus(Vector.subtract(ext_sugar.getsnapshot().getPosition(),sugar.position)).length()<2*parameters.INITIAL_SUGAR_RADIUS){
							seen=true;
							ext_sugar.setfood(sugar.amount);
						}
					}
					
					if(seen==false){
						if(ext_sugar.getexistence()){
							ext_sugar.setexistence(false);
							if(mysugar!=null){
								if(torus(Vector.subtract(ext_sugar.getsnapshot().getPosition(),mysugar.getsnapshot().getPosition())).length()<2*parameters.INITIAL_SUGAR_RADIUS)getnextsugar(1);
							}
							ext_sugar.settimestamp(time);
						}
					}else{
						ext_sugar.settimestamp(time);
					}
				}
			//	if(time-ext_sugar.gettimestamp()>memorytime && ext_sugar.getexistence()==false) sugarlist.remove(ext_sugar);
			}
		}
	}

	
	public void insertseenhill(List<wota.gameobjects.Hill> vishilllist){
		for(Hill hill: vishilllist){
			Ext_Sugar ext_hill=new Ext_Sugar(hill, 0);
			if(myhill!=null)ext_hill.setdistance(torus(Vector.subtract(ext_hill.getsnapshot().getPosition(), myhill.getsnapshot().getPosition())).length());
			insert(ext_hill,hilllist);
		}
		if(hilllist.size()>0 && myhill==null) myhill=hilllist.get(0);
	}
	
	public double f(double x){
		double result=0;
		if(x>parameters.ATTACK_RANGE/self.caste.SIGHT_RANGE){
			result=x*x;
		}
		return result;
	}
	
	public double getdir(double des, double enem, double dist){
		double weight=1.-f(dist/self.caste.SIGHT_RANGE);
		double result=Modulo.mod(-weight*enem+(1.-weight)*des+6.*SeededRandomizer.getDouble()-3, 360.);
		if((Modulo.mod(result-enem, 360.)<90 || Modulo.mod(result-enem, 360.)>270) && dist<3*parameters.ATTACK_RANGE) result=Modulo.mod(result+180., 360.);
		return result;
	}
	
	public void getnextsugar(double temp){
		boolean notfound=true;
		int index=-1;
		double mindist=4*(parameters.SIZE_X+parameters.SIZE_Y);
		if(self.caste==Caste.Gatherer){
			for(int i=0; i<sugarlist.size();i++){
				if(sugarlist.get(i).getexistence()==true && sugarlist.get(i).getvisited()==false && torus(vectorTo(sugarlist.get(i).getsnapshot())).length()+torus(Vector.subtract(vectorTo(sugarlist.get(i).getsnapshot()),vectorToHome())).length()<mindist){
					mindist=torus(vectorTo(sugarlist.get(i).getsnapshot())).length()+torus(Vector.subtract(vectorTo(sugarlist.get(i).getsnapshot()),vectorToHome())).length();
					index=i;
					notfound=false;
				}
			}
		}else{
			for(int i=0; i<sugarlist.size();i++){
				if(sugarlist.get(i).getexistence()==true && sugarlist.get(i).getvisited()==false && torus(vectorTo(sugarlist.get(i).getsnapshot())).length()+torus(Vector.subtract(vectorTo(sugarlist.get(i).getsnapshot()),vectorToHome())).length()<mindist){
					mindist=torus(vectorTo(sugarlist.get(i).getsnapshot())).length()+torus(Vector.subtract(vectorTo(sugarlist.get(i).getsnapshot()),vectorToHome())).length();
					mysugar=sugarlist.get(i);
					index=i;
					notfound=false;
				}
			}
		}
		
		
		if(notfound){
			if(sugarlist.size()!=0){
				mysugar=null;
				for(int i=sugarlist.size()-1;i>=0;i--){
					sugarlist.get(i).setvisited(false);
					if(sugarlist.get(i).getexistence()==true){
						mysugar=sugarlist.get(i);
					}
				}
				
			}
		}else{
			int tries=0;
			boolean accepted=false;
			int n;
			while(tries<20 && accepted==false){
				tries++;
				n=SeededRandomizer.getInt(sugarlist.size());
		//		if(sugarlist.get(n).getexistence()==true && sugarlist.get(n).getvisited()==false && (SeededRandomizer.getDouble()<Math.exp(-temp*(torus(vectorTo(sugarlist.get(n).getsnapshot())).length()+torus(Vector.subtract(vectorTo(sugarlist.get(n).getsnapshot()),vectorToHome())).length()-mindist)*200/mindist/time))){
				if(sugarlist.get(n).getexistence()==true && sugarlist.get(n).getvisited()==false){
					accepted=true;
					index=n;
				}
			}
			sugarlist.get(index).setvisited(true);
			mysugar=sugarlist.get(index);
		}
	}
	
	public void getnexthill(){
		boolean notfound=true;
		for(int i=0; i<hilllist.size() & notfound;i++){
			if(hilllist.get(i).getvisited()==false){
				myhill=hilllist.get(i);
				hilllist.get(i).setvisited(true);
				notfound=false;
			}
		}
		if(notfound){
			for(int i=0;i<hilllist.size();i++){
				hilllist.get(i).setvisited(false);
				myhill=hilllist.get(0);
			}
		}
	}
	
	public void dowhatcanbedone(){
		if(memorytime==0){
			memorytime=(parameters.SIZE_X/(2*Caste.Scout.SPEED*Caste.Scout.SIGHT_RANGE)*parameters.SIZE_Y);
		}
		if(time!=Integer.MIN_VALUE){
			time++;
		}
		if(mysugar!=null){
			countergetsugar++;
		}
		if(SeededRandomizer.getDouble()<0.1) direction+=10*SeededRandomizer.getDouble();
		setneighbours();
		insertseenhill(visibleHills);
		insertseensugar(visibleSugar);
		insertheard(audibleAntMessages);
		insertheardhill();
		if(mysugar==null){
			getnextsugar(1);
			timeforsugar=Integer.MAX_VALUE;
		}else{	
			timeforsugar=(int)Math.round(1.3*mysugar.getdistance()*(1/self.caste.SPEED+1/self.caste.SPEED_WHILE_CARRYING_SUGAR));
		}
		if(mysugar!=null && torus(Vector.subtract(mysugar.getsnapshot().getPosition(),self.getPosition())).length()<self.caste.SIGHT_RANGE && numbfriend>parameters.STARTING_FOOD/parameters.ANT_COST/parameters.NUMBER_OF_PLAYERS/parameters.SUGAR_SOURCES_PER_PLAYER){
			counter++;
		}else{
			counter=0;
		}
		if(counter>=12 && SeededRandomizer.getDouble()<0.3){
			getnextsugar(0);
			counter=0;
		}
			
			/*if(countergetsugar>timeforsugar){
			getnextsugar(1);
			countergetsugar=0;
		}
		if(vectorToHome().length()<2*parameters.HILL_RADIUS) {
			countergetsugar=0;
		}
		*/
		
	}

	public double acceptance(int x){
		double s=1.;
		if(x<200) s=1.0;
		if(200<=x && memorytime>x) s=1.0-x/1000.;
		if(x>=memorytime) s=0.0;
		return s;
	}
	
	public void say(int message){
			if(message==0){
				if(SeededRandomizer.getDouble()<0.1 && hilllist.size()>1){
					Ext_Sugar ext_hill=hilllist.get(SeededRandomizer.getInt(hilllist.size()-1)+1);
					talk(12,ext_hill.getsnapshot());
				}else{
					if(sugarlist.size()>0){
						boolean accepted=false;
						int tries=0;
						int index=0;
						while(accepted==false && tries<20){
							index=SeededRandomizer.getInt(sugarlist.size());
							tries++;
							if(acceptance(time-sugarlist.get(index).gettimestamp())>SeededRandomizer.getDouble()){
								if(sugarlist.get(index).getfood()>parameters.INITIAL_SUGAR_IN_SOURCE/4 || sugarlist.get(index).getexistence()==false)
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
