package mainCity.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Building implements Gui{
	
	int xLocation, yLocation; 
	int width, height; 
	
    private BufferedImage buildingImg = null;
	
	Building(int xLoc, int yLoc, String p) { 
		
		StringBuilder path = new StringBuilder("imgs/");
		
		try {
        	buildingImg = ImageIO.read(new File(path.toString() + p));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		xLocation = xLoc; 
		yLocation = yLoc; 
	}
	
	public void updatePosition() {
		
	}

	public void draw(Graphics2D g) {
		
		g.drawImage(buildingImg, xLocation, yLocation, null);
		
	}

	public boolean isPresent() {
		return true;
	}

}
