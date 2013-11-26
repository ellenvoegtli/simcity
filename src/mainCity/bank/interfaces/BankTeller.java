package mainCity.bank.interfaces;

import mainCity.bank.BankAccounts;
import mainCity.bank.gui.BankTellerGui;

public interface BankTeller {

	public abstract void setBankAccounts(BankAccounts ba);

	public abstract void setTellerNumber(int tn);

	//Messages
	public abstract void msgGoOffDuty(double amount);

	public abstract void msgIWantToDeposit(BankCustomer b, double accnum,
			int amount);

	public abstract void msgIWantToWithdraw(BankCustomer b, double accnum,
			int amount);

	public abstract boolean pickAndExecuteAnAction();

	public abstract void setGui(BankTellerGui gui);

}