package role.davidRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import role.Role;
import role.davidRestaurant.DavidCustomerRole;
import role.davidRestaurant.DavidWaiterRole;
import role.davidRestaurant.DavidCookRole.Food;
import role.davidRestaurant.DavidCookRole.Order;
import role.davidRestaurant.DavidCookRole.OrderStatus;
import role.davidRestaurant.DavidCustomerRole.AgentEvent;
import role.davidRestaurant.DavidWaiterRole.myCustomer;
import role.market.MarketDeliveryManRole;
import agent.Agent;
import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.EllenCashierRole.MarketBill;
import mainCity.restaurants.EllenRestaurant.EllenCashierRole.MarketBillState;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Cashier;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Customer;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Market;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Waiter;
import mainCity.restaurants.restaurant_zhangdt.test.mock.EventLog;
import mainCity.restaurants.restaurant_zhangdt.test.mock.LoggedEvent;

public class DavidCashierRole extends Role implements Cashier {
/*   Data   */ 
	
	//Name
	String name;
	
	//Agent Connections
	DavidCookRole cookAgent; 
	DavidHostRole host;
	
	boolean CashierFree = true;
	Timer checkTimer;
	public double Money = 1000; 
	public double change;
	double MarketTab = 0;
	public boolean PaidMarket = true;
	public int MarketNumber;
	List<Market> marketAgent = new ArrayList<Market>();
	
	public EventLog log = new EventLog();
	
	//Constructor
	public DavidCashierRole(String name, PersonAgent p) { 
		super(p); 
		this.name = name; 
	}
	
	public enum CheckState
	{none, recievedCheck, handledCheck, recievedPayment, handledPayment, recievedMarketBill, handledMarketBill}; 
	
	public enum CashierState
	{none, recievedMarketBill, handledMarketBill};
	public CashierState cashierState = CashierState.none; 
	
	public class Check { 
		Waiter waiterAgent;
		String Order; 
		public int tableNum;
		public double Price;
		public CheckState checkState = CheckState.none;
		
		public Check(Waiter w, String o, double p, int t, CheckState c) { 
			waiterAgent = w; 
			Order = o; 
			Price = p;
			tableNum = t;
			checkState = c;
		}
	}
	
	public List<Customer> customers = new ArrayList<Customer>();
	public List<Check> checkList = Collections.synchronizedList(new ArrayList<Check>());
	public List<MarketBill> marketBills = new ArrayList<MarketBill>(); 
	public double Payment;
	public enum MarketBillState {newBill, computing, waitingForChange, receivedChange, done};
	
	
/*   Messages   */ 
	
	public void msgHeresACheck(Waiter w, String order, int tableNumber) {
		print("msgHeresACheck called");
		log.add(new LoggedEvent("Recieved check from waiter."));
		CashierFree = false;
		double price = 0; 
		switch(order) {
			case "Steak": price = 15.99; break;
			case "Chicken": price = 10.99; break;
			case "Pizza": price = 8.99; break;
			case "Salad": price = 5.99; break;
		}
		checkList.add(new Check(w, order, price, tableNumber, CheckState.recievedCheck));
		stateChanged();
	}
	
	public void msgHeresMyPayment(Customer c, double custPayment, int tableNumber){
		print("Recieved Payment from Customer of " + custPayment);
		for(int i=0; i<checkList.size(); i++){ 
			if(checkList.get(i).tableNum == tableNumber){ 
				checkList.get(i).checkState = CheckState.recievedPayment; 
			}
		}
		customers.add(c);
		Payment = custPayment;
		Money = Money + custPayment;
		stateChanged();
	}
	
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, MarketDeliveryManRole d){
		print("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount);
		marketBills.add(new MarketBill(d, billAmount, inventory, MarketBillState.computing));
		stateChanged();
	}
	
	@Override
	public void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson) {
		// TODO Auto-generated method stub
		print("Received msgHereIsChange");
		MarketBill b = null;
		synchronized(marketBills){
			for (MarketBill thisMB : marketBills){
				if (thisMB.deliveryMan == deliveryPerson){
					b = thisMB;
					break;
				}
			}
		}
		
		b.amountChange = amount;
		b.s = MarketBillState.receivedChange;
		stateChanged();
	}
	
	
/*   Scheduler   */ 
	
	public boolean pickAndExecuteAnAction() { 
		synchronized(checkList){
			if(checkList.size() != 0) {
				for(Check check : checkList) {
					if(check.checkState == CheckState.recievedCheck){ 
						handleCheck(check); 
						return true;
					}
					
					if(check.checkState == CheckState.recievedPayment){ 
						handlePayment(check); 
						return true;
					}
				}	
			}
		}
		if(cashierState == CashierState.recievedMarketBill){ 
			handleMarketBill(); 
			return true;
		}
		return false; 
	}
	
/*   Actions   */
	
	private void handleCheck(Check c){
		print("Done calculating bill");
		c.checkState = CheckState.handledCheck;
		c.waiterAgent.msgBringCheckToCustomer(c.Price, c.tableNum);
		stateChanged();
			
	}
	
	private void handlePayment(Check c){ 
		print("Handling payment..");
		c.checkState = CheckState.handledCheck;
		for(int i=0; i<customers.size(); i++){
			if(customers.get(i).getTableNum() == c.tableNum){
				if(Payment == 0){
					print("You don't have enough money, adding to your tab"); 
					customers.get(i).msgHeresYourChange(change);
					stateChanged();
				}
				else { 
					print("Calculating Change");
					change = Payment -= c.Price; 
					Money = Money - change;
					print("Here's your change: " + change); 
					customers.get(i).msgHeresYourChange(change);
					stateChanged();
				}
			}
		}
	}
	
	private void handleMarketBill() { 
		print("Handling Market Bill for market " + MarketNumber);
		cashierState = CashierState.handledMarketBill;
		marketAgent.get(MarketNumber).msgPaymentFromCashier(PaidMarket);
		stateChanged();
	}
	
// utilities 
	
	public void setCook(DavidCookRole c) {
		cookAgent = c;
	}
	
	public void addMarket(Market m) {
		marketAgent.add(m);
	}
	
	public void addCustomer(Customer c) {
		customers.add(c);
	}
	
	public class MarketBill {
		Market m;
		//String deliveryPerson;
		MarketDeliveryManRole deliveryMan;
		int checkAmount;	//irrelevant for new implementation; kept to keep tests compiling
		double billAmount;
		double amountPaid;
		double amountChange;
		MarketBillState s;
		
		Map<String, Integer> itemsBought; 
		
		//old constructor
		MarketBill(Market market, int amount, MarketBillState st){
			m = market;
			checkAmount = amount;
			s = st;
		}

		MarketBill(MarketDeliveryManRole d, double amount, Map<String, Integer> inventory, MarketBillState s){
			deliveryMan = d;
			billAmount = amount;
			itemsBought = new TreeMap<String, Integer>(inventory);
			this.s = s;
		}
		

		public Market getMarket(){
			return m;
		}
		public MarketBillState getState(){
			return s;
		}
		public int getCheckAmount(){
			return checkAmount;
		}
	}

	public void setHost(DavidHostRole host) {
		this.host = host;
	}
}

