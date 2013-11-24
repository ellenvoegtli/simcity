package mainCity.restaurants.EllenRestaurant.gui;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.contactList.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class EllenRestaurantGui extends JFrame implements ActionListener {
	ContactList contactList;
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    

    private EllenRestaurantPanel restPanel = new EllenRestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoPanel
    private JButton breakB;		//part of infoPanel

    JTextField enterCustomerField;
    JButton newAddButton = new JButton("Add");


    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public EllenRestaurantGui() {
    	
        int WINDOWX = 550;
        int WINDOWY = 350;

        
        /*
        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX, WINDOWY+100);
        animationFrame.setVisible(true);
    	animationFrame.add(animationPanel); 
    	*/
        
        setBounds(50, 50, WINDOWX*2, (int) (WINDOWY*2));

        setLayout(new BoxLayout((Container) getContentPane(), 
        		BoxLayout.X_AXIS));
        
        

    	JPanel nonAnimPanel = new JPanel();
    	nonAnimPanel.setLayout(new GridLayout(3, 2, 0, 0));
    	Dimension groupDim = new Dimension(WINDOWX, WINDOWY*2);
        nonAnimPanel.setPreferredSize(groupDim);
        nonAnimPanel.setMinimumSize(groupDim);
        nonAnimPanel.setMaximumSize(groupDim);
        
        
        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .25));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        nonAnimPanel.add(restPanel);
        

        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .25));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        //customer
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        //waiter
        breakB = new JButton();
        breakB.setText("Want break?");
        breakB.setVisible(false);
        breakB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><i>Click Add to make customers or waiters</i></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        infoPanel.add(breakB);
        nonAnimPanel.add(infoPanel);
        
        //from restPanel (better formatting)
        nonAnimPanel.add(restPanel.pausePanel);
        
        
        add(nonAnimPanel);
        
        //ANIMATION PANEL
        //animationPanel.setBounds(0, WINDOWY + 50 , WINDOWX, WINDOWY);
        Dimension animDim = new Dimension(WINDOWX, WINDOWY);
        animationPanel.setPreferredSize(animDim);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        animationPanel.setVisible(true);
        animationPanel.setBorder(BorderFactory.createTitledBorder("Restaurant Animation"));

        
        add(animationPanel);
        
    }
    
    public EllenRestaurantPanel getEllenRestaurantPanel(){
    	return restPanel;
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */

    public void updateInfoPanel(Object person) {
        currentPerson = person;

        if (person instanceof EllenCustomerRole) {
        	breakB.setVisible(false);
        	stateCB.setVisible(true);
            EllenCustomerRole customer = (EllenCustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        else if (person instanceof EllenWaiterRole){
        	stateCB.setVisible(false);
        	breakB.setVisible(true);
        	EllenWaiterRole waiter = (EllenWaiterRole) person;

        	if (waiter.getGui().onBreak()){
        		breakB.setText("Come back");
        		breakB.setEnabled(true);
        	}
        	else if (waiter.getGui().wantsBreak()){
        		breakB.setText("Want break");
        		breakB.setEnabled(false);
        	}
        	else if (waiter.getGui().offBreak()){
        		breakB.setText("Want break");
        		breakB.setEnabled(true);
        	}
          // Hack. Should ask customerGui
        	infoLabel.setText(
        		"<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        }
        
        infoPanel.validate();
    }

    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
    	/*
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof EllenCustomerRole) {
                EllenCustomerRole c = (EllenCustomerRole) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        else if (e.getSource() == breakB){
        	if (currentPerson instanceof EllenWaiterRole) {
                EllenWaiterRole w = (EllenWaiterRole) currentPerson;
                if (!w.getGui().onBreak()){
	                w.getGui().IWantBreak();
	                breakB.setEnabled(false);
                }
                else if (w.getGui().onBreak()){
                	w.getGui().GoOffBreak();
                	breakB.setText("Want break");
	                breakB.setEnabled(true);
                }
                updateInfoPanel(currentPerson);
            }
        }*/
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(EllenCustomerRole c) {
        if (currentPerson instanceof EllenCustomerRole) {
            EllenCustomerRole cust = (EllenCustomerRole) currentPerson;
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
        EllenRestaurantGui gui = new EllenRestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
