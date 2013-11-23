package mainCity.restaurants.EllenRestaurant.gui;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.market.*;
import mainCity.restaurants.EllenRestaurant.sharedData.*;
import mainCity.restaurants.marcusRestaurant.gui.CookGui;
import role.Role;
import role.marcusRestaurant.MarcusCashierRole;
import role.marcusRestaurant.MarcusCookRole;
import role.marcusRestaurant.MarcusCustomerRole;
import role.marcusRestaurant.MarcusHostRole;
import role.marcusRestaurant.MarcusWaiterRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mainCity.contactList.*;
/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class EllenRestaurantPanel extends JPanel implements ActionListener{
    //Host and cook
    //private EllenHostRole host = new EllenHostRole("EllenRestaurant Host");
    //private EllenCashierRole cashier = new EllenCashierRole("EllenRestaurant Cashier");
    
    /*				steak	|	pizza	|	pasta	|	soup
     * Cook: 		8			8			8			8
     */
    //private EllenCookRole cook = new EllenCookRole("EllenRestaurant Cook", 8, 8, 0, 0);
	private EllenHostRole host;
	private EllenCashierRole cashier;
	private EllenCookRole cook;
	
    private RevolvingStand revolvingStand = new RevolvingStand();
    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
        
    private Vector<EllenCustomerRole> customers = new Vector<EllenCustomerRole>();
    private Vector<EllenWaiterRole> waiters = new Vector<EllenWaiterRole>();

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

    private EllenRestaurantGui gui; //reference to main gui

    public EllenRestaurantPanel(EllenRestaurantGui gui) {
    	
        this.gui = gui;

        /*
        cook.setMenu(new EllenMenu());
        cook.setStand(revolvingStand);
        host.startThread();
        cashier.startThread();
        cook.startThread();
        */
        
        setLayout(new GridLayout(1, 2, 0, 0));
        group.setLayout(new GridLayout(1, 3, 0, 0));
        
        waiterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        //group.add(waiterPanel);
        //group.add(customerPanel);
        

        //initRestLabel();
        add(restLabel);
        //add(group);

        cookInventoryPanel.setLayout(new GridLayout(1, 2, 5, 5));
        soupBtn.addActionListener(this);
        pizzaBtn.addActionListener(this);
        cookInventoryPanel.add(soupBtn);
        cookInventoryPanel.add(pizzaBtn);
        //add(cookInventoryPanel);
                
      //Thread to tell cook to check every so often
        Runnable standChecker = new Runnable() {
			 public void run() {
				cook.msgCheckStand();
			 }
		 };
		 
		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(standChecker, 0, 15, TimeUnit.SECONDS);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseBtn){
        	System.out.println("PAUSE BUTTON PRESSED.");
        	host.pause();
        	cook.pause();
        	for (EllenCustomerRole cust : customers){
        		cust.pause();
        	}
        	for (EllenWaiterRole waiter : waiters){
        		waiter.pause();
        	}
        }
        else if (e.getSource() == unpauseBtn){
        	System.out.println("RESTART BUTTON PRESSED.");
        	host.restart();
        	cook.restart();
        	for (EllenCustomerRole cust : customers){
        		cust.restart();
        	}
        	for (EllenWaiterRole waiter : waiters){
        		waiter.restart();
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
     *//*
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
*/
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
    
    public EllenHostRole getHost() {
    	return host;
    }
    
    public EllenCashierRole getCashier() {
    	return cashier;
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void handleRole(Role r){
    	if(r instanceof EllenCashierRole) {
    		cashier = (EllenCashierRole) r;
    		
    		for(EllenWaiterRole w : waiters) {
    			w.setCashier(cashier);
    		}
    		for(EllenCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
            
    		if(host != null) {
    			host.setCashier(cashier);
    			cashier.setHost(host);
    		}
    		
    		ContactList.getInstance().setEllenCashier(cashier);
    	}
    	
    	if(r instanceof EllenCookRole) {
    		cook = (EllenCookRole) r;
    		//cookGui = new CookGui(cook, gui);
            cook.setStand(revolvingStand);
    		//gui.animationPanel.addGui(cookGui);
            //cook.setGui(cookGui);
            cook.setCashier(cashier);
            cook.setMenu(new EllenMenu());
            
            KitchenGui kitchenGui = new KitchenGui(gui);
            cook.setKitchenGui(kitchenGui);
            gui.animationPanel.addGui(kitchenGui);
            
            if(host != null) host.setCook(cook);
            for(EllenWaiterRole w : waiters) {
    			w.setCook(cook);
    		}
    		ContactList.getInstance().setEllenCook(cook);
    	}
    	
    	if(r instanceof EllenHostRole) {
    		host = (EllenHostRole) r;
    		
    		for(EllenWaiterRole w : waiters) {
    			w.setHost(host);
    			host.addWaiter(w);
    		}
    		for(EllenCustomerRole c : customers) {
    			c.setHost(host);
    		}
    		
    		host.setCook(cook);
    		host.setCashier(cashier);
    		
    		if(cashier != null) cashier.setHost(host);
    		ContactList.getInstance().setEllenHost(host);
    	}
    	
    	if(r instanceof EllenWaiterRole) {
        	EllenNormalWaiterRole w = (EllenNormalWaiterRole) r;
        	/*
    		if(r.getName().contains("share")) {
    			MarcusSharedWaiterRole a = (MarcusSharedWaiterRole) r;
    			a.setStand(stand);
    		}*/
    		WaiterGui g = new WaiterGui(w, gui);
    		//set the home positions based on position in waiter list
    		int i = 0;
    		for (EllenWaiterRole wait : waiters){
    			if (wait.equals(w)) g.setHomePosition((WINDOWX + i*70)/3, 30); 
    			else i++;
    		}
    		gui.animationPanel.addGui(g);
            if(host != null) host.addWaiter(w);
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
            //host.addWaiter(w);
    		waiters.add(w);
    	}
    	
    	if(r instanceof EllenCustomerRole) {
    		EllenCustomerRole c = (EllenCustomerRole) r;
	    	
    		for(EllenCustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if (cust == c) return;
	    	}
			customers.add(c);
			CustomerGui g = new CustomerGui(c, gui);
    		int i = 0;
    		for (EllenCustomerRole cust : customers){
    			if (cust.equals(c)) g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			else i++;
    		}
			gui.animationPanel.addGui(g);
			c.setHost(host);
			c.setGui(g);
			c.setCashier(cashier);
    	}
    }
    
    
}
