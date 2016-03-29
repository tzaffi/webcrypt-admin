package webcrypt.crypto;

/** 
 * The PublicKeyKernel class is an abstract class that organizes the essential
 * information that is needed to completely specify the heart of a
 * cryptographic algorithm implementing public key cryptography.
 *
 * PublicKeyKernel extends the Kernel class by requiring decryption 
 * to occur only with private keys (which are defined by a flag inside
 * of the AsymmetricKey class).
 *
 * IMPORTANT: There are some uneforced requirements arising from the
 * logic of public key cryptography.  We have not enforced these requirements
 * using exception handling methods, etc., to avoid having the code get 
 * too ridiculously bloated.  Here are the requirements:
 *
 * 1) The only operation that can be done using the public key is encrypt(),
 * 2) Decryption and encryption is the same operation for private keys
 * 3) The encrypt(decrypt()) operation results in the original text (as does decrypt(encrypt()) )
 * 4) Reversing a private key is allowed and results in a public key.
 * 5) Reversing a public key is not allowed.
 */
public abstract class PublicKeyKernel extends Kernel{
    /**
     * Make sure that any associated class is of an asymmetric key class.
     */ 
    public void setAssociatedKeyClass(String className){
	//	System.out.println(0);    
	try{//following line same as "super" method. Repeat since overrides disallowed
	    super.setAssociatedKeyClass(className);
	    Class keyClass = getAssociatedKeyClass();
	    //	    System.out.println(0);    
	    Class pkClass = Class.forName("webcrypt.crypto.AsymmetricKey");
	    //	    System.out.println(0);    
	    if (!pkClass.isAssignableFrom(keyClass))
		throw new InappropriateKeyClassException("Not a public key: "+keyClass);
	    //	    System.out.println(0);    
	}catch(Exception e){
	    System.err.println(e);
	}
    }
    
    /**
     * During standard operation, ecryption occurs using the public key
     * and decryption using the private key.  However, for authentication,
     * one may wish to encrypt using the private key and decrypt using the
     * private key.  Thus we define the following two methods.
     *
     * If one wants a shorter signature, one should override these
     * methods using a good hash function in the beginning.
     */
    public StringBuffer signature(StringBuffer plaintext, AsymmetricKey e)
	throws KeyCreationException{
	if ( e.isPrivate() )
	    throw new KeyCreationException("Cannot sign using public key.");
	return encrypt(plaintext,e);
    }
    public StringBuffer verification(StringBuffer ciphertext, AsymmetricKey e)
	throws KeyCreationException{
	if ( !e.isPublic() )
	    throw new KeyCreationException("Verification must use public key.");
	return encrypt(ciphertext,e);
    }

}
