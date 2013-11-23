
package housing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import housing.OccupantRole;
import housing.LandlordRole;

import agent.Agent;



public class HomeGui extends JFrame implements ActionListener{

	
	
AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private HomePanel homePanel = new HomePanel(this);
    private Agent agent = null;
    private JPanel ProjectPanel = new JPanel();
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
   
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public HomeGui() {
        int WINDOWX = 550;
        int WINDOWY = 350;

       // animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
       // animationFrame.setVisible(true);
    	//animationFrame.add(animationPanel); 
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);
        setLayout(new BorderLayout());

        //setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .3));
        homePanel.setPreferredSize(restDim);
        homePanel.setMinimumSize(restDim);
        homePanel.setMaximumSize(restDim);
        add(homePanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .2));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(true);
        stateCB.addActionListener(this);
        
 

        infoPanel.setLayout(new GridLayout());
        
       infoLabel = new JLabel(); 
       infoLabel.setText("<html><pre><i>Click to cook</i></pre></html>");
       infoPanel.add(infoLabel);
       infoPanel.add(stateCB);
        add(infoPanel);
        
               
        
        ProjectPanel.setVisible(true);
        			// ProjectPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        			// ProjectPanel.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        
        Dimension projectDim = new Dimension((int)(WINDOWX * .45), WINDOWY);
        
        ProjectPanel.setPreferredSize(projectDim);
        ProjectPanel.setMinimumSize(projectDim);
        ProjectPanel.setMaximumSize(projectDim);
        ProjectPanel.setLayout(new BorderLayout());
       
        
        ProjectPanel.add(homePanel, BorderLayout.CENTER);
        ProjectPanel.add(infoPanel, BorderLayout.SOUTH);
        	
        
        Dimension animDim = new Dimension((int)(WINDOWX * .55), WINDOWY);
        
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

        if (person instanceof OccupantRole) {
            OccupantRole customer = (OccupantRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre> Name: " + customer.getName() + " </pre></html>");
        }
        infoPanel.validate();
     
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
            if (currentPerson instanceof OccupantRole) 
            {
                OccupantRole c = (OccupantRole) currentPerson;
                c.getGui().setHungry();
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
    public void setCustomerEnabled(OccupantRole c) 
    {
        if (currentPerson instanceof OccupantRole) {
            OccupantRole occ = (OccupantRole) currentPerson;
            if (c.equals(occ)) {
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
        HomeGui gui = new HomeGui();
        gui.setTitle("Person's Home");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	

}
