package mainCity.market.test.mock;

import mainCity.market.interfaces.Employee;
import mainCity.market.interfaces.EmployeeGuiInterface;



public class MockEmployeeGui extends Mock implements EmployeeGuiInterface {
	public Employee employee;
	
	public MockEmployeeGui(String name){
		super(name);
	}
	
	@Override
	public void DoGoToStation(){
		employee.msgAtStation();
		log.add(new LoggedEvent("Gui told to DoGoToStation by agent."));
	}
	@Override
    public void DoGoToCashier(){
    	employee.msgAtCashier();
    	log.add(new LoggedEvent("Gui told to DoGoToCashier by agent."));
    }
	@Override
    public void DoFulfillOrder(){
    	log.add(new LoggedEvent("Gui told to DoFulfillOrder by agent."));
    }
    @Override
    public void DoGoToDeliveryMan(){
    	employee.msgAtDeliveryMan();
    	log.add(new LoggedEvent("Gui told to DoGoToDeliveryMan by agent."));
    }
    public void DoLeaveMarket(){
    	
    }
    @Override
    public void DoPickUpWaitingCustomer(int x, int y){
    	employee.msgAtWaitingRoom();
    	log.add(new LoggedEvent("Gui told to DoPickUpWaitingCustomer by agent."));
    }
    
    public void guiReappear(){
    	
    }
	
}