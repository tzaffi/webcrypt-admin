package webcrypt.crypto;

/**
 * The Vigenere class is a simple implementation of Kernel
 * which implements all the basic features of a secret key stream cipher.
 * All characters are capitalized, and then shifted around
 * according to their position and the relevant portion of the key word.
 * The key is a keyword which is used cyclically with A standing
 * for no shift while Z standing for -1 shift.
 *
 * EG:  Suppose the keyword is SECRET and the plaintext was ATTACK BAGDAD
 * Then the cipher text is computed as follows:
 *
 * plaintext--->          A  T  T  A  C  K  B  A  G  D  A  D
 * cycled key word --->   S  E  C  R  E  T  S  E  C  R  E  T  
 * 
 * which is equivalent to:
 * plaintext--->          A  T  T  A  C  K  B  A  G  D  A  D
 * numeric shift--->  +  18  4  2 17  4 19 18  4  2 17  4 19 
 *                    ______________________________________
 * result --->            S  X  V  K  G  E  U  E  I  V  E  X
 *
 * The default operation on non-letters is to ignore the character
 * by not transforming it, and by not advancing the key cycling.
 *
 */
public class Vigenere extends Kernel{

    public String toString(){
	return "Vigenere"; //"Vigen\u008cre";
    }

    public Vigenere(){
	setAssociatedKeyClass("webcrypt.crypto.VigenereKey");
	setDomain(LETTERS);
    }

    /**
     * As the Vigenere cipher acts locally on the text without changing
     * the size, implement the void version
     */
    public void encryptOn(StringBuffer plaintext, Key e){
	VigenereKey vk = (VigenereKey)e; 
	char[] ek = vk.getEffectiveKey();  //Effective Key
	if(ek == null || ek.length == 0) //trivializ bad key
	    ek = new char[] { (char)0 };
	int keylength = ek.length;
	int j = 0; // rotating key-index

	int len = plaintext.length();
	for (int i=0; i<len; i++){
	    char c = plaintext.charAt(i);
	    if(inDomain(c)){ //then operate on c
		if( (int)c > 90 ) // lower case
		    c -= 32; // convert to upper case
		c -= 65; // now can operate mod-26:
		c += ek[j];
		c %= 26;
		plaintext.setCharAt(i,(char)(c+65));

		j = (j+1)%keylength;  //next key element
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
     * Simple test of Vigenere on command line
     */
    public static void main(String[] args){
	//	System.out.println("got here");
	Vigenere vigenere = new Vigenere();
	System.out.println(""+vigenere);
	System.out.println("PLAINTEXT:\n"+args[0]);
	//	System.out.println("CIPHERTEXT:\n"+vigenere.encrypt(new StringBuffer(args[0]),));
	//	System.out.println("PLAINTEXT(CIPHERTEXT):\n"
	//	   +vigenere.decrypt(vigenere.encrypt(new StringBuffer(args[0]),"3")));
    }


}
