import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Flight {
	
	private int flightID;
	private FlightDate departure_time;
	private FlightDate arrive_time;
	private Location source;
	private Location destination;
	private int capacity;
	private int booked_num;
	
	
    public Flight(int flightID, int day, String time, Location source, Location destination, int capacity, int booked_num) {
		this.flightID = flightID;
		this.source = source;
		this.destination = destination;
		this.capacity = capacity;
		this.booked_num = booked_num;
			
		this.departure_time = normalizeDepature(day,time);	
		int min = getDuration();
		int [] durations= covertDuration(min);		
		this.arrive_time = normalizeArrive(day,time,durations);
		
		source.addDeparture(this);
		destination.addArrival(this);
	}
    
    public FlightDate normalizeDepature(int day, String time) {		
		String[] t = time.split(":");
		int hour = Integer.parseInt(t[0]);
		int min = Integer.parseInt(t[1]);
		return new FlightDate(min,hour,day);
	}
 
    public FlightDate normalizeArrive(int day,String time, int[] durations) {
   	
  		int minute = durations[1];
  		int hour = durations[0];		
		String[] t = time.split(":");
		int addhour = Integer.parseInt(t[0]);
		int addmin =Integer.parseInt(t[1]);
		return FlightDate.calculateTime(day,hour,minute,addhour,addmin);		
	}
        
   public static int[] covertDuration(int min) {	   
		int hour = min/60;
	    int minute = min%60;
	    int [] duartion = { hour, minute};
	    return duartion;
	}
   
	public int getFlightID() {
		return flightID;
	}

	public FlightDate getDepature_date() {
		return this.departure_time;
	}
	public String getDepature_time() {
		return this.departure_time.toString();
	}
	
	public String getDepature_alltime() {
		return this.departure_time.showAllTime();
	}
	
	public FlightDate getArrive_date() {
		return this.arrive_time;
	}
	
	public String getArrive_time() {
		return this.arrive_time.toString();
	}
	
	public String getArrive_alltime() {
		return this.arrive_time.showAllTime();
	}

	public Location getSource() {
		return this.source;
	}

	public Location getDestination() {
		return this.destination;
	}

	public int getCapacity() {
		return this.capacity;
	}
	
	public int getBookNum() {
		return this.booked_num;
	}
	
	public void resetBookNum() {
		this.booked_num=0;		
	}

	public String getSourceName() {
		return source.getName();
	}
	
	public String getDestinationName() {
		return destination.getName();
	}
	
	//get the number of minutes this flight takes (round to nearest whole number)
    public int getDuration() {
    	double distance = this.getDistance();
    	double averageSpeed = 720;
    	int min = (int) Math.round(distance/averageSpeed*60);
    	return min;
    }

    //implement the ticket price formula
    public double getTicketPrice() {
    	double diff = this.destination.getDemand()- this.source.getDemand();
    	double inital = diff*4; 
    	double x = 0;
    	double y = 0;    	
    	if(this.capacity!=0)
    		x = Double.valueOf(this.booked_num)/ Double.valueOf(this.capacity);
    	if(x>0.7) {
    		y = (0.2/Math.PI)*Math.atan(20*x-14)+1;
    	}else if(x>0.5) {
    		y = x+0.3;
    	}else if(x>0) {
    		y = -0.4*x+1;
    	}else {	
    		y = 1;
    	}
    	double d = this.getDistance();
    	return y * (d/100)*(30+inital);   
    }

    //book the given number of passengers onto this flight, returning the total cost
    public double book(int num) {
    	double total = 0;
    	for(int i=0;i<num;i++) {
    		double t = this.getTicketPrice();
    		this.booked_num +=1;
    		total += t; 
    	}
    	return total;
    }
    //return whether or not this flight is full
    public boolean isFull() {
    	if(this.booked_num == this.capacity)
    		return true;
    	else
    		return false;
	}

    //get the distance of this flight in km
    public double getDistance() {	
		return Location.distance(this.source, this.destination);
	}

    //get the layover time, in minutes, between two flights
    public static int layover(Flight x, Flight y) {
    	FlightDate a = x.getArrive_date();
    	FlightDate d = y.getDepature_date();
    	int hour =0, min = 0, day = 0, duration = 0;
    	if(d.isGreaterThanOrEqualTo(a)) {
    		hour = d.hour - a.hour;
    		min = d.min - a.min;
    		day = d.day -a.day;
    		duration = day*60*24 + hour*60 + min;
    	}else {
    		hour = d.hour - a.hour;
    		min = d.min - a.min;
    		day = d.day -a.day+7; 			
    		duration = day*60*24 +  hour*60 + min;
    		//System.out.println("wrong");
    	}
    	//System.out.println(d.day + " "+ d.hour+":"+d.min + " - " + a.day + " "+ a.hour+":"+a.min  + "=" +duration);
    	return duration;
    }
 
}
