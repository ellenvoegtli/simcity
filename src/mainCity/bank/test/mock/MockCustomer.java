package mainCity.bank.test.mock;

import mainCity.PersonAgent;
import mainCity.bank.BankManagerRole;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.bank.interfaces.BankManager;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;

public class MockCustomer extends Mock implements BankCustomer  {
	public BankManager manager;
	public Banker banker;
	public BankTeller teller;
	public PersonAgent p;
	
	
	public MockCustomer(String name) {
		super(name);
	}

	@Override
	public void setBankManager(BankManagerRole bm) {
		log.add(new LoggedEvent("setBankManager"));
		
	}

	@Override
	public void setBanker(Banker b) {
		log.add(new LoggedEvent("setBanker"));
		
	}

	@Override
	public void setBankTeller(BankTeller t) {
		log.add(new LoggedEvent("setBankTeller"));
		
	}

	@Override
	public void msgBankClosed() {
		log.add(new LoggedEvent("recieved msgBankClosed"));
		
	}

	@Override
	public void msgAtTeller() {
		log.add(new LoggedEvent("recieved msgAtTeller"));
		
	}

	@Override
	public void msgAtBanker() {
		log.add(new LoggedEvent("recieved msgAtBanker"));
		
	}

	@Override
	public void msgAtWaiting() {
		log.add(new LoggedEvent("recieved msgAtWaiting"));
		
	}

	@Override
	public void msgLeftBank() {
		log.add(new LoggedEvent("recieved msgLeftBank"));
		
	}

	@Override
	public void msgNeedLoan() {
		log.add(new LoggedEvent("recieved msgNeedLoan"));
		
	}

	@Override
	public void msgWantNewAccount() {
		log.add(new LoggedEvent("recieved msgWantNewAccount"));
		
	}

	@Override
	public void msgWantToDeposit() {
		log.add(new LoggedEvent("recieved msgWantToDeposit"));
		
	}

	@Override
	public void msgWantToWithdraw() {
		log.add(new LoggedEvent("recieved msgWantToWithdraw"));
		
	}

	@Override
	public void msgGoToTeller(BankTeller te, int tn) {
		log.add(new LoggedEvent("recieved msgGoToTeller"));
		
	}

	@Override
	public void msgGoToBanker(Banker bk, int bn) {
		log.add(new LoggedEvent("recieved msgGoToBanker"));
		
	}

	@Override
	public void msgAccountCreated(double temp) {
		log.add(new LoggedEvent("recieved msgAccountCreated"));
		
	}

	@Override
	public void msgRequestComplete(double change, double balance) {
		log.add(new LoggedEvent("recieved msgRequestComplete"));
		
	}

	@Override
	public void msgLoanApproved(double loanamount) {
		log.add(new LoggedEvent("recieved msgLoanApproved"));
		
	}

	@Override
	public void msgLoanDenied(double loanamount) {
		log.add(new LoggedEvent("recieved msgLoanDenied"));
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAmount(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMyaccountnumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMyaccountnumber(double myaccountnumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getBankbalance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBankbalance(double bankbalance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGui(BankCustomerGui bcGui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BankCustomerGui getGui() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean bankClosed() {
		// TODO Auto-generated method stub
		return false;
	}

}
