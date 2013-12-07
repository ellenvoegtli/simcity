package mainCity.market2.gui;

import mainCity.contactList.*;
import mainCity.market2.*;
import mainCity.market2.interfaces.*;

import javax.swing.*;

import java.awt.event.*;
import java.util.Vector;

import role.Role;
import role.market2.*;

/**
 * Panel in frame that contains all the restaurant information,
 * including greeter, cook, waiters, and customers.
 */
public class Market2Panel extends JPanel implements ActionListener{
	private Market2AnimationPanel animation;
	private Greeter greeter;
	private MarketCashier cashier;
	private DeliveryMan2 deliveryMan;
        
    private Vector<Market2CustomerRole> customers = new Vector<Market2CustomerRole>();
    private Vector<Market2EmployeeRole> employees = new Vector<Market2EmployeeRole>();
    
    
    public Market2Panel(Market2AnimationPanel market){
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
    	if(r instanceof Market2CashierRole) {
    		cashier = (Market2CashierRole) r;
    		System.out.println("setting cashier");
    		
    		for(Market2EmployeeRole e : employees) {
    			e.setCashier(cashier);
    		}
    		for(Market2CustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
            
    		if(greeter != null) {
    			greeter.setCashier(cashier);
    			cashier.setGreeter(greeter);
    		}
    		
    		ContactList.getInstance().setMarket2Cashier((Market2CashierRole)cashier);
    	}
    	
    	if(r instanceof Market2DeliveryManRole) {
    		deliveryMan = (DeliveryMan2) r;
    		ContactList.getInstance().getCity().addDelivery2Gui((Market2DeliveryManRole) deliveryMan);
            deliveryMan.setCashier(cashier);
            
            if(greeter != null) greeter.setDeliveryMan(deliveryMan);
            for(Market2EmployeeRole e : employees) {
    			e.setDeliveryMan(deliveryMan);
    		}
    		ContactList.getInstance().setMarket2DeliveryMan((Market2DeliveryManRole) deliveryMan);
    	}
    	
    	if(r instanceof Market2GreeterRole) {
    		greeter = (Market2GreeterRole) r;
    		
    		for(Market2EmployeeRole e : employees) {
    			e.setGreeter(greeter);
    			greeter.addEmployee(e);
    		}
    		for(Market2CustomerRole c : customers) {
    			c.setGreeter(greeter);
    		}
    		
    		greeter.setDeliveryMan(deliveryMan);
    		greeter.setCashier(cashier);
    		
    		if(cashier != null) cashier.setGreeter(greeter);
    			ContactList.getInstance().setMarket2Greeter((Market2GreeterRole) greeter);
    	}
    	
    	if(r instanceof Market2EmployeeRole) {
    		Market2EmployeeRole e = (Market2EmployeeRole) r;
    		EmployeeGui g = new EmployeeGui(e, animation);
    		
    		animation.addGui(g);
    		e.setGreeter(greeter);
    		e.setCashier(cashier);
    		e.setDeliveryMan(deliveryMan);
    		e.setGui(g);
    		employees.add(e);
    		
    		int i = 0;
    		int x = 0, y = 0;
    		for (Market2EmployeeRole em : employees){
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
    		
    		if (greeter != null) /*greeter.addEmployee(e, x, y);*/ greeter.addEmployee(e);
    	}
    	
    	if(r instanceof Market2CustomerRole) {
    		Market2CustomerRole c = (Market2CustomerRole) r;
	    	
    		for(Market2CustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if (cust == c) return;
	    	}
			customers.add(c);
			CustomerGui g = new CustomerGui(c, animation);
    		int i = 0;
    		for (Market2CustomerRole cust : customers){
    			if (cust.equals(c)) g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			else i++;
    		}
			animation.addGui(g);
			c.setGreeter(greeter);
			c.setGui(g);
			c.setCashier(cashier);
    		
    		
    	}
    }

}
