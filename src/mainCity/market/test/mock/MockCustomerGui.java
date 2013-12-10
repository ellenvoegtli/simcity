package mainCity.market.test.mock;

import java.util.Map;

import mainCity.market.interfaces.Customer;
import mainCity.market.interfaces.CustomerGuiInterface;
import mainCity.market.interfaces.Employee;



public class MockCustomerGui extends Mock implements CustomerGuiInterface {
	public Employee employee;
	public Customer customer;
	
	public MockCustomerGui(String name){
		super(name);
	}
	
	@Override
	public void DoGoToMarket(){
		log.add(new LoggedEvent("Gui told to DoGoToMarket by agent."));
	}
	@Override
	public void DoGoToEmployeeStation(int x, int y){
		log.add(new LoggedEvent("Gui told to DoGoToEmployeeStation by agent."));
	}
	@Override
	public void DoGoToWaitingArea(){
		log.add(new LoggedEvent("Gui told to DoGoToWaitingArea by agent."));
	}
	@Override
	public void DoExitMarket(){
		log.add(new LoggedEvent("Gui told to DoExitMarket by agent."));
	}

	@Override
	public void DoGoToCashier() {
		log.add(new LoggedEvent("Gui told to DoGoToCashier by agent."));
	}


	public int getWaitingPosX() {
		return 0;
	}


	public int getWaitingPosY() {
		return 0;
	}
	
	public boolean goInside(Map<String, Integer> m){
		return true;
	}
}