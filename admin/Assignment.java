package webcrypt.admin;
import java.util.*;
import java.io.*;
import webcrypt.crypto.*;
import webcrypt.io.*;

/**
 * Create an assignment for the class.
 *
 * Usage:
 *>java webcrypt.admin.Assignment assignmentFile encryptFlag randomSeed 
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
 *   numStudents   kernelName     windowSize  groupSize  lineSize  assignmentName
 * Line 2)
 *   plaintextFile assignmentDir  solutionDir    
 * Lines 3 thru numStudents+2)
 *   studentDir    password       keyseed
 *
 * for example:
 *
 *>java webcrypt.admin.Assignment C:\windows\desktop\teaching\crypto\admin\3995.txt crack 92658790
 *
 * and check out the file C:\windows\desktop\teaching\crypto\admin\3995.txt for more details
 */
public class Assignment{

    public static void main(String[] args) 
	throws ClassNotFoundException, InstantiationException, 
	       IllegalAccessException, KeyCreationException {
	
	//get arguments:
       	String assignment = TextManip.getWholeFile(args[0]);
	boolean isEncryption = args[1].equals("encrypt"); //default behavior is cryptanalysis
	Random rand = new Random(Long.parseLong(args[2]));
	//check:
	//	System.out.println("file: "+assignment);
	System.out.println("Beginning "+(isEncryption?"encryption":"cryptanalyis")+" assignment");
	
	StringTokenizer lineTokens = new StringTokenizer(assignment,"\n");

	// parse first line of file
	StringTokenizer line = new StringTokenizer(lineTokens.nextToken());
	int numstudents = Integer.parseInt(line.nextToken());
	Class kerClass = Class.forName("webcrypt.crypto."+line.nextToken());
	int windowSize = Integer.parseInt(line.nextToken());
	int groupSize = Integer.parseInt(line.nextToken());
	int lineSize = Integer.parseInt(line.nextToken());
	String filename = line.nextToken();	
	//check first line:
	System.out.println("Number of students: "+numstudents);
	System.out.println("Kernel:  "+kerClass);
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

	Kernel ker = (Kernel)kerClass.newInstance();
	System.out.println("Kernel's toString():  "+ker);

	//ASSIGNMENT SPECIFIC STRINGS
	String[] known_for_LinearTrans =
	    new String[]{"THE FOLLOWING EXCERPT IS TAKEN FROM MOBY DICK\n",
			 "I THINK THAT MOBY DICK IS A REAALY GREAT BOOK\n",
			 "THE CATCHER IN THE RYE IS A PRETTY GOOD BOOK TOO\n",
			 "MELVILLE WAS THE GREATEST WRITER OF ALL TIME\n",
			 "I THINK THE MOVIE IS MUCH BETTER THAN THE BOOK\n",
			 "TOO BAD GREENPEACE WASNT AROUND TO STOP AHAB\n",
			 "AHAB IS WAY OUT OF CONTROL AND NEEDS TO RELAX\n",
			 "LEO THINKS THAT ONLY BAD PEOPLE HARPOON WHALES\n",
			 "THE BLUE WHALE IS THE BIGGEST CREATURE THAT EVER LIVED\n"};
	String[] known_for_Enigma = known_for_LinearTrans;
	String special = "MOBY DICK WAS MY FAVORITE BOOK GROWING UP\n";
			 
	//

	// Now generate the assignment student by student
	String student,password,keytext;
	File cipherFile, plainFile, questionFile, answerFile;
	StringBuffer text;
	for(int i = 1; i <= numstudents; i++){
	    // get the info from line
	    line = new StringTokenizer(lineTokens.nextToken());
	    student = line.nextToken();
	    password = line.nextToken();
	    keytext = line.nextToken();

	    System.out.println("Processing student: "+student);
	    if(ker instanceof ElGamal){//generate the key on the fly
		ElGamalKey[] egkeys = ElGamalKey.generateKeyPair(null); //time stamp printed as side effect
		keytext = egkeys[1].toString(); //public key
	    }
	    String studentDir = student+'_'+password;
	    String plainFileName = filename+'_'+keytext+".txt";

	    String cipherFileName = filename+".txt";
	    if(ker instanceof ElGamal) plainFileName = cipherFileName;
	    
	    if (!isEncryption){ //cryptanalysis assignment
		questionFile = new File(publicDir,studentDir+File.separator+cipherFileName);
		answerFile = new File(privateDir,student+File.separator+plainFileName);
		cipherFile = questionFile;
		plainFile = answerFile;
		}
	    else{ //encryption assignment
		questionFile = new File(publicDir,studentDir+File.separator+plainFileName);
		answerFile = new File(privateDir,student+File.separator+cipherFileName);
		cipherFile = answerFile;
		plainFile = questionFile;
	    }
	    Key k = ker.generateKey(keytext);	    
	    long idx = Math.abs(rand.nextLong() % (len-windowSize));
	    text = new StringBuffer(TextManip.getInterval(plaintextFile.toString(),idx,windowSize));
	    System.out.println("First 10 char's of plaintext:  "+text.toString().substring(0,10));

	    //ASSIGNMENT SPECIFIC MODIFICATIONS
	    if(ker instanceof LinearTrans) {
		if (i < numstudents-1)
		    text.insert(0,known_for_LinearTrans[i%(known_for_LinearTrans.length-1)]);
		else
		    text.insert(0,known_for_LinearTrans[known_for_LinearTrans.length-1]);
	    }
	    if(ker instanceof Enigma) {
		if (i < numstudents-1)
		    text.insert(0,known_for_Enigma[(i+1)%(known_for_Enigma.length-1)]);
		else
		    text.insert(0,known_for_Enigma[known_for_Enigma.length-1]);
	    }
	    //

	    SaveFile.write(plainFile,
			   StandardizeFile.standardVersion(text.toString(),true,groupSize,lineSize),
			   true); //create dirs if need be
	    ker.encryptOn(text,k);
	    System.out.println("First 10 char's of ciphertext:  "+text.toString().substring(0,10));

	    //ASSIGNMENT SPECIFIC MODIFICATIONS
	    String preamble = "";
	    if(ker instanceof LinearTrans) {
		preamble = "Next line is known plaintext occuring in beginning of ciphertext:\n";
		if (i < numstudents-1)
		    preamble += known_for_LinearTrans[i%(known_for_LinearTrans.length-1)];
		else
		    preamble += known_for_LinearTrans[known_for_LinearTrans.length-1];
	    }
	    if(ker instanceof Enigma) {
		preamble = "To decrypt properly, you should delete the first 3 lines of this file.\n"
		    +"Next line is known plaintext occuring in beginning of ciphertext:\n";
		if (i < numstudents-1)
		    preamble += known_for_Enigma[(i+1)%(known_for_Enigma.length-1)];
		else
		    preamble += known_for_Enigma[known_for_Enigma.length-1];
	    }

	    if(ker instanceof ElGamal){
		SaveFile.write(cipherFile,text.toString(),true);
	    }
	    else{
		SaveFile.write(cipherFile,
		       preamble+StandardizeFile.standardVersion(text.toString(),true,groupSize,lineSize),
			       true); //create dirs if need be
	    }
      //	    System.out.println("i ="+i);
      //	    if(i==1){
      //System.out.println(text.toString());
      //System.out.println(preamble+StandardizeFile.standardVersion(text.toString(),true,groupSize,lineSize));
	    //	    }
	}
    }
}
