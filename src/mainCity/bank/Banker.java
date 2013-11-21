package mainCity.bank;

import mainCity.PersonAgent;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.BankTeller.ClientState;
import mainCity.bank.BankTeller.myClient;
import agent.Agent;


public class Banker extends Agent {
	BankAccounts ba;
	String name;
	myClient mc;
	public enum ClientState{wantsLoan, wantsAccount}
	
	public class myClient{
		PersonAgent p;
	    BankCustomer bc;
	    String mcname;
	    double accountnumber;
	    double amount;
	    ClientState cs;
	}
	
	
	public Banker(String name){
		super();
		this.name=name;
		Do("Bank Teller initiated");
	}
	public void setBankAccounts(BankAccounts singular){
		this.ba=singular;
	}
	
	
//Messages
	
	public void msgIWantALoan(BankCustomer b, double accnum, double amnt){
		Do("Recieved msgIWantALoan from customer");
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsLoan;
		mc.accountnumber=accnum;
		stateChanged();
	}
	
	public void msgIWantNewAccount(PersonAgent p, BankCustomer b, String name, double amnt){
		Do("Recieved msgIWantNewAccount from customer");
		mc.p=p;
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsAccount;
		stateChanged();
	}
	
	
	
	protected boolean pickAndExecuteAnAction() {
		
		if(mc!=null){
			
			if(mc.cs==ClientState.wantsAccount){
				createAccount(mc);
				return true;
			}
			
			if(mc.cs==ClientState.wantsLoan){
				processLoan(mc);
				return true;
			}
			
			
		}
		
		
		
		
		
		return false;
	}
	
//Actions
	
	private void createAccount(myClient mc){
		Do("Creating new account");
		double temp =ba.getNumberOfAccounts();
		ba.addAccount(mc.mcname, mc.amount, mc.p, temp);
		mc.bc.msgAccountCreated(temp);
		mc.bc.msgRequestComplete(mc.amount*-1, mc.amount);
		mc=null;
	}
	
	
	private void processLoan(myClient mc){
		Do("processing loan");
		for(BankAccount b: ba.accounts){
			if(mc.accountnumber==b.accountNumber){
				if(b.creditScore>=600){
					b.debt+=mc.amount;
					mc.bc.msgLoanApproved(mc.amount);
					return;
				}
				else{
					
					mc.bc.msgLoanDenied(mc.amount);
					return;
				}
				
				
			}
			
			
		}
		
	}
	
	
}
