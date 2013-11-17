package mainCity.restaurants.restaurant_zhangdt;

import agent.Agent;
import mainCity.restaurants.restaurant_zhangdt.CustomerAgent.AgentState;
import mainCity.restaurants.restaurant_zhangdt.gui.WaiterGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

public class HostAgent extends Agent {
	
/*   Data   */
	
	//Global variable storing number of tables in the restaurant
	static final int NTABLES = 3;
	
	//List that stores all the customers that are hungry and are waiting to get seated
	public List<CustomerAgent> waitingCustomers
	= new ArrayList<CustomerAgent>();
	
	//Collection stores all the available tables
	public Collection<Table> tables;
	
	//List that stores all available waiters
	public List<WaiterAgent> waiters 
	= new ArrayList<WaiterAgent>(); 
	
	//Waiter Index, used to locate certain waiters
	private int waiterLoc;
	
	//Name of the Host
	private String name;

	//Constructor
	public HostAgent(String name) {
		super();
		waiterLoc = -1;
		this.name = name;
		// Make some tables, hardcoded for now
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		
		
		for (int ix = 1; ix <= NTABLES; ix++) {
			if(ix == 1) { 
				tables.add(new Table(1, 200, 250));
			}
			
			if(ix == 2) { 
				tables.add(new Table(2, 300, 175));
			}
			
			if(ix == 3) { 
				tables.add(new Table(3, 200, 100));
			}
			
			//how you add to a collections
		}	
		
	}
	
	//Host States to handle break requests 
	public enum HostState {none, recievedRequest, handledRequest}; 
	private HostState hState = HostState.none; 


	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	
/*  Messages   */

	public void msgIWantFood(CustomerAgent cust) {
		print("msgIWantFood() called");
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void msgRequestBreak(WaiterAgent w) {
		print("msgRequestBreak called by " + w); 
		
		for(int i=0; i<waiters.size(); i++){
			if(w == waiters.get(i)){ 
				waiterLoc = i;
			}
		}
		
		hState = HostState.recievedRequest; 
		stateChanged();
	}
	
	public void msgImOffBreak(WaiterAgent w){ 
		print("msgWaiterOffBreak called by " + w); 
		waiters.add(w); 
		stateChanged();
	}



/*   Scheduler   */
	
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if(hState == HostState.recievedRequest){ 
			BreakAppropriate(); 
		}
		synchronized(tables){
			for (Table table : tables) {
				if (table.isOccupied() == false) {
					if (!waitingCustomers.isEmpty()) {
						if (waiters.size() != 0) {
							designateCustomer(waitingCustomers.get(0), table);//the action
							return true;//return true to the abstract agent to reinvoke the scheduler.
						}
					}
				}
			} 
		}
		
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

/*   Actions    */

	private void designateCustomer(CustomerAgent customer, Table table) {
		WaiterAgent w = new WaiterAgent(""); 
		w = waiters.get(0); 
		
		
		//Mechanism that chooses which waiter to assign customers to. Compares the list of customers within each waiter
		//to see which has the least amount of customers and assigns the new customer to that waiter.
		for(int i=0; i<waiters.size(); i++) {
			if( waiters.get(i).getCustomerList().size() < w.getCustomerList().size() ) { 
				w = waiters.get(i);
			}
		}
		
		
		customer.setWaiter(w);
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		w.msgSeatCustomer(customer, table);
		
		stateChanged();
		
	}
	
	private void BreakAppropriate() { 
		if(waiters.size() == 1){
			print("You're the only waiter working!");
			waiters.get(waiterLoc).msgBreakDenied(); 
		}
		else { 
			waiters.get(waiterLoc).msgBreakGranted();
			waiters.remove(waiterLoc); 
		}
		hState = HostState.handledRequest;
	}
	

/*   Utilities   */ 
	
	public void addWaiter(WaiterAgent w) { 
		waiters.add(w); 
		stateChanged();
	}
	
	public void DealWithImpatience(){ 
		for(int i=0; i<waitingCustomers.size(); i++){
			print(waitingCustomers.get(i).getCustomerName() + " leaving...");
			waitingCustomers.remove(i);	
		}
	}
	
	public class Table {
		CustomerAgent occupiedBy;
		int tableNumber;
		int xPos;
		int yPos;
		

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
		
		Table(int tableNumber, int xPos, int yPos) {
			this.tableNumber = tableNumber;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
		
		void setX(int xPos) {
			this.xPos = xPos;
		}
		
		void setY(int yPos) {
			this.yPos = yPos;
		}
		
		int getTable() {
			return tableNumber;
		}
		
		int getX() {
			return xPos;
		}
		
		int getY() {
			return yPos;
		}
	}
}

