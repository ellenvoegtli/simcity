package housing.gui;


import housing.LandlordRole;
import housing.OccupantRole;




import javax.swing.*;

import housing.gui.HomePanel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;


public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    public JScrollPane paneL =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel viewLandLords = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private List<JButton> listL = new ArrayList<JButton>();
    private List<JCheckBox> hungryBoxes = new ArrayList<JCheckBox>();
    private List<JCheckBox> breakBoxes = new ArrayList<JCheckBox>();
    private JButton addPersonB = new JButton("Add");
    private JCheckBox checkHunger = new JCheckBox("Hungry?");
    private JCheckBox desireBreak = new JCheckBox("Break?");
    private JButton addWaiterB = new JButton("Add");
    private JTextField waiterNames = new JTextField();
    private HomePanel homePanel;
    private String type;
    private JTextField personNames = new JTextField();
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(HomePanel hp, String type) {
        homePanel = hp;
        this.type = type;
        
        
        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        Dimension textSize = new Dimension(400,
                (int) (30));

       
        	addPersonB.addActionListener(this);
            checkHunger.addActionListener(this);
            
             add(personNames);
             if(type == "LandLord")
             {
            	add(desireBreak); 
             }
             if(type == "Occupant")
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
			homePanel.createHunger(personNames.getText());

    	}
        if (e.getSource() == addPersonB) 
        {
        	addPerson(personNames.getText());
        	
        	 if(type == "Occupant")
        	 {
        		 if(checkHunger.isSelected())
        		 {
        			 homePanel.createHunger(personNames.getText());
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
                    homePanel.showInfo(type, temp.getText());
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
           
           
            //homePanel.addPerson(type, name);//puts customer on list
            homePanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}

