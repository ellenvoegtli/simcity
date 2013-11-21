package mainCity.restaurants.EllenRestaurant.gui;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.market.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import mainCity.contactList.*;
/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel implements ActionListener{

    //Host and cook
    private EllenHostRole host = new EllenHostRole("Host");
    private EllenCashierRole cashier = new EllenCashierRole("Cashier");
    
    /*				steak	|	pizza	|	pasta	|	soup
     * Cook: 		8			8			8			8
     */
    private EllenCookRole cook = new EllenCookRole("Cook", 8, 8, 8, 0);
    private MarketGreeterRole marketGreeter;
    private ContactList contactList;
    
    private int NMARKETS = 3;
    
    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
        
    private Vector<EllenCustomerRole> customers = new Vector<EllenCustomerRole>();
    private Vector<EllenWaiterRole> waiters = new Vector<EllenWaiterRole>();
    private Vector<EllenMarketRole> markets = new Vector<EllenMarketRole>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();
    public JPanel pausePanel = new JPanel();
    JButton pauseBtn = new JButton("Pause");
    JButton unpauseBtn = new JButton("Unpause");
    private JPanel cookInventoryPanel = new JPanel();
    JButton soupBtn = new JButton("Deplete soup");
    JButton pizzaBtn = new JButton("Deplete pizza");
    //private HostGui hostGui = new HostGui(host);

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        //host.setGui(hostGui);
        
        
        //for MORE markets
        /*				steak	|	pizza	|	pasta	|	soup
         * Market 1: 	20			15			10			5
         * Market 2:	20			15			10			5
         * Market 3:	20			15			10			5
         */

        for (int i=1; i<=NMARKETS; i++){
        	System.out.println("Creating market");
        	EllenMarketRole m = new EllenMarketRole(("Market " + i), 20, 15, 10, 5);
        	m.setCook(cook);
        	m.setCashier(cashier);
        	cook.addMarket(m);
        	m.startThread();
        	markets.add(m);
        }
        
        cook.setMenu(new EllenMenu());
        host.startThread();
        cashier.startThread();
        cook.startThread();
        
        
        //*****
        System.out.println("CONTACT LIST = " + contactList);
        //contactList.addCook(cook);
        //contactList.setEllenHost(host);
        //*****

        KitchenGui kitchenGui = new KitchenGui(gui);
        cook.setKitchenGui(kitchenGui);
        gui.animationPanel.addGui(kitchenGui);
        
        
        setLayout(new GridLayout(1, 2, 0, 0));
        group.setLayout(new GridLayout(1, 3, 0, 0));
        
        waiterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        group.add(waiterPanel);
        group.add(customerPanel);
        

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
    
    public EllenCookRole getCook(){
    	return cook;
    }
    public void setContactList(ContactList c){
    	contactList = c;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseBtn){
        	System.out.println("PAUSE BUTTON PRESSED.");
        	//baseAgent.pause();
        	host.pause();
        	cook.pause();
        	for (EllenCustomerRole cust : customers){
        		cust.pause();
        	}
        	for (EllenWaiterRole waiter : waiters){
        		waiter.pause();
        	}
        	for (EllenMarketRole m : markets){
        		m.pause();
        	}
        }
        else if (e.getSource() == unpauseBtn){
        	System.out.println("RESTART BUTTON PRESSED.");
        	//baseAgent.restart();
        	host.restart();
        	cook.restart();
        	for (EllenCustomerRole cust : customers){
        		cust.restart();
        	}
        	for (EllenWaiterRole waiter : waiters){
        		waiter.restart();
        	}
        	for (EllenMarketRole m : markets){
        		m.restart();
        	}
        }
        else if (e.getSource() == soupBtn){
        	System.out.println("DEPLETE SOUP BUTTON PRESSED.");
        	cook.depleteInventory("soup");
        }
        else if (e.getSource() == pizzaBtn){
        	System.out.println("DEPLETE PIZZA BUTTON PRESSED.");
        	cook.depleteInventory("pizza");
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
                + "<tr><td>cook:</td><td>" + cook.getName() + "</td></tr></table>"
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
                EllenCustomerRole temp = customers.get(i);
                if (temp.getName() == name) {
                    gui.updateInfoPanel(temp);
                }
            }
        }
        else if (type.equals("Waiters")){
        	for (int i = 0; i < waiters.size(); i++) {
                EllenWaiterRole temp = waiters.get(i);
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
    		EllenCustomerRole c = new EllenCustomerRole(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);

    		c.setCashier(cashier);
    		c.setGui(g);
    		customers.add(c);
    		
    		//set waitingArea positions based on position in customers list
    		//makes it able to handle endless # of rows of 5
    		int i = 0;
    		for (EllenCustomerRole cust : customers){
    			if (cust.equals(c)){
    				g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			}
    			else
    				i++;
    		}
    		
    		
    		c.startThread();

    		if (isChecked){
    			c.getGui().setHungry(); 
    		}
    	}
    	else if (type.equals("Waiters")){
    		EllenWaiterRole w = new EllenWaiterRole(name);
    		WaiterGui g = new WaiterGui(w, gui);
    		
    		gui.animationPanel.addGui(g);
    		w.setHost(host);
    		w.setCook(cook);
    		w.setCashier(cashier);
    		host.addWaiter(w);
    		w.setGui(g);
    		waiters.add(w);
    		
    		//set the home positions based on position in waiter list
    		int i = 0;
    		for (EllenWaiterRole wait : waiters){
    			if (wait.equals(w)){
    				g.setHomePosition((WINDOWX + i*70)/3, 30);
    			}
    			else
    				i++;
    		}
    		
    		w.startThread();
    		
    		if (isChecked){
    			w.getGui().IWantBreak();
    		}
    		else {
    			w.getGui().GoOffBreak();
    		}
    	}
    }

}
