import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlightScheduler {

    private static FlightScheduler instance;
    private ArrayList<Flight> flightList = new ArrayList<Flight>();
    private ArrayList<Location> locationList =  new ArrayList<Location>();
    public int flightId = 0;
    
    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public FlightScheduler(String[] args) {
    	
    }
  
	//flight import <filename>
	public void importFlights(String[] command) {
		try {
			if (command.length < 3) throw new FileNotFoundException();
			BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
			String line;
			int count = 0;
			int err = 0;		
			while ((line = br.readLine()) != null) {
				String[] lparts = line.split(",");
				if (lparts.length < 4) {
					err++;
					continue;		
				}
				String[] dparts = lparts[0].split(" ");
				if (dparts.length < 2) {
					err++;
					continue;
				}
				int status = addFlight(dparts[0], dparts[1], lparts[1], lparts[2], lparts[3], lparts[4]);
				if (status < 0) {
					err++;
					continue;
				}
				count++;
			}
			br.close();
			System.out.println("Imported "+count+" flight"+(count!=1?"s":"")+".");
			if (err > 0) {
				if (err == 1) System.out.println("1 line was invalid.");
				else System.out.println(err+" lines were invalid.");
			}
		} catch (IOException e) {
			System.out.println("Error reading file.");
			return;
		}
	}
	
	//location import <filename>
	public void importLocations(String[] command) {
		try {
			if (command.length < 3) throw new FileNotFoundException();
			BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
			String line;
			int count = 0;
			int err = 0;
			while ((line = br.readLine()) != null) {
				String[] lparts = line.split(",");
				if (lparts.length < 4) continue;
								
				int status = addLocation(lparts[0], lparts[1], lparts[2], lparts[3]);
				if (status < 0) {
					err++;
					continue;
				}
				count++;
			}
			br.close();
			System.out.println("Imported "+count+" location"+(count!=1?"s":"")+".");
			if (err > 0) {
				if (err == 1) System.out.println("1 line was invalid.");
				else System.out.println(err+" lines were invalid.");
			}	
		} catch (IOException e) {
			System.out.println("Error reading file.");
			return;
		}
	}
	
	public static boolean isNumeric (String str) { 
		for (int i = str.length(); --i >=0;) {
			if (str.charAt(i)!='.' && str.charAt(i)!='-' && !Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	} 
	
	public Location getLocation(String location) {
		for(Location l: this.locationList) {
			if(l.getName().equalsIgnoreCase(location)) {
				return l;
			}
		}
		return null;
	}

	static int convertDay(String s) {
		if(s.equalsIgnoreCase("monday")) {
			return 1;
		}else if(s.equalsIgnoreCase("tuesday") ) {
			return 2;
		}else if(s.equalsIgnoreCase("wednesday")) {
			return 3;
		}else if(s.equalsIgnoreCase("thursday")) {
			return 4;
		}else if(s.equalsIgnoreCase("friday")) {
			return 5;
		}else if(s.equalsIgnoreCase("saturday")) {
			return 6;
		}else if(s.equalsIgnoreCase("sunday")) {
			return 7;
		}
		return 0;
	}
	
	public boolean checkLocationExist(String location) {
		for(Location l:this.locationList) {
			if(l.getName().equalsIgnoreCase(location)) {			
				return true;
			}
		}
		return false;
	}
	
	public boolean conflictCheck(Flight f) {
		Location source = f.getSource();
		Location destination = f.getDestination();	
		String flag1 = source.hasRunwayDepartureSpace(f);
		String flag2 = destination.hasRunwayArrivalSpace(f);
		if(flag1!=null) {
			System.out.println(flag1);
			source.removeFlight(f.getFlightID());
			destination.removeFlight(f.getFlightID());
			return true;
		}	
		if(flag2!=null) {
			System.out.println(flag2);
			source.removeFlight(f.getFlightID());
			destination.removeFlight(f.getFlightID());
			return true;
		}
		return false;
	}
	public int addFlight(String date1, String date2, String start, String end, String capacity, String booked) {
		
		if(!checkTime(date1,date2)) {
			return -1;
		}
		else if(!checkLocationExist(start)) {
			return -2;
		}
		else if(!checkLocationExist(end)){
			return -3;
		}
		else if(!isNumeric(capacity) || Integer.parseInt(capacity)<0) {
			return -4;
		}	
		else if(start.equalsIgnoreCase(end)) {
			return -5;
		}
		else {
			int num = Integer.parseInt(capacity);
			int id = this.flightList.size();	
			Location source = getLocation(start);
			Location destination = getLocation(end);
			int day = convertDay(date1);
			int booked_num=0;
			if(booked != null) {
				if(!isNumeric(booked) || Integer.parseInt(booked)<0)
					return -6;
				booked_num = Integer.parseInt(booked);
			}
			Flight f = new Flight(flightId,day,date2,source,destination, num,booked_num );
			if(conflictCheck(f)) return -7;
			this.flightList.add(f);
			flightId++;		
			Comparator<Flight> order  = new Comparator<Flight>() {
				public int compare(Flight obj1, Flight obj2) {
						FlightDate t1 = obj1.getDepature_date();
						FlightDate t2 = obj2.getDepature_date();
						if(t1.isEqualTo(t2)) {
							return obj1.getSourceName().compareTo(obj2.getSourceName());
						}else if(t1.isGreaterThan(t2)){
							return 1;
						}else {
							return -1;
						}
					}
				};
			Collections.sort(this.flightList, order);
			return id;
		}
	}
	
	static boolean checkTime (String str1, String str2) {
		if(!str1.equalsIgnoreCase("monday")&&!str1.equalsIgnoreCase("tuesday")&&!str1.equalsIgnoreCase("wednesday")
			&&!str1.equalsIgnoreCase("thursday")&&!str1.equalsIgnoreCase("friday")
			&&!str1.equalsIgnoreCase("saturday")&&!str1.equalsIgnoreCase("sunday")) 
		{
			return false;
		}
		SimpleDateFormat time=new SimpleDateFormat("HH:mm");
		try {
			time.setLenient(false);
			time.parse(str2);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	public void flightAdd(String[] command) {
		if(command.length < 7) {
			System.out.println("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\nExample: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
			return;
		}
		int status = addFlight(command[2],command[3],command[4],command[5],command[6],null);
		switch(status) {
			case -1:
				System.out.println("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
				break;
			case -2:
				System.out.println("Invalid starting location.");
				break;
			case -3:
				System.out.println("Invalid ending location.");
				break;
			case -4:
				System.out.println("Invalid positive integer capacity.");
				break;
			case -5:
				System.out.println("Source and destination cannot be the same place.");
				break;	
			case -6:
				System.out.println("Invalid number of passengers to book.");
				break;
			case -7:
				break;
			default:
				System.out.printf("Successfully added Flight %d.\n",flightId-1);
		}
	}
	
	public Flight getFlight(int id) {
		for(Flight f: this.flightList) {
			if(f.getFlightID() == id) {
				return f;
			}
		}
		return null;
	}
	
	public void flightID(String[] command) {	
		if(!isNumeric(command[1])) {
			System.out.println("Invalid Flight ID.");
			return;
		}
		int id = Integer.parseInt(command[1]);
		Flight f = getFlight(id);
		if(f==null) {
			System.out.println("Invalid Flight ID.");
			return;
		}			
		int [] durations= Flight.covertDuration(f.getDuration());
		int minute = durations[1];
		int hour = durations[0];
		String duration = hour+"h "+minute+"m";
		double distance = Math.round(f.getDistance());
		System.out.printf("Flight %d\n",id);
		System.out.printf("Departure:    %s %s\n",f.getDepature_time(),f.getSourceName());
		System.out.printf("Arrival:      %s %s\n", f.getArrive_time(),f.getDestinationName());
		System.out.printf("Distance:     %,.0fkm\n",distance);
		System.out.printf("Duration:     %s\n", duration);
		System.out.printf("Ticket Cost:  $%.2f\n", f.getTicketPrice());
		System.out.printf("Passengers:   %d/%d\n", f.getBookNum(),f.getCapacity());			
	}
	
	public void flightBook(String [] command) {
		int book_num=0;
		if(command.length==3) book_num=1;
		else if(!isNumeric(command[3])) {
			System.out.println("Invalid number of passengers to book.");
			return;
		}
		else {
			try {
				book_num = Integer.parseInt(command[3]);
			}
			catch (Exception e) {
				System.out.println("Invalid number of passengers to book.");
				return;
			}
		}		
		if(!isNumeric(command[1])) {
			System.out.println("Invalid Flight ID.");	
			return;
		}	
		int flight_id = Integer.parseInt(command[1]);
		Flight f = getFlight(flight_id );
		if(f==null) {
			System.out.println("Invalid Flight ID.");
			return;
		}
		if(book_num<0) {
			System.out.println("Invalid number of passengers to book.");
		}
		else if(f.getBookNum() + book_num >= f.getCapacity()) {
			System.out.printf("Booked %d passengers on flight %d for a total cost of $%.2f\n",f.getCapacity()-f.getBookNum(), flight_id, f.book(f.getCapacity()-f.getBookNum()));
		}
		else {
			System.out.printf("Booked %d passengers on flight %d for a total cost of $%.2f\n",book_num, flight_id, f.book(book_num));
		}	
		if(f.isFull()) System.out.println("Flight is now full.");
	}
	
	public void flightRemove(String[] command) {
		if(!isNumeric(command[1])) {
			System.out.println("Invalid Flight ID.");		
			return;
		}
		int flight_id = Integer.parseInt(command[1]);
		Iterator<Flight> iter = this.flightList.iterator();
	    while(iter.hasNext()) {
	    	Flight f = iter.next();
	    	if(f.getFlightID()==flight_id){
	    		Location start = f.getSource();
	    		Location end = f.getDestination();
	    		start.removeFlight(flight_id);
	    		end.removeFlight(flight_id);
	    		System.out.printf("Removed Flight %d, %s %s --> %s, from the flight schedule.\n", f.getFlightID(),f.getDepature_time(),f.getSourceName(),f.getDestinationName());
	            iter.remove();
	            return;
	    	}    
	    }
	    System.out.println("Invalid Flight ID.");	
	}
	
	public void flightReset(String[] command) {
		if(!isNumeric(command[1])) {
			System.out.println("Invalid Flight ID.");		
			return;
		}
		int flight_id = Integer.parseInt(command[1]);
		Flight f = getFlight(flight_id);
		if(f==null) {
			System.out.println("Invalid Flight ID.");
			return;
		}
		f.resetBookNum();
		System.out.printf("Reset passengers booked to 0 for Flight %d, %s %s --> %s.\n",flight_id,f.getDepature_time(),f.getSourceName(),f.getDestinationName());
	}
	
	public void exportFlights(String[] command) {
		try{
			if(command.length < 3) throw new FileNotFoundException();
			Comparator<Flight> orderByID = new Comparator<Flight>(){
				public int compare(Flight obj1, Flight obj2) {
					return Integer.compare(obj1.getFlightID(),obj2.getFlightID());
				}
			};
			Collections.sort(this.flightList,orderByID);	
			int count=0;
			File csv = new File(command[2]);
			BufferedWriter w = new BufferedWriter(new FileWriter(csv)); 
			for(Flight f: this.flightList) {
			    w.write(f.getDepature_alltime()+","+f.getSourceName()+","+f.getDestinationName()+","+f.getCapacity()+","+f.getBookNum());
			    w.newLine();
			    count++;
			}
			w.flush();	
		  	w.close();	
		  	System.out.println("Exported "+count+" flight"+(count!=1?"s":"")+".");
		}
		catch (IOException e){
        	System.out.println("Error writing file.");
	    }
	}
	
	public void flightOperation(String[] command) {
		if(command.length<2) {
			System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] [num]\nFLIGHT ADD <departure time> <from> <to> <capacity>\nFLIGHT IMPORT/EXPORT <filename>");
		}
		else if(command[1].equalsIgnoreCase("add")){
			flightAdd(command);
		}
		else if(command[1].equalsIgnoreCase("import")){
			importFlights(command);
		}
		else if(command[1].equalsIgnoreCase("export")){
			exportFlights(command);
		}
		else if(command.length>2 && command[2].equalsIgnoreCase("book")) {
			flightBook(command);
		}
		else if(command.length>2 && command[2].equalsIgnoreCase("remove")){
			flightRemove(command);
		}
		else if(command.length>2 && command[2].equalsIgnoreCase("reset")){
			flightReset(command);
		}
		else {
			flightID(command);
			//System.out.printf("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] [num]\nFLIGHT ADD <departure time> <from> <to> <capacity>\nFLIGHT IMPORT/EXPORT <filename>");
		}	
	}

	public void showFlights() {
		System.out.println("Flights");
		System.out.println("-------------------------------------------------------");
		System.out.println("ID   Departure   Arrival     Source --> Destination");
		System.out.println("-------------------------------------------------------");
		if(this.flightList.size()==0) {
			System.out.println("(None)");
		}else {
			for(Flight f:this.flightList) {
				System.out.printf("%4d %2s %11s   %s --> %s\n",f.getFlightID(),f.getDepature_time(),f.getArrive_time(),f.getSourceName(),f.getDestinationName());
			}
		}	
	}
	 Comparator<Location> compareByName  = new Comparator<Location>() {
	       public int compare(Location obj1, Location obj2) {
	            return obj1.getName().compareTo(obj2.getName());
	       }
	 };
	 
	// Add a location to the database
	// do not print out anything in this function
	// return negative numbers for error cases
	public int addLocation(String name, String lat, String lon, String demand) {		
		if(checkLocationExist(name)){
			return -1;
		}
		else if(!isNumeric(lat) || Double.parseDouble(lat)<-85 || Double.parseDouble(lat)>85) {
			return -2;
		}
		else if(!isNumeric(lon) || Double.parseDouble(lon)<-180 || Double.parseDouble(lon)>180) {
			return -3;
		}
		else if(!isNumeric(demand) || Double.parseDouble(demand)<-1 || Double.parseDouble(demand)>1) {
			return -4;
		}
		else {
			double latitude = Double.parseDouble(lat);
			double longtitude = Double.parseDouble(lon);
			double location_demand = Double.parseDouble(demand);
			Location l = new Location(name,latitude,longtitude,location_demand);
			this.locationList.add(l);
		    Collections.sort(this.locationList, compareByName);
			return 1;
		}
	}
	
	public void locationAdd(String[] command) {
		if(command.length < 6) {
			System.out.println("Usage:   LOCATION ADD <name> <lat> <long> <demand_coefficient>\nExample: LOCATION ADD Sydney -33.847927 150.651786 0.2");
			return;
		}
		int status = addLocation(command[2],command[3],command[4],command[5]);
		switch(status) {
			case 1:
				System.out.printf("Successfully added location %s.\n",command[2]);
				break;
			case -1:
				System.out.println("This location already exists.");
				break;
			case -2:
				System.out.println("Invalid latitude. It must be a number of degrees between -85 and +85.");
				break;
			case -3:
				System.out.println("Invalid longitude. It must be a number of degrees between -180 and +180.");
				break;
			case -4:
				System.out.println("Invalid demand coefficient. It must be a number between -1 and +1.");	
				break;
		}		
	}
		
	public boolean locationName(String[] command) {
		Location l = getLocation(command[1]);
		if(l==null) return false;		
		double d = l.getDemand();
		System.out.printf("Location:    %s\n",l.getName());
		System.out.printf("Latitude:    %.6f\n",l.getLat());
		System.out.printf("Longitude:   %.6f\n",l.getLon());
		if(d<0)
			System.out.printf("Demand:      %.4f\n",d);
		else
			System.out.printf("Demand:      +%.4f\n",d);	
		return true;	
	}
	
	public void exportLocations(String[] command) {		
		try{
			if (command.length < 3) throw new FileNotFoundException();
			Collections.sort(this.locationList, compareByName);
			int count =0;
			File csv = new File(command[2]);
			BufferedWriter w = new BufferedWriter(new FileWriter(csv)); 
			for(Location l: this.locationList) {
			    w.write(l.getName()+","+l.getLat()+","+l.getLon()+","+l.getDemand());
			    w.newLine();
			    count++;
			}
			w.flush();	
		  	w.close();	     
		  	System.out.println("Exported "+count+" location"+(count!=1?"s":"")+".");
		}
		catch (IOException e){
        	System.out.println("Error writing file.");
	    }
	}

	public void locationOperation(String[] command) {
		if(command.length<2) {
			System.out.println("Usage:\nLOCATION <name>\nLOCATION ADD <name> <latitude> <longitude> <demand_coefficient>\nLOCATION IMPORT/EXPORT <filename>");
		}
		else if(command[1].equalsIgnoreCase("add")){
			locationAdd(command);
		}
		else if(command[1].equalsIgnoreCase("import")){
			importLocations(command);
		}
		else if(command[1].equalsIgnoreCase("export")){
			exportLocations(command);
		}
		else if(!locationName(command)){
			System.out.println("Invalid location name.");
		}	
	}

	public void showLocations() {	
		System.out.printf("Locations (%d):\n",this.locationList.size());
		if(this.locationList.size()==0) {
			System.out.println("(None)");
		}else {
			for(int i=0; i<this.locationList.size();i++) {
				if(i==locationList.size()-1)
					System.out.printf("%s\n", locationList.get(i).getName());
				else 
					System.out.printf("%s, ", locationList.get(i).getName());
			}
		}
	}
	
	public void listDepartures(String[] command) {	
		if(command.length<2) {
			System.out.println("This location does not exist in the system.");
			return;
		}
		Location l = getLocation(command[1]);
		if(l==null) {
			System.out.println("This location does not exist in the system.");
			return;
		}	
		System.out.println(l.getName());
		ArrayList<Flight> flights = l.getDeparting();
		Comparator<Flight> flightOrderByDepart  = new Comparator<Flight>() {
	        public int compare(Flight obj1, Flight obj2) {
	        	FlightDate t1 = obj1.getDepature_date();
				FlightDate t2 = obj2.getDepature_date();
				if(t1.isGreaterThan(t2))
					return 1;
				else if(t1.isEqualTo(t2))
					return 0;
				else
					return -1;			            
	        }
	    };
	    Collections.sort(flights,flightOrderByDepart);
		System.out.println("-------------------------------------------------------");
		System.out.println("ID   Time        Departure/Arrival to/from Location");
		System.out.println("-------------------------------------------------------");
		for(Flight f:flights) {
			System.out.printf("%4d %2s   Departure to %s\n",f.getFlightID(),f.getDepature_time(),f.getDestinationName());
		}
	}
	
	public void listArrives(String[] command) {
		if(command.length<2) {
			System.out.println("This location does not exist in the system.");
			return;
		}
		Location l = getLocation(command[1]);
		if(l==null) {
			System.out.println("This location does not exist in the system.");
			return;
		}	
		System.out.println(l.getName());
		ArrayList<Flight> flights = l.getArriving();
		Comparator<Flight> flightOrderByArrive  = new Comparator<Flight>() {
	        public int compare(Flight obj1, Flight obj2) {
	        	FlightDate t1 = obj1.getArrive_date();
				FlightDate t2 = obj2.getArrive_date();
				if(t1.isGreaterThan(t2))
					return 1;
				else if(t1.isEqualTo(t2))
					return 0;
				else
					return -1;			            
	        }
	    };
		Collections.sort(flights,flightOrderByArrive);
		System.out.println("-------------------------------------------------------");
		System.out.println("ID   Time        Departure/Arrival to/from Location");
		System.out.println("-------------------------------------------------------");
		for(Flight f:flights) {
			System.out.printf("%4d %2s   Arrival from %s\n",f.getFlightID(),f.getArrive_time(),f.getSourceName());
		}
	}
	
	public void listAll(String[] command) {
		if(command.length<2) {
			System.out.println("This location does not exist in the system.");
			return;
		}
		Location l = getLocation(command[1]);
		if(l==null) {
			System.out.println("This location does not exist in the system.");
			return;
		}
		System.out.println(l.getName());	
		ArrayList<Flight> flights1 = l.getArriving();
		ArrayList<Flight> flights2 = l.getDeparting();
		ArrayList<Flight> flights = new ArrayList<Flight>();
		flights.addAll(flights1);
		flights.addAll(flights2);		
		Comparator<Flight> flightOrder  = new Comparator<Flight>() {
	        public int compare(Flight obj1, Flight obj2) {
	        	FlightDate t1A = obj1.getArrive_date();
				FlightDate t2A = obj2.getArrive_date();
				FlightDate t1D = obj1.getDepature_date();
				FlightDate t2D = obj2.getDepature_date();
				if(obj1.getDestinationName().equalsIgnoreCase(command[1]) && obj2.getDestinationName().equalsIgnoreCase(command[1])) {
					if(t1A.isGreaterThan(t2A)) return 1;
					else if(t1A.isEqualTo(t2A)) return 0;
					else return -1;		
				}else if (obj1.getSourceName().equalsIgnoreCase(command[1]) && obj2.getSourceName().equalsIgnoreCase(command[1])){
					if(t1D.isGreaterThan(t2D)) return 1;
					else if(t1D.isEqualTo(t2D)) return 0;
					else return -1;	
				}else if (obj1.getSourceName().equalsIgnoreCase(command[1]) && obj2.getDestinationName().equalsIgnoreCase(command[1])){
					if(t1D.isGreaterThan(t2A)) return 1;
					else if(t1D.isEqualTo(t2A)) return 0;
					else return -1;	
				}else if (obj1.getDestinationName().equalsIgnoreCase(command[1]) && obj2.getSourceName().equalsIgnoreCase(command[1])) {
					if(t1A.isGreaterThan(t2D)) return 1;
					else if(t1A.isEqualTo(t2D)) return 0;
					else return -1;	
				}else {
					return 0;
				}			            
	        }
		};
		Collections.sort(flights, flightOrder);		
		System.out.println("-------------------------------------------------------");
		System.out.println("ID   Time        Departure/Arrival to/from Location");
		System.out.println("-------------------------------------------------------");
		for(Flight f:flights) {
			if(f.getDestinationName().equalsIgnoreCase(command[1]))
				System.out.printf("%4d %2s   Arrival from %s\n",f.getFlightID(),f.getArrive_time(),f.getSourceName(),f.getDestinationName());
			else 
				System.out.printf("%4d %2s   Departure to %s\n",f.getFlightID(),f.getDepature_time(),f.getDestinationName());
		}
	}
	
	public void findPath(Location start, Location end, ArrayList<FlightPath> pathList) {
		ArrayList<Flight> f1 = start.getDeparting();
		for(Flight f:f1) {		
			FlightPath p1 =  new FlightPath();
			if(f.getDestinationName().equalsIgnoreCase(start.getName())) continue;
			p1.addFlight(f);
			Location l1 = f.getDestination();	
			if(l1.getName().equalsIgnoreCase(end.getName())) {
				pathList.add(p1);
				continue;
			}
			ArrayList<Flight> f2 = l1.getDeparting();
			for(Flight ff:f2) {	
				FlightPath p2 = (FlightPath)p1.clone();	
				if(ff.getDestinationName().equalsIgnoreCase(start.getName()) || p2.isVisited(ff.getDestinationName())) {
					continue;		
				}
				p2.addFlight(ff);				
				Location l2 = ff.getDestination();	
				if(l2.getName().equalsIgnoreCase(end.getName())) {
					pathList.add(p2);
					continue;
				}
				ArrayList<Flight> f3 = l2.getDeparting();
				for(Flight fff:f3) {		
					FlightPath p3 = (FlightPath)p2.clone();
					if(fff.getDestinationName().equalsIgnoreCase(start.getName()) || p3.isVisited(fff.getDestinationName())) {
						continue;
					}
					p3.addFlight(fff);
					Location l3 = fff.getDestination();	
					if(l3.getName().equalsIgnoreCase(end.getName())) {
						pathList.add(p3);
						continue;
					}
					ArrayList<Flight> f4 = l3.getDeparting();
					for(Flight ffff:f4) {		
						FlightPath p4 = (FlightPath)p3.clone();
						if(ffff.getDestinationName().equalsIgnoreCase(start.getName()) || p4.isVisited(ffff.getDestinationName())) {
							continue;
						}
						p4.addFlight(ffff);			
						Location l4 = ffff.getDestination();
						if(l4.getName().equalsIgnoreCase(end.getName())) {
							pathList.add(p4);
							continue;
						}
					}
				}
			}
		}
	}
	
	public void showTravel(String command,ArrayList<FlightPath> pathList) {
		if(isNumeric(command)) {
			int num = Integer.parseInt(command);
			if(num==1) {
				pathList.get(0).showPath();
			}
			else if(num > pathList.size()) {
				pathList.get(pathList.size()-1).showPath();
			}
			else {
				if(num<0) num=1;
				for(int i=0;i<num;i++) {
					pathList.get(i).showPath();	
					if(i!=num-1)
						System.out.println();
				}
			}	
		}
		else {
			System.out.println("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
		}	
	}
	
	public void travelOperation(String[] command) {
		if(command.length<3) {
			System.out.println("Usage: TRAVEL <from> <to> [cost/duration/stopovers/layover/flight_time]");
			return;
		}
		Location start = getLocation(command[1]);
		Location end = getLocation(command[2]);
		if(start==null) {
			System.out.println("Starting location not found.");
		}	
		else if(end==null) {
			System.out.println("Ending location not found.");
		}
		else if(command.length>=3) {
			ArrayList<FlightPath> pathList = new ArrayList<FlightPath>();				
			findPath(start,end,pathList);
			for(FlightPath p:pathList) {
				p.calculateLayout();
			}
			if(command.length==3) {
				Comparator<FlightPath> duration = new Comparator<FlightPath>(){
					public int compare(FlightPath p1, FlightPath p2) {					
						return Integer.compare(p1.getDuration() ,p2.getDuration());
					}
				};	
				Collections.sort(pathList, duration);
				if(pathList.size()==0) {
					String s = command[1].substring(0, 1).toUpperCase() + command[1].substring(1);
					String e = command[2].substring(0, 1).toUpperCase() + command[2].substring(1);
					System.out.printf("Sorry, no flights with 3 or less stopovers are available from %s to %s.\n",s,e);
					return;
				}
				pathList.get(0).showPath();	
			}
			else if(command[3].equalsIgnoreCase("duration")) {
//				System.out.println("duartion");	
				Comparator<FlightPath> duration = new Comparator<FlightPath>(){
					public int compare(FlightPath p1, FlightPath p2) {					
						return Integer.compare(p1.flightTime ,p2.flightTime);
					}
				};	
				Collections.sort(pathList, duration);
				if(command.length==4) pathList.get(0).showPath();	
				else showTravel(command[4],pathList);		
			}
			else if(command[3].equalsIgnoreCase("cost")) {	
				Comparator<FlightPath> cost = new Comparator<FlightPath>(){
					public int compare(FlightPath p1, FlightPath p2) {					
						return Double.compare(p1.cost ,p2.cost);
					}
				};	
				Collections.sort(pathList, cost);				
				if(command.length==4) pathList.get(0).showPath();	
				else showTravel(command[4],pathList);		
			}
			else if(command[3].equalsIgnoreCase("stopovers")) {		
				Comparator<FlightPath> cost = new Comparator<FlightPath>(){
					public int compare(FlightPath p1, FlightPath p2) {					
						return Integer.compare(p1.stops ,p2.stops);
					}
				};	
				Collections.sort(pathList, cost);				
				if(command.length==4) pathList.get(0).showPath();	
				else showTravel(command[4],pathList);		
			}
			else if(command[3].equalsIgnoreCase("layover")) {
				Comparator<FlightPath> cost = new Comparator<FlightPath>(){
					public int compare(FlightPath p1, FlightPath p2) {					
						return Integer.compare(p1.getLayover() ,p2.getLayover());
					}
				};	
				Collections.sort(pathList, cost);				
				if(command.length==4) pathList.get(0).showPath();	
				else showTravel(command[4],pathList);	
			}
			else if(command[3].equalsIgnoreCase("flight_time")) {
				Comparator<FlightPath> cost = new Comparator<FlightPath>(){
					public int compare(FlightPath p1, FlightPath p2) {					
						return Integer.compare(p1.flightTime ,p2.flightTime);
					}
				};	
				Collections.sort(pathList, cost);				
				if(command.length==4) pathList.get(0).showPath();	
				else showTravel(command[4],pathList);		
			}	
			else {
				System.out.println("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
			}		
		}
		else {
			System.out.println("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
		}		
	}
		
    public void run() {
        // Do not use System.exit() anywhere in your code,
        // otherwise it will also exit the auto test suite.
        // Also, do not use static attributes otherwise
        // they will maintain the same values between testcases.
    	
        // START YOUR CODE HERE
    	System.out.print("User: ");
    	Scanner scan = new Scanner(System.in);
    	while(scan.hasNextLine()) {
    		String input = scan.nextLine();
    		if(input.equalsIgnoreCase("exit")) {
    			System.out.println("Application closed.");
    			break;
    		}
    		else if(input.equalsIgnoreCase("help")){
    			System.out.printf("FLIGHTS - list all available flights ordered by departure time, then departure location name\n"
    					+ "FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight\n"
    					+ "FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file\n"
    					+ "FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price, capacity, passengers booked)\n"
    					+ "FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price, and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1 passenger. If the given number of bookings is more than the remaining capacity, only accept bookings until the capacity is full.\n"
    					+ "FLIGHT <id> REMOVE - remove a flight from the schedule\n"
    					+ "FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.\n\n"
    					+ "LOCATIONS - list all available locations in alphabetical order\n"
    					+ "LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location\n"
    					+ "LOCATION <name> - view details about a location (it's name, coordinates, demand coefficient)\nLOCATION IMPORT/EXPORT <filename> - import/export locations to csv file\n"
    					+ "SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart\nDEPARTURES <location_name> - list all departing flights, in order of departure time\n"
    					+ "ARRIVALS <location_name> - list all arriving flights, in order of arrival time\n\n"
    					+ "TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not provided, display the first one in the order. If n is larger than the number of flights available, display the last one in the ordering.\n\n"
    					+ "can have other orderings:\n"
    					+ "TRAVEL <from> <to> cost - minimum current cost\nTRAVEL <from> <to> duration - minimum total duration\nTRAVEL <from> <to> stopovers - minimum stopovers\nTRAVEL <from> <to> layover - minimum layover time\nTRAVEL <from> <to> flight_time - minimum flight time\n\n"
    					+ "HELP - outputs this help string.\nEXIT - end the program.\n");			
    		}
    		else {
        		String[] command = input.split(" ");
        		if(command[0].equalsIgnoreCase("flights")){
        			showFlights();
        		}
        		else if(command[0].equalsIgnoreCase("flight")){
        			flightOperation(command);
        		}
        		else if(command[0].equalsIgnoreCase("locations")){
        			showLocations();
        		}
        		else if(command[0].equalsIgnoreCase("location")){
        			locationOperation(command);
        		}
        		else if(command[0].equalsIgnoreCase("travel")){
        			travelOperation(command);
        		}
        		else if(command[0].equalsIgnoreCase("schedule")){
        			listAll(command);
        		}
        		else if (command[0].equalsIgnoreCase("departures")){
        			listDepartures(command);
        		}
        		else if(command[0].equalsIgnoreCase("arrivals")){
        			listArrives(command);
        		}
        		else {
        			System.out.println("Invalid command. Type 'help' for a list of commands.");
        		}		
    		}
			System.out.print("\nUser: ");			
    	}    	
    }
}
