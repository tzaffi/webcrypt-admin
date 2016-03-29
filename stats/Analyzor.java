package webcrypt.stats;

/** 
 * The Analyzor class is an abstract class bridging between the statistical
 * classes in this package and the StatsGUI class used by WebBrowser for 
 * statistically analyzing texts interactively.
 */
public abstract class Analyzor{

    /**
     * Statistically analyze the text and mutate it to it's statistical analysis.
     */
    public abstract void analyzeOn(StringBuffer text, String arg);

    /**
     * Override this method with the encryption algorithms name
     * (this method is called by GUI's that make use of the Analyzor)
     */
    public abstract String toString();  //Algorithm name
}













