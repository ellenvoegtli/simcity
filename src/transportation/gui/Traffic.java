package transportation.gui;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Traffic extends JFrame implements ActionListener {
	ArrayList<Lane> lanes;
	int count = 0;
	
	public Traffic() {
		//Create some lanes to meet at an intersection
		lanes = new ArrayList<Lane>();
		Lane l = new Lane( 250, 100, 20, 100, 0, 1, false, Color.gray, Color.black );
		lanes.add( l );
		l = new Lane( 150, 200, 100, 20, 1, 0, true, Color.green, Color.black );
		lanes.add( l );
		l = new Lane( 250, 220, 20, 100, 0, -1, false, Color.blue, Color.black );
		lanes.add( l );
		l = new Lane( 270, 200, 100, 20, -1, 0, true, Color.yellow, Color.black );
		lanes.add( l );
		
		javax.swing.Timer t = new javax.swing.Timer( 25, this );
		t.start();
	}
	
	public void actionPerformed( ActionEvent ae ) {
		count++;
		
		if ( count % 40 == 0 ) {
			Lane l = lanes.get(0);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		
		if ( count % 60 == 0 ) {
			Lane l = lanes.get(1);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		
		if ( count % 80 == 0 ) {
			Lane l = lanes.get(2);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		if ( count % 100 == 0 ) {
			Lane l = lanes.get(3);
			l.addVehicle( new Vehicle( 15, 15, 16, 16) );
		}
		
		//Make them all lanes stop
		if ( count % 500 == 0 ) {
			for ( int i=0; i<lanes.size(); i++ ) {
				lanes.get(i).redLight();
			}
		}
		
		if ( count % 1000 == 0 ) {
			for ( int i=0; i<lanes.size(); i++ ) {
				lanes.get(i).greenLight();
			}
		}
		
		repaint();
	}
	
	public void paint( Graphics g ) {
		Graphics2D g2 = (Graphics2D)g;
		
		for ( int i=0; i<lanes.size(); i++ ) {
			Lane l = lanes.get(i);
			l.draw( g2 );
		}
	}
	public static void main( String[] args ) {
		Traffic t = new Traffic();
		
		t.setVisible( true );
		t.setSize( 500, 500 );

	}
}
