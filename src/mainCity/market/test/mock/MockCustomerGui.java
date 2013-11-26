package mainCity.market.test.mock;

import mainCity.market.interfaces.CustomerGuiInterface;
import mainCity.market.interfaces.Employee;



public class MockCustomerGui extends Mock implements CustomerGuiInterface {
	Employee employee;
	
	MockCustomerGui(String name){
		super(name);
	}
	
	
	public void DoGoToMarket(){
		
	}
	public void DoGoToEmployeeStation(int x, int y){
		
	}
	public void DoGoToWaitingArea(){
		
	}
	public void DoExitMarket(){
		
	}


	public void DoGoToCashier() {
		
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