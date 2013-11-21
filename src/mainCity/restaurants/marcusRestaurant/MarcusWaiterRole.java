package mainCity.restaurants.marcusRestaurant;

import agent.Agent;
import mainCity.restaurants.marcusRestaurant.interfaces.*;
import mainCity.restaurants.marcusRestaurant.gui.WaiterGui;
import mainCity.restaurants.marcusRestaurant.MarcusMenu;

import java.util.*;
import java.util.concurrent.Semaphore;

public abstract class MarcusWaiterRole extends Agent implements Waiter {
	public WaiterGui waiterGui = null;

	static final int NTABLES = 3;//a global for the number of tables.

	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	protected Semaphore isMovingSem = new Semaphore(0, true);
	protected boolean onBreak, requested, tired;
	protected String name;
	protected MarcusHostRole host;
	protected MarcusCookRole cook;
	protected Cashier cashier;
	protected MarcusMenu waiterMenu;
	
	Timer timer = new Timer();
	
	public MarcusWaiterRole(String name) {
		super();

		this.name = name;
		onBreak = false;
		tired = false;
		requested = false;//temporary
		waiterMenu = new MarcusMenu();
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return customers;
	}

	public int getCustomerCount() {
		return customers.size();
	}
	
	public void setHost(MarcusHostRole h) {
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
	
	public void msgBreakReply(boolean r) {
		print("Can " + this + " go on break? " + r);
		onBreak = tired = requested = r;
		stateChanged();
	}
	
	public void msgSeatAtTable(Customer cust, MarcusTable t) {
		customers.add(new MyCustomer(cust, t, CustomerState.waiting));
		stateChanged();
	}

	public void msgImReadyToOrder(Customer customer) {
		print(customer + " is ready to order");
		
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
		print("msgHereIsMyChoice " + customer + " asked for " + choice);
		
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
		print("The cook tells me we're out of " + choice);
		
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
		print("msgOrderIsReady: Ready to deliver food to " + table);
		
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
	protected boolean pickAndExecuteAnAction() {
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
					print("Error has occurred. Waiting.");
					return false;
				}
			}
		
		else {
			if(onBreak && requested && tired) {
				goOnBreak();
				requested = false;
			}
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(MyCustomer c) {
		waiterGui.DoPickUpCustomer(c.customer.getXPos()+20, c.customer.getYPos()+20);	
		waitForGui();

		c.customer.msgFollowMeToTable(c.table.getTableNumber(), waiterMenu, this);
		print("Seating " + c.customer + " at " + c.table);
		
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
		
		c.state = CustomerState.seated;
		waiterGui.DoGoHome();
	}

	private void askForOrder(MyCustomer c) {
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
		
		c.state = CustomerState.waitingForOrder;
		print("Asking " + c.customer + " what they want");
		c.customer.msgWhatWouldYouLike();
	}
	
	private void askToReorder(MyCustomer c) {
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();
		
		print("Asking for reorder");
		c.customer.msgPleaseReorder(waiterMenu);
		waiterMenu = new MarcusMenu();
		c.state = CustomerState.waitingForOrder;
	}
	
	protected abstract void handleOrder(MyCustomer c);

	private void serveFood(MyCustomer c) {
		print("Picking up order from Cook...");
		waiterGui.DoDeliverFood(c.choice);
		waitForGui();
		
		print("Taking order to " + c.customer);
		waiterGui.DoGoToTable(c.table.getTableNumber()); 
		waitForGui();

		c.state = CustomerState.served;
		c.customer.msgHereIsYourOrder(c.choice);
		waiterGui.DoGoHome();
	}
	
	private void retrieveCheck(MyCustomer c) {
		print("Getting check from cashier");
		c.state = CustomerState.waitingForWaiter;
		
		waiterGui.DoGoToCook(); //Location offscreen
		waitForGui();
		
		cashier.msgComputeBill(this, c.choice, c.table);
	}
	
	private void deliverCheck(MyCustomer c) {		
		waiterGui.DoGoToCook();
		waitForGui();

		print("Delivering check to customer");
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
		print("I'm going on break now");
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
	
	//utilities
	protected void waitForGui() {
		try {
			isMovingSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	public String toString() {
		return "Waiter " + this.name;
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

