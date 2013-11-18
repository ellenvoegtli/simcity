package mainCity.restaurants.jeffersonrestaurant.gui;

import javax.swing.*;

import mainCity.restaurants.jeffersonrestaurant.CustomerRole;
import mainCity.restaurants.jeffersonrestaurant.WaiterRole;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * 
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JPanel myPanel;
    private JLabel myLabel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JButton askBreak = new JButton("Ask4Break");

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 1300;
        int WINDOWY = 700;

        //animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        //animationFrame.setVisible(true);
    	//animationFrame.
    	
    	
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);

        setLayout(new BorderLayout());
       
        Dimension restDim = new Dimension((int) (.5 * WINDOWX), (int) (WINDOWY * .6));
        restPanel.setPreferredSize(restDim);
        //restPanel.setMinimumSize(restDim);
        //restPanel.setMaximumSize(restDim);
        add(restPanel,BorderLayout.WEST);
        
      //add(new JTextField("I'm a JTextField", 5));
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(300, (int) (WINDOWY * .18));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        //infoPanel.setMinimumSize(infoDim);
        //infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        //animationPanel.setPreferredSize(infoDim);
        //animationPanel.setMinimumSize(infoDim);
        //animationPanel.setMaximumSize(infoDim);
        //add(animationPanel); 
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        askBreak.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        
        infoPanel.add(infoLabel);
      
        ;
        
       add(infoPanel,BorderLayout.SOUTH);
        
        //myPanel
        myPanel = new JPanel();
        myPanel.setPreferredSize(infoDim);
        myPanel.setMinimumSize(infoDim);
        myPanel.setMaximumSize(infoDim);
        myPanel.setBorder(BorderFactory.createTitledBorder("Identification"));
        myPanel.setLayout(new FlowLayout());
        myLabel = new JLabel(); 
        myLabel.setText("My name is Jefferson and this is not a Michelin starred establishment");
        myPanel.add(myLabel);
        add(myPanel,BorderLayout.NORTH);
        //animationPanel.setPreferredSize(infoDim);
        //animationPanel.setMaximumSize(infoDim);
        //animationPanel.setMinimumSize(infoDim);
        add(animationPanel,BorderLayout.CENTER);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
       
        currentPerson = person;

        if (person instanceof CustomerRole) {
        	infoPanel.add(stateCB);
        	stateCB.setVisible(true);
        	askBreak.setVisible(false);
        	
            CustomerRole customer = (CustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        
        if (person instanceof WaiterRole) {
        	stateCB.setVisible(false);
        	askBreak.setVisible(true);
            WaiterRole w = (WaiterRole) person;
            //stateCB.setText("Hungry?");
          //Should checkmark be there? 
            //stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            //stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoPanel.add(askBreak);
            infoLabel.setText(
               "<html><pre>     Name: " + w.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerRole) {
                CustomerRole c = (CustomerRole) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        
        if(e.getSource() == askBreak){
        	if(currentPerson instanceof WaiterRole){
        		WaiterRole w = (WaiterRole) currentPerson;
        		if(w.onBreak==true){
        			w.onBreak=false;
        			w.canBreak=false;
        			w.wantToBreak=false;
        			restPanel.addWaiterToList(w);
        			System.out.println("waiter off break");
        			return;
        		}
 
        		w.msgButtonAskBreak();
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerRole c) {
        if (currentPerson instanceof CustomerRole) {
            CustomerRole cust = (CustomerRole) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
