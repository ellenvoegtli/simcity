package housing.gui;

import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.gui.OccupantGui.Command;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.List;

public class LandlordGui implements Gui {

	
	public List<OccupantRole>  Renters;
	private boolean isPresent = false;
	private boolean isHungry = false;

	
	HomeGui gui;
	LandlordRole landlord;
	public int xPos;
	private int yPos;
	public int xDestination;
	private int yDestination;
	boolean atDestination = false;
	private enum Command {noCommand, GoFix, DoLeave};
	private Command command=Command.noCommand;
	//private OccupantRole renter;
	
	public LandlordGui(LandlordRole landLord) 
	{
		this.landlord = landLord;
		xPos = 400;
		yPos = 300;
		xDestination = 400;
		yDestination = 300;
		
	}

	
	public void updatePosition() 
	{
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		if ((xDestination != 200 && yDestination != 40) || (xDestination != 250 && yDestination != 40) || (xDestination != 300 && yDestination != 40) || ( xDestination != 250 && yDestination != 150) || (xDestination != 50 && yDestination !=150) || (xDestination == 70 && yDestination == 100) )
			{
					atDestination = true;
			}

		if (this.atEndPoint()) 
		{
			command=Command.noCommand;
			if (((xDestination == 200 && yDestination == 40) || (xDestination == 250 && yDestination == 40) || (xDestination == 300 && yDestination == 40) || ( xDestination == 250 && yDestination == 150) || (xDestination == 50 && yDestination ==150) || (xDestination == 70 && yDestination == 100) ) && atDestination == true)
			{
				atDestination = false;				
				landlord.msgAtDestination();

				
			}
		}
	}

	
	public void draw(Graphics2D g) 
	{
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 10, 10);
		g.setColor(Color.BLACK);
		
	}

	public boolean isPresent() 
	{
		return isPresent;
	}
	public void setPresent(boolean p) 
	{
		isPresent = p;
	}

	public void setHungry() {
		isHungry = true;
		person.gotHungry();
		setPresent(true);
		xDestination = xPos;
		yDestination = yPos;
	}
	public boolean isHungry() 
	{
		return isHungry;
	}
	
	  public int getXPos() {
	        return xPos;
	    }

	    public int getYPos() {
	        return yPos;
	    
	    }
	    
	    public boolean atEndPoint()
	    {
	    	return xPos == xDestination && yPos == yDestination;
	    }
		
		public void DoGoToAppliance(int x, int y)
		{
			xDestination = 70;
			yDestination = 100;
		}
	
		
		public void DoLeave()
		{
			xDestination = -10;
			yDestination = 180;
		}

		public void DoGoBackHome() 
		{
			// TODO Auto-generated method stub
			
		}

		public void DoGoToRenterHome(personHome home) {
			// TODO Auto-generated method stub
			
		}

		
		
	
	




}
