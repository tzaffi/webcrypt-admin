package webcrypt.stats;

/**
 * This class encapsulates the frequency of a character inside a text.
 * It allows for easy sorting using Java Collections in descending order
 * as the Comparable interface is implemented (with < really being >)
 */
public class CharFrequency implements Comparable{

    private char character;  // The char we're counting
    private int  count = 0;  // The number of times the char has been seen

    // Following statistical properties are considered "off" when value is -1
    private double frequency = -1;  // Ratio of count to total no. of letters
    private double variance = -1;   // Square of difference average frequency

    /**
     * Public accessor  methods
     */
    public char getChar(){ return character; }
    public int  getCount(){ return count; }
    public double getFreq(){ return frequency; }
    public double getVar(){ return variance; }

    /**
     * Public settor methods
     */
    public void setChar(char c){ character = c; }
    public void setCount(int c){ count = c; }
    public void setFreq(double f){ frequency = f; }
    public void setVar(double v){ variance = v; }

    /**
     * Return string of all values which are on
     */
    public String toString(){
	return ( character+":\t"+count
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
	CharFrequency x = (CharFrequency)o;
	return (count > x.count) ? -1 
	    : (count < x.count)? 1 
	    : (character > x.character) ? 1
	    : (character < x.character) ? -1
	    : 0;
    }

    /**
     * Constructor specifying this character
     */
    public CharFrequency(char c){ setChar(c); }
}
