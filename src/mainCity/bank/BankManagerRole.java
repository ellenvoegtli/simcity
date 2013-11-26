package mainCity.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import role.Role;
import mainCity.PersonAgent;
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
	PersonAgent p;
	private BankAccounts ba;
	public List <myTeller> tellers= Collections.synchronizedList(new ArrayList<myTeller>());
	public myBanker mbanker;
	public List <myBankCustomer>  teller_bankCustomers = Collections.synchronizedList(new ArrayList<myBankCustomer>());
	public List <myBankCustomer>  banker_bankCustomers = Collections.synchronizedList(new ArrayList<myBankCustomer>());
	public boolean onDuty;

	public static class myTeller{
	    BankTeller t;
	    int tellernumber;
	    BankCustomer bc;
	    boolean Occupied;
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
		BankCustomer bc;
		public myBankCustomer(BankCustomer bc){
			this.bc=bc;
		}
	}
	
	public BankManagerRole(PersonAgent p, String name){
		super(p);
		this.p=p;
		this.name=name;
		onDuty=true;
		log("Bank Manager instantiated");
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#setBanker(mainCity.bank.BankerRole)
	 */
	@Override
	public void setBanker(BankerRole br){
		mbanker=new myBanker(br);
	}
	
	//Messages
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgBankerAdded()
	 */
	
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_MANAGER, this.getName(), s);
	}
	@Override
	public void msgBankerAdded(){
		stateChanged();
	}
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgEndShift()
	 */
	@Override
	public void msgEndShift() {
		/*
		Do("bank closing");
		onDuty = false;
		stateChanged();
		*/
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgTellerAdded(mainCity.bank.BankTellerRole)
	 */
	@Override
	public void msgTellerAdded(BankTeller bt){
		tellers.add(new myTeller(bt,tellers.size()));
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgDirectDeposit(double, int)
	 */
	@Override
	public void msgDirectDeposit(double accountNumber, int amount){
		synchronized (ba.accounts) {
			for(BankAccount account:ba.accounts){
				if(account.accountNumber == accountNumber){
					account.balance+=amount;
					log("Direct deposit performed. $" + amount + " was deposited for " + account.name +" with new balance of $" + account.balance);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgIWantToDeposit(mainCity.bank.interfaces.BankCustomer)
	 */
	@Override
	public void msgIWantToDeposit( BankCustomer bc){
		log("recieved message IWantToDeposit");
	    teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgIWantToWithdraw(mainCity.bank.interfaces.BankCustomer)
	 */
	@Override
	public void msgIWantToWithdraw( BankCustomer bc){
		log("recieved message IWantToWithdraw");
		teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgIWantNewAccount(mainCity.bank.interfaces.BankCustomer)
	 */
	@Override
	public void msgIWantNewAccount(BankCustomer bc) {
		log("recieved message want new account");
		banker_bankCustomers.add(new myBankCustomer(bc));
		stateChanged();
		
	}
	
	

	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgIWantALoan(mainCity.bank.interfaces.BankCustomer)
	 */
	@Override
	public void msgIWantALoan(BankCustomer bc){
		log("recieved message IWantALoan");
		banker_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgImLeaving(mainCity.bank.BankTellerRole)
	 */
	@Override
	public void msgImLeaving(BankTeller bt){
		for (myTeller mt: tellers){
			if(mt.t==bt){
				tellers.remove(mt);
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgImLeaving(mainCity.bank.interfaces.Banker)
	 */
	@Override
	public void msgImLeaving(Banker b){
		mbanker=null;
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#msgImLeaving(mainCity.bank.interfaces.BankCustomer)
	 */
	@Override
	public void msgImLeaving(BankCustomer bc){
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
	

	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#pickAndExecuteAnAction()
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
	
		
		for(myTeller mt:tellers){
			if(!mt.Occupied && !teller_bankCustomers.isEmpty()){
				
					assignTeller(mt);
					return true;	
				
				
			}
		}
		
		
		if(!mbanker.Occupied && !banker_bankCustomers.isEmpty()){
			assignBanker(mbanker);
			return true;
		}
			
		if(!onDuty && teller_bankCustomers.isEmpty() && banker_bankCustomers.isEmpty()){
			closeBuilding();
			
		}
		
			
		
		return false;
	}

	
//Actions
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#isOpen()
	 */
	@Override
	public boolean isOpen(){
		log("bank manager checking open");
		if(tellers.isEmpty() || mbanker.b==null || !onDuty || !mbanker.b.isActive()){
			System.out.println("false");
			return false;
		}
		System.out.println("true");
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

	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#setBankAccounts(mainCity.bank.BankAccounts)
	 */
	@Override
	public void setBankAccounts(BankAccounts accounts){
		ba= accounts;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankManager#closeBuilding()
	 */
	@Override
	public boolean closeBuilding() {
		if(!teller_bankCustomers.isEmpty() || banker_bankCustomers.isEmpty()){
			return false;
		}
		
		
		//TODO remove tellers/bankers from list
		
		
		return false;
	}

	
	
	
}
