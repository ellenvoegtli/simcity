package mainCity.gui;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.JPanel;

import role.market.MarketDeliveryManRole;
import transportation.BusAgent;
import transportation.gui.BusGui;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;

public class CityPanel extends JPanel{
	private CityGui gui; 
	private int clock;
	private int day;
	private List<PersonAgent> occupants = new ArrayList<PersonAgent>();
	private List<Timer> toBeCreated  = new ArrayList<Timer>();
		
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

    	//String[] actions = {"work"}; 
    	//addPerson("CarTest", 500, false, false, "enaWaiter", 6, 22, actions);

		//parseConfig("config9.txt");


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
	
	public void addDeliveryGui(MarketDeliveryManRole d, int x, int y){
		DeliveryManGui dg = new DeliveryManGui(d, x, y);
		d.setGui(dg);
		gui.getAnimationPanel().addMarketDeliveryGui(dg);
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
			
			if(filename.equals("config9.txt")) {
				clock = 4;
				day = 5;
			}
		}
		
		try {
		    FileInputStream fstream = new FileInputStream(filename);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    int staggerIndex = 0;
		    
		    while ((strLine = br.readLine()) != null)   {
		    	//System.out.println(strLine);
		    	if(!strLine.startsWith("-")) {
		    		Timer timer = new Timer();
		            timer.schedule(new CreationDelay(strLine), 400*(staggerIndex+1));
		            toBeCreated.add(timer);
		            ++staggerIndex;
		            
//		    		String name = strLine.substring(strLine.indexOf("Name")+5, strLine.indexOf("Cash")-1);
//				   	String cash = strLine.substring(strLine.indexOf("Cash")+5, strLine.indexOf("Renter")-1);
//				   	String renter = strLine.substring(strLine.indexOf("Renter")+7, strLine.indexOf("hasCar")-1);
//				   	String hasCar = strLine.substring(strLine.indexOf("hasCar")+7, strLine.indexOf("Occupation")-1); 
//				   	String occupation = strLine.substring(strLine.indexOf("Occupation")+11, strLine.indexOf("ShiftBegin")-1);
//				   	String shiftB = strLine.substring(strLine.indexOf("ShiftBegin")+11, strLine.indexOf("ShiftEnd")-1);
//				   	String shiftE = strLine.substring(strLine.indexOf("ShiftEnd")+9, strLine.indexOf("Actions")-1);
//				   	String actions = strLine.substring(strLine.indexOf("Actions")+8, strLine.length());
//				    String[] actionList = actions.split(",");
//				    addPerson(name, Integer.parseInt(cash), Boolean.parseBoolean(renter), Boolean.parseBoolean(hasCar), occupation, Integer.parseInt(shiftB), Integer.parseInt(shiftE), actionList);
		    	}
		    }

		    in.close();
		}
		catch(Exception e) {
			//System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void addPerson(String name, double c, boolean renter, boolean hasCar, String occupation, int sb, int se, String[] actions) {
		//System.out.println(name);
    	PersonAgent person = new PersonAgent(name);
		person.updateOccupation(occupation, sb, se);
		person.setCash(c);
		
		if(person.getName().toLowerCase().contains("walk")){
			person.setTransportation(true);
			person.setWalk(true);
		}
		else if (person.getName().toLowerCase().contains("bus")){
			person.setTransportation(true);
			person.setWalk(false);
		}
		person.setHomePlace(renter);
		person.setCar(hasCar);

		ContactList.getInstance().setPersonInstance(person, renter);
		System.out.println("selected house for person to live in");
		System.out.println("selected house for person: " + person.getName() + " to live in");
		PersonGui pg = new PersonGui(person, gui);
		gui.getAnimationPanel().addPersonGui(pg);
		pg.setAnimationPanel(gui.getAnimationPanel()); 
		person.setGui(pg);
		
		gui.addPerson(person);
		
		if(actions != null) {
			for(int i = 0; i < actions.length; ++i) {
				switch(actions[i]) {
					case "home":
						person.msgGoHome();
						break;
					case "renterHome":
						person.msgNeedToFix(person.renterHome);
						break;
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
						
					case "restaurant_ellen":
						person.msgGoToRestaurant("ellenrestaurant");
						break;
					case "restaurant_marcus":
						person.msgGoToRestaurant("marcusrestaurant");
						break;
					case "restaurant_david":
						person.msgGoToRestaurant("davidrestaurant");
						break;
					case "restaurant_ena":
						person.msgGoToRestaurant("enarestaurant");
						break;
					case "restaurant_jefferson":
						person.msgGoToRestaurant("jeffersonrestaurant");
						break;
				}
			}
		}
		
    	occupants.add(person);
    	person.startThread();
	}
	
	private void resetCity() {
		clock = 4;
		day = 1;
		
		gui.getAnimationPanel().getPersonGuiList().clear();

		for(Map.Entry<String, CityCard> entry : gui.getView().getCards().entrySet()) {
			entry.getValue().clearPeople();
		}
		
		for(PersonAgent p : occupants) {
			p.stopThread();
			p.getRoles().clear();
		}
		
		for(Timer t : toBeCreated) {
			t.cancel();
		}
		
		toBeCreated.clear();
		ContactList.getInstance().clearOccupants();
		occupants.clear();
	}
	
	class CreationDelay extends TimerTask {
		String strLine;
		
		public CreationDelay(String s) {
			this.strLine = s;
		}
		
		public void run() {
			String name = strLine.substring(strLine.indexOf("Name")+5, strLine.indexOf("Cash")-1);
		   	String cash = strLine.substring(strLine.indexOf("Cash")+5, strLine.indexOf("Renter")-1);
		   	String renter = strLine.substring(strLine.indexOf("Renter")+7, strLine.indexOf("hasCar")-1);
		   	String hasCar = strLine.substring(strLine.indexOf("hasCar")+7, strLine.indexOf("Occupation")-1);
		   	String occupation = strLine.substring(strLine.indexOf("Occupation")+11, strLine.indexOf("ShiftBegin")-1);
		   	String shiftB = strLine.substring(strLine.indexOf("ShiftBegin")+11, strLine.indexOf("ShiftEnd")-1);
		   	String shiftE = strLine.substring(strLine.indexOf("ShiftEnd")+9, strLine.indexOf("Actions")-1);
		   	String actions = strLine.substring(strLine.indexOf("Actions")+8, strLine.length());
		    String[] actionList = actions.split(",");
		    addPerson(name, Integer.parseInt(cash), Boolean.parseBoolean(renter), Boolean.parseBoolean(hasCar), occupation, Integer.parseInt(shiftB), Integer.parseInt(shiftE), actionList);
		}
	}
}
