package mainCity.restaurants.enaRestaurant;

import agent.Agent;
import mainCity.Person;
import mainCity.restaurants.enaRestaurant.EnaCustomerRole.AgentEvent;
import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;
import role.Role;

import java.util.*;
import java.util.concurrent.Semaphore;

import mainCity.restaurants.enaRestaurant.gui.EnaHostGui;
import mainCity.restaurants.enaRestaurant.gui.EnaWaiterGui;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Waiter;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class EnaWaiterRole extends Role implements Waiter{
	Timer timer = new Timer();
	public List<MyCustomers> MyCust= new ArrayList<MyCustomers>();
	public List<String> Menu = new ArrayList<String>();
	
	enum custState {waiting, seated, readyToOrder, Ordered, ReOrder, served, eating, foodRecieved, askedForBill, paying, leaving};
	//private custState customerState; 
	enum waiterState {working, wantsBreak,resting, breaking, breakOver};
	waiterState state = waiterState.working;
	//public static Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atKitchen = new Semaphore(0,true);
	private Semaphore atLobby = new Semaphore(0, true);
	private Semaphore atCashier = new Semaphore(0,true);
	private Semaphore atEntrance = new Semaphore(0,true);
	public EnaWaiterGui waiterGui;
	public boolean breakTime = false;
	public EnaHostGui hostGui;
	public EnaHostRole host;
	public EnaCookRole cook;
	public EnaCashierRole cashier;

	public EnaWaiterRole(Person p, String name)
	{
		super( p);
		this.name = name;
		
		Menu.add("steak");
		Menu.add("porkchops");
		Menu.add("lamb");
		Menu.add("lambchops");
	}

	public String getMaitreDName()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public List<MyCustomers> getMyCustomers()
	{
		return MyCust;
	}

	public Collection<Table> getTables() 
	{
		return host.getTables();
	}
	
	
	public void wantBreak() {//from animation
		print("Wants a Break");
		state = waiterState.wantsBreak;
		stateChanged();
	}
	
	// Messages

	public void msgAtTable()
	{
		atTable.release();
		stateChanged();
	}
	public void msgAtKitchen()
	{
		atKitchen.release();
		stateChanged();
	}
	public void msgAtHome()
	{
		atLobby.release();
		stateChanged();
	}
	public void msgAtCashier()
	{
		atCashier.release();
		stateChanged();
	}
	public void msgAtEntrance()
	{
		atEntrance.release();
		stateChanged();
		
	}
	
	public void msgSeatCustomer(EnaCustomerRole c, Table t) 
	{
		print("seating customer");
		MyCust.add(new MyCustomers(c,t, custState.waiting, c.getChoice()));
		stateChanged();
	}
	
	public void msgReadyToOrder( EnaCustomerRole c)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.cust == c)
			{
				System.out.println("customer ready to order");
				customer.customerState = custState.readyToOrder;
				stateChanged();
			}
		}
	}
	public void msgHereIsMyChoice(String choice, EnaCustomerRole c)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.cust == c)
			{
				System.out.println("customer chooses food " +choice);
				c.setChoice(choice);
				customer.customerState = custState.Ordered;
				stateChanged();
			}
		}
	}
	
	public void msgOrderReady(String choice, Table t)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.choice == choice && customer.table == t)
			{				
				customer.customerState = custState.served;
				stateChanged();
			}
		}
	}
	
	public void msgOutofFood(String ch)
	{
		for(MyCustomers customer: MyCust )
		{
			if(customer.choice == ch)
			{
				customer.customerState = custState.ReOrder;
				stateChanged();
			}
		}
	}
	public void msgDoneEating(EnaCustomerRole c)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.cust == c)
			{
				System.out.println("customer is finished eating");
				//customer.customerState = custState.leaving;
				stateChanged();
			}
		}
	}
	
	public void msgCheckPlease(EnaCustomerRole c, String choice)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.cust == c)
			{
				print("customer asked for bill");
				customer.customerState = custState.askedForBill;
				stateChanged();
			}
		}
	}
	
	public void msgHereIsBill(double check, Customer c)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.cust == c)
			{
				print("cashier gives bill to waiter");
				customer.customerState = custState.paying;
				stateChanged();
			}
		}
	}
	
	public void msgDoneandPaying(EnaCustomerRole c)
	{
		for(MyCustomers customer: MyCust)
		{
			if(customer.cust == c)
			{
				System.out.println("customer is finished eating and has paid");
				customer.customerState = custState.leaving;
				stateChanged();
			}
		}	
	}
	public void msgBreakApproved()
	{
		state = waiterState.breaking;
		stateChanged();
	}
	public void BreakReply()
	{
		
			breakTime = true;
			host.msgWantToGoOnBreak();
			state = waiterState.resting;
			stateChanged();
		
	}
	public void msgGetToWork()
	{
		breakTime = false;
		state = waiterState.working;
		stateChanged();	
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() 
	{
		print("waiter scheduler");
	
		if(state == waiterState.wantsBreak)
		{
			print("checking if break available");
			BreakReply();
			return true;
		}
		if(state == waiterState.breaking)
		{
			print("it is time for a break");
			GoOnBreak();
			return true;
		}
		if (state == waiterState.breakOver)
		{
			BreakChange();
			return true; 
		}
		
try
{
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.waiting)
			{
					SeatCustomer(customer);
					print("customer..............seated");
					customer.customerState = custState.Ordered;
					return true;
			}
		}
		
		for (MyCustomers customer : MyCust)
		{
		
			if(customer.customerState == custState.readyToOrder)
			{
				print("" + MyCust.size());
				customer.customerState = custState.Ordered;
				TakeOrder(customer);
				return true;
			}
		}
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.Ordered)
			{
				//System.out.println("The food is going to be delivered");
				return true;
			}
		}
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.ReOrder)
			{	
				customer.customerState = custState.Ordered;
				TakeNewOrder(customer);
				return true;
			}
		}
	
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.served)
			{
				Deliver(customer);
				customer.customerState = custState.eating;
				return true;
			}
		}
		
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.askedForBill)
			{
				print("waiter will now bring the tab to the customer");
				BringTab(customer);
				customer.customerState = custState.paying;
				return true;
			}
		}
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.paying)
			{
				print("customer is paying");			
				PayCashier(customer);
				customer.customerState = custState.leaving;
				return true;
			}
		}
		for (MyCustomers customer : MyCust)
		{
			if(customer.customerState == custState.leaving)
			{
				EmptyTable(customer);
				return true;
			}
		}
		
}

catch(ConcurrentModificationException e){};
	

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	public void SeatCustomer(MyCustomers c)
	{
		waiterGui.DoGetCustomer(c.cust);
		print("at entrance semaphore acquiring");
		try {

			atEntrance.acquire();

		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}		
		c.cust.msgFollowToTable();	
		DoSeatCustomer(c);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		host.msgWaiterArrived(c.cust);

		c.table.setOccupant(c.cust);
		waiterGui.DoLeaveCustomer();
		try{
			atLobby.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println("LEAVING CUST");
		
	}

	
	private void TakeOrder(MyCustomers c)
	{
		waiterGui.DoGoToTable(c.cust, c.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.cust.msgWhatWouldYouLike();
		PassOrder(c);
	}
	
	private void TakeNewOrder(MyCustomers c)
	{
		waiterGui.DoGoToTable(c.cust, c.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print ("OLD CHOICE " + c.choice + "IS OUT OF STOCK CHOOSE SOMETHING ELSE");
		c.cust.msgWhatElseWouldYouLike();
		for(int i=0; i<Menu.size(); i++)
		{
		  if(c.cust.getName().equals("onlyChoice"))
		  {
			  System.out.println("no money for new choice, customer will leave the restaurant");
			  c.cust.getGui().DoExitRestaurant();
			  waiterGui.DoLeaveCustomer();
			  EmptyTable(c);
		  }
		  else
		  {
			  
		  
			if(c.choice == Menu.get(3))
			{
				c.setChoice(Menu.get(0));
				c.cust.setCh(Menu.get(0));
				System.out.println("GUI SHOULD BE TAKING ORDER TO KITCHEN");
				waiterGui.DoGoToKitchen();
				waiterGui.SubmitOrder(c.choice);
				try {
					atKitchen.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				cook.msgHereIsTheOrder(this, c.choice, c.table);
				print("Delivering new order to cook " + c.choice);
				waiterGui.DoLeaveCustomer();
				break;
			}
			else if (c.choice == Menu.get(i))
			{
				print("setting new menu choice");
				c.setChoice(Menu.get(i+1));
				c.cust.setCh(Menu.get(i+1));
				print("new choice" + c.choice);		
				
				
				System.out.println("GUI SHOULD BE TAKING ORDER TO KITCHEN");
				waiterGui.DoGoToKitchen();
				waiterGui.SubmitOrder(c.choice);
				try {
					atKitchen.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				cook.msgHereIsTheOrder(this, c.choice, c.table);
				print("Delivering New OrderToCook  " +c.choice);
				waiterGui.DoLeaveCustomer();
			break;
			}
		  }
		}
	
	}
	
	private void PassOrder(MyCustomers c)
	{
		System.out.println("GUI SHOULD BE TAKING ORDER TO KITCHEN");
		waiterGui.DoGoToKitchen();
		waiterGui.SubmitOrder(c.choice);
		try {
			atKitchen.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cook.msgHereIsTheOrder(this, c.choice, c.table);
		waiterGui.DoLeaveCustomer();
		
	}
	
	private void Deliver(MyCustomers c)
	{		
		waiterGui.DoGoToKitchen();
		try {
			atKitchen.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		cook.msgRemovePlating(c.choice);

		waiterGui.Arriving(c.choice);

		System.out.println("Delivering food to customer, being served");
		waiterGui.DoServe(c.choice, c.table);	

		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//stateChanged();
		
		c.cust.msgHereIsYourFood(c.choice);
		
		waiterGui.DoLeaveCustomer();
		waiterGui.Arriving();
		
	}
	
	private void BringTab(MyCustomers c)
	{		
		print("waiter going to the cashier and then table station");
		waiterGui.DoGoToCashier();
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cashier.msgComputeBill(c.choice, c.cust);
		
		waiterGui.DoGoToTable(c.cust, c.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		waiterGui.DoLeaveCustomer();
		
	}
	
	private void PayCashier(MyCustomers c)
	{
		c.cust.msgHereIsYourCheck();
	}

	
	private void EmptyTable(MyCustomers c)
	{
		host.msgTableIsFree(c.table);
		MyCust.remove(c);
	}
	
	private void GoOnBreak()
	{
		waiterGui.DoLeaveCustomer();
		//waiterGui.DoGoOnBreak();
		//print( " is taking a break");
		timer.schedule(new TimerTask() {
			public void run() 
			{
				print("break over");
				state = waiterState.breakOver;
				stateChanged();
			}
		},
		5000);
		
		
	}
	private void BreakChange()
	{
		breakTime = false;
		state = waiterState.resting;
		host.msgOffBreak();
		stateChanged();
	}
	private void DoSeatCustomer(MyCustomers c) 
	{
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + c.cust + " at " + c.table);
		
		waiterGui.setXNum(c.table.tableNumber);
		c.cust.setNum(c.table.tableNumber);
		
				waiterGui.DoBringToTable(c.cust, c.table.tableNumber);
	}

	//utilities
	public void setGui(EnaWaiterGui gui) {
		waiterGui = gui;
	}

	public EnaWaiterGui getGui() 
	{
		return waiterGui;
	}
	
	public void setCook(EnaCookRole cook)
	{
		this.cook = cook;
	}
	public void setHost(EnaHostRole host)
	{
		this.host = host;
	}
	public void setCashier(EnaCashierRole cashier)
	{
		this.cashier = cashier;
		if(name.equals("noCash"))
		{
			cashier.msgNoMoney();
		}
		else if (!name.equals("noCash"))
		{
			try{
				cashier.msgRestMoney();
			}catch(NullPointerException e){};
		}
		
		
		
	}

	
	
	
	public class MyCustomers 
	{
		EnaCustomerRole cust;
		Table table;
		String choice;
		custState customerState;

		MyCustomers(EnaCustomerRole c, Table t, custState cSt, String fdc )
		{
			cust = c;
			table = t;
			customerState = cSt;	
			choice = fdc;
			
		}
		
	
		public void setChoice(String ch)
		{
			choice = ch; 
		}

	}




	
}

