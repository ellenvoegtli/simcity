package mainCity.bank;

import java.util.ArrayList;
import java.util.List;

import agent.Agent;

public class BankManager extends Agent {

	String name;
	public List <myTeller> tellers= new ArrayList<myTeller>();
	public List <myBanker> bankers = new ArrayList<myBanker>();
	public List <BankCustomer>  teller_bankCustomers = new ArrayList<BankCustomer>();
	public List <BankCustomer>  banker_bankCustomers = new ArrayList<BankCustomer>();

	public class myTeller{
	    BankTeller t;
	    int tellernumber;
	    BankCustomer bc;
	    boolean Occupied;
	}

	public class myBanker{
	    Banker b;
	    int bankernumber;
	    BankCustomer bc;
	    boolean Occupied;
	}
	
	
	
	//Messages
	public void msgDirectDeposit(double accountNumber, int amount){
		//TODO what parameter should be used to identify a person? is accountNumber too private?
		
	}
	
	public void msgIWantToDeposit( BankCustomer bc){
	    teller_bankCustomers.add(bc);
	}

	public void msgIWantToWithdraw( BankCustomer bc){
	    teller_bankCustomers.add(bc);
	}


	public void msgIWantALoan(BankCustomer bc){
	    banker_bankCustomers.add(bc);
	}

	public void msgImLeaving(BankCustomer bc){
	    for (myTeller mt: tellers){
	        if( mt.bc==bc){                                                  
	            mt.bc=null;
	            mt.Occupied=false;
	        }   
	    }   



	    for (myBanker mb: bankers){
	        if( mb.bc==bc){
	            mb.bc=null;
	            mb.Occupied=false;
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
	    teller_bankCustomers.get(0).msgGoToTeller(mt.t, mt.tellernumber);
	    mt.Occupied=true;
	}
	private void assignBanker(myBanker mb){
	    banker_bankCustomers.get(0).msgGoToBanker(mb.b, mb.bankernumber);
	    mb.Occupied=true;
	}
	
	
	
	
	
}
