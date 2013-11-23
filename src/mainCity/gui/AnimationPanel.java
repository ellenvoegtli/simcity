package mainCity.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

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
    private final int WINDOWX = 1050;
    private final int WINDOWY = 700;
    
    //Size of House
    private BufferedImage houseImg1 = null;
    private BufferedImage houseImg2 = null;
    private BufferedImage restaurantLeft=null;
    private BufferedImage restaurantRight=null;
    private BufferedImage topGrassImg = null;
    private BufferedImage bankImg = null;
    private BufferedImage marketImg = null;
    
    private final int HouseWidth = 80; 
    private final int HouseLength = 80;
    private final int topHouseY = -4;
    private final int bottomHouseY = 585;
    
    //Size of Road
    private final int RoadWidth = 70; 
    
    
    private Image bufferImage;
    private Dimension bufferSize;

    //List of all guis that we need to animate in the city (Busses, Cars, People...etc) 
    //Will be Added in CityPanel analogous to RestaurantPanel
    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        StringBuilder path = new StringBuilder("imgs/");
        
        try {
			//Size is 80 x 64
        	houseImg1 = ImageIO.read(new File(path.toString() + "house1.png"));
        	houseImg2 = ImageIO.read(new File(path.toString() + "house2.png"));
        	restaurantLeft = ImageIO.read(new File(path.toString() + "restaurant_left.png"));
        	restaurantRight = ImageIO.read(new File(path.toString() + "restaurant_right.png"));
        	topGrassImg = ImageIO.read(new File(path.toString() + "grass.png"));
        	bankImg = ImageIO.read(new File(path.toString() + "bank.png"));
        	marketImg = ImageIO.read(new File(path.toString() + "market.png"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
        
        
        bufferSize = this.getSize();
        
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

    //Draw city objects here (where we drew tables before)
        
        //Roads 
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 80, 1050, RoadWidth);
        g2.fillRect(0, 495, 1050, RoadWidth);
        g2.fillRect(185, 150, RoadWidth, 415);
        g2.fillRect(485, 150, RoadWidth, 415);
        g2.fillRect(785, 150, RoadWidth, 415);
        
        //RoadLines 
        g2.setColor(Color.WHITE); 
        g2.fillRect(0, 110, 1050, 5);
        g2.fillRect(0, 525, 1050, 5);
        g2.fillRect(217, 150, 5, 345);
        g2.fillRect(517, 150, 5, 345);
        g2.fillRect(817, 150, 5, 345);
        
        //Northern Houses
        g.drawImage(topGrassImg, 0,0,null);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 20, topHouseY, null);
        //g2.fillRect(20, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 120, topHouseY, null);
        //g2.fillRect(120, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 220, topHouseY, null);
        //g2.fillRect(220, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 320, topHouseY, null);
        //g2.fillRect(320, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 420, topHouseY, null);
        //g2.fillRect(420, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 520, topHouseY, null);
        //g2.fillRect(520, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 620, topHouseY, null);
        //g2.fillRect(620, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 720, topHouseY, null);
        //g2.fillRect(720, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 820, topHouseY, null);
        //g2.fillRect(820, 0, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg1, 920, topHouseY, null);
        //g2.fillRect(920, 0, HouseWidth, HouseLength);
        
        //Southern Houses
        g2.setColor(Color.red);
        g.drawImage(topGrassImg,0,bottomHouseY,null);
        g.drawImage(houseImg2, 20, bottomHouseY, null);
        //g2.fillRect(20, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 120, bottomHouseY, null);
        //g2.fillRect(120, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 220, bottomHouseY, null);
        //g2.fillRect(220, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 320, bottomHouseY, null);
        //g2.fillRect(320, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 420, bottomHouseY, null);
        //g2.fillRect(420, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 520, bottomHouseY, null);
        //g2.fillRect(520, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 620, bottomHouseY, null);
        //g2.fillRect(620, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 720, bottomHouseY, null);
        //g2.fillRect(720, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 820, bottomHouseY, null);
        //g2.fillRect(820, 565, HouseWidth, HouseLength);
        
        g2.setColor(Color.red);
        g.drawImage(houseImg2, 920, bottomHouseY, null);
        //g2.fillRect(920, 565, HouseWidth, HouseLength);
        
        //Restaurants 
        g2.setColor(Color.BLACK);
        
        g.drawImage(restaurantLeft,105,200,null);
        //g2.fillRect(105, 200, HouseWidth, HouseLength);
        g.drawImage(restaurantLeft,105,350,null);
        //g2.fillRect(105, 350, HouseWidth, HouseLength);
        
        g.drawImage(restaurantRight,405,375,null);
        //g2.fillRect(405, 350, HouseWidth, HouseLength);
        g.drawImage(restaurantRight,405,150,null);
        //g2.fillRect(405, 200, HouseWidth, HouseLength);
        g.drawImage(restaurantLeft,705,275,null);
        //g2.fillRect(705, 275, HouseWidth, HouseLength);
        
        //Bank
        g2.setColor(Color.GREEN);
        g.drawImage(bankImg,255,275,null);
        //g2.fillRect(255, 275, HouseWidth, HouseLength);
        
        //Market 
        g2.setColor(Color.ORANGE);
        g.drawImage(marketImg,555,275,null);
        //g2.fillRect(555, 275, HouseWidth, HouseLength);

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

    public void addHouseGui(CityRestaurant gui) {
        guis.add(gui);
    }
    
    public void addRestGui(CityRestaurant gui) { 
    	guis.add(gui);
    }
 
 
}