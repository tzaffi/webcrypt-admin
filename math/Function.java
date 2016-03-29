package webcrypt.math;

/**
 * A non-invertible version of Permutation, allowing for
 * easy experimentation during cryptanlysis of Monolphabetid
 * ciphers.
 * {1,3,1,2} denotes 0 -> 1, 1 -> 3, 2 -> 1, 3 -> 2
 *
 *
 * @author <a href="mailto:zeph@cs.columbia.edu">Zeph Grunschlag</a>
 */
public class Function{

    // Set the domain size.
    protected  int domainSize;

    /**
    * Get the domain size.
    */
    public int getDomainSize(){ return domainSize; }
    
    /**
    * The domain is a set of characters.
    * We have elected to allow an arbitrary set (and not just
    * a range) for maximal flexibility, though we sacrifice
    * efficiency both in time and space.
    */
    protected char[] domain;

    public char[] getDomain() { return domain; }


    // The function is given by an array
    // with the property that every possible index appears as an element.
    protected int[] func;

    /**
     * The numerical version of the function is publicly available:
     * No check has been made to avoid ArrayIndexOutOfBoundsExeption's
     * which would occur if an invalid input is given.  This is upto
     * the user to ensure.
     */
    public int permute(int input){ return func[input]; }

    /**
     * The char version of the function:
     * This is slow since, unless the char2idx function is 
     * over-ridden with a faster hash-table based method.
     * If the character is not in the domain it is returned
     * as-is
     */
    public char permute(char input){
	int idx = char2idx(input);
	return( idx==-1 ? input : domain[permute(idx)] );
    }

    /**
     * Find the index of the given char.  -1 is returned if character
     * not present.
     * Theoretically faster methods are possible using hash-tables
     * but for the small examples anticipated being used, this would
     * be overkill.  If you are considering encrypting large tracts,
     * override this method.
     */
    public int char2idx(char c){
	int i;
	for(i = 0; i < domainSize; i++)
	    if (c == domain[i])
		break;
	if (i==domainSize) return -1;
	return i;
    }

    /**
     * CANNOT INVERT FUNCTIONS IN GENERAL
     * Invert the permuation.  Only the func[] needs to be changed 
     * as the domain is fixed.
    public void invert(){
	int[] inverse = new int[domainSize];
	for(int i=0; i<domainSize; i++)
	    inverse[perm[i]] = i;
	perm = inverse;
    }
    */
	  

    /**
     * Create a function by specifying a function
     * using an array
     **/
    public Function(int[] outputarray, char[] domain) 
	throws IllegalArgumentException{
	this.domain = domain;
	domainSize = domain.length;
	if( !validRange(outputarray) )
	    throw new IllegalArgumentException("Invalid input array.");
	func = outputarray;
   }

    /**
     * Helper method for the previous constructor
     */
    public boolean validRange(int[] outputarray){
	//	System.out.println("dsize = "+domainSize);
	if ( outputarray.length != domainSize )
	    return false; // domain and range of different sizes
	int[] counter = new int[domainSize]; // all 0's automatically
	for(int i=0; i < domainSize; i++){
	    int val = outputarray[i];
	    // if out-of range
	    if (val < 0 || val >= domainSize)
		return false;
	}
	return true;
    }
    
    /**
     * Useful for inspecting functions:
     */
    public String toString(){
	String part1 = "Numerical: (";
	String part2 = "Alphabet:   ";
	//	for(int i = 0; i<domain.length; i++) System.out.print(domain[i]+",");
	for(int i=0; i < domainSize; i++){
	    //	    System.out.println("i = "+i);
	    part1 += (i==0?"":",")+func[i];
	    //	    System.out.print(part1);
	    part2 += permute(domain[i]);
	    //	    System.out.print(part2);
	}
	part1 += ")";
	return (part1+'\n'+part2);
    }
}
