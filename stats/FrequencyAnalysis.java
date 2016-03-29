package webcrypt.stats;

/**
 * Perform frequency ansalysis to aid in cryptanalysis.
 */
public class FrequencyAnalysis extends Analyzor{

    /**
     * Used to show in window.
     */
    public String toString(){
	return "Frequency Analysis (no args)";
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
    public void analyzeOn(StringBuffer text, String arg){
	MonoAlphabeticStats mas = new MonoAlphabeticStats();
	CharFrequency[] freqtable = mas.getFrequencies(text.toString());
	int cnt = 0;
	String out = "Letter\tCount\tFrequency\t\tVariance\n\n";
	for(int i=0; i<freqtable.length; i++){
	    out += freqtable[i] + "\n";
	    cnt += freqtable[i].getCount();
	}
	out += "\n\nFinal in-domain count = "+cnt;
	text.delete(0,text.length());
	text.append(out);
    }

}

