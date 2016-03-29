package webcrypt.stats;

/**
 * The PlugAnalysis class allows the cryptanylist who has already figured out 
 * the rotor setting for an Enigma-encrypted ciphertext, try out different
 * plugboard settings.  The user specifies the rotors and known plugboard
 * settings and a letter which is suspected to be part of a plug, and this
 * program tries out every possible letter for the other end of the plug
 * and reports back some statistical results.
 *
 * EG: suppose the user already knows rotor settings "ZEP" and plugboard
 * settings { {H,G} , {R,U} } and suspects that E is plugged to another
 * letter.  The the user can specify the argument
 *
 *   ZEP_HG_RU_E
 *
 * and this program will try out plugging into every remaining letter 
 * (A-Z except H,G,R,U and E), and report back some statistical information (such
 * as the ratio of frequencies between the greatest and smallest occurring frequencies)
 * which will let the user make an educated guess as to which letter E is plugged
 * into.
 */
import webcrypt.crypto.*;

public class PlugAnalysis extends Analyzor{
    
    public String toString(){ 
	return "PlugAnalysis(rotors[_plugs]*_lastplug)"; 
    }

    private String errstr = "Wrong argument.\n"
	+"A correct argument consists of a rotor setting, followed\n"
	+"by a sequence of plug settings with distinct letters,\n"
	+"followed by a single plug setting whose other end is searched.\n"
	+"For example, the following is an example of valid input:\n"
	+"                KAX_AW_ES_D";

    /**
     * Analyze as follows:
     * 1) For each letter X in the alphabet:
     * 2) Try to create an Enigma machine with the given argument and X appended.
     * 3) If created valid Enigma, encrypt the text with it and do frequency analysis
     * 4) If all created Enigmas were invalid, throw an error
     * 5) Else, report the results in the string buffer
     */
    public void analyzeOn(StringBuffer text, String arg) throws IllegalArgumentException{
	String keystr;
	Enigma enigma = new Enigma();
	EnigmaKey k;
	boolean atLeastOneSuccess = false;
	StringBuffer results 
	    = new StringBuffer("key\tCipher-Letter Frequency Order\t\t(smallest freq / biggest freq)\n\n");
	
	char plugged = arg.toUpperCase().charAt(arg.length()-1);
	StringBuffer ciphertext;
	char X ='A';
	X--;
	for( ; X <= 'Z'; X++){
	    keystr = X<'A' ? arg.substring(0,arg.length()-2) : (arg + X);
	    try{
		k = new EnigmaKey(keystr);
	    }catch(KeyCreationException e){
		continue; // try next letter
	    }
	    if(X == plugged) continue;
	    if(X >= 'A') atLeastOneSuccess = true;
	    
	    ciphertext = new StringBuffer(text.toString());
	    results.append(keystr+"\t");
	    
	    MonoAlphabeticStats mas = new MonoAlphabeticStats();
	    CharFrequency[] freqtable = mas.getFrequencies(enigma.decrypt(ciphertext,k).toString());
	    for(int i=0; i<freqtable.length; i++)
		results.append(freqtable[i].getChar());
	    results.append("\t"+((double)freqtable[freqtable.length-1].getCount())
			   /    freqtable[0].getCount()+"\n");
	}
	if (!atLeastOneSuccess)
	    throw new IllegalArgumentException("All keys produced by "+arg+" are invalid!!!\n"+errstr);

	text.delete(0,text.length());
	text.append(results.toString());
    }

}

