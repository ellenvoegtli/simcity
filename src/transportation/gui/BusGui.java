package transportation.gui;

import java.awt.Graphics2D;

import mainCity.gui.CityGui;
import mainCity.gui.Gui;

public class BusGui extends Vehicle {
	
	CityGui gui; 
	
	public int x; 
	public int y;
	private int xLocation;
	private int yLocation; 
	private int width;
	private int height; 
	private int xDestination, yDestination;
	
	public BusGui(int x, int y, int w, int h){ 
		super(x, y, w, h);
		xLocation = x; 
		yLocation = y; 
		width = w; 
		height = h;
	}
	
	public int getXLoc() { 
		return xLocation; 
	}
	
	public int getYLoc() { 
		return yLocation;
	}

}
