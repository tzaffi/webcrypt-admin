package webcrypt.crypto;
import java.util.StringTokenizer;

/** 
 *  The NumericKey class implements a single integer key.
 *
 *  Incorrect input generate a key creation exception.
 */
public class NumericKey extends Key{
    /** Paramaterless constructor useful when there are problems with the key */
    public NumericKey(){
    }

    String keyErrMsg = "\nKey should consist of single integer.";


    private String readableKey;
    private int effectiveKey;

    public String getSeedKey(){
	return seedKey;
    }
    public String getReadableKey(){
	return readableKey;
    }
    public int getEffectiveKey(){
	return effectiveKey;
    }

    /**
     * Create effective key in accordance to steps 1-4 above
     */
    public NumericKey(String keytext) throws KeyCreationException{
	super(keytext);
	seedKey = keytext;
	
	boolean problem = false;
	try{
	    effectiveKey = Integer.parseInt(seedKey);
	}catch(NumberFormatException e){
	    problem = true;
	}

	if(problem || this == null){
	    //	    System.out.println("problem in key generation process");
	    throw new KeyCreationException(keyErrMsg);
	}
	//create readable key (a,b)
	readableKey = ""+effectiveKey;
    }

    public Object clone(){  
	Object x = null;
	try{x = new NumericKey(seedKey); }
	catch(KeyCreationException e){
	    //can't happen
	}
	return x;
    }

    public static void main(String[] args) throws KeyCreationException{
	/*	NumericKey vkey = new NumericKey(args[0]);
	System.out.println("Seed:      "+vkey.getSeedKey());
	System.out.println("Readable:  "+vkey.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkey.getEffectiveKey().length; i++)
	    System.out.print(0+vkey.getEffectiveKey()[i]+",");
	System.out.println("");
	NumericKey vkclone = (NumericKey)vkey.clone();
	System.out.println("Clone reverse? "+vkclone.isReverseKey());
	System.out.println("Clone Seed:      "+vkclone.getSeedKey());
	System.out.println("Clone Readable:  "+vkclone.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkclone.getEffectiveKey().length; i++)
	    System.out.print(0+vkclone.getEffectiveKey()[i]+",");
	System.out.println("");
	NumericKey vkreverse = (NumericKey)vkey.reverseKey();
	System.out.println("Reverse is reverse? "+vkreverse.isReverseKey());
	System.out.println("Reverse Seed:      "+vkreverse.getSeedKey());
	System.out.println("Reverse Readable:  "+vkreverse.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkreverse.getEffectiveKey().length; i++)
	    System.out.print(0+vkreverse.getEffectiveKey()[i]+",");
	System.out.println("");
	*/
    }

}


