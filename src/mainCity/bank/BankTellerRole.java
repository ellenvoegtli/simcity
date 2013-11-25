package mainCity.bank;
import java.util.List;

import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.gui.BankTellerGui;
import agent.Agent;


public class BankTellerRole extends Agent {
	
	public enum TellerState{none, atWork, offWork }
	
	TellerState tstate =TellerState.none;
	BankAccounts ba;
	String name;
	myClient mc;
	private int tellernumber;
	BankTellerGui btGui;
	public enum ClientState{withdrawing, depositing, talking}
	
	public class myClient{
	    BankCustomerRole bc;
	    double accountnumber;
	    double amount;
	    ClientState cs;
	}

	public BankTellerRole(String name){
		super();
		this.name=name;
		Do("Bank Teller initiated");
	}
	
	public void setBankAccounts(BankAccounts ba){
		this.ba=ba;
	}
	
	public void setTellerNumber(int tn){
		this.tellernumber=tn;
	}
	
	//Messages
	public void msgGoToWork(){
		Do("tellernumber is" + tellernumber);
		System.out.println("Teller at station");
		tstate=TellerState.atWork;
		stateChanged();
	}
	
	public void msgLeaveWork(){
		tstate=TellerState.offWork;
		stateChanged();
	}
	
	public void msgIWantToDeposit(BankCustomerRole b, double accnum, int amount){
		Do("recieved msgIWantToDeposit from a customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		mc.cs=ClientState.depositing;
		stateChanged();
	}
	
	public void msgIWantToWithdraw(BankCustomerRole b, double accnum, int amount){
		Do("recieved msgIWantToWithdraw from a customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		mc.cs=ClientState.withdrawing;
		stateChanged();
	}
	
	
	
	
	
	protected boolean pickAndExecuteAnAction() {
		if(tstate==TellerState.atWork){
			tstate=TellerState.none;
			doGoToWork();
			return true;
		}
		if(tstate==TellerState.offWork){
			tstate=TellerState.none;
			doLeaveWork();
			return true;
		}
		
		
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
	private void doGoToWork(){
	btGui.doGoToWork(tellernumber);
		
		
	}
	
	private void doLeaveWork(){
		btGui.doLeaveWork();
	}
	
	
	
	private void doDeposit(myClient mc){
		Do("doing deposit");
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
				if(mc.accountnumber==b.accountNumber){
					//handles repaying of loans
					if(b.debt>0){
						b.debt -=mc.amount*.5;
						b.balance+=mc.amount*.5;
						b.creditScore+=5;
						mc.bc.msgRequestComplete(mc.amount*-1, b.balance);
						
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
		Do("doing withdraw");
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
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
		this.btGui=gui;
	}
	
	
	

}
