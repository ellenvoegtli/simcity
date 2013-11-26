package mainCity.gui;

import housing.gui.HomeGui;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.JPanel;

import role.market.MarketDeliveryManRole;
import transportation.BusAgent;
import transportation.gui.BusGui;
import mainCity.PersonAgent;
import mainCity.bank.gui.BankPanel;
import mainCity.contactList.ContactList;
import mainCity.market.*;
import mainCity.market.gui.*;
import mainCity.restaurants.marcusRestaurant.gui.*;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantGui;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.enaRestaurant.gui.*;

public class CityPanel extends JPanel{
	private CityGui gui; 
	private int clock;
	private List<PersonAgent> occupants = new ArrayList<PersonAgent>();
		
	public CityPanel(CityGui gui) {
		this.gui = gui;
		clock = 4;
		
		ContactList.getInstance().setCity(this);

    	//Hardcoding a bus
    	BusAgent bus = new BusAgent();
    	BusGui bg = new BusGui(15,15,16,16,bus);
    	bus.setGui(bg);
    	gui.getAnimationPanel().addBusGui(bg);
    	bus.startThread();

    	BusAgent bus1 = new BusAgent(); 
    	BusGui bg1 = new BusGui(15,15,16,16,bus1); 
    	bus1.setGui(bg1);
    	gui.getAnimationPanel().addBusGui(bg1); 
    	bus1.startThread();

		parseConfig();

		//Instantiation of the Global City Clock
		Runnable cityClock = new Runnable() {
			 public void run() {
				 clock = (clock+1) % 24;
				 updateCity();
			 }
		 };

		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(cityClock, 0, 30, TimeUnit.SECONDS); //Timer goes off every 30 seconds
	}
	
	public void addDeliveryGui(MarketDeliveryManRole d){
		DeliveryManGui dg = new DeliveryManGui(d);
		d.setGui(dg);
		gui.getAnimationPanel().addMarketDeliveryGui(dg);
	}
	
	private void updateCity() {
		for(PersonAgent p : occupants) {
			p.updateClock(clock);
		}
	}
	
	public List<PersonAgent> getOccupants(){
		return occupants;
	}
	
	
	private void parseConfig() {
		try {
		    FileInputStream fstream = new FileInputStream("config.txt");
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
	
		    while ((strLine = br.readLine()) != null)   {
		    	if(!strLine.startsWith("-")) {
				   	String name = strLine.substring(strLine.indexOf("Name")+5, strLine.indexOf("Cash")-1);
				   	String cash = strLine.substring(strLine.indexOf("Cash")+5, strLine.indexOf("Renter")-1);
				   	String renter = strLine.substring(strLine.indexOf("Renter")+7, strLine.indexOf("Occupation")-1);
				   	String occupation = strLine.substring(strLine.indexOf("Occupation")+11, strLine.indexOf("ShiftBegin")-1);
				   	String shiftB = strLine.substring(strLine.indexOf("ShiftBegin")+11, strLine.indexOf("ShiftEnd")-1);
				   	String shiftE = strLine.substring(strLine.indexOf("ShiftEnd")+9, strLine.indexOf("Actions")-1);
				   	String actions = strLine.substring(strLine.indexOf("Actions")+8, strLine.length());
				    String[] actionList = actions.split(",");

				    addPerson(name, Integer.parseInt(cash), Boolean.parseBoolean(renter), occupation, Integer.parseInt(shiftB), Integer.parseInt(shiftE), actionList);
		    	}
		    }

		    in.close();
		}
		catch(Exception e) {
			//System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void addPerson(String name, double c, boolean renter, String occupation, int sb, int se, String[] actions) {
		System.out.println(name);
    	PersonAgent person = new PersonAgent(name);
		person.updateOccupation(occupation, sb, se);
		person.setCash(c);
		
		person.setHomePlace(renter);
		System.out.println("selected house for person to live in");
		PersonGui pg = new PersonGui(person, gui);
		gui.getAnimationPanel().addPersonGui(pg);
		person.setGui(pg);
		
		gui.addPerson(person);
		
		if(actions != null) {
			for(int i = 0; i < actions.length; ++i) {
				switch(actions[i]) {
					case "work":
						if(!occupation.equals("rich")) person.msgGoToWork();
						break;
					case "hungry":
						person.msgGotHungry();
						break;
					case "market":
						person.msgGoToMarket();
						break;
					case "restaurant":
						person.msgGoToRestaurant();
						break;
					case "bank":
						person.msgGoToBank("deposit");
				}
			}
		}
		
    	occupants.add(person);
    	person.startThread();
	}
}
