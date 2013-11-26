package mainCity.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.interfaces.ManagerRole;
import agent.Agent;

public class BankManagerRole extends Role implements ManagerRole {

	String name;
	PersonAgent p;
	private BankAccounts ba;
	public List <myTeller> tellers= Collections.synchronizedList(new ArrayList<myTeller>());
	public myBanker mbanker;
	public List <myBankCustomer>  teller_bankCustomers = Collections.synchronizedList(new ArrayList<myBankCustomer>());
	public List <myBankCustomer>  banker_bankCustomers = Collections.synchronizedList(new ArrayList<myBankCustomer>());
	public boolean onDuty;

	public static class myTeller{
	    BankTellerRole t;
	    int tellernumber;
	    BankCustomerRole bc;
	    boolean Occupied;
	    public int gettellernumber(){
	    	return tellernumber;
	    }
	    
	    public void settellernumber(int tn){
	    	tellernumber=tn;
	    }
	    
	    public myTeller(BankTellerRole bt, int tn){
	    	t=bt;
	    	Occupied=false;
	    	tellernumber=tn;
	    }
	}

	public static class myBanker{
	    BankerRole b;
	    int bankernumber;
	    BankCustomerRole bc;
	    boolean Occupied;
	    public myBanker(BankerRole ba){
	    	b=ba;
	    	Occupied=false;
	    }
	}
	
	public static class myBankCustomer{
		BankCustomerRole bc;
		public myBankCustomer(BankCustomerRole bc){
			this.bc=bc;
		}
	}
	
	public BankManagerRole(PersonAgent p, String name){
		super(p);
		this.p=p;
		this.name=name;
		onDuty=true;
		Do("Bank Manager instantiated");
		
	}
	
	
	
	//Messages
	
	//TODO end shift and tell all employees to leave
	@Override
	public void msgEndShift() {
		Do("bank closing");
		onDuty = false;
		stateChanged();
		
	}
	
	public void msgTellerAdded(BankTellerRole bt){
		tellers.add(new myTeller(bt,tellers.size()));
	}
	
	public void msgDirectDeposit(double accountNumber, int amount){
		synchronized (ba.accounts) {
			for(BankAccount account:ba.accounts){
				if(account.accountNumber == accountNumber){
					account.balance+=amount;
					Do("Direct deposit performed. $" + amount + " was deposited for " + account.name +" with new balance of $" + account.balance);
				}
			}
		}
	}
	
	public void msgIWantToDeposit( BankCustomerRole bc){
		Do("recieved message IWantToDeposit");
	    teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	public void msgIWantToWithdraw( BankCustomerRole bc){
		Do("recieved message IWantToWithdraw");
		teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}
	
	public void msgIWantNewAccount(BankCustomerRole bc) {
		Do("recieved message want new account");
		banker_bankCustomers.add(new myBankCustomer(bc));
		stateChanged();
		
	}
	
	

	public void msgIWantALoan(BankCustomerRole bc){
		Do("recieved message IWantALoan");
		banker_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	public void msgImLeaving(BankTellerRole bt){
		for (myTeller mt: tellers){
			if(mt.t==bt){
				tellers.remove(mt);
			}
		}
		
	}
	
	public void msgImLeaving(BankerRole b){
		mbanker=null;
	}
	
	public void msgImLeaving(BankCustomerRole bc){
		Do("recieved message ImLeaving");
	    for (myTeller mt: tellers){
	        if( mt.bc==bc){                                                  
	            mt.bc=null;
	            mt.Occupied=false;
	            stateChanged();
	        }   
	    }   



	    
	       	if( mbanker.bc==bc){
	       		mbanker.bc=null;
	       		mbanker.Occupied=false;
	       		stateChanged();
	       	}   
	    
	    
	}
	
	
	
	
	
//TODO handle scenarios where not enough employees	
	

	public boolean pickAndExecuteAnAction() {
	
		
		for(myTeller mt:tellers){
			if(!mt.Occupied && !teller_bankCustomers.isEmpty()){
				
					assignTeller(mt);
					return true;	
				
				
			}
		}
		
		
		if(mbanker.Occupied && !banker_bankCustomers.isEmpty()){
			assignBanker(mbanker);
			return true;
		}
			
		if(!onDuty && teller_bankCustomers.isEmpty() && banker_bankCustomers.isEmpty()){
			closeBuilding();
			
		}
		
			
		
		return false;
	}

	
//Actions
	
	public boolean isOpen(){
		if(tellers.isEmpty() || mbanker==null || !onDuty ){
			return false;
		}
		return true;
		
	}
	
	
	private void assignTeller(myTeller mt){
	Do("assigning teller");
	    teller_bankCustomers.get(0).bc.msgGoToTeller(mt.t, mt.tellernumber);
	    mt.bc=teller_bankCustomers.get(0).bc;
	    mt.Occupied=true;
	    teller_bankCustomers.remove(0);
	}
	private void assignBanker(myBanker mb){
		Do("Assigning banker");
	    banker_bankCustomers.get(0).bc.msgGoToBanker(mb.b, mb.bankernumber);
	    mb.bc=banker_bankCustomers.get(0).bc;
	    mb.Occupied=true;
	    banker_bankCustomers.remove(0);
	}

	public void setBankAccounts(BankAccounts accounts){
		ba= accounts;
	}

	public boolean closeBuilding() {
		if(!teller_bankCustomers.isEmpty() || banker_bankCustomers.isEmpty()){
			return false;
		}
		
		
		//TODO remove tellers/bankers from list
		
		
		return false;
	}

	
	
	
}
