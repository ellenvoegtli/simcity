package mainCity.bank;

import role.Role;
import mainCity.Person;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.BankTellerRole.ClientState;
import mainCity.bank.BankTellerRole.TellerState;
import mainCity.bank.BankTellerRole.myClient;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.gui.BankerGui;
import mainCity.interfaces.WorkerRole;
import agent.Agent;


public class BankerRole extends Role implements WorkerRole {
	
	
	BankAccounts ba;
	String name;
	myClient mc;
	BankerGui bGui;
	private Person p;
	public enum ClientState{none,wantsLoan, wantsAccount,done}
	private boolean onDuty;
	
	public class myClient{
		Person p;
	    BankCustomerRole bc;
	    String mcname;
	    double accountnumber;
	    double amount;
	    
	    ClientState cs=ClientState.none;
	}
	
	
	public BankerRole(Person p, String name){
		super(p);
		this.p=p;
		this.name=name;
		Do("Bank Teller initiated");
		onDuty=true;
	}
	public void setBankAccounts(BankAccounts singular){
		this.ba=singular;
	}
	
	
//Messages
	public void msgGoOffDuty(double d){
		addToCash(d);
		onDuty=false;
		stateChanged();
	}
	
	public void msgGoToWork(){
		
		System.out.println("Banker at station");
		onDuty=true;
		stateChanged();
	}
	

	
	public void msgIWantALoan(BankCustomerRole b, double accnum, double amnt){
		Do("Recieved msgIWantALoan from customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsLoan;
		mc.accountnumber=accnum;
		stateChanged();
	}
	
	public void msgIWantNewAccount(Person p, BankCustomerRole b, String name, double amnt){
		Do("Recieved msgIWantNewAccount from customer");
		mc=new myClient();
		mc.p=p;
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsAccount;
		stateChanged();
	}
	
	
	
	public boolean pickAndExecuteAnAction() {
		if(onDuty){
			doGoToWork();
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
		
		if(!onDuty && mc ==null){
			doLeaveWork();
			return false;
			
		}
		
		
		
		return false;
	}
	
//Actions
	private void doGoToWork(){
		bGui.doGoToWork();
			
			
		}
		
	private void doLeaveWork(){
			bGui.doLeaveWork();
			setInactive();
			onDuty=true;
		}
		
		
	
	private void createAccount(myClient mc){
		Do("Creating new account");
		double temp =ba.getNumberOfAccounts();
		ba.addAccount(mc.mcname, mc.amount, mc.p, temp);
		mc.bc.msgAccountCreated(temp);
		if(mc.bc.getBankbalance()< 50){
			processLoan(mc);
			return;
		}
		mc.bc.msgRequestComplete(mc.amount*-1, mc.amount);
		mc=null;
	}
	
	
	private void processLoan(myClient mc){
		Do("processing loan");
		if(mc.accountnumber==-1){
			createAccount(mc);
		}
		for(BankAccount b: ba.accounts){
			if(mc.accountnumber==b.accountNumber){
				if(b.debt>=500){
					b.creditScore-=20;
				}
				if(b.debt<=500){
					b.creditScore+=20;
				}
				if(b.creditScore>=600){
					b.debt+=mc.amount;
					mc.bc.msgLoanApproved(mc.amount);
					Do("Loan approved");
					mc=null;
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
