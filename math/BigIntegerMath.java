package webcrypt.math;
/**
 * CODE BY David Bishop.  Non-commercial use only!!!!
 */

import java.math.*;
import java.security.SecureRandom;
import java.util.*;
public class BigIntegerMath {
   //Define some BigInteger constants; this is handy for comparisons
   static final BigInteger ZERO=new BigInteger("0");
   static final BigInteger ONE=new BigInteger("1");
   static final BigInteger TWO=new BigInteger("2");
   static final BigInteger THREE=new BigInteger("3");
   static final BigInteger FOUR=new BigInteger("4");

   //A nonrecursive version of euclid.  It returns an array answer of 3 BigIntegers
   //answer[0] is the gcd, answer[1] is the coefficient of a, answer[2] the coeff of b
   public static BigInteger[] euclid(BigInteger a,BigInteger b) throws IllegalArgumentException {
      //Throw an exception if either argument is not positive
      if (a.compareTo(ZERO)<=0||b.compareTo(ZERO)<=0) throw new IllegalArgumentException("Euclid requires both arguments to be positive!");
      BigInteger[] answer=new BigInteger[3];
      //Set up all the initial table entries
      BigInteger r0=new BigInteger(a.toByteArray());
      BigInteger r1=new BigInteger(b.toByteArray());
      BigInteger s0=new BigInteger("1");
      BigInteger s1=new BigInteger("0");
      BigInteger t0=new BigInteger("0");
      BigInteger t1=new BigInteger("1");
      BigInteger q1=r0.divide(r1);
      BigInteger r2=r0.mod(r1);
      BigInteger s2,t2;
      //When r2 becomes zero, the previous table entries are the answers
      while (r2.compareTo(ZERO)>0) {
         s2=s0.subtract(q1.multiply(s1)); s0=s1; s1=s2;
         t2=t0.subtract(q1.multiply(t1)); t0=t1; t1=t2;
         r0=r1; r1=r2; q1=r0.divide(r1); r2=r0.mod(r1);
      }
      answer[0]=r1; answer[1]=s1; answer[2]=t1;
      return answer;
   }

   //Returns a particular solution (if any solutions exist) of linear
   //diophantine equations of the form ax+by=c.
   //Returns an array z of 3 elements; z[0] is the gcd of a and b,
   //z[1] is x, z[2] is y.
   public static BigInteger[] solveLinearDiophantine(BigInteger a, BigInteger b, BigInteger c) throws IllegalArgumentException {
      if (a.compareTo(ZERO)<=0||b.compareTo(ZERO)<=0||c.compareTo(ZERO)<0)
      throw new IllegalArgumentException("All constants must be positive in linear diophantine equation.");
      BigInteger[] euclidAnswers=euclid(a,b);
      if (c.mod(euclidAnswers[0]).compareTo(ZERO)!=0)
      throw new IllegalArgumentException("No solution since "+euclidAnswers[0]+" does not divide "+c+".");
      BigInteger[] answer=new BigInteger[3];
      BigInteger q=c.divide(euclidAnswers[0]);
      answer[0]=euclidAnswers[0];
      answer[1]=q.multiply(euclidAnswers[1]);
      answer[2]=q.multiply(euclidAnswers[2]);
      return answer;
   }

   //Computes the least nonnegative residue of b mod m, where m>0.
   public static BigInteger lnr(BigInteger b, BigInteger m) {
      if (m.compareTo(ZERO)<=0) throw new IllegalArgumentException("Modulus must be positive.");
      BigInteger answer=b.mod(m);
      return (answer.compareTo(ZERO)<0)?answer.add(m):answer;
   }

   //Returns a solution of x for linear congruences of the form
   //ax congruent to b (mod m)
   //Returns an array z; z[0] is the gcd of a and m, z[1] is the solution for x
   public static BigInteger[] solveLinearCongruence(BigInteger a, BigInteger b, BigInteger m) {
      BigInteger[] answers=solveLinearDiophantine(lnr(a,m),m,lnr(b,m));
      return answers;
   }

   //Implements the Rabin-Miller test.
   //Number of different bases to try is passed in as an int
   //If the BigInteger passes all tests, returns the probabilty it is prime as a double.
   //Returns zero if the BigInteger is determined to be composite.
   public static double primeProbability(BigInteger n,int numPasses,SecureRandom sr) {
      if (n.compareTo(TWO)<0) return 0;
      BigInteger b,x;
      BigInteger nMinusOne=n.subtract(ONE);
      if (numPasses<1) throw new IllegalArgumentException("Number of bases must be positive!");
      //If the number is small, just factor it
      if (n.compareTo(new BigInteger("2000000"))<0) {
         int smalln=n.intValue();
         for (int i=2;i<=Math.sqrt(smalln);i++) if (smalln%i==0) return 0;
         return 1;
      }
      for (int i=0;i<numPasses;i++) {
         //Choose a random base smaller than n
         b=new BigInteger(n.bitLength()-1,sr);
         //Test Fermat's condition first
         x=b.modPow(nMinusOne,n);
         if (!x.equals(ONE)) return 0.0;//not prime
         //Divide n-1 by 2
         BigInteger[] dr=nMinusOne.divideAndRemainder(TWO);
         //Perform the root tests
         while (dr[1].equals(ZERO)) {
            x=b.modPow(dr[0],n);
            //if you get -1, this is a PASS; get out
            if (x.equals(nMinusOne)) break;//pass
            //Now, if its not -1 or 1, this is a FAIL, return 0
            if (!x.equals(ONE)) return 0.0;//not prime
            //If its 1, so far its a pass
            //We can continue with the test; divide by 2
            dr=dr[0].divideAndRemainder(TWO);
         }
      }
      //Only way to get here is by passing all tests
      return 1.0-Math.pow(0.25,numPasses);
   }

   //Finds simultaneous solutions to a linear system of congruences
   //involving only one variable and multiple moduli.
   public static BigInteger[] solveCRT(BigInteger[] residue, BigInteger[] modulus) {
   //Test if the number of moduli and residues match
      if (residue.length!=modulus.length)
         throw new IllegalArgumentException("Residues and moduli are in different amounts.");
   //Test if the moduli are pairwise relatively prime
      for (int i=0; i<modulus.length-1; i++) {
         for (int j=i+1; j<modulus.length; j++) {
            if (!(modulus[i].gcd(modulus[j]).equals(ONE)))
               throw new IllegalArgumentException("Moduli are not pairwise relatively prime.");
         }
      }
   //Form the product of the individual moduli
      BigInteger M=new BigInteger("1");
      for (int i=0; i<modulus.length; i++)
         M=M.multiply(modulus[i]);
   //Form the solution as in Chinese Remainder Theorem
      BigInteger solution=new BigInteger("0");
      for (int i=0;i<modulus.length; i++) {
         BigInteger Mi=M.divide(modulus[i]);
         solution=solution.add(residue[i].multiply(Mi).multiply(Mi.modInverse(modulus[i])));
      }
      solution=lnr(solution,M);
   //Answer must be returned as a two dimensional array.
      BigInteger[] result=new BigInteger[2];
      result[0]=solution;
      result[1]=M;
      return result;
   }

   //Solves quadratic congruences ax^2+bx+c congruent to 0 mod n=pq
   //Returns four solutions when they exist
   public static BigInteger[] solveQuadratic(BigInteger a, BigInteger b, BigInteger c,
      BigInteger p, BigInteger q, int primeTolerance) {
      //Check that the factors of the modulus are distinct
      if (p.equals(q))
         throw new IllegalArgumentException("The modulus factors are not unique!");
      //Check that the factors are congruent to 3 modulo 4
      BigInteger n=p.multiply(q);
      if (!lnr(p.mod(FOUR),n).equals(THREE))
         throw new IllegalArgumentException(p+" is not of form 4k+3!");
      if (!lnr(q.mod(FOUR),n).equals(THREE))
         throw new IllegalArgumentException(q+" is not of form 4k+3!");
      //Check that the factors of the modulus are prime
      if (!p.isProbablePrime(primeTolerance))
         throw new IllegalArgumentException(p+" is not prime!");
      if (!q.isProbablePrime(primeTolerance))
         throw new IllegalArgumentException(q+" is not prime!");
      //Create the array of solutions
      BigInteger[] result=new BigInteger[4];
      //Start forming the terms
      BigInteger aInv=a.modInverse(n);
      BigInteger pInv=p.modInverse(q);
      BigInteger qInv=q.modInverse(p);
      BigInteger twoInv=TWO.modInverse(n);
      BigInteger term1=aInv.multiply(twoInv.multiply(b).modPow(TWO,n).multiply(aInv).subtract(c));
      BigInteger term2=twoInv.multiply(aInv).multiply(b);
      BigInteger t1=lnr(term1.modPow(p.add(ONE).divide(FOUR),n).subtract(term2).multiply(q).multiply(qInv),n);
      BigInteger t2=lnr(term1.modPow(q.add(ONE).divide(FOUR),n).subtract(term2).multiply(p).multiply(pInv),n);
      BigInteger t3=lnr(term1.modPow(p.add(ONE).divide(FOUR),n).negate().subtract(term2).multiply(q).multiply(qInv),n);
      BigInteger t4=lnr(term1.modPow(q.add(ONE).divide(FOUR),n).negate().subtract(term2).multiply(p).multiply(pInv),n);
      //Form the solutions
      result[0]=lnr(t1.add(t2),n);
      result[1]=lnr(t1.add(t4),n);
      result[2]=lnr(t3.add(t2),n);
      result[3]=lnr(t3.add(t4),n);
      //Check the solutions; if any are bad, throw an exception
      BigInteger x;
      for (int i=0;i<4;i++) {
         x=result[i];
         if (!lnr(a.multiply(x.multiply(x)).add(b.multiply(x)).add(c),n).equals(ZERO))
            throw new IllegalArgumentException("Solution x="+x+" does not check!");
      }
      return result;
   }

   //Monte Carlo factorization method returns a Monte Carlo factor.
   //An array holds a sequence of random numbers; must specify max
   //size of this array.
   //This puppy returns null if no factor is found.
   public static BigInteger monteCarloFactor(BigInteger n,int maxArraySize) throws IllegalArgumentException {
      if (n.compareTo(THREE)<=0) throw new IllegalArgumentException("Number to factor must be > 3");
      BigInteger[] m=new BigInteger[maxArraySize+1];
      m[0]=BigInteger.valueOf(new Random().nextInt());
      BigInteger g;
      for (int i=1;i<=maxArraySize;i++) {
         m[i]=m[i-1].multiply(m[i-1]).add(ONE).mod(n);
         if (i%2==0) {
            g=m[i].subtract(m[i/2]).gcd(n);
            if (g.compareTo(ONE)>0&&g.compareTo(n)<0) return g;
         }
      }
      return null;
   }

   //Pollard p-1 factorization-runs until a factor is found
   public static BigInteger pMinusOneFactor(BigInteger n) throws IllegalArgumentException {
      Random rand=new Random();
      BigInteger power=BigInteger.valueOf(1);
      BigInteger residue=lnr(BigInteger.valueOf(rand.nextInt()),n);
      BigInteger test=residue.subtract(ONE);
      BigInteger gcd=test.gcd(n);
      while (true) {
         while (gcd.equals(ONE)) {
            power=power.add(ONE);
            residue=residue.modPow(power,n);
            test=residue.subtract(ONE);
            gcd=test.gcd(n);
         }
         if (gcd.equals(n)) {
            power=BigInteger.valueOf(1);
            residue=lnr(BigInteger.valueOf(rand.nextInt()),n);
            test=residue.subtract(ONE);
            gcd=test.gcd(n);
         } else return gcd;
      }
   }

   public static BigInteger sqrt(BigInteger m) {
      //Uses the Newton method to find largest integer whose square does not exceed m
      //We search for a zero of f(x)=x^2-p ==>  note that derivative f'(x)=2x
      int diff=m.compareTo(ZERO);
      //Throw an exception for negative arguments
      if (diff<0) throw new IllegalArgumentException("Cannot compute square root of a negative integer!");
      //Return 0 in case m is 0
      if (diff==0) return BigInteger.valueOf(0);
      BigDecimal two=new BigDecimal(TWO);
      //Convert the parameter to a BigDecimal
      BigDecimal n=new BigDecimal(m);
      //Begin with an initial guess-the square root will be half the size of m
      //Make a byte array at least that long, & set bit in the high order byte
      byte[] barray=new byte[m.bitLength()/16+1];
      barray[0]=(byte)255;
      //This is the first guess-it will be too high
      BigDecimal r=new BigDecimal(new BigInteger(1,barray));
      //Next approximation is computed by taking r-f(r)/f'(r)
      r=r.subtract(r.multiply(r).subtract(n).divide(r.multiply(two),BigDecimal.ROUND_UP));
      //As long as our new approximation squared exceeds m, we continue to approximate
      while (r.multiply(r).compareTo(n)>0) {
         r=r.subtract(r.multiply(r).subtract(n).divide(r.multiply(two),BigDecimal.ROUND_UP));
      }
      return r.toBigInteger();
   }

   public static BigInteger fermatFactor(BigInteger n) {

      //Disallow negative and even integers
      if (n.compareTo(ONE)<0||n.mod(TWO).equals(ZERO)) 
		throw new IllegalArgumentException(
		"Fermat factoring can only be done on odd, positive integers!");
      if (n.equals(ONE)) return BigInteger.valueOf(1);

      //Start with smallest integer > sqrt(n)
      //sqrt(n) method returns integer (truncated) square root of n
      BigInteger step = sqrt(n).add(ONE);
      //Square this and subtract n
      BigInteger diff = step.multiply(step).subtract(n);
      //Take the integer square root
      BigInteger test = sqrt(diff);

      //As long as this is not a perfect square, continue with next step value
      //This loop WILL terminate; it might take a LONG TIME
      while (!test.multiply(test).equals(diff)) {
         step = step.add(ONE);
         diff = step.multiply(step).subtract(n);
         test = sqrt(diff);
      }

      //The factor is step plus (or minus), the square root of the perfect square
      return step.add(test);

   }

   public static BigInteger logExhaustiveSearch(BigInteger base, BigInteger residue, BigInteger modulus) {
      //This algorithm solves base^x = residue (mod modulus) for x using exhaustive search
      BigInteger basePow=BigInteger.valueOf(1);
      BigInteger j;
      for (j=BigInteger.valueOf(1);j.compareTo(modulus)<0;j=j.add(ONE)) {
         basePow=basePow.multiply(base).mod(modulus);
         if (basePow.equals(residue)) break;
      }
      if (j.equals(modulus)) throw new NoSuchElementException("No solution");
      return j;
   }


   public static BigInteger logBabyStepGiantStep(BigInteger base, BigInteger residue, BigInteger modulus) {
      //This algorithm solves base^x = residue (mod modulus) for x using baby step giant step
      BigInteger m=sqrt(modulus).add(ONE);
      //Use a hash table to store the entries-use Java Hashtable class
      Hashtable h=new Hashtable();
      BigInteger basePow=BigInteger.valueOf(1);
      //Build the hash table base^j is the key, index j is the value
      for (BigInteger j=BigInteger.valueOf(0);j.compareTo(m)<0;j=j.add(ONE)) {
         h.put(basePow,j);
         basePow=basePow.multiply(base).mod(modulus);
      }
      //Compute an inverse of base^m modulo p
      BigInteger basetotheminv=base.modPow(m,modulus).modInverse(modulus);
      BigInteger y=new BigInteger(residue.toByteArray());
      //Search the hashtable for a base^j such that y=base^j for some j
      BigInteger target;
      for (BigInteger i=BigInteger.valueOf(0);i.compareTo(m)<0;i=i.add(ONE)) {
         target = (BigInteger)h.get(y);
         if (target!=null) return i.multiply(m).add(target);
         y=y.multiply(basetotheminv).mod(modulus);
      }
      throw new NoSuchElementException("No solution");
   }

}

