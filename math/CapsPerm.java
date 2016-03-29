package webcrypt.math;

/**
 * An implementation of Permutation for the set of upper case letters.
 * Allows generating a perumation of the 26 upper-case letters by
 * using a keyword, as specified in the comment explaining the 
 * constructor.
 *
 * To allow for cryptalysis experiments, the String based constructor
 * allows to create a general function, rather than a permutation if
 * the first character is '!'
 *
 * @author <a href="mailto:zeph@cs.columbia.edu">Zeph Grunschlag</a>
 */
public class CapsPerm extends Permutation{

    static{
	domainSize = 26;
	domain = new char[domainSize];
	domain[0] = 'A';
	for( int i=1; i<domainSize; i++){
	    domain[i] = domain[i-1];
	    domain[i]++;
	}
    }
    
    public CapsPerm(int[] outputarray) throws IllegalPermutationException{
	super(outputarray);
    }

    /** 
     * Permutation constructor in line with historical use of keywords to
     * generate permutation.  For example, the string "WILLIAMGATES"
     * generates (22,8,11,0,12,6,19,4,18,1,2,3,5,6,7,9,10,13,14,15,16,17,20,21,23,24,25)
     * Through the following process:
     * 1.  Remove doubled letters resulting in "WILAMGTES"
     * 2.  Add the missing letters in order resulting in "WILAMGTESBCDFGHJKNOPQRUVXYZ"
     * 3.  This is the permutation 
     *     {22,8,11,0,12,6,19,4,18,1,2,3,5,6,7,9,10,13,14,15,16,17,20,21,23,24,25}
     * 
     * Any text string will be accepted as lower case letters will be capitalized
     * while ineligible characters will be thrown out.
     *
     * If the first character is '!', we create a function instead.
     */
    public CapsPerm(String keyword) throws IllegalPermutationException{
	this(string2perm(keyword.charAt(0)=='!'?keyword.substring(1):keyword));
	//	System.out.println("keyword: "+keyword);
	if ( keyword.charAt(0) == '!'){  // a function
	    int[] v = string2perm(keyword);
	    //	    for(int i=0; i<v.length; i++) System.out.print(v[i]+",");
	    Function f = new Function(v,domain);
	    System.out.println("\nf = "+f);
	    perm = f.func;
	}
    }

    /**
     * Helper method that converts keywords into permutations.
     * See the constructor's comment for further details.
     *
     * If the first character is '!', we create a function instead.
     */
    public static int[] string2perm(String keystr){
	int[] outputarray = null;
	if ( keystr.charAt(0) != '!'){  // a regular permutation
	    //capitalize and remove non-letters
	    keystr = keystr.toUpperCase();
	    StringBuffer temp = new StringBuffer(keystr);
	    for(int i = 0; i < temp.length(); ){
		if( !Character.isLetter(temp.charAt(i)) )
		    temp.deleteCharAt(i);
		else
		    i++;
	    }
	    keystr = temp.toString();
	    //System.out.println("k1: "+keystr);
	    
	    // First convert keystr to naive array:
	    outputarray = new int[keystr.length()];
	    for(int i = 0; i<outputarray.length; i++)
		outputarray[i] = (int)(keystr.charAt(i)-'A');
	    
	    //remove repeated letters
	    temp = new StringBuffer(keystr);
	    int[] counter = new int[26]; // all 0's automatically
	    for(int i=0, bufferidx=0; i < outputarray.length; i++){
		int val = outputarray[i];
		// if value already encountered remove it
		if (counter[val] > 0) 
		    temp.deleteCharAt(bufferidx);
		else{ // first time seen value
		    bufferidx++;
		    counter[val]++;
		}
	    }
	    keystr = temp.toString();
	    //	System.out.println("k2: "+keystr);

	    // Convert keystr to array
	    outputarray = new int[26];
	    int idx = 0;
	    // First the keystr letters:
	    for( ; idx<keystr.length(); idx++)
		outputarray[idx] = (int)(keystr.charAt(idx)-'A');
	    // Then the missing letters (none missing for
	    for(int i=0; i<counter.length; i++)
		if ( counter[i] == 0 ) // yet to see letter
		    outputarray[idx++] = i;
	} 
	else { // first char is '!' so a function not permutation
	    //	    System.out.println(keystr);
	    //capitalize and replace non-letters by 'X':
	    keystr = keystr.toUpperCase().substring(1); // get rid of '!'
	    //	    System.out.println(keystr);
	    String temp = "";
	    int i;
	    for(i = 0; i < keystr.length() && i < 26; i++){
		char c =  keystr.charAt(i);
		temp += Character.isLetter(c) ? c : 'X';
		//		System.out.println("temp:"+temp);
	    }
	    for(; i<26; i++) // pad rest with identical letters
		temp += domain[i];
	    keystr = temp;
	    System.out.println(keystr);
	    //convert keystr to array:
	    outputarray = new int[26];
	    for(i = 0; i<26; i++)
		outputarray[i] =  (int)(keystr.charAt(i)-'A');
	    for(i=0; i<outputarray.length; i++) System.out.print(outputarray[i]+",");
	}

	return outputarray;	
    }


    /**
     * This main method just runs an example to make sure everything's working fine.
     */
    public static void main(String[] args) throws IllegalPermutationException{
	int[] a = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,
			     14,15,16,17,18,19,20,21,22,23,24,25,0};
	
	CapsPerm cp = new CapsPerm(a);
	int x = 17;
	System.out.println("x = "+x+", cp.permute(x) = "+cp.permute(x));
	for( char X = 'A'; X <= 'z'; X++)
	    System.out.print(X+"-->"+cp.permute(X));

	//Now see the resulting permutations on the inputs
	for(int i=0; i<args.length; i++){
	    cp = new CapsPerm(args[i]);
	    System.out.println(args[i]+" --> \n"+cp);
	    cp.invert();
	    System.out.println(args[i]+"inverse:\n"+cp);
	}

	System.out.println("\n\ncreate CapsPerm with string \"zeph\"");
	cp = new CapsPerm("zeph");
	System.out.println(cp);
	for( char X = 'A'; X <= 'z'; X++)
	    System.out.print(X+"-->"+cp.permute(X));

	System.out.println("\n\ncreate CapsPerm with string \"!zeph\"");
	cp = new CapsPerm("!zeph");
	System.out.println(cp);
	for( char X = 'A'; X <= 'z'; X++)
	    System.out.print(X+"-->"+cp.permute(X));
    }
    
}




