package housing.gui;

import housing.OccupantRole;
import housing.personHome;
import housing.Interfaces.LanLordGuiInterface;
import housing.Interfaces.landLord;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class LandlordGui implements Gui, LanLordGuiInterface {

	
	public List<OccupantRole>  Renters;
	private boolean isPresent = true;
	HomeAnimationPanel animation;
	
	HomeGui gui;
	landLord landlord;
	public int xPos;
	private int yPos;
	public int xDestination;
	private int yDestination;
	boolean atDestination = false;
	private enum Command {noCommand, GoFix, DoLeave};
	private Command command=Command.noCommand;
	//private OccupantRole renter;
	
	public LandlordGui(landLord landLord, HomeAnimationPanel h) 
	{
		this.landlord = landLord;
		this.animation = h;
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
		
		if((xDestination != 350 && yDestination != 30) || (xDestination != 450 && yDestination != 30) || (xDestination != 400 && yDestination != 30) || (xDestination != 415 && yDestination != 63) || (xDestination != 50 && yDestination != 55)	)
			{
					atDestination = true;
			}

		if (this.atEndPoint()) 
		{
			command=Command.noCommand;
			if ((xDestination != 350 && yDestination != 30) || (xDestination != 450 && yDestination != 30) || (xDestination != 400 && yDestination != 30) || (xDestination != 415 && yDestination != 63) || (xDestination != 50 && yDestination != 55)	&& atDestination == true)
			{
				atDestination = false;				
				landlord.msgAtDestination();

				
			}
		}
	}

	
	public void draw(Graphics2D g) 
	{
		g.setColor(Color.RED);
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
			xDestination = x;
			yDestination = y;
		}
	
		
		public void DoLeave()
		{
			xDestination = -10;
			yDestination = 40;
		}


		@Override
		public void DoGoToAplliance(String app) {
			// TODO Auto-generated method stub
			
		}

	


}
