package role.ellenRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.ManagerRole;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import role.Role;

import java.util.*;

/**
 * Restaurant Host Agent
 */

public class EllenHostRole extends Role implements ManagerRole {
	private String name;
	static final int NTABLES = 4;//a global for the number of tables.
	EllenCookRole cook;
	EllenCashierRole cashier;
	
	public Collection<MyWaitingCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyWaitingCustomer>());
	public Collection<Table> tables;	
	private Collection<MyWaiter> myWaiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	
	private boolean onDuty;

	
	public EllenHostRole(PersonAgent p, String name) {
		super(p);

		this.name = name;	
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		onDuty = true;
	}
	
	public String getName() {
		return name;
	}
	public void setCashier(EllenCashierRole c){
		cashier = c;
	}
	public void setCook(EllenCookRole c){
		cook = c;
	}
	public Collection<MyWaitingCustomer> getWaitingCustomers(){
		return waitingCustomers;
	}
	public Collection<Table> getTables() {
		return tables;
	}
	public void msgEndShift(){
		onDuty = false;
		stateChanged();
	}
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_HOST, this.getName(), s);
	}

	
	// Messages

	public void msgIWantFood(EllenCustomerRole cust, int waitingX, int waitingY) {	//from customer
		log("Received msg IWantFood");
		waitingCustomers.add(new MyWaitingCustomer(cust, waitingX, waitingY));
		stateChanged();
	}
	
	public void msgIWillStay(EllenCustomerRole cust){
		MyWaitingCustomer mwc = null;
		synchronized(waitingCustomers){
			for (MyWaitingCustomer thisWC : waitingCustomers){ //to find the MyWaiter with this specific CustomerAgent within myWaiters
				if (thisWC.c.equals(cust)){
					mwc = thisWC;
					break;
				}
			}
		}

		mwc.confirmedToWait = true;
		log("Received msg IWillStay from " + cust.getName());
		stateChanged();
	}
	
	public void msgIAmLeaving(EllenCustomerRole cust){
		MyWaitingCustomer mwc = null;
		synchronized(waitingCustomers){
			for (MyWaitingCustomer thisWC : waitingCustomers){ //to find the MyWaiter with this specific CustomerAgent within myWaiters
				if (thisWC.c.equals(cust)){
					mwc = thisWC;
					break;
				}
			}
		}
		mwc.confirmedToWait = true;
		log("Received msg IAmLeaving from " + cust.getName());
		waitingCustomers.remove(mwc);
	}
	
	public void msgIWantBreak(Waiter w){	//from waiter
		MyWaiter mw = null;
		synchronized(myWaiters){
			for (MyWaiter thisMW : myWaiters){ //to find the MyWaiter with this specific Waiter within myWaiters
				if (thisMW.w.equals(w)){
					mw = thisMW;
					break;
				}
			}
		}
		mw.wantsBreak = true;
		log("Received msg IWantToGoOnBreak");
		stateChanged();
	}
	
	public void msgComingOffBreak(Waiter w){	//from waiter
		MyWaiter mw = null;
		synchronized(myWaiters){
			for (MyWaiter thisMW : myWaiters){ //to find the MyWaiter with this specific Waiter within myWaiters
				if (thisMW.w.equals(w)){
					mw = thisMW;
					break;
				}
			}
		}
		
		mw.onBreak = false;
		
		log("Received msgIWantToComeBack");
		stateChanged();
	}
	
	public void msgTableFree(int tablenum){		//from waiter
		log("Received msgTableFree");
		synchronized(tables){
			for (Table table: tables){
				if (table.tableNumber == tablenum){
					table.isOccupied = false;
					stateChanged();
				}
			}
		}
	}
	
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(myWaiters){
			for (MyWaiter mw: myWaiters){		//lowest priority
				 if (mw.wantsBreak){
					 respondToBreakRequest(mw);
				 }
			 }
		}
		
		
		/*
		This next statement + loop checks that:
		If there is more than one waiter,
		check the size of each waiter's MyCustomer list, and find the waiter with the 
		lowest # of customers. (load balance mechanism)
		 */
		
		Waiter currentWaiter = null;
		int min = 100;
		if (myWaiters.size() > 0){
			synchronized(myWaiters){
				for (MyWaiter mw : myWaiters){
					if (!mw.onBreak){
						int n = mw.w.getMyCustomers().size();
						if (n < min){
							min = n;
							currentWaiter = mw.w;
						}
					}
				}//end of for loop
			}//end of synchronized
			
		}//end of if
		
		
		/*
		 * For notifying a waiting customer that the restaurant is full
		 */
		int n = 0;
		synchronized(tables){
			for (Table table : tables){
				if (table.isOccupied)
					n++;
				else
					break;
				
				if (n == NTABLES){	//all tables are occupied
					if (!waitingCustomers.isEmpty()){
						for (MyWaitingCustomer wc : waitingCustomers){
							if (!wc.confirmedToWait){
								notifyCustomerRestFull(wc.c);
								return false;
							}
						}
					}
				}//end of if
			} //end of for loop
		}//end of synchronized

		
		/*
		 * For notifying a waiting customer that the restaurant is full
		 */

		

		/* Think of this rule as:
        Does there exist a table and customer and waiter,
        so that table is unoccupied, customer is waiting, and waiter has
        the least # of customers.
        If so assign the customer to that table and waiter.
	 */
		synchronized(tables){
			 for (Table table : tables) {
					if (!table.isOccupied) {
						if (!waitingCustomers.isEmpty()) {
							if (!myWaiters.isEmpty()){
								if (isOpen()){
									assignCustomerToWaiter(waitingCustomers.iterator().next(), table, currentWaiter); //the action
									return true;//return true to the abstract agent to reinvoke the scheduler.
								}
							}
						}
					}
				} //end of for loop
		}//end of synch
		
		
		if (!onDuty){
			closeBuilding();
		}
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void assignCustomerToWaiter(MyWaitingCustomer cust, Table table, Waiter w) {
		log("Assigning " + cust.c.getName() + " to " + w.getName() + " at table " + table.tableNumber);
		
		w.msgPleaseSeatCustomer(cust.c, cust.waitingPosX, cust.waitingPosY, table.tableNumber);
		waitingCustomers.remove(cust);
		table.isOccupied = true;
	}

	private void respondToBreakRequest(MyWaiter mw){
		if (myWaiters.size() > 1){
			for (MyWaiter m : myWaiters){
				if (!m.equals(mw) && !m.onBreak){
					mw.w.msgBreakRequestResponse(true);
					mw.wantsBreak = false;
					mw.onBreak = true;
					return;
				}
			}
			mw.w.msgBreakRequestResponse(false);
			mw.wantsBreak = false;
			mw.onBreak = false;
		}
		else {
			mw.w.msgBreakRequestResponse(false);
			mw.wantsBreak = false;
			mw.onBreak = false;
		}
	}
	
	private void notifyCustomerRestFull(EllenCustomerRole cust){
		cust.msgRestaurantFull();
	}
	
	public boolean closeBuilding(){
		if(!waitingCustomers.isEmpty()) return false;
		
		for(Table t : tables) {
			if(t.isOccupied) {
				return false;
			}
		}
		
		double payroll = 0;
		for(MyWaiter w : myWaiters) {
			EllenWaiterRole temp = ((EllenWaiterRole) w.w);
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
	

	public boolean isOpen() {
		stateChanged();
		return (cook != null && cook.isActive()) && (cashier != null && cashier.isActive());
	}
	
	public void addWaiter(Waiter w){
		myWaiters.add(new MyWaiter(w));
		stateChanged();
	}
	
	private class MyWaiter {
		Waiter w;
		boolean wantsBreak;
		boolean onBreak;
		
		MyWaiter(Waiter waiter){
			this.w = waiter;
			boolean onBreak = false;
			boolean wantsBreak = false;
		}
	}
	
	private class MyWaitingCustomer {
		EllenCustomerRole c;
		boolean confirmedToWait;
		
		int waitingPosX;
		int waitingPosY;
		
		MyWaitingCustomer (EllenCustomerRole cust, int waitingX, int waitingY) {
			this.c = cust;
			this.waitingPosX = waitingX;
			this.waitingPosY = waitingY;
			confirmedToWait = false;
		}
	}
	
	private class Table {
		int tableNumber;
		boolean isOccupied;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
	}
}

