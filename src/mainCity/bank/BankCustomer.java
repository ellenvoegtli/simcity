package mainCity.bank;

import mainCity.PersonAgent;
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
	double myaccountnumber;
	double bankbalance;
	//double mymoney;
	//will be used for all transactions, loan requests, etc
	int amount;
	
	public enum BankCustomerTransactionState{ none,wantToDeposit, wantToWithdraw, wantNewAccount, wantLoan}

	BankCustomerTransactionState tstate=BankCustomerTransactionState.none;
	
	public enum BankCustomerState{ none,waitingInBank, atTeller, atBanker, assignedTeller, assignedBanker, goingToTeller, goingToBanker, talking, leaving, done
	}
	
	BankCustomerState bcstate=BankCustomerState.none;

//Messages
	
	public void msgNeedLoan(){
	    tstate=BankCustomerTransactionState.wantLoan;
	    stateChanged();
	}
	
	public void msgWantNewAccount(){
		tstate=BankCustomerTransactionState.wantNewAccount;
		stateChanged();
	}
	
	public void msgWantToDeposit(){
		tstate=BankCustomerTransactionState.wantToDeposit;
		stateChanged();
	}
	
	public void msgWantToWithdraw(){
		tstate=BankCustomerTransactionState.wantToWithdraw;
		stateChanged();
	}
	
	
	public void msgGoToTeller(BankTeller te, int tn) {
		t=te;
	    tellernumber=tn;
	    bcstate=BankCustomerState.assignedTeller;
	    stateChanged();
		
	}

	public void msgGoToBanker(Banker bk, int bn) {
		b=bk;
		bankernumber=bn;
		bcstate=BankCustomerState.assignedBanker;
		
	}

	
	public void msgRequestComplete(int change, int balance){
	    p.setCash(p.getCash()+change);
		//mymoney += change;
	    bankbalance=balance;
	    bcstate=BankCustomerState.done;
	}

	
	public void msgLoanApproved(int loanamount){
		
		p.setCash(p.getCash()+loanamount);
		bcstate=BankCustomerState.done;
	}
	
public void msgLoanDenied(int loanamount){
	
		bcstate=BankCustomerState.done;
	}
	
	
	
	
	protected boolean pickAndExecuteAnAction() {
		if(bcstate==BankCustomerState.none & tstate==BankCustomerTransactionState.wantToDeposit){
			bcstate=BankCustomerState.waitingInBank;
			tellBankManagerDeposit();	
			return true;
		}
		
		if(bcstate==BankCustomerState.none & tstate==BankCustomerTransactionState.wantToWithdraw){
			bcstate=BankCustomerState.waitingInBank;
			tellBankManagerWithdraw();	
			return true;
		}
		
		if(bcstate==BankCustomerState.assignedTeller){
			//TODO Gui setup
			//doGoToTeller();	
			return true;
		}
		if(bcstate==BankCustomerState.assignedBanker){
			//TODO Gui setup
			//doGoToBanker();	
			return true;
		}
		
		
		
		if(bcstate==BankCustomerState.atTeller){
			bcstate=BankCustomerState.talking;
			
			if(tstate==BankCustomerTransactionState.wantToWithdraw){
				withdrawTeller(amount);
				return true;
			}
			
			if(tstate==BankCustomerTransactionState.wantToDeposit){
				depositTeller(amount);
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
			}
			
			
		}	
			
		
			
			
			
	
		
		
		
		return false;
	}
	
//Actions
	
	private void tellBankManagerDeposit(){

	    bm.msgIWantToDeposit(this);


	}

	private void tellBankManagerWithdraw(){

	    bm.msgIWantToWithdraw(this);

	}

	private void withdrawTeller( int n){
	   // t.msgIWantToWithdraw(this, n);

	}

	private void depositTeller( int n){
	    //t.msgIWantToDeposit(this, n);

	}

	private void requestLoan(int n){
	    //b.msgIWantALoan(this, amount);
	}

	private void requestNewAccount(int n){
	    //b.msgIWantNewAccount(this, amount);

	}


}
