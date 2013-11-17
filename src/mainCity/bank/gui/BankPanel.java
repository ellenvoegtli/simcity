package mainCity.bank.gui;



import javax.swing.*;

import mainCity.bank.gui.BankGui;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class BankPanel extends JPanel {

    //Host, cook, waiters and customers
  // private WaiterAgent waiter = new WaiterAgent("Sarah");
   // private HostAgent host =new HostAgent ("Sal");
  // private WaiterGui waiterGui = new WaiterGui(waiter);
   /*
	private CookAgent cook = new CookAgent("Jim");
    private CashierAgent cashier = new CashierAgent("Dave");
    
    private MarketAgent m1 = new MarketAgent();
    private MarketAgent m2 = new MarketAgent();
    private MarketAgent m3 = new MarketAgent();
    private MarketAgent m4 = new MarketAgent();
    
    
    
    
  

    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this,"Waiters");
    
    */
    private JPanel group = new JPanel();

    private BankGui gui; //reference to main gui



    public BankPanel(BankGui gui) {
        this.gui = gui;
      
        //host.waiters.add(waiter);
       // waiters.add(waiter);

        setLayout(new FlowLayout());
        group.setLayout(new FlowLayout());
     
        
       // group.add(customerPanel);
       // group.add(waiterPanel);

    
        add(group);
    }


 

}
