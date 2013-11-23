package mainCity.restaurants.marcusRestaurant.gui;

import javax.swing.*;

import role.marcusRestaurant.MarcusCustomerRole;
import role.marcusRestaurant.MarcusWaiterRole;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class MarcusRestaurantGui extends JFrame implements ActionListener {
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
    private MarcusRestaurantPanel restPanel = new MarcusRestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel

    private JPanel namePanel;
    private JLabel nameLabel; //part of namePanel
    
    private JButton pauseButton;
    private boolean paused;
    
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public MarcusRestaurantGui() {
        int WINDOWX = 1100;
        int WINDOWY = 600;
        paused = false;
        //animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        //animationFrame.setVisible(true);
    	//animationFrame.add(animationPanel); 
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);

        //setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS));
    	setLayout(new BorderLayout(10, 5));
    	
        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .6));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel, BorderLayout.CENTER);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, 80);
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click on a customer or waiter</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        add(infoPanel, BorderLayout.SOUTH);

     // Setup the name panel
        Dimension nameDim = new Dimension(WINDOWX, 80);
        namePanel = new JPanel();
        namePanel.setPreferredSize(nameDim);
        namePanel.setMinimumSize(nameDim);
        namePanel.setMaximumSize(nameDim);
        namePanel.setBorder(BorderFactory.createTitledBorder("User"));
        namePanel.setLayout(new GridBagLayout());
        
        nameLabel = new JLabel(); 
        nameLabel.setText("<html>Marcus Eng</html>");
        namePanel.add(nameLabel);
        
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this);
        namePanel.add(pauseButton);
        add(namePanel, BorderLayout.NORTH);
        
        Dimension animDim = new Dimension(500, 600);
        animationPanel.setPreferredSize(animDim);
        //animationPanel.setBounds(0,0,600,700);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        add(animationPanel, BorderLayout.EAST);
        repaint();
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof MarcusCustomerRole) {
            MarcusCustomerRole customer = (MarcusCustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }

        if (person instanceof MarcusWaiterRole) {
            MarcusWaiterRole waiter = (MarcusWaiterRole) person;
            stateCB.setText("Go on Break?");
            stateCB.setSelected(waiter.checkOnBreak());
            stateCB.setEnabled(!waiter.checkOnBreak());
            infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        }
        
        infoPanel.validate();
    }
    
    public AnimationPanel getAnimationPanel() {
    	return animationPanel;
    }
    
    public MarcusRestaurantPanel getMarcusRestaurantPanel() {
    	return restPanel;
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof MarcusCustomerRole) {
                MarcusCustomerRole c = (MarcusCustomerRole) currentPerson;
                c.getGui().goInside();
                stateCB.setEnabled(false);
            }
            
            if(currentPerson instanceof MarcusWaiterRole) {
            	MarcusWaiterRole w = (MarcusWaiterRole) currentPerson;
            	w.msgRequestBreak();
            	stateCB.setEnabled(false);
            }
        }
        
        if (e.getSource() == pauseButton) {
        	if(!paused) {
        		pauseButton.setText("Resume");
        		restPanel.callPause();
        		paused = true;
        	}
        	else {
        		pauseButton.setText("Pause");
        		restPanel.callResume();
        		paused = false;
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(MarcusCustomerRole c) {
        if (currentPerson instanceof MarcusCustomerRole) {
            MarcusCustomerRole cust = (MarcusCustomerRole) currentPerson;
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
    	MarcusRestaurantGui gui = new MarcusRestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
