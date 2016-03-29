package webcrypt.math;
/**
 * CODE BY David Bishop.  Non-commercial use only!!!!
 *
 * I added code for computing determinants and inverting using the determinant
 * formula for dimensions 2 and 3.  The determinant code is extremely
 * slow for large dimensions because the recursive algorithm is used 
 * since Gaussian elimination breaks down over general modulii.
 *
 * -Zeph Grunschlag
 */
import java.math.BigInteger;
import java.security.*;
import java.util.*;
//ModSquareMatrix objects inherit all methods from ModMatrix
public class ModSquareMatrix extends ModMatrix {

   //Creates a square matrix with random entries
   //Or, it creates a matrix with all zeros
   //Another parameter specifies whether or not you wish the random
   //matrix to be invertible; if NOT, matrix may still be invertible by accident
   public ModSquareMatrix(int s,BigInteger m,boolean makeZero,boolean makeInvertible) throws MatricesNonConformableException {
         //Call a auperconstructor from ModMatrix-make the zero matrix, or a matrix with random entries
         super(s,s,m,makeZero);

         //Zero matrix is not invertible
         if (makeZero&&makeInvertible) throw new IllegalArgumentException ("Zero matrix cannot be inverted!");

         //A random invertible matrix is desired
         if (makeInvertible) {
            Random r=new Random();
            SecureRandom sr=new SecureRandom();
            boolean done=false;
            //Do this until the matrix inverts
            while (!done) {
               try {
                  //Try to take the inverse-may throw an exception if not invertible
                  this.inverse();
                  done=true;
               } catch (SingularMatrixException sme) {
                  //Change a random entry in the matrix
                  int row=Math.abs(r.nextInt())%numRows+1;
                  int col=Math.abs(r.nextInt())%numCols+1;
                  BigInteger value=new BigInteger(modulus.bitLength(),sr).mod(modulus);
                  this.setElement(row,col,value);
               } catch (ArithmeticException ae) {
                  //Change a random entry in the matrix
                  int row=Math.abs(r.nextInt())%numRows+1;
                  int col=Math.abs(r.nextInt())%numCols+1;
                  BigInteger value=new BigInteger(modulus.bitLength(),sr).mod(modulus);
                  this.setElement(row,col,value);
               }
            }
         }
   }

   //Makes a square matrix from a 1D array of values
   public ModSquareMatrix(int s,BigInteger[] a,BigInteger m) {
         super(s,s,a,m);
   }

    /**
     * ZEPH ADDED:  Constructor using int arrays.
     *
     * s is the size
     * a is the 1-D array of integer values
     * m is the modules
     */
   public ModSquareMatrix(int s, int[] a, int m) {
       super(s,s,a,m);
   }

   //Makes a copy of a matrix
   public ModSquareMatrix(ModSquareMatrix m) {
         array=new BigInteger[m.numRows+1][m.numCols+1];
         numRows=m.numRows;
         numCols=m.numCols;
         modulus=m.modulus;
         for (int i=1;i<=m.numRows;i++) {
             for (int j=1;j<=m.numCols;j++) {
                 array[i][j]=new BigInteger(m.array[i][j].toString());
             }
         }
   }

    /**
     * ZEPH ADDITION:
     * 
     * The missing "cast" down.  Useful after we used a ModMatrix outputting
     * method that didn't really change the dimensions!!!!
   //Makes a copy of a matrix
   public ModSquareMatrix(ModSquareMatrix m) {
         array=new BigInteger[m.numRows+1][m.numCols+1];
         numRows=m.numRows;
         numCols=m.numCols;
         modulus=m.modulus;
         for (int i=1;i<=m.numRows;i++) {
             for (int j=1;j<=m.numCols;j++) {
                 array[i][j]=new BigInteger(m.array[i][j].toString());
             }
         }
   }
     */

   //Method which uses Gaussian elimination to solve AX=B mod m for X
   //A is the ModSquarematrix calling the method
   //B is the Modmatrix constants - need not be a Vector
   //X is the ModMatrix returned
   public ModMatrix gaussianSolve(ModMatrix constants) throws MatricesNonConformableException,SingularMatrixException {
      //This method only works when the modulus is prime
      if (!modulus.isProbablePrime(16)) throw new IllegalArgumentException("Gaussian elimination method currently requires modulus to be prime!");
      //Copy the matrices and modify the copies
      ModSquareMatrix mat=new ModSquareMatrix(this);
      ModMatrix b;

      //If the ModMatrix constants is square, the answer should also be a ModSquareMatrix object (not just a ModMatrix)
      //Check for this here
      if (constants instanceof ModSquareMatrix) b=new ModSquareMatrix((ModSquareMatrix)constants);
      else b=new ModMatrix(constants);

      //Check if matrices are of compatible size first
      if (b.numRows!=mat.numRows) throw new MatricesNonConformableException("Matrix of coefficients and matrix of constants have different # of rows!");

      //Work the rows, starting with the first row
      int currentRow=1;
      while (currentRow<=mat.numRows) {
         int i=currentRow;
         //Make sure diagonal element is nonzero, if possible, by swapping
         while (i<=mat.numRows&&mat.array[i][currentRow].equals(BigIntegerMath.ZERO)) i++;
         if (i>mat.numRows) throw new SingularMatrixException("Linear dependence exists here!");
         //Swap with a row not having zero in diagonal position
         if (currentRow!=i) swapRows(mat,b,currentRow,i);
         //Now, you must produce all zeros below and above the diagonal element
         i=1;
         //Multiply each row by the proper scalar
         while (i<=mat.numRows) {
            if (i!=currentRow) {
               BigInteger scalar=mat.array[i][currentRow];
               if (!scalar.equals(BigIntegerMath.ZERO)) {
                  multiplyRow(mat,b,i,mat.array[currentRow][currentRow]);
                  multiplyRow(mat,b,currentRow,scalar);
                  //Replace row i with row i minus diagonal row
                  subtractRow(mat,b,i,currentRow);
               }
            }
            i++;
         }
         currentRow++;
      }
      //Now, produce 1's along main diagonal by multiplying by an inverse
      for (int index=1;index<=mat.numRows;index++) {
         multiplyRow(mat,b,index,mat.array[index][index].modInverse(modulus));
      }
      //Remember, b may be a square matrix-polymorphism takes care of this here
      return b;
   }

   //This method exists in case the answer is actually a square matrix
   public ModSquareMatrix gaussianSolve(ModSquareMatrix constants) throws MatricesNonConformableException,SingularMatrixException {
      return (ModSquareMatrix) gaussianSolve((ModMatrix)constants);
   }

   //Used by gaussianSolve to multiply a row by some scalar
   private void multiplyRow(ModSquareMatrix mat,ModMatrix b,int i,BigInteger scalar) {
      //Multiplies row i by scalar-answer replaces i-th row
      for (int k=1;k<=mat.numCols;k++) mat.array[i][k]=BigIntegerMath.lnr(mat.array[i][k].multiply(scalar),mat.modulus);
      for (int k=1;k<=b.numCols;k++) b.array[i][k]=BigIntegerMath.lnr(b.array[i][k].multiply(scalar),mat.modulus);
   }

   //Used by gaussianSolve to subtract one row from another
   private void subtractRow(ModSquareMatrix mat,ModMatrix b,int i,int j) {
      //Subtracts row j from row i; answer replaces row i
      for (int k=1;k<=mat.numCols;k++) mat.array[i][k]=BigIntegerMath.lnr(mat.array[i][k].subtract(mat.array[j][k]),mat.modulus);
      for (int k=1;k<=b.numCols;k++) b.array[i][k]=BigIntegerMath.lnr(b.array[i][k].subtract(b.array[j][k]),mat.modulus);
   }

   //Used by gaussianSolve to swap two rows
   private void swapRows(ModSquareMatrix mat,ModMatrix b,int r1,int r2) {
      BigInteger temp;
      for (int j=1;j<=mat.numCols;j++) {
         temp=mat.array[r1][j];
         mat.array[r1][j]=mat.array[r2][j];
         mat.array[r2][j]=temp;
      }
      for (int j=1;j<=b.numCols;j++) {
         temp=b.array[r1][j];
         b.array[r1][j]=b.array[r2][j];
         b.array[r2][j]=temp;
      }
   }

   //Method produces an inverse of A (if possible) by using gaussianSolve on AX=I mod m
   //where I is an identity matrix
   public ModSquareMatrix inverse() throws MatricesNonConformableException, SingularMatrixException {
      //See the ModIdentityMatrix class-subclass of ModSquareMatrix
      return gaussianSolve(new ModIdentityMatrix(numRows,modulus));
   }

    /** 
     * ZEPH ADDITION:
     *
     * Compute the determinant using the slow recursive algorithm.
     */
    public BigInteger det(){
	int s = rows();
	if ( s == 1 )// 1x1 matrix
	    return getElement(1,1);
	ModSquareMatrix minor;
	BigInteger sign = new BigInteger("1");
	BigInteger negone = new BigInteger("-1");
	BigInteger d = new BigInteger("0");
	for(int i = 1; i<=s; i++){
	    //	    System.out.println("d = "+d);
	    //	    System.out.println("idx = "+i);
	    //	    System.out.println("elt = "+getElement(1,i));
	    //	    System.out.println("det(minor) = "+minor(1,i).det());
	    //	    System.out.println("minor = ");
	    //	    minor(1,i).display();
	    d = d.add( sign.multiply(getElement(1,i).multiply(minor(1,i).det())) );
	    sign = sign.multiply(negone);
	}
	return d.mod(getModulus());
    }

    /** 
     * ZEPH ADDITION:
     *
     * Compute the minor of the given indices.  The minor consist of 
     * all the entries except for the row and column indicated
     *
     * If this is a 1x1 matrix (or less), mathematical prudence implies should return [1]
     */
    public ModSquareMatrix minor(int row, int col){
	int s = rows();
	try{
	    if (s <= 1) return new ModIdentityMatrix(1,getModulus());
	}catch(MatricesNonConformableException e){
	    System.out.println("THIS SHOULD NEVER HAPPEN!");
	}

	ModSquareMatrix output = null;
	try{
	    output = new ModSquareMatrix(s-1,getModulus(),true,false);
	}catch(MatricesNonConformableException e){
	    System.out.println("THIS SHOULD NEVER HAPPEN!");
	}
	//Now fill in the entry starting from the upper left corner and going counter clockwise:
	//Upper-left corner
	int i,j;
	for( i = 1 ; i < row; i++)
	    for( j = 1 ; j < col; j++)
		output.setElement(i,j,getElement(i,j));
	//Lower-left corner		
	for(i = row+1 ; i <= s; i++)
	    for( j = 1 ; j < col; j++)
		output.setElement(i-1,j,getElement(i,j));
	//Lower-right corner		
	for(i = row+1 ; i <= s; i++)
	    for(j = col+1 ; j <=s; j++)
		output.setElement(i-1,j-1,getElement(i,j));
	//Upper-right corner	       
	for(i = 1 ; i < row; i++)
	    for(j = col+1 ; j <=s; j++)
		output.setElement(i,j-1,getElement(i,j));

	return output;
    }
    /**
     * ZEPH ADDITION:
     *
     * Test if BigInteger d is invertible module m
     */
    public static boolean isInvertibleModulo(BigInteger d, BigInteger m){
	try{
	    d.modInverse(m);
	}catch(ArithmeticException e){
	    return false;
	}
	return true;
    }

    /**
     * Versions of the above for different numeric types:
     */
    public static boolean isInvertibleModulo(BigInteger d, int m){
	return isInvertibleModulo(d,new BigInteger(""+m));
    }
    public static boolean isInvertibleModulo(int d, BigInteger m){
	return isInvertibleModulo(new BigInteger(""+d),m);
    }
    public static boolean isInvertibleModulo(int d, int m){
	return isInvertibleModulo(new BigInteger(""+d),new BigInteger(""+m));
    }

    /** 
     * ZEPH ADDITION:
     *
     * Compute the inverse the really slow way.  (Useful when modulus not prime).
     *
     * Not recommended for usage in high dimensions.
     */
    public ModSquareMatrix slowInverse() throws SingularMatrixException{
	BigInteger d = det();
	try{
	    d = d.modInverse(getModulus());
	}catch(ArithmeticException e){
	   throw new SingularMatrixException(d+" is not invertible modulo "+getModulus());
	}

	int s = rows();
	ModSquareMatrix output = null;
	try{
	    output = new ModSquareMatrix(s,getModulus(),true,false);
	    if (s == 1)
		output.setElement(1,1,d);
	    else{
		BigInteger negone = new BigInteger("-1");
		BigInteger coef;
		BigInteger neg_d = d.multiply(negone);
		for(int i=1; i<=s; i++)
		    for(int j=1; j<=s; j++){ // i and j transposed!
			coef = ( (i-j)%2==0 ? d : neg_d );
			output.setElement(j,i,minor(i,j).det().multiply(coef).mod(getModulus()) );
		    }
	    }
	}catch(MatricesNonConformableException e){
	    System.out.println("THIS SHOULD NEVER HAPPEN!");
	}
	return output;
    }

    /**
     * ZEPH ADDED:
     *
     * Input:
     * dim     - the square matrix dimension
     * modulus - the modulus to do operations under
     * a11 ... ann - the entries (n  = dim)
     *
     *     / a11 a12 ...  \
     * M = |              | is constructed
     *     \      ... ann /
     *
     * Output:  A printout explaining:
     * determinant(M)
     * 
     * If M is invertible, the inverse is computed and displayed.
     *
     * USAGE:
     *>java webcrypt.math.ModSquareMatrix dim modulus a11 a12 a13 .... ann-1 ann
     */
    public static void main(String[] args) throws NumberFormatException,SingularMatrixException,MatricesNonConformableException{
	BigInteger[] a = new BigInteger[args.length-2];
	for( int i=0; i<a.length; i++) 
	    a[i] = new BigInteger(args[i+2]);
	ModSquareMatrix m = new ModSquareMatrix(Integer.parseInt(args[0]),
						a,new BigInteger(args[1]));
	m.display();
	/*	
        System.out.println("00: "+m.getElement(0,0));
	System.out.println("01: "+m.getElement(0,1));
	System.out.println("11: "+m.getElement(1,1));
	*/
	System.out.println("det(m) = "+m.det());
	ModSquareMatrix inv = m.slowInverse();
	System.out.println("Inverse: ");
	inv.display();
	System.out.println("m*inverse(m) = Identity Matrix? -->");
	m.multiply(inv).display();
	/*
	System.out.println("\n\nExperiment mod 26.  Percentage of 2x2 invertible matrices:");
	Random r=new Random();
	int[] randarray = new int[4];
	int numsingular = 0;
	for(int i = 1; i <= 100; i++){
	    for(int j = 0; j<4; j++) randarray[j] = r.nextInt(26);
	    m = new ModSquareMatrix(2,randarray,26);
	    BigInteger d = m.det();
	    if (!isInvertibleModulo(d,26)) numsingular++;
	}
	System.out.println(numsingular+" out of 100 were singular");
	*/
    }
}
