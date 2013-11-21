package mainCity.gui;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;

import java.awt.*;

public class PersonGui implements Gui{
	CityGui gui;
	private PersonAgent agent = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private static final int w = 20;
	private static final int h = 20;
	private boolean isPresent = false;
	private boolean traveling;

	public PersonGui(PersonAgent p, CityGui g) {
		agent = p;
		this.gui = g;
		xDestination = xPos = 100;
		yDestination = yPos = 100;
		traveling = false;
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
			//hide self from GUI
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.ORANGE);
		g.fillRect(xPos, yPos, w, h);
	}

	public boolean isPresent() {
		return true;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToLocation(PersonAgent.CityLocation destination) {
		System.out.println("Gui is told to go to " + destination);
		
		switch(destination) {
			case restaurant_marcus:
				xDestination = 500;
				yDestination = 300;
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
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
}