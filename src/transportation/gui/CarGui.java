package transportation.gui;

import java.awt.Graphics2D;

import transportation.BusAgent;
import mainCity.PersonAgent;
import mainCity.gui.CityGui;
import mainCity.gui.Gui;

public class CarGui extends Vehicle {
	
	public int x; 
	public int y;
	private int xLocation;
	private int yLocation; 
	private int width;
	private int height; 
	private int xDestination, yDestination;
	public PersonAgent owner; 
	
	public CarGui(PersonAgent p, int x, int y, int w, int h){ 
		super(x, y, w, h, false);
		xLocation = x; 
		yLocation = y; 
		width = w; 
		height = h;
		owner = p;
	}
	
	public int getXLoc() { 
		return xLocation; 
	}
	
	public int getYLoc() { 
		return yLocation;
	}
	
	public void setXDest(int xD) { 
		xDestination = xD; 
	}
	
	public void setYDest(int yD) { 
		yDestination = yD; 
	}
	
	public int getXDest() { 
		return xDestination; 
	}
	
	public int getYDest() { 
		return yDestination; 
	}

}
