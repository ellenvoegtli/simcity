package mainCity.market.test;

import role.market.MarketDeliveryManRole;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market.MarketCustomerRole;
import mainCity.market.gui.CustomerGui;
import mainCity.market.test.mock.MockEmployee;
import junit.framework.TestCase;


public class CustomerTest extends TestCase {
	MarketCustomerRole customer;
	CustomerGui gui;
	MockEmployee employee;
	
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Customer");
        customer = new MarketCustomerRole(d, d.getName());
        d.addRole(ActionType.work, customer);
        
        //customer1 = new MockCustomer("MockCustomer1"); 
        //customer2 = new MockCustomer("MockCustomer2");  
        gui = new MockCustomerGui("MockCustomerGui");
        deliveryMan.setGui(gui);
        
        employee = new MockEmployee("MockEmployee");
        cashier = new MockCashier("MockCashier");
        cook = new MockCook("MockCook");
        
        
	}
}