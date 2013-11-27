package mainCity.bank;

import role.Role;
import mainCity.bank.BankAccounts.BankAccount;
import mainCity.bank.BankTellerRole.ClientState;
import mainCity.bank.BankTellerRole.myClient;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.gui.BankerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.Banker;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.WorkerRole;
import agent.Agent;


public class BankerRole extends Role implements WorkerRole, Banker {
	
	
	public BankAccounts ba;
	String name;
	public myClient mc;
	BankerGui bGui;
	private PersonAgent p;
	public enum ClientState{none,wantsLoan, wantsAccount,done}
	private boolean onDuty;
	
	public class myClient{
		public PersonAgent p;
	    public BankCustomer bc;
	    String mcname;
	    double accountnumber;
	    double amount;
	    
	    public ClientState cs=ClientState.none;
	}
	
	
	public BankerRole(PersonAgent p, String name){
		super(p);
		this.p=p;
		this.name=name;
		log("Banker initiated");
		onDuty=true;
	}
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#setBankAccounts(mainCity.bank.BankAccounts)
	 */
	@Override
	public void setBankAccounts(BankAccounts singular){
		this.ba=singular;
	}
	
	
//Messages
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#msgGoOffDuty(double)
	 */
	@Override
	public void msgGoOffDuty(double d){
		addToCash(d);
		onDuty=false;
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#msgGoToWork()
	 */
	@Override
	public void msgGoToWork(){
		
		log("Banker at station");
		onDuty=true;
		stateChanged();
	}
	

	
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#msgIWantALoan(mainCity.bank.interfaces.BankCustomer, double, double)
	 */
	@Override
	public void msgIWantALoan(BankCustomer b, double accnum, double amnt){
		log("Recieved msgIWantALoan from customer");
		mc=new myClient();
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsLoan;
		mc.accountnumber=accnum;
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#msgIWantNewAccount(mainCity.PersonAgent, mainCity.bank.interfaces.BankCustomer, java.lang.String, double)
	 */
	@Override
	public void msgIWantNewAccount(PersonAgent p, BankCustomer b, String name, double amnt){
		log("Recieved msgIWantNewAccount from customer");
		mc=new myClient();
		mc.mcname=name;
		mc.p=p;
		mc.bc=b;
		mc.amount=amnt;
		mc.cs=ClientState.wantsAccount;
		stateChanged();
	}
	
	
	
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#pickAndExecuteAnAction()
	 */
	@Override
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
		log("Creating new account");
		double temp =ba.getNumberOfAccounts();
		ba.addAccount(mc.mcname, mc.amount, mc.p, temp);
		mc.bc.msgAccountCreated(temp);
		
		mc.bc.msgRequestComplete(mc.amount*-1, mc.amount);
		mc=null;
	}
	
	
	private void processLoan(myClient mc){
		log("processing loan");
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
	/* (non-Javadoc)
	 * @see mainCity.bank.Banker#setGui(mainCity.bank.gui.BankerGui)
	 */
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_BANKER, this.getName(), s);
	}
	@Override
	public void setGui(BankerGui gui){
		this.bGui=gui;
	}
	
	
	
}
