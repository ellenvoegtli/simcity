package mainCity.bank.interfaces;

import role.bank.BankManagerRole;
import mainCity.bank.gui.BankRobberGui;

public interface BankRobber {

	//Messages
	public abstract void msgLeftBank();

	public abstract void msgAtBankManager();

	public abstract void msgWantToRobBank();

	public abstract void msgOkayDontHurtMe(double bounty);

	//Scheduler
	public abstract boolean pickAndExecuteAnAction();

	public abstract void log(String s);

	public abstract boolean bankClosed();

	public abstract void setGui(BankRobberGui brGui2);

	public abstract BankRobberGui getGui();

	public abstract void setBankManager(BankManagerRole bankmanager);

}