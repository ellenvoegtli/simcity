package mainCity.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CityRestaurant implements Gui{
	
	int xLocation, yLocation; 
	int width, height; 
	String Direction; 
	
	private BufferedImage RestImgL = null;
	private BufferedImage RestImgR = null;
	
	CityRestaurant(int xLoc, int yLoc, String d) { 
		
		StringBuilder path = new StringBuilder("imgs/");
		
		try {
			RestImgL = ImageIO.read(new File(path.toString() + "restaurant_left.png"));
			RestImgR = ImageIO.read(new File(path.toString() + "restaurant_right.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		xLocation = xLoc; 
		yLocation = yLoc; 
		Direction = d;
	}
	
	public void updatePosition() {
		
	}

	public void draw(Graphics2D g) {
		if(Direction == "left"){
			g.drawImage(RestImgL, xLocation, yLocation, null);
		}
		else{ 
			g.drawImage(RestImgR, xLocation, yLocation, null);
		}
	}

	public boolean isPresent() {
		return true;
	}

}
