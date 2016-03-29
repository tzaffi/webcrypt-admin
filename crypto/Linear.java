package webcrypt.crypto;

/**
 * The Linear class is implements a simple linear monoalphabatic cipher.
 * All characters are capitalized, and then shifted according to
 * the modular formula:
 * Y = aX+b mod 26
 * where (a,b) compose the key.
 *
 * The default operation on non-letters is to ignore the character
 * by not transforming it.
 *
 */
public class Linear extends Kernel{

    public String toString(){
	return "Linear"; 
    }

    public Linear(){
	setDomain(LETTERS);
	setAssociatedKeyClass("webcrypt.crypto.LinearKey");
    }

    /**
     * As the Linear cipher acts locally on the text without changing
     * the size, implement the void version
     */
    public void encryptOn(StringBuffer plaintext, Key e){
	LinearKey lk = (LinearKey)e; 
	int a = lk.getEffectiveKey()[0];  //Effective Key
	int b = lk.getEffectiveKey()[1];  //Effective Key
	a = (a%26+26)%26;
	b = (b%26+26)%26;

	int len = plaintext.length();
	for (int i=0; i<len; i++){
	    char c = plaintext.charAt(i);
	    if(inDomain(c)){ //then operate on c
		if( (int)c > 90 ) // lower case
		    c -= 32; // convert to upper case
		c -= 65; // now can operate mod-26:
		c *= (a%26+26)%26;  //get into correct range!!!
		c += b;
		c %= 26;
		c += 26;
		c %= 26;
		plaintext.setCharAt(i,(char)(c+65));
	    }
	}
   } 

    /**
     * Decryption is a simple variant of the above 
     * 
     * CURRENTLY THIS IS DEAD SINCE INVERSION NOT YET IMPLEMENTED.
     */
    public void decryptOn(StringBuffer ciphertext, Key e){
	encryptOn(ciphertext,e.reverseKey());
    }

    /**
     * Simple test of Linear on command line
     */
    public static void main(String[] args) throws KeyCreationException{
	//	System.out.println("got here");
	Linear linear = new Linear();
	System.out.println(""+linear);
	Key k = new LinearKey(args[1]);
	System.out.println("PLAINTEXT:\n"+args[0]);
	System.out.println("CIPHERTEXT:\n"+linear.encrypt(new StringBuffer(args[0]),k));
	//	System.out.println("PLAINTEXT(CIPHERTEXT):\n"
	//			   +linear.decrypt(linear.encrypt(new StringBuffer(args[0]),k ));
    }


}

