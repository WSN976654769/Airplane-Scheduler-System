import java.util.ArrayList;
import java.util.Arrays;

public class FlightPath implements Cloneable{
	
	public ArrayList<Flight> path;
	public int stops;
	public double cost;
	public ArrayList<Integer> layover;
	public int flightTime;
	public int duration;
	
	
	public FlightPath() {
		this.path = new ArrayList<Flight>();
		this.stops = 0;
		this.cost =0;
		this.layover = new ArrayList<Integer>();
		this.flightTime =  0;
		this.duration = 0;
	}
	
	public void addFlight(Flight f) {
		this.path.add(f);
		this.flightTime += f.getDuration();
		this.duration+=f.getDuration();
		this.cost+= f.getTicketPrice();
		this.stops+=1;	
	}
	
	public void calculateLayout() {
		for(int i=0;i<this.path.size()-1;i++) {
			Flight current = path.get(i);
			Flight next = path.get(i+1);
			int min = Flight.layover(current, next);
			this.layover.add(min);
			this.duration+=min;
		}
	}
	
	public int getLayover() {
		//this.calculateLayout();
		int count = 0;
		for(int i=0;i<this.layover.size();i++) {
			count+=this.layover.get(i);
		}
		return count;
	}
	
	public int getDuration() {
		//this.calculateLayout();
		return this.duration;
	}
		
	public boolean isVisited(String dest) {
		for(Flight f:this.path) {
			if(f.getDestinationName().equalsIgnoreCase(dest))
				return true;
		}
		return false;
	}
	
	public void showPath() {	
		//this.calculateLayout();
		int[] time = Flight.covertDuration(this.duration);
		System.out.printf("Legs:%14d\n",this.stops);
		System.out.printf("Total Duration:   %dh %dm\n",time[0],time[1]);
		System.out.printf("Total Cost:       $%.2f\n",this.cost);
		System.out.println("-------------------------------------------------------------");
		System.out.println("ID   Cost      Departure   Arrival     Source --> Destination");
		System.out.println("-------------------------------------------------------------");	
		int index=0;
		for(Flight f:this.path) {	
			
			System.out.printf("%4d $%8.2f %s   %s   %s --> %s\n",f.getFlightID(),f.getTicketPrice(),f.getDepature_time(),f.getArrive_time(),f.getSourceName(),f.getDestinationName());
			if(index<this.layover.size()) {
				//System.out.println("++ "+this.layover.get(index));
				time = Flight.covertDuration(this.layover.get(index));
				System.out.printf("LAYOVER %dh %dm at %s\n",time[0],time[1],f.getDestinationName());
			}
			index++;
		}
	}
	
	public int getPathLength() {
		return this.path.size();
	}
	
	 @Override 
	 @SuppressWarnings("unchecked")
	 public Object clone() {  
		 FlightPath p = null;  
		 try{  
			 p = (FlightPath)super.clone();	
			 p.path = (ArrayList<Flight>) path.clone(); 
			 p.layover = (ArrayList<Integer>)layover.clone();   
		 }catch(CloneNotSupportedException e) {  
			 e.printStackTrace();  
		 }  
		 return p;  
	 }  
	
}
