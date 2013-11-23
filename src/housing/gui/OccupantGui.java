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
	private enum Command {noCommand, GoRest, Fix, GoCook};
	private Command command=Command.noCommand;
	
	
	
	public OccupantGui(OccupantRole occupant, HomeGui gui) 
	{
		person = occupant;
		xPos = 175;
		yPos = 150;
		xDestination = 175 ;
		yDestination = 150;
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

		if (xPos == xDestination && yPos == yDestination) 
		{
			//if (command==Command.GoRest) person.msgAnimationFinishedGoToRest();
			//else if (command==Command.GoCook) {
				//person.msgAnimationFinishedGoCook();
				isHungry = false;
				gui.setCustomerEnabled(person);
			//}
			command=Command.noCommand;
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


		public void DoGoToStove() 
		{
			System.out.println("cooking at stove");
			xDestination = 200;
			yDestination = 35;
			command = Command.GoCook;
		}


		public void DoGoToKitchenTable() 
		{
			System.out.println("going to table to eat");
			xDestination = 200;
			yDestination = 150;
		}
		
		public void DoGoRest()
		{
			xDestination = 50;
			yDestination = 150;
		}
		
		public void DoGoToAppliance(int x, int y)
		{
			xDestination = x;
			yDestination = y;
		}

}
