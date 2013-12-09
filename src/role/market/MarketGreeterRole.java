package role.market;

import mainCity.PersonAgent;
import mainCity.market.interfaces.*;
import mainCity.gui.trace.*;
import mainCity.interfaces.*;
import role.Role;

import java.util.*;


public class MarketGreeterRole extends Role implements Greeter, ManagerRole {
	private String name;
	MarketCashier cashier;
	DeliveryMan deliveryMan;
	static final int NTABLES = 8;
	
	public List<MyWaitingCustomer> waitingCustomers  = Collections.synchronizedList(new ArrayList<MyWaitingCustomer>());
	public List<MyWaitingBusiness> waitingBusinesses = Collections.synchronizedList(new ArrayList<MyWaitingBusiness>());
	public List<Employee> myEmployees = Collections.synchronizedList(new ArrayList<Employee>());

	int nextEmployee = 0;
	boolean cashierArrived = false;
	private boolean onDuty;

	
	public MarketGreeterRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;
		
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
		if (cashier instanceof MarketCashierRole){
			MarketCashierRole c = (MarketCashierRole) cashier;
			return ((deliveryMan != null && deliveryMan.isActive()) && (c != null && c.isActive()));
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
		if (name.toLowerCase().contains("market2")){
			AlertLog.getInstance().logMessage(AlertTag.MARKET2, this.getName(), s);
	        AlertLog.getInstance().logMessage(AlertTag.MARKET2_GREETER, this.getName(), s);
		}
		else {
			AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
	        AlertLog.getInstance().logMessage(AlertTag.MARKET_GREETER, this.getName(), s);
		}
	}
	
	// Messages
	
	//from customers
	public void msgINeedInventory(Customer c, int x, int y){
		log("Received msgINeedInventory from: " + c.getName());
        waitingCustomers.add(new MyWaitingCustomer(c, x, y));
		stateChanged();
	}
	
	//from businesses
	public void msgINeedInventory(String restaurantName, Map<String, Integer> inventoryNeeded){
		log("Received msgINeedInventory from " + restaurantName);
		waitingBusinesses.add(new MyWaitingBusiness(restaurantName, inventoryNeeded));
		stateChanged();
	}

	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

		synchronized(waitingCustomers){
			synchronized(myEmployees){
				if (!waitingCustomers.isEmpty()){
					if (!myEmployees.isEmpty()){
						nextEmployee++;
						if (nextEmployee > myEmployees.size() - 1)
							nextEmployee = 0;
						
						assignCustomerToEmployee(waitingCustomers.get(0), myEmployees.get(nextEmployee));
						return true;
					}
				}
			}
		}
		
		synchronized(waitingBusinesses){
			synchronized(myEmployees){
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
		e.msgAssignedToBusiness(business.restaurantName, /*business.cook, business.cashier,*/ business.inventory);
		waitingBusinesses.remove(business);
	}
	public boolean closeBuilding(){
		if (!waitingCustomers.isEmpty())
			return false;
		
		/*for (Table t : tables){
			if (t.isOccupied)
				return false;
		}*/
		
		double payroll = 0;
		for(Employee e : myEmployees) {
			MarketEmployeeRole temp = ((MarketEmployeeRole) e);
			double amount = temp.getShiftDuration()*4.75;
			temp.msgGoOffDuty(amount);
			payroll += amount;
		}
		
		if(cashier != null) {
			MarketCashierRole c = (MarketCashierRole) cashier;
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
		Map<String, Integer> inventory;
		
		MyWaitingBusiness(String restaurantName, Map<String, Integer> inventoryNeeded){
			this.restaurantName = restaurantName;
			inventory = inventoryNeeded;
		}
		public String getRestaurant(){
			return restaurantName;
		}
	}
	
	public class MyWaitingCustomer {
		Customer c;
		
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
}

