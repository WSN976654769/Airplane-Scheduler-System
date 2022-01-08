
public class FlightDate {

	public int day, hour, min;
	public FlightDate(int min, int hour, int day) {
		this.day = day;
		this.hour = hour;
		this.min = min;
	}
	//refer to week5 SimpleDate.java
	public boolean isGreaterThan(FlightDate other) {
		return this.day > other.day || 
			(this.day == other.day && this.hour > other.hour) || 
			(this.day == other.day && this.hour == other.hour && this.min > other.min);
	}
	
	public boolean isEqualTo(FlightDate other) {
		return this.day == other.day && this.hour == other.hour && this.min == other.min;
	}

	public static FlightDate calculateTime(int day, int hour, int min, int addhour, int addmin) {
		min += addmin;		
		int carry = min/60;
		if(carry>0) {
			hour += carry;
			min -=carry*60;
		}	
		hour += addhour;
		int diff = hour/24;
		if(diff>0) {
			day +=diff;
			hour -= diff*24;
			if(day>7) {
				day = 1;
			}
		}
		return new FlightDate(min,hour,day);		
	}
	
	public boolean isConflict(FlightDate other) {
		
		FlightDate after = FlightDate.calculateTime(other.day,other.hour,other.min,1,0);
		FlightDate before = FlightDate.calculateTime(other.day,other.hour,other.min,-1,0);
	
		if(after.day==1 && before.day==7) {	
			if(this.day==7 && isGreaterThan(before))	return true;
			else if(this.day==1 && isLessThan(after))	return true;
			//else 
		}
		else if(isLessThan(after) && isGreaterThan(before)) {
			//System.out.println("conflict");
			return true;
		}	
		return false;
	}

	//<
	public boolean isLessThan(FlightDate other) {	
		return this.day < other.day || 
			(this.day == other.day && this.hour < other.hour) || 
			(this.day == other.day && this.hour == other.hour && this.min < other.min);
	}


	//>=
	public boolean isGreaterThanOrEqualTo(FlightDate other) {
		return isGreaterThan(other) || isEqualTo(other);
	}
	
	public String toString() {
		String day = normalizeDay(this.day);
		return String.format("%3s %02d:%02d", day, this.hour, this.min);
	}	
	
	public String showAllTime() {
		String day = showAllDay(this.day);
		return String.format("%3s %02d:%02d", day, this.hour, this.min);
	}	
	
	public static String normalizeDay(int i) {
    	switch(i) {
    		case 1:
    			return "Mon";
    		case 2:
    			return "Tue";
    		case 3:
    			return "Wed";
    		case 4:
    			return "Thu";
    		case 5:
    			return "Fri";
    		case 6:
    			return "Sat";
    		case 7:
    			return "Sun";			
    	}
		return null; 	
    }
	
	public static String showAllDay(int i) {
    	switch(i) {
    		case 1:
    			return "Monday";
    		case 2:
    			return "Tuesday";
    		case 3:
    			return "Wednesday";
    		case 4:
    			return "Thursday";
    		case 5:
    			return "Friday";
    		case 6:
    			return "Saturday";
    		case 7:
    			return "Sunday";			
    	}
		return null; 	
    }
}
