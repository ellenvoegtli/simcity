package mainCity.bank;
import java.util.List;

import mainCity.bank.BankAccounts.BankAccount;
import agent.Agent;


public class BankTeller extends Agent {

	BankAccounts ba;
	String name;
	myClient mc;
	public enum ClientState{withdrawing, depositing}
	
	public class myClient{
	    BankCustomer bc;
	    double accountnumber;
	    double amount;
	    ClientState cs;
	}

	//Messages
	
	public void msgIWantToDeposit(BankCustomer b, double accnum, int amount){
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		stateChanged();
	}
	
	public void msgIWantToWithdraw(BankCustomer b, double accnum, int amount){
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		stateChanged();
	}
	
	
	
	
	
	protected boolean pickAndExecuteAnAction() {
		
		if(mc!=null){
			if(mc.cs==ClientState.depositing){
				doDeposit(mc);
				return true;
			}
			
			if(mc.cs==ClientState.withdrawing){
				doWithdraw(mc);
				return true;
			}
		}
		
		
		
		return false;
	}
	
//Actions
	private void doDeposit(myClient mc){
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
				if(mc.accountnumber==b.accountNumber){
					b.balance+=mc.amount;
					mc.bc.msgRequestComplete(mc.amount*-1, b.balance);
					mc=null;
				}
			}
			
		}
		
	}
	
	private void doWithdraw(myClient mc){
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
				if(mc.accountnumber==b.accountNumber){
					b.balance-=mc.amount;
					mc.bc.msgRequestComplete(mc.amount, b.balance);
					mc=null;
				}
			}
			
		}
		
	}
	
	
	
	
	

}
