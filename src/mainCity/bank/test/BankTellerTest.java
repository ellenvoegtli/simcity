package mainCity.bank.test;

import role.bank.BankTellerRole;
import role.bank.BankerRole;
import role.bank.BankTellerRole.ClientState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.bank.BankAccounts;
import mainCity.bank.gui.BankTellerGui;
import mainCity.bank.gui.BankerGui;
import mainCity.bank.test.mock.MockCustomer;
import junit.framework.TestCase;

public class BankTellerTest extends TestCase {
	BankTellerRole teller;
	MockCustomer m1;
	MockCustomer m2;
	BankAccounts mainaccounts;
	

	public void setUp() throws Exception{
		super.setUp();
		PersonAgent t = new PersonAgent("banktellerdude");
		teller = new BankTellerRole(t, t.getName());
		BankTellerGui btgui = new BankTellerGui(teller);
		teller.setGui(btgui);
		t.addRole(ActionType.work, teller);
		
		mainaccounts = new BankAccounts();
		teller.setBankAccounts(mainaccounts);
	}
	
	
	
	public void testOneCustomerDepositScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		assertTrue("BankTeller's myClient should be Null, it isnt", teller.myclient==null);
		
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
		
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		//Step 1- Add the client
		teller.msgIWantToDeposit(m1, 0, 100);
		
		//Step 2 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

		
		assertTrue("Teller's myClient's customer should be m1. It isn't", teller.myclient.bc==m1);		
		
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==100);
		
		assertTrue("Teller's myClient's accountnumber should be 0 . It isn't", teller.myclient.accountnumber==0);
		
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.depositing);
		
		//Step 3 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
		
		
		//Step 4- check postconditions
		
		assertTrue("Account should have a balance of 100. It doesnt", mainaccounts.accounts.get(0).balance==200);
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
	}	
	
	public void testOneCustomerWithdrawScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		assertTrue("BankTeller's myClient should be Null, it isnt", teller.myclient==null);
		
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
		
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		//Step 1- Add the client
		teller.msgIWantToWithdraw(m1, 0, 50);
		
		//Step 2 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

		
		assertTrue("Teller's myClient's customer should be m1. It isn't", teller.myclient.bc==m1);		
		
		assertTrue("Teller's myClient's amount should be 50 . It isn't", teller.myclient.amount==50);
		
		assertTrue("Teller's myClient's accountnumber should be 0 . It isn't", teller.myclient.accountnumber==0);
		
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.withdrawing);
		
		//Step 3 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
		
		
		//Step 4- check postconditions
		
		assertTrue("Account should have a balance of 50. It doesnt", mainaccounts.accounts.get(0).balance==50);
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
	}	
	public void testTwoCustomerDepositScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		assertTrue("BankTeller's myClient should be Null, it isnt", teller.myclient==null);
		
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
		
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		//Step 1- Add the client
		teller.msgIWantToDeposit(m1, 0, 100);
		
		//Step 2 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

		
		assertTrue("Teller's myClient's customer should be m1. It isn't", teller.myclient.bc==m1);		
		
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==100);
		
		assertTrue("Teller's myClient's accountnumber should be 0 . It isn't", teller.myclient.accountnumber==0);
		
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.depositing);
		
		//Step 3 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
		
		
		//Step 4- check postconditions
		
		assertTrue("Account should have a balance of 100. It doesnt", mainaccounts.accounts.get(0).balance==200);
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		/********************SECOND CUSTOMER*******************************/
		m2 = new MockCustomer("mockCustomer2");
		PersonAgent p2 = new PersonAgent("mockCustomer1");
		m2.p=p2;
		mainaccounts.addAccount("mockCustomer2", 100, p2, 1);
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
				
		//assertTrue("BankTeller's myClient should be Null, it isnt", teller.mc==null);
				
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
				
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
						+ "event log reads: "+ m2.log.toString(),0, m2.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		//Step 5- Add the client
		teller.msgIWantToDeposit(m2, 1, 100);
				
		//Step 6 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

				
		assertTrue("Teller's myClient's customer should be m2. It isn't", teller.myclient.bc==m2);		
			
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==100);
			
		assertTrue("Teller's myClient's accountnumber should be 1 . It isn't", teller.myclient.accountnumber==1);
			
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.depositing);
			
		//Step 7 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
				
				
		//Step 8- check postconditions
				
		assertTrue("Account should have a balance of 100. It doesnt", mainaccounts.accounts.get(1).balance==200);
				
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m2.log.containsString("recieved msgRequestComplete"));
				
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());		
		
	}

	public void testTwoCustomerWithdrawScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		assertTrue("BankTeller's myClient should be Null, it isnt", teller.myclient==null);
		
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
		
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		//Step 1- Add the client
		teller.msgIWantToWithdraw(m1, 0, 50);
		
		//Step 2 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

		
		assertTrue("Teller's myClient's customer should be m1. It isn't", teller.myclient.bc==m1);		
		
		assertTrue("Teller's myClient's amount should be 50 . It isn't", teller.myclient.amount==50);
		
		assertTrue("Teller's myClient's accountnumber should be 0 . It isn't", teller.myclient.accountnumber==0);
		
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.withdrawing);
		
		//Step 3 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
		
		
		//Step 4- check postconditions
		
		assertTrue("Account should have a balance of 50. It doesnt", mainaccounts.accounts.get(0).balance==50);
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		/********************SECOND CUSTOMER*******************************/
		m2 = new MockCustomer("mockCustomer2");
		PersonAgent p2 = new PersonAgent("mockCustomer1");
		m2.p=p2;
		mainaccounts.addAccount("mockCustomer2", 100, p2, 1);
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
				
		//assertTrue("BankTeller's myClient should be Null, it isnt", teller.mc==null);
				
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
				
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
						+ "event log reads: "+ m2.log.toString(),0, m2.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		//Step 5- Add the client
		teller.msgIWantToWithdraw(m2, 1, 50);
				
		//Step 6 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

				
		assertTrue("Teller's myClient's customer should be m2. It isn't", teller.myclient.bc==m2);		
			
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==50);
			
		assertTrue("Teller's myClient's accountnumber should be 1 . It isn't", teller.myclient.accountnumber==1);
			
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.withdrawing);
			
		//Step 7 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
				
				
		//Step 8- check postconditions
				
		assertTrue("Account should have a balance of 50. It doesnt", mainaccounts.accounts.get(1).balance==50);
				
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m2.log.containsString("recieved msgRequestComplete"));
				
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());		
		
	}
	
	public void testTwoCustomerWithdrawAndDepositScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		assertTrue("BankTeller's myClient should be Null, it isnt", teller.myclient==null);
		
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
		
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		//Step 1- Add the client
		teller.msgIWantToWithdraw(m1, 0, 50);
		
		//Step 2 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

		
		assertTrue("Teller's myClient's customer should be m1. It isn't", teller.myclient.bc==m1);		
		
		assertTrue("Teller's myClient's amount should be 50 . It isn't", teller.myclient.amount==50);
		
		assertTrue("Teller's myClient's accountnumber should be 0 . It isn't", teller.myclient.accountnumber==0);
		
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.withdrawing);
		
		//Step 3 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
		
		
		//Step 4- check postconditions
		
		assertTrue("Account should have a balance of 50. It doesnt", mainaccounts.accounts.get(0).balance==50);
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		/**************NUMBER TWOOOO***************************/
		
		m2 = new MockCustomer("mockCustomer2");
		PersonAgent p2 = new PersonAgent("mockCustomer1");
		m2.p=p2;
		mainaccounts.addAccount("mockCustomer2", 100, p2, 1);
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
				
		//assertTrue("BankTeller's myClient should be Null, it isnt", teller.mc==null);
				
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
				
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
						+ "event log reads: "+ m2.log.toString(),0, m2.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		//Step 5- Add the client
		teller.msgIWantToDeposit(m2, 1, 100);
				
		//Step 6 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

				
		assertTrue("Teller's myClient's customer should be m2. It isn't", teller.myclient.bc==m2);		
			
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==100);
			
		assertTrue("Teller's myClient's accountnumber should be 1 . It isn't", teller.myclient.accountnumber==1);
			
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.depositing);
			
		//Step 7 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
				
				
		//Step 8- check postconditions
				
		assertTrue("Account should have a balance of 100. It doesnt", mainaccounts.accounts.get(1).balance==200);
				
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m2.log.containsString("recieved msgRequestComplete"));
				
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());	
		
	}
	
	public void testTwoCustomerDepositAndWithdrawScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		
		assertTrue("BankTeller's myClient should be Null, it isnt", teller.myclient==null);
		
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
		
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		//Step 1- Add the client
		teller.msgIWantToDeposit(m1, 0, 100);
		
		//Step 2 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

		
		assertTrue("Teller's myClient's customer should be m1. It isn't", teller.myclient.bc==m1);		
		
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==100);
		
		assertTrue("Teller's myClient's accountnumber should be 0 . It isn't", teller.myclient.accountnumber==0);
		
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.depositing);
		
		//Step 3 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
		
		
		//Step 4- check postconditions
		
		assertTrue("Account should have a balance of 100. It doesnt", mainaccounts.accounts.get(0).balance==200);
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
		/***********************PART TWOOOOO******************/
		m2 = new MockCustomer("mockCustomer2");
		PersonAgent p2 = new PersonAgent("mockCustomer1");
		m2.p=p2;
		mainaccounts.addAccount("mockCustomer2", 100, p2, 1);
		//check preconditions
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());
				
		//assertTrue("BankTeller's myClient should be Null, it isnt", teller.mc==null);
				
		assertTrue("Banker's bankaccounts should exist, it doesnt", teller.bankaccounts!=null);
				
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
						+ "event log reads: "+ m2.log.toString(),0, m2.log.size(),0 );
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		//Step 5- Add the client
		teller.msgIWantToDeposit(m2, 1, 100);
				
		//Step 6 Check postconditions
		assertTrue("Banker's myClient shouldn't be Null, it is", teller.myclient!=null);

				
		assertTrue("Teller's myClient's customer should be m2. It isn't", teller.myclient.bc==m2);		
			
		assertTrue("Teller's myClient's amount should be 100 . It isn't", teller.myclient.amount==100);
			
		assertTrue("Teller's myClient's accountnumber should be 1 . It isn't", teller.myclient.accountnumber==1);
			
		assertTrue("Teller's myClient's state should be withdrawing . It isn't", teller.myclient.cs==ClientState.depositing);
			
		//Step 7 Run the scheduler
		assertTrue("BankTeller's scheduler should have returned true. But it didn't.", teller.pickAndExecuteAnAction());
				
				
		//Step 8- check postconditions
				
		assertTrue("Account should have a balance of 100. It doesnt", mainaccounts.accounts.get(1).balance==200);
				
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" . It didnt.",m2.log.containsString("recieved msgRequestComplete"));
				
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
			
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
				
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
				
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
				
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		assertFalse("BankTeller's scheduler should have returned false. But it didn't.", teller.pickAndExecuteAnAction());	
		
		
	}
	


}
