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

import transportation.gui.*;

public class PersonGui implements Gui, PersonGuiInterface {
	CityGui gui;
	Rectangle rectangle = new Rectangle();
	AnimationPanel animation; 
	private String name;
	private PersonAgent agent = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	Coordinate destination; 
	private int xHome, yHome;
	private int xRenter, yRenter;
	private static final int w = 20;
	private static final int h = 20;
	private boolean isPresent = false;
	private boolean isVisible = true;
	private boolean traveling;
	private boolean CarFinished = false;
	private boolean inCar = false;
	private BufferedImage personImg = null;
	private ArrayList<Coordinate> corners = new ArrayList<Coordinate>();
	private LinkedList<Coordinate> path = new LinkedList<Coordinate>();
	private ArrayList<Coordinate> roads = new ArrayList<Coordinate>(); 
	
	public CarGui myCar; 
	
	public PersonGui(PersonAgent p, CityGui g) {
		agent = p;
		this.gui = g;
		name = agent.getName();
		
		try {
			xHome = agent.getHomePlace().getXLoc();
			yHome = agent.getHomePlace().getYLoc()+10;
			
		}
		catch(Exception e) {
			xHome = 710;
			yHome = 400;
		}
		
		xDestination = xPos = 750;
		yDestination = yPos = 80;
		rectangle.setLocation(xPos, yPos);
		rectangle.setSize(14, 19);
		
		myCar = new CarGui(p, 750, 80, 16, 16);
		
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
		
		roads.add(new Coordinate( 181, 105));
		roads.add(new Coordinate( 155, 330));
		roads.add(new Coordinate( 585, 355));
		roads.add(new Coordinate( 610, 131));
	}

	public PersonGui(PersonAgent p) {
		this.agent = p;
	}
	
	public Rectangle getRectangle() { 
		return rectangle;
	}
	
	public PersonAgent getPerson() { 
		return agent;
	}

	public void updatePosition() {
		
		rectangle.setLocation(xPos, yPos);
		
		if(inCar){
			if (xPos < xDestination)
				xPos+=5;
			else if (xPos > xDestination)
				xPos-=5;
	
			else if (yPos < yDestination)
				yPos+=5;
			else if (yPos > yDestination)
				yPos-=5;
		}
			
		else{
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
	
			else if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
		}
		
		if(xPos == xDestination && yPos == yDestination && traveling && (inCar!=true) ) {
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
	

	public void DoGoToLocation(PersonAgent.CityLocation destination) {
		switch(destination) {
			case restaurant_marcus:
				calculatePath(110, 180);
				break;
			case restaurant_ellen:
				calculatePath(110, 280);
				break;
			case restaurant_ena:
				calculatePath(347, 180);
				break;
			case restaurant_jefferson:
				calculatePath(347, 280);
				break;
			case restaurant_david: 
				calculatePath(592, 230);
				break;
			case market:
				calculatePath(415, 215);
				break;
			case market2:
				calculatePath(660, 153);
				break;
			case bank:
				calculatePath(175, 230);
				break;
			case bank2:
				calculatePath(660, 285);
				break;
			case home:
				calculatePath(xHome, yHome);
				break;
			case renterHome:
				
				xRenter = agent.renterHome.getXLoc();
				yRenter = agent.renterHome.getYLoc();
				calculatePath(xRenter, yRenter);
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

	public void DoGetOnRoad() { 
		System.out.println("Gui is told to go to nearest road");
		destination = findNearestRoad();
		calculatePath(destination.x, destination.y); 
		if(!path.isEmpty()) {
			xDestination = destination.x; 
			yDestination = destination.y;
		}
	}
	
	public void AddCarToLane() { 
		if(xPos == 181 && yPos == 105) {
			if(animation.lanes.get(1).vehicles.isEmpty()){
				animation.Cars.add(myCar);
				animation.lanes.get(4).addVehicle(myCar);
			}
		}
		
		else if(xPos == 155 && yPos == 330) { 
			animation.Cars.add(myCar);
			animation.lanes.get(13).addVehicle(myCar);
		}
	
		else if(xPos == 585 && yPos == 355) { 
			animation.Cars.add(myCar);
			animation.lanes.get(7).addVehicle(myCar);
		}

		else if(xPos == 610 && yPos == 131) { 
			animation.Cars.add(myCar);
			animation.lanes.get(16).addVehicle(myCar);
		}
	}
	
	public void DoGoToLocationOnCar(PersonAgent.CityLocation destination) { 
		
		switch(destination) {
			case restaurant_marcus:
				xDestination = xPos = 155;
				yDestination = yPos = 155;
				break;
			case restaurant_ellen:
				xDestination = xPos = 155;
				yDestination = yPos = 305;
				break;
			case restaurant_ena:
				xDestination = xPos = 347;
				yDestination = yPos = 105;
				break;
			case restaurant_jefferson:
				xDestination = xPos = 345;
				yDestination = yPos = 355;
				break;
			case restaurant_david: 
				xDestination = xPos = 610; 
				yDestination = yPos = 230; 
				break;
			case market:
				xDestination = xPos = 440;
				yDestination = yPos = 105;
				break;
			case market2:
				xDestination = xPos = 610;
				yDestination = yPos = 140;
				break;
			case bank:
				xDestination = xPos = 155;
				yDestination = yPos = 230;
				break;
			case bank2:
				xDestination = xPos = 610;
				yDestination = yPos = 285;
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
			case market2:
				calculatePath(660, 155);
				break;
			case bank:
				calculatePath(105, 230);
				break;
			case bank2:
				calculatePath(660, 305);
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
			case market2:
				xDestination = xPos = 660;
				yDestination = yPos = 230;
				break;
			case bank:
				xDestination = xPos = 105;
				yDestination = yPos = 230;
				break;
			case bank2:
				xDestination = xPos = 660;
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
	
	public void DoGoInside() {
		isVisible = false;
	}
	
	public void DoGoOutside() {
		isVisible = true;
	}
	
	public void DoDie() {
		isVisible = false;
		xDestination = xPos = xHome;
		yDestination = yPos = yHome;
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
	
	public Coordinate findNearestRoad() { 
		Coordinate destination = new Coordinate(0,0); 
		int distance = Math.abs(xPos - roads.get(0).x) + Math.abs(yPos - roads.get(0).y);
		destination = roads.get(0); 
		
		for(int i=0; i<roads.size(); i++) { 
			int tempdistance = Math.abs(xPos - roads.get(i).x) + Math.abs(yPos - roads.get(i).y);
			if(tempdistance <= distance){ 
				distance = tempdistance; 
				destination = roads.get(i);
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
				if(!path.contains(corners.get(i)))
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

			Coordinate pathNext = node1;//(current.x != node1.x && current.y != node1.y) ? node1 : node2;
			if(getDistance(pathNext, destination) > getDistance(node2, destination)) {
				pathNext = node2;
			}
			else if(getDistance(pathNext, destination) > getDistance(node3, destination)) {
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
	
	public void getInCar(){ 
		inCar = true; 
	}
	
	public void getOutOfCar() { 
		inCar = false;
	}
	
	public void setAnimationPanel(AnimationPanel a) { 
		animation = a;
	}
	
	public void setCarFinished(boolean b) { 
		CarFinished = b;
	}
}