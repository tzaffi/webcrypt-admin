package webcrypt.crypto;
import webcrypt.math.*;

/** 
 *  The MonoAlphabeticKey class implements a general substitution on one alphabet.
 *  The key is created by specifying a keyword.  To seed the details, view the file
 *              webcrypt.math.CapsPerm
 */
public class MonoAlphabeticKey extends Key{

    // The only CapsPerm property needed is the constructor, so
    // better to use more general Permuation object
    private Permutation permKey;

    public Permutation getPerm(){
	return permKey;
    }

    /**
     * Create effective key in accordance to steps 1-4 above
     */
    public MonoAlphabeticKey(String keytext) throws KeyCreationException{
	super(keytext);
	seedKey = keytext;
	// The only CapsPerm property needed is the constructor!
	try{ 
	    permKey = new CapsPerm(keytext);
	}catch(IllegalPermutationException ipe){
	    throw new KeyCreationException("Text \""+keytext+" resulted in "
					   +"IllegalPermutationException:\n"+ipe);
	}
    }

    public Object clone(){  
	Object x = null;
	try{
	    x = new MonoAlphabeticKey(seedKey); 
	}catch(KeyCreationException e){
	    // can't happen since key already got created
	}
	return x;
    }
    
    /**
     * To reverse the key, one must reverse the permutation.
     */
    public Key reverseKey(){
	MonoAlphabeticKey k = (MonoAlphabeticKey)clone();
	k.permKey.invert();
	k.setReverseKey(true);
	return k;
    }

    //    public static void main(String[] args) throws KeyCreationException{}
}

