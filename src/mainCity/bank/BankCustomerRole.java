package mainCity.bank;

import java.util.concurrent.Semaphore;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.market.gui.CustomerGui;


public class BankCustomerRole extends Role {
	PersonAgent p;
	String name;
	BankManagerRole bm;
	BankerRole b;
	int bankernumber;
	BankTellerRole t;
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
	
	public enum DeferredTransaction{none,deposit,withdraw,loan}

	BankCustomerTransactionState tstate=BankCustomerTransactionState.none;
	
	DeferredTransaction dtrans =DeferredTransaction.none;
	
	public enum BankCustomerState{ none,waitingInBank, atTeller, atBanker, assignedTeller, assignedBanker, goingToTeller, goingToBanker, talking, done, leaving,left
	}
	
	BankCustomerState bcstate=BankCustomerState.none;
	
	public BankCustomerRole(PersonAgent p,String name){
		super(p);
		Do("bank customer initiated");
		this.p=p;
		this.name=name;
		this.myaccountnumber= -1;
		this.bankbalance= -1;
		this.amount=50;
	}
	
	public void setBankManager(BankManagerRole bm){
		this.bm=bm;
	}
	
	public void setBanker(BankerRole b){
		this.b=b;
	}
	
	public void setBankTeller(BankTellerRole t){
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
		Do("finished leaving bank");
		atHome.release();
		
		bcstate=BankCustomerState.left;
		setInactive();
		
	}
	
	public void msgNeedLoan(){
		Do("Recieved message need loan");
	    tstate=BankCustomerTransactionState.wantLoan;
	    if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			dtrans=DeferredTransaction.loan;
			Do("no account exists, making account");
		}
	    stateChanged();
	}
	
	public void msgWantNewAccount(){
		Do("Recieved message want new account");
		tstate=BankCustomerTransactionState.wantNewAccount;
		stateChanged();
	}
	
	public void msgWantToDeposit(){
		amount = (int) p.getCash()-100;
		System.out.println(amount);
		if(amount<100){
			bcstate=BankCustomerState.done;
			dtrans=DeferredTransaction.none;
			System.out.println("setting done");
			//setInactive();
		}
		Do("Recieved message want to deposit");
		tstate=BankCustomerTransactionState.wantToDeposit;
		bcstate=BankCustomerState.none;
		if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			//dtrans=DeferredTransaction.deposit;
			Do("no account exists, making account");
			System.out.println("tstate is " + tstate);
			System.out.println("bcstate is " + bcstate);
		}
		stateChanged();
	}
	
	public void msgWantToWithdraw(){
		Do("Recieved message want to withdraw");
		amount = 50;
		tstate=BankCustomerTransactionState.wantToWithdraw;
		if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			dtrans=DeferredTransaction.withdraw;
			Do("no account exists, making account");
			stateChanged();
			return;
			
		}
		if(bankbalance < amount){
			tstate=BankCustomerTransactionState.wantLoan;
			Do("Not enough money in bank. requesting loan");
			stateChanged();
			return;
			
		}
		stateChanged();
		
	}
	
	
	public void msgGoToTeller(BankTellerRole te, int tn) {
		Do("Recieved message go to teller");
		t=te;
	    tellernumber=tn;
	    bcstate=BankCustomerState.assignedTeller;
	    stateChanged();
		
	}

	public void msgGoToBanker(BankerRole bk, int bn) {
		Do("Recieved message go to banker");
		b=bk;
		bankernumber=bn;
		bcstate=BankCustomerState.assignedBanker;
		stateChanged();
		
	}
	
	public void msgAccountCreated(double temp) {
		Do("Recieved message account created");
		setMyaccountnumber(temp);
		p.setAccountnumber(temp);
		
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
	public boolean pickAndExecuteAnAction() {
		
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
			Do("waiting in bank");
			tellBankManagerNewAccount();
			return true;
		}
		
		if(bcstate == BankCustomerState.none && tstate==BankCustomerTransactionState.wantLoan){
			bcstate=BankCustomerState.waitingInBank;
			tellBankManagerLoan();
			return true;
		}
		
		if(bcstate==BankCustomerState.assignedTeller){
			
			bcstate=BankCustomerState.goingToTeller;
			doGoToTeller();	
			return true;
		}
		if(bcstate==BankCustomerState.assignedBanker){
			
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
				System.out.println(amount);
				depositTeller(getAmount());
				return true;
			}
		}	
			
		if(bcstate==BankCustomerState.atBanker){
			bcstate=BankCustomerState.talking;
			
			if(tstate==BankCustomerTransactionState.wantNewAccount){
				requestNewAccount(amount);
				return true;
			}
			
			if(tstate==BankCustomerTransactionState.wantLoan){
				requestLoan(amount);
				return true;
			}
			
			
		}	
		
		if(bcstate==BankCustomerState.done && dtrans!=DeferredTransaction.none){
			switch (dtrans){
			
			case deposit:
				dtrans=DeferredTransaction.none;
				bcstate=BankCustomerState.none;
				tstate=BankCustomerTransactionState.wantToDeposit;
				break;
			case withdraw:
				dtrans=DeferredTransaction.none;
				bcstate=BankCustomerState.none;
				tstate=BankCustomerTransactionState.wantToWithdraw;
				break;

			case loan:
				dtrans=DeferredTransaction.none;
				bcstate=BankCustomerState.none;
				tstate=BankCustomerTransactionState.wantLoan;
				break;
			}
			
			return true;
			
			
		}
			
		if(bcstate==BankCustomerState.done && dtrans==DeferredTransaction.none){
			
			bcstate=BankCustomerState.leaving;
			Do("leaving");
			Do("New account balance is " + bankbalance);
			Do("current cash balance is " + p.getCash());
			setInactive();
			doLeaveBank();
			
		}
			
			
			
	
		
		
		
		return false;
	}
	
//Actions
	


	/*////////////////////////GUI ACTIONS  //////////////////////////////////////*/
	private void doLeaveBank() {
		custGui.DoLeaveBank();
		bm.msgImLeaving(this);
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
	}
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
	    b.msgIWantALoan(this, getMyaccountnumber() ,n);
	}

	private void requestNewAccount(int n){
		Do("requesting new acccount");
	    b.msgIWantNewAccount(p, this, name, n);

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

	public BankCustomerGui getGui() {
		return custGui;
		}

	public boolean bankClosed() {
		
		if(bm != null && bm.isActive() && bm.isOpen()){
			return false;
		}
		System.out.println("customer checked and bank is closed");
		return true;
	}

	



	


}
