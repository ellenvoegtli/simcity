package mainCity.bank.test.mock;

import mainCity.bank.BankAccounts;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankTeller;

public class MockBankTeller extends Mock implements BankTeller {

	public MockBankTeller(String name) {
		super(name);
	}

	@Override
	public void setBankAccounts(BankAccounts ba) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTellerNumber(int tn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoOffDuty(double amount) {
		log.add(new LoggedEvent("recieved msgGoOffDuty"));
		
	}

	@Override
	public void msgIWantToDeposit(BankCustomer b, double accnum, int amount) {
		log.add(new LoggedEvent("recieved msgIWantToDeposit"));
	}

	@Override
	public void msgIWantToWithdraw(BankCustomer b, double accnum, int amount) {
		log.add(new LoggedEvent("recieved msgIWantToWithdraw"));
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGui(BankTellerGui gui) {
		// TODO Auto-generated method stub
		
	}

}
