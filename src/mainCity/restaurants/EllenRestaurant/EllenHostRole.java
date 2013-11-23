package mainCity.restaurants.EllenRestaurant;

import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import agent.Agent;
import role.Role;
import mainCity.PersonAgent;

import java.util.*;

/**
 * Restaurant Host Agent
 */

public class EllenHostRole extends Role {
	private String name;
	
	public Collection<MyWaitingCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyWaitingCustomer>());
	public Collection<Table> tables;	
	private Collection<MyWaiter> myWaiters = Collections.synchronizedList(new ArrayList<MyWaiter>());

	
	public EllenHostRole(PersonAgent p, String name) {
		super(p);

		this.name = name;		
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}


	public Collection getWaitingCustomers(){
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}

	
	// Messages

	public void msgIWantFood(EllenCustomerRole cust, int waitingX, int waitingY) {	//from customer
		//print("Received msg IWantFood");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg IWantFood");
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
		//print("Received msg IWillStay from " + cust.getName());
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg IWillStay from " + cust.getName());
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
		//print("Received msg IAmLeaving from " + cust.getName());
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg IAmLeaving from " + cust.getName());
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
		
		//print("Received msg IWantToGoOnBreak");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg IWantToGoOnBreak");
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
		
		//print("Received msgIWantToComeBack");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msgIWantToComeBack");
		stateChanged();
	}
	
	public void msgTableFree(int tablenum){		//from waiter
		//print("Received msgTableFree");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msgTableFree");
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
									assignCustomerToWaiter(waitingCustomers.iterator().next(), table, currentWaiter); //the action
									return true;//return true to the abstract agent to reinvoke the scheduler.
							}
						}
					}
				} //end of for loop
		}//end of synch
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void assignCustomerToWaiter(MyWaitingCustomer cust, Table table, Waiter w) {
		//print("Assigning " + cust.c.getName() + " to " + w.getName() + " at table " + table.tableNumber);
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Assigning " + cust.c.getName() + " to " + w.getName() + " at table " + table.tableNumber);
		
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
	
	//utilities
/*
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
*/
	
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

