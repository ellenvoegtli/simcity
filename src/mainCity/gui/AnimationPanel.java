package mainCity.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.PersonGui.Coordinate;
import transportation.gui.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimerTask;

public class AnimationPanel extends JPanel implements ActionListener, MouseListener {

	//Dimensions for the window that will have the City in it
    private final int WINDOWX = 780;
    private final int WINDOWY = 480;
    
    //House data (house sprites are 80x64)
    private final int TopHouseLocY = -4;
    private final int BotHouseLocY = 416;
    
    public int xPos;
    public int yPos;
    
    //List of buildings in the city 
    List<Building> buildings = new ArrayList<Building>(); 
    
    
    
    //List of home objects in the city, each gas building and occupied reference
    public static List<HomeObject> houses = new ArrayList<HomeObject> ();
    
    
    //list of apartment buildings in the city, each has reference to how many apartmnents are filled and the building
    public static List<ApartmentObject> apartments= new ArrayList<ApartmentObject> ();
   //public static  Map<Building, List<Integer>> apartments = new HashMap<Building, List<Integer> >();
   
   //list to hold apartments in a single apartment building
   
   public static List<Integer> Apt = new ArrayList<Integer>();
    
    //Road Data
    public ArrayList<Lane> lanes;
    public ArrayList<Intersection> intersections; 
    
    private final int RoadWidth = 50; 
    
    //Setting up data for stop sign image
    private BufferedImage stopSign = null;
    
    boolean onlyOnce = true;
    boolean dontReset = false;
    boolean timeOnce = true;
    public boolean atStop = false;
    int count; 
    private Image bufferImage;
    private Dimension bufferSize;
    private boolean recklessDriving = false;
    
    CityGui gui; 
    List<BusGui> Buses = Collections.synchronizedList(new ArrayList<BusGui>()); 
    List<CarGui> Cars = Collections.synchronizedList(new ArrayList<CarGui>()); 
    List<CarStopLocation> carStops = new ArrayList<CarStopLocation>();

    //List of all guis that we need to animate in the city (Busses, Cars, People...etc) 
    //Will be Added in CityPanel analogous to RestaurantPanel
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    private List<Gui> personGuis = Collections.synchronizedList(new ArrayList<Gui>());

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
		addMouseListener(this);
		
        StringBuilder path = new StringBuilder("imgs/");
        
        //Populating 
        carStops.add(new CarStopLocation(320, 105, PersonAgent.CityLocation.home)); 
        carStops.add(new CarStopLocation(610, 230, PersonAgent.CityLocation.restaurant_david)); 
        carStops.add(new CarStopLocation(155, 155, PersonAgent.CityLocation.restaurant_marcus)); 
        carStops.add(new CarStopLocation(345, 355, PersonAgent.CityLocation.restaurant_jefferson)); 
        carStops.add(new CarStopLocation(155, 305, PersonAgent.CityLocation.restaurant_ellen)); 
        carStops.add(new CarStopLocation(347, 105, PersonAgent.CityLocation.restaurant_ena)); 
        carStops.add(new CarStopLocation(155, 230, PersonAgent.CityLocation.bank)); 
        carStops.add(new CarStopLocation(610, 285, PersonAgent.CityLocation.bank2));
        carStops.add(new CarStopLocation(440, 105, PersonAgent.CityLocation.market)); 
        carStops.add(new CarStopLocation(610, 140, PersonAgent.CityLocation.market2));
        
        //Bus Stop 
        try {
			stopSign = ImageIO.read(new File(path.toString() + "stopsign.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //Instantiating roads
        lanes = new ArrayList<Lane>();
        intersections = new ArrayList<Intersection>();
        
//Creating Lanes (int xo, int yo, int w, int h, int xv, int yv, boolean ish, Color lc, Color sc)
        
      //top road left
        Lane l = new Lane( 0, 75, 125, (RoadWidth/2), -5, 0, true, Color.gray, Color.white );  
        lanes.add(l); //0
        l = new Lane (176, 75, 429, (RoadWidth/2), -5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //1
        l = new Lane( 656, 75, 125, (RoadWidth/2), -5, 0, true, Color.gray, Color.white );
        lanes.add(l); //2
        
      //top road right
        l = new Lane( 0, 100, 125, (RoadWidth/2), 5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //3
        l = new Lane( 176, 100, 429, (RoadWidth/2), 5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //4
        l = new Lane( 656, 100, 125, (RoadWidth/2), 5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //5
        
      //bot road left
        l = new Lane( 0, 350, 125, (RoadWidth/2), -5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //6
        l = new Lane( 176, 350, 429, (RoadWidth/2), -5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //7
        l = new Lane( 656, 350, 125, (RoadWidth/2), -5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //8
        
      //bot road right 
        l = new Lane( 0, 375, 125, (RoadWidth/2), 5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //9
        l = new Lane( 176, 375, 429, (RoadWidth/2), 5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //10
        l = new Lane( 656, 375, 125, (RoadWidth/2), 5, 0, true, Color.gray, Color.white ); 
        lanes.add(l); //11
        
      //left vertical road
        l = new Lane( 125, 126, (RoadWidth/2), 224, 0, 5, false, Color.gray, Color.white );
        lanes.add(l); //12
        l = new Lane( 150, 126, (RoadWidth/2), 224, 0, -5, false, Color.gray, Color.white );
        lanes.add(l); //13
        
      //center vertical road
        l = new Lane( 365, 126, (RoadWidth/2), 224, 0, 5, false, Color.gray, Color.white );
        lanes.add(l); //14
        l = new Lane( 390, 126, (RoadWidth/2), 224, 0, -5, false, Color.gray, Color.white );
        lanes.add(l); //15
        
      //right vertical road
        l = new Lane( 605, 126, (RoadWidth/2), 224, 0, 5, false, Color.gray, Color.white );
        lanes.add(l); //16
        l = new Lane( 630, 126, (RoadWidth/2), 224, 0, -5, false, Color.gray, Color.white );
        lanes.add(l); //17
        
//Intersections 
        
      //top left
        Intersection in = new Intersection( 125, 75, RoadWidth, RoadWidth, 5, 5, true, Color.gray, Color.white) ;
        intersections.add(in);
      
      //bot left
        in = new Intersection( 125, 350, RoadWidth, RoadWidth, 5, 5, true, Color.gray, Color.white) ;
        intersections.add(in);
      
      //bot right
        in = new Intersection( 605, 350, RoadWidth, RoadWidth, 5, 5, true, Color.gray, Color.white) ;
        intersections.add(in);
      
      //top right
        in = new Intersection( 605, 75, RoadWidth, RoadWidth, 5, 5, true, Color.gray, Color.white) ;
        intersections.add(in);
        
        //drawing top houses
        for(int i=0; i<7; i++){
	        Building house = new Building( ( 20 + (i*110) ), TopHouseLocY, "house1.png", "house" +i );
	        buildings.add(house);
	        addBuildingGui(house);

	        HomeObject home = new HomeObject(house, false);
	        houses.add(home);
        }
        
        //drawing bottom houses 
        for(int i=0; i<7; i++){
	        Building house = new Building( ( 20 + (i*110) ), BotHouseLocY, "house2.png", "apartment" +i);
	        		//list to hold apartments in a single apartment building
	        buildings.add(house);
	        addBuildingGui(house);
    		List<Integer> Apt = new ArrayList<Integer>();
	        ApartmentObject aptment = new ApartmentObject(house, Apt);
	        apartments.add(aptment);
        }
        
        //drawing restaurants 
        Building building = new Building ( 35, 150, "restaurant_right.png", "marcusRestaurant");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 35, 250, "restaurant_right.png", "EllenRestaurant");
        buildings.add(building); 
        addBuildingGui(building);
       
        building = new Building ( 190, 200, "bank.png", "bank");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 670, 250, "bank.png", "bank2");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 275, 150, "restaurant_right.png", "enarestaurant");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 275, 250, "restaurant_right.png", "jeffersonrestaurant");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 425, 200, "market.png", "market");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 670, 140, "market.png", "market2");
        buildings.add(building); 
        addBuildingGui(building);
        
        building = new Building ( 520, 200, "restaurant_right.png", "davidRestaurant");
        buildings.add(building); 
        addBuildingGui(building);
        
       
        
        
        
        javax.swing.Timer t = new javax.swing.Timer( 25, this );
		t.start();
      
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(100, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		count++; 
		
		if(Buses.size() != 0){
			
			//Creating first bus
			if(onlyOnce == true){
				onlyOnce = false;
				lanes.get(3).addVehicle(Buses.get(0));
			}
			
			//Creating second bus 
			if((count % 350 == 0) && dontReset == false){ 
				dontReset = true;		
				if(lanes.size() != 0){
					synchronized(lanes.get(3).vehicles){
						lanes.get(3).addVehicle(Buses.get(1));
					}
				}
			}
			
			if(Cars.size() != 0) {
				if(recklessDriving) { 
					if(Cars.get(0).getX() == 610 && Cars.get(0).getY() == 206) { 
						Cars.get(0).atBusStop = true;
					}
				}
			}

			for(int i=0; i<ContactList.stops.size(); i++){
				for(int s=0; s<Buses.size(); s++){
					if( ( Buses.get(s).getX() > (ContactList.stops.get(i).xLocation-2) ) 
						&& ( Buses.get(s).getX() < (ContactList.stops.get(i).xLocation+2) ) 
							&& ( Buses.get(s).getY() > (ContactList.stops.get(i).yLocation-2) ) 
								&& (Buses.get(s).getY() < (ContactList.stops.get(i).yLocation)+2) ) {
						
						Buses.get(s).atBusStop = true;
						if(count % 100 == 0){
							Buses.get(s).atBusStop = false;
							Buses.get(s).agent.msgAtBusStop(ContactList.stops.get(i).stopLocation);
						}	
					}
				}
			}
			
			int CarLocation = -1;  
			
			for(int i=0; i<carStops.size(); i++){
				for(int s=0; s<Cars.size(); s++){
					if( ( Cars.get(s).getX() > (carStops.get(i).getXCoord()-5) ) 
						&& ( Cars.get(s).getX() < (carStops.get(i).getXCoord()+5) ) 
							&& ( Cars.get(s).getY() > (carStops.get(i).getYCoord()-5) )
								&& (Cars.get(s).getY() < (carStops.get(i).getYCoord()+5) ) ) {
						
						if(carStops.get(i).getLocation() == Cars.get(s).owner.getDestination()) {
							
							Cars.get(s).owner.msgArrivedAtDestinationInCar();
							
							for(int g=0; g<lanes.size(); g++) { 
								for(int v=0; v<lanes.get(g).vehicles.size(); v++) { 
									
									synchronized(lanes.get(g).vehicles){
										synchronized(Cars){
											if(lanes.get(g).vehicles.get(v) == Cars.get(s)) {
												CarLocation = g;
												break;
											}
										}
									}
								}
							}
							
							lanes.get(CarLocation).vehicles.remove(Cars.get(s));
							Cars.remove(Cars.get(s));
						}
					}
				}
			}
				
			
			for(int t=0; t<Buses.size(); t++){
				
				if(Buses.get(t).getX() == 105 && Buses.get(t).getY() == 105){  
					lanes.get(3).redLight();
					if(count % 100 == 0) { 
						lanes.get(3).greenLight();
						lanes.get(3).vehicles.remove(Buses.get(t)); 
						intersections.get(0).addVehicle(Buses.get(t));
					}
				}
				
				if(Buses.get(t).getX() == 130 && Buses.get(t).getY() == 110){
					intersections.get(0).vehicles.remove(Buses.get(t)); 
					lanes.get(12).addVehicle(Buses.get(t)); 
				}
				
				if(Buses.get(t).getX() == 130 && Buses.get(t).getY() == 331){ 
					lanes.get(12).redLight();
					if(count % 100 == 0) { 
						lanes.get(12).greenLight();
						lanes.get(12).vehicles.remove(Buses.get(t));
						intersections.get(1).addVehicle(Buses.get(t));
					}
				}
				
				if(Buses.get(t).getX() == 155 && Buses.get(t).getY() == 380){ 
					intersections.get(1).vehicles.remove(Buses.get(t)); 
					lanes.get(10).addVehicle(Buses.get(t));
				}
				
				if(Buses.get(t).getX() == 586 && Buses.get(t).getY() == 380){ 
					lanes.get(10).redLight();
					if(count % 100 == 0) { 
						lanes.get(10).greenLight();	
						lanes.get(10).vehicles.remove(Buses.get(t));
						intersections.get(2).addVehicle(Buses.get(t));
					}
				}
				
				if(Buses.get(t).getX() == 635 && Buses.get(t).getY() == 355){ 
					intersections.get(2).vehicles.remove(Buses.get(t));
					lanes.get(17).addVehicle(Buses.get(t));
				}
				
				if(Buses.get(t).getX() == 635 && Buses.get(t).getY() == 129){ 
					lanes.get(17).redLight();
					if(count % 100 == 0) { 
						lanes.get(17).greenLight();	
						lanes.get(17).vehicles.remove(Buses.get(t));
						intersections.get(3).addVehicle(Buses.get(t));
					}
				}
				
				if(Buses.get(t).getX() == 610 && Buses.get(t).getY() == 80){ 
					intersections.get(3).vehicles.remove(Buses.get(t)); 
					lanes.get(1).addVehicle(Buses.get(t));
				}
				
				if( (Buses.get(t).getX() == 179) || (Buses.get(t).getX() == 180) && Buses.get(t).getY() == 80){  
					lanes.get(1).redLight();
					if(count % 100 == 0) { 
						lanes.get(1).greenLight();	
						lanes.get(1).vehicles.remove(Buses.get(t)); 
						intersections.get(0).addVehicle(Buses.get(t));
					}
				}
				
			}	
			
			if(Cars.size() != 0) {
				
				for(int c=0; c<Cars.size(); c++){
					
					if(Cars.get(c).getX() == 610 && Cars.get(c).getY() == 331){  
						lanes.get(16).redLight();
						if(count % 100 == 0) { 
							lanes.get(16).greenLight();
							lanes.get(16).vehicles.remove(Cars.get(c)); 
							intersections.get(2).addVehicle(Cars.get(c));
						}
					}
					
					if(Cars.get(c).getX() == 600 && Cars.get(c).getY() == 355){
						intersections.get(2).vehicles.remove(Cars.get(c)); 
						lanes.get(7).addVehicle(Cars.get(c)); 
					}
					
					if(Cars.get(c).getX() == 179 && Cars.get(c).getY() == 355){ 
						lanes.get(7).redLight();
						if(count % 100 == 0) { 
							lanes.get(7).greenLight();
							lanes.get(7).vehicles.remove(Cars.get(c));
							intersections.get(1).addVehicle(Cars.get(c));
						}
					}
					
					if(Cars.get(c).getX() == 155 && Cars.get(c).getY() == 350){ 
						intersections.get(1).vehicles.remove(Cars.get(c)); 
						lanes.get(13).addVehicle(Cars.get(c));
					}
					
					if(Cars.get(c).getX() == 155 && Cars.get(c).getY() == 129){
						lanes.get(13).redLight();
						if(count % 100 == 0) { 
							lanes.get(13).greenLight();
							lanes.get(13).vehicles.remove(Cars.get(c));
							intersections.get(0).addVehicle(Cars.get(c));
						}
					}
	
					
					if(Cars.get(c).getX() == 160 && Cars.get(c).getY() == 100){ 
						intersections.get(0).vehicles.remove(Cars.get(c));
						lanes.get(4).addVehicle(Cars.get(c));
					}
					
					
					if(Cars.get(c).getX() == 586 && Cars.get(c).getY() == 105){ 
						lanes.get(4).redLight();
						if(count % 100 == 0) { 
							lanes.get(4).greenLight();	
							lanes.get(4).vehicles.remove(Cars.get(c));
							intersections.get(3).addVehicle(Cars.get(c));
						}
					}
					
					
					if(Cars.get(c).getX() == 610 && Cars.get(c).getY() == 110){ 
						intersections.get(3).vehicles.remove(Cars.get(c)); 
						lanes.get(16).addVehicle(Cars.get(c));
					}
					
				}	
			}
		}
		
		//Collision detection car vs person
		if(gui != null) {
			if(gui.collision == true) { 
				for(int i=0; i<personGuis.size(); i++) { 
					for(int j=0; j<Cars.size(); j++) { 
						if(((PersonGui) personGuis.get(i)).getRectangle().intersects(Cars.get(j))) { 
							if(((PersonGui) personGuis.get(i)).myCar != Cars.get(j)) {
								((PersonGui) personGuis.get(i)).getPerson().msgHitByVehicle();
							}
						}
						
					}
				}
			}
		}
		
		int Car1Loc = -1;
		int Car2Loc = -1; 
		//Collision Detection car vs car 
		if(gui != null) {
			if(gui.collision == true) { 		
				for(int i=0; i<Cars.size(); i++) { 
					for(int j=0; j<Cars.size(); j++) { 
						if(Cars.get(i) != Cars.get(j)){ 
							if(Cars.get(i).intersects(Cars.get(j))) {
								Cars.get(i).owner.msgHitByVehicle(); 
								Cars.get(j).owner.msgHitByVehicle(); 
								lanes.get(16).vehicles.remove(Cars.get(i));
								lanes.get(16).vehicles.remove(Cars.get(j));
								Cars.clear();
							}
						}
					}
				}
			}
		}
		
		
		if(gui != null) {
			for(Map.Entry<String, CityCard> r : gui.getView().getCards().entrySet()) {
				if(!r.getValue().isVisible()) {
					r.getValue().backgroundUpdate();
				}
			}
		}
		
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

   //Draw city objects here (where we drew tables before)  
        
      //drawing intersections 
        for ( int j=0; j<intersections.size(); j++) { 
        	Intersection in = intersections.get(j); 
        	in.draw(g2);
        }
        
        //drawing lanes
        for ( int i=0; i<lanes.size(); i++ ) {
			Lane l = lanes.get(i);
			l.draw( g2 );
		}
        
        //drawing bus stops 
        g2.drawImage(stopSign, 320, 55, null); 
        g2.drawImage(stopSign, 660, 230, null);
        g2.drawImage(stopSign, 660, 155, null);
        g2.drawImage(stopSign, 660, 305, null);
        g2.drawImage(stopSign, 105, 155, null); 
        g2.drawImage(stopSign, 220 , 405, null);
        g2.drawImage(stopSign, 105, 305, null); 
        g2.drawImage(stopSign, 215 , 55, null); 
        g2.drawImage(stopSign, 105, 230, null);
        g2.drawImage(stopSign, 440, 55, null);
        g2.drawImage(stopSign, 440, 405, null);
      
        /*   ~~~~~~~~~~~~~~MAP OUT DOORWAYS FOR WHERE PEOPLE SHOULD GO.~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        g2.setColor(Color.LIGHT_GRAY);
        //Location of doorways 
        g2.fillRect(36, 55, 20, 20);  //house1 
        g2.fillRect(146, 55, 20, 20);
        g2.fillRect(256, 55, 20, 20);
        g2.fillRect(366, 55, 20, 20);
        g2.fillRect(476, 55, 20, 20);
        g2.fillRect(586, 55, 20, 20);
        g2.fillRect(696, 55, 20, 20); //house7

        //Location of doorways
        g2.fillRect(49, 400, 20, 20);  //house1
        g2.fillRect(159, 400, 20, 20);
        g2.fillRect(269, 400, 20, 20);
        g2.fillRect(379, 400, 20, 20);
        g2.fillRect(489, 400, 20, 20);
        g2.fillRect(599, 400, 20, 20);
        g2.fillRect(709, 400, 20, 20); //house7
        
        g2.fillRect(105, 180, 20, 20); //doorway
        g2.fillRect(105, 280, 20, 20); //doorway
        g2.fillRect(175, 230, 20, 20); //doorway
        g2.fillRect(347, 180, 20, 20); //doorway
        g2.fillRect(347, 280, 20, 20); //doorway
        g2.fillRect(415, 215, 20, 20); //doorway
        g2.fillRect(585, 230, 20, 20); //doorway
        g2.fillRect(655, 155, 22, 20);//market2 
         */

        synchronized(guis){	
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
        synchronized(personGuis) {
	        for(Gui gui : personGuis) { 
	        	if (gui.isPresent() ) {
	                gui.updatePosition();
	            }
	        }
	        for(Gui gui : personGuis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
    }
    
    public void setGui(CityGui g){ 
    	gui = g;
    }
    
    public void mousePressed(MouseEvent arg0) {
    	for (Building b: buildings) { 
    		if (b.contains(arg0.getX(), arg0.getY())){ 
    			gui.getView().setView(b.ID);
    		}
    	}
    	
    }
   
    
    
    public void addBusGui(BusGui gui){
    	 Buses.add(gui);
    }
    
    public void addCar(CarGui gui) { 
    	Cars.add(gui);
    }

    public void addPersonGui(PersonGui gui) {
        personGuis.add(gui);
    }
    
    public void addBuildingGui(Building gui) { 
    	guis.add(gui);
    }
 

    public void addMarketDeliveryGui(DeliveryManGui gui){
    	personGuis.add(gui);
    }
    
        
    public static List<HomeObject> getHouses()
    {
    	return houses;
    }
    /*public static Map<Building, List<Integer>> getApartments()
    {
    	return apartments;
    }*/
    
    public static List<ApartmentObject> getApartments()
    {
    	return apartments;

    }
    public static List<Integer> getApts()
    {
    	return Apt;
    }
    
    public List<Gui> getGuiList() {
    	return guis;
    }
    
    public List<Gui> getPersonGuiList() {
    	return personGuis;
    }
    
    public void setRecklessDriving(Boolean b) { 
    	recklessDriving = b;
    }
    
//Unused.
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
 
	public class HomeObject
	{
		private Building house;
		private boolean occupied;
		
		private HomeObject(Building bd, boolean occ)
		{
			this.house = bd;
			this.occupied = occ;
			
		}
		
		public void setBusy(boolean oc)
		{
			this.occupied = oc;
			//return occupied;
		}
		
		public boolean getBusy()
		{
			return occupied;
		}
		
		public Building getBuild()
		{
			return house;
		}
		
		
	}
	
	public class ApartmentObject
	{
		private Building apartment;
		private List<Integer> buildingSize;
		
		private ApartmentObject(Building bd, List<Integer> bldSize)
		{
			this.apartment = bd;
			this.buildingSize = bldSize;
			
		}
		
		/*public void setBusy(boolean oc)
		{
			this.occupied = oc;
			//return occupied;
		}*/
		
		public List<Integer> getComplex()
		{
			return buildingSize;
		}
		
		public Building getBuild()
		{
			return apartment;
		}
	}
	
	public class CarStopLocation { 
		private int xCoordinate; 
		private int yCoordinate; 
		private PersonAgent.CityLocation Location; 
		
		CarStopLocation(int x, int y, PersonAgent.CityLocation loc) { 
			xCoordinate = x; 
			yCoordinate = y; 
			Location = loc;
		}
		
		public int getXCoord() { 
			return xCoordinate; 
		}
		
		public int getYCoord() { 
			return yCoordinate; 
		}
		
		public PersonAgent.CityLocation getLocation() { 
			return Location;
		}
	}
}