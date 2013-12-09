package mainCity.bank.gui;



import javax.swing.*;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.BankAccounts;
import mainCity.bank.BankAccounts.BusinessAccount;
import mainCity.bank.BankCustomerRole;
import mainCity.bank.BankManagerRole;
import mainCity.bank.BankRobberRole;
import mainCity.bank.BankTellerRole;
import mainCity.bank.BankManagerRole.myBankCustomer;
import mainCity.bank.BankManagerRole.myBanker;
import mainCity.bank.BankManagerRole.myTeller;
import mainCity.bank.BankerRole;
import mainCity.bank.gui.*;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankTeller;
import mainCity.contactList.ContactList;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class BankPanel extends JPanel {
	
	private BankAnimationPanel bankAnimationPanel;
	private List <BankCustomerRole>  bankcustomers = Collections.synchronizedList(new ArrayList<BankCustomerRole>());
	private List <BankTellerRole> banktellers = Collections.synchronizedList(new ArrayList <BankTellerRole>());
	private List <BankRobberRole> bankrobbers = Collections.synchronizedList(new ArrayList <BankRobberRole>());
	
	
	
	//public PersonAgent p1 = new PersonAgent("bob1");
	
	public BankManagerRole bankmanager;
	
	private BankerRole banker;
	//private BankTellerRole bankteller = new BankTellerRole("kim");
	//private BankTellerRole bankteller1 = new BankTellerRole("sam");
	
	BankCustomer bankcust;
	//BankCustomerRole bankcust1 = new BankCustomerRole(p1,"bob1");
	
	

	
	private BankAccounts mainaccounts = new BankAccounts();
	
	

    
    private JPanel group = new JPanel();

    //private BankGui gui; //reference to main gui



    public BankPanel(BankAnimationPanel BAPanel) {
        //this.gui = gui;
        bankAnimationPanel=BAPanel;
        
        mainaccounts.addBusinessAccount("market1", 2000);
        mainaccounts.addBusinessAccount("market2", 2000);
        mainaccounts.addBusinessAccount("ellenrestaurant", 2000);
        mainaccounts.addBusinessAccount("enarestaurant", 2000);
        mainaccounts.addBusinessAccount("davidrestaurant", 2000);
        mainaccounts.addBusinessAccount("jeffersonrestaurant", 2000);
        mainaccounts.addBusinessAccount("marcusrestaurant", 2000);
        
        //ContactList.getInstance().setBank(this);
        
        
        //hack creating a new account with 100 monies
        
        
        //TODO use this info for handlerole()
        
        //PersonAgent p = new PersonAgent("bob");
        //bankcust = new BankCustomerRole(p, p.getName());
        //mainaccounts.addAccount(p.getName(), 100, p, 0);
        //BankCustomerGui bcGui = new BankCustomerGui(bankcust, gui);
        
        //bankcust.setGui(bcGui);
		//bankcust.setBankManager(bankmanager);
		//bankcust.setAmount(400);
		//bankcust.setBankbalance(100);
       // bankcust.setMyaccountnumber(0);
        //bankcust.setBankbalance(0);

       // p.setCash(400);
       // p.addRole(PersonAgent.ActionType.bankWithdraw, bankcust);
        //bankcust.setActive();
        
        //mainaccounts.addAccount("bob1", 1000, p1, 1);
        
        //p1.setCash(500);
      
        
        /*
        bankteller.setTellerNumber(banktellers.size());
        banktellers.add(bankteller);
        bankteller1.setTellerNumber(banktellers.size());
        banktellers.add(bankteller1);
        
        BankTellerGui btGui = new BankTellerGui(bankteller);
        BankTellerGui btGui1 = new BankTellerGui(bankteller1);
        bankteller.setGui(btGui);
        bankteller1.setGui(btGui1);
        //bankAnimationPanel.addGui(btGui);
        //bankAnimationPanel.addGui(btGui1);
        //gui.bankAnimationPanel.addGui(bcGui);
        
        
       // BankerGui bGui = new BankerGui(banker,gui);
        //banker.setGui(bGui);
        //gui.bankAnimationPanel.addGui(bGui);
        
        //banker.startThread();
        //banker.setBankAccounts(mainaccounts);
        
        bankteller.startThread();
        bankteller.msgGoToWork();
        bankteller.setBankAccounts(mainaccounts);
        
        bankteller1.startThread();
        bankteller1.msgGoToWork();
        bankteller1.setBankAccounts(mainaccounts);
        *.
       // bankmanager.bankers.add(new myBanker(banker));
        bankmanager.msgTellerAdded(bankteller);
        bankmanager.setBankAccounts(mainaccounts);
        //bankmanager.msgTellerAdded(bankteller1);
        
        
        
        //BankCustomerGui bcGui = new BankCustomerGui(bankcust, gui);
        //BankCustomerGui bcGui1 = new BankCustomerGui(bankcust1, gui);
        //gui.bankAnimationPanel.addGui(bcGui);
        //gui.bankAnimationPanel.addGui(bcGui1);
        //bankcust.setGui(bcGui);
        //bankcust.setBankManager(bankmanager);
        /*
        bankcust1.setGui(bcGui1);
        if(bankmanager!=null){
        	bankcust1.setBankManager(bankmanager);	
        }
        else{
        	bankcust1.msgBankClosed();
        }
        */


        setLayout(new FlowLayout());
        group.setLayout(new FlowLayout());
     

       // bankmanager.startThread();


        
        //banker.msgGoToWork();
        //banker.startThread();
        
        
        //bankcustomers.add(bankcust);
        //bankcust.startThread();
        //bankcust.setAmount(400);
        //bankcust.setMyaccountnumber(0);
        //bankcust.setBankbalance(0);
        
        //bankcustomers.add(bankcust1);
        
        //changed startthread to that of person
        //p1.startThread();
        //bankcust1.startThread();
        //bankcust1.setAmount(400);
        //bankcust1.setMyaccountnumber(1);
        //bankcust1.setBankbalance(0);
        //System.out.println(bankmanager.bankers.size());
        //System.out.println(bankmanager.tellers.size());

        //bankcust.msgWantToWithdraw();
        //bankmanager.msgDirectDeposit(0, 500);
        //p.startThread();
        //bankcust1.msgWantToDeposit();
        
        add(group);
    }
    
    public void directDeposit(String businessname, double money){
    	for(BusinessAccount ba: mainaccounts.businessaccounts){
    		if(ba.businessName == businessname){
    			ba.balance+= money;
    		}
    	}		
    }
   
    public void directWithdraw(String businessname, double money){
    	for(BusinessAccount ba: mainaccounts.businessaccounts){
    		if(ba.businessName == businessname){
    			ba.balance-= money;
    		}
    	}		
    }
    
    
    //TODO finish this
    public void handleRole(Role r){
    	if(r instanceof BankCustomerRole){
    		BankCustomerRole b = (BankCustomerRole) r;
    		
    		synchronized(bankcustomers){
	    		for (BankCustomer bc: bankcustomers){
	    			if( bc==b){
	    				return;
	    			}
	    		}
    		}
    		bankcustomers.add(b);
    		BankCustomerGui bcGui = new BankCustomerGui(b);
    		b.setGui(bcGui);
    		bankAnimationPanel.addGui(bcGui);
    		b.setBankManager(bankmanager);
    		b.setAmount(((BankCustomer) r).getAmount());
            b.setMyaccountnumber(((BankCustomer) r).getMyaccountnumber());
            b.setBankbalance(((BankCustomer) r).getBankbalance());
    		
    	}
    	if(r instanceof BankRobberRole){
    		BankRobberRole br = (BankRobberRole) r;
    		
    		synchronized(bankrobbers){
	    		for (BankRobberRole current: bankrobbers){
	    			if( current==br){
	    				return;
	    			}
	    		}
    		}
    		bankrobbers.add(br);
    		BankRobberGui brGui = new BankRobberGui(br);
    		br.setGui(brGui);
    		bankAnimationPanel.addGui(brGui);
    		br.setBankManager(bankmanager);
    		
            
    		
    	}
    	
    	
    	
    	if (r instanceof BankerRole){
    		banker = (BankerRole) r;
    		BankerGui bg = new BankerGui(banker);
    		banker.setGui(bg);
    		banker.setBankAccounts(mainaccounts);
    		bankAnimationPanel.addGui(bg);
    		
    		if(bankmanager!=null){
    			bankmanager.mbanker=new myBanker(banker);
    			bankmanager.msgBankerAdded();
    		}
    	}
    	if (r instanceof BankTellerRole){
    		//System.out.println("in bankpanel teller handlerole");
    		BankTellerRole btr = (BankTellerRole) r;
    		
    		synchronized(banktellers){
	    		for(BankTeller bt:banktellers){
	    			if(bt==btr){
	    				return;
	    			}
	    		}
    		}
    		BankTellerGui btgui = new BankTellerGui(btr);
    		//System.out.println("teller gui instantiated");
    		btr.setGui(btgui);
    		btr.setBankAccounts(mainaccounts);
    		bankAnimationPanel.addGui(btgui);
    		btr.setTellerNumber(banktellers.size());
    		banktellers.add(btr);
    		if(bankmanager!=null){
    			bankmanager.msgTellerAdded(btr);
    		}
    		
    		
    	}
    	if(r instanceof BankManagerRole){
    		bankmanager = (BankManagerRole) r;
    		bankmanager.setBankAccounts(mainaccounts);
    		bankmanager.setBanker(banker);
    		
    		for(BankTeller bt:banktellers){
    			bankmanager.msgTellerAdded(bt);
    			
    		}
    		
    		for(BankCustomer bc:bankcustomers){
    			bc.setBankManager(bankmanager);
    		}	
    		ContactList.getInstance().setBankManager(bankmanager);
    	}
    		
    		
    	}
    	
    }
    
    
 


