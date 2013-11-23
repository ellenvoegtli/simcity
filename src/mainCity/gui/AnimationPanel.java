package mainCity.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import transportation.gui.Lane;
import transportation.gui.Vehicle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

	//Dimensions for the window that will have the City in it
    private final int WINDOWX = 780;
    private final int WINDOWY = 480;
    
    //House data (house sprites are 80x64)
    private final int TopHouseLocY = -4;
    private final int BotHouseLocY = 416;
    
    //Road Data
    ArrayList<Lane> lanes;
    private final int RoadWidth = 50; 
    
    int count; 
    private Image bufferImage;
    private Dimension bufferSize;

    //List of all guis that we need to animate in the city (Busses, Cars, People...etc) 
    //Will be Added in CityPanel analogous to RestaurantPanel
    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        //Instantiating roads
        lanes = new ArrayList<Lane>();
        
        //Creating Lanes (int xo, int yo, int w, int h, int xv, int yv, boolean ish, Color lc, Color sc)
        Lane l = new Lane( 0, 75, 780, (RoadWidth/2), 1, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 0, 100, 780, (RoadWidth/2), -1, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 0, 350, 780, (RoadWidth/2), 1, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 0, 375, 780, (RoadWidth/2), -1, 0, true, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 125, 125, (RoadWidth/2), 226, 0, -1, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 150, 125, (RoadWidth/2), 226, 0, 1, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 365, 125, (RoadWidth/2), 226, 0, -1, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 390, 125, (RoadWidth/2), 226, 0, 1, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 605, 125, (RoadWidth/2), 226, 0, -1, false, Color.gray, Color.white );
        lanes.add(l);
        l = new Lane( 630, 125, (RoadWidth/2), 226, 0, 1, false, Color.gray, Color.white );
        lanes.add(l);
        
        javax.swing.Timer t = new javax.swing.Timer( 25, this );
		t.start();
      
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		count++;
		
		if ( count % 40 == 0 ) {
			Lane l = lanes.get(0);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		
		if ( count % 60 == 0 ) {
			Lane l = lanes.get(1);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		
		if ( count % 80 == 0 ) {
			Lane l = lanes.get(2);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		if ( count % 100 == 0 ) {
			Lane l = lanes.get(3);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		
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
        
        //drawing top houses
        for(int i=0; i<7; i++){
	        Building house = new Building( ( 20 + (i*110) ), TopHouseLocY, "house1.png");
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
	        Building house = new Building( ( 20 + (i*110) ), BotHouseLocY, "house2.png");
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
        Building building = new Building ( 35, 150, "restaurant_right.png");
        addBuildingGui(building);
        
        g2.fillRect(105, 180, 20, 20); //doorway
        
        building = new Building ( 35, 250, "restaurant_right.png");
        addBuildingGui(building);
        
        g2.fillRect(105, 280, 20, 20); //doorway
        
        building = new Building ( 190, 200, "bank.png");
        addBuildingGui(building);
        
        g2.fillRect(175, 230, 20, 20); //doorway
        
        building = new Building ( 275, 150, "restaurant_right.png");
        addBuildingGui(building);
        
        g2.fillRect(347, 180, 20, 20); //doorway
        
        building = new Building ( 275, 250, "restaurant_right.png");
        addBuildingGui(building);
        
        g2.fillRect(347, 280, 20, 20); //doorway
        
        building = new Building ( 425, 200, "market.png");
        addBuildingGui(building);
        
        g2.fillRect(415, 215, 20, 20);
        
        building = new Building ( 520, 200, "restaurant_right.png");
        addBuildingGui(building);
        
        g2.fillRect(585, 230, 20, 20);
        

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addPersonGui(PersonGui gui) {
        guis.add(gui);
    }
    
    public void addBuildingGui(Building gui) { 
    	guis.add(gui);
    }

 
 
}