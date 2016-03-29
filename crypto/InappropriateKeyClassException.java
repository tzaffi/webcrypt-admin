package webcrypt.crypto;

/**
 * An exception to be thrown whenever a Key class is used inappropriately.
 * Currently there is only one usage:
 *
 * When trying to associate a private key to a public algorithm.
 */

public class InappropriateKeyClassException extends Exception{
    public InappropriateKeyClassException(String errstr){
	super(errstr);
    }
}
