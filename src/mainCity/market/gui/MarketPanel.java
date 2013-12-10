package mainCity.market.gui;

import mainCity.contactList.*;
import mainCity.interfaces.DeliveryMan;
import mainCity.market.*;
import mainCity.market.interfaces.*;

import javax.swing.*;

import java.awt.event.*;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import role.Role;
import role.market.MarketCashierRole;
import role.market.MarketCustomerRole;
import role.market.MarketDeliveryManRole;
import role.market.MarketEmployeeRole;
import role.market.MarketGreeterRole;

/**
 * Panel in frame that contains all the restaurant information,
 * including greeter, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel implements ActionListener{
	private MarketAnimationPanel animation;
	private Greeter greeter;
	private MarketCashier cashier;
	private DeliveryMan deliveryMan;
	private MarketMenu menu1 = new MarketMenu(5, 5, 5, 5, 5, 5, 5, 5, 5, 20, 20, 20, 20, 20, 20, 20, 20, 20);
	private MarketMenu menu2 = new MarketMenu(20, 20, 20, 20, 20, 20, 20, 20, 20, 5, 5, 5, 5, 5, 5, 5, 5, 5);
        
    private Vector<MarketCustomerRole> customers = new Vector<MarketCustomerRole>();
    private Vector<MarketEmployeeRole> employees = new Vector<MarketEmployeeRole>();
    
    
    public MarketPanel(MarketAnimationPanel market){
        this.animation = market;
        
      //Thread to tell delivery man to check for undelivered orders every so often
		 Runnable orderChecker = new Runnable() {
			 public void run() {
				try {
					if(deliveryMan.isActive())
						deliveryMan.msgCheckForRedeliveries();
				}
				catch(NullPointerException e) {
				}
			 }
		 };
		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(orderChecker, 0, 15, TimeUnit.SECONDS);
        
    }
    public void actionPerformed(ActionEvent e) {

    }


    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void handleRole(Role r){
    	if(r instanceof MarketCashierRole) {
    		cashier = (MarketCashierRole) r;
    		if (r.getName().toLowerCase().contains("market2"))
    			cashier.setMenu(menu2);
    		else
    			cashier.setMenu(menu1);
    		
    		for(MarketEmployeeRole e : employees) {
    			e.setCashier(cashier);
    		}
    		for(MarketCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
            
    		if(greeter != null) {
    			greeter.setCashier(cashier);
    			cashier.setGreeter(greeter);
    		}
    		
    		ContactList.getInstance().setMarketCashier((MarketCashierRole)cashier);
    	}
    	
    	if(r instanceof MarketDeliveryManRole) {
    		deliveryMan = (MarketDeliveryManRole) r;
    		if (r.getName().toLowerCase().contains("market2"))
    			ContactList.getInstance().getCity().addDeliveryGui((MarketDeliveryManRole) deliveryMan, 655, 135);
    		else
    			ContactList.getInstance().getCity().addDeliveryGui((MarketDeliveryManRole) deliveryMan, 415, 200);
            deliveryMan.setCashier(cashier);
            
            if(greeter != null) greeter.setDeliveryMan(deliveryMan);
            for(MarketEmployeeRole e : employees) {
    			e.setDeliveryMan(deliveryMan);
    		}
    		ContactList.getInstance().setMarketDeliveryMan((MarketDeliveryManRole) deliveryMan);
    	}
    	
    	if(r instanceof MarketGreeterRole) {
    		greeter = (MarketGreeterRole) r;
    		
    		for(MarketEmployeeRole e : employees) {
    			e.setHost(greeter);
    			greeter.addEmployee(e);
    		}
    		for(MarketCustomerRole c : customers) {
    			c.setHost(greeter);
    		}
    		
    		greeter.setDeliveryMan(deliveryMan);
    		greeter.setCashier(cashier);
    		
    		if(cashier != null) cashier.setGreeter(greeter);
    		ContactList.getInstance().setMarketGreeter((MarketGreeterRole) greeter);
    	}
    	
    	if(r instanceof MarketEmployeeRole) {
    		MarketEmployeeRole e = (MarketEmployeeRole) r;
    		EmployeeGui g = new EmployeeGui(e, animation);
    		
    		animation.addGui(g);
    		e.setHost(greeter);
    		e.setCashier(cashier);
    		e.setDeliveryMan(deliveryMan);
    		e.setGui(g);
    		employees.add(e);
    		
    		if (e.getName().toLowerCase().contains("market2"))
    			e.setMenu(menu2);
    		else
    			e.setMenu(menu1);
    		
    		int i = 0;
    		int x = 0, y = 0;
    		for (MarketEmployeeRole em : employees){
    			if (em.equals(e)){
    				if (i < 4){
    					x = 150 + 100*i + 15;	//add 15 to go to center of station
    					y = 50 + 17; 	//station height is 17
    				}
    				else {
    					x = 150 + 100*(i-4) + 15;	//add 15 to go to center of station
    					y = 150 + 17;	//station height is 17
    				}
    				g.setHomePosition(x, y);
    			}
    			else
    				i++;
    		}
    		
    		if (greeter != null)
    			greeter.addEmployee(e);
    	}
    	
    	if(r instanceof MarketCustomerRole) {
    		MarketCustomerRole c = (MarketCustomerRole) r;
    		if (c.getName().toLowerCase().contains("market2"))
    			c.setMenu(menu2);
    		else
    			c.setMenu(menu1);
	    	
    		for(MarketCustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if (cust == c) return;
	    	}
			customers.add(c);
			CustomerGui g = new CustomerGui(c, animation);
    		int i = 0;
    		for (MarketCustomerRole cust : customers){
    			if (cust.equals(c)) g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			else i++;
    		}
			animation.addGui(g);
			c.setHost(greeter);
			c.setGui(g);
			c.setCashier(cashier);
    		
    		
    	}
    }
    
    public MarketGreeterRole getGreeter(){
    	return (MarketGreeterRole) greeter;
    }
    public MarketCashierRole getCashier(){
    	return (MarketCashierRole) cashier;
    }
    public MarketDeliveryManRole getDeliveryMan(){
    	return (MarketDeliveryManRole) deliveryMan;
    }

}
