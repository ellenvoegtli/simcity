package mainCity.market.test.mock;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.*;
import mainCity.market.MarketEmployeeRole.MyBusiness;
import mainCity.market.MarketEmployeeRole.MyCustomer;
import mainCity.market.interfaces.*;
import mainCity.market.interfaces.MarketCashier;


public class MockEmployee extends Mock implements Employee {
	public DeliveryMan deliveryMan;
	public MarketCashier cashier;
	
	public MockEmployee(String name){
		super(name);
	}

	
	public void msgAtCashier(){
		
	}
	public void msgAtWaitingRoom(){
		
	}
	public void msgAtDeliveryMan(){
		
	}
	public void msgAtStation(){
		
	}
	public void msgDoneLeaving(){
		
	}
	public void setHomeX(int x){
		
	}
	public void setHomeY(int y){
		
	}
	
	@Override
	public void msgAssignedToBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory){
		log.add(new LoggedEvent("Received msgAssignedToBusiness: " + restaurantName));
	}
	@Override
	public void msgAssignedToCustomer(Customer c, int waitPosX, int waitPosY){
		log.add(new LoggedEvent("Received msgAssignedToCustomer: " + c.getName()));
	}
	public void msgHereIsMyOrder(Customer c, Map<String, Integer> inventory, String deliveryMethod){
		log.add(new LoggedEvent("Received msgHereIsMyOrder from " + c.getName()));
	}
	@Override
	public void msgHereIsBill(Customer c, double amount){		//from cashier
		log.add(new LoggedEvent("Received msgHereIsBill from cashier for " + c.getName() +". Amount = $"+ amount));
	}
	public void msgHereIsBill(String name, double amount){		//from cashier
		log.add(new LoggedEvent("Received msgHereIsBill from cashier for " + name +". Amount = $"+ amount));
	}
	public void msgOrderFulfilled(MyCustomer mc){		//from timer
		
	}
	public void msgOrderFulfilled(MyBusiness mb){		//from timer
		
	}
	@Override
	public void msgDoneAndLeaving(Customer c){
		log.add(new LoggedEvent("Received msgDoneAndLeaving from " + c.getName()));
	}
	
}