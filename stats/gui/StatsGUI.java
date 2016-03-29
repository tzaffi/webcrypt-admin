/*
 * Notice by Zeph Grunschlag:
 * This program is based on a similar program created by David Flanagan.
 * In keeping with David Flanagan's instructions I have retained his notice
 * in the final paragraph of this comment.  It is very likely that
 * little if any code was actually changed.  The following
 * quantifies how much was actually changed from David Flanagan's original:
 * >>>>>LOTS OF CHANGES.  CHANGED NAME AND PACKAGE HIERARCHY.
 *        REMOVED AND ADDED A COUPLE OF COMPONENTS.
 *        CUT OUT MOST OF THE CODE
 *
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */
package webcrypt.stats.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.*;                 // Networking with URLs (and loading files as well!)
import java.io.*;                  // Input/output (File class)
import javax.swing.filechooser.*;
// Import this class by name.  JFileChooser uses it, and its name conflicts
// with java.io.FileFilter
import javax.swing.filechooser.FileFilter;
import java.util.*;
import webcrypt.stats.*;
// ZEPH ADD-IN>> for downloading web-pages as text (for viewing source)
import webcrypt.net.GetURL;

/**
 * A simple GUI that combines all the necessary elements to provide
 * for graphical encryption functionality
 **/
public class StatsGUI extends JDialog{ //JDialog locks up calling frame to avoid synchro prob's
    
    /** following code based on WebBrowser.openPage() .*/
    // make staic so that will persist between different instantiations of StatsGUI
    static final JFileChooser fileChooser = new JFileChooser();
    static{
	// This javax.swing.filechooser.FileFilter displays only TXT files
	FileFilter filter = new FileFilter() {
		public boolean accept(File f) {
		    String fn = f.getName();
		    if (fn.endsWith(".text") || fn.endsWith(".txt"))
			return true;
		    else return false;
		}
		public String getDescription() { return "TEXT Files"; }
	    };
	fileChooser.setFileFilter(filter);
	fileChooser.addChoosableFileFilter(filter);
    }


    /**
     * A StatsGUI object is created with a particular text "before" in mind
     * and using an array of statistical analyzors
     */
    public StatsGUI(Frame parent, Analyzor[] analyzorsin, StringBuffer textin) {
	super(parent, "StatsDialog", true);  // this is "modal" dialogue so parent locked up
	setDefaultCloseOperation(DISPOSE_ON_CLOSE); //DISPOSE_ON_CLOSE
	final StatsGUI self = this;

	final StringBuffer text = textin;//Prior to applying encrypt or decrypt

	final Analyzor[] analyzors = analyzorsin;

	// A "message line" to display results in
	final JLabel msgline = new JLabel(" ");
	
	// Create a panel holding three StatsRadio components
	JPanel chooserPanel = new JPanel();

	final StatsRadio radiobuttons = new StatsRadio("Statistical Test", analyzors, 0);

	// Create argument panel
	JPanel argpanel = new JPanel();
	argpanel.setBorder(new TitledBorder(new EtchedBorder(), "Enter Argument"));
	argpanel.setLayout(new BorderLayout());
	final JTextField argtext = new JTextField(20);  // enter text directly
	final JButton argfile = new JButton("Load argument from file");	

	argfile.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
 		    //		    System.out.println("TEST1");
		    //avoid wrong encryption if window closed during operation:
		    self.setDefaultCloseOperation(DISPOSE_ON_CLOSE);		    
		    /** following code based on WebBrowser.openPage() .*/
		    
		    // Ask the user to choose a file.
		    int result = fileChooser.showOpenDialog(self);
		    if (result == JFileChooser.APPROVE_OPTION) {
			// If they didn't click "Cancel", then try to display the file.
			File selectedFile = fileChooser.getSelectedFile();
			String url = "file://" + selectedFile.getAbsolutePath();
			String arg;
			try{
			    arg = GetURL.getText(new URL(url));
			}catch(MalformedURLException mue){
			    //THIS SHOULD NEVER HAPPEN!!!
			    arg = "MalformedURLException.  Try Again!!!";
			}
			//put the text inside the text are (if too large will be mostly hidden
			//but should not affect encryption behavior for reasonable sizes
			argtext.setText(arg);
		    }
		}
	    });
	argpanel.add(argtext,"North");
	argpanel.add(argfile,"South");

	// An event listener that displays changes on the message line
	StatsRadio.Listener l = new StatsRadio.Listener() {
		public void itemChosen(StatsRadio.Event e) {
		    msgline.setText(e.getStatsRadio().getName() + ": " +
				    e.getSelectedIndex() + ": " +
				    e.getSelectedAnalyzor());
		}
	    };
	radiobuttons.addStatsRadioListener(l);
	/*
	c2.addStatsRadioListener(l);
	c3.addStatsRadioListener(l);
	*/

	// Instead of tracking every change with a Listener,
	// applications can also just query the current state when
	// they need it.  Here's a button that does that.
	JButton analyze = new JButton("Analyze");
	analyze.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
 		    //		    System.out.println("TEST1");
		    try{
			//			System.out.println("TEST2");
			//avoid wrong analysis if window closed during operation:
			self.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		    
			
			Analyzor anzr = radiobuttons.getSelectedAnalyzor();
			anzr.analyzeOn(text,argtext.getText());
		    }catch(IllegalArgumentException iae){
			//			System.out.println("TEST3");			
			JOptionPane.showMessageDialog(self,""+iae);
		    }finally{ dispose(); }
		}
	    });

	// Add the 3 StatsChooserOLD objects, and the Button to the panel
	chooserPanel.add(radiobuttons);
	//chooserPanel.add(c2);
	//chooserPanel.add(c3);

	// Create a vertical panel holding two StatsChooserOLD components
	JPanel subpanel = new JPanel();
	subpanel.setLayout(new GridBagLayout());

	GridBagConstraints c = new GridBagConstraints();
	//	c.fill = GridBagConstraints.BOTH; //growth in both dimensions
	c.insets = new Insets(5,5,5,5);   //5-pixel margins
	
	c.gridx = c.gridy = 0;
	c.gridwidth = 1;
	c.gridheight = analyzors.length + 1;
	c.weightx = c.weighty = 1.0;
	subpanel.add(chooserPanel, c);
       
	c.gridx = 1; c.gridy = analyzors.length/2;
	c.gridheight = 1; c.gridwidth = 1;
	subpanel.add(argpanel,c);


	JPanel buttonpanel = new JPanel();
	buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(analyze, "Center");
	//	buttonpanel.add(decrypt, "South");

	c.weightx = c.weighty = 0.0;
	c.gridx = 2; c.gridy= analyzors.length/ 2; 
	c.gridwidth = 1; c.gridheight = 2;
	subpanel.add(buttonpanel, c);

	// Set the window size and pop it up.
	getContentPane().add(subpanel);
    }

    /**
     * A demonstration
    public static void main(String[] args){
	final Analyzor[] analyzors = new Analyzor[6];
	for(int i = 0; i<4 ; i++)
	    analyzors[i] = new Caesar();
	analyzors[4] = new Vigenere();
	analyzors[5] = new Linear();

	final JFrame frame = new JFrame("Demo");
	
	final JButton top = new JButton("ABCDEFGHIJKLMNOPQRSTUVWXYZ - ORIGINAL PLAINTEXT");
	final StringBuffer text = new StringBuffer(top.getText());

	JButton bigbutton = new JButton("CLICK ME TO ANALYZE or DECRYPT ABOVE");
	bigbutton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    StatsGUI dialog = new StatsGUI(frame,analyzors,text);
		    
		    dialog.pack();
		    dialog.setLocationRelativeTo(frame);
		    dialog.setVisible(true);
		    //NOW FREEZES UP UNTIL DIALOG EXITS!!!!
		    
		    top.setText(text.toString());
		}
	    });
	frame.getContentPane().setLayout(new BorderLayout(10,10));
	frame.getContentPane().add(top,"North");
	frame.getContentPane().add(bigbutton,"South");
	frame.pack();
	frame.show();
    }
    */
}
