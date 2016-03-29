package webcrypt.crypto.zg33;
import java.math.BigInteger;
import java.util.StringTokenizer;
import java.util.Random; // to generate the prime number p
import webcrypt.crypto.*;

/**
 * It is suggested that you modify this file for your PublicRabin cipher assignment.
 * I'll give you pointers below to where you should add/modify code. so PLEASE READ THROUGH THIS CODE.
 *
 * This code Shows:
 * 1) how blocks are supposed to be handled by Rabin's cryptosystem
 * 2) how public/private keys are handled
 * 3) how a private key is generated using a password
 *
 * Public keys have the form:
 * 
 <base-36 alphanumeric number (cap's don't matter)>
 *
 * Private keys have the form (start with exclamation mark):
 *
 ![<numbits>_]<base-63 alphanumber password (cap's matter)> 
 * 
 * If the number of bits is specified above, this is used for the block size and
 * sizes of n, p and q.  If not, the number of bits is calculated from the password length.
 *
 * To create a public key, run the main method using the command:
 *
>java webcrypt/crypto/zg33/DummyPublicRabinKey <password>
 *
 * Where password is an alpha-numeric string (no exclamation mark)
 */
public class DummyPublicRabinKey extends Key{

    // section containing important defining fields, along with
    // accessor methods for accessing, and mutator methods for changing values
    //I don't recommend messing with this code.
    private int plainBlockSize;  // number of char's per block of plaintext
    public int getPlainBlockSize(){return plainBlockSize;}
    private BigInteger[] effectiveKey = new BigInteger[3];  // effective key {n , p, q}
    public BigInteger getn(){ return effectiveKey[0]; }
    public BigInteger getp(){ return effectiveKey[1]; }
    public BigInteger getq(){ return effectiveKey[2]; }
    private boolean isPublic; //is true if this is the public key.  Set p = q = 0 in this case!!!!
    public boolean isPublic(){ return isPublic; }
    public boolean isPrivate(){ return !isPublic; }

    // Standard toString method.  Returns info that is necessary for generating copy of this key.
    //I don't recommend messing with this code.
    public String toString(){
	return seedKey;
    }

    /**
     * Let x = PRIME_CERTAINTY.  Then this class is willing to tolerate
     * a probability of 2^x that the purportedly prime numbers used are
     * in actuality composites.
     *
     * A value of 20 indicates a one in a million = 2^20 non-primality tolerance.
     *
     * I don't recommend messing with this code.  Use this constant when you're creating p , q and n.
     */
    public final static int PRIME_CERTAINTY = 100;
    
    /**
     * Dummy constructor used only for internal work in clone method.
     *
     * I don't recommend messing with this code.
     */
    private DummyPublicRabinKey(int dummy) throws KeyCreationException{ super(""); }

    /**
     * Construct this Key
     *
     * If the first char is the exclamation point '!' generate the private key using the password
     * 
     * Else, the password should be base-36 number representing "n" in the Rabin cryptosystem
     */
    public DummyPublicRabinKey(String seedtext) throws KeyCreationException{
	//standard start-up of a Key
	//I don't recommend messing with this code. 
	super("");
	keyErrMsg = "Seed must be of the form !password or a base-36 alphanumeric number\n";
	seedKey = seedtext;
	String problem = "NONE";  // detect problem if at the end problem doesn't equal "NONE"
	if( seedKey==null || seedKey.length() == 0) problem = "Empty key";
	else{
	    try{
		if (seedKey.charAt(0) == '!'){ // case of private key
		    int numbits;
		    StringTokenizer keyTokens = new StringTokenizer(seedKey.substring(1),"_");
		    if (keyTokens.countTokens() < 1 || keyTokens.countTokens() > 2)
			problem = "Incorrect number of tokens!\n";
		    else{
			//I don't recommend messing with this code:			
			if ( keyTokens.countTokens() == 1 ) { // bit length not specified		    
			    // calculate the number of bits from the size of the password.
			    // If the password has length n, then since a base-63 number encodes in
			    // up to 63^n different possibilities, the number of bits
			    // required is log_2(63^n) = n*log_2(63) 
			    // But since log_2(63) is almost log_2(64) = 6, set the number of bits to 6n
			    numbits = 6*(seedKey.length()-1);
			}
			//I don't recommend messing with this code:			
			else { // bit length specified in the first token
			    numbits = Integer.parseInt(keyTokens.nextToken());
			}
			
			isPublic = false; // case of private key
			
			// plainBlockSize in bytes set to be floor( n.bitlength / 8 )
			// so that text is fully retrievable
			// [[ EXCEPTION: if the most significant byte of the block is past 127 in ASCII, 
			//    will subtract 128 so could garble the first non-ASCII byte of a block]]
			plainBlockSize = numbits/8;

			// prg the "pseudo random generator" 
			// THIS IS A SECURITY GAP IN THE PROGRAM AS AT MOST 48 BITS OF RANDOMNESS!!!! 
			// WILL FIX LATER!!!! (keep for now)
			// method alphanumeric2base10() inherited from super-class webcrypt.character.CharDomains:
			//I don't recommend messing with this code:			
			Random prg = new Random( 
			   ( new BigInteger(
			       webcrypt.character.CharDomains.alphanumeric2base10( seedKey.substring(1) ) 
			       )
			     ).longValue() 
			   );

			// YOU HAVE TO MODIFY THIS PART OF THE CODE:
			// generate n, p and q 
			// in your program you'll have to make sure that all the requirements for (n,p,q) are upheld:
			// constructor BigInteger( numbits, prg ) 
			// - see also the constructor BigInteger( numbits, certainty, prg ) to generate primes
			// returns a random integer in the range [0,2^numbits-1]
			effectiveKey[0] = new BigInteger( numbits, prg);
			effectiveKey[1] = new BigInteger( numbits, prg);
			effectiveKey[2] = new BigInteger( numbits, prg);
		    }
		}
		else { 
		    // create a public key
		    //I don't recommend messing with this code:			
		    isPublic = true; // case of public key
		    setCanDecryptWith(false); // cannot use public Rabin key to decrypt (or to verify signature, as with RSA)
		    setReasonCantDecryptWith("Can only decrypt with private key.");

		    // read in n, set p = q = 0 and figure out block size
		    //I don't recommend messing with this code:			
		    BigInteger n = new BigInteger(seedKey,36); // viewing seedKey as a base-36 number
		    BigInteger p = BigInteger.ZERO, q = p;
		    effectiveKey = new BigInteger[] {n, p, q};
		    // plainBlockSize in bytes set to be floor( n.bitlength / 8 )
		    // so that text is fully retrievable
		    // [[ EXCEPTION: if the most significant byte of the block is past 127 in ASCII, 
		    //    will subtract 128 so could garble the first non-ASCII byte of a block]]
		    plainBlockSize = getn().bitLength() / 8;
		}
		if (plainBlockSize == 0) problem = "Key is too short.\nNeed at least 8 bits.";
	    }catch (NumberFormatException nfe){
		problem = "n is not a well formatted integer\n"+nfe;
	    }catch (Exception e){
		problem = "Specific unkknown problem\n";
	    }
	}
	if (!problem.equals("NONE")) // wrong kinds of inputs
	    throw new KeyCreationException("\n"+keyErrMsg+"\n"+problem);
	}
       

    /**
     * Clone method.
     *
     *I don't recommend messing with this code:			
     */
    public Object clone(){
	DummyPublicRabinKey out = null;
	try{ out = new DummyPublicRabinKey(seedKey);
	}catch(KeyCreationException e){
	    System.out.println("THIS SHOULD NEVER HAPPEN!!!!!!");
	}
	return out;
    }
	

    /**
     * Reverse the key.  Mainly to make password it possible to use password-based encryption.
     *
     * If the key is public, return a clone of this without reversing.
     *
     * I don't recommend messing with this code:			
     */
    public Key reverseKey(){
	if ( isPublic() ){
	    //un-reversable public key, so return same key
	    return (Key)clone();
	}
	Key out = new Key();
	try{ // if got here, must be a successfully created private key
	   out = new DummyPublicRabinKey(""+getn().toString(36) );
	}catch(KeyCreationException kce){
	    System.out.println("IMPOSSIBLE!  Should not have been possible to fail in making reverse");
	}
	return out; //empty if failed
    }

    /**
     * main method used to generate public key from password
     *
     *I don't recommend messing with this code:			
     */
    public static void main(String[] args){
	try{
	    DummyPublicRabinKey dbrk = new DummyPublicRabinKey("!"+args[0]);
	    System.out.println("public key = \n"+dbrk.getn().toString(36) );
	}catch(KeyCreationException kce){
	    System.out.println(kce);
	}
    }

}
