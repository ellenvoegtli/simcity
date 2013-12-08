package mainCity.gui;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.JPanel;

import role.market1.Market1DeliveryManRole;
import role.market2.Market2DeliveryManRole;
import transportation.BusAgent;
import transportation.gui.BusGui;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;

public class CityPanel extends JPanel{
	private CityGui gui; 
	private int clock;
	private int day;
	private List<PersonAgent> occupants = new ArrayList<PersonAgent>();
		
	public CityPanel(CityGui gui) {
		this.gui = gui;
		clock = 4;
		day = 1;//Will be 0-6 for each day, 0 = Sunday...6 = Saturday
		
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

		//parseConfig("config.txt");

		//Instantiation of the Global City Clock
		Runnable cityClock = new Runnable() {
			 public void run() {
				 clock = (clock+1) % 24;
				 if(clock == 0) {
					 day = (day+1) % 6;
					 
					 switch(day) {
						case 0:
						 	System.out.println("It is now Sunday");
							break;
						case 1:
						 	System.out.println("It is now Monday");			 
							break;
						case 2:
							System.out.println("It is now Tuesday");
							break;
						case 3:
							System.out.println("It is now Wednesday");
							break;
						case 4:
							System.out.println("It is now Thursday");
							break;
						case 5:
							System.out.println("It is now Friday");
							break;
						case 6:
							System.out.println("It is now Saturday");
							break;
					 }
				 }
				 updateCity();
			 }
		 };

		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(cityClock, 0, 15, TimeUnit.SECONDS); //Timer goes off every 15 seconds
	}
	
	public void addDeliveryGui(Market1DeliveryManRole d){
		DeliveryManGui1 dg = new DeliveryManGui1(d);
		d.setGui(dg);
		gui.getAnimationPanel().addMarketDeliveryGui(dg);
	}
	
	public void addDelivery2Gui(Market2DeliveryManRole d){
		DeliveryManGui2 dg = new DeliveryManGui2(d);
		d.setGui(dg);
		gui.getAnimationPanel().addMarket2DeliveryGui(dg);
	}
	
	private void updateCity() {
		for(PersonAgent p : occupants) {
			p.updateClock(clock, day);
		}
	}
	
	public List<PersonAgent> getOccupants(){
		return occupants;
	}
	
	
	public void parseConfig(String filename) {
		if(!occupants.isEmpty()) {
			resetCity();
		}
		
		try {
		    FileInputStream fstream = new FileInputStream(filename);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null)   {
		    	System.out.println(strLine);
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
					case "homeAndEat":	//changed from hungry
						person.msgGotHungryForHome();	//changed from gotHungry
						break;
					case "market":
						person.msgGoToMarket();
						break;
					case "restaurant":
						person.msgGoToRestaurant();
						break;
					case "bank":
						person.msgGoToBank("deposit");
						break;
					case "rob":
						person.msgGoToBank("rob");
						break;
				}
			}
		}
		
    	occupants.add(person);
    	person.startThread();
	}
	
	private void resetCity() {
		gui.getAnimationPanel().getPersonGuiList().clear();
		for(PersonAgent p : occupants) {
			p.stopThread();
			p.getRoles().clear();
		}
		
		for(Map.Entry<String, CityCard> entry : gui.getView().getCards().entrySet()) {
			entry.getValue().clearPeople();
		}
		
		occupants.clear();
	}
}
