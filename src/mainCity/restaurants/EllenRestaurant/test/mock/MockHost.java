package mainCity.restaurants.EllenRestaurant.test.mock;


import java.util.ArrayList;
import java.util.Collection;

import role.ellenRestaurant.EllenHostRole.Table;
import mainCity.restaurants.EllenRestaurant.interfaces.Host;
import mainCity.restaurants.EllenRestaurant.interfaces.Waiter;


public class MockHost extends Mock implements Host {
	public Collection<Table> tables = new ArrayList<Table>();
	
	public MockHost(String name){
		super(name);
	}
	
	public void addTable(Table t){
		tables.add(t);
	}
	@Override
	public void msgTableFree(int t){
		log.add(new LoggedEvent("Received msgTableFree for table " + t));
	}
	
	public void msgIWantBreak(Waiter w){
		
	}
	public void msgComingOffBreak(Waiter w){
		
	}
}