package mainCity.market.gui;

import mainCity.restaurants.EllenRestaurant.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */

public class ListPanel extends JPanel implements ActionListener, KeyListener {


    public JScrollPane customerPane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel customerView = new JPanel();
    private List<JButton> customerList = new ArrayList<JButton>();
    
    public JScrollPane waiterPane = 
    		new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel waiterView = new JPanel();
    private List<JButton> waiterList = new ArrayList<JButton>();

    private JButton addCustB = new JButton("Add");
    private JButton addWaiterB = new JButton("Add");
   
    private MarketPanel restPanel;

    private String type;
    JTextField enterCustomerField;
    JTextField enterWaiterField;
    private JCheckBox hungryB;
    private JCheckBox onBreakB;
    private JCheckBox offBreakB;


    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(MarketPanel rp, String type) {

        restPanel = rp;
        this.type = type;
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
 
	    //***WAITERS****
        if (type == "Waiters"){
	        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        	//setLayout(new FlowLayout());
        	
	        add(new JLabel("<html><u>" + type + "</u><br></html>"));
	        
	
	        waiterView.setLayout(new BoxLayout((Container) waiterView, BoxLayout.Y_AXIS));
	        waiterPane.setViewportView(waiterView);
	        add(waiterPane);
	        
	        add(new JLabel("Add name:"));
	        enterWaiterField = new JTextField(100);
	        Dimension waiterDim = new Dimension(250, 30);
	        enterWaiterField.setPreferredSize(waiterDim);
	        enterWaiterField.setMinimumSize(waiterDim);
	        enterWaiterField.setMaximumSize(waiterDim);
	        enterWaiterField.addKeyListener(this);
	        enterWaiterField.setVisible(true);
	        enterWaiterField.setAlignmentX(Component.LEFT_ALIGNMENT);
	        add(enterWaiterField);
	        
	        onBreakB = new JCheckBox();
	        onBreakB.addActionListener(this);
	        onBreakB.setVisible(true);
	        onBreakB.setText("Want break?");
	        onBreakB.setEnabled(false);
	        onBreakB.setAlignmentX(Component.LEFT_ALIGNMENT);
	        add(onBreakB);

	        addWaiterB.addActionListener(this);
	        addWaiterB.setAlignmentX(Component.LEFT_ALIGNMENT);
	        add(addWaiterB);
        }
        
        //***CUSTOMERS****
        else if (type == "Customers"){
	        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        	//setLayout(new FlowLayout(FlowLayout.LEFT));
	        JLabel custTitle = new JLabel("<html><u>" + type + "</u><br></html>");
	        custTitle.setAlignmentX(LEFT_ALIGNMENT);
	        add(custTitle);
	        
	
	        customerView.setLayout(new BoxLayout((Container) customerView, BoxLayout.Y_AXIS));
	        customerPane.setViewportView(customerView);
	        add(customerPane);
	        
	        
	        //JPanel enterCustPanel = new JPanel();
	        //enterCustPanel.setLayout(new GridLayout(3, 1, 0, 0));
	        add(new JLabel("Add name:"));
	        enterCustomerField = new JTextField(100);
	        Dimension custDim = new Dimension(250, 30);
	        enterCustomerField.setPreferredSize(custDim);
	        enterCustomerField.setMinimumSize(custDim);
	        enterCustomerField.setMaximumSize(custDim);
	        enterCustomerField.setVisible(true);
	        enterCustomerField.addKeyListener(this);
	        enterCustomerField.setAlignmentX(Component.LEFT_ALIGNMENT);
	        add(enterCustomerField);
	
	        hungryB = new JCheckBox();
	        hungryB.addActionListener(this);
	        hungryB.setVisible(true);
	        hungryB.setText("Hungry?");
	        hungryB.setEnabled(false);
	        hungryB.setAlignmentX(Component.LEFT_ALIGNMENT);
	        add(hungryB);
	        
	        addCustB.addActionListener(this);
	        addCustB.setAlignmentX(Component.LEFT_ALIGNMENT);
	        add(addCustB);
        }
        
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */

    
    public Object getObject(Object person){
    	return person;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addCustB) {      	
            addPerson(enterCustomerField.getText(), hungryB.isSelected());
            enterCustomerField.setText(null);
            restPanel.showInfo(type, enterCustomerField.getText());
            hungryB.setEnabled(false);
            //hungryB.setSelected(true);
        }
        else if (e.getSource() == hungryB){
        		hungryB.setSelected(true);
        }
        else if (e.getSource() == onBreakB){
        		onBreakB.setSelected(true);
        }
        else if (e.getSource() == addWaiterB){
        	addWaiter(enterWaiterField.getText(), onBreakB.isSelected());
        	enterWaiterField.setText(null);
        	restPanel.showInfo(type, enterWaiterField.getText());
        	onBreakB.setEnabled(false);
        }

        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/


        	for (JButton temp:customerList){
                if (e.getSource() == temp){
                    restPanel.showInfo(type, temp.getText());
                }
            }
        	for (JButton temp:waiterList){
        		if (e.getSource() == temp){
        			restPanel.showInfo(type, temp.getText());
        		}
        	}//end of second for loop
        
        }
    
    }


    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */

    public void addPerson(String name, boolean isChecked) {

        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = customerPane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            customerList.add(button);
            customerView.add(button);

            
            restPanel.addPerson(type, name, isChecked);//puts customer on list            
            restPanel.showInfo(type, name);//puts hungry button on panel
            
            hungryB.setEnabled(false);
            
            validate();
        }
    }
    public void addWaiter(String name, boolean isChecked) {

        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = waiterPane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            waiterList.add(button);
            waiterView.add(button);

            
            restPanel.addPerson(type, name, isChecked);//puts customer on list            
            restPanel.showInfo(type, name);//puts hungry button on panel
            
            onBreakB.setEnabled(false);
            
            validate();
        }
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == enterCustomerField){
			hungryB.setEnabled(true);
			hungryB.setSelected(false);
		}
		else if (e.getSource() == enterWaiterField){
			onBreakB.setEnabled(true);
			onBreakB.setSelected(false);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
