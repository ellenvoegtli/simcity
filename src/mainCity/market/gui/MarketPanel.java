package mainCity.market.gui;

import mainCity.market.*;
import mainCity.contactList.*;
import mainCity.market.interfaces.*;

import javax.swing.*;

import java.awt.event.*;
import java.util.Vector;

import role.Role;
import role.market.MarketCashierRole;
import role.market.MarketCustomerRole;
import role.market.MarketDeliveryManRole;
import role.market.MarketEmployeeRole;
import role.market.MarketGreeterRole;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel implements ActionListener{
	private MarketAnimationPanel animation;
	private Greeter host;
	private MarketCashier cashier;
	private DeliveryMan deliveryMan;
        
    private Vector<MarketCustomerRole> customers = new Vector<MarketCustomerRole>();
    private Vector<MarketEmployeeRole> employees = new Vector<MarketEmployeeRole>();
    
    
    public MarketPanel(MarketAnimationPanel market){
        this.animation = market;
        
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
    		System.out.println("setting cashier");
    		
    		for(MarketEmployeeRole e : employees) {
    			e.setCashier(cashier);
    		}
    		for(MarketCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
            
    		if(host != null) {
    			host.setCashier(cashier);
    			cashier.setGreeter(host);
    		}
    		
    		ContactList.getInstance().setMarketCashier((MarketCashierRole)cashier);
    	}
    	
    	if(r instanceof MarketDeliveryManRole) {
    		deliveryMan = (DeliveryMan) r;
    		ContactList.getInstance().getCity().addDeliveryGui((MarketDeliveryManRole) deliveryMan);
            deliveryMan.setCashier(cashier);
            
            if(host != null) host.setDeliveryMan(deliveryMan);
            for(MarketEmployeeRole e : employees) {
    			e.setDeliveryMan(deliveryMan);
    		}
    		ContactList.getInstance().setMarketDeliveryMan((MarketDeliveryManRole) deliveryMan);
    	}
    	
    	if(r instanceof MarketGreeterRole) {
    		host = (MarketGreeterRole) r;
    		
    		for(MarketEmployeeRole e : employees) {
    			e.setHost(host);
    			host.addEmployee(e);
    		}
    		for(MarketCustomerRole c : customers) {
    			c.setHost(host);
    		}
    		
    		host.setDeliveryMan(deliveryMan);
    		host.setCashier(cashier);
    		
    		if(cashier != null) cashier.setGreeter(host);
    		ContactList.getInstance().setMarketGreeter((MarketGreeterRole) host);
    	}
    	
    	if(r instanceof MarketEmployeeRole) {
    		MarketEmployeeRole e = (MarketEmployeeRole) r;
    		EmployeeGui g = new EmployeeGui(e, animation);
    		
    		animation.addGui(g);
    		e.setHost(host);
    		e.setCashier(cashier);
    		e.setDeliveryMan(deliveryMan);
    		e.setGui(g);
    		employees.add(e);
    		
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
    		
    		if (host != null) /*host.addEmployee(e, x, y);*/ host.addEmployee(e);
    	}
    	
    	if(r instanceof MarketCustomerRole) {
    		MarketCustomerRole c = (MarketCustomerRole) r;
	    	
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
			c.setHost(host);
			c.setGui(g);
			c.setCashier(cashier);
    		
    		
    	}
    }

}
