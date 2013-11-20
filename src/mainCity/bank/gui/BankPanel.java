package mainCity.bank.gui;



import javax.swing.*;

import mainCity.PersonAgent;
import mainCity.bank.BankAccounts;
import mainCity.bank.BankCustomer;
import mainCity.bank.BankManager;
import mainCity.bank.BankManager.myBanker;
import mainCity.bank.BankManager.myTeller;
import mainCity.bank.BankTeller;
import mainCity.bank.Banker;
import mainCity.bank.gui.BankGui;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class BankPanel extends JPanel {
	
	public PersonAgent p = new PersonAgent();
	
	public BankManager bankmanager = new BankManager("Saul");
	
	private Banker banker = new Banker("Dave");
	private BankTeller bankteller = new BankTeller("kim");
	
	BankCustomer bankcust = new BankCustomer(p, "bob");
	

	
	private BankAccounts mainaccounts = new BankAccounts();
	

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
        //hack creating a new account with 0 monies
        
        mainaccounts.addAccount("bob", 1000, p, 0);
        
        p.setCash(400);
        
        
        banker.startThread();
        banker.setBankAccounts(mainaccounts);
        bankteller.startThread();
        bankteller.setBankAccounts(mainaccounts);
        bankmanager.bankers.add(new myBanker(banker));
        bankmanager.tellers.add(new myTeller(bankteller));
        
        bankcust.setBankManager(bankmanager);
        
        
        //host.waiters.add(waiter);
       // waiters.add(waiter);

        setLayout(new FlowLayout());
        group.setLayout(new FlowLayout());
     
        
       // group.add(customerPanel);
       // group.add(waiterPanel);

        bankmanager.startThread();
        banker.startThread();
        bankteller.startThread();
        bankcust.startThread();
        bankcust.setAmount(400);
        bankcust.setMyaccountnumber(0);
        bankcust.setBankbalance(0);
        //System.out.println(bankmanager.bankers.size());
        //System.out.println(bankmanager.tellers.size());

        bankcust.msgWantToDeposit();
        
        add(group);
    }


 

}
