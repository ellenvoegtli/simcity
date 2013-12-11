package role.davidRestaurant;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.ManagerRole;
import role.davidRestaurant.*; 
import mainCity.restaurants.restaurant_zhangdt.gui.WaiterGui;

import java.util.*;
import java.util.concurrent.Semaphore;

import role.Role;
import role.davidRestaurant.DavidCustomerRole.AgentState;

/**
 * Restaurant Host Agent
 */

public class DavidHostRole extends Role implements ManagerRole{
	
/*   Data   */
	
	//Global variable storing number of tables in the restaurant
	static final int NTABLES = 3;
	
	//List that stores all the customers that are hungry and are waiting to get seated
	public List<DavidCustomerRole> waitingCustomers
	= new ArrayList<DavidCustomerRole>();
	
	//Collection stores all the available tables
	public Collection<Table> tables;
	
	//List that stores all available waiters
	public List<MyWaiter> waiters = new ArrayList<MyWaiter>(); 
	
	//Waiter Index, used to locate certain waiters
	private int waiterLoc;
	
	//Name of the Host
	private String name;
	
	//Connection with other agents
	DavidCashierRole cashier;
	DavidCookRole cook;
	
	boolean onDuty;
	boolean entered;

	//Constructor
	public DavidHostRole(String name, PersonAgent p) {
		super(p);
		waiterLoc = -1;
		this.name = name;
		boolean onDuty = true;
		entered = true;
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
	
	public void log(String s) { 
		AlertLog.getInstance().logMessage(AlertTag.DAVID_RESTAURANT, this.getName(), s); 
		AlertLog.getInstance().logMessage(AlertTag.DAVID_HOST, this.getName(), s);
	}
	
/*  Messages   */

	public void msgIWantFood(DavidCustomerRole cust) {
		log("msgIWantFood() called");
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(DavidCustomerRole cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				log(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void msgRequestBreak(DavidWaiterRole w) {
		log("msgRequestBreak called by " + w); 
		
		for(int i=0; i<waiters.size(); i++){
			if(w == waiters.get(i).waiter){ 
				waiterLoc = i;
			}
		}
		
		hState = HostState.recievedRequest; 
		stateChanged();
	}
	
	public void msgImOffBreak(DavidWaiterRole w){ 
		log("msgWaiterOffBreak called by " + w); 
		//waiters.add(w); 
		stateChanged();
	}



/*   Scheduler   */
	
	public boolean pickAndExecuteAnAction() {
		if(!checkWaiters()) {
			return false;
		}
		
		if(entered) { 
			ContactList.getInstance().setDavidHost(this); 
			entered = false;
		}
		
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

	private void designateCustomer(DavidCustomerRole customer, Table table) {
		DavidWaiterRole w; 
		w = waiters.get(0).waiter; 
		
		
		//Mechanism that chooses which waiter to assign customers to. Compares the list of customers within each waiter
		//to see which has the least amount of customers and assigns the new customer to that waiter.
		for(int i=0; i<waiters.size(); i++) {
			if( waiters.get(i).waiter.getCustomerList().size() < w.getCustomerList().size() ) { 
				w = waiters.get(i).waiter;
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
			log("You're the only waiter working!");
			waiters.get(waiterLoc).waiter.msgBreakDenied(); 
		}
		else { 
			waiters.get(waiterLoc).waiter.msgBreakGranted();
			waiters.remove(waiterLoc); 
		}
		hState = HostState.handledRequest;
	}
	

/*   Utilities   */ 
	
	public void addWaiter(DavidWaiterRole w) { 
		waiters.add(new MyWaiter(w));
		stateChanged();
	}
	
	public void DealWithImpatience(){ 
		for(int i=0; i<waitingCustomers.size(); i++){
			log(waitingCustomers.get(i).getCustomerName() + " leaving...");
			waitingCustomers.remove(i);	
		}
	}
	
	public static class Table {
		DavidCustomerRole occupiedBy;
		public int tableNumber;
		int xPos;
		int yPos;
		

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
		
		public Table(int tableNumber, int xPos, int yPos) {
			this.tableNumber = tableNumber;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		void setOccupant(DavidCustomerRole cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		DavidCustomerRole getOccupant() {
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
	
	public boolean isOpen() { 
		return (cook != null && cook.isActive()) && (cashier != null && cashier.isActive());
	}

	public void setCashier(DavidCashierRole cashier) {
		this.cashier = cashier;
	}

	public void setCook(DavidCookRole cook) {
		this.cook = cook;
		
	}

	public void msgEndShift() {
		onDuty = false;
		stateChanged();
	}

	
	private boolean checkWaiters() { 
		for(MyWaiter w : waiters) { 
			if(((DavidWaiterRole) w.waiter).isActive()) { 
				return true;
			}
		}
		
		return false;
	}
	
	public boolean closeBuilding() { 
		if(!waitingCustomers.isEmpty()) { 
			return false;
		}
		
		for(Table t: tables) { 
			if(t.isOccupied()) { 
				return false; 
			}
		}
		
		double payroll = 0; 
		for(MyWaiter w : waiters) { 
			DavidWaiterRole temp = ((DavidWaiterRole) w.waiter); 
			double amount = temp.getShiftDuration()*5.75; 
			temp.msgGoOffDuty(amount); 
			payroll += amount; 
		}
		
		if(cashier != null) { 
			payroll += cashier.getShiftDuration()*6.0; 
			cashier.msgGoOffDuty(cashier.getShiftDuration()*6.0); 
		}
		
		if(cook != null) { 
			payroll += cashier.getShiftDuration()*8.0; 
			cook.msgGoOffDuty(cook.getShiftDuration()*8.0); 
		}
		
		addToCash(getShiftDuration()*10.0); 
		payroll += getShiftDuration()*10.0; 
		
		cashier.deductCash(payroll); 
		setInactive(); 
		onDuty = true; 
		entered = true;
		return true;
	}
	
	public enum WaiterState {onDuty, requested, onBreak}; 
	
	class MyWaiter { 
		DavidWaiterRole waiter; 
		WaiterState state; 
		
		MyWaiter(DavidWaiterRole w) { 
			this.waiter = w; 
			state = WaiterState.onDuty;
		}
	}
}

