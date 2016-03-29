package webcrypt.stats;
/**
 * A class used to analyze ciphertexts arising from a
 * periodic polyalphabetic substitution.  Rather than having
 * static classes, we use object oriented design to allow
 * for customization of analysis, and concurrent analysis
 * of several texts in different windows.
 *
 * We will make use of methods from MonoalphabeticStats so extend that class.
 */


public class VigenereStats extends MonoAlphabeticStats{

    /**
     * Frequencies of A-Z in Moby Dick:
     */
    static final double[] FREQ = new double[] {
	0.08184722174077004, 0.017728294997484195, 0.023642278574887526, 0.04014680965271366, 0.12300035399866174, 
	0.02188384011865783, 0.021870184383932033, 0.06607064636794222, 0.0687345650806056, 0.0011365773056395032, 
	0.008465505088862067, 0.04495257783506183, 0.024451118247107873, 0.06892679580789952, 0.07282393241041575, 
	0.01812536174566509, 0.001634486402564757, 0.05476474845611415, 0.0674708843979029, 0.09243566835892733,
	0.028043626921125525, 0.009031692859416312, 0.023343953293185473, 0.001081954366736311, 0.017723042791820427, 
	6.638787959003384E-4
    };
    
    public VigenereStats(){
	super();
    }

    
    /**
     * Return an array listing the averaged index of coincidence assuming
     * Vigenere period 1 through period maxPeriod.
     * The index of coincidence is defined as the probability that two random
     * letters will be coincident.  The formula is
     * 
     *  I_c = [(Sum f_i^2) - len ] / [ len^2 - len ]
     *
     *  where f_i is the frequency of the i'th letter and len is the length of the text
     *
     * For totally random text this rapidly approaches 1/n where n is the number of letters.
     * But for structure text, this will be considerable greater than 1/n (a factor of 2 can
     * be expected)
     *
     * Letting x^i_j denote the substring of x starting at the j'th letter and with letters
     * i space apart (e.g. in x = "hellomom", x^3^2 = "eom") define I_c(i) to be average
     * of all coincidence indices for letters i apart in x.  IE:
     *
     * I_c(i) = Avg ( I_c^i_1 , I_c^i_2 , I_c^i_3 , ... , I_c^i_i )
     * 
     * The return array is
     *
     * { I_c(1) , I_c(2) , ... , I_c(maxPeriod) }
     */
    public double[] avgCoincidenceIndex(String text, int maxPeriod){
	//first capitalize since don't care about cases
	text = text.toUpperCase();

	String[][] substrings = periodicSubstrings(text,maxPeriod); // periodic substrings x^i_j of x

	double[] out = new double[maxPeriod];
	for(int i=0; i<maxPeriod ; i++){
	    double tmp = 0;
	    for(int j=0; j < i+1; j++){
		tmp += indexOfCoincidence(substrings[i][j]);
	    }
	    out[i] = tmp/(i+1);
	}
	return out;
    }


    /**
     * Compute I_c(x)
     */
    public double indexOfCoincidence(String x){
	if (x==null || x.length()<2) return 1;
	CharFrequency[] freqs = getFrequencies(x);
	double sum = 0;
	for(int i=0; i<freqs.length; i++) sum += Math.pow(freqs[i].getCount(),2);
	int n = x.length();
	return (sum - n)/(n*n - n);
    }


    /**
     * Part 2 of Friedman's analysis.  If the period has been discovered, interpolate the expected frequencies with
     * the frequencies obtained for each offsetted translation, at each possible offset.
     *
     * INPUT:  the text x, and the period p
     * OUTPUT: double array of interpolated frequencies of the form:
     *         { { M[0][0] , ... , M[0][25] } ,
     *           { M[1][0] , ... , M[1][25] } ,
     *          ...
     *           { M[p-1][0] , ... , M[p-1][25] } ,
     *
     * Where M[i][j] is the interpolation of the standard frequency chart FREQ
     * with the frequencies of the substring x_i,x_i+p,x_i+2p, ... 
     * translated back by j letters.
     * For j the incorrect translation, we expect the interpolation of two
     * uncorrelated random variables with the same expectation. So expect to get:
     * Sum ( X_i*Y_i ) / n  = weighted average of (X) = E(X) = 1/26 = .038
     *
     * But for the correct j we expect to get the variance of structured English:
     * Sum ( X_i^2 ) / n = Var(X) = .065
     */
    public double[][] FriedmanTable(String x, int p){
	//first capitalize since don't care about cases
	x = x.toUpperCase();

	// easy way to get table
	CharFrequency[][] cftable = new CharFrequency[p][];
	String[] substrings = (periodicSubstrings(x,p))[p-1];
	for(int i=0; i<p; i++) cftable[i] = unorderedFrequencies(substrings[i]);
	
	//now convert to table of double's.  
	double[][] table = new double[p][cftable[0].length];
	for(int i=0; i<p; i++){
	    int totalcount = substrings[i].length();
	    for(int j=0; j<domsize; j++){
		double interpolation = 0;
		for (int k=0; k<domsize; k++){
		    interpolation += FREQ[k]*cftable[i][(k+j+domsize) % domsize].getCount();
		}
		table[i][j] = interpolation / totalcount;
	    }
	}
	return table;
    }


    /**
     * Return all the periodic substrings of string x.  Only characters in domain are kept for periodic analysis.
     *
     * Letting x^i_j denote the substring of x starting at the j'th letter and with letters
     * i space apart (e.g. in x = "hellomom", x^3^2 = "eom") return the double array.
     * { { x^1_1 == x } ,
     * { { x^2_1 , x^2_2 }
     * { { x^3_1 , x^3_2 , x^3_3 }
     * ...
     * { { x^m_1 , x^m_2 , x^m_3 , ... , x^m_m }
     *
     * Where m is the maxPeriod
     */
    public String[][] periodicSubstrings(String x, int maxPeriod){
	x = stripNondomain(x); // remove all characters that would have been ignore by the Vigenere encryption
	String[][] out = new String[maxPeriod][];
	for(int i = 0; i < maxPeriod; i++){
	    out[i] = new String[i+1];
	    for(int j = 0; j < i+1; j++){
		StringBuffer sb = new StringBuffer("");
		for(int k=j; k<x.length(); k += (i+1)){
		    //		    System.out.println("i = "+i+", j = "+j+", k = "+k);
		    sb.append(x.charAt(k));
		}
		out[i][j] = sb.toString();
	    }
	}
	return out;
    }
    public static void main(String[] args){
	VigenereStats vs = new VigenereStats();

	//periodic substrings
	String[][] substrings = vs.periodicSubstrings(args[0],10);
	for(int i=0; i<substrings.length; i++){
	    for(int j=0; j<substrings[i].length; j++)
		System.out.print(substrings[i][j]+"\t");
	    System.out.print("\n");
	}


	//indices of coincidence
	double[] indices = vs.avgCoincidenceIndex(args[0],10);
	for(int i=0; i<substrings.length; i++)
	    System.out.println("I_c("+(i+1)+") = "+ indices[i]);
    }

//BASICALLY USELESS.  MAYBE WILL FIND SOME USE FOR THIS:
    /**
     * THIS DIDN'T ACTUALLY WORK BECAUSE EYEBALLING IS TOO DIFFICULT
     * 
     * Part 2 of Friedman's analysis.  If the period has been discovered, compute the frequencies of all letters
     * at each translated subtext.   Return the ordered letters as well as a standart English example of the
     * most common letters.  This will let the user "eyeball" the correct translation at each key.
     *
     * INPUT:  the text x, and the period p
     * OUTPUT: array of CharFrequencies table, such that table[0] are the sorted letters at offset 0, etc.
     */
    public char[][] OLDFriedmanTable(String x, int p){
	// easy way to get table
	CharFrequency[][] cftable = new CharFrequency[p][];
	String[] substrings = (periodicSubstrings(x,p))[p-1];
	for(int i=0; i<p; i++) cftable[i] = getFrequencies(substrings[i]);
	
	//now convert to table of char's.  Anticipate problems as well.
	char[][] table = new char[p][cftable[0].length];
	for(int i=0; i<p; i++)
	    for(int j=0; j<cftable[i].length && j<table[i].length; j++)
		table[i][j] = (cftable[i][j] != null) ? cftable[i][j].getChar() : '!';
	return table;
    }


}
