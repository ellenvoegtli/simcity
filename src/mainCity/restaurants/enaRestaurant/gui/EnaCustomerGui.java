package mainCity.restaurants.enaRestaurant.gui;

import java.awt.*;

import role.enaRestaurant.EnaCustomerRole;

public class EnaCustomerGui implements Gui{

	private EnaCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean deciding = false;
	private boolean fdChoice = false;
	//private HostAgent host;
	EnaRestaurantGui gui;

	public int xPos;
	private int yPos;
	public int xDestination;
	private int yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	public static String fd;
	public static int yTable = 150;
	public final int xTable1 = 50;
    public final int xTable2 = 200;
    public final int xTable3 = 350;
    
	

	

	public EnaCustomerGui(EnaCustomerRole c, EnaRestaurantGui gui)
	{
		agent = c;
		xPos = 50;
		yPos = 10;
		xDestination = 50 ;
		yDestination = 10;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
		g.setColor(Color.BLACK);;
		
		if(fdChoice)
        {
        	if (fd.equals("steak"))
        	{
        			g.drawString("ST", xPos, yPos);
        	}
        	if(fd.equals("porkchops"))
        	{
        		g.drawString("PC", xPos, yPos);

        	}
        	if (fd.equals("lamb"))
        	{
        		g.drawString("LA", xPos, yPos);
        	}
        	if (fd.equals("lambchops"))
        	{
        		g.drawString("LC", xPos, yPos);
        	}
        }
		if(deciding)
		{
			g.drawString("?", xPos, yPos);
			
		}
	}
	public void Arriving(String foodChoice)
	{
		fd = foodChoice;
		 fdChoice = true;
	}
	
	public void Arrived(String foodChoice)
	{
		 fdChoice =  false;
	}
	
	public void Deciding()
	{
		 deciding = true;
	}
	public boolean Decided()
	{
		return deciding = false;
	}

	public boolean isPresent() {
		return isPresent;
	}
	/*public void setHungry()
	{
		if(agent.restaurantOpen())
		{
			System.out.println("^^^^^^^^^^^^^^^");
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
		xDestination = xPos;
		yDestination = yPos;
		}
	}*/
	
	public boolean goInside() {
		if(agent.restaurantOpen()) {
			System.out.println("^^^^^^^^^^^^^^^");

			isHungry = true;
			agent.gotHungry();
			setPresent(true);
			xDestination = xPos;
			yDestination = yPos;
			return true;
		}
		
		return false;
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int tableNum) {// int seatnumber, later you will map seatnumber to table coordinates.
		
		if(tableNum == 0)
		{
			xDestination = xTable1;
		}
		else if(tableNum == 1)
		{
			xDestination = xTable2;
		}
		else if(tableNum == 2)
		{
			xDestination = xTable3;
		}
		
		yDestination = yTable;
		command = Command.GoToSeat;
	}
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    
    }
	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
