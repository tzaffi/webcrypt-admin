package webcrypt.math;

/**
 * This allows for permutations with some extra internal orientation 
 * information which let it rotate as used.
 *
 * Though this class extends CapsPerm and could make use of some
 * of its important methods, since the class is also intended for
 * use in Turing Bombe simulations, methods which aren't O(1)
 * in CapsPerm and can be made O(1) are fixed in this class.
 *
 * The webcrypt.crypto.EnigmaDefinitions interface defines some rotor
 * settings
 */
public class EnigmaPerm extends CapsPerm implements webcrypt.crypto.EnigmaDefinitions{
    
    /**
     * The notch past which the rotor rotates
     */
    private char notch;

    /**
     * The current orientation.  Array index is translated by this value.
     */
    private int orientation;

    /**
     * Flag for wheter this is a rotor or a reflector
     */
    private boolean permtype;

    /**
     * Accessor methods
     */
    public char getNotch(){ return notch; }
    public int  getOrientation(){ return orientation; }
    public boolean getType() { return permtype; }

    /**
     * Settor method
     */
    public void setOrientation(char orientation){ this.orientation = (int)(orientation-'A'); }

    /**
     * Since rotors are meant to be used both forwards and backwards,
     * it's useful to have the inverse array around.
     */
    private int[] inverse;

    /**
     * The notch is said to be "present" and ready to attempt
     * rotating the next rotor, when the orientation is equal to our notch
     */
    public boolean notchPresent(){ return ( orientation == (int)(notch-'A') ); }

    /**
     * O(1) method for permuting char's, assuming that the domain
     * are capitals.  Overriding CapsPerm's slower method.
     *
     * Rotors must not use this method alone as one must
     * account also for the notch and rotation information.
     *
     * The method is suitable for reflectors.
     */
    public char permute(char input){
	return( (char)(perm[(26+input-'A'-orientation) % 26] + 'A') );
    }

    /**
     * Method for inverse permutation.  
     */
    public char inversePermute(char input){
	return  (char)('A' + (inverse[input-'A'] + 26 + orientation) % 26 );
    }

    /**
     * Rotate the wheel one position.
     */
    public void rotate(){ orientation = (orientation+1) % 26 ;  }
    
    /**
     * Possibly rotate and then permute the letter.
     *
     * The name is misleading because rotation depends on 
     * where the notch is present in the previous rotor.
     *
     * No rotations occur on inverse permutation so no corresponsing method needed.
     */
    public char rotNperm(char c, boolean rotate){
	if (rotate){ rotate(); }
	return permute(c);
    }
    

    /**
     * Constructor builds on CapsPerm.
     *
     * Need to also define whether this is a reflector or a rotor
     * (though currently there is no functional difference except that
     * reflectors ignore the rotate() method)
     * and define the intial orientation.
     */
    public EnigmaPerm(String wiring, char notch, char initOrientation, boolean permtype)
	throws IllegalPermutationException
    {
	super(wiring);
	this.permtype = permtype;
	if (permtype == ROTORTYPE){
	    this.notch = notch;
	    setOrientation(initOrientation);
	    inverse = inverseArray();  // inherited from Permutation
	}
    }
    
    /**
     * Constructor without orientation.
     */
    public EnigmaPerm(String wiring, char notch, boolean permtype)
	throws IllegalPermutationException
    {  this(wiring,notch,'A',permtype); }   

    /**
     * Constructor more useful for reflectors
     */
    public EnigmaPerm(String wiring) throws IllegalPermutationException{
	this(wiring,'A','A',REFLECTORTYPE);
    }

    /**
     * Constructor for plugboards, which since they are self-inverses,
     * can be considered as reflectors.
     *
     * The second argument is a double array of char's indicating
     * which chars get swapped.  For example, the plugboard array
     *
     * { {'A','X'}, {'B','F'}, {'G','Q'} }
     *
     * indicates that A and X, B and F, and G and Q  re swapped.
     */
    public EnigmaPerm(char[][] plugboard) throws IllegalPermutationException{
	this(plugboard2keyword(plugboard));
    }

    /**
     * Helper method for the plugboard constructor.
     *
     * If the plugboard is incorrect (a letter appears in two places) 
     * an exception is thrown.
     */
    public static String plugboard2keyword(char[][] plugboard) 
	throws IllegalPermutationException
    {
	char[] keyword = new char[26];
	int idx0, idx1;
	for(int i=0; i<plugboard.length; i++){
	    idx0 = (int)(plugboard[i][0]-'A');
	    idx1 = (int)(plugboard[i][1]-'A');
	    // If a letter already seen, throw exception.  Else, still okay.
	    if ( 0 != keyword[idx0] || 0 != keyword[idx1] ) 
		throw new IllegalPermutationException("Incorrect plugboard setting.");
	    keyword[idx0] = plugboard[i][1];
	    keyword[idx1] = plugboard[i][0];
	}
	//Now fill in the rest with identity map
	for (int i=0; i<keyword.length; i++)
	    if( 0==keyword[i] ) keyword[i] = (char)('A'+i);

	return new String(keyword);
    }
    
    /**
     * Test the constructs.
     */
    public static void main(String[] args) throws IllegalPermutationException{
	EnigmaPerm reflector, rotor, rotoreflector, plugboard;
	reflector = new EnigmaPerm(REFLECTOR);
	rotoreflector = new EnigmaPerm(REFLECTOR,'A','A',ROTORTYPE);
	rotor = new EnigmaPerm(ROTORS[0],NOTCHES[0],'A',ROTORTYPE);
	plugboard = new EnigmaPerm(new char[][]
	    { {'A','Y'},{'B','R'},{'C','U'},{'D','H'},{'E','Q'},{'F','S'},
	      {'G','L'},{'I','P'},{'J','X'},{'K','N'},{'M','O'},{'T','Z'},{'V','W'}
	    } );
				   

	System.out.println("reflector:\n");
	for( char X = 'A'; X <= 'Z'; X++)
	    System.out.print(X+"-->"+reflector.permute(X));

	System.out.println("\nrotoreflector:\n");
	for( char X = 'A'; X <= 'Z'; X++)
	    System.out.print(X+"-->"+rotoreflector.permute(X));
	System.out.println("\nrotoreflector inverse:\n");
	for( char X = 'A'; X <= 'Z'; X++)
	    System.out.print(X+"-->"+rotoreflector.inversePermute(X));

	System.out.println("\nplugboard:\n");
	for( char X = 'A'; X <= 'Z'; X++)
	    System.out.print(X+"-->"+plugboard.permute(X));

	System.out.println("\nrotor :\n");
	for( char X = 'A'; X <= 'Z'; X++)
	    System.out.print(X+"-->"+rotor.permute(X));
       	System.out.println("\nrotor inverse:\n");
	for( char X = 'A'; X <= 'Z'; X++)
	    System.out.print(X+"-->"+rotor.inversePermute(X));

	System.out.println("\n\nNow rotate and permute A  50 times");
	for( int i = 1; i<=50; i++)
	    System.out.print("A-->"+rotor.rotNperm('A',true));

	
    }

}


