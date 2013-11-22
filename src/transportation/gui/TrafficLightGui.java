package transportation.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import mainCity.gui.CityGui;
import mainCity.gui.Gui;
import transportation.TrafficLight;

public class TrafficLightGui implements Gui{ 
	CityGui gui; 
	private TrafficLight trafficLight = null;
	private int xPos, yPos; 
	private static final int w = 20;
	private static final int h = 40; 
	public String TrafficLightColor = "green"; 
	
	public TrafficLightGui(TrafficLight t, CityGui g, int xLoc, int yLoc){ 
		trafficLight = t; 
		this.gui = g; 
		xPos = xLoc; 
		yPos = yLoc;
	}
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.black); 
		g.fillRect(xPos, yPos, w, h); 
		
		//Logic for timers and light switching will go here
	}
	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	} 

}
