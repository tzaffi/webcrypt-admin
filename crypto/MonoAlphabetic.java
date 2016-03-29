package webcrypt.crypto;
import webcrypt.math.*;

/**
 * The MonoAlphabetic class is a substitution cipher utilizing a single general 
 * permutation on the letters.
 *
 * A keyword generates the permutation using webcrypt.math.CapsPerm
 *
 * The default operation on non-letters is to ignore the character
 * by not transforming it, and by not advancing the key cycling.
 *
 * If an exclamation mark '!' is used as the first char of the Constructor's
 * input, a function is created instead of a permuation.
 *
 */
public class MonoAlphabetic extends Kernel{
    
    public String toString(){
	return "MonoAlphabetic([!]keyword)"; 
    }

    public MonoAlphabetic(){
	setDomain(LETTERS);
	setAssociatedKeyClass("webcrypt.crypto.MonoAlphabeticKey");
    }

    /**
     * As the MonoAlphabetic cipher acts locally on the text without changing
     * the size, implement the void version
     */
    public void encryptOn(StringBuffer plaintext, Key e){
	MonoAlphabeticKey k = (MonoAlphabeticKey)e; 
	Permutation p = k.getPerm();  //permutation
	int len = plaintext.length();
	for (int i=0; i<len; i++){
	    char c = plaintext.charAt(i);
	    if(inDomain(c)){ //then operate on c
		if( (int)c > 90 ) // lower case
		    c -= 32; // convert to upper case
		// now permute in replace in string buffer
		plaintext.setCharAt(i,p.permute(c));
	    }
	}
   } 

    /**
     * Decryption is a simple variant of the above 
     */
    public void decryptOn(StringBuffer ciphertext, Key e){
	encryptOn(ciphertext,e.reverseKey());
    }

    /**
     * Simple test of MonoAlphabetic on command line
     */
    public static void main(String[] args) throws KeyCreationException{
	//	System.out.println("got here");
	MonoAlphabetic kernel = new MonoAlphabetic();
	System.out.println(""+kernel);
	System.out.println("PLAINTEXT:\n"+args[0]);
	
	MonoAlphabeticKey k = new MonoAlphabeticKey(args[1]);
	System.out.println("Key:\n"+k.getPerm());	
	StringBuffer ciphertext = kernel.encrypt(new StringBuffer(args[0]),k);
	System.out.println("CIPHERTEXT:\n"+ciphertext);
	StringBuffer possibleplain = kernel.decrypt(ciphertext,k);
	System.out.println("Encrypt and decrypt:\n"+possibleplain);
    }


}
