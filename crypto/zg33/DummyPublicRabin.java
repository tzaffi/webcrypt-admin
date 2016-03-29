package webcrypt.crypto.zg33;
import java.util.Random; 
import java.util.StringTokenizer;
import java.math.BigInteger;
import webcrypt.crypto.*;
import webcrypt.io.*; //for StandardizeFile.separatorVersion()

/**
 * DummyPublicRabin demonstrates how you public block ciphers are handled on the webcrypt platform.
 * It is suggested that you modify this file for your PublicRabin cipher assignment.
 * I'll give you pointers below to where you should add/modify code. so PLEASE READ THROUGH THIS CODE.
 *
 *
 * The text is assumed to be made of 7-bit ASCII characters.  Encrypting general unicode char's
 * will result in lost information. 
 *
 * STRING-BUFFER BEHAVIOR:
 * encryption:
 * 1. The string buffer is viewed as (8 x blocksize - 1)-bit base-2 number
 *    (the first bit of the first byte is dropped, which is okay for ASCII)
 * 2. modulus n satisfies floor(numbytes(n)) = blocksize
 * 3. The last block is padded with n-bytes each containing the number n in it (this works up to 2048 bit blocks)
 * 5. Blocks are encrypted into line-break seperated base-36 numbers.
 *
 * decryption:
 * Apply the algorithm block by block.  When get to last block, expect a repeated
 * number which gives us the number of blocks to delete, and delete it.
 * Delete the introduced whitespace as well.
 *
 * private-public key behavior:
 * throws KeyCreationException if attempting to decrypt with public-key.
 */    
public class DummyPublicRabin extends Kernel{
  
    // You need to customize this block of code.  Also rename the class appropriately.
    public DummyPublicRabin(){
	setAssociatedKeyClass("webcrypt.crypto.zg33.DummyPublicRabinKey");
	setDomain(PRINTABLE);
    }

    public String toString(){ return "DummyPublicRabin( n | ![numbits]_password )"; }

    /**
     * Encryption produces a string buffer whose length is not exactly the
     * same as plaintext.  Consequently should you encrypt() method
     * with encryptOn() making usage of it.
     *
     * Output is a list of base-36 numbers seperated by new-lines.
     *
     * Currently, all that is done is convert each input block to a number, 
     * forcible removing most significant bit to make size smaller than modulus.
     */
    public StringBuffer encrypt(StringBuffer plaintext, Key e){
	DummyPublicRabinKey drk = (DummyPublicRabinKey)e;
	BigInteger n = drk.getn();
	int plainBlockSize = drk.getPlainBlockSize();
	
	//Formula below because if no remainder, still need extra block because of buffer method.
	//I don't recommend messing with this code.
	int numblocks = plaintext.length() / plainBlockSize + 1;
	int bufferlength = plainBlockSize*numblocks - plaintext.length();
	if (bufferlength == 0) bufferlength += plainBlockSize;  // the case with a dummy buffer block

	//Now set-up the buffer text:
	//I don't recommend messing with this code.
	char bufferchar = (char)bufferlength;
	String buffer = "";
	for(int i=0; i<bufferlength; i++) buffer += bufferchar;
	String blocks = plaintext.toString()+buffer;

	//Set up the StringBuffer output
	//I don't recommend messing with this code.
	int len = plainBlockSize * numblocks;
	StringBuffer out = new StringBuffer(len);

	//I don't recommend messing with this code:
	String block; // plaintext block
	BigInteger M; // plaintext block -as an integer

	byte[] bytes = new byte[plainBlockSize];
	for(int i=0; i<numblocks; i++){ 
	    block = blocks.substring(i*plainBlockSize,(i+1)*plainBlockSize);

	    // convert the block to a string of bytes in the obvious way then convert
	    // to a biginteger viewed as a begin in base-2 from the bytes.
	    //I don't recommend messing with this code:
	    M = new BigInteger(string2bytes(block));  // string2bytes defined below

	    // HERE YOU'LL HAVE TO DO THE COMPUTATIONS DEFINED BY THE RABIN CIPHER TO MODIFY M

	    // Computation finished; ready to append the block as a base-36 number
	    //I don't recommend messing with this code:
	    out.append(M.toString(36)+"\n");  // base-36 with char set [0-9A-Z]
	}
	
	return out;
    }

    /**
     * Required method.  Wrap encrypt() since stringbuffer length changed
     *
     * Due to a serious bug in Java 1.4.1 which produces memory leaks in
     * StringBuffer objects because of some String/StringBuffer memory
     * sharing violations, we cannot replace the StringBuffer text
     * in the obvious manner (delete everything then append).
     * Instead, we must first create a temp string to store the
     * converted results of the wrapped method, and then append
     * the string to the string buffer after having deleted its contents.
     *
     * I don't recommend messing with this code:
     */
    public void encryptOn(StringBuffer text, Key e){
	String temp = encrypt(text,e).toString();
	text.delete(0,text.length());
	text.append(temp);
    }

    /**
     * Since cipher-text is longer than plaintext need to implement decrypt() and have
     * decryptOn() make use of this method.
     *
     * Take a list of line separated base-36 numbers and convert back to text
     *
     * You'll have to modify this heavily to implement the Rabin decryption.
     */
    public StringBuffer decrypt(StringBuffer text, Key e){
	DummyPublicRabinKey drk = (DummyPublicRabinKey)e;
	
	//Set up for recovering the plaintext
	//I don't recommend messing with this code:	
	StringTokenizer linetokens = new StringTokenizer(text.toString());  // blocks are whitespace separated blocks
	int numblocks = linetokens.countTokens();  
	String[] blocks = new String[numblocks];
	for(int i=0; i< numblocks; i++) blocks[i] = linetokens.nextToken(); // put blocks in an array
	int plainBlockSize = drk.getPlainBlockSize();   // key holds info about size of plain blocks (in bytes)
	int len =  numblocks * plainBlockSize ;  // length of message in bytes
	StringBuffer output = new StringBuffer(len);  


	//I don't recommend messing with this code:	
	BigInteger M;  // local variable for viewing each block as an integer
	String block;  // local variable for each ouptut block

	// You'll have to modify this appropriatel
	BigInteger n = drk.getn(); 	//get relevant info from key to allow decryption

	for(int i=0; i<numblocks; i++){
	    M = new BigInteger(blocks[i],36);  // radix 36 since stored base-36

	    // begin your decryption computations here:
	    M = M.mod(n); 	// You'll have to modify this appropriately - does nothing right now

	    //insert the blocks AFTER YOU'VE FINISHED YOUR COMPUTATIONS
	    //I don't recommend messing with this code for the remainder of this method
	    byte[] temp1 = M.toByteArray();
	    //may have lost some leading zeros -so put back in:
	    int padsize = plainBlockSize-temp1.length;
	    if(padsize > 0){
		byte padsym = 0;
		byte[] temp2 = new byte[plainBlockSize];
		for(int j=0; j<padsize; j++) temp2[j] = padsym;
		for(int j=padsize; j<plainBlockSize; j++) temp2[j] = temp1[j-padsize];
		temp1 = temp2;
	    }
	    block = new String(temp1);
	    if(i<(numblocks-1) ){ // simple general case of blocks before the last
		output.append(block);
	    }else { // the last block must be stripped of pads - the special case
		int numpads = (int)block.charAt(plainBlockSize-1);
		try{
		    output.append(block.substring(0,plainBlockSize-numpads));
		}catch(StringIndexOutOfBoundsException sioobe){
		    output.append(block); // gave up trying to clean block up, just append it as is
		}
	    }	    
	}
	return output;
    }

    /**
     * Required method.  Wrap decrypt() since stringbuffer length changed
     *    
     * Due to a serious bug in Java 1.4.1 which produces memory leaks in
     * StringBuffer objects because of some String/StringBuffer memory
     * sharing violations, we cannot replace the StringBuffer text
     * in the obvious manner (delete everything then append).
     * Instead, we must first create a temp string to store the
     * converted results of the wrapped method, and then append
     * the string to the string buffer after having deleted its contents.
     *
     * I don't recommend messing with this code:
     */
    public void decryptOn(StringBuffer text, Key e){
	String temp = decrypt(text,e).toString();
	text.delete(0,text.length());
	text.append(temp);
    }

    /**
     * Useful method converting a String into an array of bytes of equal
     * length representing a positive BigInteger.
     * There is a possible loss of information in that the first bit may
     * by turned off and all char's are converted to bytes.  
     * For ASCII text this is not an issue.
     *
     * I don't recommend messing with this code:
     */
    public static byte[] string2bytes(String in){
	byte[] out = in.getBytes();
	out[0] &= (byte)127;
	return out;
    }


}




