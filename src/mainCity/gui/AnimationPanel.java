package mainCity.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import mainCity.contactList.ContactList;
import transportation.BusAgent;
import transportation.gui.BusGui;
import transportation.gui.Lane;
import transportation.gui.Vehicle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener, MouseListener {

	//Dimensions for the window that will have the City in it
    private final int WINDOWX = 780;
    private final int WINDOWY = 480;
    
    //House data (house sprites are 80x64)
    private final int TopHouseLocY = -4;
    private final int BotHouseLocY = 416;
    
    //List of buildings in the city 
    List<Building> buildings = new ArrayList<Building>(); 
    
    //Road Data
    ArrayList<Lane> lanes;
    private final int RoadWidth = 50; 
    
    //Setting up data for stop sign image
    private BufferedImage stopSign = null;
    
    boolean onlyOnce = true;
    int count; 
    private Image bufferImage;
    private Dimension bufferSize;
    
    CityGui gui;
    BusGui bus;

    //List of all guis that we need to animate in the city (Busses, Cars, People...etc) 
    //Will be Added in CityPanel analogous to RestaurantPanel
    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
		addMouseListener(this);
		
        StringBuilder path = new StringBuilder("imgs/");

        
        //Bus Stop 
        try {
			stopSign = ImageIO.read(new File(path.toString() + "stopsign.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Instantiating roads
        lanes = new ArrayList<Lane>();
        
        //Creating Lanes (int xo, int yo, int w, int h, int xv, int yv, boolean ish, Color lc, Color sc)
        Lane l = new Lane( 0, 75, 650, (RoadWidth/2), -5, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 0, 100, 780, (RoadWidth/2), 5, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 0, 350, 780, (RoadWidth/2), -5, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 0, 375, 125, (RoadWidth/2), 5, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 125, 375, 780, (RoadWidth/2), 5, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 125, 125, (RoadWidth/2), 226, 0, 5, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 150, 125, (RoadWidth/2), 226, 0, -5, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 365, 125, (RoadWidth/2), 226, 0, 5, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 390, 125, (RoadWidth/2), 226, 0, -5, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 605, 125, (RoadWidth/2), 226, 0, 5, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 630, 125, (RoadWidth/2), 226, 0, -5, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 650, 75, 780, (RoadWidth/2), -5, 0, true, Color.gray, Color.white );
        lanes.add(l);
        
        javax.swing.Timer t = new javax.swing.Timer( 25, this );
		t.start();
      
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(100, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {

		if(bus != null){
			if(onlyOnce == true){
				onlyOnce = false;
				lanes.get(1).addVehicle(bus);
			}
			
			if(ContactList.stops.size() != 0){
				for(int i=0; i<ContactList.stops.size(); i++){
					if( (bus.getX() == ContactList.stops.get(i).xLocation) 
							&& (bus.getY() == ContactList.stops.get(i).yLocation) ) {
						bus.agent.msgAtBusStop(ContactList.stops.get(i).stopLocation);
					}
				}
			}
			
			if(bus.getX() == 130 && bus.getY() == 105){ 
				lanes.get(1).vehicles.remove(bus); 
				lanes.get(5).addVehicle(bus);
			}
			
			//Bus Stop at 105,105
			
			if(bus.getX() == 130 && bus.getY() == 335){ 
				lanes.get(5).vehicles.remove(bus);
				lanes.get(4).addVehicle(bus);
			}
			
			if(bus.getX() == 635 && bus.getY() == 380){ 
				lanes.get(4).vehicles.remove(bus); 
				lanes.get(10).addVehicle(bus);
			}
			
			if(bus.getX() == 635 && bus.getY() == 130){ 
				lanes.get(10).vehicles.remove(bus);
				lanes.get(0).addVehicle(bus);
			}
			
			if(bus.getX() == 0 && bus.getY() == 80){
				lanes.get(1).addVehicle(bus);
			}
			
		}	
		//Bus stop at 210, 130
		
		
		//Make them all lanes stop
		if ( count % 500 == 0 ) {
			for ( int i=0; i<lanes.size(); i++ ) {
				lanes.get(i).redLight();
			}
		}
		
		if ( count % 1000 == 0 ) {
			for ( int i=0; i<lanes.size(); i++ ) {
				lanes.get(i).greenLight();
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
        
        //drawing lanes
        for ( int i=0; i<lanes.size(); i++ ) {
			Lane l = lanes.get(i);
			l.draw( g2 );
		}
        
        //drawing bus stops 
        g2.drawImage(stopSign, 320, 80, null); 
        g2.drawImage(stopSign, 635, 230, null); 
        g2.drawImage(stopSign, 130, 180, null); 
        g2.drawImage(stopSign, 220 , 380, null);
        g2.drawImage(stopSign, 130, 280, null); 
        g2.drawImage(stopSign, 260 , 80, null); 
        g2.drawImage(stopSign, 130, 230, null);
        g2.drawImage(stopSign, 455, 80, null);
        
        //drawing top houses
        for(int i=0; i<7; i++){
	        Building house = new Building( ( 20 + (i*110) ), TopHouseLocY, "house1.png", "tophouse" + i);
	        buildings.add(house);
	        addBuildingGui(house);
        }
        
        g2.setColor(Color.yellow);
        //Location of doorways 
        g2.fillRect(36, 55, 20, 20);  //house1
        g2.fillRect(146, 55, 20, 20);
        g2.fillRect(256, 55, 20, 20);
        g2.fillRect(366, 55, 20, 20);
        g2.fillRect(476, 55, 20, 20);
        g2.fillRect(586, 55, 20, 20);
        g2.fillRect(696, 55, 20, 20); //house7
        
        
        //drawing bottom houses 
        for(int i=0; i<7; i++){
	        Building house = new Building( ( 20 + (i*110) ), BotHouseLocY, "house2.png", "bothouse" + i);
	        buildings.add(house);
	        addBuildingGui(house);
        }
        
        //Location of doorways
        g2.fillRect(49, 400, 20, 20);  //house1
        g2.fillRect(159, 400, 20, 20);
        g2.fillRect(269, 400, 20, 20);
        g2.fillRect(379, 400, 20, 20);
        g2.fillRect(489, 400, 20, 20);
        g2.fillRect(599, 400, 20, 20);
        g2.fillRect(709, 400, 20, 20); //house7
        
        
        //drawing restaurants 
        Building building = new Building ( 35, 150, "restaurant_right.png", "marcusRestaurant");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(105, 180, 20, 20); //doorway
        
        building = new Building ( 35, 250, "restaurant_right.png", "rest2");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(105, 280, 20, 20); //doorway
        
        building = new Building ( 190, 200, "bank.png", "bank");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(175, 230, 20, 20); //doorway
        
        building = new Building ( 275, 150, "restaurant_right.png", "rest3");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(347, 180, 20, 20); //doorway
        
        building = new Building ( 275, 250, "restaurant_right.png", "rest4");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(347, 280, 20, 20); //doorway
        
        building = new Building ( 425, 200, "market.png", "market");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(415, 215, 20, 20); //doorway
        
        building = new Building ( 520, 200, "restaurant_right.png", "rest5");
        buildings.add(building); 
        addBuildingGui(building);
        
        g2.fillRect(585, 230, 20, 20); //doorway
        
        /*
        //CORNER LOCATION
    	g2.setColor(Color.CYAN);
        g2.fillRect(105, 125, 20, 20);
        g2.fillRect(105, 330, 20, 20);

        g2.fillRect(175, 125, 20, 20);
        g2.fillRect(175, 330, 20, 20);
        
        g2.fillRect(345, 125, 20, 20);
        g2.fillRect(345, 330, 20, 20);
    	
        g2.fillRect(415, 125, 20, 20);
        g2.fillRect(415, 330, 20, 20);
        
        g2.fillRect(585, 125, 20, 20);
        g2.fillRect(585, 330, 20, 20);
        
        g2.fillRect(655, 125, 20, 20);
        g2.fillRect(655, 330, 20, 20);
        */
        

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
                gui.updatePosition();
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
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

    	bus = gui;
    }

    public void addPersonGui(PersonGui gui) {
        guis.add(gui);
    }
    
    public void addBuildingGui(Building gui) { 
    	guis.add(gui);
    }
    
//Unused.
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

 
 
}