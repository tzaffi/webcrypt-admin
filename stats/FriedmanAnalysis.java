package webcrypt.stats;

/**
 * Perform Index of Coincidence ansalysis to a Vigenere ciphertext.
 * Invented by Wolfe Friedman in 1920.
 */
public class FriedmanAnalysis extends Analyzor{

    /**
     * Used to show in window.
     */
    public String toString(){
	return "Friedman Analysis (max period)";
    }

    public void analyzeOn(StringBuffer text, String arg)
	throws IllegalArgumentException{

	int maxPeriod = 0;
	try{ 
	    maxPeriod = Integer.parseInt(arg);
	}catch(NumberFormatException nfe){
	    throw new IllegalArgumentException("Argument must be a positive integer.");
	}
	if ( maxPeriod <= 0 )
	    throw new IllegalArgumentException("Argument must be a positive integer.");

	VigenereStats vs = new VigenereStats();
	double[] indices = vs.avgCoincidenceIndex(text.toString(),maxPeriod);
	String out = "Period\tIndex of Coincidence\n\n";
	for(int i=0; i<indices.length; i++){
	    out += ""+(i+1)+"\t"+indices[i]+"\n";
	}
	text.delete(0,text.length());
	text.append(out);
    }
    
}

