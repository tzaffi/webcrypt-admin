package webcrypt.stats.gui;
/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import webcrypt.stats.*;

/**
 * This class is a Swing component that presents a choice to the user.  It
 * allows the choice to be presented in a JList, in a JComboBox, or with a
 * bordered group of JRadioButton components.  Additionally, it displays the
 * name of the choice with a JLabel.  It allows an arbitrary analyzor to be
 * associated with each possible choice.  Note that this component only allows
 * one item to be selected at a time.  Multiple selections are not supported.
 **/
public class StatsRadio extends JPanel {
    // These fields hold property values for this component
    String name;           // The overall name of the choice
    
    String[] labels;       // The text for each choice option
    Analyzor[] analyzors;       // Arbitrary analyzors associated with each option
    int selection;         // The selected choice
    int presentation;      // How the choice is presented
    JRadioButton[] radiobuttons;    // Yet another type

    // The list of objects that are interested in our state
    ArrayList listeners = new ArrayList();

    // The constructor method sets everything up
    public StatsRadio(String name, Analyzor[] analyzors,
		       int defaultSelection)
    {
	// Copy the constructor arguments to instance fields
	this.name = name;
	this.labels = new String[analyzors.length];
	for(int i=0; i<analyzors.length; i++)
	    labels[i] = analyzors[i].toString();
	this.analyzors = analyzors;
	this.selection = defaultSelection;
	//	this.presentation = presentation;
	initRadioButtons();
    }

    // Initialization for JRadioButton 
    void initRadioButtons() {
	// Create an array of mutually exclusive radio buttons
	radiobuttons = new JRadioButton[analyzors.length];   // the array
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
				StatsRadio.this.select(i);
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
	for(int i = 0; i < analyzors.length; i++) {
	    radiobuttons[i] = new JRadioButton(labels[i]);
	    //	    System.out.println(labels[i]);
	    if (i == selection) radiobuttons[i].setSelected(true);
	    radiobuttons[i].addChangeListener(listener);
	    radioButtonGroup.add(radiobuttons[i]);
	    this.add(radiobuttons[i]);
	}
    }
    
    // These simple property accessor methods just return field values
    // These are read-only properties.  The values are set by the constructor
    // and may not be changed.
    public String getName() { return name; }
    //    public int getPresentation() { return presentation; }
    public String[] getLabels() { return labels; }
    public Analyzor[] getAnalyzors() { return analyzors; }
    
    /** Return the index of the selected item */
    public int getSelectedIndex() { return selection; }
    
    /** Return the object associated with the selected item */
    public Analyzor getSelectedAnalyzor() { return analyzors[selection]; }
    
    /**
     * Set the selected item by specifying its index.  Calling this
     * method changes the on-screen display but does not generate events.
     **/
    public void setSelectedIndex(int selection) {
	radiobuttons[selection].setSelected(true);
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
	    StatsRadio.Event e =
		new StatsRadio.Event(this, selection, analyzors[selection]);
	    // Loop through the listeners using an Iterator
	    for(Iterator i = listeners.iterator(); i.hasNext();) {
		StatsRadio.Listener l = (StatsRadio.Listener)i.next();
		l.itemChosen(e);  // Notify each listener of the selection
	    }
	}
    }

    // These methods are for event listener registration and deregistration
    public void addStatsRadioListener(StatsRadio.Listener l) {
	listeners.add(l);
    }
    public void removeStatsRadioListener(StatsRadio.Listener l) {
	listeners.remove(l);
    }

    /**
     * This inner class defines the event type generated by StatsRadio objects
     * The inner class name is Event, so the full name is StatsRadio.Event
     **/
    public static class Event extends java.util.EventObject {
	int selectedIndex;      // index of the selected item
	Analyzor selectedAnalyzor;   // the analyzor associated with it
	public Event(StatsRadio source,
		     int selectedIndex, Analyzor selectedAnalyzor) {
	    super(source);
	    this.selectedIndex = selectedIndex;
	    this.selectedAnalyzor = selectedAnalyzor;
	}

	public StatsRadio getStatsRadio() { return (StatsRadio)getSource();}
	public int getSelectedIndex() { return selectedIndex; }
	public Analyzor getSelectedAnalyzor() { return selectedAnalyzor; }
    }

    /**
     * This inner interface must be implemented by any object that wants to be
     * notified when the current selection in a StatsRadio component changes.
     **/
    public interface Listener extends java.util.EventListener {
	public void itemChosen(StatsRadio.Event e);
    }

}







