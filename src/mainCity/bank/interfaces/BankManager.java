package mainCity.bank.interfaces;

import mainCity.bank.BankAccounts;
import mainCity.bank.BankerRole;

public interface BankManager {

	public abstract void setBanker(BankerRole br);

	public abstract void msgBankerAdded();

	public abstract void msgEndShift();

	public abstract void msgTellerAdded(BankTeller bt);

	public abstract void msgDirectDeposit(double accountNumber, int amount);

	public abstract void msgIWantToDeposit(BankCustomer bc);

	public abstract void msgIWantToWithdraw(BankCustomer bc);

	public abstract void msgIWantNewAccount(BankCustomer bc);

	public abstract void msgIWantALoan(BankCustomer bc);

	public abstract void msgImLeaving(BankTeller bt);

	public abstract void msgImLeaving(Banker b);

	public abstract void msgImLeaving(BankCustomer bc);

	public abstract boolean pickAndExecuteAnAction();

	public abstract boolean isOpen();

	public abstract void setBankAccounts(BankAccounts accounts);

	public abstract boolean closeBuilding();

}