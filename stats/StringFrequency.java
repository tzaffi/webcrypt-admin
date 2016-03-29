package webcrypt.stats;

/**
 * This class encapsulates the frequency of a String inside a text.
 * It allows for easy sorting using Java Collections in descending order
 * as the Comparable interface is implemented (with < really being >)
 */
public class StringFrequency implements Comparable{

    private String str;  // The string we're counting
    private int  count = 0;  // The number of times the String has been seen

    // Following statistical properties are considered "off" when value is -1
    private double frequency = -1;  // Ratio of count to total no. of letters
    private double variance = -1;   // Square of difference average frequency

    /**
     * Public accessor  methods
     */
    public String getString(){ return str; }
    public int  getCount(){ return count; }
    public double getFreq(){ return frequency; }
    public double getVar(){ return variance; }

    /**
     * Public settor methods
     */
    public void setString(String s){ str = s; }
    public void setCount(int c){ count = c; }
    public void setFreq(double f){ frequency = f; }
    public void setVar(double v){ variance = v; }
    
     /**
      * Return string of all values which are on
      */
     public String toString(){
	 return ( str+":\t"+count
		  +(frequency!=-1?("\t"+frequency):"")
		  +(variance!=-1?("\t"+variance):"") );
     }

     /**
      * The method we must implement in implementing the comparable interface.
      * Counterintuively, we say that this < o if this.count > o.count.  
      * This is to allow for easy sorting by descending order in Collections objects.
      * If the frequencies are equal, we compare the objects alphabetically
      */
     public int compareTo(Object o){
	 StringFrequency x = (StringFrequency)o;
	 return (count > x.count) ? -1 
	     : (count < x.count)? 1 
	     : str.compareTo(x.str) ;
     }

     /**
      * Constructor specifying this string
      */
     public StringFrequency(String s){ setString(s); }

    /**
      * Constructor specifying this string and count
      */
     public StringFrequency(String s, int cnt){ 
	 setString(s);
	 setCount(cnt);
     }
}
