package role.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.BankAccounts;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankManager;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.ManagerRole;
import agent.Agent;

public class BankManagerRole extends Role implements ManagerRole, BankManager {

	String name;
	PersonAgent person;
	private BankAccounts baccounts;
	public List <myTeller> tellers= Collections.synchronizedList(new ArrayList<myTeller>());
	public myBanker mbanker;
	public List <myBankCustomer>  teller_bankCustomers = Collections.synchronizedList(new ArrayList<myBankCustomer>());
	public List <myBankCustomer>  banker_bankCustomers = Collections.synchronizedList(new ArrayList<myBankCustomer>());
	public boolean onDuty;
	
	
	BankRobberRole robber=null;
	double robberdemand =0;

	public static class myTeller{
	    BankTeller t;
	    int tellernumber;
	    public BankCustomer bc;
	    public boolean Occupied;
	    public int gettellernumber(){
	    	return tellernumber;
	    }
	    
	    public void settellernumber(int tn){
	    	tellernumber=tn;
	    }
	    
	    public myTeller(BankTeller bt, int tn){
	    	t=bt;
	    	Occupied=false;
	    	tellernumber=tn;
	    }
	}

	public static class myBanker{
	    BankerRole b;
	    int bankernumber;
	    BankCustomer bc;
	    boolean Occupied;
	    public myBanker(BankerRole ba){
	    	b=ba;
	    	Occupied=false;
	    }
	}
	
	public static class myBankCustomer{
		public BankCustomer bc;
		public myBankCustomer(BankCustomer bc){
			this.bc=bc;
		}
	}
	
	public BankManagerRole(PersonAgent p, String name){
		super(p);
		this.person=p;
		this.name=name;
		onDuty=true;
		log("Bank Manager instantiated");
		
	}
	
	
	public void setBanker(BankerRole br){
		mbanker=new myBanker(br);
	}
	
	//Messages
	
	
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_MANAGER, this.getName(), s);
	}
	@Override
	public void msgBankerAdded(){
		stateChanged();
	}


	public void msgEndShift() {
		
		log("BANK CLOSING");
		log("BANK CLOSING");
		log("BANK CLOSING");
		log("BANK CLOSING");
		log("BANK CLOSING");
		log("BANK CLOSING");
		onDuty = false;
		stateChanged();
		
	}
	
	
	public void msgGiveMeTheMoney(BankRobberRole bankRobber, double amount) {
		robber=bankRobber;
		robberdemand = amount;
		
		stateChanged();
		
	}
	
	public void msgTellerAdded(BankTeller bt){
		tellers.add(new myTeller(bt,tellers.size()));
	}
	
	
	public void msgDirectDeposit(double accountNumber, int amount){
		synchronized (baccounts.accounts) {
			for(BankAccount account:baccounts.accounts){
				if(account.accountNumber == accountNumber){
					account.balance+=amount;
					log("Direct deposit performed. $" + amount + " was deposited for " + account.name +" with new balance of $" + account.balance);
				}
			}
		}
	}
	
	
	public void msgIWantToDeposit( BankCustomer bc){
		log("recieved message IWantToDeposit");
	    teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	
	public void msgIWantToWithdraw( BankCustomer bc){
		log("recieved message IWantToWithdraw");
		teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}
	
	
	public void msgIWantNewAccount(BankCustomer bc) {
		log("recieved message want new account");
		banker_bankCustomers.add(new myBankCustomer(bc));
		stateChanged();
		
	}
	
	

	
	public void msgIWantALoan(BankCustomer bc){
		log("recieved message IWantALoan");
		banker_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	
	public void msgImLeaving(BankTeller bt){
		for (myTeller mt: tellers){
			if(mt.t==bt){
				tellers.remove(mt);
			}
		}
		
	}
	
	
	public void msgImLeaving(Banker b){
		mbanker=null;
	}
	
	
	public void msgImLeaving(BankCustomer bc){
		log("recieved message ImLeaving");
	    for (myTeller mt: tellers){
	        if( mt.bc==bc){                                                  
	            mt.bc=null;
	            mt.Occupied=false;
	            stateChanged();
	            return;
	        }   
	    }   



	    
	       	if( mbanker.bc==bc){
	       		mbanker.bc=null;
	       		mbanker.Occupied=false;
	       		stateChanged();
	       	}   
	    
	    
	}
	
	
	
	
/*SCHEDULER*/

	

	public boolean pickAndExecuteAnAction() {
	
		
		for(myTeller mt:tellers){
			if(!mt.Occupied && !teller_bankCustomers.isEmpty()){
				
					assignTeller(mt);
					return true;	
				
				
			}
		}
		
		
		if(mbanker!=null &&!mbanker.Occupied && !banker_bankCustomers.isEmpty()){
			assignBanker(mbanker);
			return true;
		}
			
		if(!onDuty && teller_bankCustomers.isEmpty() && banker_bankCustomers.isEmpty()){
			closeBuilding();
			
		}
		//If robber exists
		if(robber!=null){
			payRobber();
			
		}
		
			
		
		return false;
	}

	
//Actions
	
	private void payRobber(){
		robber.msgOkayDontHurtMe(robberdemand);
		robber=null;
		//Detract from FDIC insurance fund
		//Bank is insured up to 250,000 
		baccounts.FDICfund-=robberdemand;
	}
	
	
	
	public boolean isOpen(){
		log("bank manager checking open");
		if(tellers.isEmpty() || mbanker.b==null || !onDuty || !mbanker.b.isActive()){
			log("Open = false");
			return false;
		}
		log("Open = true");
		return true;
		
	}
	
	
	private void assignTeller(myTeller mt){
	log("assigning teller");
	    teller_bankCustomers.get(0).bc.msgGoToTeller(mt.t, mt.tellernumber);
	    mt.bc=teller_bankCustomers.get(0).bc;
	    mt.Occupied=true;
	    teller_bankCustomers.remove(0);
	}
	private void assignBanker(myBanker mb){
		log("Assigning banker");
	    banker_bankCustomers.get(0).bc.msgGoToBanker(mb.b, mb.bankernumber);
	    mb.bc=banker_bankCustomers.get(0).bc;
	    mb.Occupied=true;
	    banker_bankCustomers.remove(0);
	}

	
	public void setBankAccounts(BankAccounts accounts){
		baccounts= accounts;
	}

	
	public boolean closeBuilding() {
		if(!teller_bankCustomers.isEmpty() || !banker_bankCustomers.isEmpty()){
			return false;
		}
		
		double payroll = 0;
		
		for (myTeller mt : tellers){
			BankTellerRole temp = (BankTellerRole) mt.t;
			double amount = temp.getShiftDuration()*50;
			temp.msgGoOffDuty(amount);
			payroll+=amount;
		}
		
		if (mbanker!= null){
			BankerRole temp = (BankerRole)mbanker.b;
			double amount = temp.getShiftDuration();
			temp.msgGoOffDuty(amount);
			payroll+=amount;
		}

		addToCash(getShiftDuration()*100.00);
		payroll += getShiftDuration()*100.00;	
		
		//Embezzle Funds here
		baccounts.FDICfund-= payroll;
		setInactive();
		onDuty = true;
		return true;
		
	}





	
	
	
}
