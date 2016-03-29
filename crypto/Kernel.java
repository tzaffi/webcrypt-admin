package webcrypt.crypto;
import java.lang.reflect.*;   // useful for encrypting using unknown key type


/** 
 * The Kernel class is an abstract class that organizes the essential
 * information that is needed to completely specify the heart of a
 * cryptographic algorithm:  how a stream of text is transformed from
 * under the cryptographic transformation, and ONLY SUCH necessary information.
 * The kernel does not handle any formatting information (e.g. the
 * encrypted text may be given in 5 letter blocks, but the kernel will not
 * see this).  
 *
 * Classes directly implementing Kernel are by default secret key algorithms.
 * Public key algorithms should implement the abstract subclasss 
 * PublicKeyKernel.
 *
 * By extending webcrypt.char.CharDomains, kernel inherits 
 * a typical domain of char's and methods for manipulating char's.
 */
public abstract class Kernel extends webcrypt.character.CharDomains{

    /** 
     * May be useful to define an encryption versus decryption mode.
     * So following constants may help:
     */
    public final static boolean ENCRYPT = true;
    public final static boolean DECRYPT = false;

    /**
     * Every cipher has a block size.  For alphabetic ciphers the size is 1 char,
     * which is taken as default.
     *
     * We make the blockSize an instance field because it is possible to 
     * define the block-size based on the key used.  This is especially useful
     * for variable size based modular arithmetic ciphers.  The bigger the base,
     * the bigger the possible block size.
     */
    private int blockSize = 1;

    /**
     * The settor method for the blockSize
     */
    public void setBlockSize(int size){
	blockSize = size;
    }
    /**
     * The gettor method for the blockSize
     */
    public int getBlockSize(){
	return blockSize;
    }

    /**
     * Every kernel should know a corresponding key type that it can refer to
     * and generate keys with
     */ 
    private Class associatedKeyClass;
   
    public void setAssociatedKeyClass(Class keyClass){
	associatedKeyClass = keyClass;
    }

    /**
     * This method should be called in the implementing Kernel's constructor
     * (unless the algorithm is a very weak key-less algorithm)
     */
    public void setAssociatedKeyClass(String className){ 
	try{
	    associatedKeyClass = Class.forName(className);
	}catch(Exception e){
	    System.err.println(e);
	}
    }

    /**
     * Accessor method used for automatic key generation.
     */
    public Class getAssociatedKeyClass(){ return associatedKeyClass; }

    /**
     * Using reflection, we can allow every kernel to generate instances
     * of its associated key-class.
     *
     * If something goes wrong, a KeyCreationException with an embedded
     * message is thrown, to be caught in the gui and displayed in an
     * error message box.
     */
    public Key generateKey(String seed) throws KeyCreationException 
    {
	Key key = null;
	try{
	    Constructor constructor =
		associatedKeyClass.getDeclaredConstructor(new Class[] {Class.forName("java.lang.String")});
	    //	    System.out.println("constructor: "+constructor);
	    key = (Key)constructor.newInstance(new Object[] {seed});
	    //	    System.out.println("key: "+key);
	}catch(java.lang.reflect.InvocationTargetException ite){
	    throw (KeyCreationException)ite.getCause();  // getCause() ONLY SINCE JAVA 1.4!!!!!
	    //IF YOU WANT THIS TO COMPILE WITH Java 1.3 COMMENT OUT ABOVE AND UNCOMMENT OUT:
	    //throw new KeyCreationException(ite.toString());  
	}catch(Exception e){
	    //	    if (e instanceof java.lang.reflect.InvocationTargetException)
	    //  System.out.println(e.getCause());
	    String tmp = "unclear problem: "+e;
	    System.out.println("0: "+tmp);
	    try{
		Field f = associatedKeyClass.getField("keyErrMsg");
		System.out.println(1);
		// create dummy object so can get the error message
		Constructor constructor = 
		    associatedKeyClass.getConstructor(new Class[] {});
		System.out.println(2);
		Key k = (Key)constructor.newInstance(new Object[] {});
		System.out.println(3);
		tmp = (String)f.get(k);
		System.out.println(4);
	    }catch(Exception e2){
		System.out.println("Unanticipated problem: "+e2);
	    }
	    finally{
		throw new KeyCreationException(tmp);
	    }
	}
	// DON'T: catch(KeyCreationException e)
	return key;
    }

    /**
     * There may be some key-less algorithms (e.g. Caesar).  For these
     * we use the following dummy key:
     */ 
    public static Key DUMMYKEY = null;
    static{
	try{
	    DUMMYKEY = new Key("dummy");
	}catch(KeyCreationException e){
	    System.err.println(e);
	}
    }

    /**
     * The heart of the cipher are the decryption and encryption algorithms
     * which should be implemented.  We assume the input and output are
     * StringBuffers.  This assumption is made as for the majority of
     * cases, input and output size will be identical and all operations will
     * be occuring on local portion of the text.  Since most encryption
     * schemes keep the text the same length, and it makes sense for such
     * algorithms to operate ON the text, we insist that the void
     * versions encryptOn() and decryptOn() be implemented.  In such
     * cases, the more standard input/output versions simply call
     * on their ancestor methods here.  In cases where local operations
     * cannot take place (e.g. in ElGamal, the cipher text is twice the
     * size of the plaintext) one should over-ride encrypt()/decrypt()
     * and then define encryptOn()/decryptOn() by replacing the values
     * of the paramater string buffer
     */

    /**
     * Encrypt the input plaintext using the Key e.  Return
     * the ciphertext as output.
     */
    public StringBuffer encrypt(StringBuffer plaintext, Key e){
	encryptOn(plaintext,e);
	return plaintext; //transformed into the ciphertext
	}

    /**
     * Decrypt the input ciphertext using the Key e.  Return
     * the ciphertext as output.
     */
    public StringBuffer decrypt(StringBuffer ciphertext, Key d){
	decryptOn(ciphertext,d);
	return ciphertext; //transformed into the plaintext
    }
    
    /**
     * Encrypt the parameter plaintext and mutate it to the ciphertext
     * using the key e.
     */
    public abstract void encryptOn(StringBuffer plaintext, Key e);

    /**
     * Decrypt the parameter ciphertext and mutate it back to the plaintext
     * using the key e.
     */
    public abstract void decryptOn(StringBuffer ciphertext, Key d);

    /**
     * Override this method with the encryption algorithms name
     * (this method is called by GUI's that make use of the Kernel)
     */
    public abstract String toString();  //Algorithm name


    public void test(){ alphanumeric2base10("hello"); }
    
}













