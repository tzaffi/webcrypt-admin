package webcrypt.math.zg33;
import java.math.BigInteger;

/**
 * Class CRT - "Chinese Remainder Theorem"
 */
public class CRT{

    /**
     * You'll need some private fields that get computed in the constructor
     * It's up to you to figure out what to include.
     */

    /**
     * Constructor processes all the modulii n[0] ... n[k-1].
     * If not all pairwise relatively prime, throw an Arithmetic exception with a relevant message.
     *
     * The input is an array of BigInteger's represententing the modulii
     *
     * If modulii all pairwise relatively prime, you should calculate and store the information
     * that defines the function "psi"
     */
    public CRT( BigInteger[] n) throws ArithmeticException{
	//keep the following line
	if ( n == null || n.length == 0 )
	    throw new ArithmeticException("Non-empty set of modulii required for CRT.");
	
	//next you should test the modulii and throw an ArithmeticException if any problems

	//of all goes well, calculate the informtion needed for psi
    }

    /**
     * INPUT:  x[0] ... x[k-1] 
     * OUTPUT: X in the range [0,N-1] such that for all i, x mod n[i] == x[i] mod n[i].
     * Here N is the product of all the modulii:  N = n[0] * n[1] * ... * n[k-1]
     */
    public BigInteger psi( BigInteger[] x){
	// dummy return for compilation purposes.
	// replace by the actual function
	return BigInteger.ZERO; 
    }

}