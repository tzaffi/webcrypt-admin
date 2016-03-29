package webcrypt.io;
import java.io.*;

/**
 * A class with a simple method for writing text files.
 */
public class SaveFile{
    /**
     * Given the file object f and the text contents save the file.
     * Typically f has been obtained via a dialog.
     */
    public static void write(File f, String contents){
	FileOutputStream fos = null;
	PrintWriter out = null;
	try{
	    fos = new FileOutputStream(f);
	    out = new PrintWriter(fos);
	    
	    out.print(contents);
	    
	    if(out != null ) out.close();
	    if(fos != null ) fos.close();    
	}catch( FileNotFoundException e){
	    e.printStackTrace();
	}catch( IOException e){
	    e.printStackTrace();
	}
    }
    
    /**
     * Given the file object f and the text contents save the file.
     * Typically f has been obtained via a dialog.
     *
     * If the last argument is true, we create any missing directories.
     */
    public static void write(File f, String contents, boolean createDirs){
	write(f,"",contents,createDirs);
    }

    /**
     * Given the file object f and the text contents save the file.
     * Typically f has been obtained via a dialog.
     *
     * Preamble is a the first part of the file that remains untouched
     * by the rest of the standardizations.
     *
     * If the last argument is true, we create any missing directories.
     */
    public static void write(File f, String preamble, String contents, boolean createDirs){
	FileOutputStream fos = null;
	PrintWriter out = null;
	try{
	    fos = new FileOutputStream(f);
	    out = new PrintWriter(fos);
	    
	    out.print(contents);
	    
	    if(out != null ) out.close();
	    if(fos != null ) fos.close();    
	}catch( FileNotFoundException e){
	    if(createDirs){
		try{
		    f.getParentFile().mkdirs();
		    fos = new FileOutputStream(f);
		    out = new PrintWriter(fos);
		    out.print(contents);
		    if(out != null ) out.close();
		    if(fos != null ) fos.close();    
		}catch( IOException ioe){
		    e.printStackTrace();
		}
	    }
	    else{
		e.printStackTrace();
	    }
	}catch( IOException e){
	    e.printStackTrace();
	}
    }


    public static void main(String[] args){
	File f = new File(args[0]);
	write(f,args[1]);
    }

}


