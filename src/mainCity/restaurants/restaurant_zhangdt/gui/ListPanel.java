package mainCity.restaurants.restaurant_zhangdt.gui;

import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener, KeyListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    
    /*Adding Text Field for Lab 2*/ 
    public JTextField nameSlot = new JTextField(); 
    private JCheckBox hungerCB = new JCheckBox(); 

    private EllenRestaurantPanel restPanel;
    private String type;
    
    private Object currentPerson;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(EllenRestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        
        nameSlot.setMaximumSize(new Dimension(250, 10));
        nameSlot.addKeyListener(this);
        add(nameSlot); 
        
        
        if(type == "Customers") {
	        hungerCB.setText("Hungry?");
	        hungerCB.setEnabled(false);
	        add(hungerCB); 
        }
        

        addPersonB.addActionListener(this);
        add(addPersonB);

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }
    
    public void keyReleased(KeyEvent k){
    	if (nameSlot.getText().compareTo("") != 0) { 
    		hungerCB.setEnabled(true);
    	}
    	else {
    		hungerCB.setEnabled(false);
    	}
    }
    
    public void keyPressed(KeyEvent k){
    	if (nameSlot.getText().compareTo("") != 0) { 
    		hungerCB.setEnabled(true);
    	}
    	else {
    		hungerCB.setEnabled(false);
    	}
    }
    
    public void keyTyped(KeyEvent k){
    	if (nameSlot.getText().compareTo("") != 0) { 
    		hungerCB.setEnabled(true);
    	}
    	else {
    		hungerCB.setEnabled(false);
    	}
    }
   
    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
            //addPerson(JOptionPane.showInputDialog("Please enter a name:"));
            addPerson(nameSlot.getText()); 
            nameSlot.setText("");
        }
        
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        }
    }
    
    

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);
            
            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson(type, name, hungerCB.isSelected());//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}
