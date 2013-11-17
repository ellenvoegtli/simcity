package mainCity.restaurants.enaRestaurant.gui;

import mainCity.restaurants.enaRestaurant.CustomerRole;
import mainCity.restaurants.enaRestaurant.WaiterRole;
import javax.swing.*;

import agent.Agent;

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
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private Agent agent = null;
    private JPanel ProjectPanel = new JPanel();
    private boolean paused = false;
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
   // private JPanel personalPanel;
   // private JLabel personalLabel;
    //private ImageIcon(restaurant_trokic, th.jpeg)
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JButton pauseB; //part of info label

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 1200;
        int WINDOWY = 750;

       // animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
       // animationFrame.setVisible(true);
    	//animationFrame.add(animationPanel); 
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);
        setLayout(new BorderLayout());

        //setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .3));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .2));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        pauseB = new JButton("Pause/Restart");
        pauseB.setVisible(true);
        pauseB.addActionListener(this);

        infoPanel.setLayout(new GridLayout());
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        infoPanel.add(pauseB);
        add(infoPanel);
        
        
        //Adding personal info to the information panel
       
        
        ProjectPanel.setVisible(true);
        			// ProjectPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        			// ProjectPanel.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        
        Dimension projectDim = new Dimension((int)(WINDOWX * .5), WINDOWY);
        
        ProjectPanel.setPreferredSize(projectDim);
        ProjectPanel.setMinimumSize(projectDim);
        ProjectPanel.setMaximumSize(projectDim);
        ProjectPanel.setLayout(new BorderLayout());
       
        
        ProjectPanel.add(restPanel, BorderLayout.CENTER);
        ProjectPanel.add(infoPanel, BorderLayout.SOUTH);
        	
        
        Dimension animDim = new Dimension((int)(WINDOWX * .5), WINDOWY);
        
        animationPanel.setPreferredSize(animDim);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        //animationPanel.setLayout(new BorderLayout());
        
       
       add(ProjectPanel, BorderLayout.WEST);
       add(animationPanel, BorderLayout.EAST);
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

        if (person instanceof CustomerRole) {
            CustomerRole customer = (CustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Customer Name: " + customer.getName() + " </pre></html>");
        }
        infoPanel.validate();
        
        if(person instanceof WaiterRole)
        {
        	WaiterRole waiter = (WaiterRole) person;
        	stateCB.setText("Want Break");
        	stateCB.setSelected(waiter.getGui().onBreak());
        	stateCB.setEnabled(true);
            infoLabel.setText(
                    "<html><pre>     Waiter Name: " + waiter.getName() + " </pre></html>");
        	
        }
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == stateCB) 
        {
            if (currentPerson instanceof CustomerRole) 
            {
                CustomerRole c = (CustomerRole) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
            if(currentPerson instanceof WaiterRole)
            {
            	WaiterRole w = (WaiterRole) currentPerson;
            	System.out.println(w.getName());
            	w.getGui().setBreak();
            	stateCB.setEnabled(false);
            }
        }
        //////////////////////////////////////////clicking of the pause button to pause the agents
        if(e.getSource() == pauseB)
        {      
        	System.out.println("pause button");

        	 if(paused)
        	{
        		paused = false;
        		System.out.println("pause button restarting call");
        		restPanel.restartAll();
        	}
        	else
        	{
        		paused = true;
        		System.out.println("pause button clicked to pause gui");
        		restPanel.pauseAll();
        	}
        	
        }
        
        
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerRole c) 
    {
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
    public static void main(String[] args)
    {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
