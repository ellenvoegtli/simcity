package mainCity.market1.test.mock;

import mainCity.market1.interfaces.CustomerGuiInterface;
import mainCity.market1.interfaces.Employee;



public class MockCustomerGui extends Mock implements CustomerGuiInterface {
	Employee employee;
	
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
	
	public boolean goInside(){
		return true;
	}
}