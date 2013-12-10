package mainCity.bank.gui;



import javax.swing.*;

import role.Role;
import role.bank.BankCustomerRole;
import role.bank.BankManagerRole;
import role.bank.BankRobberRole;
import role.bank.BankTellerRole;
import role.bank.BankerRole;
import role.bank.BankManagerRole.myBankCustomer;
import role.bank.BankManagerRole.myBanker;
import role.bank.BankManagerRole.myTeller;
import mainCity.PersonAgent;
import mainCity.bank.BankAccounts;
import mainCity.bank.BankAccounts.BusinessAccount;
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
        
        
     

        setLayout(new FlowLayout());
        group.setLayout(new FlowLayout());

        
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
    
    
 


