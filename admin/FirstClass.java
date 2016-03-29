package webcrypt.admin;
import java.util.*;
import java.io.*;
import webcrypt.io.*;

/**
 * Create an assignment for the class.
 *
 * Usage:
 *>java webcrypt.admin.FirstClass C:\Windows\Desktop\Teaching\crypto\admin\3995Roster.txt
 *
 * Create first day's handout.  Similar in construct to Assignment.java
 */
public class FirstClass{

    public static void main(String[] args) 
	throws ClassNotFoundException, InstantiationException, 
	       IllegalAccessException{
	
	//ASSIGNMENT SPECIFIC STRINGS
	String[] text =
	    new String[]
	{"                         3995 CONTRACT\n",
	 "\n\n\nI understand that cryptographic software is subject to special\n"
	 +"U.S. laws that forbid its export or transfer out of the U.S.A.\n"
	 +"(See for example http://www.bxa.doc.gov/Encryption for the government's\n"
	 +"view, or http://www.freeswan.org/freesman_trees/freesman-1.5/doc/exportlaws.html\n"
	 +"for an opposing view.)\n\n"
	 +"Consequently, I hereby promise to abide by the following rules:\n"
	 +"1.  I will not transfer any of the code given to me during the course, or\n"
	 +"    created by me outside the borders of the U.S.A.  In particular:\n"
	 +"    1A)  I will not tranport or send a physical electronic copy of the code \n"
	 +"         outside the U.S.A.\n"
	 +"    1B)  I will not post the code on any publicly available internet sight.\n"
	 +"    1C)  I will not email the code to anyone except a course instructor.\n"
	 +"2.  These restrictions will continue for as long as I have a copy of the code,\n"
	 +"    and the U.S. export rules still apply to cryptography."
	};

	//get arguments:
       	String assignment = TextManip.getWholeFile(args[0]);
	StringTokenizer lineTokens = new StringTokenizer(assignment,"\n\r\f");

	// parse first line of file
	StringTokenizer line = new StringTokenizer(lineTokens.nextToken());
	//	System.out.println(line);
	int numstudents = Integer.parseInt(line.nextToken());
	File outDir = new File(line.nextToken());

	//check first line:
	System.out.println("Number of students:  "+numstudents);	
	System.out.println("Private Directory:  "+outDir);

	// Now generate the assignment student by student
	String last,first,email,password;
	for(int i = 1; i <= numstudents; i++){
	    // get the info from line
	    line = new StringTokenizer(lineTokens.nextToken());
	    last = line.nextToken();
	    first = line.nextToken();
	    email = line.nextToken();
	    password = line.nextToken();
	    
	    String filename = last.substring(0,last.length()-1);
	    File outFile = new File(outDir,filename+".txt");
	    FileOutputStream fos = null;
	    PrintWriter out = null;
	    try{
		fos = new FileOutputStream(outFile);
		out = new PrintWriter(fos);
		
		out.print(text[0]+"\n\n");
		out.print("\nNAME:\t\t"+last+"  "+first);
		out.print("\nEMAIL:\t\t"+email);
		out.print("\nPASSWORD:\t"+password);
		out.print("\nWORK DIRECTORY:\nhttp://www.cs.columbia.edu/~zeph/3995/assignments/"+email+"_"+password);
		out.print(text[1]);
		out.print("\n\nSIGNATURE:\n\n\n\n");
		out.print("\n\nDATE:");
		
		if(out != null ) out.close();
		if(fos != null ) fos.close();    
	    }catch( FileNotFoundException e){
		e.printStackTrace();
	    }catch( IOException e){
		e.printStackTrace();
	    }
	}
    }
}
