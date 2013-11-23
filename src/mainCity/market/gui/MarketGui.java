package mainCity.market.gui;

import mainCity.market.MarketCustomerRole;
import mainCity.market.MarketEmployeeRole;
import mainCity.contactList.*;
import mainCity.gui.trace.AlertLevel;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.gui.trace.TracePanel;

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
	TracePanel tracePanel;
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
    
    private JPanel controlPanel = new JPanel();
    private JButton marketCustTagButton;
    private JButton marketEmpTagButton;
    private JButton messagesButton;
    
    private boolean showCustTags = true;
    private boolean showEmpTags = true;
    private boolean showMessages = true;



    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public MarketGui() {
        this.tracePanel = new TracePanel();
        tracePanel.setPreferredSize(new Dimension(800, 300));
        tracePanel.showAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel.showAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel.hideAlertsWithLevel(AlertLevel.DEBUG);
        //tracePanel.showAlertsWithTag(AlertTag.PERSON);
        tracePanel.showAlertsWithTag(AlertTag.MARKET_CUSTOMER);   	//as default, hide everything     
        tracePanel.showAlertsWithTag(AlertTag.MARKET_EMPLOYEE);        

        AlertLog.getInstance().addAlertListener(tracePanel);
    	

        int WINDOWX = 550;
        int WINDOWY = 350;
        
        setBounds(50, 50, WINDOWX*2, (int) (WINDOWY*2));
        //setLayout(new BoxLayout((Container) getContentPane(), 
        	//	BoxLayout.X_AXIS));
        setLayout(new GridLayout(2, 1, 0, 0));

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
       

        infoPanel.setLayout(new GridLayout(2, 1, 30, 0));
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><i>Click Add to make customers or waiters</i></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        
        nonAnimPanel.add(infoPanel);
        nonAnimPanel.add(tracePanel);
        add(nonAnimPanel);
       
        //ANIMATION PANEL
        animationPanel.setBounds(0, 0, WINDOWX, WINDOWY);
        Dimension animDim = new Dimension(WINDOWX, WINDOWY);
        animationPanel.setPreferredSize(animDim);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        animationPanel.setVisible(true);
        animationPanel.setBorder(BorderFactory.createTitledBorder("Restaurant Animation"));

        
        add(animationPanel);
        
        
     
        marketCustTagButton = new JButton("Hide tag: MARKET_CUSTOMER");
        marketEmpTagButton = new JButton("Hide tag: MARKET_EMPLOYEE");
        messagesButton = new JButton("Hide level: MESSAGE");
        
        marketCustTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (!showCustTags){
                    //============================ TUTORIAL ==========================================
                    //This is how you make messages with a certain Level (normal MESSAGE here) show up in the trace panel.
                    tracePanel.showAlertsWithTag(AlertTag.MARKET_CUSTOMER);
                    //================================================================================
                    marketCustTagButton.setText("Hide tag: MARKET_CUSTOMER");
                    showCustTags = true;
            	}
            	else{
            		tracePanel.hideAlertsWithTag(AlertTag.MARKET_CUSTOMER);
            		marketCustTagButton.setText("Show tag: MARKET_CUSTOMER");
            		showCustTags = false;
            	}
            }
	    });
        marketEmpTagButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	if (!showEmpTags){
	                    //============================ TUTORIAL ==========================================
	                    //This is how you make messages with a certain Level not show up in the trace panel.
	                    tracePanel.showAlertsWithTag(AlertTag.MARKET_EMPLOYEE);
	                    //================================================================================
	                    marketEmpTagButton.setText("Hide tag: MARKET_EMPLOYEE");
	                    showEmpTags = true;
	            	}
	            	else{
	            		tracePanel.hideAlertsWithTag(AlertTag.MARKET_EMPLOYEE);
	            		marketEmpTagButton.setText("Show tag: MARKET_EMPLOYEE");
	            		showEmpTags = false;
	            	}
	            }
	    });
        messagesButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	if (!showMessages){
	                    //============================ TUTORIAL ==========================================
	                    //This is how you make messages with a level of ERROR show up in the trace panel.
	                    tracePanel.showAlertsWithLevel(AlertLevel.MESSAGE);
	                    messagesButton.setText("Hide level: MESSAGES");
	                    showMessages = true;
	                    //================================================================================
	            	}
	            	else{
	            		tracePanel.hideAlertsWithLevel(AlertLevel.MESSAGE);
	            		messagesButton.setText("Show level: MESSAGES");
	            		showMessages = false;
	            	}
	            }
	    });
	   
        
	    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
	    controlPanel.add(marketCustTagButton);
	    controlPanel.add(marketEmpTagButton);
	    controlPanel.add(messagesButton);
	    controlPanel.setMinimumSize(new Dimension(50, 600));
        
        this.add(controlPanel);
        
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
