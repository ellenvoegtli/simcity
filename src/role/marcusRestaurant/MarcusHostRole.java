package role.marcusRestaurant;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.ManagerRole;
import mainCity.interfaces.WorkerRole;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;

import role.Role;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class MarcusHostRole extends Role implements ManagerRole, Host {
	static final int NTABLES = 4;//a global for the number of tables.
	private MarcusCookRole cook;
	private MarcusCashierRole cashier;
	private List<Customer> waitingCustomers = Collections.synchronizedList(new ArrayList<Customer>());
	private List<MyWaiter> waitersList = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public Collection<MarcusTable> tables;
	boolean newCustomer;
	private boolean onDuty;
	private boolean entered;
	
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	
	private String name;
	private int customerCount;
	//private Semaphore atTable = new Semaphore(0,true);

	//public WaiterGui waiterGui = null;

	public MarcusHostRole(PersonAgent p, String name) {
		super(p);

		this.name = name;
		customerCount = 0;
		newCustomer = false;
		onDuty = true;
		entered = true;
		
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<MarcusTable>(NTABLES));
		
		synchronized(tables) {
			for (int ix = 1; ix <= NTABLES; ix++) {
				tables.add(new MarcusTable(ix));//how you add to a collections
			}
		}
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
	
	public void setCashier(MarcusCashierRole c) {
		cashier = c;
	}
	public void setCook(MarcusCookRole c) {
		cook = c;
	}
	
	public MarcusCookRole getCook() {
		return cook;
	}
	
	public void addWaiter(Waiter w) {
		waitersList.add(new MyWaiter(w));
		stateChanged();
	}
	
	// Messages	
	@Override
	public void msgIWantToEat(Customer cust) {
		output(cust + " wants to eat at Marcus' Grilled Cheese Restaurant");
		waitingCustomers.add(cust);
		newCustomer = true;
		stateChanged();
	}

	@Override
	public void msgIWillWait(Customer c) {
		output(c + " decided to wait");
		waitingCustomers.add(c);
		stateChanged();
	}
	
	@Override
	public void msgTableIsClear(MarcusTable t) {
		output(t + " is now clear");
		t.setUnoccupied();
		customerCount--;
		stateChanged();
	}

	@Override
	public void msgWantToGoOnBreak(Waiter w) {
		output(w + " just requested to go on break");
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.waiter == w) {
					waiter.state = WaiterState.requested;
					stateChanged();
					return;
				}
			}
		}
	}
	
	@Override
	public void msgBackOnDuty(Waiter w) {
		output(w + " is now back on duty");
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.waiter == w) {
					waiter.state = WaiterState.onDuty;
					stateChanged();
					return;
				}
			}
		}
	}
	
	@Override
	public void msgEndShift() {
		onDuty = false;
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(entered) {
			ContactList.getInstance().setMarcusHost(this);
			entered = false;
		}
		
		if(restaurantFull() && newCustomer && !waitingCustomers.isEmpty()) {//If the restaurant is full, we get the customer in the back of the queue 
			waitingCustomers.get(0).msgWantToWait();
			waitingCustomers.remove(waitingCustomers.get(0));
			newCustomer = false;
			return true;
		}
		
		synchronized(tables) {
			for (MarcusTable table : tables) {
				if (!table.isOccupied() && !waitingCustomers.isEmpty() && !waitersList.isEmpty()) {
					if(!checkWaiters()) {
						return false;
					}

					seatCustomer(waitingCustomers.get(waitingCustomers.size()-1), table);//the action
					return true;//return true to the abstract agent to reinvoke the scheduler.
				}
			}
		}
		
		synchronized(waitersList) {
			for(MyWaiter w : waitersList) {
				if(w.state == WaiterState.requested) {
					handleRequest(w);
					return true;
				}
			}
		}

		if(!onDuty) {
			closeBuilding();
		}
		
		return false;
	}

	// Actions
	private void seatCustomer(Customer customer, MarcusTable table) {
		output("Calling a waiter to take " + customer + " to " + table);		
		customerCount++;
		Waiter temp = chooseWaiter();
		
		if(temp == null) return;
		
		temp.msgSeatAtTable(customer, table);
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
	}
	
	private Waiter chooseWaiter() {
		int holder = (customerCount / waitersList.size());
		
		int counter = 0;
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.state == WaiterState.onBreak || !((MarcusWaiterRole) waiter.waiter).isActive()) {
					++counter;
				}
			}
		}
		
		synchronized(waitersList) {
			for(MyWaiter w : waitersList) {
				if(w.state != WaiterState.onBreak && ((MarcusWaiterRole) w.waiter).isActive()) {
					if(((customerCount < waitersList.size() && w.waiter.getCustomerCount() == 0)) || (waitersList.size() - counter) == 1) {
						return w.waiter;
					}
					else if(w.waiter.getCustomerCount() < holder) {
						return w.waiter;
					}
				}
			}
		}
		
		return waitersList.get(0).waiter; //Backup case, should not be reached though
	}
	
	private void handleRequest(MyWaiter w) {
		output("Handling waiter's break request");
		int counter = 0;
		
		synchronized(waitersList) {
			for(MyWaiter waiter : waitersList) {
				if(waiter.state == WaiterState.onBreak) {
					++counter;
				}
			}
		}

		if(waitersList.size() == 1 || (waitersList.size() - counter) == 1) {//makes sure someone is always on duty
			w.state = WaiterState.onDuty;
			w.waiter.msgBreakReply(false);
			return;
		}
		
		w.state = WaiterState.onBreak;
		w.waiter.msgBreakReply(true);
	}
	
	private void output(String input) {
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_RESTAURANT, this.getName(), input);
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_HOST, this.getName(), input);
	}
	
	private boolean restaurantFull() {
		synchronized(tables) {
			for(MarcusTable table : tables) {
				if (!table.isOccupied()) {
					return false;
				}
			}
		}
	
		return true;
	}
	
	private boolean checkWaiters() {		
		for(MyWaiter w : waitersList) {
			if(((MarcusWaiterRole) w.waiter).isActive()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean closeBuilding() {
		if(!waitingCustomers.isEmpty()) return false;
		
		for(MarcusTable t : tables) {
			if(t.isOccupied()) {
				return false;
			}
		}
		
		double payroll = 0;
		for(MyWaiter w : waitersList) {
			MarcusWaiterRole temp = ((MarcusWaiterRole) w.waiter);
			double amount = temp.getShiftDuration()*4.75;
			temp.msgGoOffDuty(amount);
			payroll += amount;
		}
		
		if(cashier != null) {
			payroll += cashier.getShiftDuration()*6.0;
			cashier.msgGoOffDuty(cashier.getShiftDuration()*6.0);
		}
		if(cook != null){
			payroll += cook.getShiftDuration()*7.50;
			cook.msgGoOffDuty(cook.getShiftDuration()*7.50);
		}
		
		addToCash(getShiftDuration()*9.50);
		payroll += getShiftDuration()*9.50;		
		
		if(cashier != null)
			cashier.deductCash(payroll);
		
		setInactive();
		onDuty = true;
		entered = true;
		return true;
	}

	public boolean lastCustomer() {
		return onDuty;
	}
	
	public boolean isOpen() {
		return (cook != null && cook.isActive()) && (cashier != null && cashier.isActive());
	}
	
	public enum WaiterState {onDuty, requested, onBreak};
	
	class MyWaiter {
		Waiter waiter;
		WaiterState state;
		
		MyWaiter(Waiter w) {
			this.waiter = w;
			state = WaiterState.onDuty;
		}
	}
}

