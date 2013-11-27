package mainCity.bank.test.mock;

import mainCity.bank.BankAccounts;
import mainCity.bank.BankerRole;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankManager;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.bank.test.mock.LoggedEvent;

public class MockBankManager extends Mock implements BankManager{

	public MockBankManager(String name) {
		super(name);
	}

	@Override
	public void setBanker(BankerRole br) {
		log.add(new LoggedEvent("banker set"));
		
	}

	@Override
	public void msgBankerAdded() {
		log.add(new LoggedEvent("recieved msgBankerAdded"));
		
	}

	@Override
	public void msgEndShift() {
		log.add(new LoggedEvent("recieved msgEndShift"));
		
	}

	@Override
	public void msgTellerAdded(BankTeller bt) {
		log.add(new LoggedEvent("recieved msgTellerAdded"));
		
	}

	@Override
	public void msgDirectDeposit(double accountNumber, int amount) {
		log.add(new LoggedEvent("recieved msgDirectDeposit"));
		
	}

	@Override
	public void msgIWantToDeposit(BankCustomer bc) {
		log.add(new LoggedEvent("recieved msgIWantToDeposit"));
		
	}

	@Override
	public void msgIWantToWithdraw(BankCustomer bc) {
		log.add(new LoggedEvent("recieved msgIWantToWithdraw"));
		
	}

	@Override
	public void msgIWantNewAccount(BankCustomer bc) {
		log.add(new LoggedEvent("recieved msgIWantNewAccount"));
		
	}

	@Override
	public void msgIWantALoan(BankCustomer bc) {
		log.add(new LoggedEvent("recieved msgIWantALoan"));
		
	}

	@Override
	public void msgImLeaving(BankTeller bt) {
		log.add(new LoggedEvent("recieved msgImLeaving"));
		
	}

	@Override
	public void msgImLeaving(Banker b) {
		log.add(new LoggedEvent("recieved msgImLeaving"));
		
	}

	@Override
	public void msgImLeaving(BankCustomer bc) {
		log.add(new LoggedEvent("recieved msgImLeaving"));
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBankAccounts(BankAccounts accounts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean closeBuilding() {
		// TODO Auto-generated method stub
		return false;
	}

}
