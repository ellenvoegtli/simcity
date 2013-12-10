package mainCity.bank.interfaces;

import role.bank.BankManagerRole;
import mainCity.bank.gui.BankCustomerGui;

public interface BankCustomer {

	public abstract void setBankManager(BankManagerRole bm);

	public abstract void setBanker(Banker b);

	public abstract void setBankTeller(BankTeller t);

	//Messages
	public abstract void msgBankClosed();

	public abstract void msgAtTeller();

	public abstract void msgAtBanker();

	public abstract void msgAtWaiting();

	public abstract void msgLeftBank();

	public abstract void msgNeedLoan();

	public abstract void msgWantNewAccount();

	public abstract void msgWantToDeposit();

	public abstract void msgWantToWithdraw();

	public abstract void msgGoToTeller(BankTeller te, int tn);

	public abstract void msgGoToBanker(Banker bk, int bn);

	public abstract void msgAccountCreated(double temp);

	public abstract void msgRequestComplete(double change, double balance);

	public abstract void msgLoanApproved(double loanamount);

	public abstract void msgLoanDenied(double loanamount);

	//Scheduler	
	public abstract boolean pickAndExecuteAnAction();

	public abstract int getAmount();

	public abstract void setAmount(int amount);

	public abstract double getMyaccountnumber();

	public abstract void setMyaccountnumber(double myaccountnumber);

	public abstract double getBankbalance();

	public abstract void setBankbalance(double bankbalance);

	public abstract void setGui(BankCustomerGui bcGui);

	public abstract BankCustomerGui getGui();

	public abstract boolean bankClosed();

}