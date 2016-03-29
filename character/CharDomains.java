package webcrypt.character;

/**
 * An class to be extended by any class that wishes to 
 * allow its objects to have a predefined domain and to allow for
 * char manipulations methods.
 */
import java.util.Hashtable;
import java.math.BigInteger;
import webcrypt.crypto.KeyCreationException;

public class CharDomains{
    
    /**
     * Every cryptographic algorithm has a range of operation, called
     * a domain.  For pedagogic examples, the domain is A-Z (or in unicode
     * 65-90).  The default domain is all possible unicode char's (which
     * is a assumed when null).  
     * A domain is specified as an array of closed ranges of increasing order with
     * values in the 0-65535.
     * E.g.  valid:  { {1,3}, {7,9}, {11,1000} }
     * E.g. redundancy.  Following are equivalent:  { {1,10} } vs. { {1,4},{5,10} }
     * Each algorithm decides what to do with out-of-range char's.
     * Some options are to:
     *  o  ignore so leave as is
     *  o  delete (information lost)
     *  o  wipe to a predetermined character such as ' ' (information lost)
     *  o  convert losing little information (e.g. changing lower case to caps)
     */
    protected int[][] domain = null;

    /** 
     * Setter method for the domain
     */
    public void setDomain(int[][] dom){
	domain = dom;
    }

    /**
     * Get the total size of the domain
     */
    public int getDomainSize(){
	int size = 0;
	for(int i = 0; i < domain.length; i++)
	    size += (domain[i][1]-domain[i][0])+1;
	return size;
    }

    /**
     * Some useful preset domains
     */
    
    /**
     * A-Z domain
     */ 
    public final static int[][] CAPS = new int[][] { new int[] {65, 90} };

    /**
     * A-Z and a-z domain
     */
    public final static int[][] LETTERS = new int[][]{ new int[] {65, 90},
						       new int[] {97, 122} };

    /**
     * Standard printable characters including the space = 32
     */
    public final static int[][] PRINTABLE = new int[][] { new int[] {32, 126} };

    /**
     *  Text-readable binary '0' and '1'
     */
    public final static int[][] ASCII_BIN = new int[][] { new int[] {48, 49} };

    /**
     * Not actually useful since null is by default universal
     */
    public final static int[][] UNICODE = new int[][] { new int[] {0, 65535} };

    // Some useful methods:
    /**
     * View an upper case string as a base-26 number and convert to standard
     * version. 
     *
     * EG, if we start with the string "ZBZ" which represents 25*26^2 + 1*26 + 25
     * we convert to the more standard "p1p"
     *
     * A-J are converted to 0-9 by subtracting 27 from the character value
     * K-Z are converted to A-P by subtracting 10 from the character value
     *
     * At the end we convert to lower case.
     */
    public static String caps2base26(String capstr){
	//capitalize just in case
	capstr = capstr.toUpperCase();
	StringBuffer base26buf = new StringBuffer(capstr.length());
	char c;
	for(int i = 0; i < capstr.length(); i++){
	    c = capstr.charAt(i);
	    base26buf.append( (char)(c < 'K' ? ( c-'A'+'0' ) : (c-'K'+'A') ) );
	}
	return base26buf.toString().toLowerCase();
    }


    /**
     * Convert a capitalized string to an integer.  Uses BigInteger's arbitrary
     * radix computations.  All strings are assumed to be positive.  If there is a problem
     * return -1.
     */
    public static int caps2base26num(String capstr) {
	int output = -1;
	try{
	    output = (new BigInteger(caps2base26(capstr),26)).intValue();
	}catch(NumberFormatException e){
	    output =  -1;
	}
	return output;
    }

    /**
     * Convert a base-26 number between into a string coverting 0 --> A, 1 --> B, etc.
     */
    public static String base26toCaps(String base26){
	//capitalize just in case
	base26 = base26.toUpperCase();
	StringBuffer capsbuf = new StringBuffer(base26.length());
	char c;
	for(int i = 0; i < base26.length(); i++){
	    c = base26.charAt(i);
	    capsbuf.append( (char)(c <= '9' ? ( c-'0'+'A' ) : (c-'A'+'K') ) );
	}
	return capsbuf.toString().toUpperCase();	
    }

    /**
     * View an alphanumeric string as a base-63 number and convert to base 10.
     * We view the number base 63 to easily allow no redundancies such as 0001 vs 1
     *
     * EG, if the string "ZBZ" is converted to the number  25*62^2 + 1*62 + 25
     * which is then retuned base-10.
     *
     * 0-9 are converted to 0-9  by subtracting '0' == 48 from the char value and adding 1
     * A-Z are converted to 10-35 by subtracting 'A' == 65 and adding 11 
     * a-z are converted to 36-61 by subtracting 'a' == 97 and adding 37
     *
     */
    public static String alphanumeric2base10(String alphanum){
	BigInteger bignum = new BigInteger("0");

	//least significant number on the right:
	for (int i=0; i<alphanum.length(); i++){	    
	    char c = alphanum.charAt(i);
	    int x;
	    if ( c <= '9' )
		x = (int)(c - '0') + 1;
	    else if ( c <= 'Z')
		x = (int)(c - 'A') + 11;
	    else 
		x = (int)(c - 'a') + 37;
	    //	    System.out.println("adding x = "+x);
	    bignum = bignum.multiply(new BigInteger("63")).add(new BigInteger(""+x));
	}

	return bignum.toString();
    }

    /**
     * Built-in method for testing whether a character is in the preset range.
     * May want to over-ride the method with faster algorithm, if available.
     */
    public boolean inDomain(char c){
	int cint = (int)c;
	if (domain == null) return true;
	for (int i = 0; i < domain.length; i++) {
	    if (cint < domain[i][0]) return false;
	    if (cint <= domain[i][1]) return true;
	}
	return false;	    
    }

    //For use with the following method
    private Hashtable table = null;

    /**
     * Built-in method for finding the index of the searched character in O(1) time
     * using a hashtable.  The hashtable is constructed only if it was never used before.
     *
     * Returns -1 if character is outside the domain.
     */
    public int getIndex(char c){
	// initialize the table if this is the first use:
	if (table == null) makeIndexTable();
	Integer idx = (Integer)table.get(new Character(c));
	return idx==null?-1:idx.intValue();
    }

    /**
     * Make the index table (helper for getIndex
     */
    public void makeIndexTable(){
	table = new Hashtable();
	int j = 0;
	for(int i = 0; i < domain.length; i++)
	    for(char ch = (char)domain[i][0]; ch <= (char)domain[i][1]; ch++)
		table.put(new Character(ch), new Integer(j++));
    }

    /**
     * Method for finding the offsets of a block of acceptable text in a string.
     * Useful when you want to encrypt disjoint blocks leaving non-domain characters
     * in place.
     *
     * For example consider a StringBbuffer with underlying string  "mambajamba*h37ll%o d&(lly".
     * Suppose we found the first three 4-letter blocks mamb,ajam,bahl and we are looking for
     * the fifth block starting at index 15.  The block is "lodl" and its offsets from 15 are
     * (0,2,4,7) which would be the output.
     *
     * If a block of the given size cannot be found, the un-found offsets are set to -1.
     */
    public int[] findBlockOffsets(String str, int idx, int blockSize){
	int[] output = new int[blockSize];
	int j=0; //output's index
	for(int i=0; j<blockSize && i+idx<str.length() ; i++)
	    if( inDomain(str.charAt(i+idx)) )
		output[j++]=i;
	for( ; j<blockSize; j++) output[j] = -1;  //fill the unfound offsets
	return output;
    }

    /**
     * Method removes all non-domain characters returning stripped version.
     */
    public String stripNondomain(String x){
	StringBuffer sb = new StringBuffer("");
	for(int i=0; i<x.length(); i++){
	    char c = x.charAt(i);
	    if(inDomain(c)) sb.append(c);
	}
	return sb.toString();
    }


    /**
     * Method for testing purposes
     */
    public static void test(String[] args) throws KeyCreationException{
	for(int i = 0; i < args.length; i++){
	    String str = args[i].toUpperCase();
	    System.out.println(str+": base26 = "+caps2base26(str)+", int = "+caps2base26num(str));
	}
	String str = args[args.length-1];
	System.out.println("\n\nFinding block offsets for size-4 blocks in "+str
			   +" starting at index 2");
	CharDomains cd = new CharDomains();
	cd.setDomain(CAPS);
	int[] offsets = cd.findBlockOffsets(str,2,4);
	for(int i=0; i<offsets.length; i++)System.out.print(offsets[i]+",");

	System.out.println("Test how char arrays are initialized.");
	char[] test = new char[5];
	System.out.println("char value at index 3: \'"+test[3]+"\'");
	System.out.println("int value at index 3: \'"+(int)test[3]+"\'");
	System.out.println("Will it equal the int 0? "+(0==test[3]));
    }

    /**
     * The problem with the byte-array paramater constructor of String is
     * that the bytes are converted to char's in a platform dependent way.
     * Thus information may be lost when attempting to save random bytes
     * inside a String.  This method fixes the problem by converting
     * to chars in the usual way, and then creating a string.
    public static String bytes2chars(byte[] in){
	char[] temp = new char[in.length];
	for(int i=0; i<temp.length; i++) temp[i]= (char)in[i];
	return new String(temp);
    }
     */

    /**
     * Main method converts characters to numbers between 0 and 25 or
     * the opposite, depending on the first input.  Incorrect number
     * format returns -1
     *
     * The the argument is viewd as a base-63 number and converts to base-10
     */
    public static void main(String[] args) throws webcrypt.crypto.KeyCreationException{
	String firstarg = args[0];
	//if first char of first arg is a letter, convert chars to nums,
	char indicator = args[0].charAt(0);
	if (Character.isLetter(indicator)){
	    for(int i=0; i<args.length; i++){
		args[i] = args[i].toUpperCase();
		for(int j=0; j<args[i].length(); j++){
		    char c=args[i].charAt(j);
		    int x = (int)(c - 'A');
		    System.out.print(x+" ");
		}
	    }
	}
	//else convert nums to chars
	else{ 	
	    for(int i=0; i<args.length; i++){
		int x = -1;
		try{
		    x = Integer.parseInt(args[i]);
		}catch(NumberFormatException e){ }
		String s = (x==-1)?"-1":(""+(char)(x+'A'));
		System.out.print(s+" ");
	    }
	}

	// in either case, convert from base-63 to base-10 and print:
	System.out.println("Interpreting "+firstarg+" as base-63.\nIn base-10 = "+alphanumeric2base10(firstarg));
    }
}




