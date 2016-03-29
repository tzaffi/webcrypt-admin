package webcrypt.crypto;
import java.util.StringTokenizer;

/** 
 *  The LinearKey class implements a two integer key (a,b)
 *  where a is the multiplier and b is the translation.
 *  Care should be taken to make sure that a is relatively
 *  prive to the modulus so that the ensuing encryption is
 *  decipherable.
 *  
 *  To generate (a,b) input the two numbers separated by
 *  a single underline symbol. 
 *
 *  E.G. to generate (11,4) use the constructor
 *  LinearKey("11_4")
 *
 *  Incorrect input generate a key creation exeption.
 */
public class LinearKey extends Key{
    /** Paramaterless constructor useful when there are problems with the key */
    public LinearKey(){
    }

    String keyErrMsg = "\nKey should consist of integer followed by underscore followed by integer.";

    private String readableKey;
    private final int[] effectiveKey = new int[2];

    public String getSeedKey(){
	return seedKey;
    }
    public String getReadableKey(){
	return readableKey;
    }
    public int[] getEffectiveKey(){
	return effectiveKey;
    }

    /**
     * Create effective key in accordance to steps 1-4 above
     */
    public LinearKey(String keytext) throws KeyCreationException{
	super(keytext);
	seedKey = keytext;
	
	//Throw exception if not of the form a_b:
	StringTokenizer st = new StringTokenizer(seedKey,"_");
	boolean problem = false;
	if(st.countTokens() == 2)
	    try{
		effectiveKey[0] = Integer.parseInt(st.nextToken());
		effectiveKey[1] = Integer.parseInt(st.nextToken());
	    }catch(NumberFormatException e){
		problem = true;
	    }
	else
	    problem = true;

	if(problem || this == null){
	    //	    System.out.println("problem in key generation process");
	    throw new KeyCreationException(keyErrMsg);
	}
	//create readable key (a,b)
	readableKey = "("+ effectiveKey[0]+", "+effectiveKey[1]+")";
    }

    public Object clone(){  
	Object x = null;
	try{x = new LinearKey(seedKey); }
	catch(KeyCreationException e){
	    //can't happen
	}
	return x;
    }

    public static void main(String[] args) throws KeyCreationException{
	LinearKey vkey = new LinearKey(args[0]);
	System.out.println("Seed:      "+vkey.getSeedKey());
	System.out.println("Readable:  "+vkey.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkey.getEffectiveKey().length; i++)
	    System.out.print(0+vkey.getEffectiveKey()[i]+",");
	System.out.println("");
	LinearKey vkclone = (LinearKey)vkey.clone();
	System.out.println("Clone reverse? "+vkclone.isReverseKey());
	System.out.println("Clone Seed:      "+vkclone.getSeedKey());
	System.out.println("Clone Readable:  "+vkclone.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkclone.getEffectiveKey().length; i++)
	    System.out.print(0+vkclone.getEffectiveKey()[i]+",");
	System.out.println("");
	LinearKey vkreverse = (LinearKey)vkey.reverseKey();
	System.out.println("Reverse is reverse? "+vkreverse.isReverseKey());
	System.out.println("Reverse Seed:      "+vkreverse.getSeedKey());
	System.out.println("Reverse Readable:  "+vkreverse.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkreverse.getEffectiveKey().length; i++)
	    System.out.print(0+vkreverse.getEffectiveKey()[i]+",");
	System.out.println("");
    }

}


