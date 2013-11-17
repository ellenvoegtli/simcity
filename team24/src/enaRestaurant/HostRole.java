
package enaRestaurant;

import agent.Agent;
import enaRestaurant.CustomerRole.AgentEvent;
import enaRestaurant.WaiterRole;

import java.util.*;
//import java.util.concurrent.Semaphore;

import enaRestaurant.gui.HostGui;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostRole extends Agent {
	static final int NTABLES = 3;
	//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerRole> waitingCustomers = Collections.synchronizedList(new ArrayList<CustomerRole>());
	public List<CustomerRole> waitingLine = Collections.synchronizedList(new ArrayList<CustomerRole>());

	public List<WaiterRole> waiters = Collections.synchronizedList(new ArrayList<WaiterRole>());
	public boolean OnBreak = false;
	public static Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	public HostGui hostGui;
	Timer timer = new Timer();
	public HostRole(String name) 
	{
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 0; ix < NTABLES; ix++) 
		{
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<CustomerRole> getWaitingCustomers() 
	{
		return waitingCustomers;
	}

	public Collection<Table> getTables() 
	{
		return tables;
	}
	
	public void addWaiterRole(WaiterRole w) 
	{
		waiters.add(w);
	}
	
	public void WantsBreak(WaiterRole w)
	{
		OnBreak = true;
	}
	// Messages


	public void msgIWantToEat(CustomerRole cust) 
	{
		
		waitingCustomers.add(cust);
		waitingLine.add(cust);
		System.out.println("customer added to waiting list");
		stateChanged();
	}
	
	public void msgWaiterArrived(CustomerRole cust)
	{
		waitingLine.remove(cust);
		stateChanged();
	}
	
	public void msgWantToGoOnBreak()
	{
		print("Recieved message that the waiter wants to go on a break");
		OnBreak = true;
		stateChanged();
	}
	public void msgOffBreak()
	{
		print("waiter is back from break");
		OnBreak = false;
		stateChanged();
	}

	public void msgTableIsFree(Table t) 
	{
		for (Table table : tables) 
		{
			if (table == t) 
			{
				t.setUnoccupied();
				stateChanged();
			}
		}
	}

	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() 
	{
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
		synchronized(waitingLine)
		{
			for(int i = 0; i<waitingLine.size(); i++)
			{
				waitingLine.get(i).setXPos( 50 + i * (20 + 3) );
				waitingLine.get(i).setXDest( 50 + i * (20 + 3) );

			}
		}
		synchronized(waiters)
		{
		for(WaiterRole waiter: waiters)
		{
			if(!CanGoOnBreak(waiter))
			{
				print("waiter cannot go on break, get back to work");
				waiter.msgGetToWork();
			}
			if(OnBreak && CanGoOnBreak(waiter) && waiter.breakTime)
			{
				WaiterBreak(waiter);
			}
		}
		}
		
		for (Table table : tables) 
		{
			if (!table.isOccupied()) 
			{
				if (!waitingCustomers.isEmpty() )
				{
					if(!waiters.isEmpty())
					{
						print("there is an empty table and a customer waiting and an available waiter");
						int min = waiters.get(0).getMyCustomers().size();
						int t;
						WaiterRole select = waiters.get(0);
						//////looking for waiters with the smallest customer lists
						synchronized(waiters)
						{
							for(WaiterRole waiter : waiters)
							{
								if(waiter.breakTime == false)
								{
									t = waiter.getMyCustomers().size();
									if(t<= min)
									{
										min = t;
										select = waiter;
									}
								}
							}
						}
						
						AssignWaiter(waitingCustomers.get(0), table, select);
						return true;//return true to the abstract agent to reinvoke the scheduler.

					}
					
				}
			}
				
		}
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void AssignWaiter(CustomerRole c, Table t, WaiterRole w)
	{
		System.out.println("Assgning waiter to the customer");
		c.setWaiter(w);
		w.msgSeatCustomer(c, t);
		t.setOccupant(c);
		synchronized(waitingCustomers)
		{
			waitingCustomers.remove(c);
		}
	}
	
public boolean CanGoOnBreak(WaiterRole w)
{
	int waitersWorking = 0;
	for(int i=0; i<waiters.size(); i++)
	{
		waitersWorking++;
		if(waitersWorking > 1)
		{
			return true;
		}
	}
	return false;
	
}

public void WaiterBreak(final WaiterRole  waiter)
{
	waiter.msgBreakApproved();	

}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	public class Table 
	{
		CustomerRole occupiedBy;
		int tableNumber;

		Table(int tableNumber)
		{
			this.tableNumber = tableNumber;
		}

		void setOccupant(CustomerRole cust)
		{
			occupiedBy = cust;
		}

		void setUnoccupied() 
		{
			occupiedBy = null;
		}

		CustomerRole getOccupant() 
		{
			return occupiedBy;
		}

		boolean isOccupied() 
		{
			return occupiedBy != null;
		}

		public String toString() 
		{
			return "table " + tableNumber;
		}
		
		public int getTableNumber()
		{
			return tableNumber;
		}
	}
}

