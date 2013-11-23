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
	
	private Vector <BankCustomer> bankcustomers = new Vector <BankCustomer>();
	private Vector <BankTeller> banktellers = new Vector <BankTeller>();
	
	
	public PersonAgent p = new PersonAgent("bob");
	public PersonAgent p1 = new PersonAgent("bob1");
	
	public BankManager bankmanager = new BankManager("Saul");
	
	private Banker banker = new Banker("Dave");
	private BankTeller bankteller = new BankTeller("kim");
	private BankTeller bankteller1 = new BankTeller("sam");
	
	
	
	BankCustomer bankcust = new BankCustomer(p, "bob");
	BankCustomer bankcust1 = new BankCustomer(p1,"bob1");
	
	

	
	private BankAccounts mainaccounts = new BankAccounts();
	

    
    private JPanel group = new JPanel();

    private BankGui gui; //reference to main gui



    public BankPanel(BankGui gui) {
        this.gui = gui;
        //hack creating a new account with 0 monies
        
        mainaccounts.addAccount("bob", 1000, p, 0);
        mainaccounts.addAccount("bob1", 1000, p1, 1);
        
        p.setCash(400);
        p1.setCash(500);
      //TODO should check the list size then add set the tellernumber then add self to list
        bankteller.setTellerNumber(banktellers.size());
        banktellers.add(bankteller);
        
        bankteller1.setTellerNumber(banktellers.size());
        banktellers.add(bankteller1);
        
        BankTellerGui btGui = new BankTellerGui(bankteller, gui);
        BankTellerGui btGui1 = new BankTellerGui(bankteller1, gui);
        bankteller.setGui(btGui);
        bankteller1.setGui(btGui1);
        gui.bankAnimationPanel.addGui(btGui);
        gui.bankAnimationPanel.addGui(btGui1);
        
        
        
        
        banker.startThread();
        banker.setBankAccounts(mainaccounts);
        
        bankteller.startThread();
        bankteller.msgGoToWork();
        bankteller.setBankAccounts(mainaccounts);
        
        bankteller1.startThread();
        bankteller1.msgGoToWork();
        bankteller1.setBankAccounts(mainaccounts);
        
        bankmanager.bankers.add(new myBanker(banker));
        bankmanager.msgTellerAdded(bankteller);
        bankmanager.msgTellerAdded(bankteller1);
        
        
        
        BankCustomerGui bcGui = new BankCustomerGui(bankcust, gui);
        BankCustomerGui bcGui1 = new BankCustomerGui(bankcust1, gui);
        gui.bankAnimationPanel.addGui(bcGui);
        gui.bankAnimationPanel.addGui(bcGui1);
        bankcust.setGui(bcGui);
        bankcust.setBankManager(bankmanager);
        bankcust1.setGui(bcGui1);
        bankcust1.setBankManager(bankmanager);
        


        setLayout(new FlowLayout());
        group.setLayout(new FlowLayout());
     
        


        bankmanager.startThread();
        banker.startThread();
        
        
        bankcustomers.add(bankcust);
        bankcust.startThread();
        bankcust.setAmount(400);
        bankcust.setMyaccountnumber(0);
        bankcust.setBankbalance(0);
        
        bankcustomers.add(bankcust1);
        bankcust1.startThread();
        bankcust1.setAmount(400);
        bankcust1.setMyaccountnumber(1);
        bankcust1.setBankbalance(0);
        //System.out.println(bankmanager.bankers.size());
        //System.out.println(bankmanager.tellers.size());

        bankcust.msgWantToDeposit();
        bankcust1.msgWantToWithdraw();
        
        add(group);
    }


 

}
