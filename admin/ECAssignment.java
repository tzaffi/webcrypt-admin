package webcrypt.admin;
import java.util.*;
import java.io.*;
import webcrypt.crypto.*;
import webcrypt.io.*;

/**
 * Create an extra credit assignment for the class.
 *
 * Usage:
 *>java webcrypt.admin.ECAssignment assignmentFile randomSeed 
 *
 * where assignmentFile is a file containing information about the assignment including 
 *   students and type of encryption
 * and encryptFlag is "encrypt" if this an encryption assignment,
 *   or any other word if this is a cryptanalysis assignment
 *
 * Format of assignmentFile
 * (for an example, check out assignmentTemplate.txt in this directory):
 *
 * Line 1)                       
 *   numClasses  windowSize  groupSize  lineSize  assignmentName
 * Line 2)
 *   plaintextFile assignmentDir  solutionDir    
 * Lines 3 thru numStudents+2)
 *   kernelName    keyseed1  keyseed2
 *
 * for example:
 *
 *>java webcrypt.admin.ECAssignment C:\windows\desktop\teaching\crypto\admin\extracracks.txt 92658790
 *
 * and check out the file C:\windows\desktop\teaching\crypto\admin\extracracks.txt for more details
 */
public class ECAssignment{

    public static void main(String[] args) 
	throws ClassNotFoundException, InstantiationException, 
	       IllegalAccessException, KeyCreationException {
	
	//get arguments:
       	String assignment = TextManip.getWholeFile(args[0]);
	Random rand = new Random(Long.parseLong(args[1]));
	//check:
	//	System.out.println("file: "+assignment);
	
	StringTokenizer lineTokens = new StringTokenizer(assignment,"\n");

	// parse first line of file
	StringTokenizer line = new StringTokenizer(lineTokens.nextToken());
	int numciphers = Integer.parseInt(line.nextToken());
	int windowSize = Integer.parseInt(line.nextToken());
	int groupSize = Integer.parseInt(line.nextToken());
	int lineSize = Integer.parseInt(line.nextToken());
	String filename = line.nextToken();	
	//check first line:
	System.out.println("Number of ciphers: "+numciphers);
	System.out.println("(Window,groupSize,lineSize) = ("+windowSize+","+groupSize+","+lineSize+")");
	System.out.println("File name:  "+filename);

	// parse second line
	line = new StringTokenizer(lineTokens.nextToken());
	File plaintextFile = new File(line.nextToken());
	File publicDir = new File(line.nextToken());
	File privateDir = new File(line.nextToken());
	//check second line:
	System.out.println("Plaintext File:  "+plaintextFile);
	System.out.println("Public Directory:  "+publicDir);
	System.out.println("Private Directory:  "+privateDir);

	// Use TextManip methods in case file is longer than biggest int
	long len = TextManip.getFileLength(plaintextFile.toString());

	//ASSIGNMENT SPECIFIC STRINGS
	String crib = "AHAB IS WAY OUT OF CONTROL AND NEEDS TO RELAX\n";

	// Now generate the assignment cipher by cipher
	String cipher, keytext[] = new String[2];
	File cipherFile, plainFile;
	StringBuffer text;
	Class kerClass;
	Kernel ker;
	for(int i = 1; i <= numciphers; i++){
	    // get the info from line
	    line = new StringTokenizer(lineTokens.nextToken());
	    cipher = line.nextToken();
	    keytext[0] = line.nextToken();
	    keytext[1] = line.nextToken();

	    kerClass = Class.forName(cipher);
	    ker = (Kernel)kerClass.newInstance();
	    System.out.println("Kernel's toString():  "+ker);

	    System.out.println("Processing cipher: "+cipher);

	    
	    for(int j=1; j<=2; j++){
		String subdir = (j==1) ? ( "withcrib"+File.separator ) : "";
		cipherFile = new File(publicDir,subdir+filename+"_"+cipher+(5*j)+"pts.txt");
		plainFile = new File(privateDir,subdir+filename+"_"+cipher+"_"+keytext[j-1]+".txt");

		Key k = ker.generateKey(keytext[j-1]);	    
		long idx = Math.abs(rand.nextLong() % (len-windowSize));
		text = new StringBuffer(TextManip.getInterval(plaintextFile.toString(),idx,windowSize));
		System.out.println("First 10 char's of plaintext "
				   +((j==1)?"(past crib)":"")+":  "
				   +text.toString().substring(0,10));

		if(j == 1)
		    text.insert(0,crib);

		SaveFile.write(plainFile,
			       StandardizeFile.standardVersion(text.toString(),true,groupSize,lineSize),
			       true); //create dirs if need be

		ker.encryptOn(text,k);
		System.out.println("First 10 char's of ciphertext:  "+text.toString().substring(0,10));

		SaveFile.write(cipherFile,
			       StandardizeFile.standardVersion(text.toString(),true,groupSize,lineSize),
			       true); //create dirs if need be
	    }
	}
    }
}
