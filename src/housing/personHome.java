package housing;

//import housing.houseAgent.Appliance;
//import housing.houseAgent.kitchenState;
//import housing.houseAgent.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Base class for simple agents
 */
public abstract class personHome 
{
	OccupantRole occupant;
	LandlordRole owner;
	String cookingMeal;
	Map<String, Integer> FoodSupply = new HashMap<String, Integer>();
	enum kitchenState {pending, checkingSupplies, stocked, reStocked, cooking, done};
	kitchenState kState = kitchenState.pending;
	enum type {apartment, house};
	type homeType;
	List <Appliance> kitchen = new ArrayList<Appliance>();
	List<String> needFood = new ArrayList<String>();
	
	personHome(OccupantRole occ, LandlordRole lndlrd, type home)
	{
		super();
		//homeType = home;
		occupant = occ;
		if(home == type.apartment)
		{
			owner = lndlrd;

		}
		FoodSupply.put("pasta" , 2);
		FoodSupply.put("fish", 1);
		FoodSupply.put("chickenSoup", 0);
		
		boolean working = true;
		
	kitchen.add(new Appliance("stove" , working));
	kitchen.add(new Appliance("fridge" , working));
	kitchen.add(new Appliance("sink" , working));

		
	}
	
	
	
	
	
//ACTIONS
	
	public void fixAppliance(String appName)
	{
		occupant.msgNeedsMaintenance(appName);
		
	}
	
	
	
	public void checkSupplies(String meal)
	{
		if(FoodSupply.get(meal) == null)
		{
			needFood.add(meal);
			occupant.msgNeedFood(needFood);
			
		}
		else
		{
			occupant.msgCookFood(meal);
		}
		
	}
	
	public void GroceryListDone()
	{
		if(needFood.size() != 0)
		{
			for(String f : needFood)
			{
				FoodSupply.put(f, 1);
				System.out.println("replenish food supply in kitchen");
				needFood.remove(f);
			}				
			System.out.println("no other food needed");

		}
	}
	
	

//Inner class for the different appliances in the kitchen
	
	
	public class Appliance
	{
		//DATA
		
			String appliance;
			boolean working = true;
			int yPos;
			int xPos;
			
			public Appliance (String nm, boolean wrk)
			{
				appliance = nm; 
				working = wrk;
				
				if(nm.equals("stove"))
				{
					xPos = 200;
					yPos = 25;
					
				}
				if(nm.equals("fridge"))
				{
					xPos = 300;
					yPos = 25;
				}
				if(nm.equals("sink"))
				{
					xPos = 250;
					yPos = 25;
				}
				if(nm.equals("TV"))
				{
					xPos = 50;
					yPos = 80;
				}
				
			}
			
			public int getXPos()
			{
				return xPos;
			}
			public int getYPos()
			{
				return yPos;
			}
			
			
	}


}





