package mainCity.restaurants.jeffersonrestaurant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mainCity.restaurants.jeffersonrestaurant.CookRole;
import mainCity.restaurants.jeffersonrestaurant.CookRole.OrderState;
import agent.Agent;
import mainCity.restaurants.jeffersonrestaurant.interfaces.*;



public class MarketRole extends Agent implements Market {

	
	private CookRole cook;
	private CashierRole cashier;
	private double monies;
	
	
	
	public MarketRole() {
		inventory.put("steak",1);
		inventory.put("chicken",1);
		inventory.put("salad",1);
		inventory.put("pizza",1);
		monies=0;
	}
	
	public void setCook(CookRole c){
		cook=c;
		
	}
	
	public void setCashier(CashierRole ca){
		cashier=ca;
		
	}


	private String name;
	Map<String, Integer> inventory =  new HashMap<String,Integer>();
	

	
	Timer timer = new Timer();
	
	public List <Request> requests = Collections.synchronizedList(new ArrayList<Request>());
	
	public class Request{
		String item;
		int quantity;
		boolean processed;
		
		public Request(String i, int q){
			item=i;
			quantity=q;
			processed=false;
		}
		
	}
	
	
	// Messages

	public void msgNeedStock(String item, Integer quantity){
		Do("market recieved message");
		requests.add(new Request(item,quantity));
		stateChanged();
		
	}
	
	public void msgHereIsMonies(double m){
		
		monies+=m;
	}
	
	
	// Scheduler
	
	protected boolean pickAndExecuteAnAction(){
		synchronized(requests){
			for(Request r: requests){
				if(!r.processed){
					Do("triggered market scheduler");
					processRequest(r);	
					return true;
				}	
			}
		}
		
		
		return false;
	}
		

	
	
	// Actions
	
	
	private void processRequest(Request r){
		if(inventory.get(r.item)==0){
			r.processed=true;
			
					Do("order cannot be fulfilled");
		
			
			cook.msgCannotFulfill(r.item, r.quantity);
			return;
		}
		
		if(inventory.get(r.item)> r.quantity|inventory.get(r.item)== r.quantity){
			Do("entered market processing action");
			r.processed=true;
			int curr=inventory.get(r.item);
			curr=curr-r.quantity;
			
					Do("order is being fulfilled");
		
			cook.msgRestockFulfilled(r.item, r.quantity);
            inventory.put(r.item, curr);
            
            //Bill the Market $5 per unit
            cashier.msgHereIsMarketBill(r.quantity*5, this);
			
			return;
		}
		
		if (inventory.get(r.item) < r.quantity){
			r.processed=true;
			
					Do("order can be partially fulfilled");
			
			cook.msgPartialFulfill(r.item, inventory.get(r.item) );
			inventory.put(r.item, 0);
			//Bill the Market $5 per unit
			cashier.msgHereIsMarketBill(r.quantity*5, this);
			return;			
		}	
	}
	
	

	

	
	
}
