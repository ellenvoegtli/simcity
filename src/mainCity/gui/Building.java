package mainCity.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Building implements Gui{
	
	Rectangle rectangle = new Rectangle();
	int xLocation, yLocation; 
	int width, height; 
	String ID; 
	
    private BufferedImage buildingImg = null;
	
	Building(int xLoc, int yLoc, String p, String id) { 
		
		StringBuilder path = new StringBuilder("imgs/");
		
		try {
        	buildingImg = ImageIO.read(new File(path.toString() + p));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		xLocation = xLoc; 
		yLocation = yLoc; 
		rectangle.setLocation(xLoc, yLoc);

		
		if(p.contains("house1")){ 
			rectangle.setSize(80, 64);
		}
		if(p.contains("house2")){ 
			rectangle.setSize(80, 93);
		}
		if(p.contains("restaurant")){ 
			rectangle.setSize(79, 79);
		}
		if(p.contains("bank")){ 
			rectangle.setSize(59, 81);
		}
		if(p.contains("market")){ 
			rectangle.setSize(71, 80);
		}
		
		ID = id; 
	}
	
	public void updatePosition() {
		
	}

	public void draw(Graphics2D g) {
		
		g.drawImage(buildingImg, xLocation, yLocation, null);
		
	}

	public boolean isPresent() {
		return true;
	}

	public boolean contains(int x, int y) {
		return rectangle.contains(x, y);
	}
	
	public int getXLoc()
	{
		
		return xLocation;
		
	}
	public int getYLoc()
	{
		
		return yLocation;
	}

	public void setXRenterHome(int renterX) {
		this.xLocation = renterX;
		
	}

	public void setYRenterHome(int renterY) {
		this.yLocation = renterY;
		
	}

}
