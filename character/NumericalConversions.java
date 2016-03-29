package webcrypt.character;
import java.math.BigInteger;

/**
 * Static class for converting back and forth between numbers
 * represented by BigIntegers and {0,1}-Strings for any given bitlength.
 * k-bit numbers are embedded in {0,1}^k by simply padding with leading 0's
 * while {0,1}^k is embedded in k-bit numbers by simply viewing the string
 * as a standard base-2 number.
 * The bijections have the property that 0^k <----> 0 which
 * implies that the map to {0,1}-Strings miss these all-zeros
 * string 0^(k+1) when restricted away from 0.  This is a useful
 * cryptographic property for the implementation of 
 * the simplified Merkle-Damard iterated hash function
 * construction
 */
public class NumericalConversions{

    public final static BigInteger TWO = new BigInteger("2");
    public final static BigInteger MINUS_ONE = new BigInteger("-1");

    /**
     * Return the string 0^k
     */
    public static String zeros(int k){
	String temp="";
	for(int i=0; i<k; i++) temp += "0";
	return temp;
    }


    /**
     * Bijection from [ 0, 2^k - 1] ---> {0,1}^k
     * Simply pre-pend with 0's.  Bring the input 
     * into the correct range by modding.
     */
    public static String num2string( BigInteger n, int k){
	n = n.mod(TWO.shiftLeft(k-1));
	if( n.equals(BigInteger.ZERO) ) return zeros(k);
	return zeros(k-n.bitLength())+n.toString(2);
    }


    /**
     * Bijection from {0,1}^k ---> [ 0, 2^k - 1]
     * k is implicit in the length of the input.
     * If the input is not a {0,1}-string, return -1.
     */
    public static BigInteger string2num(String x){
	BigInteger n = MINUS_ONE;
	if (x==null) return n;
	int k = x.length();
	try{
	    n = new BigInteger(x,2);
	}catch(NumberFormatException nfe){}
	return n;
    }

    /**
     * Main method for testing purposes
     */
    public static void main(String[] args){
	int k = Integer.parseInt(args[0]);
	System.out.println("0^"+k+"="+zeros(k));
	BigInteger domsize = TWO.shiftLeft(k);
	String[] strarray = new String[domsize.intValue()];
	System.out.println("numbers ---> strings:");
	for(BigInteger i=BigInteger.ZERO.subtract(TWO); 
	    i.compareTo(domsize.add(TWO))<0;
	    i=i.add(BigInteger.ONE) ){
	    String str = num2string(i,k);
	    System.out.println("num2string("+i+","+k+")="+str);
	    if( i.compareTo(BigInteger.ZERO) >= 0 &&
		i.compareTo(domsize)< 0 )
		strarray[i.intValue()] = str;
	}
	for(int i=0; i<strarray.length; i++){
	    BigInteger bi = string2num(strarray[i]);
	    System.out.println("string2num("+strarray[i]+")="+bi);
	    String dblstr = strarray[i]+strarray[i];
	    System.out.println("string2num("+dblstr+")="+string2num(dblstr));

	    System.out.println("string2num("+args[1]+")="+string2num(args[1]));
	}
    }

}
