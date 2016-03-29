package webcrypt.math;
/**
 * CODE BY David Bishop.  Non-commercial use only!!!!
 */
import java.math.BigInteger;
//ModIdentityMatrix objects inherit all methods from ModSquareMatrix, and from ModMatrix
public class ModIdentityMatrix extends ModSquareMatrix {

   //Make a ModSquareMatrix whose diagonal elements are all 1, zeros elsewhere
  public ModIdentityMatrix(int n,BigInteger mod) throws MatricesNonConformableException {
         //Call a super constructor first, making zero matrix
         super(n,mod,true,false);
         //Set the disgonal elements to 1
         for (int i=1;i<=n;i++) array[i][i]=new BigInteger("1");
  }

}
