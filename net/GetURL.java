/*
 * Notice by Zeph Grunschlag:
 * This program is based on a similar program created by David Flanagan.
 * In keeping with David Flanagan's instructions I have retained his notice
 * in the final paragraph of this comment.  It is very likely that
 * little if any code was actually changed.  The following
 * quantifies how much was actually changed from David Flanagan's original:
 * >>>>>MINOR CHANGE.  ALLOWED TO DOWNLOAD INTO A STRING BUFFER.
 *
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */
package webcrypt.net;
import java.io.*;
import java.net.*;

/**
 * This simple program uses the URL class and its openStream() method to
 * download the contents of a URL and copy them to a file or to the console.
 **/
public class GetURL {
    public static void main(String[] args) {
        InputStream in = null;   
        OutputStream out = null;
        try {
            // Check the arguments
            if ((args.length != 1)&& (args.length != 2)) 
                throw new IllegalArgumentException("Wrong number of args");
	    
            // Set up the streams
            URL url = new URL(args[0]);   // Create the URL
            in = url.openStream();        // Open a stream to it
            if (args.length == 2)         // Get an appropriate output stream
                out = new FileOutputStream(args[1]);
            else out = System.out;
	    
            // Now copy bytes from the URL to the output stream
            byte[] buffer = new byte[4096];
            int bytes_read;
            while((bytes_read = in.read(buffer)) != -1)
                out.write(buffer, 0, bytes_read);
	}
        // On exceptions, print error message and usage message.
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java GetURL <URL> [<filename>]");
        }
        finally {  // Always close the streams, no matter what.
            try { in.close();  out.close(); } catch (Exception e) {}
        }
    }
    
    /** ZEPH ADD-IN>>>>
     *  Assuming that a String makes sense as the content type, 
     *  return the contents as a string.  No error checking.
     */
    public static String getText(URL url){
        InputStream in = null; 
	StringBuffer tempbuf = new StringBuffer();
        try {
            in = url.openStream();        // Open a stream to it
	    
            // Now copy bytes from the URL to the StringBuffer
            byte[] buffer = new byte[4096];
            int bytes_read;
            while((bytes_read = in.read(buffer)) != -1)
                tempbuf.append(new String(buffer, 0, bytes_read));
	}
        // On exceptions, print error message and usage message.
        catch (Exception e) {
            System.err.println(e);
        }
        finally {  // Always close the streams, no matter what.
	    try { in.close(); } catch (Exception e) {}
	    return tempbuf.toString();
        }
    }
}




