package mainCity.restaurants.jeffersonrestaurant.gui;


import java.awt.*;

import role.jeffersonRestaurant.JeffersonCustomerRole;
import role.jeffersonRestaurant.JeffersonWaiterRole;
import role.jeffersonRestaurant.JeffersonWaiterRole.*;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.WaiterGuiInterface;

public class WaiterGui implements Gui, WaiterGuiInterface {

    private JeffersonWaiterRole agent = null;
    
    private int xHome, yHome;
    
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private boolean atTable=false;
    int t;
    public boolean atOrigin= true;
    public boolean traveling = false;
    public CookGui cookgui =null;
    public WaiterGui(JeffersonWaiterRole agent) {
        this.agent = agent;
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#setOrigin(int, int)
	 */
    public void setOrigin( int x, int y){
    	xPos=x;
    	yPos=y;
    	xDestination = x;
    	yDestination =y;
    	xHome=x;
    	yHome=y;
    	
    	
    }
    
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#updatePosition()
	 */
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
        if(xPos==-20 && yPos == -20 && traveling==true){
        	traveling=false;
        	agent.msgFinishedLeavingRestaurant();
        	
        }
        
      
        else
        {
        	atOrigin = false;
        }
        
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#draw(java.awt.Graphics2D)
	 */
    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, width, height);
       
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#isPresent()
	 */
    public boolean isPresent() {
        return true;
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoBringToTable(mainCity.restaurants.jeffersonrestaurant.interfaces.Customer, int)
	 */
    public void DoBringToTable(Customer customer,int table) {
        t=table;
        
        	xDestination = xTable + 20+ 100*t -100;
        	yDestination = yTable - 20;
        	traveling = true;
       }
    
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoGoToCook()
	 */
    public void DoGoToCook(){
    	xDestination = 400;
    	yDestination = 200;
    	traveling = true;
    	
    }
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoTakeOrder(int)
	 */
    public void DoTakeOrder(int table){
    	t=table;
    	xDestination = xTable + 20+ 100*t -100;
    	yDestination = yTable - 20;
    	traveling = true;
    }
    
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoGetFood()
	 */
    public void DoGetFood(){
    	xDestination = 240;
    	yDestination = 200;
    	traveling = true;
    	
    }
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoDeliverOrder(int)
	 */
    public void DoDeliverOrder(int table){
    	t=table;
    	xDestination = xTable + 20+ 100*t -100;
    	yDestination = yTable - 20;
    	traveling = true;
    	
    }
    
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoLeaveRestaurant()
	 */
    public void DoLeaveRestaurant(){
    	xDestination = -20;
    	yDestination = -20;
    	traveling = true;
    	
    }
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#atStart()
	 */
    public boolean atStart()
    {
    	return atOrigin;
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoLeaveCustomer()
	 */
    public void DoLeaveCustomer() {
        xDestination = xHome;
        yDestination = yHome;
        traveling = true;
    }
    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#DoGoHome()
	 */
    public void DoGoHome(){
    	xDestination = xHome;
        yDestination = yHome;
        traveling = true;
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#getXPos()
	 */
    public int getXPos() {
        return xPos;
    }

    /* (non-Javadoc)
	 * @see mainCity.restaurants.jeffersonrestaurant.gui.WaiterGuiInterface#getYPos()
	 */
    public int getYPos() {
        return yPos;
    }
}
