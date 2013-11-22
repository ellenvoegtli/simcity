package mainCity.restaurants.enaRestaurant.gui;

import mainCity.restaurants.enaRestaurant.CustomerRole;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;

import javax.swing.*;
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
    public JScrollPane paneW =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel viewWaiters = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private List<JButton> listW = new ArrayList<JButton>();
    private List<JCheckBox> hungryBoxes = new ArrayList<JCheckBox>();
    private List<JCheckBox> breakBoxes = new ArrayList<JCheckBox>();
    private JButton addPersonB = new JButton("Add");
    private JCheckBox checkHunger = new JCheckBox("Hungry?");
    private JCheckBox desireBreak = new JCheckBox("Break?");
    private JButton addWaiterB = new JButton("Add");
    private JTextField waiterNames = new JTextField();
    private RestaurantPanel restPanel;
    private String type;
    private JTextField personNames = new JTextField();
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;
        
        
        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        Dimension textSize = new Dimension(400,
                (int) (30));

       
        	addPersonB.addActionListener(this);
            checkHunger.addActionListener(this);
            
             add(personNames);
             if(type == "Waiters")
             {
            	add(desireBreak); 
             }
             if(type == "Customers")
             {
            	 add(checkHunger);
             }
             
             add(addPersonB);
             
                   
             personNames.setPreferredSize(textSize);
             personNames.setMinimumSize(textSize);
             personNames.setMaximumSize(textSize);	
             personNames.setText("Enter Name");
    
        
             view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
             pane.setViewportView(view);
             add(pane);
  
       
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e)
    {
    	if(e.getSource() == personNames)
    	{
    		checkHunger.setEnabled(true);
    	}
    	
    	if(e.getSource() == checkHunger)
    	{
			restPanel.createHunger(personNames.getText());

    	}
        if (e.getSource() == addPersonB) 
        {
        	addPerson(personNames.getText());
        	
        	 if(type == "Customers")
        	 {
        		 if(checkHunger.isSelected())
        		 {
        			 restPanel.createHunger(personNames.getText());
        			 checkHunger.setEnabled(false);
        		 }
        	 }
        	 personNames.setText(""); 
        	 checkHunger.setEnabled(true);
        }
        
    
        else 
        {
        	
        	for (JButton temp:list)
        	{
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        	
       /* 	for(int i=0; i<hungryBoxes.size(); i++)
        	{
        		JCheckBox temp = hungryBoxes.get(i);
        		if(e.getSource() == temp)
        		{
        			restPanel.createHunger(list.get(i).getText());
        			temp.setEnabled(false);
        		}
        	}
        	for(int j=0; j<breakBoxes.size(); j++)
        	{
        		JCheckBox tempW = breakBoxes.get(j);
        		if(e.getSource() == tempW)
        		{
        			restPanel.wantBreak(listW.get(j).getText());
        			tempW.setEnabled(false);
        		}
        	}
        	*/
        			
       }
 }
        
    

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    

    public void addPerson(String name)
{
        if (name != null)
        {
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
           
           
            restPanel.addPerson(type, name);//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}
