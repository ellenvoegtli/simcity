package mainCity.bank.test;

import role.bank.BankManagerRole;
import role.bank.BankManagerRole.myTeller;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.bank.test.mock.MockBankTeller;
import mainCity.bank.test.mock.MockBanker;
import mainCity.bank.test.mock.MockCustomer;
import junit.framework.TestCase;

public class BankManagerTest extends TestCase {
	BankManagerRole bankmanager;
	MockCustomer m1;
	MockCustomer m2;
	MockBanker b1;
	MockBankTeller t1;
	
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent b = new PersonAgent("Bankmanager");
		bankmanager = new BankManagerRole(b, b.getName());
		b.addRole(ActionType.work, bankmanager);
		
		m1 = new MockCustomer("mockCustomer1");
		m2 = new MockCustomer("mockCustomer2");
		
		b1 = new MockBanker("mockBanker");
		t1= new MockBankTeller("teller");
	}
	
	//Test a customer requesting deposit
	public void testOneNormalBankCustomerScenarioDeposit(){
		m1.manager=bankmanager;
		bankmanager.tellers.add(new myTeller(t1, 0));
		
		//check Preconditions
		assertEquals("bankmanager should have 0 banker_customers in it. Tt doesnt.", bankmanager.banker_bankCustomers.size(),0);
		assertEquals("bankmanager should have 0 teller_customers in it. Tt doesnt.", bankmanager.teller_bankCustomers.size(),0);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 1 - Add a customer
		bankmanager.msgIWantToDeposit(m1);
		
		//Check postconditions
		
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		
		assertEquals("MockBanker should have an empty eventlog before manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		assertEquals("BankManager should contain one customer in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.size(),1);
		
		assertTrue("BankManager should contain  mockcustomer m1 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(0).bc==m1);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 2-check if scheduler returns true
		assertTrue("BankManager's scheduler should have return true. But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check postconditions for step 2/preconditions for step 3
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m1);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		//Step 3 
		bankmanager.msgImLeaving(m1);
		
		//Check postconditions for step 3
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		//check if Scheduler returns true
		
		assertFalse("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
		
	}

	public void testOneNormalBankCustomerScenarioWithdraw(){
		m1.manager=bankmanager;
		bankmanager.tellers.add(new myTeller(t1, 0));
		
		//check Preconditions
		assertEquals("bankmanager should have 0 banker_customers in it. Tt doesnt.", bankmanager.banker_bankCustomers.size(),0);
		assertEquals("bankmanager should have 0 teller_customers in it. Tt doesnt.", bankmanager.teller_bankCustomers.size(),0);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 1 - Add a customer
		bankmanager.msgIWantToDeposit(m1);
		
		//Check postconditions
		
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		
		assertEquals("MockBanker should have an empty eventlog before manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		assertEquals("BankManager should contain one customer in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.size(),1);
		
		assertTrue("BankManager should contain  mockcustomer m1 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(0).bc==m1);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 2-check if scheduler returns true
		assertTrue("BankManager's scheduler should have return true. But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check postconditions for step 2/preconditions for step 3
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m1);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		//Step 3 
		bankmanager.msgImLeaving(m1);
		
		//Check postconditions for step 3
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		//check if Scheduler returns true
		
		assertFalse("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
		
	}

	public void testTwoNormalBankCustomerScenariodeposit(){
		m1.manager=bankmanager;
		m2.manager=bankmanager;
		bankmanager.tellers.add(new myTeller(t1, 0));
		
		//check Preconditions
		assertEquals("bankmanager should have 0 banker_customers in it. Tt doesnt.", bankmanager.banker_bankCustomers.size(),0);
		assertEquals("bankmanager should have 0 teller_customers in it. Tt doesnt.", bankmanager.teller_bankCustomers.size(),0);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 1 - Add a customer
		bankmanager.msgIWantToDeposit(m1);
		bankmanager.msgIWantToDeposit(m2);
		
		//Check postconditions
		
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		
		assertEquals("MockBanker should have an empty eventlog before manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		assertEquals("BankManager should contain two customer in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.size(),2);
		
		assertTrue("BankManager should contain  mockcustomer m1 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(0).bc==m1);
		assertTrue("BankManager should contain  mockcustomer m2 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(1).bc==m2);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 2-check if scheduler returns true
		assertTrue("BankManager's scheduler should have return true. But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check postconditions for step 2/preconditions for step 3
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m1);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain one. It doesn't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		//Step 3 
		bankmanager.msgImLeaving(m1);
		
		//Check postconditions for step 3
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		//check if Scheduler returns true
		
		assertTrue("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check post scheduler conditions
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m2);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain zero. It doesn't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		bankmanager.msgImLeaving(m2);
		
		//Check postconditions for second customer leaving
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		//check if Scheduler returns false
		
		assertFalse("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
	}
	public void testTwoNormalBankCustomerScenariowithdraw(){
		m1.manager=bankmanager;
		m2.manager=bankmanager;
		bankmanager.tellers.add(new myTeller(t1, 0));
		
		//check Preconditions
		assertEquals("bankmanager should have 0 banker_customers in it. Tt doesnt.", bankmanager.banker_bankCustomers.size(),0);
		assertEquals("bankmanager should have 0 teller_customers in it. Tt doesnt.", bankmanager.teller_bankCustomers.size(),0);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 1 - Add a customer
		bankmanager.msgIWantToWithdraw(m1);
		bankmanager.msgIWantToWithdraw(m2);
		
		//Check postconditions
		
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		
		assertEquals("MockBanker should have an empty eventlog before manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		assertEquals("BankManager should contain two customer in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.size(),2);
		
		assertTrue("BankManager should contain  mockcustomer m1 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(0).bc==m1);
		assertTrue("BankManager should contain  mockcustomer m2 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(1).bc==m2);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 2-check if scheduler returns true
		assertTrue("BankManager's scheduler should have return true. But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check postconditions for step 2/preconditions for step 3
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m1);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain one. It doesn't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		//Step 3 
		bankmanager.msgImLeaving(m1);
		
		//Check postconditions for step 3
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		//check if Scheduler returns true
		
		assertTrue("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check post scheduler conditions
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m2);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain zero. It doesn't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		bankmanager.msgImLeaving(m2);
		
		//Check postconditions for second customer leaving
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		//check if Scheduler returns false
		
		assertFalse("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
	}
	public void testTwoNormalBankCustomerScenariowithdrawanddeposit(){
		m1.manager=bankmanager;
		m2.manager=bankmanager;
		bankmanager.tellers.add(new myTeller(t1, 0));
		
		//check Preconditions
		assertEquals("bankmanager should have 0 banker_customers in it. Tt doesnt.", bankmanager.banker_bankCustomers.size(),0);
		assertEquals("bankmanager should have 0 teller_customers in it. Tt doesnt.", bankmanager.teller_bankCustomers.size(),0);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 1 - Add a customer
		bankmanager.msgIWantToWithdraw(m1);
		bankmanager.msgIWantToDeposit(m2);
		
		//Check postconditions
		
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		
		assertEquals("MockBanker should have an empty eventlog before manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		assertEquals("BankManager should contain two customer in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.size(),2);
		
		assertTrue("BankManager should contain  mockcustomer m1 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(0).bc==m1);
		assertTrue("BankManager should contain  mockcustomer m2 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(1).bc==m2);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 2-check if scheduler returns true
		assertTrue("BankManager's scheduler should have return true. But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check postconditions for step 2/preconditions for step 3
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m1);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain one. It doesn't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		//Step 3 
		bankmanager.msgImLeaving(m1);
		
		//Check postconditions for step 3
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		//check if Scheduler returns true
		
		assertTrue("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check post scheduler conditions
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m2);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain zero. It doesn't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		bankmanager.msgImLeaving(m2);
		
		//Check postconditions for second customer leaving
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		//check if Scheduler returns false
		
		assertFalse("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
	}
	
	public void testTwoNormalBankCustomerScenariodepositandwithdraw(){
		m1.manager=bankmanager;
		m2.manager=bankmanager;
		bankmanager.tellers.add(new myTeller(t1, 0));
		
		//check Preconditions
		assertEquals("bankmanager should have 0 banker_customers in it. Tt doesnt.", bankmanager.banker_bankCustomers.size(),0);
		assertEquals("bankmanager should have 0 teller_customers in it. Tt doesnt.", bankmanager.teller_bankCustomers.size(),0);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 1 - Add a customer
		bankmanager.msgIWantToDeposit(m1);
		bankmanager.msgIWantToWithdraw(m2);
		
		//Check postconditions
		
		assertEquals("MockCustomer should have an empty eventlog before manager scheduler is called. Instead the MockCustomer's "
				+ "event log reads: "+ m1.log.toString(),0, m1.log.size(),0 );
		
		
		assertEquals("MockBanker should have an empty eventlog before manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		assertEquals("BankManager should contain two customer in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.size(),2);
		
		assertTrue("BankManager should contain  mockcustomer m1 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(0).bc==m1);
		assertTrue("BankManager should contain  mockcustomer m2 in teller_bankCustomers. It doesnt",bankmanager.teller_bankCustomers.get(1).bc==m2);
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		//Step 2-check if scheduler returns true
		assertTrue("BankManager's scheduler should have return true. But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check postconditions for step 2/preconditions for step 3
		
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m1);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain one. It doesn't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		//Step 3 
		bankmanager.msgImLeaving(m1);
		
		//Check postconditions for step 3
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),1);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		//check if Scheduler returns true
		
		assertTrue("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
		
		//Check post scheduler conditions
		assertTrue("Mock customer should have logged \"recieved msgGoToTeller\" msgGoToTeller. It didnt.",m1.log.containsString("recieved msgGoToTeller"));
		
		assertEquals("MockBanker should have an empty eventlog when manager scheduler is called. Instead the MockBanker's "
				+ "event log reads: "+ b1.log.toString(),0, b1.log.size(),0 );
		
		assertTrue("MockTeller should have been assigned MockCustomer m1. It didnt.",bankmanager.tellers.get(0).bc==m2);
		
		assertTrue("MockTeller should have been set occupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		
		assertEquals("Bankmanager's list of teller_customers should contain zero. It doesn't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
	
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		
		bankmanager.msgImLeaving(m2);
		
		//Check postconditions for second customer leaving
		assertEquals("Bankmanager's list of teller_customers should be empty. It isnt't.", bankmanager.teller_bankCustomers.size(),0);
		
		assertEquals("Bankmanager's list of banker_customers should be empty. It isnt't.", bankmanager.banker_bankCustomers.size(),0);
		
		assertTrue("banker should be null, it isn't", bankmanager.mbanker==null);
		assertTrue("MockTeller's cust should have been set to null. It didnt.",bankmanager.tellers.get(0).bc==null);
		assertFalse("MockTeller should have been set Unoccupied. It didnt.",bankmanager.tellers.get(0).Occupied);
		//check if Scheduler returns false
		
		assertFalse("BankManager's scheduler should have returned false But it didn't.", bankmanager.pickAndExecuteAnAction());
	}
	

}
