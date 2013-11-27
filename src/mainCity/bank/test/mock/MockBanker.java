package mainCity.bank.test.mock;

import mainCity.PersonAgent;
import mainCity.bank.BankAccounts;
import mainCity.bank.gui.BankerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.Banker;

public class MockBanker extends Mock implements Banker {

	public MockBanker(String name) {
		super(name);
	}

	@Override
	public void setBankAccounts(BankAccounts singular) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoOffDuty(double d) {
		log.add(new LoggedEvent("recieved msgGoOffDuty"));
		
	}

	@Override
	public void msgGoToWork() {
		log.add(new LoggedEvent("recieved msgGoToWork"));

		
	}

	@Override
	public void msgIWantALoan(BankCustomer b, double accnum, double amnt) {
		log.add(new LoggedEvent("recieved msgIWantALoan"));

		
	}

	@Override
	public void msgIWantNewAccount(PersonAgent p, BankCustomer b, String name,
			double amnt) {
		log.add(new LoggedEvent("recieved msgIWantNewAccount"));

		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGui(BankerGui gui) {
		// TODO Auto-generated method stub
		
	}

}
