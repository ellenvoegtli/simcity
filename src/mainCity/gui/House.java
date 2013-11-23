package mainCity.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class House implements Gui{
	
	int xLocation, yLocation; 
	int width, height; 
	String Direction;
	
	private BufferedImage houseImg1 = null;
	private BufferedImage houseImg2 = null; 
	
	House(int xLoc, int yLoc, String d) { 
		
		StringBuilder path = new StringBuilder("imgs/");
		
		try {
			houseImg1 = ImageIO.read(new File(path.toString() + "house1.png"));
			houseImg2 = ImageIO.read(new File(path.toString() + "house2.png"));
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
		if(Direction == "down"){
			g.drawImage(houseImg1, xLocation, yLocation, null);
		} 
		else{ 
			g.drawImage(houseImg2, xLocation, yLocation, null);
		}
	}

	public boolean isPresent() {
		return true;
	}

}
