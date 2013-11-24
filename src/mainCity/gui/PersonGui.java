package mainCity.gui;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.contactList.ContactList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PersonGui implements Gui{
	CityGui gui;
	private PersonAgent agent = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private static final int w = 20;
	private static final int h = 20;
	private boolean isPresent = false;
	private boolean isVisible = true;
	private boolean traveling;
	private BufferedImage personImg = null;

	public PersonGui(PersonAgent p, CityGui g) {
		agent = p;
		this.gui = g;
		xDestination = xPos = (int)(Math.random() * 700);
		yDestination = yPos = (int)(Math.random() * 400);
		traveling = false;
		StringBuilder path = new StringBuilder("imgs/");
		try {
			personImg = ImageIO.read(new File(path.toString() + "person.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		if ((xPos == xDestination) && (yPos == yDestination) && traveling) {
			traveling = false;
			agent.msgAtDestination();
			//isVisible = false;
		}
	}
	
	public void draw(Graphics2D g) {
		if(isVisible) {
			g.setColor(Color.ORANGE);
			g.drawImage(personImg, xPos,yPos, null);
			//g.fillRect(xPos, yPos, w, h);
		}
	}

	public boolean isPresent() {
		return true;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToLocation(PersonAgent.CityLocation destination) {		
		switch(destination) {
			case restaurant_marcus:
				xDestination = 105;
				yDestination = 180;
				break;
			case restaurant_ellen:
				xDestination = 105;
				yDestination = 280;
				break;
			case restaurant_ena:
				xDestination = 347;
				yDestination = 180;
				break;
			case restaurant_jefferson:
				xDestination =  347;
				yDestination = 280;
				break;
			case restaurant_david: 
				xDestination = 585; 
				yDestination = 230; 
				break;
			case market:
				xDestination = 415;
				yDestination = 215;
				break;
			case bank:
				xDestination = 175;
				yDestination = 230;
				break;
			case home:
				xDestination = 100;
				yDestination = 500;
				break;
			default:
				xDestination = 0;
				yDestination = 0;
				break;
		}

		traveling = true;
	}
	
	public void DoGoToStop() {
		System.out.println("Gui is told to go to nearest bus stop");

		//Looking for stop that is the minimum distance.
		PersonAgent.CityLocation destination = findNearestStop();
		
		System.out.println("Walking toward " + destination);
		
		switch(destination) {
		case restaurant_marcus:
			xDestination = 500;
			yDestination = 300;
			
			break;
		case restaurant_ellen:
			xDestination = 300;
			yDestination = 400;
			break;
		case restaurant_david:
			xDestination = 300;
			yDestination = 400;
			break;
		case restaurant_ena:
			xDestination = 300;
			yDestination = 400;
			break;
		case restaurant_jefferson:
			xDestination = 300;
			yDestination = 400;
			break;
		case market:
			xDestination = 400;
			yDestination = 400;
			break;
		case bank:
			xDestination = 200;
			yDestination = 100;
			break;
		case home:
			xDestination = 100;
			yDestination = 500;
			break;
		default:
			xDestination = 0;
			yDestination = 0;
			break;
	}

		traveling = true;
	}
	
	public void DoGoInside() {
		isVisible = false;
	}
	
	public void DoGoOutside() {
		isVisible = true;
	}
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
	
	public CityLocation findNearestStop(){ 
		
		//starts off with first bus stop
		//measures absolute value of difference in x and y between person's current location and bus stop's location
		//sets destination to the stop
		int distance = Math.abs(xPos - ContactList.stops.get(0).xLocation) + (Math.abs(yPos - ContactList.stops.get(0).yLocation));
		PersonAgent.CityLocation destination = ContactList.stops.get(0).stopLocation;
		
		//goes through list of bus stops to find nearest stop
		for(int i=1; i<ContactList.stops.size(); i++) { 
			int tempdistance = Math.abs(xPos - ContactList.stops.get(i).xLocation) 
								+ (Math.abs(yPos - ContactList.stops.get(i).yLocation)); 
			if(tempdistance < distance){ 
				destination = ContactList.stops.get(i).stopLocation;
			}
		}
		
		return destination;
	}
}