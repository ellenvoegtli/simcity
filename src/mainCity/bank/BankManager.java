package mainCity.bank;

import java.util.ArrayList;
import java.util.List;

import agent.Agent;

public class BankManager extends Agent {

	String name;
	public List <myTeller> tellers= new ArrayList<myTeller>();
	public List <myBanker> bankers = new ArrayList<myBanker>();
	public List <myBankCustomer>  teller_bankCustomers = new ArrayList<myBankCustomer>();
	public List <myBankCustomer>  banker_bankCustomers = new ArrayList<myBankCustomer>();

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
	    Banker b;
	    int bankernumber;
	    BankCustomer bc;
	    boolean Occupied;
	    public myBanker(Banker ba){
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
	
	public BankManager(String name){
		super();
		this.name=name;
		Do("Bank Manager instantiated");
		
	}
	
	//Messages
	public void msgTellerAdded(BankTeller bt){
		tellers.add(new myTeller(bt,tellers.size()));
	}
	
	public void msgDirectDeposit(double accountNumber, int amount){
		//TODO what parameter should be used to identify a person? is accountNumber too private?
		
	}
	
	public void msgIWantToDeposit( BankCustomer bc){
		Do("recieved message IWantToDeposit");
	    teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}

	public void msgIWantToWithdraw( BankCustomer bc){
		Do("recieved message IWantToWithdraw");
		teller_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}
	
	public void msgIWantNewAccount(BankCustomer bc) {
		Do("recieved message want new account");
		banker_bankCustomers.add(new myBankCustomer(bc));
		stateChanged();
		
	}
	
	

	public void msgIWantALoan(BankCustomer bc){
		Do("recieved message IWantALoan");
		banker_bankCustomers.add(new myBankCustomer(bc));
	    stateChanged();
	}
//TODO fix this
	public void msgImLeaving(BankCustomer bc){
		Do("recieved message ImLeaving");
	    for (myTeller mt: tellers){
	        if( mt.bc==bc){                                                  
	            mt.bc=null;
	            mt.Occupied=false;
	            stateChanged();
	        }   
	    }   



	    for (myBanker mb: bankers){
	        if( mb.bc==bc){
	            mb.bc=null;
	            mb.Occupied=false;
	            stateChanged();
	        }   
	    }
	    
	}
	
	
	
	
	
	
	
	protected boolean pickAndExecuteAnAction() {
		for(myTeller mt:tellers){
			if(!mt.Occupied && !teller_bankCustomers.isEmpty()){
				
					assignTeller(mt);
					return true;	
				
				
			}
		}
		
		for(myBanker mb: bankers){
			if(!mb.Occupied && !banker_bankCustomers.isEmpty()){
				assignBanker(mb);
				return true;
			}
			
		}
			
		
		return false;
	}

	
//Actions
	
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


	
	
	
}
