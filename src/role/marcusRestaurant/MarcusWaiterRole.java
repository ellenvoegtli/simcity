package role.marcusRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.WorkerRole;
import mainCity.restaurants.marcusRestaurant.interfaces.*;
import mainCity.restaurants.marcusRestaurant.MarcusMenu;
import mainCity.restaurants.marcusRestaurant.MarcusTable;

import java.util.*;
import java.util.concurrent.Semaphore;

import role.Role;

public abstract class MarcusWaiterRole extends Role implements Waiter, WorkerRole {
	public WaiterGuiInterface waiterGui = null;

	static final int NTABLES = 3;//a global for the number of tables.

	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	protected Semaphore isMovingSem = new Semaphore(0, true);
	protected boolean onBreak, onDuty, requested, tired;
	protected String name;
	protected Host host;
	protected MarcusCookRole cook;
	protected Cashier cashier;
	protected MarcusMenu waiterMenu;
	protected boolean needGui;
	
	Timer timer = new Timer();
	
	public MarcusWaiterRole(PersonAgent p, String name) {
		super(p);

		this.name = name;
		onBreak = tired = requested = false;
		onDuty = true;
		waiterMenu = new MarcusMenu();
		needGui = false;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<MyCustomer> getWaitingCustomers() {
		return customers;
	}

	public int getCustomerCount() {
		return customers.size();
	}
	
	public void setHost(Host h) {
		this.host = h;
	}
	
	public void setCook(MarcusCookRole c) {
		this.cook = c;
	}
	
	public void setCashier(Cashier c) {
		this.cashier = c;
	}
	
	public boolean checkOnBreak() {
		return onBreak;
	}
	
	// Messages
	public void msgRequestBreak() {
		tired = true;
		stateChanged();
	}
	
	public void msgGoOffDuty(double amount) {
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}
	
	public void msgBreakReply(boolean r) {
		output("Can " + this + " go on break? " + r);
		onBreak = tired = requested = r;
		stateChanged();
	}
	
	public void msgSeatAtTable(Customer cust, MarcusTable t) {
		customers.add(new MyCustomer(cust, t, CustomerState.waiting));
		stateChanged();
	}

	public void msgImReadyToOrder(Customer customer) {
		output(customer + " is ready to order");
		
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if(c.customer == customer) {
					c.state = CustomerState.readyToOrder;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgHereIsMyChoice(Customer customer, String choice) {
		output("msgHereIsMyChoice " + customer + " asked for " + choice);
		
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if(c.customer == customer) {
					c.choice = choice;
					c.state = CustomerState.ordered;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgOutOfFood(int table, String choice) {
		output("The cook tells me we're out of " + choice);
		
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if (c.table.getTableNumber() == table) {
					waiterMenu.outOf(choice);
					c.state = CustomerState.reorder;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgOrderIsReady(int table, String choice) {
		output("msgOrderIsReady: Ready to deliver food to " + table);
		
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if (c.table.getTableNumber() == table) {
					c.state = CustomerState.readyToServe;
					stateChanged();
					return;
				}
			}
		}
	}

	public void msgReadyForCheck(Customer cust) {
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if (c.customer == cust) {
					c.state = CustomerState.wantsCheck;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgHereIsCheck(double amount, int table) {
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if (c.table.getTableNumber() == table) {
					c.state = CustomerState.checkReady;
					c.check = amount;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgLeavingTable(Customer cust) {
		synchronized(customers) {
			for(MyCustomer c : customers) {
				if (c.customer == cust) {
					c.state = CustomerState.leaving;
					stateChanged();
					return;
				}
			}
		}
	}

	public void msgAtDest() {//from animation
		isMovingSem.release();// = true;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(needGui) {
			waiterGui.guiAppear();
			needGui = false;
		}
		if(!requested && !onBreak && tired) {
			this.requestBreak();
			return true;
		}
		
		
		if(!customers.isEmpty()) {
			try{
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.leaving) {
						this.clearTable(customer);
						return true;
					}
				}
							
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.waiting) {
						this.seatCustomer(customer);
						return true;
					}
				}
					
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.readyToOrder) {
						this.askForOrder(customer);
						return true;
					}
				}
					
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.reorder) {
						this.askToReorder(customer);
						return true;
					}
				}
					
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.ordered) {
						this.handleOrder(customer);
						return true;
					}
				}
					
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.readyToServe) {
						this.serveFood(customer);
						return true;
					}
				}
					
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.wantsCheck) {
						this.retrieveCheck(customer);
						return true;
					}
				}
					
				for(MyCustomer customer : customers) {
					if(customer.state == CustomerState.checkReady) {
						this.deliverCheck(customer);
						return true;
					}
				}
			}
				
			//catch statement
			catch(ConcurrentModificationException e) {
				output("Error has occurred. Waiting.");
				return false;
			}
		}
		
		else {
			if(onBreak && requested && tired) {
				goOnBreak();
				requested = false;
			}
			
			if(!onDuty) {
				leaveRestaurant();
				onDuty = true;
				needGui = true;
			}
		}
		
		return false;
	}

	// Actions

	private void seatCustomer(MyCustomer c) {
		waiterGui.DoPickUpCustomer(c.customer.getXPos()+20, c.customer.getYPos()+20);	
		waitForGui();

		c.customer.msgFollowMeToTable(c.table.getTableNumber(), waiterMenu, this);
		output("Seating " + c.customer + " at " + c.table);
		
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
		
		c.state = CustomerState.seated;
		waiterGui.DoGoHome();
	}

	private void askForOrder(MyCustomer c) {
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
		
		c.state = CustomerState.waitingForOrder;
		output("Asking " + c.customer + " what they want");
		c.customer.msgWhatWouldYouLike();
	}
	
	private void askToReorder(MyCustomer c) {
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
		
		output("Asking for reorder");
		c.customer.msgPleaseReorder(waiterMenu);
		waiterMenu = new MarcusMenu();
		c.state = CustomerState.waitingForOrder;
	}
	
	protected abstract void handleOrder(MyCustomer c);

	private void serveFood(MyCustomer c) {
		output("Picking up order from Cook...");
		waiterGui.DoDeliverFood(c.choice);
		waitForGui();
		
		output("Taking order to " + c.customer);
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();

		c.state = CustomerState.served;
		c.customer.msgHereIsYourOrder(c.choice);
		waiterGui.DoGoHome();
	}
	
	private void retrieveCheck(MyCustomer c) {
		output("Getting check from cashier");
		c.state = CustomerState.waitingForWaiter;
		
		waiterGui.DoGoToCook(); //Location offscreen
		waitForGui();
		
		cashier.msgComputeBill(this, c.choice, c.table);
	}
	
	private void deliverCheck(MyCustomer c) {		
		waiterGui.DoGoToCook();
		waitForGui();

		output("Delivering check to customer");
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
				
		c.state = CustomerState.waitingForWaiter;
		c.customer.msgHereIsCheck();
		waiterGui.DoGoHome();
	}

	private void clearTable(MyCustomer c) {
		host.msgTableIsClear(c.table);
		customers.remove(c);
		
		waiterGui.DoGoHome();
	}
	
	private void requestBreak() {
		host.msgWantToGoOnBreak(this);
		requested = true;
	}
	
	private void goOnBreak() {
		output("I'm going on break now");
		timer.schedule(new TimerTask() {
			public void run() {
				finishBreak();
				return;
			}
		},
		25000); //On break for 25 seconds
	}
	
	private void finishBreak() {
		onBreak = tired = false;
		host.msgBackOnDuty(this);
	}
	
	private void leaveRestaurant() {
		waiterGui.DoLeaveRestaunt();
		waitForGui();
		setInactive();
		onDuty = true;
		needGui = true;
	}
	
	//utilities
	protected void waitForGui() {
		try {
			isMovingSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setGui(WaiterGuiInterface gui) {
		waiterGui = gui;
	}

	public WaiterGuiInterface getGui() {
		return waiterGui;
	}
	
	public String toString() {
		return "Waiter " + this.name;
	}
	
	protected void output(String input) {
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_RESTAURANT, name, input);
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_WAITER, name, input);
	}
	
	protected enum CustomerState {waiting, seated, readyToOrder, waitingForOrder, ordered, reorder,
		waitingForFood, readyToServe, served, waitingForWaiter, wantsCheck, checkReady, leaving};
	
	protected class MyCustomer {
		Customer customer;
		MarcusTable table;
		String choice;
		CustomerState state;
		double check;

		MyCustomer(Customer c, MarcusTable t, CustomerState s) {
			this.customer = c;
			this.table = t;
			this.state = s;
			this.check = 0;
		}

		public String toString() {
			return "Customer " + this.customer;
		}
	}
}

