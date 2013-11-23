package mainCity.restaurants.jeffersonrestaurant.gui;

import javax.swing.*;

import mainCity.restaurants.jeffersonrestaurant.JeffersonCustomerRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonWaiterRole;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    //private JButton addWaiterB = new JButton("AddWaiter");
    //private JButton pause = new JButton("Pause");
    //private JButton run = new JButton("Run");
    //create a textfield
    JTextField Entry = new JTextField(25);
    //JTextField waiterEntry = new JTextField(25);
    JCheckBox initial = new JCheckBox("Hungry?");

    private JeffersonRestaurantPanel restPanel;
    private String type;

    
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(JeffersonRestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;
        
        //pause.addActionListener(this);
        //add(pause);
        //run.addActionListener(this);
        //add(run);
        
       
        

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);
        add(addPersonB);

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        
        Entry.setPreferredSize(new Dimension(160,20));
        Entry.setMinimumSize(new Dimension(160, 20));
        Entry.setMaximumSize(new Dimension(160, 20));
        //waiterEntry.setPreferredSize(new Dimension(160,20));
        //waiterEntry.setMinimumSize(new Dimension(160, 20));
        //waiterEntry.setMaximumSize(new Dimension(160, 20));
        //initial.setEnabled(false);
        add(Entry);
        if (this.type.equals("Customers")){
        	add(initial);
        }
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
    	
    	if (!(Entry.getText()=="") || Entry.getText() == null){
    	initial.setEnabled(true);	
    	}
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
            //addPerson(JOptionPane.showInputDialog("Please enter a name:"));
        	addPerson(Entry.getText(), initial.isSelected());
        	Entry.setText("");
        	initial.setSelected(false);
        	//initial.setEnabled(false);
        }
      
        

        /*
        if(e.getSource()==addWaiterB){
        	System.out.println("waiter added");
        	addwaiter(waiterEntry.getText());
        	
        }
        */
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
    public void addPerson(String name,boolean hungry) {
        if (!name.equals("") || name == null) {
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
            restPanel.addPerson(type, name,hungry);//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
    
   
}