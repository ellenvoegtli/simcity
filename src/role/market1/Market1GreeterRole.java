package role.market1;

import mainCity.PersonAgent;
import mainCity.market1.interfaces.*;
import mainCity.gui.trace.*;
import mainCity.interfaces.*;
import role.Role;

import java.util.*;


public class Market1GreeterRole extends Role implements Greeter, ManagerRole {
	private String name;
	MarketCashier cashier;
	DeliveryMan deliveryMan;
	static final int NTABLES = 8;
	
	public List<MyWaitingCustomer> waitingCustomers  = Collections.synchronizedList(new ArrayList<MyWaitingCustomer>());
	public List<MyWaitingBusiness> waitingBusinesses = Collections.synchronizedList(new ArrayList<MyWaitingBusiness>());
	public List<Employee> myEmployees = Collections.synchronizedList(new ArrayList<Employee>());
	public Collection<Table> tables;

	int nextEmployee = 0;
	boolean cashierArrived = false;
	private boolean onDuty;

	
	public Market1GreeterRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;
		
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));
		}
	}
	
	public void addEmployee(Employee e){
		myEmployees.add(e);
		stateChanged();
	}
	public void setCashier(MarketCashier c){
		cashier = c;
		cashierArrived = true;
	}
	public void setDeliveryMan(DeliveryMan d){
		deliveryMan = d;
	}
	public String getMaitreDName() {
		return name;
	}
	public String getName() {
		return name;
	}
	public List<MyWaitingCustomer> getWaitingCustomers(){
		return waitingCustomers;
	}
	public List<MyWaitingBusiness> getWaitingBusinesses(){
		return waitingBusinesses;
	}
	public List<Employee> getEmployees(){
		return myEmployees;
	}
	public void setCashierArrived(boolean x){		//for testing
		cashierArrived = x;
	}
	public boolean isOpen() {
		if (cashier instanceof Market1CashierRole){
			Market1CashierRole c = (Market1CashierRole) cashier;
			return (/*deliveryMan != null && deliveryMan.isActive()) &&*/ (c != null && c.isActive()));
		}
		return false;
	}
	public void msgEndShift(){
		print("msgEndShift called");
		onDuty = false;
		stateChanged();
	}

	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.MARKET_GREETER, this.getName(), s);
	}
	
	// Messages
	
	//from customers
	public void msgINeedInventory(Customer c, int x, int y){
		log("Received msgINeedInventory from: " + c.getName());
        waitingCustomers.add(new MyWaitingCustomer(c, x, y));
		stateChanged();
	}
	
	//from businesses
	public void msgINeedInventory(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer> inventoryNeeded){
		log("Received msgINeedInventory from " + restaurantName);
		waitingBusinesses.add(new MyWaitingBusiness(restaurantName, cook, cashier, inventoryNeeded));
		stateChanged();
	}

	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

		if (!waitingCustomers.isEmpty()){
			if (!myEmployees.isEmpty()){
				nextEmployee++;
				if (nextEmployee > myEmployees.size() - 1)
					nextEmployee = 0;
				
				assignCustomerToEmployee(waitingCustomers.get(0), myEmployees.get(nextEmployee));
				return true;
			}
		}

		if (!waitingBusinesses.isEmpty()){
			if (!myEmployees.isEmpty()){
				if (isOpen() || cashierArrived){
					//cashierArrived = false;
					nextEmployee++;
					if (nextEmployee > myEmployees.size() - 1)
						nextEmployee = 0;
					
					assignBusinessToEmployee(waitingBusinesses.get(0), myEmployees.get(nextEmployee));
					return true;
				}
			}
		}
		
		if (!onDuty){
			closeBuilding();
		}

		return false;
	}

	// Actions

	private void assignCustomerToEmployee(MyWaitingCustomer cust, Employee e) {
		log("Assigning " + cust.c.getName() + " to " + e.getName());
		e.msgAssignedToCustomer(cust.c, cust.waitingPosX, cust.waitingPosY);
		waitingCustomers.remove(cust);
	}
	private void assignBusinessToEmployee(MyWaitingBusiness business, Employee e){
        log("Assigning " + business.restaurantName + " to " + e.getName());
		e.msgAssignedToBusiness(business.restaurantName, business.cook, business.cashier, business.inventory);
		waitingBusinesses.remove(business);
	}
	public boolean closeBuilding(){
		if (!waitingCustomers.isEmpty())
			return false;
		
		for (Table t : tables){
			if (t.isOccupied)
				return false;
		}
		
		double payroll = 0;
		for(Employee e : myEmployees) {
			Market1EmployeeRole temp = ((Market1EmployeeRole) e);
			double amount = temp.getShiftDuration()*4.75;
			temp.msgGoOffDuty(amount);
			payroll += amount;
		}
		
		if(cashier != null) {
			Market1CashierRole c = (Market1CashierRole) cashier;
			payroll += c.getShiftDuration()*6.0;
			c.msgGoOffDuty(c.getShiftDuration()*6.0);
		}
		
		addToCash(getShiftDuration()*9.50);
		payroll += getShiftDuration()*9.50;		
		
		cashier.deductCash(payroll);
		setInactive();
		onDuty = true;
		return true;
	}

	
	

	
	
	public class MyWaitingBusiness {
		String restaurantName;
		MainCook cook;
		MainCashier cashier;
		Map<String, Integer> inventory;
		
		MyWaitingBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer> inventoryNeeded){
			this.restaurantName = restaurantName;
			this.cook = cook;
			this.cashier = cashier;
			inventory = inventoryNeeded;
		}
		public String getRestaurant(){
			return restaurantName;
		}
	}
	
	public class MyWaitingCustomer {
		Customer c;
		//boolean confirmedToWait;
		
		int waitingPosX;
		int waitingPosY;

		MyWaitingCustomer (Customer cust, int waitingX, int waitingY) {
			this.c = cust;
			this.waitingPosX = waitingX;
			this.waitingPosY = waitingY;
		}
		
		MyWaitingCustomer(Customer cust){
			c = cust;
		}
		public Customer getCustomer(){
			return c;
		}
	}
	
	public class Table {
		int tableNumber;
		boolean isOccupied;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
	}
}

