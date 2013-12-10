package mainCity.bank;
import java.util.List;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankTeller;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.WorkerRole;
import agent.Agent;


public class BankTellerRole extends Role implements WorkerRole, BankTeller {
	
	
	
	PersonAgent person;
	
	public BankAccounts bankaccounts;
	String name;
	public myClient myclient;
	private int tellernumber;
	BankTellerGui btGui;
	public enum ClientState{withdrawing, depositing, talking}
	public boolean onDuty;
	
	public class myClient{
	    public BankCustomer bc;
	    public double accountnumber;
	    public double amount;
	    public ClientState cs;
	}

	public BankTellerRole(PersonAgent p, String name){
		super(p);
		this.person=p;
		this.name=name;
		log("Bank Teller initiated");
		onDuty=true;
	}
	
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.getName(), s);
	}
	

	public void setBankAccounts(BankAccounts ba){
		this.bankaccounts=ba;
	}
	
	
	public void setTellerNumber(int tn){
		this.tellernumber=tn;
	}
	
	//Messages
	
	
	public void msgGoOffDuty(double amount) {
		
		addToCash(amount);
		
		onDuty = false;
		stateChanged();
		
	}
	
	
	public void msgIWantToDeposit(BankCustomer b, double accnum, int amount){
		log("recieved msgIWantToDeposit from a customer");
		myclient=new myClient();
		myclient.bc=b;
		myclient.amount=amount;
		myclient.accountnumber=accnum;
		myclient.cs=ClientState.depositing;
		stateChanged();
	}
	
	
	public void msgIWantToWithdraw(BankCustomer b, double accnum, int amount){
		log("recieved msgIWantToWithdraw from a customer");
		myclient=new myClient();
		myclient.bc=b;
		myclient.amount=amount;
		myclient.accountnumber=accnum;
		myclient.cs=ClientState.withdrawing;
		stateChanged();
	}
	
	
	public boolean pickAndExecuteAnAction() {
		if(onDuty){
			
			doGoToWork();
			
		}
		
		
		if(myclient!=null){
			if(myclient.cs==ClientState.depositing){
				myclient.cs=ClientState.talking;
				doDeposit(myclient);
				return true;
			}
			
			if(myclient.cs==ClientState.withdrawing){
				myclient.cs=ClientState.talking; 
				doWithdraw(myclient);
				return true;
			}
		}
		
		if(!onDuty && myclient==null){
			doLeaveWork();
			return true;
		}
		
		
		return false;
	}
	
//Actions
	private void doGoToWork(){
	btGui.doGoToWork(tellernumber);
		
		
	}
	
	private void doLeaveWork(){
		btGui.doLeaveWork();
		setInactive();
		onDuty=true;
	}
	
	
	
	private void doDeposit(myClient mc){
		log("doing deposit");
		synchronized (bankaccounts.accounts) {
			
			for(BankAccount b: bankaccounts.accounts){
				if(mc.accountnumber==b.accountNumber){
					//handles repaying of loans
					if(b.debt>0){
						b.debt -=mc.amount*.5;
						b.balance+=mc.amount*.5;
						b.creditScore+=5;
						mc.bc.msgRequestComplete(mc.amount*-1, b.balance);
						mc=null;
						
						return;
					}
					
					
					b.balance+=mc.amount;
					mc.bc.msgRequestComplete(mc.amount*-1, b.balance);
					mc=null;
					return;
				}
			}
			
		}
		
	}
	
	private void doWithdraw(myClient mc){
		log("doing withdraw");
		synchronized (bankaccounts.accounts) {
			
			for(BankAccount b: bankaccounts.accounts){
				if(mc.accountnumber==b.accountNumber){
					if(b.balance < mc.amount){
						mc.bc.msgRequestComplete(b.balance, 0);
						//mc.bc.msgNeedLoan();
						b.balance=0;
						b.creditScore-=10;
						mc=null;
						return;	
					}
					b.balance-=mc.amount;
					mc.bc.msgRequestComplete(mc.amount, b.balance);
					mc=null;
					return;
				}
			}
			
		}
		
	}
	
	
	public void setGui(BankTellerGui gui){
		//Do("gui set");
		this.btGui=gui;
	}


	
	
	

}
