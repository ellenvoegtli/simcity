package mainCity.gui;

import housing.gui.HomeGui;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.JPanel;

import transportation.BusAgent;
import transportation.gui.BusGui;
import mainCity.PersonAgent;
import mainCity.bank.gui.BankGui;
import mainCity.bank.gui.BankPanel;
import mainCity.contactList.ContactList;
import mainCity.market.*;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.EllenRestaurantGui;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantGui;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantPanel;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.enaRestaurant.gui.*;

public class CityPanel extends JPanel{
	private CityGui gui; 
	private int clock;
	private List<PersonAgent> occupants = new ArrayList<PersonAgent>();
		
	public CityPanel(CityGui gui) {
		this.gui = gui;
		clock = 6;
		
    	MarketGui marketGui = new MarketGui();
    	ContactList.getInstance().setMarket(marketGui.getMarketPanel());
    	marketGui.setVisible(true);
		
	    EllenRestaurantGui ellenRestaurant = new EllenRestaurantGui();
	    ContactList.getInstance().setEllenRestaurant(ellenRestaurant.getEllenRestaurantPanel());
        //ellenRestaurant.setVisible(true);
		
		EnaRestaurantGui enaRestaurant = new EnaRestaurantGui();
	    ContactList.getInstance().setEnaRestaurant(enaRestaurant.getEnaRestaurantPanel());
	    //enaRestaurant.setVisible(true);
		
		//DavidRestaurantGui davidRestGui = new DavidRestaurantGui(); 
		//davidRestGui.setVisible(true);
		
    	MarcusRestaurantGui marcusRestaurant = new MarcusRestaurantGui();
    	ContactList.getInstance().setMarcusRestaurant(marcusRestaurant.getMarcusRestaurantPanel());
    	//marcusRestaurant.setVisible(true);

    	//JeffersonRestaurantGui jeffersonRestaurant = new JeffersonRestaurantGui();
    	//ContactList.getInstance().setJeffersonRestaurant(jeffersonRestaurant.getJeffersonRestaurantPanel());
    	//jeffersonRestaurant.setVisible(true);
    	

    	BankGui bank = new BankGui();
    	ContactList.getInstance().setBank(bank.getBankPanel());
    	//bank.setVisible(true);
    	
    	HomeGui home= new HomeGui();
    	ContactList.getInstance().setHome(home.getHomePanel());
    	//home.setVisible(true);
    	

    	//Hardcoding a bus
    	BusAgent bus = new BusAgent();
    	BusGui bg = new BusGui(15,15,16,16,bus);
    	bus.setGui(bg);
    	gui.getAnimationPanel().addBusGui(bg);
    	bus.startThread();
    	
    	
    	PersonAgent person = new PersonAgent("Greeter");
    	person.msgGoToWork();
    	PersonGui pg1 = new PersonGui(person, gui);
    	person.updateOccupation("marketGreeter", 8, 11);
    	gui.getAnimationPanel().addPersonGui(pg1);
    	person.setGui(pg1);
    	person.startThread(); 
    	
    	/*
    	PersonAgent person2 = new PersonAgent("Cook");
    	person2.msgGoToWork();
    	PersonGui pg2 = new PersonGui(person2, gui);
    	person2.updateOccupation("ellenCook", 8, 11);
    	gui.getAnimationPanel().addPersonGui(pg2);
    	person2.setGui(pg2);
    	person2.startThread();
    	*/
    	
		//parseConfig();
   
/*
    	PersonAgent person = new PersonAgent("joeMoe");
    	PersonAgent person2 = new PersonAgent("Waiter");
    	PersonAgent person3 = new PersonAgent("Cook");
    	PersonAgent person4 = new PersonAgent("Cashier");
    	PersonAgent person5 = new PersonAgent("Host");
    	
    	
    	
    	
    	
    	occupants.add(person);
    	occupants.add(person2);
    	occupants.add(person3);
    	occupants.add(person4);
    	occupants.add(person5);*/

    	//person.msgGoHome();
    	//person.msgGotHungry();
    	//person.msgGoToMarket();
    	//person.msgGoToWork();
    	/*
		PersonGui pg1 = new PersonGui(person, gui);
		PersonGui pg2 = new PersonGui(person2, gui); 
		PersonGui pg3 = new PersonGui(person3, gui);
		PersonGui pg4 = new PersonGui(person4, gui);
		PersonGui pg5 = new PersonGui(person5, gui);
		

		//person.updateOccupation("rich", -1, -1);
		//person2.updateOccupation("marcusWaiter", 8, 22);
		//person3.updateOccupation("marcusCook", 8, 22);
		//person4.updateOccupation("marcusCashier", 8, 22);
		//person5.updateOccupation("marcusHost", 7, 22);

		
		//person2.updateOccupation("enaHost", 8, 11);
		//person3.updateOccupation("enaCook", 8, 11);
		//person4.updateOccupation("enaCashier", 8, 11);
		//person5.updateOccupation("enaWaiter", 7, 10);
		 
		
		//person2.updateOccupation("ellenWaiter", 8, 11);
		//person3.updateOccupation("ellenCook", 8, 11);
		//person4.updateOccupation("ellenCashier", 8, 11);
		//person5.updateOccupation("ellenHost", 7, 10);
		
		person2.updateOccupation("marketGreeter", 7, 11);
		person3.updateOccupation("marketCashier", 7, 11);
		person4.updateOccupation("marketDeliveryMan", 7, 11);
		//person5.updateOccupation("enaWaiter", 7, 10);


		gui.getAnimationPanel().addPersonGui(pg1);
		gui.getAnimationPanel().addPersonGui(pg2);
		gui.getAnimationPanel().addPersonGui(pg3);
		gui.getAnimationPanel().addPersonGui(pg4);
		gui.getAnimationPanel().addPersonGui(pg5);
		

		person.setGui(pg1);
		person2.setGui(pg2);
		person3.setGui(pg3);
		person4.setGui(pg4);
		person5.setGui(pg5);


		//person.msgGoToRestaurant();
		person2.msgGoToWork();
		person3.msgGoToWork();
		person4.msgGoToWork();

		person5.msgGoToWork();	
		
		

		//person.startThread(); 
		person2.startThread(); 
		person3.startThread();
		person4.startThread();
		person5.startThread();
		*/
    	
    	//add(personPanel);
    	
		//Instantiation of the Global City Clock
		Runnable standChecker = new Runnable() {
			 public void run() {
				 clock = (clock+1) % 24;
				 updateCity();
			 }
		 };

		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(standChecker, 0, 5, TimeUnit.SECONDS); //Timer goes off every 10 seconds
	}
	
	private void updateCity() {
		for(PersonAgent p : occupants) {
			p.updateClock(clock);
		}
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
				   	String cash = strLine.substring(strLine.indexOf("Cash")+5, strLine.indexOf("Occupation")-1);
				   	String occupation = strLine.substring(strLine.indexOf("Occupation")+11, strLine.indexOf("ShiftBegin")-1);
				   	String shiftB = strLine.substring(strLine.indexOf("ShiftBegin")+11, strLine.indexOf("ShiftEnd")-1);
				   	String shiftE = strLine.substring(strLine.indexOf("ShiftEnd")+9, strLine.indexOf("Actions")-1);
				   	String actions = strLine.substring(strLine.indexOf("Actions")+8, strLine.length());
				    String[] actionList = actions.split(",");
				    	
				   	for(int i = 0; i < actionList.length; ++i) {
				   		System.out.println(actionList[i]);
				   	}
				    	
				    addPerson(name, Integer.parseInt(cash), occupation, Integer.parseInt(shiftB), Integer.parseInt(shiftE), actionList);
		    	}
		    }

		    in.close();
		}
		catch(Exception e) {
			//System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void addPerson(String name, double c, String occupation, int sb, int se, String[] actions) {
    	PersonAgent person = new PersonAgent(name);
		person.updateOccupation(occupation, sb, se);
		person.setCash(c);

		PersonGui pg = new PersonGui(person, gui);
		gui.getAnimationPanel().addPersonGui(pg);
		person.setGui(pg);
		
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
			}
		}
		
    	occupants.add(person);
    	person.startThread();
	}
}
