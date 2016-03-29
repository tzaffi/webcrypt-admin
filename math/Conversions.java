package webcrypt.math;

/**
 * Static class useful for converting between various numerical types
 */
import java.math.BigInteger;

public class Conversions{

    public static BigInteger int2BigInteger(int x){
	return new BigInteger(""+x);
    }

    public static BigInteger[] int2BigInteger(int[] x){
	BigInteger[] output = new BigInteger[x.length];
	for(int i=0; i<x.length; i++) output[i] = int2BigInteger(x[i]);
	return output;
    }
}
