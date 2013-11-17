package mainCity.restaurants.enaRestaurant.gui;


import mainCity.restaurants.enaRestaurant.CustomerRole;
import mainCity.restaurants.enaRestaurant.HostRole;
import mainCity.restaurants.enaRestaurant.HostRole.Table;
import mainCity.restaurants.enaRestaurant.WaiterRole.MyCustomers;
import mainCity.restaurants.enaRestaurant.WaiterRole;
import sun.tools.javap.Tables;

import java.awt.*;

public class HostGui implements Gui {

   // private WaiterAgent agent = null;
    private HostRole agent = null;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    public int tableXX;
    public static  int xTable;
    public final int xTable1 = 50;
    public final int xTable2 = 200;
    public final int xTable3 = 350;

    public static final int yTable = 450;

public int setPositionX(int tableNum)
{
		if(tableNum == 0)
		{
			xTable = xTable1;
		}
		else if(tableNum == 1)
		{
			xTable = xTable2;
		}
		else if(tableNum == 2)
		{
			xTable = xTable3;
		}
		
		
		return xTable;
}

public void setXNum(int tablePlace)
{
	tableXX = tablePlace;
}
    
public HostGui(HostRole agent) 
    {
        this.agent = agent;
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
        
        
/*
        if (this.atDestination())
        {
        		if(xDestination == setPositionX(tableXX) + 20 && (yDestination == yTable - 20)) 
           				agent.msgAtTable();
           		else if(xDestination == -20 && yDestination == -20)
           		{
        	   
        	   			//agent.msgAtHome();
           		}
        }*/
    }
public boolean atDestination(){
	
	return xPos == xDestination && yPos == yDestination;
}
    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerRole customer, int tableN) {
    	xDestination = setPositionX(tableXX) + 20;
		yDestination = yTable - 20;
    	
    
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
