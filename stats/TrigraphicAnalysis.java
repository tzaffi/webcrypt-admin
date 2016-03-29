package webcrypt.stats;

/**
 * Perform frequency ansalysis to aid in cryptanalysis.
 */
public class TrigraphicAnalysis extends Analyzor{

    /**
     * Used to show in window.
     */
    public String toString(){
	return "Trigraphic Analysis (num_trigraphs)";
    }

    /**
     * Utilize MonoAlphabeticStats to present a frequency analysis table.
     *
     * Due to a serious bug in Java 1.4.1 which produces memory leaks in
     * StringBuffer objects because of some String/StringBuffer memory
     * sharing violations, we cannot replace the StringBuffer text
     * in the obvious manner (delete everything then append).
     * Instead, we must first create a temp string to store the
     * converted results of the wrapped method, and then append
     * the string to the string buffer after having deleted its contents.
     */
    public void analyzeOn(StringBuffer text, String arg) 
	throws IllegalArgumentException{

	// first, try to catch any errors
	int n = -1;
	try{
	    n = Integer.parseInt(arg);
	}catch( NumberFormatException e ){
	    throw new IllegalArgumentException("Argument must be a positive integer.");
	}
	if ( n <= 0 )
	    throw new IllegalArgumentException("Argument must be a positive integer.");

	MonoAlphabeticStats mas = new MonoAlphabeticStats();
	StringFrequency[] freqtable = mas.getMultigramFrequencies(text.toString(),3,n);
	String out = "String\tCount\tFrequency\t\tVariance\n\n";
	for(int i=0; i<freqtable.length; i++){
	    out += freqtable[i] + "\n";
	}
	text.delete(0,text.length());
	text.append(out);
    }

}
