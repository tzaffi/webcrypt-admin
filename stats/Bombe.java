package webcrypt.stats;

/**
 * The Bombe class contains methods for simulating Turing's Bombe which
 * was used to cryptanalyze Nazi Enigma machines.
 *
 * The cryptanalyst goes through the following steps.
 *
 * 1) Identify a "crib" (known portion of plaintext) -this is where you need inside
 *    info.
 * 2) Find the location of the ciphertext version of the crib (this can be
 *    done by the sliding the text until no cipher text letter is equal
 *    to the plaintext letter above it.
 * 3) Create a transformation graph with vertices the alphabet and
 *    with edges between tranformed letters labeled by the the index
 *    of the transformed letters starting from the begining of the ciphertext.
 * 4) Find all the cycles in the graph.
 * 5) If there are enough cycles, it is very unlikely that there will be another
 *    rotor setting which achieves the same cycle pattern.  So use this class
 *    to go through all 26^3 rotor settings and report all possible positives.
 * 6) By detecting patterns and using trial and error one should now be able to
 *    guess the plugboard settings.
 */
import webcrypt.crypto.*;
import java.util.StringTokenizer; //for processing the input argument
import java.util.TreeSet;         //for finding the set of all needed enigmas
import java.util.ArrayList;       //to keep track of matches

public class Bombe extends Analyzor{

    public String toString(){
	return "Bombe(crib graph)";
    }

    /**
     * Return a string with internal info about all the Enigma machines.
     */
    public String internalInfo(){
	String out = "Enigma States:";

	for (int i=0; i<numenigmas; i++) out+="\n\n"+(i+1)+")\n"+enigmas[i].internalInfo();
	return out;
    }

    private String errstr = "The Bombe simulation did not take place.\n"
	+"There was a problem with your argument.  The argument is\n"
	+"supposed to describe the recurrence pattern arising a\n"
	+"known plaintext - ciphertext Enigma interaction.\n"
	+"For example, suppose the letter X returns to itself\n"
	+"after going through E_3 and E_17, and also returns to itself\n"
	+"after going sequentially through E_3, E_12, and E_26.\n"
	+"Furthermore, suppose the letter A returns to itself after\n"
	+"going through E_5, E_15, E_9, and E_12.  Finally, suppose\n"
	+"the letter J returns to itself after going through E_9 and E_17,\n"
	+"and also after going through E_9, E_11, E_4, and also after going\n"
	+"through E_9 and E_7.  Then the input string would look like:\n"
	+"       3:17|12-26_5:15-9-12_9:17|11-4|7   \n";
	
    private int numgroups; //number of cycle groups for different sources
    private int[] sourcenumbers;  // the first setting where a group of cycles start
    private int[][][] links; // the list of all the links in the cycles after the source
    
    private int numenigmas; // number of distinct enigmas needed
    private Enigma[] enigmas; // the enigma simulators, not necessarily sorted
    private int[] positions;  // the temporal-position of each enigma simulator in enigmas[]
    private Enigma[] sourceEnigmas; // array pointing to source Enigmas, mimicking sourcenumbers
    private Enigma[][][] linkEnigmas; // array pointing to the Enigmas on the cycles

    /**
     * Start the simulation and list the results out in the text
     */
    public void analyzeOn(StringBuffer text, String arg) throws IllegalArgumentException{
	processInput(arg);

	/* Check if precessed argument correctly */
	StringBuffer outbuf = new StringBuffer("Sourcenumbers:\n");
	for(int i=0; i<sourcenumbers.length; i++) 
	    outbuf.append(sourcenumbers[i] + ( (i<sourcenumbers.length-1)?", ":""));
	
	outbuf.append("\n\nCycles:\n");
	for(int i=0; i<links.length; i++) {
	    for (int j=0; j<links[i].length; j++){
		outbuf.append("("+sourcenumbers[i]+",");
		int k=0;
		for (k=0; k<links[i][j].length-1; k++)
		    outbuf.append(links[i][j][k]+",");
		outbuf.append(links[i][j][k]+")\n");
	    }
	}
	
	createEnigmas();
	outbuf.append(internalInfo());

	String[] matches = findMatches();  //the results of the simulation

	outbuf.append("\n\nNumber of possible matches: "+matches.length);
	outbuf.append("\n\nPossible Matches found at:\n");
	for (int i=0; i<matches.length; i++)
	    outbuf.append("\n\"abc\" = "+matches[i]);

	String outstr = outbuf.toString();
	text.delete(0,text.length());
	text.append(outstr);
    }

    /**
     * Fill in sourcenumbers[] and links[][][] accordint to the info. given in
     * the input.
     */
    private void processInput(String input) throws IllegalArgumentException{
	StringTokenizer groupTokens;  // for cycle groups (separated by underscores)
	StringTokenizer sourceNrest; // separate source from the rest (with colon)
	StringTokenizer cycleTokens;  // cycles in a group (separated by pipes)
	StringTokenizer linkTokens;   // links in a cycle (separated by hiphens)
	
	if (input == null) throw new IllegalArgumentException(errstr);
	groupTokens = new StringTokenizer(input,"_");
	numgroups = groupTokens.countTokens();
	if (numgroups == 0) throw new IllegalArgumentException(errstr);

	sourcenumbers = new int[numgroups];
	links = new int[numgroups][][];
	
	for(int i=0; i<numgroups; i++){
	    sourceNrest = new StringTokenizer(groupTokens.nextToken(),":");
	    if (sourceNrest.countTokens() != 2)
		throw new IllegalArgumentException(errstr
						   +"\n\nEach group must have exactly one ':'!!!");
	    try{
		sourcenumbers[i] = Integer.parseInt(sourceNrest.nextToken());
		cycleTokens = new StringTokenizer(sourceNrest.nextToken(),"|");
		int numcycles = cycleTokens.countTokens();
		if (numcycles == 0) 
		    throw new IllegalArgumentException(errstr+"\nSome group has no cycles!!!");
		links[i] = new int[numcycles][];
		for(int j=0; j<numcycles; j++){
		    linkTokens = new StringTokenizer(cycleTokens.nextToken(),"-");
		    int numlinks = linkTokens.countTokens();
		    if (numlinks == 0)
			throw new IllegalArgumentException(errstr
							   +"\nSome cycle has no links!!!");
		    links[i][j] = new int[numlinks];
		    for(int k=0; k<numlinks; k++){
			int idx = Integer.parseInt(linkTokens.nextToken());
			if (idx < 1)
			    throw new IllegalArgumentException(errstr+"\nIllegal index = "+idx
							       +" entered.\nAll indices of"
							       +" Enigma simulators must be positive!!!");
			links[i][j][k] = idx;
		    }
		}
	    }catch(NumberFormatException e){
		throw new IllegalArgumentException(errstr+"\n"+e);
	    }
	}
    }
	

    /**
     * Construct the Enigma simulators used in the Bombe.
     */
    private void createEnigmas(){
	// First find all the Enigma rotation numbers using TreeSet
	TreeSet tree = new TreeSet();
	for(int i=0; i<numgroups; i++){
	    tree.add(new Integer(sourcenumbers[i]));
	    for(int j=0; j<links[i].length; j++)
		for(int k=0; k<links[i][j].length; k++)
		    tree.add(new Integer(links[i][j][k]));
	}
	// Retrieve the unique Enigma numbers:
	Object[] temp = tree.toArray();
	numenigmas = temp.length;
	positions = new int[numenigmas];
	for(int i=0; i<numenigmas; i++) positions[i] = ((Integer)temp[i]).intValue();

	// Now create the enigmas.  After creating each one
	// forward it by the number of steps indicated indices.
	enigmas = new Enigma[numenigmas];
	for(int i=0; i<numenigmas; i++){
	    enigmas[i] = new Enigma();
	    try{
		enigmas[i].setup(new EnigmaKey("AAA"));
	    }catch(KeyCreationException e){
		System.out.println("In Bombe.java:  THIS SHOULD NEVER HAVE HAPPENED!!!!!!");
	    }
	    // crank forward the number of necessary positions.
	    // position == 1 doesn't get cranked
	    for(int j=1; j < positions[i]; j++)
		enigmas[i].crank();
	}

	// For each source number, find its index in the enigma array and point sourceEnigmas there
	sourceEnigmas = new Enigma[numgroups];
	for(int i=0; i<numgroups; i++){
	    int sourcenum = sourcenumbers[i];
	    for(int j=0; j<numenigmas; j++)
		if( positions[j] == sourcenum )
		    sourceEnigmas[i] = enigmas[j];
	}
	
	// For each link number, find its index in the enigma array and point sourceEnigmas there
	linkEnigmas = new Enigma[numgroups][][];
	for(int i=0; i<numgroups; i++){
	    linkEnigmas[i] = new Enigma[links[i].length][];
	    for(int j=0; j<links[i].length; j++){
		linkEnigmas[i][j] = new Enigma[links[i][j].length];
		for(int k=0; k<links[i][j].length; k++){
		    int linknum = links[i][j][k];
		    for(int l=0; l<numenigmas; l++)
			if( positions[l] == linknum )
			    linkEnigmas[i][j][k] = enigmas[l];
		}
	    }
	}	
    }

    /**
     * After all the cycle information has been stored, we start looking for
     * rotor configurations which match the cycle patterns.
     *
     * Here's pseudocode for the algorithm:
     * 
     * for each rotor configuration
     *   total_match = true
     *   for each source enigma while total_match still true
     *      source_match = false
     *      for each letter in the alphabet while source_match still false
     *         letter_match = true
     *         for each cycle while letter_match still true
     *            for each link in the cycle
     *               transform the current char through current link
     *            if final value != original value // no cycle for this letter
     *               letter_match = false //goes to next letter
     *         if letter_match still true, source_match = true
     *      if source_match still false, total_match = false
     *   if total_match still true, add current rotor configuraion to match list
     */
    private String[] findMatches(){
	ArrayList matches = new ArrayList();
	Enigma indexEnigma = new Enigma(); //just used to keep track of rotor positions
	try{
	    indexEnigma.setup(new EnigmaKey("AAA"));
	}catch(KeyCreationException e){
	    System.out.println("In Bombe.java:  THIS SHOULD NEVER HAVE HAPPENED!!!!!!");
	}
	for(int rot = 1; rot <= 26*26*26; rot++){
	    //DEBUGGING CODE:
	    //	    if ( rot % 100 == 0 )
	    //      System.out.println("Analyzing postion no. "+rot+" in Bombe.");
	    
	    String rotorPositions = indexEnigma.rotors();

	    //	    System.out.print(rot+") Index Enigma->"+rotorPositions+",");
	    //	    for (int i=0; i<numenigmas; i++) System.out.print(i+"->"+enigmas[i].rotors()+",");
	    //	    System.out.println();

	    //We crank at the beginning because the behavior written into the enigmas
	    //is to rotate first, and then transform.
	    indexEnigma.crank();
	    for (int i=0; i<numenigmas; i++)  
		enigmas[i].crank();
	    boolean totalMatch = true;
	    for(int i=0; totalMatch && i<sourceEnigmas.length; i++){
		boolean sourceMatch = false;
		for(char X='A'; !sourceMatch && X<='Z'; X++){
		    boolean letterMatch = true;
		    for(int j=0; letterMatch && j<linkEnigmas[i].length; j++){
			char Y = sourceEnigmas[i].encryptChar(X,false); //false so no cranking
			for(int k=0; k<linkEnigmas[i][j].length; k++)
			    Y = linkEnigmas[i][j][k].encryptChar(Y,false);
			if ( X != Y) 
			    letterMatch = false;
		    }
		    sourceMatch = letterMatch;  // true now
		}
		totalMatch = sourceMatch; // if no source match, false now
	    }
	    if (totalMatch)
		matches.add(rotorPositions);
	}
	//	System.out.println("Got to end of search.  matches.length="+matches.size());
	Object[] temp = matches.toArray();
	//	System.out.println("Converted array.  out.length="+temp.length);
	String[] out = new String[temp.length];
	for(int i=0; i<out.length; i++) out[i] = (String)temp[i];
	return out;
    }
}
