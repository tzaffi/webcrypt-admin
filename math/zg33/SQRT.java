package webcrypt.math.zg33;
import java.math.BigInteger;

/**
 * Class SQRT - used for computing square roots module a prime p which is 3 mod 4 (Blum integer)
 *
 * Should be a "static class" such as java.lang.Math where all methods are static and
 * no Objects can be instantiated
 */
public class SQRT{

    /**
     * May want to include some helper methods here:
     */

    /**
     * This computes the square root of y modula p.
     *
     * Throws an ArithmeticException if p is not prime (use BigInteger's method
     *  isProbablePrime() with certainty set to bout 100) or p mod 4 isn't 3.
     *
     * INPUT:  y and p
     * OUTPUT: an array x[0], x[1], etc. such containing all the square roots
     * of y modulo p.
     * If no square roots, should return an array of length 0.
     */
    public  static BigInteger[] compute(BigInteger y, BigInteger p) throws ArithmeticException{
	BigInteger three = new BigInteger("3");
	BigInteger four = new BigInteger("4");

	// throw an exception if p not 3 mod 4
	if (!p.mod(four).equals(three)) throw new ArithmeticException("p mod 4 != 3");
	
	// throw an exception if p isn't a prime:
	
	// dummy return for compilation purposes (only valid if no square roots)
	return new BigInteger[0];
    }

}