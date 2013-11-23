package mainCity.bank;

import java.util.concurrent.Semaphore;

import mainCity.PersonAgent;
import mainCity.bank.gui.BankCustomerGui;
import agent.Agent;

public class BankCustomer extends Agent {
	PersonAgent p;
	String name;
	BankManager bm;
	Banker b;
	int bankernumber;
	BankTeller t;
	int tellernumber;
	//customer should know how much money he has beforehand
	private double myaccountnumber;
	private double bankbalance;
	private BankCustomerGui custGui;
	
	private Semaphore atHome = new Semaphore(0,false);
	private Semaphore atWaiting = new Semaphore(0,false);
	private Semaphore atTeller = new Semaphore(0, false);
	private Semaphore atBanker = new Semaphore(0,false);
	
	
	//will be used for all transactions, loan requests, etc
	private int amount;
	
	public enum BankCustomerTransactionState{ none,wantToDeposit, wantToWithdraw, wantNewAccount, wantLoan}

	BankCustomerTransactionState tstate=BankCustomerTransactionState.none;
	
	public enum BankCustomerState{ none,waitingInBank, atTeller, atBanker, assignedTeller, assignedBanker, goingToTeller, goingToBanker, talking, done, leaving,left
	}
	
	BankCustomerState bcstate=BankCustomerState.none;
	
	public BankCustomer(PersonAgent p,String name){
		Do("bank customer initiated");
		this.p=p;
		this.name=name;
		this.myaccountnumber= -1;
		this.bankbalance= -1;
	}
	
	public void setBankManager(BankManager bm){
		this.bm=bm;
	}
	
	public void setBanker(Banker b){
		this.b=b;
	}
	
	public void setBankTeller(BankTeller t){
		this.t=t;
	}

//Messages
	public void msgBankClosed() {
		Do("Bank closed");
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
	public void msgAtTeller() {
		atTeller.release();
		bcstate=BankCustomerState.atTeller;
		stateChanged();
		
	}
	
	public void msgAtBanker(){
		Do("arrived at banker");
		atBanker.release();
		bcstate=BankCustomerState.atBanker;
		stateChanged();
	}
	public void msgAtWaiting(){
		//Do("arrived at waiting");
		atWaiting.release();
		stateChanged();
	}
	public void msgLeftBank(){
		//Do("finished leaving bank");
		atHome.release();
		bcstate=BankCustomerState.left;
		stateChanged();
		
	}
	
	public void msgNeedLoan(){
		Do("Recieved message need loan");
	    tstate=BankCustomerTransactionState.wantLoan;
	    stateChanged();
	}
	
	public void msgWantNewAccount(){
		Do("Recieved message want new account");
		tstate=BankCustomerTransactionState.wantNewAccount;
		stateChanged();
	}
	
	public void msgWantToDeposit(){
		Do("Recieved message want to deposit");
		tstate=BankCustomerTransactionState.wantToDeposit;
		stateChanged();
	}
	
	public void msgWantToWithdraw(){
		Do("Recieved message want to withdraw");
		tstate=BankCustomerTransactionState.wantToWithdraw;
		stateChanged();
	}
	
	
	public void msgGoToTeller(BankTeller te, int tn) {
		Do("Recieved message go to teller");
		t=te;
	    tellernumber=tn;
	    bcstate=BankCustomerState.assignedTeller;
	    stateChanged();
		
	}

	public void msgGoToBanker(Banker bk, int bn) {
		Do("Recieved message go to banker");
		b=bk;
		bankernumber=bn;
		bcstate=BankCustomerState.assignedBanker;
		stateChanged();
		
	}
	
	public void msgAccountCreated(double temp) {
		Do("Recieved message account created");
		setMyaccountnumber(temp);
		
	}
	
	public void msgRequestComplete(double change, double balance){
		Do("Recieved message request complete");
	    p.setCash((int) (p.getCash()+change));
		//mymoney += change;
	    setBankbalance(balance);
	    bcstate=BankCustomerState.done;
	    stateChanged();
	}

	
	public void msgLoanApproved(double loanamount){
		Do("Recieved message loan approved");
		p.setCash(p.getCash()+loanamount);
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
public void msgLoanDenied(double loanamount){
		Do("Recieved message loan denied");
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
	
	
//Scheduler	
	protected boolean pickAndExecuteAnAction() {
		if(bcstate==BankCustomerState.none && tstate==BankCustomerTransactionState.wantToDeposit){
			bcstate=BankCustomerState.waitingInBank;
			
			tellBankManagerDeposit();	
			return true;
		}
		
		if(bcstate==BankCustomerState.none && tstate==BankCustomerTransactionState.wantToWithdraw){
			bcstate=BankCustomerState.waitingInBank;
			
			tellBankManagerWithdraw();	
			return true;
		}
		
		if(bcstate == BankCustomerState.none && tstate==BankCustomerTransactionState.wantNewAccount){
			bcstate=BankCustomerState.waitingInBank;
			tellBankManagerNewAccount();
			return true;
		}
		
		if(bcstate == BankCustomerState.none && tstate==BankCustomerTransactionState.wantLoan){
			bcstate=BankCustomerState.waitingInBank;
			tellBankManagerLoan();
			return true;
		}
		
		if(bcstate==BankCustomerState.assignedTeller){
			//TODO Gui setup, temporarily bypassing
			bcstate=BankCustomerState.goingToTeller;
			doGoToTeller();	
			return true;
		}
		if(bcstate==BankCustomerState.assignedBanker){
			//TODO Gui setup, temporarily bypassing
			bcstate=BankCustomerState.goingToBanker;
			doGoToBanker();	
			return true;
		}
		
		
		
		if(bcstate==BankCustomerState.atTeller){
			bcstate=BankCustomerState.talking;
			
			if(tstate==BankCustomerTransactionState.wantToWithdraw){
				withdrawTeller(getAmount());
				return true;
			}
			
			if(tstate==BankCustomerTransactionState.wantToDeposit){
				depositTeller(getAmount());
				return true;
			}
		}	
			
		if(bcstate==BankCustomerState.atBanker){
			bcstate=BankCustomerState.talking;
			
			if(tstate==BankCustomerTransactionState.wantNewAccount){
				requestNewAccount(getAmount());
				return true;
			}
			
			if(tstate==BankCustomerTransactionState.wantLoan){
				requestLoan(getAmount());
				return true;
			}
			
			
		}	
			
		if(bcstate==BankCustomerState.done){
			bcstate=BankCustomerState.leaving;
			Do("leaving");
			Do("New account balance is " + bankbalance);
			Do("current cash balance is " + p.getCash());
			
			doLeaveBank();
		}
			
			
			
	
		
		
		
		return false;
	}
	
//Actions
	
	private void doLeaveBank() {
		custGui.DoLeaveBank();
		bm.msgImLeaving(this);
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*////////////////////////GUI ACTIONS  //////////////////////////////////////*/
	private void doGoToWaiting(){
		custGui.doGoToWaitingArea();
		try {
			atWaiting.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void doGoToTeller(){
		if (tellernumber==0){
			custGui.doGoToTeller1();
			try {
				atTeller.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(tellernumber==1){
			custGui.doGoToTeller2();
			try {
				atTeller.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void doGoToBanker(){
		custGui.doGoToBanker();
		try {
			atBanker.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*////////////////////NON GUI ACTIONS////////////////////////////////////////*/	
	private void tellBankManagerDeposit(){
		doGoToWaiting();
		Do("Telling Bank manager i want to deposit");
	    bm.msgIWantToDeposit(this);


	}

	private void tellBankManagerWithdraw(){
		doGoToWaiting();
		Do("Telling Bank manager i want to withdraw");
	    bm.msgIWantToWithdraw(this);

	}
	
	private void tellBankManagerNewAccount(){
		doGoToWaiting();
		Do("Telling Bank Manager i want new account");
		bm.msgIWantNewAccount(this);
		
	}
	
	private void tellBankManagerLoan(){
		doGoToWaiting();
		Do("Telling bank manager want loan");
		bm.msgIWantALoan(this);
	}
	
	private void withdrawTeller( int n){
		Do("Telling teller i want to withdraw");
	   t.msgIWantToWithdraw(this,getMyaccountnumber() ,n);

	}

	private void depositTeller( int n){
		Do("Telling teller i want to deposit");
	   t.msgIWantToDeposit(this,getMyaccountnumber(), n);

	}

	private void requestLoan(int n){
		Do("requesting loan");
	    b.msgIWantALoan(this, getMyaccountnumber() ,getAmount());
	}

	private void requestNewAccount(int n){
		Do("requesting new acccount");
	    b.msgIWantNewAccount(p, this, name, getAmount());

	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getMyaccountnumber() {
		return myaccountnumber;
	}

	public void setMyaccountnumber(double myaccountnumber) {
		this.myaccountnumber = myaccountnumber;
	}

	public double getBankbalance() {
		return bankbalance;
	}

	public void setBankbalance(double bankbalance) {
		this.bankbalance = bankbalance;
	}

	public void setGui(BankCustomerGui bcGui) {
		custGui=bcGui;
		
	}

	



	


}
