package mainCity.restaurants.restaurant_zhangdt.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import role.davidRestaurant.*;

public class CookGui implements Gui{

	private DavidCookRole agent = null;
	
	private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position 
    private int FridgeX = 0, FridgeY = 200; 
    private int GrillX, GrillY; 
    private int PlatingX, PlatingY;
    
    public CookGui(DavidCookRole agent, int xStart, int yStart, int xStartDest, int yStartDest) {
        this.agent = agent;
        xPos = xStart; 
        yPos = yStart; 
        xDestination = xStartDest; 
        yDestination = yStartDest;
    }
	
	public void updatePosition() {
		// TODO Auto-generated method stub
		if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
        if (xPos == xDestination && yPos == yDestination 
        		& (xDestination == GrillX) & (yDestination == GrillY)) {
        	agent.msgFinishedMovingToGrill();
        }
	}

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.CYAN);
		g.fillRect(20, 200, 20, 20);
	}

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void DoMoveToGrill(){ 
		xDestination = 20; 
        yDestination = 215;
	}
	
	public void DoMoveToPlating(){ 
		xDestination = 20; 
        yDestination = 165;
	}
	
	public void DoMoveToFridge(){ 
		xDestination = 0; 
        yDestination = 170;
	}

}
