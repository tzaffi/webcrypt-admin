package webcrypt.crypto;
import webcrypt.math.*;

/**
 * The Enigma class is a multi-alphabetic substitution
 * modeled on the electro-mechanical devices used during WWII.
 * 
 * @see <a href="http://www.ehistory.com/world/library/books/wwii/enigma/index.cfm">Solving The Enigma</a>
 *
 * A keyword takes the form:
 *
 * threeletterstring[_twoletterstring]*
 *
 * Where the threeletterstring defins the rotor settings
 * and the sequence of twoletterstrings define the plugboard settings.
 *
 * The default operation on non-letters is to ignore the character
 * by not transforming it, and by not advancing the enigma machine.
 *
 *
 */
public class Enigma extends Kernel implements EnigmaDefinitions{
    
    public String toString(){
	return "Enigma(threeletters[_twoletters]*)"; 
    }

    /**
     * Get the rotor positions in order a,b,c
     */
    public String rotors(){
	return (  (""+(char)(a.getOrientation()+'A'))
		  + (char)(b.getOrientation()+'A') )
	    + (char)(c.getOrientation()+'A') ;
    }

    /** 
     * Return a string with internal information about rotors and 
     * plug positions.
     */
    public String internalInfo(){
	String out = "Rotor Positions:";
	out += "\na = "+(char)(a.getOrientation()+'A');
	out += "\nb = "+(char)(b.getOrientation()+'A');
	out += "\nc = "+(char)(c.getOrientation()+'A');
	out += "\n\n Plugboard Permutaion:"+p;
	return out;
    }


    /**
     * Internal components of the enigma machine.
     * We create this in the constructor for faster resetting if needed
     * during cryptanalysis simulations.
     */
    protected EnigmaPerm p; //plugboard
    protected EnigmaPerm a; //first rotor
    protected EnigmaPerm b; //second rotor
    protected EnigmaPerm c; //third rotor
    protected EnigmaPerm r; //reflector

    /**
     * Enigma works as follows.
     * 0) The character X is taken
     * 1) The plugboard is applied obtaining p(X)
     * 2) The first rotor is rotated and then transforms p(X) to a(p(X))
     * 3) If the notch is present from rotor A, the second rotor rotates; in
     *    any case we then apply rotor B to obtain b(a(p(X)))
     * 4) Similar to previous step but with rotor C obtaining c(b(a(p(X))))
     * 5) The reflector is applied obtaining r(c(b(a(p(X)))))
     * 6) The inverse of the third rotor is applied without rotation 
     *    obtaining c_inv(r(c(b(a(p(X))))))
     * 7) Similar to (6) but with second rotor: b_inv(c_inv(r(c(b(a(p(X)))))))
     * 8) Similar to previous 2 with first rotor: a_inv(b_inv(c_inv(r(c(b(a(p(X))))))))
     * 9) Finally apply plugboard (self-inverse):p(a_inv(b_inv(c_inv(r(c(b(a(p(X)))))))))
     */
    public Enigma(){
	setDomain(LETTERS);
	setAssociatedKeyClass("webcrypt.crypto.EnigmaKey");
	//plugboard can only be set up when we know the key
	try{
	    a = new EnigmaPerm(ROTORS[0],NOTCHES[0],ROTORTYPE); //first rotor
	    b = new EnigmaPerm(ROTORS[1],NOTCHES[1],ROTORTYPE); //second rotor
	    c = new EnigmaPerm(ROTORS[2],NOTCHES[2],ROTORTYPE); //third rotor
	    r = new EnigmaPerm(REFLECTOR); //reflector
	}catch(IllegalPermutationException e){
	    System.out.println("THIS SHOULD NEVER HAPPEN SINCE WIRING DONE WITH CONSTANTS!!!!");
	}
    }

    /**
     * As the Enigma cipher acts locally on the text without changing
     * the size, implement the void version.
     */
    public void encryptOn(StringBuffer plaintext, Key e){
	EnigmaKey k = (EnigmaKey)e; 
	setup(k);
	
	int len = plaintext.length();
	for (int i=0; i<len; i++){
	    char X = plaintext.charAt(i);
	    if(inDomain(X)){ //then operate on X
		if( (int)X > 90 ) // lower case
		    X -= 32; // convert to upper case
		// now permute in replace in string buffer
		plaintext.setCharAt(i,encryptChar(X,true)); //true for rotation
	    }
	}
    }

    /**
     * Helper method to encryptOn().  Puts the settings in place.
     */
    public void setup(EnigmaKey k){
	try{
	    p = new EnigmaPerm(k.plugboardArray);   //plugboard
	}catch(IllegalPermutationException e){
	    System.out.println("THIS SHOULD NEVER HAPPEN SINCE KEY CHECKED PLUGBOARD SETTINGS!!!!");
	}
	a.setOrientation(k.orientations[0]);    //first rotor
	b.setOrientation(k.orientations[1]);    //second rotor
	c.setOrientation(k.orientations[2]);    //third rotor
    }

    /** 
     * Encrypt one letter.  If rotate true, rotate the gears forward.
     * Useful to allow no rotation, in case want to reuse the machine
     * in same setting (e.g. for cryptanalysis).
     *
     * rotate becomes a's argument because if a doesn't rotate,
     * none of the other rotors will rotate.
     **/
    public char encryptChar(char X, boolean rotate){	
      return 
	p.permute(
	    a.inversePermute(
		b.inversePermute(
		    c.inversePermute(
			r.permute(
			     c.rotNperm(
				  b.rotNperm(
				      a.rotNperm(
					   p.permute(X)    /* p */
					   ,rotate)        /* a */
				      ,a.notchPresent() && rotate )   /* b */
				  , b.notchPresent() && a.notchPresent() && rotate )      /* c */
			     )                             /* r */
			)                                  /* c_inv */
		    )                                      /* b_inv */
		)                                          /* a_inv */
	    );                                             /* p */
    }

    /**
     * Crank the machine forward one unit.
     */ 
    public void crank(){
	encryptChar('A',true);
    }

    /**
     * Decryption is exactly the same as encryption!
     */
    public void decryptOn(StringBuffer ciphertext, Key e){
	encryptOn(ciphertext,e); 
    }

    /**
     * Simple test of Enigma on command line
     */
    public static void main(String[] args) throws KeyCreationException{
	//	System.out.println("got here");
	Enigma kernel = new Enigma();
	System.out.println(""+kernel);
	System.out.println("PLAINTEXT:\n"+args[0]);
	
	EnigmaKey k = new EnigmaKey(args[1]);
	StringBuffer ciphertext = kernel.encrypt(new StringBuffer(args[0]),k);
	System.out.println("CIPHERTEXT:\n"+ciphertext);
	StringBuffer possibleplain = kernel.decrypt(ciphertext,k);
	System.out.println("Encrypt and decrypt:\n"+possibleplain);
    }


}



