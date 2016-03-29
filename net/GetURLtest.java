package webcrypt.net;
import java.net.*;

public class GetURLtest{
    public static void main( String[] args ){
	try{
	System.out.println(GetURL.getText(new URL(args[0])));
	}
	catch(Exception e){
	    System.out.println(e);
	}
    }
}
