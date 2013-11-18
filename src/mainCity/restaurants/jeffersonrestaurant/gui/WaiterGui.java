package mainCity.restaurants.jeffersonrestaurant.gui;


import java.awt.*;

import mainCity.restaurants.jeffersonrestaurant.CustomerRole;
import mainCity.restaurants.jeffersonrestaurant.WaiterRole;
import mainCity.restaurants.jeffersonrestaurant.WaiterRole.*;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;

public class WaiterGui implements Gui {

    private WaiterRole agent = null;
    
    private int xHome, yHome;
    
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private boolean atTable=false;
    public static final int xTable = 200;
    public static final int yTable = 350;
    public static final int width = 20;
    public static final int height = 20;
    int t;
    public boolean atOrigin= true;
    public CookGui cookgui =null;
    public WaiterGui(WaiterRole agent) {
        this.agent = agent;
    }

    public void setOrigin( int x, int y){
    	xPos=x;
    	yPos=y;
    	xDestination = x;
    	yDestination =y;
    	xHome=x;
    	yHome=y;
    	
    }
    
    public void updatePosition() {
        if (xPos < xDestination)
            //xPos++;
        	xPos=xPos+5;
        else if (xPos > xDestination)
            //xPos--;
        	xPos=xPos-5;

        if (yPos < yDestination)
            //yPos++;
        	yPos=yPos+5;
        else if (yPos > yDestination)
            //yPos--;
        	yPos=yPos-5;
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20+ 100*t-100) & (yDestination == yTable - 20)) {
    		agent.msgAtTable();
    	   
          // System.out.println("agent is at table");
        }
        if(xPos== 400 && yPos == 200){
        	
        	agent.msgAtCook();
        }
        if(xPos== 240 && yPos == 200){
        	
        	agent.msgAtPlating();
        	
        }
        
        if(xPos == xHome && yPos == yHome){
        	atOrigin=true;
        	agent.msgAtHome();
        	//System.out.println("reached home");
        }
        
      
        else
        {
        	atOrigin = false;
        }
        
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, width, height);
       
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(Customer customer,int table) {
        t=table;
        
        	xDestination = xTable + 20+ 100*t -100;
        	yDestination = yTable - 20;
        
       }
    
    public void DoGoToCook(){
    	xDestination = 400;
    	yDestination = 200;
    	
    }
    public void DoTakeOrder(int table){
    	t=table;
    	xDestination = xTable + 20+ 100*t -100;
    	yDestination = yTable - 20;
    }
    
    public void DoGetFood(){
    	xDestination = 240;
    	yDestination = 200;
    	
    }
    public void DoDeliverOrder(int table){
    	t=table;
    	xDestination = xTable + 20+ 100*t -100;
    	yDestination = yTable - 20;
    	
    }
    public boolean atStart()
    {
    	return atOrigin;
    }

    public void DoLeaveCustomer() {
        xDestination = xHome;
        yDestination = yHome;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
