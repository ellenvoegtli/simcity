package mainCity.restaurants.restaurant_zhangdt.gui;

import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	//JInternalFrame animationFrame = new JInternalFrame("Restaurant Animation");
	private AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JCheckBox breakCB; 
    
    /* personalPanel holds information about myself */ 
    private JPanel personalPanel; 
    private JLabel personalLabel; 
    private JLabel picLabel; 
    private ImageIcon picture; 

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 1000;
        int WINDOWY = 350;

        //animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        //animationFrame.setVisible(true);
    	//animationFrame.add(animationPanel); 
    	
    	setBounds(50, 50, WINDOWX, WINDOWY+150);

        //setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS)); 
    	setLayout(new BorderLayout());
        //setLayout(new GridLayout(2, 3, 20, 20)); 	
        
        Dimension animationDim = new Dimension((int) (WINDOWX * .5), (int) (WINDOWY * .70));
        getAnimationPanel().setPreferredSize(animationDim);
        getAnimationPanel().setMinimumSize(animationDim);
        getAnimationPanel().setMaximumSize(animationDim);
        getAnimationPanel().setBorder(BorderFactory.createEtchedBorder());
        add(getAnimationPanel(), BorderLayout.CENTER);

        Dimension restDim = new Dimension((int) (WINDOWX* .5), (int) (WINDOWY * .70));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel, BorderLayout.WEST);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .12));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(true);
        stateCB.addActionListener(this);
        
        breakCB = new JCheckBox(); 
        breakCB.setVisible(true); 
        breakCB.addActionListener(this);
        

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        infoPanel.add(breakCB);
        add(infoPanel, BorderLayout.SOUTH); 
        
        // New Panel with Personal Information 
        Dimension personalDim = new Dimension(WINDOWX, (int) (WINDOWY * .18)); 
        personalPanel = new JPanel(); 
        personalPanel.setPreferredSize(personalDim); 
        personalPanel.setMinimumSize(personalDim);
        personalPanel.setMaximumSize(personalDim); 
        
        personalPanel.setLayout(new GridLayout(1, 2, 5, 5));
        
        personalLabel = new JLabel("  Hi, My Name is David Zhang, Welcome to My Restaurant!");
        personalPanel.add(personalLabel); 
        
        picture = new ImageIcon("C:/Users/David Zhang/Desktop/ThePhoneBill.jpg"); 
        picLabel = new JLabel("", picture, JLabel.CENTER);
        personalPanel.add(picLabel); 
        
        add(personalPanel, BorderLayout.NORTH);
        
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        //stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof DavidCustomerRole) {
        	stateCB.setVisible(true);
        	breakCB.setVisible(false);
            DavidCustomerRole customer = (DavidCustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        
        if (person instanceof DavidWaiterRole) {
        	stateCB.setVisible(false);
        	breakCB.setVisible(true);
        	DavidWaiterRole waiter = (DavidWaiterRole) person; 
        	breakCB.setText("Break?"); 
        	
        	breakCB.setSelected(waiter.isOnBreak());
        	breakCB.setEnabled(waiter.isRequestingBreak());
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
            if (currentPerson instanceof DavidCustomerRole) {
                DavidCustomerRole c = (DavidCustomerRole) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        
        if (e.getSource() == breakCB) {
        	if (currentPerson instanceof DavidWaiterRole) { 
        		DavidWaiterRole w = (DavidWaiterRole) currentPerson;
        		if (breakCB.isSelected()){
	        		w.msgAskForBreak(); 
        		}
        		else if (!breakCB.isSelected()) {
            		w.msgOffBreak();
            	}
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(DavidCustomerRole c) {
        if (currentPerson instanceof DavidCustomerRole) {
            DavidCustomerRole cust = (DavidCustomerRole) currentPerson;
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
	public AnimationPanel getAnimationPanel() {
		return animationPanel;
	}
	public void setAnimationPanel(AnimationPanel animationPanel) {
		this.animationPanel = animationPanel;
	}
	
	/* Utilities */ 
	public void deselectBreakCB() { 
		breakCB.setSelected(false);
	}
}
