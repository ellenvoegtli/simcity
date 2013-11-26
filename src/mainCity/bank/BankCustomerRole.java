package mainCity.bank;

import java.util.concurrent.Semaphore;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.market.gui.CustomerGui;


public class BankCustomerRole extends Role implements BankCustomer {
	PersonAgent p;
	String name;
	BankManagerRole bm;
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
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setBankManager(mainCity.bank.BankManagerRole)
	 */
	@Override
	public void setBankManager(BankManagerRole bm){
		this.bm=bm;
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setBanker(mainCity.bank.BankerRole)
	 */
	@Override
	public void setBanker(Banker b){
		this.b=b;
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setBankTeller(mainCity.bank.BankTellerRole)
	 */
	@Override
	public void setBankTeller(BankTeller t){
		this.t=t;
	}

//Messages
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgBankClosed()
	 */
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), s);
	}
	@Override
	public void msgBankClosed() {
		Do("Bank closed");
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgAtTeller()
	 */
	@Override
	public void msgAtTeller() {
		atTeller.release();
		bcstate=BankCustomerState.atTeller;
		stateChanged();
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgAtBanker()
	 */
	@Override
	public void msgAtBanker(){
		log("arrived at banker");
		atBanker.release();
		bcstate=BankCustomerState.atBanker;
		stateChanged();
	}
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgAtWaiting()
	 */
	@Override
	public void msgAtWaiting(){
		//Do("arrived at waiting");
		atWaiting.release();
		stateChanged();
	}
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgLeftBank()
	 */
	@Override
	public void msgLeftBank(){
		log("finished leaving bank");
		atHome.release();
		
		bcstate=BankCustomerState.left;
		setInactive();
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgNeedLoan()
	 */
	@Override
	public void msgNeedLoan(){
		log("Recieved message need loan");
	    tstate=BankCustomerTransactionState.wantLoan;
	    if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			dtrans=DeferredTransaction.loan;
			log("no account exists, making account");
		}
	    stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgWantNewAccount()
	 */
	@Override
	public void msgWantNewAccount(){
		log("Recieved message want new account");
		tstate=BankCustomerTransactionState.wantNewAccount;
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgWantToDeposit()
	 */
	@Override
	public void msgWantToDeposit(){
		amount = (int) p.getCash()-100;
		System.out.println(amount);
		if(amount<100){
			bcstate=BankCustomerState.done;
			dtrans=DeferredTransaction.none;
			System.out.println("setting done");
			//setInactive();
		}
		log("Recieved message want to deposit");
		tstate=BankCustomerTransactionState.wantToDeposit;
		bcstate=BankCustomerState.none;
		if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			//dtrans=DeferredTransaction.deposit;
			log("no account exists, making account");
			System.out.println("tstate is " + tstate);
			System.out.println("bcstate is " + bcstate);
		}
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgWantToWithdraw()
	 */
	@Override
	public void msgWantToWithdraw(){
		Do("Recieved message want to withdraw");
		amount = 50;
		tstate=BankCustomerTransactionState.wantToWithdraw;
		if(myaccountnumber== -1){
			tstate=BankCustomerTransactionState.wantNewAccount;
			dtrans=DeferredTransaction.withdraw;
			log("no account exists, making account");
			stateChanged();
			return;
			
		}
		if(bankbalance < amount){
			tstate=BankCustomerTransactionState.wantLoan;
			log("Not enough money in bank. requesting loan");
			stateChanged();
			return;
			
		}
		stateChanged();
		
	}
	
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgGoToTeller(mainCity.bank.BankTellerRole, int)
	 */
	@Override
	public void msgGoToTeller(BankTeller te, int tn) {
		log("Recieved message go to teller");
		t=te;
	    tellernumber=tn;
	    bcstate=BankCustomerState.assignedTeller;
	    stateChanged();
		
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgGoToBanker(mainCity.bank.BankerRole, int)
	 */
	@Override
	public void msgGoToBanker(Banker bk, int bn) {
		log("Recieved message go to banker");
		b=bk;
		bankernumber=bn;
		bcstate=BankCustomerState.assignedBanker;
		stateChanged();
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgAccountCreated(double)
	 */
	@Override
	public void msgAccountCreated(double temp) {
		log("Recieved message account created");
		setMyaccountnumber(temp);
		p.setAccountnumber(temp);
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgRequestComplete(double, double)
	 */
	@Override
	public void msgRequestComplete(double change, double balance){
		log("Recieved message request complete");
	    p.setCash((int) (p.getCash()+change));
		//mymoney += change;
	    setBankbalance(balance);
	    bcstate=BankCustomerState.done;
	    stateChanged();
	}

	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#msgLoanApproved(double)
	 */
	@Override
	public void msgLoanApproved(double loanamount){
		log("Recieved message loan approved");
		p.setCash(p.getCash()+loanamount);
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
/* (non-Javadoc)
 * @see mainCity.bank.BankCustomer#msgLoanDenied(double)
 */
@Override
public void msgLoanDenied(double loanamount){
		log("Recieved message loan denied");
		bcstate=BankCustomerState.done;
		stateChanged();
	}
	
	
	
//Scheduler	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#pickAndExecuteAnAction()
	 */
	@Override
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
			log("waiting in bank");
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
			log("leaving");
			log("New account balance is " + bankbalance);
			log("current cash balance is " + p.getCash());
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
		log("Telling Bank manager i want to deposit");
	    bm.msgIWantToDeposit(this);


	}

	private void tellBankManagerWithdraw(){
		doGoToWaiting();
		log("Telling Bank manager i want to withdraw");
	    bm.msgIWantToWithdraw(this);

	}
	
	private void tellBankManagerNewAccount(){
		doGoToWaiting();
		log("Telling Bank Manager i want new account");
		bm.msgIWantNewAccount(this);
		
	}
	
	private void tellBankManagerLoan(){
		doGoToWaiting();
		log("Telling bank manager want loan");
		bm.msgIWantALoan(this);
	}
	
	private void withdrawTeller( int n){
		log("Telling teller i want to withdraw");
	   t.msgIWantToWithdraw(this,getMyaccountnumber() ,n);

	}

	private void depositTeller( int n){
		log("Telling teller i want to deposit");
	   t.msgIWantToDeposit(this,getMyaccountnumber(), n);

	}

	private void requestLoan(int n){
		log("requesting loan");
	    b.msgIWantALoan(this, getMyaccountnumber() ,n);
	}

	private void requestNewAccount(int n){
		log("requesting new acccount");
	    b.msgIWantNewAccount(p, this, name, n);

	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#getAmount()
	 */
	@Override
	public int getAmount() {
		return amount;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setAmount(int)
	 */
	@Override
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#getMyaccountnumber()
	 */
	@Override
	public double getMyaccountnumber() {
		return myaccountnumber;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setMyaccountnumber(double)
	 */
	@Override
	public void setMyaccountnumber(double myaccountnumber) {
		this.myaccountnumber = myaccountnumber;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#getBankbalance()
	 */
	@Override
	public double getBankbalance() {
		return bankbalance;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setBankbalance(double)
	 */
	@Override
	public void setBankbalance(double bankbalance) {
		this.bankbalance = bankbalance;
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#setGui(mainCity.bank.gui.BankCustomerGui)
	 */
	@Override
	public void setGui(BankCustomerGui bcGui) {
		custGui=bcGui;
		
	}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#getGui()
	 */
	@Override
	public BankCustomerGui getGui() {
		return custGui;
		}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankCustomer#bankClosed()
	 */
	@Override
	public boolean bankClosed() {
		
		if(bm != null && bm.isActive() && bm.isOpen()){
			return false;
		}
		System.out.println("customer checked and bank is closed");
		return true;
	}

	



	


}
