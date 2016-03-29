package webcrypt.crypto;

/**
 * An exception to be thrown whenever a problem occurs when trying to 
 * create a key.  Some examples of uses:
 * 1) Seed used was inappropriate type
 * 2) Seed used did not satisfy the specifications (e.g. components were not separated correctly)
 * 3) Key created would result in a non-decryptable kernel (e.g. in modular multiplication cipher
 *    using a number that is not relatively prime to the modulus)
 **/
public class KeyCreationException extends Exception{
    public KeyCreationException(String errstr){
	super(errstr);
    }
}
