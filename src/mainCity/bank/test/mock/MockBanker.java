package mainCity.bank.test.mock;

import mainCity.PersonAgent;
import mainCity.bank.BankManagerRole;
import mainCity.bank.BankTellerRole;
import mainCity.bank.BankerRole;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.Banker;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;
import mainCity.restaurants.jeffersonrestaurant.test.mock.LoggedEvent;

public class MockBanker implements Banker {
	public BankManagerRole bankmanager;
	public BankTellerRole bankteller;
	public BankerRole banker;
	public EventLog log;

	public MockBanker() {
		
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

}
