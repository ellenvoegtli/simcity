package transportation.gui;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;


public class Intersection {
	Rectangle2D.Double rectangle;
	ArrayList<Line2D.Double> sides;
	int xVelocity;
	int yVelocity;
	boolean redLight;
	int xOrigin;
	int yOrigin;
	int width;
	int height;
	boolean isHorizontal;
	boolean startAtOrigin;
	Color laneColor;
	Color sideColor;
	public ArrayList<Vehicle> vehicles;
	private final int RoadWidth = 50;
	
	public Intersection(int xo, int yo, int w, int h, int xv, int yv, boolean ish, Color lc, Color sc ) {
		redLight = false;
		width = w;
		height = h;
		xVelocity = xv;
		yVelocity = yv;
		xOrigin = xo;
		yOrigin = yo;
		isHorizontal = ish;
		laneColor = lc;
		sideColor = sc;
		
		//Make the lane surface
		rectangle = new Rectangle2D.Double( xOrigin, yOrigin, width, height );
		
		//Make the edges to the lane surface
		sides = new ArrayList<Line2D.Double>();
		sides.add( new Line2D.Double( xOrigin, yOrigin, xOrigin+width, yOrigin ) );
		sides.add( new Line2D.Double( xOrigin, yOrigin+height, xOrigin+width, yOrigin+height ) );
		sides.add( new Line2D.Double( xOrigin, yOrigin, xOrigin, yOrigin+height ) );
		sides.add( new Line2D.Double( xOrigin+width, yOrigin, xOrigin+width, yOrigin+height ) );
		
		vehicles = new ArrayList<Vehicle>();
	}
	
	public void addVehicle( Vehicle v ) {
		
		//Entering from the bottom left horizontally
		if( (v.getY() > (yOrigin + (RoadWidth/2))) && (v.getY() < (yOrigin + RoadWidth)) ){ 
			v.setRect( xOrigin, (yOrigin + (RoadWidth/2)+5), v.getWidth(), v.getHeight() );
			v.setDirection(Vehicle.Direction.Right);
		}
		
		//Entering from top right horizontally
		else if( v.getY() > yOrigin && v.getY() < (yOrigin + (RoadWidth/2)) ) {
			v.setRect( (xOrigin + (RoadWidth/2))+10, yOrigin+5, v.getWidth(), v.getHeight() );
			v.setDirection(Vehicle.Direction.Left);
		}
		
		//Entering from top left vertically 
		else if( (v.getX() > xOrigin) && (v.getX() < (xOrigin + (RoadWidth/2))) ) { 
			v.setRect( xOrigin+5, (yOrigin - (RoadWidth/2))+5, v.getWidth(), v.getHeight() );
			v.setDirection(Vehicle.Direction.Down);
		}
		
		//Entering from the bottom right vertically
		else if( (v.getX() > (xOrigin + (RoadWidth/2))) && (v.getX() < (xOrigin + RoadWidth)) ) { 
			v.setRect( (xOrigin + (RoadWidth/2))+5, (yOrigin + (RoadWidth/2))+5, v.getWidth(), v.getHeight() );
			v.setDirection(Vehicle.Direction.Up);
		}
		
		vehicles.add(v);
	}
	
	public void draw( Graphics2D g2 ) {
		g2.setColor( laneColor );
		g2.fill( rectangle );
		
		for ( int i=0; i<sides.size(); i++ ) {
			g2.setColor( sideColor );
			g2.draw( sides.get(i) );
		}
		
		for ( int i=vehicles.size()-1; i >= 0; i-- ) {
			Vehicle v = vehicles.get(i);
			if ( !redLight && !v.atBusStop ) {
				
				if(v.myDirection == Vehicle.Direction.Right){
					v.move( xVelocity, 0 );
					if(v.getY() < 150) { 
						if(v.getX() > (xOrigin)) {
							v.myDirection = Vehicle.Direction.SimpleDown; 
						}
					}
					
					else if(v.getY() >= 150) { 
						if(v.getX() > (xOrigin + (RoadWidth/2))){ 
							v.myDirection = Vehicle.Direction.SimpleUp; 
						}
					}
				}
				
				else if(v.myDirection == Vehicle.Direction.Left){
					v.move( -xVelocity, 0 );
					if(v.getY() < 150) { 
						if(v.getX() < (xOrigin+10)) { 
							v.myDirection = Vehicle.Direction.SimpleDown; 
						}
					}
					else if (v.getY() >= 150) { 
						if(v.getX() < (xOrigin + (RoadWidth/2)+10)){ 
							v.myDirection = Vehicle.Direction.SimpleUp; 
						}
					}
				}
				
				else if(v.myDirection == Vehicle.Direction.Down){
					v.move( 0, yVelocity );
					if(v.getX() < 200) { 
						if(v.getY() > (yOrigin + (RoadWidth/2))){ 
							v.myDirection = Vehicle.Direction.SimpleRight; 
						}
					}
					else if (v.getX() >= 200) { 
						if(v.getY() > (yOrigin )){ 
							v.myDirection = Vehicle.Direction.SimpleLeft;
						}
					}
				}
				
				else if(v.myDirection == Vehicle.Direction.Up){
					v.move( 0, -yVelocity );
					if(v.getX() > 200) { 
						if(v.getY() < (yOrigin + +10)) { 
							v.myDirection = Vehicle.Direction.SimpleLeft; 
						}
					}
					else if (v.getX() < 200) { 
						if(v.getY() == 100){ 
							v.myDirection = Vehicle.Direction.SimpleRight;
						}
					}
				}
				
				else if(v.myDirection == Vehicle.Direction.SimpleLeft) { 
					v.move( -xVelocity, 0 );
				}
				
				else if(v.myDirection == Vehicle.Direction.SimpleRight) { 
					v.move( xVelocity, 0 );			
				}
								
				else if(v.myDirection == Vehicle.Direction.SimpleUp) { 
					v.move( 0, -yVelocity );
				}
								
				else if(v.myDirection == Vehicle.Direction.SimpleDown) { 
					v.move( 0, yVelocity );
				}
				else { 
					System.out.println("Intersection logic error: Never supposed to reach here.");
				}
			}
			
			double x = v.getX();
			double y = v.getY();

		}
		
		for ( int i=0; i<vehicles.size(); i++ ) {
			Vehicle v = vehicles.get(i);
			g2.setColor( v.getColor() );
			g2.fill( v );
		}
	}
	
	public void redLight() {
		redLight = true;
	}
	
	public void greenLight() {
		redLight = false;
	}
	

}
