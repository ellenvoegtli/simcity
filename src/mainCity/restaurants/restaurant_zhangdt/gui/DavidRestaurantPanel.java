package mainCity.restaurants.restaurant_zhangdt.gui;

import mainCity.contactList.ContactList;
import mainCity.restaurants.restaurant_zhangdt.gui.CookGui;
import mainCity.restaurants.restaurant_zhangdt.sharedData.RevolvingStand;
import role.davidRestaurant.*;

import javax.swing.*;

import role.Role;
import role.davidRestaurant.DavidCashierRole;
import role.davidRestaurant.DavidCookRole;
import role.davidRestaurant.DavidCustomerRole;
import role.davidRestaurant.DavidHostRole;
import role.davidRestaurant.DavidMarketRole;
import role.davidRestaurant.DavidWaiterRole;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class DavidRestaurantPanel extends JPanel implements ActionListener{
	DavidAnimationPanel animation; 
    private DavidHostRole host;
    
    private Vector<DavidCustomerRole> customers = new Vector<DavidCustomerRole>();
    private Vector<DavidWaiterRole> waiters = new Vector<DavidWaiterRole>();
    private RevolvingStand stand = new RevolvingStand();
    
    private DavidCashierRole cashier;
    private DavidCookRole cook;
    private CookGui cookGui;
    
    private int WaiterXLoc = 60;
    private int WaiterYLoc = 20; 

    public DavidRestaurantPanel(DavidAnimationPanel a) {
        this.animation = a;
        
        Runnable standChecker = new Runnable() { 
        	public void run() { 
        		try { 
        			if(cook.isActive()) {
        				//cook.msgCheckStand(); 
        			}
        		}
        		catch(NullPointerException e) {
        			
        		}
        	}
        };
        
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(standChecker, 0, 15, TimeUnit.SECONDS);
        addKeyBindings();
    }

    
    /*
    public void addPerson(String type, String name, boolean hungry) {

    	if (type.equals("Customers")) {
    		DavidCustomerRole c = new DavidCustomerRole(name, null);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.getAnimationPanel().addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(cashier);
    		c.setGui(g);
    		customers.add(c);
    		
    		if(hungry) {
    			c.getGui().setHungry();
    		}
    		c.startThread();
    	}
    	
    	if (type.equals("Waiters")) { 
    		DavidWaiterRole w = new DavidWaiterRole(name); 
    		
    		WaiterGui h = new WaiterGui(w, WaiterXLoc, WaiterYLoc, WaiterXLoc, WaiterYLoc); 
    		
    		WaiterYLoc = WaiterYLoc + 30;
    		if(WaiterYLoc > 165){ 
    			WaiterYLoc = 20;
    			WaiterXLoc = WaiterXLoc + 30;
    		}
    		
    		
    		gui.getAnimationPanel().addGui(h); 
    		w.setCook(cook); 
    		w.setCashier(cashier);
    		w.setHost(host);
    		w.setGui(h); 
    		w.setRGui(gui);
    		waiters.add(w);
    		
    		w.startThread();
    		
    		host.addWaiter(w);
    	}
    }
    */

    
    public void actionPerformed(ActionEvent e) { 
    	
    }
    
    public void handleRole(Role r) { 
    	if(r instanceof DavidCashierRole) { 
    		cashier = (DavidCashierRole) r; 
    		
    		for(DavidWaiterRole w : waiters) { 
    			w.setCashier(cashier); 
    		}
    		for(DavidCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
    		
    		if(host != null) { 
    			host.setCashier(cashier); 
    			cashier.setHost(host);
    		}
    		
    		ContactList.getInstance().setDavidCashier(cashier);
    	}
    	
    	if(r instanceof DavidCookRole) { 
    		cook = (DavidCookRole) r;
    		cookGui = new CookGui(cook, 20, 200, 20, 200); 
    		cook.setStand(stand);
    		animation.addGui(cookGui);
    		cook.setGui(cookGui); 
    		cook.addCashier(cashier);
    		
    		if(host != null) 
    			host.setCook(cook); 
    		for(DavidWaiterRole w : waiters) { 
    			w.setCook(cook);
    		}
    		ContactList.getInstance().setDavidCook(cook);
    	}
    	
    	if(r instanceof DavidCustomerRole) { 
    		DavidCustomerRole c = (DavidCustomerRole) r; 
    		customers.add(c); 
    		CustomerGui g = new CustomerGui(c, animation);
    		
    		animation.addGui(g); 
    		c.setHost(host);
    		c.setGui(g); 
    		c.setCashier(cashier);
    	}
    	
    	if(r instanceof DavidHostRole) { 
    		host = (DavidHostRole) r; 
    		
    		for(DavidWaiterRole w : waiters) { 
    			w.setHost(host); 
    			host.addWaiter(w); 
    		}
    		for(DavidCustomerRole c : customers) { 
    			c.setHost(host); 
    		}
    		
    		host.setCook(cook);
    		host.setCashier(cashier);
    		
    		if(cashier != null) { 
    			cashier.setHost(host); 
    			ContactList.getInstance().setDavidHost(host);
    		}
    	}
    	
    	if(r instanceof DavidWaiterRole) {
    		
    		if(r instanceof DavidSharedWaiterRole) { 
    			((DavidSharedWaiterRole) r).setStand(stand);
    		}
    		
    		DavidWaiterRole w = (DavidWaiterRole) r; 
    		
    		WaiterGui g = new WaiterGui(w, WaiterXLoc, WaiterYLoc, WaiterXLoc, WaiterYLoc); 
    		
	    		WaiterYLoc = WaiterYLoc + 30;
	    		if(WaiterYLoc > 165){ 
	    			WaiterYLoc = 20;
	    			WaiterXLoc = WaiterXLoc + 30;
	    		}
	    	
	    	animation.addGui(g); 
	    	w.setHost(host);
	    	w.setGui(g); 
	    	w.setCook(cook);
	    	w.setCashier(cashier); 
	    	if(host != null) { 
	    		host.addWaiter(w);
	    		waiters.add(w); 
	    	}
    	}
    }
    
    protected void addKeyBindings() { 
    	
    	String CtrlQ = "CTRL Q"; 
    	String CtrlW = "CTRL W";
        String CtrlE = "CTRL E";
        String CtrlR = "CTRL R";
        String CtrlT = "CTRL T";
        String CtrlY = "CTRL Y";
        String CtrlA = "CTRL A";
        String CtrlS = "CTRL S";
        String CtrlD = "CTRL D";
        String CtrlF = "CTRL F";
        String CtrlG = "CTRL G";
        String CtrlZ = "CTRL Z";
        
        Action keyCtrlQ = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl Q : Waiting Customers will leave if restaurant is full"); 
        		host.DealWithImpatience();
        	}
        };
        
        Action keyCtrlW = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl W : Sets the first customer's money to zero"); 
        		customers.get(0).setMoney(0);
        	}
        };
        
        Action keyCtrlE = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl E : Emptys the cook's inventory"); 
        		cook.emptyInventory();
        	}
        };
        
        Action keyCtrlR = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl R : Refills the cook's inventory"); 
        		cook.ManualOrder();
        	}
        };
        
        Action keyCtrlT = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl T : Allows first customer to order without having enough money"); 
        		customers.get(0).setMoral();
        	}
        };
        
        Action keyCtrlY = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl Y : Empties first market"); 
        	}
        };
        
        Action keyCtrlA = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl A : Empties the inventory of steak"); 
        		cook.emptySteak();
        	}
        };
        
        Action keyCtrlS = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl S : Empties the inventory of chicken"); 
        		cook.emptyChicken();
        	}
        };
        
        Action keyCtrlD = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl D : Empties the inventory of salad"); 
        		cook.emptySalad();
        	}
        };
        
        Action keyCtrlF = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl F : Empties the inventory of pizza"); 
        		cook.emptyPizza();
        	}
        };
        
        Action keyCtrlG = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Ctrl G : Empties the inventory of pizza"); 
        		customers.get(0).setMoney(6);
        	}
        };
        
        Action keyCtrlZ = new AbstractAction() { 
        	public void actionPerformed(ActionEvent e){
        		cashier.Money = 0;
        		System.out.println("Ctrl Z : Gets rid of Cashier's money. Setting cashier's money to: " + cashier.Money); 
        	}
        };
        
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK), CtrlQ);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK), CtrlW);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK), CtrlE);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK), CtrlR);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK), CtrlT);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK), CtrlY);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK), CtrlA);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK), CtrlS);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK), CtrlD);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK), CtrlF);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK), CtrlG);
        getInputMap(this.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), CtrlZ);
 
       
        getActionMap().put(CtrlQ, keyCtrlQ);
        getActionMap().put(CtrlW, keyCtrlW);
        getActionMap().put(CtrlE, keyCtrlE);
        getActionMap().put(CtrlR, keyCtrlR);
        getActionMap().put(CtrlT, keyCtrlT);
        getActionMap().put(CtrlY, keyCtrlY);
        getActionMap().put(CtrlA, keyCtrlA);
        getActionMap().put(CtrlS, keyCtrlS);
        getActionMap().put(CtrlD, keyCtrlD);
        getActionMap().put(CtrlF, keyCtrlF);
        getActionMap().put(CtrlG, keyCtrlG);
        getActionMap().put(CtrlZ, keyCtrlZ);
    }

}
