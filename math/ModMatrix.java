package webcrypt.math;
/**
 * CODE BY David Bishop.  Non-commercial use only!!!!
 */
import java.math.*;
import java.security.*;

public class ModMatrix {

  //A ModMatrix is a 2D array of BigIntegers
  BigInteger[][] array;

  //Number of columns/rows recorded here
  int numRows, numCols;

  //The modulus of the ModMatrix
  BigInteger modulus;

  //This is the default constructor; it does nothing-required for subclass
  public ModMatrix() {}


  //Creates a matrix with random entries having r rows, c columns,
  //Or, it creates a matrix of all zeros
  //Matrices start indexing at 1,1.  Zeroth column and row are not used.
  public ModMatrix(int r,int c,BigInteger m,boolean makeZero) {
         SecureRandom sr=new SecureRandom();
         modulus=m;
         array=new BigInteger[r+1][c+1];
         numRows=r;
         numCols=c;
         for (int i=0;i<r;i++) {
             for (int j=0;j<c;j++) {
                 //If makeZero set to true, make the zero matrix
                 if (makeZero) array[i+1][j+1]=new BigInteger("0");
                 //otherwise, make matrix with random entries
		 else array[i+1][j+1]=new BigInteger(modulus.bitLength(),sr).mod(modulus);
             }
         }
  }

    /**
     * ZEPH ADDED:  Better constructor for 0 matrix only
     */
    public ModMatrix(int r, int c, BigInteger m){
	this(r,c,m,true);
    }

  //Creates a matrix getting its values from the 1D array a.
  //If array is not long enough to fill matrix, zeros are used.
  public ModMatrix(int r,int c,BigInteger[] a, BigInteger m) {
         modulus=m;
         //Make the 2D array larger than specified-indices start at 1,1
         array=new BigInteger[r+1][c+1];
         numRows=r;
         numCols=c;
         for (int i=0;i<r;i++) {
             for (int j=0;j<c;j++) {
                 int pos=i*c+j;
                 //Set values for the matrix from the array
                 if (pos<a.length&&a[pos]!=null) 
			array[i+1][j+1]=BigIntegerMath.lnr(a[pos],m);
                 //If we have run out of input from the array, fill rest of matrix with zeros
                 else array[i+1][j+1]=new BigInteger("0");
             }
         }
  }

    /**
     * ZEPH ADDED:  Constructor using int arrays.
     *
     * r is the number of rows
     * c is the number of columns
     * a is the 1-D array of integer values
     * m is the modules
     */
    public ModMatrix(int r,int c, int[] a, int m){
	this(r,c,Conversions.int2BigInteger(a),Conversions.int2BigInteger(m));
    }

  //Makes a copy of another ModMatrix
  public ModMatrix(ModMatrix m) {
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

  //Methods declared here-get rows or columns or modulus of the ModMatrix
  public int rows() {return numRows;}
  public int columns() {return numCols;}
  public BigInteger getModulus() {return modulus;}

  //Allows one to retrieve an element.
  public BigInteger getElement(int row,int column) {return array[row][column];}

  //Allows one to set the value of an element-least nonnegative residue always used
  public void setElement(int row,int column,BigInteger value) {
         array[row][column]=BigIntegerMath.lnr(value,modulus);
  }

  //Adds two matrices together and returns result.
  public ModMatrix add(ModMatrix m) throws MatricesNonConformableException {
         ModMatrix result;
//Matrices must be the same dimensions and have same modulus 
//to be added together
         if (!modulus.equals(m.modulus)) throw new MatricesNonConformableException
	     ("These matrices cannot be added; different moduli.");
         if (numRows==m.numRows&&numCols==m.numCols) {
                  //Make a new ModMatrix for the sum-start with zero matrix
                  result=new ModMatrix(numRows,numCols,modulus,true);
                  //Add i,j-th entries of each to get i,j-th entry of result
                  for (int i=1;i<=numRows;i++) {
                      for (int j=1;j<=numCols;j++) {
                          result.array[i][j]=
				BigIntegerMath.lnr(array[i][j].add(m.array[i][j]),modulus);
                      }
                  }
         } else throw new MatricesNonConformableException
		("These matrices cannot be added; different dimensions.");
         return result;
  }

  //Subtracts 2nd matrix from 1st and returns result.
  public ModMatrix subtract(ModMatrix m) 
	throws MatricesNonConformableException {
	//Multiply the 2nd matrix by the scalar -1 then add them
	//-see multiply(BigInteger) method
     return this.add(m.multiply(new BigInteger("-1")));
  }

  //Multiplies two matrices.
  public ModMatrix multiply(ModMatrix m) 
	throws MatricesNonConformableException {
         ModMatrix result;
         //Both matrices must be using the same modulus
         if (!modulus.equals(m.modulus)) throw new MatricesNonConformableException
		("These matrices cannot be multiplied; different moduli.");
         //If # rows in 2nd matrix = # columns in 1st matrix, they can be multiplied together
         if (m.numRows==numCols) {
                  result=new ModMatrix(numRows,m.numCols,modulus,true);
                  //Move down the rows in outer loop
                  for (int i=1;i<=numRows;i++) {
                      //Multiply i-th row of 1st by j-th column of 2nd
                      for (int j=1;j<=m.numCols;j++) {
                          //Start the i,j-th entry of result at zero
                          result.array[i][j]=new BigInteger("0");
                          //i,j-th entry is sum of i,k-th entry of 1st times k,j-th entry of 2nd for all k
                          for (int k=1;k<=m.numRows;k++)
                             result.array[i][j]=
				BigIntegerMath.lnr(result.array[i][j].add(array[i][k]
				.multiply(m.array[k][j])),modulus);
                      }
                  }
         } else throw new MatricesNonConformableException
		("These matrices cannot be multiplied!");
         return result;
  }

  //Multiplies a matrix by a scalar.
  public ModMatrix multiply(BigInteger scalar) {
         ModMatrix result=new ModMatrix(numRows,numCols,modulus,true);
         for (int i=1;i<=numRows;i++)
            for (int j=1;j<=numCols;j++)
               //Multiply i,j-th entry by the scalar
               result.array[i][j]=BigIntegerMath.lnr(array[i][j].multiply(scalar),modulus);
         return result;
  }

  //Displays a matrix.  Each row has a separate line.  Issues a return at end.
  public void display() {
         for (int i=1;i<=numRows;i++) {
            for (int j=1;j<=numCols;j++) {
               System.out.print(array[i][j]+"\t");
            }
            System.out.println();
         }
  }

  //Method to produce the transpose of a matrix.
  public ModMatrix transpose() {
      ModMatrix answer=new ModMatrix(numCols,numRows,modulus,true);
      for (int i=1;i<=numRows;i++)
         for (int j=1;j<=numCols;j++)
            answer.array[j][i]=array[i][j];
      return answer;
  }

}
