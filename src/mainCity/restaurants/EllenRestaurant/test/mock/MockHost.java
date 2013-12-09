package mainCity.restaurants.EllenRestaurant.test.mock;


import java.util.ArrayList;
import java.util.Collection;

import role.ellenRestaurant.EllenHostRole.Table;
import mainCity.restaurants.EllenRestaurant.interfaces.Host;


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
}