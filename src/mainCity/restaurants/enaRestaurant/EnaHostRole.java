
package mainCity.restaurants.enaRestaurant;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.enaRestaurant.EnaCustomerRole.AgentEvent;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;

import java.util.*;

import role.Role;
import role.ellenRestaurant.EllenWaiterRole;
import role.ellenRestaurant.EllenHostRole.MyWaiter;
import role.ellenRestaurant.EllenHostRole.Table;
import mainCity.restaurants.enaRestaurant.gui.EnaHostGui;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Host;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class EnaHostRole extends Role implements Host {
	static final int NTABLES = 3;
	//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<EnaCustomerRole> waitingCustomers = Collections.synchronizedList(new ArrayList<EnaCustomerRole>());
	public List<EnaCustomerRole> waitingLine = Collections.synchronizedList(new ArrayList<EnaCustomerRole>());
	private EnaCookRole cook;
	private EnaCashierRole cashier;
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public boolean OnBreak = false;
	public static Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	private boolean onShift;
	private boolean newHost;
	//public EnaHostGui hostGui;
	Timer timer = new Timer();
	public EnaHostRole( PersonAgent p, String name) 
	{
		super(p);

		this.name = name;
		onShift = true;
		newHost = true;
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
	
	
	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
	}

	// Messages


	public void msgIWantToEat(EnaCustomerRole cust) 
	{
		
		waitingCustomers.add(cust);
		waitingLine.add(cust);
		log("customer added to waiting list......");
		stateChanged();
	}
	
	public void msgWaiterArrived(EnaCustomerRole cust)
	{
		waitingLine.remove(cust);
		stateChanged();
	}
	
	public void msgWantToGoOnBreak()
	{
		log("Recieved message that the waiter wants to go on a break");
		OnBreak = true;
		stateChanged();
	}
	public void msgOffBreak()
	{
		log("waiter is back from break");
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

	public void msgShiftEnd()
	{
		onShift = false;
		stateChanged();
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() 
	{
		if(newHost)
		{
			ContactList.getInstance().setEnaHost(this);
			newHost=false;
		}
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
		/*synchronized(waiters)
		{
		for(EnaWaiterRole waiter: waiters)
		{
			if(!CanGoOnBreak(waiter))
			{
				log("waiter cannot go on break, get back to work");
				waiter.msgGetToWork();
			}
			if(OnBreak && CanGoOnBreak(waiter) && waiter.breakTime)
			{
				WaiterBreak(waiter);
			}
		}
		}*/
		
		for (Table table : tables) 
		{
			if (!table.isOccupied()) 
			{
				if (!waitingCustomers.isEmpty() )
				{
					if(!waiters.isEmpty())
					{
						log("there is an empty table and a customer waiting and an available waiter");
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
		
		if(!onShift)
		{
			closeBuilding();
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void AssignWaiter(EnaCustomerRole c, Table t, EnaWaiterRole w)
	{
		log("Assgning waiter to the customer");
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
public boolean isItOpen() 
{
	return (cook != null && cook.isActive()) && (cashier != null && cashier.isActive());
}

public boolean closeBuilding()
{
	if(!waitingCustomers.isEmpty()) return false;
	
	for(Table t : tables) {
		if(t.isOccupied()) {
			return false;
		}
	}
	
	double payroll = 0;
	for(MyWaiter w : waiters) {
		EnaWaiterRole temp = ((EnaWaiterRole) w.);
		double amount = temp.getShiftDuration()*4.75;
		temp.msgGoOffDuty(amount);
		payroll += amount;
	}
	
	if(cashier != null) {
		payroll += cashier.getShiftDuration()*6.0;
		cashier.msgGoOffDuty(cashier.getShiftDuration()*6.0);
	}
	if(cook != null){
		payroll += cashier.getShiftDuration()*7.50;
		cook.msgGoOffDuty(cook.getShiftDuration()*7.50);
	}
	
	addToCash(getShiftDuration()*9.50);
	payroll += getShiftDuration()*9.50;		
	
	cashier.deductCash(payroll);
	setInactive();
	onDuty = true;
	return true;
}
	//utilities

	/*public void setGui(EnaHostGui gui) {
		hostGui = gui;
	}

	public EnaHostGui getGui() {
		return hostGui;
	}*/

	public class MyWaiters
	{
		
	}
	public class Table 
	{
		Customer occupiedBy;
		int tableNumber;

		Table(int tableNumber)
		{
			this.tableNumber = tableNumber;
		}

		public void setOccupant(Customer cust)
		{
			occupiedBy = cust;
		}

		void setUnoccupied() 
		{
			occupiedBy = null;
		}

		Customer getOccupant() 
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

	public void setCook(EnaCookRole cook) 
	{
		this.cook = cook;
	}

	public void setCashier(EnaCashierRole cashier) 
	{
		this.cashier = cashier;		
	}

	public boolean isOpen() {
		return false;
	}

	@Override
	public void msgWaiterArrived(Customer cust) {
		// TODO Auto-generated method stub
		
	}

	
}

