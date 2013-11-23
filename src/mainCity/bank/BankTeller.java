package mainCity.bank;
import java.util.List;

import mainCity.bank.BankAccounts.BankAccount;
import agent.Agent;


public class BankTeller extends Agent {

	BankAccounts ba;
	String name;
	myClient mc;
	public enum ClientState{withdrawing, depositing, talking}
	
	public class myClient{
	    BankCustomer bc;
	    double accountnumber;
	    double amount;
	    ClientState cs;
	}

	public BankTeller(String name){
		super();
		this.name=name;
		Do("Bank Teller initiated");
	}
	
	public void setBankAccounts(BankAccounts ba){
		this.ba=ba;
	}
	
	//Messages
	
	public void msgIWantToDeposit(BankCustomer b, double accnum, int amount){
		Do("recieved msgIWantToDeposit from a customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		mc.cs=ClientState.depositing;
		stateChanged();
	}
	
	public void msgIWantToWithdraw(BankCustomer b, double accnum, int amount){
		Do("recieved msgIWantToWithdraw from a customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		mc.cs=ClientState.withdrawing;
		stateChanged();
	}
	
	
	
	
	
	protected boolean pickAndExecuteAnAction() {
		
		if(mc!=null){
			if(mc.cs==ClientState.depositing){
				mc.cs=ClientState.talking;
				doDeposit(mc);
				return true;
			}
			
			if(mc.cs==ClientState.withdrawing){
				mc.cs=ClientState.talking; 
				doWithdraw(mc);
				return true;
			}
		}
		
		
		
		return false;
	}
	
//Actions
	private void doDeposit(myClient mc){
		Do("doing deposit");
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
				if(mc.accountnumber==b.accountNumber){
					b.balance+=mc.amount;
					mc.bc.msgRequestComplete(mc.amount*-1, b.balance);
					mc=null;
					return;
				}
			}
			
		}
		
	}
	
	private void doWithdraw(myClient mc){
		Do("doing withdraw");
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
				if(mc.accountnumber==b.accountNumber){
					b.balance-=mc.amount;
					mc.bc.msgRequestComplete(mc.amount, b.balance);
					mc=null;
					return;
				}
			}
			
		}
		
	}
	
	
	
	
	

}
