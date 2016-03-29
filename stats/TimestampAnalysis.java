package webcrypt.stats;

/**
 * Convert dates to timestamps
 */
import java.util.StringTokenizer;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;

public class TimestampAnalysis extends Analyzor{

    /**
     * Used to show in window.
     */
    public String toString(){
	return "Timestamp Analysis(yr_mon_date_hrs_min_sec)";
    }

    /**
     * Given a timestamp to the nearest second, print out the value
     * that system.currentTimeMillis() would give for that precise time.
     *
     * Only confusing fact is that January is month 0, February is month 1, etc. since
     * months start at 1 instead of 0 (everything else starts at 0!!!)
     */
    public void analyzeOn(StringBuffer text, String arg) throws IllegalArgumentException{
	int year, month, date, hrs, min, sec;
	StringTokenizer argtokens = new StringTokenizer(arg,"_");
	if(argtokens.countTokens() != 6)
	    throw new IllegalArgumentException("Wrong number of arguments!");
	try{
	    year = Integer.parseInt(argtokens.nextToken());
	    month= Integer.parseInt(argtokens.nextToken());
	    date = Integer.parseInt(argtokens.nextToken());
	    hrs  = Integer.parseInt(argtokens.nextToken());
	    min  = Integer.parseInt(argtokens.nextToken());
	    sec  = Integer.parseInt(argtokens.nextToken());
	}catch(NumberFormatException e){
	    throw new IllegalArgumentException("Arguments must be integers");
	}
	//	GregorianCalendar greg = new GregorianCalendar(year,month-1,date,hrs,min,sec);
	//	greg.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));
	//	GregorianCalendar start = new GregorianCalendar(1970,0,0,0,0,0);

	String out = ""+(new Date(year-1900,month-1,date,hrs,min,sec)).getTime();
	// out = "Gregorian:"+greg.getTimeInMillis();
	//	
	//	out+= "\nTime since 1970:"+(greg.getTimeInMillis()-start.getTimeInMillis());
	text.delete(0,text.length());
	text.append(out);
    }
    
    /**
     * Test analyzeOn method
     */
    public static void main(String[] args){
	TimestampAnalysis a = new TimestampAnalysis();
	StringBuffer buff = new StringBuffer("");
	a.analyzeOn(buff,args[0]);
	System.out.println(buff);
    }

}



