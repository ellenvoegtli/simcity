package mainCity.restaurants.EllenRestaurant.test.mock;

import java.util.Collection;

import mainCity.restaurants.EllenRestaurant.interfaces.*;


public class MockWaiter extends Mock implements Waiter{
	
	public Cashier cashier;

    public MockWaiter(String name) {
            super(name);

    }
    
    
    public void setInactive(){
    	
    }
	public Collection getMyCustomers(){
		return this.getMyCustomers();
	}
	
	public void msgPleaseSeatCustomer(Customer cust, int waitX, int waitY, int table){
		
	}
	
	public void msgReadyToOrder(Customer cust){
		
	}
	
	public void msgHereIsChoice(Customer cust, String choice){
		
	}
	@Override
	public void msgOrderDoneCooking(String choice, int tablenum){
		log.add(new LoggedEvent(choice + " done cooking"));
	}
	
	public void msgOutOfFood(String choice, int tablenum){
		
	}
	
	public void msgDoneAndLeaving(Customer cust){
		
	}
	
	public void msgBreakRequestResponse(boolean isOK){
		
	}

	
	
	
	public void msgIWantMyCheck(Customer cust, String choice){
		
	}
	
	@Override
	public void msgHereIsCheck(double amount, Customer cust){		//from cashier
		log.add(new LoggedEvent("Received HereIsCheck from cashier. Amount = $"+ amount));
	}
    
	
}