package webcrypt.math;

/**
 * To be thrown if trying to create a permuation that
 * violates the given permutation class definition
 */
public class IllegalPermutationException extends Exception{
    public IllegalPermutationException(String errstr){
	super(errstr);
    }
}
