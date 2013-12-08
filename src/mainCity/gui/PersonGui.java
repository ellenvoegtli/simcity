package mainCity.gui;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.contactList.ContactList;
import mainCity.interfaces.PersonGuiInterface;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class PersonGui implements Gui, PersonGuiInterface{
	CityGui gui;
	private String name;
	private PersonAgent agent = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private int xHome, yHome;
	private static final int w = 20;
	private static final int h = 20;
	private boolean isPresent = false;
	private boolean isVisible = true;
	private boolean traveling;
	private boolean inCar = false;
	private BufferedImage personImg = null;
	private ArrayList<Coordinate> corners = new ArrayList<Coordinate>();
	private LinkedList<Coordinate> path = new LinkedList<Coordinate>();
	
	public PersonGui(PersonAgent p, CityGui g) {
		agent = p;
		this.gui = g;
		name = agent.getName();
		
		try {
			xHome = agent.getHomePlace().getXLoc();
			yHome = agent.getHomePlace().getYLoc();
		}
		catch(Exception e) {
			xHome = 710;
			yHome = 400;
		}
		
		xDestination = xPos = 750;
		yDestination = yPos = 80;
		
		traveling  = false;
		StringBuilder path = new StringBuilder("imgs/");
		try {
			personImg = ImageIO.read(new File(path.toString() + "person.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		corners.add(new Coordinate(105, 125));
		corners.add(new Coordinate(105, 330));
		corners.add(new Coordinate(175, 125));
		corners.add(new Coordinate(175, 330));
		corners.add(new Coordinate(345, 125));
		corners.add(new Coordinate(345, 330));
		corners.add(new Coordinate(415, 125));
		corners.add(new Coordinate(415, 330));
		corners.add(new Coordinate(585, 125));
		corners.add(new Coordinate(585, 330));
		corners.add(new Coordinate(655, 125));
		corners.add(new Coordinate(655, 330));
	}

	public PersonGui(PersonAgent p) {
			this.agent = p;
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
		
		if(xPos == xDestination && yPos == yDestination && traveling) {
			if(path.isEmpty()) {
				traveling = false;
				agent.msgAtDestination();
				return;
			}
			
			xDestination = path.peek().x;
			yDestination = path.poll().y;
		}
	}
	
	public void draw(Graphics2D g) {
		if(isVisible) {
			g.setColor(Color.ORANGE);
			g.drawImage(personImg, xPos,yPos, null);
			//g.fillRect(xPos, yPos, w, h);
        	g.drawString(name, xPos, yPos);
		}
	}

	public boolean isPresent() {
		return true;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToLocationOnCar(PersonAgent.CityLocation destination) { 
		if(yPos < )
	}

	@Override
	public void DoGoToLocation(PersonAgent.CityLocation destination) {
		switch(destination) {
			case restaurant_marcus:
				calculatePath(105, 180);
				break;
			case restaurant_ellen:
				calculatePath(105, 280);
				break;
			case restaurant_ena:
				calculatePath(347, 180);
				break;
			case restaurant_jefferson:
				calculatePath(347, 280);
				break;
			case restaurant_david: 
				calculatePath(585, 230);
				break;
			case market:
				calculatePath(415, 215);
				break;
			case market2:
				calculatePath(670, 140);
				break;
			case bank:
				calculatePath(175, 230);
				break;
			case home:
				calculatePath(xHome, yHome);
				break;
			default:
				calculatePath(0, 0);
				break;
		}
		
		if(!path.isEmpty()) {
			xDestination = path.peek().x;
			yDestination = path.poll().y;
		}
	}
	
	@Override
	public void DoGoToStop() {
		System.out.println("Gui is told to go to nearest bus stop");
		
		//Looking for stop that is the minimum distance.
		PersonAgent.CityLocation destination = findNearestStop();
		
		System.out.println("Walking toward " + destination + " bus stop");
		
		switch(destination) {
			case restaurant_marcus:
				calculatePath(105,155);
				break;
			case restaurant_ellen:
				calculatePath(105, 305);
				break;
			case restaurant_david:
				calculatePath(660, 230);
				break;
			case restaurant_ena:
				calculatePath(215, 55);
				break;
			case restaurant_jefferson:
				calculatePath(220, 405);
				break;
			case market:
				calculatePath(440, 55);
				break;
			case bank:
				calculatePath(105, 230);
				break;
			case home:
				calculatePath(320, 55); 
				break;
			default:
				calculatePath(0, 0); 
				break;
		}

		if(!path.isEmpty()) {
			xDestination = path.peek().x;
			yDestination = path.poll().y;
		}		
	}
	
	@Override
	public void DoGoToLocationOnBus(PersonAgent.CityLocation destination) { 
		switch(destination) {
			case restaurant_marcus:
				xDestination = xPos = 105;
				yDestination = yPos = 155;
				break;
			case restaurant_ellen:
				xDestination = xPos = 105;
				yDestination = yPos = 305;
				break;
			case restaurant_ena:
				xDestination = xPos = 215;
				yDestination = yPos = 55;
				break;
			case restaurant_jefferson:
				xDestination = xPos = 220;
				yDestination = yPos = 405;
				break;
			case restaurant_david: 
				xDestination = xPos = 660; 
				yDestination = yPos = 230; 
				break;
			case market:
				xDestination = xPos = 445;
				yDestination = yPos = 55;
				break;
			case bank:
				xDestination = xPos = 105;
				yDestination = yPos = 230;
				break;
			case home:
				xDestination = xPos = xHome;
				yDestination = yPos = yHome;
				break;
			default:
				xDestination = xPos = 0;
				yDestination = yPos = 0;
				break;
		}		
	}
	
	@Override
	public void DoGoInside() {
		isVisible = false;
	}
	
	@Override
	public void DoGoOutside() {
		isVisible = true;
	}
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public CityLocation findNearestStop(){ 
		//starts off with first bus stop
		//measures absolute value of difference in x and y between person's current location and bus stop's location
		//sets destination to the stop
		int distance = Math.abs(xPos - ContactList.stops.get(0).xLocation) + (Math.abs(yPos - ContactList.stops.get(0).yLocation));
		PersonAgent.CityLocation destination = ContactList.stops.get(0).stopLocation;
		
		//goes through list of bus stops to find nearest stop
		for(int i=0; i < ContactList.stops.size(); i++) { 
			int tempdistance = Math.abs(xPos - ContactList.stops.get(i).xLocation) 
								+ (Math.abs(yPos - ContactList.stops.get(i).yLocation)); 
			if(tempdistance <= distance){  
				distance = tempdistance;
				destination = ContactList.stops.get(i).stopLocation;
			}
		}
		return destination;
	}
	
	private void calculatePath(int destX, int destY) {
		Coordinate current = new Coordinate(xPos, yPos);
		Coordinate destination = new Coordinate(destX, destY);
		TreeMap<Integer, Coordinate> nodes = new TreeMap<Integer, Coordinate>();
		
		while(current.x != destination.x || current.y != destination.y) {
			for(int i = 0; i < corners.size(); ++i) {
				nodes.put(getDistance(current, corners.get(i)), corners.get(i));
			}
			
			nodes.put(getDistance(current, destination), destination);
			
			Coordinate node1 = (Coordinate) nodes.pollFirstEntry().getValue();
			Coordinate node2 = (Coordinate) nodes.pollFirstEntry().getValue();
			Coordinate node3 = (Coordinate) nodes.pollFirstEntry().getValue();
			
			if(node1 == destination || node2 == destination || node3 == destination) {
				path.add(destination);
				traveling = true;
				nodes.clear();
				return;
			}
			
			Coordinate pathNext = node1;
			if(getDistance(pathNext, destination) > getDistance(node2, destination)) {
				pathNext = node2;
			}
			if(getDistance(pathNext, destination) > getDistance(node3, destination)) {
				pathNext = node3;
			}
			
			current = pathNext;
			path.add(pathNext);			
			nodes.clear();
		}

		traveling = true;
	}
	
	private int getDistance(Coordinate a, Coordinate b) {
		return ((int) Math.sqrt(Math.pow((Math.abs(a.x - b.x)), 2) + Math.pow((Math.abs(a.y - b.y)), 2)));
	}
	
	public class Coordinate {
		int x;
		int y;
		
		Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}