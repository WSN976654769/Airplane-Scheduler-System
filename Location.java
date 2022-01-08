import java.io.*;
import java.util.*;

public class Location {

	private String name;
	private double lat;
	private double lon;
	private ArrayList<Flight> departing;
	private ArrayList<Flight> arriving;
	private double demand;
	
	public Location(String name, double lat, double lon, double demand) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.departing = new ArrayList<Flight>(); 
		this.arriving = new ArrayList<Flight>(); 
		this.demand = demand;
	}
	
	public String getName() {
		return name;
	}

	public double getLat() {
		return lat;
	}


	public double getLon() {
		return lon;
	}

	public double getDemand() {
		return demand;
	}

	public ArrayList<Flight> getDeparting() {
		return departing;
	}

	public ArrayList<Flight> getArriving() {
		return arriving;
	}

	//Implement the Haversine formula - return value in kilometres
	// refer to  Wikipedia
    public static double distance(Location l1, Location l2) {
    	 // Radious of the earth
    	 Double latDiff = l2.getLat() - l1.getLat();
    	 Double lonDiff =  l2.getLon() - l1.getLon();
    	 Double latDistance = latDiff * Math.PI / 180;;
    	 Double lonDistance = lonDiff* Math.PI / 180;
    	 Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
    	 Math.cos((l1.getLat())*Math.PI / 180) * Math.cos(l2.getLat()* Math.PI / 180) * 
    	 Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    	 return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    public void addArrival(Flight f) {
    	this.arriving.add(f);
	}
	
	public void addDeparture(Flight f) { 
		this.departing.add(f);
	}
	
	public void removeFlight(int flight_id) {
		Iterator<Flight> iter1 = this.departing.iterator();
	    while(iter1.hasNext()) {
	    	Flight f = iter1.next();
	    	if(f.getFlightID()==flight_id){
	            iter1.remove();
	    	}    
	    }
	    Iterator<Flight> iter2 = this.arriving.iterator();
	    while(iter2.hasNext()) {
	    	Flight f = iter2.next();
	    	if(f.getFlightID()==flight_id){
	            iter2.remove();
	    	}    
	    }
	}
	
	
	/**
	 * Check to see if Flight f can depart from this location.
	 * If there is a clash, the clashing flight string is returned, otherwise null is returned.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's departure time.
	 * @param f The flight to check.
	 * @return "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>". Return null if there is no clash.
	 */
	public String hasRunwayDepartureSpace(Flight f) {
		FlightDate departure_time = f.getDepature_date();
		Flight conflictDepart = null;
		ArrayList<Flight> departs_source =  this.getDeparting();		
		for(Flight ff: departs_source) {
			if(f.getFlightID()!=ff.getFlightID() && departure_time.isConflict(ff.getDepature_date())) {
				//System.out.println("1");
				//System.out.printf("%s %s %s %d", f.getDepature_time(),f.getSourceName(),f.getDestinationName(),f.getCapacity());
				conflictDepart = ff;
				
				
			}
		}
		if(conflictDepart!=null)
			return "Scheduling conflict! This flight clashes with Flight "+ conflictDepart.getFlightID()+" departing from "+conflictDepart.getSourceName()+ " on "+conflictDepart.getDepature_alltime()+".";
		ArrayList<Flight> arrives_source = this.getArriving();	
		
		Flight conflictArrive = null;
		for(Flight ff: arrives_source) {
			if(f.getFlightID()!=ff.getFlightID() && departure_time.isConflict(ff.getArrive_date())) {
				//System.out.println("2");
				//System.out.printf("%s %s %s %s %d\n", f.getDepature_time(),f.getArrive_time(),f.getSourceName(),f.getDestinationName(),f.getCapacity());
				conflictArrive = ff;
				
			}
		}
		
		if(conflictArrive !=null)
			return "Scheduling conflict! This flight clashes with Flight "+ conflictArrive.getFlightID() + " arriving at " +conflictArrive.getDestinationName()+ " on "+conflictArrive.getArrive_alltime() + ".";
		
		return null;
	}

    /**
	 * Check to see if Flight f can arrive at this location.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's arrival time.
	 * @param f The flight to check.
	 * @return String representing the clashing flight, or null if there is no clash. Eg. "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>"
	 */
	public String hasRunwayArrivalSpace(Flight f) {
		FlightDate arrival_time =f.getArrive_date();
		
		Flight conflictDepart = null;
		ArrayList<Flight> departs_destination = this.getDeparting();		
		for(Flight ff: departs_destination) {
			if(f.getFlightID()!=ff.getFlightID() && arrival_time.isConflict(ff.getDepature_date())) {
				//System.out.println("3");
				conflictDepart = ff;		
			}
		}
		if(conflictDepart!=null)
			return "Scheduling conflict! This flight clashes with Flight "+conflictDepart.getFlightID()+" departing from "+conflictDepart.getSourceName()+" on "+conflictDepart.getDepature_alltime()+".";			
		
		Flight conflictArrive = null;
		ArrayList<Flight> arrives_destination = this.getArriving();	
		for(Flight ff: arrives_destination) {
			if(f.getFlightID()!=ff.getFlightID() && arrival_time.isConflict(ff.getArrive_date())) {
				//System.out.println("4");
				conflictArrive = ff;		
			}
		}
		if(conflictArrive!=null)
			return "Scheduling conflict! This flight clashes with Flight "+ conflictArrive.getFlightID() +" arriving at "+conflictArrive.getDestinationName()+" on "+conflictArrive.getArrive_alltime()+".";			
		return null;
    }
}
