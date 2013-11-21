package mainCity.restaurants.restaurant_zhangdt.gui;

import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole;
import mainCity.restaurants.restaurant_zhangdt.DavidHostRole;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole;
import mainCity.restaurants.restaurant_zhangdt.DavidCookRole;
import mainCity.restaurants.restaurant_zhangdt.DavidMarketRole;
import mainCity.restaurants.restaurant_zhangdt.CashierAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel implements ActionListener{

    //Host, cook, waiters and customers
    private DavidHostRole host = new DavidHostRole("Sarah");
    private DavidCookRole cook = new DavidCookRole("Jim");
    private CashierAgent cashier = new CashierAgent("Bob");
    
    private Vector<DavidMarketRole> markets = new Vector<DavidMarketRole>();
    private Vector<DavidCustomerRole> customers = new Vector<DavidCustomerRole>();
    private Vector<DavidWaiterRole> waiters = new Vector<DavidWaiterRole>();

    private JPanel restLabel = new JPanel();
    private JTabbedPane CWPane = new JTabbedPane();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();
    
    private int WaiterXLoc = 60;
    private int WaiterYLoc = 20; 
    
    private JButton pauseButton = new JButton("Pause");

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        
        host.startThread(); 
        CookGui cg = new CookGui(cook, 20, 200, 20, 200); 
        gui.getAnimationPanel().addGui(cg);
        cook.setGui(cg);
        
        for(int i=0; i<3; i++){
	        markets.add(new DavidMarketRole(i,5,5,5,5));
	        markets.get(i).addCook(cook);
	        markets.get(i).addCashier(cashier);
	        markets.get(i).setGui(gui);
	        markets.get(i).startThread();
	        cook.addMarket(markets.get(i));
	        cashier.addMarket(markets.get(i));
        }
        
        cook.addCashier(cashier);
        cook.startThread();
        cashier.startThread();
        cashier.setGui(gui);

        setLayout(new BorderLayout());
        CWPane.addTab("Customers",  customerPanel);
        CWPane.addTab("Waiters", waiterPanel);
        group.setLayout(new GridLayout(1, 2, 10, 10));
        group.setPreferredSize(new Dimension(200, 40));
        group.add(CWPane);
        
        // Creating contents of RestLabel
        initRestLabel();
        restLabel.setPreferredSize(new Dimension(150, 40));
 
        // Adding properties to pause button
        JPanel buttonPanel = new JPanel(); 
        buttonPanel.setLayout(new BorderLayout());
        pauseButton.addActionListener(this);
        pauseButton.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(pauseButton, BorderLayout.CENTER);
        
        // Adding components to Restaurant Panel
        add(restLabel, BorderLayout.WEST);
        add(group, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
        
        addKeyBindings();
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("        "), BorderLayout.WEST);
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
                DavidCustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if (type.equals("Waiters")) { 
        	for (int i = 0; i < waiters.size(); i++) { 
        		DavidWaiterRole temp = waiters.get(i); 
        		if (temp.getName() == name) 
        			gui.updateInfoPanel(temp);
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean hungry) {

    	if (type.equals("Customers")) {
    		DavidCustomerRole c = new DavidCustomerRole(name);	
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
    
    public void actionPerformed(ActionEvent e) { 
    	if(e.getSource() == pauseButton){
    		//if(host.pause == false){
	    		host.pause();
	    		cook.pause();
	    		
	    		for(int i=0; i<waiters.size(); i++) {
	    			waiters.get(i).pause();
	    		}
	    		
	    		for(int i=0; i<customers.size(); i++){
	    			customers.get(i).pause(); 
	    		}
	    		
	    		pauseButton.setText("Resume"); 
	    		
    		}
    		else{ 
    			host.restart();
	    		cook.restart();
	    		
	    		for(int i=0; i<waiters.size(); i++) {
	    			waiters.get(i).restart();
	    		}
	    		
	    		for(int i=0; i<customers.size(); i++){
	    			customers.get(i).restart(); 
	    		}
	    		
	    		pauseButton.setText("Pause");
	    		
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
        		markets.get(0).emptyMarket();
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
