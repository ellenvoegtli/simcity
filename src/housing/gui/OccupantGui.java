package housing.gui;

import housing.OccupantRole;

import java.awt.Color;
import java.awt.Graphics2D;



public class OccupantGui implements Gui
{

	
	private OccupantRole person = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	
	HomeGui gui;
	
	public int xPos;
	private int yPos;
	public int xDestination;
	private int yDestination;
	boolean atDestination = false;
	private enum Command {noCommand, GoRest, GoCook, doneInKitchen};
	private Command command=Command.noCommand;
	
	
	
	public OccupantGui(OccupantRole occupant, HomeGui gui) 
	{
		person = occupant;
		xPos = 0;
		yPos = 35;
		xDestination = 40;
		yDestination = 35;
		this.gui = gui;
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
		
		if ((xDestination != 200 && yDestination != 40) || (xDestination != 250 && yDestination != 40) || (xDestination != 300 && yDestination != 40) || ( xDestination != 250 && yDestination != 150) || (xDestination != 50 && yDestination !=150) || (xDestination == 70 && yDestination == 100) ||
				(xDestination != 350 && yDestination != 30) || (xDestination != 450 && yDestination != 30) || (xDestination != 400 && yDestination != 30) || (xDestination != 415 && yDestination != 63) || (xDestination != 50 && yDestination != 55)	)
			{
					atDestination = true;
			}

		if (this.atEndPoint()) 
		{
			if (command==Command.doneInKitchen)
			{
				isHungry = false;
				gui.setCustomerEnabled(person);
			}
			command=Command.noCommand;
			if (((xDestination == 200 && yDestination == 40) || (xDestination == 250 && yDestination == 40) || (xDestination == 300 && yDestination == 40) || ( xDestination == 250 && yDestination == 150) || (xDestination == 50 && yDestination ==150) || (xDestination == 70 && yDestination == 100) ) 
					|| (xDestination != 350 && yDestination != 30) || (xDestination != 450 && yDestination != 30) || (xDestination != 400 && yDestination != 30) || (xDestination != 415 && yDestination != 63) || (xDestination != 50 && yDestination != 55) 
																																								&& atDestination == true)
			{
				atDestination = false;				
				person.msgAtDestination();

				
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
			xDestination = x;
			yDestination = y;
		}
		
		public void DoGoToFridge()
		{
			System.out.println("going to fridge");
			xDestination = 300;
			yDestination = 40;
			//command = Command.GoCook;
		}
		
		public void DoGoToFridgeA()
		{
			xDestination = 450;
			yDestination = 30;
		}
		
		public void DoGoToStove()
		{
			//System.out.println("cooking at stove");
			xDestination = 200;
			yDestination = 40;
			//command = Command.GoCook;
		}
		
		public void DoGoToStoveA()
		{
			xDestination = 350;
			yDestination = 30;
		}
		
		
		
		public void DoGoToSink()
		{
			//System.out.println("doing dishes");
			xDestination = 250;
			yDestination = 40;
			command = Command.doneInKitchen;
		}
		
		public void DoGoToSinkA()
		{
			xDestination = 400;
			yDestination = 30;
		}
		
		public void DoGoToKitchenTable() 
		{
			//System.out.println("going to table to eat");
			xDestination = 250;
			yDestination = 150;
		}
		
		public void DoGoToKitchenTableA()
		{
			xDestination = 415;
			yDestination = 63;
		}
		
		public void DoGoRest()
		{
			xDestination = 50;
			yDestination = 200;
		}
		
		public void DoGoRestA()
		{
			xDestination = 50;
			yDestination = 55;
		}
		
		public void DoLeave()
		{
			xDestination = -10;
			yDestination = 45;
		}
		
		

}
