package mainCity.bank.test;

import role.bank.BankerRole;
import role.bank.BankerRole.ClientState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.bank.BankAccounts;
import mainCity.bank.gui.BankerGui;
import mainCity.bank.test.mock.MockCustomer;
import junit.framework.TestCase;

public class BankerTest extends TestCase {
	BankerRole banker;
	MockCustomer m1;
	MockCustomer m2;
	BankAccounts mainaccounts;
	
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent b = new PersonAgent("bankerdude");
		banker = new BankerRole(b, b.getName());
		BankerGui bgui = new BankerGui(banker);
		banker.setGui(bgui);
		b.addRole(ActionType.work, banker);
		
		//m1 = new MockCustomer("mockCustomer1");
		//PersonAgent p1 = new PersonAgent("mockCustomer1");
		//m1.p=p1;
	
		//m2 = new MockCustomer("mockCustomer2");
		//PersonAgent p2 = new PersonAgent("mockCustomer1");
		//m2.p=p2;
		
		mainaccounts = new BankAccounts();
		banker.setBankAccounts(mainaccounts);
		//mainaccounts.addAccount("mockCustomer1", 1000, b, 0);
		
	}
	public void testOneCustomerNewAccountScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		
		//check preconditions
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
		assertTrue("Banker's myClient should be Null, it isnt", banker.myclient==null);
		assertTrue("Banker's bankaccounts should exist, it doesnt", banker.bankaccounts!=null);
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		//Step 1- Add the client
		banker.msgIWantNewAccount(m1.p, m1, "mockCustomer1", 100);
		
		//check postConditions for step 1
		assertTrue("Banker's myClient shouldn't be Null, it is", banker.myclient!=null);
		
		assertTrue("Banker's myClient's person should be p1. It isn't", banker.myclient.p==p1);
		
		assertTrue("Banker's myClient's customer should be m1. It isn't", banker.myclient.bc==m1);
		
		assertTrue("Banker's myClient's state should be wantsAccount. It isn't", banker.myclient.cs==ClientState.wantsAccount);
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		//Step 2 - check if the scheduler returns true
		
		assertTrue("Banker's scheduler should have return true. But it didn't.", banker.pickAndExecuteAnAction());
		
		//check postconditions for step 2/preconditions for step 3
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertTrue("New account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" . It didnt.",m1.log.containsString("recieved msgAccountCreated"));
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" msgRequestComplete. It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		//assertTrue("Banker's myClient should be Null, it isn't", banker.mc!=null);
		
		//Step 3 check if scheduler returns false
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
	}

	public void testTwoCustomerNewAccountScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		
		m2 = new MockCustomer("mockCustomer2");
		PersonAgent p2 = new PersonAgent("mockCustomer1");
		m2.p=p2;
		
		//check preconditions
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
		assertTrue("Banker's myClient should be Null, it isnt", banker.myclient==null);
		assertTrue("Banker's bankaccounts should exist, it doesnt", banker.bankaccounts!=null);
		assertEquals("MockCustomer should have an empty eventlog before scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		//Step 1- Add the client
		banker.msgIWantNewAccount(m1.p, m1, "mockCustomer1", 100);
		
		//check postConditions for step 1
		assertTrue("Banker's myClient shouldn't be Null, it is", banker.myclient!=null);
		
		assertTrue("Banker's myClient's person should be p1. It isn't", banker.myclient.p==p1);
		
		assertTrue("Banker's myClient's customer should be m1. It isn't", banker.myclient.bc==m1);
		
		assertTrue("Banker's myClient's state should be wantsAccount. It isn't", banker.myclient.cs==ClientState.wantsAccount);
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		//Step 2 - check if the scheduler returns true
		
		assertTrue("Banker's scheduler should have return true. But it didn't.", banker.pickAndExecuteAnAction());
		
		//check postconditions for step 2/preconditions for step 3
		assertEquals("Number of existing accounts should be one. It isn't.", mainaccounts.getNumberOfAccounts(),1.0);
		
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertTrue("New account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" . It didnt.",m1.log.containsString("recieved msgAccountCreated"));
		
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" msgRequestComplete. It didnt.",m1.log.containsString("recieved msgRequestComplete"));
		
		//assertTrue("Banker's myClient should be Null, it isn't", banker.mc!=null);
		
		//Step 3 check if scheduler returns false
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
		
		/****************ADD SECOND CLIENT************************************/
		
		//Step 4- Add the client
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
		banker.msgIWantNewAccount(m2.p, m2, "mockCustomer2", 100);
				
		//check postConditions for step 4
		assertTrue("Banker's myClient shouldn't be Null, it is", banker.myclient!=null);
				
		assertTrue("Banker's myClient's person should be p1. It isn't", banker.myclient.p==p2);
		
		assertTrue("Banker's myClient's customer should be m1. It isn't", banker.myclient.bc==m2);
				
		assertTrue("Banker's myClient's state should be wantsAccount. It isn't", banker.myclient.cs==ClientState.wantsAccount);
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
						+ "event log reads: "+ m2.log.toString(),0, m2.log.size(),0 );
				
		//Step 2 - check if the scheduler returns true
				
		assertTrue("Banker's scheduler should have return true. But it didn't.", banker.pickAndExecuteAnAction());
				
		//check postconditions for step 2/preconditions for step 3
		assertEquals("Number of existing accounts should be two. It isn't.", mainaccounts.getNumberOfAccounts(),2.0);
				
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(1).name==m2.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(1).p==p2);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(1).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(1).debt==0);
				
		assertTrue("New account's account # should be 0, it isn't.", mainaccounts.accounts.get(1).accountNumber==1);
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" . It didnt.",m2.log.containsString("recieved msgAccountCreated"));
				
		assertTrue("Mock customer should have logged \"recieved msgRequestComplete\" msgRequestComplete. It didnt.",m2.log.containsString("recieved msgRequestComplete"));
				
		//assertTrue("Banker's myClient should be Null, it isn't", banker.mc!=null);
				
		//Step 3 check if scheduler returns false
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
		
	}


	public void testOneCustomerLoanScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		

		//check preconditions
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
		assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==830);
		
		assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
		
		assertTrue("New account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
		
		//Step 1 -Add the client
		banker.msgIWantALoan(m1, 0, 500);
		
		
		//Step 2- run the scheduler
		assertTrue("Banker's scheduler should have return true. But it didn't.", banker.pickAndExecuteAnAction());
		
		
		//Step 3- check Postconditions
		
		assertTrue("Account's debt should be 500, it isn't.", mainaccounts.accounts.get(0).debt==500);
		
		assertTrue("Account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
		
		assertTrue("Account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
		
		assertTrue("Account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==850);

		assertTrue("Account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
		
		assertTrue("Mock customer should have logged \"recieved msgLoanApproved\" . It didnt.",m1.log.containsString("recieved msgLoanApproved"));
		
		//Step 4- Scheduler should have returned false
		assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());

	}

	public void testOneCustomerDeniedLoanScenario(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		mainaccounts.accounts.get(0).creditScore=550;
		//check preconditions
			assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());

			assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
				
			assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
				
			assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==550);
				
			assertTrue("New account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
				
			assertTrue("New account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
			
			//Step 1 -Add the client
			banker.msgIWantALoan(m1, 0, 500);
			
			
			//Step 2- run the scheduler
			assertTrue("Banker's scheduler should have return true. But it didn't.", banker.pickAndExecuteAnAction());
			
			//Step 3- check Postconditions
			
			assertTrue("Account's debt should be 0, it isn't.", mainaccounts.accounts.get(0).debt==0);
			
			assertTrue("Account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
			
			assertTrue("Account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
			
			assertTrue("Account's creditscore should be 570, but isn't. It is " +mainaccounts.accounts.get(0).creditScore, mainaccounts.accounts.get(0).creditScore==570);

			assertTrue("Account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
			
			assertTrue("Mock customer should have logged \"recieved msgLoanDenied\" . It didnt.",m1.log.containsString("recieved msgLoanDenied"));
		
			//Step 4- run the scheduler
			assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
	}
	
	public void testOneCustomerDeniedLoanScenarioOutstandingDebt(){
		m1 = new MockCustomer("mockCustomer1");
		PersonAgent p1 = new PersonAgent("mockCustomer1");
		m1.p=p1;
		mainaccounts.addAccount("mockCustomer1", 100, p1, 0);
		mainaccounts.accounts.get(0).creditScore=550;
		mainaccounts.accounts.get(0).debt=1000;
		//check preconditions
			assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());

			assertTrue("New account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
				
			assertTrue("New account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
				
			assertTrue("New account's creditscore should be 830, but isn't.", mainaccounts.accounts.get(0).creditScore==550);
				
			assertTrue("New account's debt should be 1000, it isn't.", mainaccounts.accounts.get(0).debt==1000);
				
			assertTrue("New account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
			
			//Step 1 -Add the client
			banker.msgIWantALoan(m1, 0, 500);
			
			
			//Step 2- run the scheduler
			assertTrue("Banker's scheduler should have return true. But it didn't.", banker.pickAndExecuteAnAction());
			
			//Step 3- check Postconditions
			
			assertTrue("Account's debt should be 1000, it isn't.", mainaccounts.accounts.get(0).debt==1000);
			
			assertTrue("Account's name should match that of customer", mainaccounts.accounts.get(0).name==m1.getName());
			
			assertTrue("Account's Person should match that of customer", mainaccounts.accounts.get(0).p==p1);
			
			assertTrue("Account's creditscore should be 530, but isn't. It is " +mainaccounts.accounts.get(0).creditScore, mainaccounts.accounts.get(0).creditScore==530);

			assertTrue("Account's account # should be 0, it isn't.", mainaccounts.accounts.get(0).accountNumber==0);
			
			assertTrue("Mock customer should have logged \"recieved msgLoanDenied\" . It didnt.",m1.log.containsString("recieved msgLoanDenied"));
		
			//Step 4- run the scheduler
			assertFalse("Banker's scheduler should have returned false. But it didn't.", banker.pickAndExecuteAnAction());
	}
}
