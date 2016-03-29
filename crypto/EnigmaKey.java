package webcrypt.crypto;
/** 
 *  The EnigmaKey class implements the key for the Enigma encryption algorithm.
 *
 *  The key consist of rotor and plugboard settings.
 *
 *  The keyseed looks like
 * 
 *  orientationstring[_plugboardstring]*
 *
 *  Where orientationstring should be a string at least as long as the number
 *  of rotors such that the first letter orients the first rotor, the second
 *  letter orients the second rotor, etc...
 *
 *  and where [_plugboardstring]* is a regular expression denoting that
 *  "_plugboardstring" may occur 0 or more times where "_" is the token
 *  separator and plugboardstring consists of two letters (at least, the other
 *  letters are ignored) which are supposed to give the plugboard wirings.
 *  
 *  If none alphabetic letters are used, or not enough letters are given in
 *  one of the tokens, or the plugboard strings define an incorrect plugboard,
 *  a KeyCreationException is thrown.
 */
import webcrypt.math.*;
import java.util.StringTokenizer;

public class EnigmaKey extends Key implements EnigmaDefinitions{

    /**
     * The information needed by the enigma machine to start encryption:
     */
    protected char[][] plugboardArray;  // The plugboard settings
    protected char[] orientations = new char[NUMROTORS]; // the rotor orientations

    private static String errstr 
	= "An Enigma key consists of "+NUMROTORS+" orientation letters\n"
	+"followed by a sequence of pairs of letters for the pluboard\n"
	+"with each token separated by an underscore.\n"
	+"For example, here's a valid key string for 3 rotors and 5 plug cables:"
	+"        GAN_BF_AQ_EG_ZN_RT";

    /**
     * Create effective key in accordance to steps 1-4 above
     */
    public EnigmaKey(String keytext) throws KeyCreationException{
	super(keytext);
	seedKey = keytext.toUpperCase();

	//if there are non-alphabetic letters other than "_" throw an exception
	for(int i=0; i<seedKey.length(); i++)
	    if( !Character.isLetter(seedKey.charAt(i))
		&& seedKey.charAt(i) != '_')
		throw new KeyCreationException(errstr);
	
	StringTokenizer keytokens = new StringTokenizer(seedKey,"_");

	// the orientations are given by the first token
	orientations = keytokens.nextToken().toCharArray();
	if (orientations.length < NUMROTORS)
	    throw new KeyCreationException(errstr);
	
	//the plugboard settings are given by all the rest:
	plugboardArray = new char[keytokens.countTokens()][2];
	String pair;
	for(int i=0; i<plugboardArray.length; i++){
	    pair = keytokens.nextToken();
	    if (pair.length() < 2)
		throw new KeyCreationException(errstr);
	    for(int j=0; j<2; j++){
		plugboardArray[i][j] = pair.charAt(j);
	    }
	}
	// the only thing that could still be wrong are clashing cables:
	try{ 
	    new EnigmaPerm(plugboardArray);
	}catch(IllegalPermutationException ipe){
	    throw new KeyCreationException(ipe.toString()+"\n"+errstr);
	}
    }

    public Object clone(){  
	Object x = null;
	try{
	    x = new EnigmaKey(seedKey); 
	}catch(KeyCreationException e){
	    // can't happen since key already got created
	}
	return x;
    }
    
    /**
     * Since Enigma encryptions is the same as decryption, reversal is trival
     */
    public Key reverseKey(){
	EnigmaKey k = (EnigmaKey)clone();
	k.setReverseKey(true);
	return k;
    }

    //    public static void main(String[] args) throws KeyCreationException{}
}






