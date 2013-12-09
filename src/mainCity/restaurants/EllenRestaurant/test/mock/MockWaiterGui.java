package mainCity.restaurants.EllenRestaurant.test.mock;


import mainCity.restaurants.EllenRestaurant.interfaces.WaiterGuiInterface;
import role.ellenRestaurant.EllenWaiterRole;


public class MockWaiterGui extends Mock implements WaiterGuiInterface {
	EllenWaiterRole waiter;
	
	public MockWaiterGui(String name){
		super(name);
	}
	
	public void setWaiter(EllenWaiterRole w){
		waiter = w;
	}
	
	@Override
	public void DoGoToTable(int t){
		log.add(new LoggedEvent("Going to table " + t));	
		waiter.msgAtTable();
	}
	@Override
	public void DoGoToCook(){
		log.add(new LoggedEvent("Going to stand to post order"));
		waiter.msgAtCook();
	}
	public void DoDeliverFood(int t, String choice){
		
	}
	public void DoWait(){
		
	}
	
	public void GoOffBreak(){
		
	}
	public void GoOnBreak(){
		
	}
	@Override
	public void DoPickUpWaitingCustomer(int x, int y){
		log.add(new LoggedEvent("Picking up waiting customer"));
		waiter.msgAtStart();
	}
	public void setIsDeliveringFood(boolean b){
		
	}
	public void DoLeaveRestaurant(){
		
	}
}