package webcrypt.crypto;

/** 
 * The Key class is needed to effect all useful cryptographic algorithms.
 * Indeed, Kerchkoff's law states that the security of the cryptographic 
 * method must reside in knowing the key and not in knowing the algorithm.
 * Most cryptorgraphic method will have a corresponding Key type and usage.
 * We do not make the key abstract to allow for some simple examples
 * (such as the Caesar cipher) which do not use a key and for which
 * we need not define a corresponding key-type.
 *
 * This is by default a key to be used in a secret-key scheme.  Thus
 * knowing the key, one is assumed to be able to find an inverse key
 * (if the algorithm is not naturally symmetric).  Public key algorithms
 * should extend the subclass AsymmetricKey.
 *
 * @author <a href="mailto:zeph@cs.columbia.edu">Zeph Grunschlag</a>
 */
public class Key{
    /**
     * The following field should contain a String which when used as the paramater
     * in the constructor, will create an equivalent key to this.
     */
    protected String seedKey;

    /**
     * Accessor method for seedKey
     */
    public String getSeedKey(){
	return seedKey;
    }

    public Key(){
	setErrMsg();
    }
    
    /**
     * It is useful to set this to a message that tells the user how a 
     * key is supposed to be specified in case the key is entered 
     * incorrectly.
     */
    public String keyErrMsg;

    public void setErrMsg(){
	   keyErrMsg =  "GENERIC ERROR MESSAGE";
    }

    /**
     * Keys are generated using a single string.  Each implementing
     * class should know how to parse the input string in accordance
     * with it's type.
     */
    public Key(String seedtext) throws KeyCreationException{
	seedKey = seedtext;
    }  

    // Variables that let the encryption algorithm know when can't use a key to encrypt or decrypt
    private boolean canEncryptWith = true, canDecryptWith = true;  // usally can do both (especially for symmetric)
    protected void setCanEncryptWith(boolean b){ canEncryptWith = b; }
    protected void setCanDecryptWith(boolean b){ canDecryptWith = b; }
    public boolean canEncryptWith(){ return canEncryptWith; }
    public boolean canDecryptWith(){ return canDecryptWith; }

    private String reasonCantEncryptWith = "", reasonCantDecryptWith = "";
    public String reasonCantEncryptWith(){ return reasonCantEncryptWith; }
    public String reasonCantDecryptWith(){ return reasonCantDecryptWith; }
    protected void setReasonCantEncryptWith(String s){ reasonCantEncryptWith = s; }
    protected void setReasonCantDecryptWith(String s){ reasonCantDecryptWith = s; }

    //SYMMETRIC KEY ENCRYPTION SECTION:
    private boolean isReverseKey = false;

    /**
     * Some subclasses may wish to allow reverse keys to be generated
     * E.g., these may allow to use the same algorithm for encrypting
     * as decrypting but with a reverse key.
     *
     * In such a scheme, to decrypt using the key e, one could
     * just call encrypt(text,e.reverseKey())
     */
    public boolean isReverseKey(){ return isReverseKey; }
    public void setReverseKey(boolean value){
	isReverseKey = value;
    }

    /**
     * If a key is reversable, one should implement the following
     * method in reversing a key.
     */
    public Key reverseKey(){
	return this; //dummy method returns same key unaltered
    }
    

}
