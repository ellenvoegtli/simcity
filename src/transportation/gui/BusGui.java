package transportation.gui;

import java.awt.Graphics2D;

import transportation.BusAgent;
import mainCity.gui.CityGui;
import mainCity.gui.Gui;

public class BusGui extends Vehicle {

	public BusAgent agent = null;
	CityGui gui; 
	
	public int x; 
	public int y;
	private int xLocation;
	private int yLocation; 
	private int width;
	private int height; 
	private int xDestination, yDestination;
	
	public BusGui(int x, int y, int w, int h, BusAgent b){ 
		super(x, y, w, h, true);
		xLocation = x; 
		yLocation = y; 
		width = w; 
		height = h;
		agent = b;
	}
	
	public int getXLoc() { 
		return xLocation; 
	}
	
	public int getYLoc() { 
		return yLocation;
	}

}
