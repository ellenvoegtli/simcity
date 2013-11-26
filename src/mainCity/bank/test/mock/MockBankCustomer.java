package mainCity.bank.test.mock;

import mainCity.bank.BankManagerRole;
import mainCity.bank.BankTellerRole;
import mainCity.bank.BankerRole;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;
import mainCity.restaurants.jeffersonrestaurant.test.mock.LoggedEvent;

public class MockBankCustomer extends Mock implements BankCustomer {

	public MockBankCustomer(String name) {
		super(name);
	}
	public BankManagerRole bankmanager;
	public BankTellerRole bankteller;
	public BankerRole banker;
	public EventLog log;

	

	public void msgBankClosed() {
		log.add(new LoggedEvent("recieved msgBankClosed"));

	}


	public void msgAtTeller() {
		log.add(new LoggedEvent("recieved msgAtTeller"));

	}

	public void msgAtBanker() {
		log.add(new LoggedEvent("recieved msgAtBanker"));

	}

	public void msgAtWaiting() {
		log.add(new LoggedEvent("recieved msgAtWaiting"));

	}

	public void msgLeftBank() {
		log.add(new LoggedEvent("recieved msgLeft"));

	}

	public void msgNeedLoan() {
		log.add(new LoggedEvent("recieved msgNeedLoan"));

	}

	
	public void msgWantNewAccount() {
		log.add(new LoggedEvent("recieved msgWantNewAccount"));

	}

	
	public void msgWantToDeposit() {
		log.add(new LoggedEvent("recieved msgWantToDeposit"));

	}

	
	public void msgWantToWithdraw() {
		log.add(new LoggedEvent("recieved msgWantToWithdraw"));

	}

	
	public void msgGoToTeller(BankTeller te, int tn) {
		log.add(new LoggedEvent("recieved msgGoToTeller"));

	}

	
	public void msgGoToBanker(Banker bk, int bn) {
		log.add(new LoggedEvent("recieved msgGoToBanker"));

	}

	
	public void msgAccountCreated(double temp) {
		log.add(new LoggedEvent("recieved msgAccountCreated"));

	}

	
	public void msgRequestComplete(double change, double balance) {
		log.add(new LoggedEvent("recieved msgRequestComplete"));

	}

	
	public void msgLoanApproved(double loanamount) {
		log.add(new LoggedEvent("recieved msgLoanApproved"));

	}

	
	public void msgLoanDenied(double loanamount) {
		log.add(new LoggedEvent("recieved msgLoanDenied"));

	}
}
