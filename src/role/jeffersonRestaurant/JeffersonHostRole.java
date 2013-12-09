package role.jeffersonRestaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import role.Role;
import role.jeffersonRestaurant.JeffersonWaiterRole.*;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.ManagerRole;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Host;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;


/**
 * Restaurant Host Agent
 */



public class JeffersonHostRole extends Role implements ManagerRole,Host{
	
	
	static final int NTABLES = 3;
	
	private String name;
	
	boolean tablesFull;
	
	public Collection<Table> tables;
	
	boolean restaurantOpen;
	
	int temp;
	
	public List<Customer> waitingCustomers
	= Collections.synchronizedList (new ArrayList<Customer>());
	private PersonAgent p;
	private JeffersonCashierRole cashier;
	private JeffersonCookRole cook;
	private JeffersonWaiterRole waiter;
	public List<JeffersonWaiterRole> waiters = Collections.synchronizedList (new ArrayList<JeffersonWaiterRole>());
	public List<myWaiters> breakWaiters = Collections.synchronizedList (new ArrayList<myWaiters>());
	private boolean onDuty;
	
	
	 
	
	public JeffersonHostRole(PersonAgent p,String name) {
		super(p);

		this.name = name;
		onDuty=true;
		// make some tables
		tables =Collections.synchronizedList (new ArrayList<Table>(NTABLES));
		log("Host instantiated");
		tablesFull=false;
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));
		}
	}
	
	/*
	 * Hack to establish connection to one waiter
	 */
	/* 
	public void setWaiter(WaiterAgent w){
		
		this.waiter=w;
	}
	*/
	
	//messages

	public void msgEndShift(){
		log("jeffersonHost closing");
		onDuty=false;
		stateChanged();
	}
	
	public void msgFinishingShift(JeffersonWaiterRole jw) {
		
		synchronized(waiters){
			if(waiters.isEmpty()){
				return;
			}
			for(JeffersonWaiterRole j: waiters){
				if(j==jw){
					temp=waiters.indexOf(j);
				}
			}
			
		}
		waiters.remove(temp);
	}
	public void msgWaitersUpdate(){
	
		stateChanged();
	}
	
	public void msgIWantFood(Customer cust) {
		
		waitingCustomers.add(cust);
		//System.out.println("host added customer to list");
		stateChanged();
	}
	
	public void msgWaiterAdded(){
		if (waiters.size()==1){
			
			stateChanged();
		}
	}
	
	public void msgTableIsFree (int table){
		
		for(Table t:tables){
			if(t.tableNumber==table){
				t.setUnoccupied();
				log("table set unoccupied");
				stateChanged();
			}
			
		}
		
	}
	
	public void msgWantToGoOnBreak(Waiter w){
		breakWaiters.add(new myWaiters(w));
		log("break request recieved");
		stateChanged();
	}
		
	//scheduler
	//This is temporarily set to public, should be protected
	public boolean pickAndExecuteAnAction() {
		
		
		//System.out.println("entered host scheduler");
		tablesFull=true;
		
		//check if tables are empty
		synchronized(tables){
			for (Table table:tables){
				if(!table.isOccupied()){
					tablesFull=false;	
				}
			}
		}
		
		
		
		
		synchronized(breakWaiters){
			for(myWaiters w:breakWaiters){
				if(w.wantToBreak==true){
					if(waiters.size()<=1){
						
						log("Break denied");
						tellWaiterNo(w.mw);
						w.wantToBreak=false;
						return true;
					}
					else{		
						log("break approved");
						w.wantToBreak=false;
						tellWaiterYes(w.mw);
						waiters.remove(w.mw);
						return true;
					}	
				}	
			}
		}

		
		//will randomly decide if a customer leaves or stays
		synchronized(tables){
			for (Table table : tables) {
				if (tablesFull==true){
					if(!waitingCustomers.isEmpty()){
						Random gen = new Random();
						int leave=gen.nextInt(5);
						if(leave==1){
							
							waitingCustomers.get(0).msgRestaurantFullLeave();
							waitingCustomers.remove(0);
							break;	
						}
					}		
				}
			}
		}
		
		synchronized(tables){
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty()) {
						//chooses a random index number in waiter array
						Random generator = new Random();
						int index = generator.nextInt (waiters.size());
						waiter=waiters.get(index);
						
						//waitingCustomers.get(0).setWaiter(waiter);
						log("waiter assigned");
						tellWaiterToSeatAtTable(waitingCustomers.get(0), table.getTableNumber());//the action
						//System.out.println("messaged waiter to seat cust");
						table.setOccupant(waitingCustomers.get(0));
						waitingCustomers.remove(0);
						//System.out.println("host done adding");
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
		}
		
		if(!onDuty){
			closeBuilding();
			
		}
		
		
		return false;
	
	}
	
	
	//Actions
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_HOST, this.getName(), s);
	}
	public boolean closeBuilding() {
		if(!waitingCustomers.isEmpty()) return false;
		
		for(Table t : tables) {
			if(t.isOccupied()) {
				return false;
			}
		}
		
		double payroll = 0;
		for(JeffersonWaiterRole w : waiters) {
			JeffersonWaiterRole temp = w;
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
		
		if(cashier!=null){
			cashier.deductCash(payroll);
		}
		setInactive();
		onDuty = true;
		return true;
	}
	
		private void tellWaiterToSeatAtTable(Customer c, int t){
			waiter.msgSeatAtTable(c, t);
			
		}
	
	
		private void tellWaiterNo(Waiter w){
			log("Break Denied");
		}
		
		private void tellWaiterYes(Waiter w){
			log("telling waiter ok to break");
			w.msgYouCanBreak();
			
		}
		
		public class myWaiters{
			Waiter mw;
			boolean wantToBreak;
			
		 public myWaiters(Waiter w){
			 mw=w;
			 wantToBreak=true;
			 
		 }
			
			
			
		}
		
		public boolean isOpen() {
			//TODO removed check for empty waiter list, make sure waiters are assign customers on entry/return
			return (cook != null && cook.isActive()) && (cashier != null && cashier.isActive());
		}
		
		public void setCook(JeffersonCookRole ck){
			cook=ck;
		}
		public void setCashier (JeffersonCashierRole ca){
			cashier=ca;
		}
		
		public class Table {
			Customer occupiedBy;
			int tableNumber;
			int locx;
			int locy;
			int number;
		
			public int getTableNumber() {
				return tableNumber;
			}
			public int getLocx() {
				return locx;
			}
			public int getLocy() {
				return locy;
			}
			
			public void setLocx(int locx) {
				this.locx = locx;
			}
			public void setLocy(int locy) {
				this.locy = locy;
			}

			Table(int tableNumber) {
				this.tableNumber = tableNumber;
			}

			void setOccupant(Customer cust) {
				occupiedBy = cust;
			}

			void setUnoccupied() {
				occupiedBy = null;
			}

			Customer getOccupant() {
				return occupiedBy;
			}

			boolean isOccupied() {
				return occupiedBy != null;
			}

			public String toString() {
				return "table " + tableNumber;
			}
		}
	/*
		public class MyCustomer{
			CustomerAgent c;
			int table;
			
			public MyCustomer(CustomerAgent c) {
				this.c=c;
			}
		}
	
	*/

		







}