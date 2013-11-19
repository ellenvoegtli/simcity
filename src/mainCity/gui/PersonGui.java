package mainCity.gui;

import mainCity.PersonAgent;

import java.awt.*;

public class PersonGui implements Gui{
	CityGui gui;
	private PersonAgent agent = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private static final int w = 20;
	private static final int h = 20;
	private boolean isPresent = false;

	public PersonGui(PersonAgent p, CityGui g) {
		agent = p;
		this.gui = g;
		xDestination = xPos = 100;
		yDestination = yPos = 100;
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
		
		if ((xPos == xDestination) && (yPos == yDestination) 
				& (xDestination == 400) & (yDestination == 400)) {
			agent.msgAtDestination();
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, w, h);
	}

	public boolean isPresent() {
		return true;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToLocation(PersonAgent.CityLocation d) {
		System.out.println("Gui is told to go to " + d);
		
		xDestination = 400;
		yDestination = 400;
	}
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
}