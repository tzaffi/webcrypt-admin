package webcrypt.io;
import java.io.*;

/**
 * Convert a piece of text to standard format.
 *
 * 1.  Either all caps or all lower case
 * 2.  Remove all non-letters and all punctuation
 * 3.  Write in groups of n letters where n is user satisfied
 * 4.  Collect letter groups in to m groups per line where m is user specified
 */
public class StandardizeFile{
    
    public final static boolean UPPERCASE = true;
    public final static boolean LOWERCASE = false;

    /**
     * Convert into standard form.
     */ 
    public static String standardVersion(String input, boolean makeCaps, 
					 int groupSize, int lineSize){
	return separatorVersion(input,makeCaps,groupSize,lineSize,
				" ");
    }

    /**
     * Similar to above but can specify what string separates
     * each group.  
     *
     * A separator is guaranteed to occur after every block (including
     * the last one) and all separators are guaranteed to be of equal length.
     */
    public static String separatorVersion(String input, boolean makeCaps, 
					  int groupSize, int lineSize,
					  String separator){
	input = makeCaps ? input.toUpperCase() : input.toLowerCase();
	StringBuffer temp = new StringBuffer(input);
	//System.out.println("Created buffer.");
	// first delete the non-chars
	StringBuffer out = new StringBuffer();
	int len = temp.length();
	for(int i = 0; i < len; i++){
	    if( Character.isLetter(temp.charAt(i)) )
		out.append(temp.charAt(i));
	}
	temp = out;
	
	// finally group as specified by paramaters
	out = new StringBuffer();
	len = temp.length();
	for(int i=0; i < len; i++){
	    //insert either new line or extra space at every group
	    out.append(temp.charAt(i));
	    if ( (i+1) % groupSize == 0 ) // found a group
		if ( (i+1) % (groupSize * lineSize) == 0 ) //found a line
		    out.append(separator.substring(0,separator.length()-1)+"\n");
		else
		    out.append(separator);
	}
	
	return out.toString();
    }
    

    /**
     * Convert into a long line
     */ 
    public static String longLine(String input, boolean makeCaps){
	return standardVersion(input,makeCaps,input.length(),2);
    }

    /** 
     * Former main, for testing purpose
     */
    public static void test(String[] args){
	/*	File f = new File(args[0]);
	write(f,args[1]);
	*/
	System.out.println(standardVersion(args[0],false,5,5));
	System.out.println(longLine(args[0],true));
    }

    /**
     * Conver the file to standard form where user specifizes group sizes,
     * number of groups per line, input file and output filename.
     * Usage:
     *>java StandardizeFile filein [makeCaps [groupSize lineSize]] fileout
     *
     * Default behavior:  unless makeCaps.equals("false"), set true and capitalize
     *
     * If there are no arguments other than the filename, we just create one long
     * uninterrupted string.
     */
    public static void main(String[] args){
	String s = TextManip.getWholeFile(args[0]);
	System.out.println("Got file.");
	boolean makeCaps = false;
	if(args.length > 2)
	    makeCaps = !args[1].equals("false");
	
	if(args.length < 4)
	    {
		System.out.println("Got info.");
		s = longLine(s,makeCaps);
	    }
	else
	    {
		int groupSize = Integer.parseInt(args[2]);
		int lineSize =  Integer.parseInt(args[3]);
		System.out.println("Got info.");
		s = standardVersion(s,makeCaps,groupSize,lineSize);
	    }
	System.out.println("Standardized.");
	SaveFile.write(new File(args[args.length-1]),s);
	System.out.println("Wrote.");
    }
}


