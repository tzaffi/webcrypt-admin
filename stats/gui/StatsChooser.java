/*
 * Notice by Zeph Grunschlag:
 * This program is based on a similar program created by David Flanagan.
 * In keeping with David Flanagan's instructions I have retained his notice
 * in the final paragraph of this comment.  It is very likely that
 * little if any code was actually changed.  The following
 * quantifies how much was actually changed from David Flanagan's original:
 * >>>>>MODERATE CHANGES.  CHANGED NAME AND PACKAGE HIERARCHY.
 *        REMOVED AND ADDED A COUPLE OF COMPONENTS.
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
import java.util.*;

/**
 * This class is a Swing component that presents a choice to the user.  It
 * allows the choice to be presented in a JList, in a JComboBox, or with a
 * bordered group of JRadioButton components.  Additionally, it displays the
 * name of the choice with a JLabel.  It allows an arbitrary value to be
 * associated with each possible choice.  Note that this component only allows
 * one item to be selected at a time.  Multiple selections are not supported.
 **/
public class StatsChooser extends JPanel {
    // These fields hold property values for this component
    String name;           // The overall name of the choice
    String[] labels;       // The text for each choice option
    Object[] values;       // Arbitrary values associated with each option
    int selection;         // The selected choice
    int presentation;      // How the choice is presented

    // These are the legal values for the presentation field
    public static final int LIST = 1;
    public static final int COMBOBOX = 2;
    public static final int RADIOBUTTONS = 3;
    public static final int TEXT = 4; //ZEPH ADD-IN

    // These components are used for each of the 3 possible presentations
    JList list;                     // One type of presentation
    JComboBox combobox;             // Another type of presentation
    JRadioButton[] radiobuttons;    // Yet another type
    JTextArea textarea;             // ZEPH ADD-IN>>> NEED IT FOR KEY

    // The list of objects that are interested in our state
    ArrayList listeners = new ArrayList();

    // The constructor method sets everything up
    public StatsChooser(String name, String[] labels, Object[] values,
		       int defaultSelection, int presentation)
    {
	// Copy the constructor arguments to instance fields
	this.name = name;
	this.labels = labels;
	this.values = values;
	this.selection = defaultSelection;
	this.presentation = presentation;

	// If no values were supplied, use the labels
	if (values == null) this.values = labels;

	// Now create content and event handlers based on presentation type
	switch(presentation) {
	case LIST: initList(); break;
	case COMBOBOX: initComboBox(); break;
	case RADIOBUTTONS: initRadioButtons(); break;
	case TEXT: initTextArea(); break;
	}
    }

    // Initialization for JList presentation
    void initList() {
	list = new JList(labels);          // Create the list
	list.setSelectedIndex(selection);  // Set initial state
	
	// Handle state changes
	list.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    StatsChooser.this.select(list.getSelectedIndex());
		}
	    });
	
	// Lay out list and name label vertically
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // vertical
	this.add(new JLabel(name));        // Display choice name
	this.add(new JScrollPane(list));   // Add the JList
    }
    
    // Initialization for JComboBox presentation
    void initComboBox() {
	combobox = new JComboBox(labels);         // Create the combo box
	combobox.setSelectedIndex(selection);     // Set initial state
	
	// Handle changes to the state
	combobox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    StatsChooser.this.select(combobox.getSelectedIndex());
		}
	    });
	
	// Lay out combo box and name label horizontally
	this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	this.add(new JLabel(name));
	this.add(combobox);
    }

    // Initialization for JRadioButton presentation
    void initRadioButtons() {
	// Create an array of mutually exclusive radio buttons
	radiobuttons = new JRadioButton[labels.length];   // the array
	ButtonGroup radioButtonGroup = new ButtonGroup(); // used for exclusion
	ChangeListener listener = new ChangeListener() {  // A shared listener
		public void stateChanged(ChangeEvent e) {
		    JRadioButton b = (JRadioButton)e.getSource();
		    if (b.isSelected()) {
			// If we received this event because a button was
			// selected, then loop through the list of buttons to
			// figure out the index of the selected one.
			for(int i = 0; i < radiobuttons.length; i++) {
			    if (radiobuttons[i] == b) {
				StatsChooser.this.select(i);
				return;
			    }
			}
		    }
		}
	    };
    
	// Display the choice name in a border around the buttons
	this.setBorder(new TitledBorder(new EtchedBorder(), name));
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	// Create the buttons, add them to the button group, and specify
	// the event listener for each one.
	for(int i = 0; i < labels.length; i++) {
	    radiobuttons[i] = new JRadioButton(labels[i]);
	    if (i == selection) radiobuttons[i].setSelected(true);
	    radiobuttons[i].addChangeListener(listener);
	    radioButtonGroup.add(radiobuttons[i]);
	    this.add(radiobuttons[i]);
	}
    }
    
    // Initialization for JComboBox presentation
    void initTextArea() {
	textarea = new JTextArea(labels[0]);
	//	combobox.setSelectedIndex(selection);     // Set initial state
	
	// Handle changes to the state
	/*	textarea.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    System.out.println(textarea.getText());
		}
	    });
	*/
	// Lay out text area and name label horizontally
	this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	this.add(new JLabel(name));
	this.add(textarea);
    }

    // These simple property accessor methods just return field values
    // These are read-only properties.  The values are set by the constructor
    // and may not be changed.
    public String getName() { return name; }
    public int getPresentation() { return presentation; }
    public String[] getLabels() { return labels; }
    public Object[] getValues() { return values; }
    
    /** Return the index of the selected item */
    public int getSelectedIndex() { return selection; }
    
    /** Return the object associated with the selected item */
    public Object getSelectedValue() { return values[selection]; }
    
    /**
     * Set the selected item by specifying its index.  Calling this
     * method changes the on-screen display but does not generate events.
     **/
    public void setSelectedIndex(int selection) {
	switch(presentation) {
	case LIST: list.setSelectedIndex(selection); break;
	case COMBOBOX: combobox.setSelectedIndex(selection); break;
	case RADIOBUTTONS: radiobuttons[selection].setSelected(true); break;
	}
	this.selection = selection;
    }

    /**
     * This internal method is called when the selection changes.  It stores
     * the new selected index, and fires events to any registered listeners.
     * The event listeners registered on the JList, JComboBox, or JRadioButtons
     * all call this method.
     **/
    protected void select(int selection) {
	this.selection = selection;  // Store the new selected index
	if (!listeners.isEmpty()) {  // If there are any listeners registered
	    // Create an event object to describe the selection
	    StatsChooser.Event e =
		new StatsChooser.Event(this, selection, values[selection]);
	    // Loop through the listeners using an Iterator
	    for(Iterator i = listeners.iterator(); i.hasNext();) {
		StatsChooser.Listener l = (StatsChooser.Listener)i.next();
		l.itemChosen(e);  // Notify each listener of the selection
	    }
	}
    }

    // These methods are for event listener registration and deregistration
    public void addStatsChooserListener(StatsChooser.Listener l) {
	listeners.add(l);
    }
    public void removeStatsChooserListener(StatsChooser.Listener l) {
	listeners.remove(l);
    }

    /**
     * This inner class defines the event type generated by StatsChooser objects
     * The inner class name is Event, so the full name is StatsChooser.Event
     **/
    public static class Event extends java.util.EventObject {
	int selectedIndex;      // index of the selected item
	Object selectedValue;   // the value associated with it
	public Event(StatsChooser source,
		     int selectedIndex, Object selectedValue) {
	    super(source);
	    this.selectedIndex = selectedIndex;
	    this.selectedValue = selectedValue;
	}

	public StatsChooser getStatsChooser() { return (StatsChooser)getSource();}
	public int getSelectedIndex() { return selectedIndex; }
	public Object getSelectedValue() { return selectedValue; }
    }

    /**
     * This inner interface must be implemented by any object that wants to be
     * notified when the current selection in a StatsChooser component changes.
     **/
    public interface Listener extends java.util.EventListener {
	public void itemChosen(StatsChooser.Event e);
    }

    /**
     * This inner class is a simple demonstration of the StatsChooser component
     * It uses command-line arguments as StatsChooser labels and values.
    public static class Demo {
	public static void main(String[] args) {
	    // Create a window, arrange to handle close requests
	    final JFrame frame = new JFrame("StatsChooser Demo");
	    frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {System.exit(0);}
		});

	    // A "message line" to display results in
	    final JLabel msgline = new JLabel(" ");

	    // Create a panel holding three StatsChooser components
	    JPanel chooserPanel = new JPanel();
	    final StatsChooser c1 = new StatsChooser("Algorithm", args, null, 0,
						       StatsChooser.RADIOBUTTONS);
	    final StatsChooser c2 = new StatsChooser("Key", args, null, 0,
						       StatsChooser.TEXT);
	    //	    final StatsChooser c2 = new StatsChooser("Choice #2", args, null, 0,
	    //						   StatsChooser.COMBOBOX);
	    //	    final StatsChooser c3 = new StatsChooser("Choice #3", args, null, 0,
	    //						   StatsChooser.LIST);
	    
	    // An event listener that displays changes on the message line
	    StatsChooser.Listener l = new StatsChooser.Listener() {
		    public void itemChosen(StatsChooser.Event e) {
			msgline.setText(e.getStatsChooser().getName() + ": " +
					e.getSelectedIndex() + ": " +
					e.getSelectedValue());
		    }
		};
	    c1.addStatsChooserListener(l);
	    c2.addStatsChooserListener(l);
	    //	    c3.addStatsChooserListener(l);

	    // Instead of tracking every change with a StatsChooser.Listener,
	    // applications can also just query the current state when
	    // they need it.  Here's a button that does that.
	    JButton encrypt = new JButton("Encrypt");
	    encrypt.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			// Note the use of multi-line italic HTML text
			// with the JOptionPane message dialog box.
			String msg = "<html><i>" +
			  c1.getName() + ": " + c1.getSelectedValue() + "<br>"+
			    c2.getName() + ": " + textarea.getText()+"</i>";// + "<br>"+
			    // c3.getName() + ": " + c3.getSelectedValue() + "</i>";
			JOptionPane.showMessageDialog(frame, msg);
		    }
		});

	    // ZEPH ADDITION--> basically COPIED/PASTED previous paragraph 
	    // Instead of tracking every change with a StatsChooser.Listener,
	    // applications can also just query the current state when
	    // they need it.  Here's a button that does that.
	    JButton decrypt = new JButton("Decrypt");
	    decrypt.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			// Note the use of multi-line italic HTML text
			// with the JOptionPane message dialog box.
			String msg = "<html><i>" +
			  c1.getName() + ": " + c1.getSelectedValue() + "<br>"+
			    c2.getName() + ": " + c2.getSelectedValue()+"</i>";// + "<br>"+
			    // c3.getName() + ": " + c3.getSelectedValue() + "</i>";
			JOptionPane.showMessageDialog(frame, msg);
		    }
		});

	    // Add the 3 StatsChooser objects, and the Button to the panel
	    chooserPanel.add(c1);
	    chooserPanel.add(c2);
	    //	    chooserPanel.add(c3);

	    // Create a vertical panel holding two StatsChooser components
	    JPanel subpanel = new JPanel();
	    subpanel.setLayout(new BoxLayout(subpanel,BoxLayout.Y_AXIS));
	    subpanel.add(encrypt);
	    subpanel.add(decrypt);

	    chooserPanel.add(subpanel);

	    // Add the panel and the message line to the window
	    Container contentPane = frame.getContentPane();
	    contentPane.add(chooserPanel, BorderLayout.CENTER);
	    contentPane.add(msgline, BorderLayout.SOUTH);
	    
	    // Set the window size and pop it up.
	    frame.pack();
	    frame.show();
	}
    }
    */
}
