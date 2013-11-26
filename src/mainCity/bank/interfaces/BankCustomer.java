package mainCity.bank.interfaces;

import mainCity.bank.BankManagerRole;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.market.gui.CustomerGui;

public interface BankCustomer {


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

	public abstract CustomerGui getGui();
	
}
	