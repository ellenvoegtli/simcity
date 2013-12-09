package transportation.gui;
import java.awt.*;
import java.awt.geom.*;


public class Vehicle extends Rectangle2D.Double {
	Color vehicleColor;
	public boolean atBusStop;
	public int counter;
	boolean isBus; 
	
	public enum Direction 
	{Up, Down, Left, Right, SimpleUp, SimpleDown, SimpleLeft, SimpleRight}; 
	public Direction myDirection; 
	
	public Vehicle( int x, int y, int width, int height, boolean iB ) {
		super( x, y, width, height );
		isBus = iB; 
		if(isBus) 
			vehicleColor = Color.yellow; 
		else 
			vehicleColor = Color.blue;
			
		atBusStop = false;
	}
	public void setLocation( int x, int y ) {
		setRect( x, y, getWidth(), getHeight() );
	}
	
	public Color getColor() {
		return vehicleColor;
	}
	
	public void move( int xv, int yv ) {
		setRect( x+xv, y+yv, getWidth(), getHeight() );
	}
	
	public void stop() { 
		atBusStop = true; 
	}
	
	public void go() { 
		atBusStop = false;
	}
	
	public void setDirection(Direction d) { 
		myDirection = d; 
	}
}
