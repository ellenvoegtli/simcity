package mainCity.market.gui;

import mainCity.market.MarketCustomerRole;
import mainCity.market.MarketEmployeeRole;

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
public class MarketGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* marketPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    

    private MarketPanel marketPanel = new MarketPanel(this);
    
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
    public MarketGui() {

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
        marketPanel.setPreferredSize(restDim);
        marketPanel.setMinimumSize(restDim);
        marketPanel.setMaximumSize(restDim);
        nonAnimPanel.add(marketPanel);
        

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
        /*
        breakB = new JButton();
        breakB.setText("Want break?");
        breakB.setVisible(false);
        breakB.addActionListener(this);
        */

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><i>Click Add to make customers or waiters</i></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        //infoPanel.add(breakB);
        nonAnimPanel.add(infoPanel);
        
        //from marketPanel (better formatting)
        nonAnimPanel.add(marketPanel.pausePanel);
        
        
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
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */

    public void updateInfoPanel(Object person) {
        currentPerson = person;

        if (person instanceof MarketCustomerRole) {
        	//breakB.setVisible(false);
        	stateCB.setVisible(true);
            MarketCustomerRole customer = (MarketCustomerRole) person;
            stateCB.setText("Needs inventory?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().needsInventory());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().needsInventory());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        else if (person instanceof MarketEmployeeRole){
        	stateCB.setVisible(false);
        	//breakB.setVisible(true);
        	MarketEmployeeRole waiter = (MarketEmployeeRole) person;

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
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof MarketCustomerRole) {
                MarketCustomerRole c = (MarketCustomerRole) currentPerson;
                //c.getGui().setHungry();
                c.getGui().setNeedsInventory();
                stateCB.setEnabled(false);
            }
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(MarketCustomerRole c) {
        if (currentPerson instanceof MarketCustomerRole) {
            MarketCustomerRole cust = (MarketCustomerRole) currentPerson;
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
        MarketGui gui = new MarketGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
