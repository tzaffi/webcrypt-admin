package webcrypt.io;
import java.io.*;

/**
 * A class with a simple method for retreiving a piece of text from a file
 * and some other simple methods for files.
 */
public class TextManip{
    /**
     * Given the file name f and endpoints (a,a+len) get the text
     * from starting at index a and ending at a+len-1 {the interval [a,a+len-1) }
     *
     * Only works on 8-bit ASCII (since assume each character takes up a byte).
     *
     * WARNING:  Only works if interval-length is less than biggest int!
     */
    public static String getInterval(String f, long a, int len){
	String output = null;
	try{
	    RandomAccessFile raf = new RandomAccessFile(f,"r"); //r - read only
	    
	    if (len > (int)(raf.length()-a)) // going over edge of file so reset
		len = (int)(raf.length()-a);
	    if (len < 0) 
		len = 0;
	    byte[] interval = new byte[len];
	    
	    // Now read starting from a
	    raf.seek(a);  	//file pointer at a
	    raf.readFully(interval);
	    output = new String(interval);
	}catch( FileNotFoundException e){
	    e.printStackTrace();
	}catch( IOException e){
	    e.printStackTrace();
	}
	return output;
    }

    /**
     * Given the file name f get the entire text.
     *
     * Only works on 8-bit ASCII (since assume each character takes up a byte).
     * 
     * WARNING:  Only works if file-length is less than biggest int!
     */
    public static String getWholeFile(String f){
	return getInterval(f, 0L, (int)getFileLength(f));
    }
    
    /**
     * Get the file length
     */
    public static long getFileLength(String f){
	long len = -1;
	try{
	    RandomAccessFile raf = new RandomAccessFile(f,"r"); //r - read only
	    len = raf.length();
	}catch( FileNotFoundException e){
	    e.printStackTrace();
	}catch( IOException e){
	    e.printStackTrace();
	}
	return len;

    }

    public static void main(String[] args){
	System.out.println("length = "+getFileLength(args[0]));
        String s = getInterval(args[0],Long.parseLong(args[1]),Integer.parseInt(args[2]));
	System.out.println("INTERVAL:\n"+s);
	s = getWholeFile(args[0]);
	System.out.println("WHOLE FILE:\n"+s);
    }

}


