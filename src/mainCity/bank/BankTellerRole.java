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
	
	
	
	PersonAgent p;
	
	public BankAccounts ba;
	String name;
	public myClient mc;
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
		this.p=p;
		this.name=name;
		Do("Bank Teller initiated");
		onDuty=true;
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#setBankAccounts(mainCity.bank.BankAccounts)
	 */
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.getName(), s);
	}
	@Override
	public void setBankAccounts(BankAccounts ba){
		this.ba=ba;
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#setTellerNumber(int)
	 */
	@Override
	public void setTellerNumber(int tn){
		this.tellernumber=tn;
	}
	
	//Messages
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#msgGoOffDuty(double)
	 */
	@Override
	public void msgGoOffDuty(double amount) {
		
		addToCash(amount);
		
		onDuty = false;
		stateChanged();
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#msgIWantToDeposit(mainCity.bank.interfaces.BankCustomer, double, int)
	 */
	@Override
	public void msgIWantToDeposit(BankCustomer b, double accnum, int amount){
		log("recieved msgIWantToDeposit from a customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		mc.cs=ClientState.depositing;
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#msgIWantToWithdraw(mainCity.bank.interfaces.BankCustomer, double, int)
	 */
	@Override
	public void msgIWantToWithdraw(BankCustomer b, double accnum, int amount){
		log("recieved msgIWantToWithdraw from a customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amount;
		mc.accountnumber=accnum;
		mc.cs=ClientState.withdrawing;
		stateChanged();
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#pickAndExecuteAnAction()
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		if(onDuty){
			
			doGoToWork();
			
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
		
		if(!onDuty && mc==null){
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
		synchronized (ba.accounts) {
			
			for(BankAccount b: ba.accounts){
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
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankTeller#setGui(mainCity.bank.gui.BankTellerGui)
	 */
	@Override
	public void setGui(BankTellerGui gui){
		//Do("gui set");
		this.btGui=gui;
	}


	
	
	

}
