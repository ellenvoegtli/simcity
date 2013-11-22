
package mainCity.restaurants.enaRestaurant;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.restaurants.enaRestaurant.EnaCustomerRole.AgentEvent;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;

import java.util.*;

import role.Role;
import mainCity.restaurants.enaRestaurant.gui.HostGui;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class EnaHostRole extends Agent {
	static final int NTABLES = 3;
	//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<EnaCustomerRole> waitingCustomers = Collections.synchronizedList(new ArrayList<EnaCustomerRole>());
	public List<EnaCustomerRole> waitingLine = Collections.synchronizedList(new ArrayList<EnaCustomerRole>());

	public List<EnaWaiterRole> waiters = Collections.synchronizedList(new ArrayList<EnaWaiterRole>());
	public boolean OnBreak = false;
	public static Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	public HostGui hostGui;
	Timer timer = new Timer();
	public EnaHostRole( String name) 
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

	public List<EnaCustomerRole> getWaitingCustomers() 
	{
		return waitingCustomers;
	}

	public Collection<Table> getTables() 
	{
		return tables;
	}
	
	public void addWaiterRole(EnaWaiterRole w) 
	{
		waiters.add(w);
	}
	
	public void WantsBreak(EnaWaiterRole w)
	{
		OnBreak = true;
	}
	// Messages


	public void msgIWantToEat(EnaCustomerRole cust) 
	{
		
		waitingCustomers.add(cust);
		waitingLine.add(cust);
		System.out.println("customer added to waiting list");
		stateChanged();
	}
	
	public void msgWaiterArrived(EnaCustomerRole cust)
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
	public boolean pickAndExecuteAnAction() 
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
		for(EnaWaiterRole waiter: waiters)
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
						EnaWaiterRole select = waiters.get(0);
						//////looking for waiters with the smallest customer lists
						synchronized(waiters)
						{
							for(EnaWaiterRole waiter : waiters)
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

	private void AssignWaiter(EnaCustomerRole c, Table t, EnaWaiterRole w)
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
	
public boolean CanGoOnBreak(EnaWaiterRole w)
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

public void WaiterBreak(final EnaWaiterRole  waiter)
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
		EnaCustomerRole occupiedBy;
		int tableNumber;

		Table(int tableNumber)
		{
			this.tableNumber = tableNumber;
		}

		void setOccupant(EnaCustomerRole cust)
		{
			occupiedBy = cust;
		}

		void setUnoccupied() 
		{
			occupiedBy = null;
		}

		EnaCustomerRole getOccupant() 
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

