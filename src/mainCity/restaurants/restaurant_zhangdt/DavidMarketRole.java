package mainCity.restaurants.restaurant_zhangdt;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import agent.Agent;
import mainCity.restaurants.restaurant_zhangdt.DavidCookRole.Order;
import mainCity.restaurants.restaurant_zhangdt.DavidCookRole.OrderStatus;
import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole.AgentEvent;
import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Market;

public class DavidMarketRole extends Agent implements Market{
/*   Data   */ 
	private DavidCookRole cookAgent;
	private DavidCashierRole cashierAgent;
	
	class Food {
		String Choice; 
		int quantity;
		int cookingTime; 
		double price;
		
		public Food(String c, int q, int ct, double p){
			Choice = c; 
			quantity = q; 
			cookingTime = ct; 
			price = p;
		}
	}
	
	Timer DeliveryTimer = new Timer();
	enum MarketStatus {none, recievedOrder, handlingOrder, handledOrder, recievedMassOrder, denied}; 
	MarketStatus currentStatus = MarketStatus.none; 
	List<Food> MarketInventory = new ArrayList<Food>(); 
	double MarketBill;
	boolean PaidOff = true;
	int MarketNumber;
	String CurrentOrder;
	
	private RestaurantGui gui; 
	
	public DavidMarketRole(int mn, int st, int c, int sa, int p) {
		super(); 
		MarketNumber = mn;
		MarketInventory.add(new Food("Steak", st, 5000, 15.99));
		MarketInventory.add(new Food("Chicken", c, 5000, 10.99));
		MarketInventory.add(new Food("Salad", sa, 5000, 5.99));
		MarketInventory.add(new Food("Pizza", p, 5000, 8.99));
	}
	
/*   Messages   */ 
	public void msgOrderFromCook(String MarketOrder) {
		print("msgOrderFromCook recieved");
		currentStatus = MarketStatus.recievedOrder; 
		CurrentOrder = MarketOrder; 
		stateChanged(); 
	}
	
	public void msgMassOrderRecieved(){
		print("msgMassOrderRecieved"); 
		currentStatus = MarketStatus.recievedMassOrder;
		stateChanged();
	}
	
	public void msgPaymentFromCashier(boolean Paid){
		print ("msgPaymentFromCashier recieved. Paid = " + Paid);
		PaidOff = Paid;
		stateChanged();
	}
	
/*   Scheduler   */ 
	protected boolean pickAndExecuteAnAction() { 
		try{
			if(currentStatus == MarketStatus.recievedOrder && PaidOff == true){ 
				HandleOrder(); 
				return true;
			}
			
			if(currentStatus == MarketStatus.recievedOrder && PaidOff == false){
				ServiceDenied();
				return true;
			}
			
			if(currentStatus == MarketStatus.recievedMassOrder && PaidOff == true){
				HandleMassOrder(); 
				return true;
			}
			
			if(currentStatus == MarketStatus.recievedMassOrder && PaidOff == false){
				ServiceDenied(); 
				return true;
			}
			
			return false; 
		}
		catch(ConcurrentModificationException r){ 
			return false;
		}
	}
	
/*   Actions   */
	private void HandleOrder() { 

		currentStatus = MarketStatus.handledOrder;
		for(int i=0; i<MarketInventory.size(); i++){
			if(MarketInventory.get(i).Choice == CurrentOrder){
				if(MarketInventory.get(i).quantity == 0) { 
					print("Out of " + MarketInventory.get(i).Choice); 
					cookAgent.msgMarketOutOfOrder();
				}
				else if(MarketInventory.get(i).quantity < 5) {
					print("Not enough " + MarketInventory.get(i).Choice + " in stock.");
					cookAgent.msgMarketOutOfOrder();
				}
				else if(MarketInventory.get(i).quantity >= 5) {
					MarketInventory.get(i).quantity = MarketInventory.get(i).quantity - 5;  
					MarketBill = (MarketInventory.get(i).price)*5;
					print("Bill: " + MarketBill);
					DeliveryTimer.schedule(new TimerTask() {
						public void run() { 
							print("Now Delivering...");
							//cookAgent.msgOrderFromMarket();
							//cashierAgent.msgPayMarket(MarketNumber, MarketBill);
							stateChanged();
						}
					},
					5000);
				}
			}
		}
	}
		
		
	
	private void HandleMassOrder() {
		currentStatus = MarketStatus.handledOrder; 
		for(int i=0; i<MarketInventory.size(); i++){
			if(MarketInventory.get(i).quantity < 5) { 
				print("Out of " + MarketInventory.get(0).Choice); 
				cookAgent.msgMarketOutOfOrder();
				stateChanged();
			}
		}
		
		DeliveryTimer.schedule(new TimerTask() {
			public void run() { 
				print("Now Delivering...");
				cookAgent.msgMassOrderReady();
				stateChanged();
			}
		},
		1000);
			
	}	
	
	private void ServiceDenied() { 
		print("Service Denied due to debt."); 
		currentStatus = MarketStatus.denied;
		cookAgent.msgMarketOutOfOrder(); 
		stateChanged();
		
	}
	
	// utilities 
	public void setGui(RestaurantGui RG) {
		this.gui = RG;
	}
	
	public void addCook(DavidCookRole c){
		cookAgent = c;
	}
	
	public void addCashier(DavidCashierRole c){
		cashierAgent = c;
	}
	
	public void emptyMarket(){
		for(int i=0; i<MarketInventory.size(); i++){
			MarketInventory.get(i).quantity = 0;
		}
	}
}
