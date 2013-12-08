package transportation.gui;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;


public class Lane {
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
	public List<Vehicle> vehicles;
	
	public Lane(int xo, int yo, int w, int h, int xv, int yv, boolean ish, Color lc, Color sc ) {
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
		if ( isHorizontal ) {
			sides.add( new Line2D.Double( xOrigin, yOrigin, xOrigin+width, yOrigin ) );
			sides.add( new Line2D.Double( xOrigin, yOrigin+height, xOrigin+width, yOrigin+height ) );
		} else {
			sides.add( new Line2D.Double( xOrigin, yOrigin, xOrigin, yOrigin+height ) );
			sides.add( new Line2D.Double( xOrigin+width, yOrigin, xOrigin+width, yOrigin+height ) );
		}
		
		vehicles = Collections.synchronizedList(new ArrayList<Vehicle>());
	}
	
	public void addVehicle( Vehicle v ) {
		//We need to set the proper origin for this new vehicle, given the lane starting geometry constraints
		//The +2 is due to my lanes being 20 pixels "wide" and vehicles being 16 pixels "wide". 
		if ( xVelocity > 0 ) {
			v.setRect( xOrigin+5, yOrigin+5, v.getWidth(), v.getHeight() ); 
		} else if ( yVelocity > 0 ) {
			v.setRect( xOrigin+5, yOrigin+5, v.getWidth(), v.getHeight() ); 
		} else {
			if ( isHorizontal ) {
				v.setRect( xOrigin + width - v.getWidth(), yOrigin+5 , v.getWidth(), v.getHeight() );
			} else {
				v.setRect( xOrigin+5 , yOrigin + height - v.getHeight(), v.getWidth(), v.getHeight() ) ;
			}
		}
		
		vehicles.add( v );
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
				v.move( xVelocity, yVelocity );
			}
			
			double x = v.getX();
			double y = v.getY();

			
			//Remove the vehicle from the list if it is at the end of the lane
			if ( isHorizontal ) {
				//End of lane is xOrigin + width - vehicle width
				double endOfLane = xOrigin + width - v.getWidth();
				if ( xVelocity > 0 && x >= endOfLane ) {
					vehicles.remove(i);					
				} else if ( x <= xOrigin ) {
					vehicles.remove(i);
				}
			} else {
				//End of lane is xOrigin + height - vehicle height
				double endOfLane = yOrigin + height - v.getHeight();
				if ( yVelocity > 0 && y >= endOfLane ) {
					vehicles.remove(i);					
				} else if ( y <= yOrigin ) {
					vehicles.remove(i);
				}
			}
			
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
