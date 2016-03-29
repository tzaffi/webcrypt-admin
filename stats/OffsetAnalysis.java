package webcrypt.stats;

import java.text.*; // for NumberFormat and DecimalFormat
/**
 * After discovering the period of a Vigenere ciphertext, calculate the mixed frequencies
 * of each offseted periodic substring at each possible translation key.
 */
public class OffsetAnalysis extends Analyzor{
    // obtained from Moby Dick:
    public static String standardOrder = "ETAONISHRLDUMCWFGPBYVKQJXZ";
    public static String alph = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Used to show in window.
     */
    public String toString(){
	return "Offset Analysis (period)";
    }


    /**
     * The older version printed out a list of ordered letters at each period
     * so that a user could eyeball the answer.  This didn't work very well.
     */
    public void analyzeOn(StringBuffer text, String arg)
	throws IllegalArgumentException{
	
	int p=0; // the period
	try{ 
	    p = Integer.parseInt(arg);
	}catch(NumberFormatException nfe){
	    throw new IllegalArgumentException("Argument must be a positive integer.");
	}
	if ( p <= 0 )
	    throw new IllegalArgumentException("Argument must be a positive integer.");

	VigenereStats vs = new VigenereStats();
	double[][] table = vs.FriedmanTable(text.toString(),p);
	int[] maxindices = getMaxRowIndices(table);
	String out = getPrintableTable(table,maxindices,p);
	text.delete(0,text.length());
	text.append(out);
    }

    /**
     * Get the index of the maximum value of each row
     *
     * e.g.  INPUT  
     * { { .1 , .2, .3, .4 } ,
     *   { .5 , .2, .3, .4 } ,
     *   { .1 , .2, .5, .4 } ,
     *   { .1 , .2, .5, .4 } ,
     *   { .1 , .5, .3, .4 } }
     *
     * give OUTPUT
     * { 3, 0, 2, 2, 1}
     */
    int[] getMaxRowIndices(double[][] t){
	int[] out = new int[t.length];
	for(int i=0; i<out.length; i++){
	    int index = -1;
	    if ( t[i].length > 0 ){
		index = 0;
		double max = t[i][index];
		for(int j=1; j<t[i].length; j++)
		    if ( t[i][j] > max ){
			max = t[i][j];
			index = j;
		    }
	    }
	    out[i] = index;
	}
	return out;
    }

    /**
     * Helper method to analyzeOn
     */
    private static String getPrintableTable(double[][] table, int[] maxindices, int p){
	NumberFormat formatter = new DecimalFormat(".000");

	String out = "List of translated frequency with period "+p+" interpolated with standard frequency:\n\n";
	out += "Translation";
	for(int j=0; j<p; j++) out+="\tOFFSET "+j;
 	out += "\n\n";
	for(int i=0; i<alph.length(); i++){
	    out += alph.charAt(i);
	    for(int j=0; j<p; j++) {
		out += "\t";
		if ( i == maxindices[j] ) out += "**";
		out+=  formatter.format( i < table[j].length  ? table[j][i] : '!' );
		if ( i == maxindices[j] ) out += "**";
		//		System.out.println("i="+i+"   j="+j);
	    }
	    out += "\n";
	}
	out += "\n**X** means X is the best key-guess for the given offset \n";
	return out;
    }

    /**
     * The older version printed out a list of ordered letters at each period
     * so that a user could eyeball the answer.  This didn't work very well.
     */
    public void analyzeOnOLDERVERSION(StringBuffer text, String arg)
	throws IllegalArgumentException{
	
	int p=0; // the period
	try{ 
	    p = Integer.parseInt(arg);
	}catch(NumberFormatException nfe){
	    throw new IllegalArgumentException("Argument must be a positive integer.");
	}
	if ( p <= 0 )
	    throw new IllegalArgumentException("Argument must be a positive integer.");

	VigenereStats vs = new VigenereStats();
	char[][] table = vs.OLDFriedmanTable(text.toString(),p);
	String out = getOLDPrintableTable(table,p);
	text.delete(0,text.length());
	text.append(out);
    }

    /**
     * Helper method to analyzeOn
     */
    private static String getOLDPrintableTable(char[][] table, int p){
	String out = "List of letters ordered by frequency with period "+p+" at different offsets:\n\n";
	out += "INDEX";
	for(int i=0; i<p; i++) out+="\tOFFSET "+i;
 	out += "\tvs. EXPECTED ORDER\n\n";
	for(int i=0; i<standardOrder.length(); i++){
	    out += i;
	    for(int j=0; j<p; j++) {
		out+="\t"+ ( i < table[j].length  ? table[j][i] : '!' );
		//		System.out.println("i="+i+"   j="+j);
	    }
	    out += "\t<"+standardOrder.charAt(i)+">\n";
	}
	return out;
    }

    /**
     * For testing purposes
     **/
    public static void main(String[] args) throws Exception{
	String text = args[0];
	int p = Integer.parseInt(args[1]);
	
	VigenereStats vs = new VigenereStats();
	char[][] table = vs.OLDFriedmanTable(text,p);
	System.out.println("Successfully computed Friedman Table");
	System.out.println("\n"+getOLDPrintableTable(table,p));
    }
    
}

