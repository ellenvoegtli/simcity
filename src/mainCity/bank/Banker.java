package mainCity.bank;

import mainCity.PersonAgent;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.BankTeller.ClientState;
import mainCity.bank.BankTeller.TellerState;
import mainCity.bank.BankTeller.myClient;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.gui.BankerGui;
import agent.Agent;


public class Banker extends Agent {
	public enum BankerState{none, atWork, offWork }
	BankerState bstate =BankerState.none;
	BankAccounts ba;
	String name;
	myClient mc;
	BankerGui bGui;
	public enum ClientState{none,wantsLoan, wantsAccount,done}
	
	public class myClient{
		PersonAgent p;
	    BankCustomer bc;
	    String mcname;
	    double accountnumber;
	    double amount;
	    ClientState cs=ClientState.none;
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
	public void msgGoToWork(){
		
		System.out.println("Banker at station");
		bstate=BankerState.atWork;
		stateChanged();
	}
	
	public void msgLeaveWork(){
		bstate=BankerState.offWork;
		stateChanged();
	}
	
	public void msgIWantALoan(BankCustomer b, double accnum, double amnt){
		Do("Recieved msgIWantALoan from customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsLoan;
		mc.accountnumber=accnum;
		stateChanged();
	}
	
	public void msgIWantNewAccount(PersonAgent p, BankCustomer b, String name, double amnt){
		Do("Recieved msgIWantNewAccount from customer");
		mc=new myClient();
		mc.p=p;
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsAccount;
		stateChanged();
	}
	
	
	
	protected boolean pickAndExecuteAnAction() {
		
		if(bstate==BankerState.atWork){
			bstate=BankerState.none;
			doGoToWork();
			return true;
		}
		if(bstate==BankerState.offWork){
			bstate=BankerState.none;
			doLeaveWork();
			return true;
		}
		
		if(mc!=null){
			
			if(mc.cs==ClientState.wantsAccount){
				createAccount(mc);
				mc.cs=ClientState.done;
				return true;
			}
			
			if(mc.cs==ClientState.wantsLoan){
				processLoan(mc);
				mc.cs=ClientState.done;
				return true;
			}
			
			
		}
		
		
		
		
		
		return false;
	}
	
//Actions
	private void doGoToWork(){
		bGui.doGoToWork();
			
			
		}
		
	private void doLeaveWork(){
			bGui.doLeaveWork();
		}
		
		
	
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
		if(mc.accountnumber==-1){
			mc.bc.msgLoanDenied(mc.amount);
			Do("Loan denied, no account exists");
			return;
		}
		for(BankAccount b: ba.accounts){
			if(mc.accountnumber==b.accountNumber){
				if(b.creditScore>=600){
					b.debt+=mc.amount;
					mc.bc.msgLoanApproved(mc.amount);
					Do("Loan approved");
					return;
				}
		
			}		
		}//end for
		//else
			
			mc.bc.msgLoanDenied(mc.amount);
			Do("Loan denied");
			return;
		
	}
	public void setGui(BankerGui gui){
		this.bGui=gui;
	}
	
	
	
}
