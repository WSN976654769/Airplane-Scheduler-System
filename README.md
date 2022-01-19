# Flight-Scheduler-System

 The program is a tool for airlines to use to schedule flights between different locations, producing timetable plans, and an easy way to check routing between cities on multiple flights. Implement the Flight Schedular application that accepts input from the user via standard input. The terminal interface allows the user to interact with the program, to give it input and receive output. The available commands are described below in the section ‘Commands’.
 
 ## Commands
 
FLIGHTS - list all available flights ordered by departure time, then departure location name.  
FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight  
FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file  
FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price, capacity, passengers booked)  
FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price, and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1 passenger. If the given number of bookings is more than the remaining capacity, only accept bookings until the capacity is full.  
FLIGHT <id> REMOVE - remove a flight from the schedule  
FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.  
 
 
LOCATIONS - list all available locations in alphabetical order  
LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location  
LOCATION <name> - view details about a location (it’s name, coordinates, demand coefficient) LOCATION IMPORT/EXPORT <filename> - import/export locations to csv file
SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart DEPARTURES <location_name> - list all departing flights, in order of departure time  
ARRIVALS <location_name> - list all arriving flights, in order of arrival time  
 
 
TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not provided, display the first one in the order. If n is larger than the number of flights available, display the last one in the ordering.  
 
 
can have other orderings:  
TRAVEL <from> <to> cost - minimum current cost TRAVEL <from> <to> duration - minimum total duration TRAVEL <from> <to> stopovers - minimum stopovers TRAVEL <from> <to> layover - minimum layover time TRAVEL <from> <to> flight_time - minimum flight time. 
 
 
HELP – outputs this help string.  
EXIT – end the program.   
 
Note: All commands may be case insensitive.  
However Location names when stored in the location class, should display the name as initially given.  
  
## Travel command
  
Since the schedule is weekly and wraps around, you need to consider the possibility of a flight arriving on Sunday evening potentially connecting with a flight that departs on Monday morning. As such, you may ignore available seat capacity selecting a flight in a potential route, since it is assumed that the current bookings are only for the current week, and this flight route may be used to show results for travellers in subsequent weeks, looking to make a booking later on. However, the ticket prices and overall route cost should depend on the current booking numbers of each flight, since we are assuming that the current booking demand is a good indicator of future demand, so ticket prices will be similar in the future to what they are now.  
 
The TRAVEL command has 5 potential orderings, detailed below. If the primary sorting property is equal between two flight paths, it will fall back to the following secondary and tertiary sorting properties.  
 
- If total cost is equal, sort then by minimum total duration.  
- If total duration is equal, sort then by minimum current cost. Total duration is the time taken from. 
initial departure of the first flight, to finally arriving at the destination.  
- If number of stopovers is equal, sort then by minimum total duration (and then by minimum cost). Stopovers are intermediary locations travelled to in order to reach the destination.  
- If layover time is equal, sort then by minimum total duration (and then by minimum cost). Layover time is the time spent waiting at the airport for connecting flights.  
- If flight time is equal, sort then by minimum total duration (and then by minimum cost). Flight time is the time spent onboard the aircraft while it is flying (ie. total duration excluding layover time).  
 
The output format of the travel command is composed of the flight plan, with layover times between flights specified, see the examples section below.  
 
Note: The number of stopovers is the number of intermediary destinations, not including the original starting location and final destination. It is equivalent to the number of flight legs minus 1.  
Also: The nth flight in the order, starts from 0 being the first one.  
  
## CSV file formats
  
The import and export command for flights and locations allow the contents of the flight and location databases within the program to be saved to CSV (comma separated values) files. Two example files have been provided, as well as a sample command input/output sequence below.  
  
When importing, if invalid lines are encountered in the file without the required data, skip them. Display the total number of invalid flights at the end, if any were invalid. An invalid flight is only considered if there were enough parameters in the line (ie. Exclude empty lines and lines without enough parameters from the invalid total). For example:  
  
User: location import locations.csv.  
Imported 23 locations.   
1 line was invalid.   
  
User: flight import flights.csv.  
Imported 23 flights.    
3 lines were invalid.   
  
User: flight import flights2.csv.  
Imported 1 flight.  
  
User: flight export flights3.csv.  
Exported 1 flight.  
  
Flights and locations are to be imported in the order they are given in the file.  
Export should write to the file flights in the order of their flight id, and locations in the order of their name.  
Flights csv has the following format: day time,startLocation,endLocation,capacity,booked.  
Locations csv has the following format: locationName,latitude,longitude,demandCoefficient.  
 
  
