package mainCity.market.gui;

import mainCity.market.*;
import mainCity.restaurants.EllenRestaurant.gui.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel implements ActionListener{

    //Host and cook
    private MarketGreeterRole host = new MarketGreeterRole("Host");
    private MarketCashierRole cashier = new MarketCashierRole("Cashier");
    
    
    private int NMARKETS = 3;
    
    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
        
    private Vector<MarketCustomerRole> customers = new Vector<MarketCustomerRole>();
    private Vector<MarketEmployeeRole> employees = new Vector<MarketEmployeeRole>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel employeePanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();
    public JPanel pausePanel = new JPanel();
    JButton pauseBtn = new JButton("Pause");
    JButton unpauseBtn = new JButton("Unpause");
    private JPanel cookInventoryPanel = new JPanel();
    JButton soupBtn = new JButton("Deplete soup");
    JButton pizzaBtn = new JButton("Deplete pizza");
    //private HostGui hostGui = new HostGui(host);

    private MarketGui gui; //reference to main gui

    public MarketPanel(MarketGui gui) {
        this.gui = gui;
        //host.setGui(hostGui);

        host.startThread();
        cashier.startThread();
        
        
        setLayout(new GridLayout(1, 2, 0, 0));
        group.setLayout(new GridLayout(1, 3, 0, 0));
        
        employeePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        //group.add(waiterPanel);
        //group.add(customerPanel);
        

        initRestLabel();
        add(restLabel);
        add(group);
        
        
        pausePanel.setLayout(new GridLayout(2, 1, 5, 5));
        //group together in a bigger panel
        pauseBtn.addActionListener(this);
        pausePanel.add(pauseBtn);
        unpauseBtn.addActionListener(this);
        pausePanel.add(unpauseBtn);
        
        
        
        cookInventoryPanel.setLayout(new GridLayout(1, 2, 5, 5));
        soupBtn.addActionListener(this);
        pizzaBtn.addActionListener(this);
        cookInventoryPanel.add(soupBtn);
        cookInventoryPanel.add(pizzaBtn);
        pausePanel.add(cookInventoryPanel);
        
        add(pausePanel);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseBtn){
        	System.out.println("PAUSE BUTTON PRESSED.");
        	host.pause();
        	for (MarketCustomerRole cust : customers){
        		cust.pause();
        	}
        	for (MarketEmployeeRole employee : employees){
        		employee.pause();
        	}
        }
        else if (e.getSource() == unpauseBtn){
        	System.out.println("RESTART BUTTON PRESSED.");
          	host.restart();
        	for (MarketCustomerRole cust : customers){
        		cust.restart();
        	}
        	for (MarketEmployeeRole employee : employees){
        		employee.pause();
        	}
        }
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table>"
                + "<tr><td>host:</td><td>" + host.getName() + "</td></tr></table>"
                + "<h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Pizza</td><td>$10.99</td></tr><tr><td>Pasta</td><td>$5.99</td></tr><tr><td>Soup</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        //restLabel.add(new JLabel("               "), BorderLayout.EAST);
        //restLabel.add(new JLabel("               "), BorderLayout.WEST);
        restLabel.add(new JLabel("            "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
            	MarketCustomerRole temp = customers.get(i);
                if (temp.getName() == name) {
                    gui.updateInfoPanel(temp);
                }
            }
        }
        else if (type.equals("Employees")){
        	for (int i = 0; i < employees.size(); i++) {
        		MarketEmployeeRole temp = employees.get(i);
                if (temp.getName() == name) {
                    gui.updateInfoPanel(temp);
                }
            }
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean isChecked) {

    	if (type.equals("Customers")) {
    		MarketCustomerRole c = new MarketCustomerRole(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);

    		c.setCashier(cashier);
    		c.setGui(g);
    		customers.add(c);
    		
    		//set waitingArea positions based on position in customers list
    		//makes it able to handle endless # of rows of 5
    		int i = 0;
    		for (MarketCustomerRole cust : customers){
    			if (cust.equals(c)){
    				g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			}
    			else
    				i++;
    		}
    		
    		
    		c.startThread();

    		if (isChecked){
    			c.getGui().setNeedsInventory(); 
    		}
    	}
    	else if (type.equals("Employees")){
    		MarketEmployeeRole e = new MarketEmployeeRole(name);
    		EmployeeGui g = new EmployeeGui(e, gui);
    		
    		int i = 0;
    		int x = 0, y = 0;
    		for (MarketEmployeeRole em : employees){
    			if (em.equals(e)){
    				g.setHomePosition(200, 100);
    				x = (WINDOWX + i*70)/3;
    				y = 30;
    			}
    			else
    				i++;
    		}
    		
    		gui.animationPanel.addGui(g);
    		e.setHost(host);
    		e.setCashier(cashier);
    		host.addEmployee(e, x, y);
    		e.setGui(g);
    		employees.add(e);
    		
    		//set the home positions based on position in waiter list
    		/*
    		int i = 0;
    		for (WaiterAgent wait : waiters){
    			if (wait.equals(w)){
    				g.setHomePosition((WINDOWX + i*70)/3, 30);
    			}
    			else
    				i++;
    		}
    		*/
    		e.startThread();
    		
    		/*
    		if (isChecked){
    			e.getGui().IWantBreak();
    		}
    		else {
    			w.getGui().GoOffBreak();
    		}
    		*/
    	}
    }

}
