package mainCity.market1.gui;

import mainCity.contactList.*;
import mainCity.market1.*;
import mainCity.market1.interfaces.*;

import javax.swing.*;

import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import role.Role;
import role.market1.Market1CashierRole;
import role.market1.Market1CustomerRole;
import role.market1.Market1DeliveryManRole;
import role.market1.Market1EmployeeRole;
import role.market1.Market1GreeterRole;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel implements ActionListener{
	private MarketAnimationPanel animation;
	private Greeter host;
	private MarketCashier cashier;
	private DeliveryMan1 deliveryMan;
        
    private Vector<Market1CustomerRole> customers = new Vector<Market1CustomerRole>();
    private Vector<Market1EmployeeRole> employees = new Vector<Market1EmployeeRole>();
    
    
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
    	if(r instanceof Market1CashierRole) {
    		cashier = (Market1CashierRole) r;
    		System.out.println("setting cashier");
    		
    		for(Market1EmployeeRole e : employees) {
    			e.setCashier(cashier);
    		}
    		for(Market1CustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
            
    		if(host != null) {
    			host.setCashier(cashier);
    			cashier.setGreeter(host);
    		}
    		
    		ContactList.getInstance().setMarketCashier((Market1CashierRole)cashier);
    	}
    	
    	if(r instanceof Market1DeliveryManRole) {
    		deliveryMan = (Market1DeliveryManRole) r;
    		if (r.getName().toLowerCase().contains("market2"))
    			ContactList.getInstance().getCity().addDeliveryGui((Market1DeliveryManRole) deliveryMan, 655, 135);
    		else
    			ContactList.getInstance().getCity().addDeliveryGui((Market1DeliveryManRole) deliveryMan, 415, 200);
            deliveryMan.setCashier(cashier);
            
            if(host != null) host.setDeliveryMan(deliveryMan);
            for(Market1EmployeeRole e : employees) {
    			e.setDeliveryMan(deliveryMan);
    		}
    		ContactList.getInstance().setMarketDeliveryMan((Market1DeliveryManRole) deliveryMan);
    	}
    	
    	if(r instanceof Market1GreeterRole) {
    		host = (Market1GreeterRole) r;
    		
    		for(Market1EmployeeRole e : employees) {
    			e.setHost(host);
    			host.addEmployee(e);
    		}
    		for(Market1CustomerRole c : customers) {
    			c.setHost(host);
    		}
    		
    		host.setDeliveryMan(deliveryMan);
    		host.setCashier(cashier);
    		
    		if(cashier != null) cashier.setGreeter(host);
    		ContactList.getInstance().setMarketGreeter((Market1GreeterRole) host);
    	}
    	
    	if(r instanceof Market1EmployeeRole) {
    		Market1EmployeeRole e = (Market1EmployeeRole) r;
    		EmployeeGui g = new EmployeeGui(e, animation);
    		
    		animation.addGui(g);
    		e.setHost(host);
    		e.setCashier(cashier);
    		e.setDeliveryMan(deliveryMan);
    		e.setGui(g);
    		employees.add(e);
    		
    		int i = 0;
    		int x = 0, y = 0;
    		for (Market1EmployeeRole em : employees){
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
    		
    		if (host != null) /*host.addEmployee(e, x, y);*/ host.addEmployee(e);
    	}
    	
    	if(r instanceof Market1CustomerRole) {
    		Market1CustomerRole c = (Market1CustomerRole) r;
	    	
    		for(Market1CustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if (cust == c) return;
	    	}
			customers.add(c);
			CustomerGui g = new CustomerGui(c, animation);
    		int i = 0;
    		for (Market1CustomerRole cust : customers){
    			if (cust.equals(c)) g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			else i++;
    		}
			animation.addGui(g);
			c.setHost(host);
			c.setGui(g);
			c.setCashier(cashier);
    		
    		
    	}
    }

}
