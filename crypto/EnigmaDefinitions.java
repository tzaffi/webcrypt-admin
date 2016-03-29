package webcrypt.crypto;

/**
 * Some useful definitions for the Enigma simulation.
 *
 * Created separate interface so could add more rotors for more complicated
 * simulations.
 */
public interface EnigmaDefinitions{

    /**
     * The following arrays were taken from
     * <a href="http://www.cs.miami.edu/~harald/enigma/enigma.c">Harald Schmidl</a>
     * and are based on 
     * <a href="http://www.enigma-replica.com/wiring.html">Enigma Replica Homepage</a>
    /* Rotor wirings */
    public final static String[] ROTORS = new String[]{
	                          /* Input:"ABCDEFGHIJKLMNOPQRSTUVWXYZ" */
	                          /* 1: */ "EKMFLGDQVZNTOWYHXUSPAIBRCJ",
	                          /* 2: */ "AJDKSIRUXBLHWTMCQGZNPYFVOE",
				  /* 3: */ "BDFHJLCPRTXVZNYEIWGAKMUSQO",
				  /* 4: */ "ESOVPZJAYQUIRHXLNFTGKDCMWB",
				  /* 5: */ "VZBRGITYUPSDNHLXAWMJQOFECK" };
    public final static String REFLECTOR = "YRUHQSLDPXNGOKMIEBFZCWVJAT";
    
    public final static char[] NOTCHES = new char[]{ 'Q','E','V','J','Z' };
 
    /**
     * Associated types
     */
    public final static boolean ROTORTYPE = true;
    public final static boolean REFLECTORTYPE = false;

    /**
     * Number of rotors
     */
    public final static int NUMROTORS = 3;
}

