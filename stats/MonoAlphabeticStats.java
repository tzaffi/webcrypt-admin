package webcrypt.stats;
/**
 * A class used to analyze ciphertexts arising from a
 * monoalphabetic substitution.  Rather than having
 * static classes, we use object oriented design to allow
 * for customization of analysis, and concurrent analysis
 * of several texts in different windows.
 */

import java.util.*;  // For TreeSet, Iterator

public class MonoAlphabeticStats extends webcrypt.character.CharDomains{

    /** Size of this domain */
    protected int domsize; 

    /**
     * Since different ranges of characters are possible
     * one will need to specify the range used.  The
     * Default range will be LETTERS, but this can be
     * modified with the setDomain method.
     */
    public MonoAlphabeticStats(){
	setDomain(CAPS);
	domsize = getDomainSize();
    }
    
    /**
     * Get the maximum frequency
     *
     * Cheap and dirty solution, but wasteful
     */
    public CharFrequency maxFreq(String text){
	return (getFrequencies(text))[0];
    }

    /**
     * Return an array listing the frequency of each char in the domain
     * sorted in descending order of frequence
     */
    public CharFrequency[] getFrequencies(String text){
	//first capitalize since don't care about cases
	text = text.toUpperCase();

	CharFrequency[] index = unorderedFrequencies(text);
	// count the total
	int totalcount = 0;
	for( int i=0; i<index.length; i++) totalcount += index[i].getCount();

	//order the CharFrequency in descending (real) order
	TreeSet tree = new TreeSet();
	for(int i = 0; i < domsize; i++){
	    tree.add(index[i]);
	    //	    System.out.println(index[i]);
	}
	
	//retrieve the objects in any array, recompute and return
	CharFrequency[] output = new CharFrequency[domsize];
	int j = 0;
	CharFrequency cf;
	double cnt, var;
	for( Iterator itr = tree.iterator(); itr.hasNext(); ){
	    cf = (CharFrequency)itr.next();
	    cnt = cf.getCount();
	    cf.setFreq( cnt/totalcount );
	    var = cnt/totalcount - 1.0/domsize;
	    cf.setVar( var * var );
	    output[j++] = cf;
	    //	    System.out.println(cf);
	}

	return output;
    }

    /**
     * Return an array listing the frequency of each char in the domain.
     * Ordered by the letter
     */
    protected CharFrequency[] unorderedFrequencies(String text){
	//initialize an index array containing all the letter frequency objects
	CharFrequency[] index = new CharFrequency[domsize];
	int j = 0;
	for(int i = 0; i < domain.length; i++)
	    for(char c = (char)domain[i][0]; c <= (char)domain[i][1]; c++){
		index[j++] = new CharFrequency(c);
	    }
	//run through string ignoring non-domain char's and counting the rest
	char c;
	int idx, count;
	CharFrequency cf;
	int totalcount = 0;
	double var;
	for(int i = 0; i < text.length(); i++){
	    c = text.charAt(i);
	    if (inDomain(c)){
		//recompute the stats:
		totalcount++;
		idx = getIndex(c);
		cf = index[idx];
		count = cf.getCount()+1;
		cf.setCount(count);
		//		System.out.println(cf);
	    }
	}
	return index;
    }

   /**
     * Return an array listing the frequency of the top n length-len strings in the text.
     *
     * For len = 1, n = 26 this is just the monographic frequency table
     * For len = 2 these are the bigrams
     * For len = 3 these are the trigrams
     *
     * Don't try len > 4 !!!!
     */
    public StringFrequency[] getMultigramFrequencies(String text, int len, int n)
	throws IllegalArgumentException{
	if( len < 1 || len > 4 )
	    throw new IllegalArgumentException("length not in the range [1,4]");
	//capitalize  and remove all non characters
	text = webcrypt.io.StandardizeFile.longLine(text,true);
	text = text.substring(0,text.length()-1);  //longLine adds a newline char!

	int totalcount = text.length() - len + 1;  //total number of len-grams

	//next initialize an index array.  Will create objects only as needed
	int arrsize = (int)Math.round(Math.pow(domsize,len));
	StringFrequency[] index = new StringFrequency[arrsize];

	/*
	int j = 0;
	for(int i = 0; i < domain.length; i++)
	    for(char c = (char)domain[i][0]; c <= (char)domain[i][1]; c++){
		index[j++] = new CharFrequency(c);
		//		System.out.println("j = "+j);
		//		System.out.println((j-1)+": "+index[j-1]);
	    }
	*/

	//run through string counting all len-grams, and adding those that
	//that haven't been added.
	String str;
	int idx, count;
	StringFrequency sf;
	double var;
	//	System.out.println(text.length());
	for(int i = 0; i <= text.length()-len; i++){
	    str = text.substring(i,i+len);
	    idx = caps2base26num(str);
	    //	    System.out.println("str="+str+", idx="+idx+", index.length="+index.length);
	    sf = index[idx];
	    if (sf == null){
		index[idx] = new StringFrequency(str,1);
	    }
	    else{
		count = sf.getCount()+1;
		sf.setCount(count);
		//		System.out.println(str+" number "+count+": "+sf);
	    }
	    //		System.out.println(sf);
	}

	//order the StringFrequencies in descending (real) order
	TreeSet tree = new TreeSet();
	for(int i = 0; i < arrsize; i++){
	    if (index[i] != null){
		tree.add(index[i]);
		//		System.out.println(index[i]);
	    }
	}

	//retrieve the objects in any array, recompute and return
	StringFrequency[] output = new StringFrequency[n];
	int j = 0;
	double cnt;
	for( Iterator itr = tree.iterator(); itr.hasNext() && j < n; ){
	    sf = (StringFrequency)itr.next();
	    cnt = sf.getCount();
	    sf.setFreq( cnt/totalcount );
	    var = cnt/totalcount - 1.0/arrsize;
	    sf.setVar( var * var );
	    output[j++] = sf;
	    //	    System.out.println(sf);
	}

	return output;
    }

    public static void main(String[] args){
	MonoAlphabeticStats mas = new MonoAlphabeticStats();

	//monograph statistics:
	CharFrequency[] freqtable = mas.getFrequencies(args[0]);
	//	double tot = 0;
	for(int i=0; i<freqtable.length; i++){
	    System.out.println(freqtable[i]);
	    //	    tot += freqtable[i].getFreq();
	}
	//	System.out.println("sum of frequencies = "+tot);

	//bigram statistics:
	StringFrequency[] sfreqtable = mas.getMultigramFrequencies(args[0],2,20);
	for(int i=0; i<sfreqtable.length; i++){
	    System.out.println(sfreqtable[i]);
	}
    }
}
