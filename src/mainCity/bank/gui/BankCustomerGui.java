package mainCity.bank.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import mainCity.bank.BankCustomer;
import mainCity.gui.Gui;


public class BankCustomerGui implements Gui {

    private BankCustomer bankcustomer = null;
    
    private int xHome, yHome;
    
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    
    public static final int xTeller1 = 150;
    public static final int yTeller1 = 120;
    public static final int xBanker;
    public static final int yBanker;
    
    public static final int xTeller2 = 150;
    public static final int yTeller2 = 120;
    
    
    public static final int width = 20;
    public static final int height = 20;
    int t;
    public boolean atOrigin= true;
   
    public BankCustomerGui(BankCustomer bankcustomer) {
        this.bankcustomer = bankcustomer;
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
        		& (xDestination == xTeller1) & (yDestination == yTeller1)) {
    		bankcustomer.msgAtTeller();
    	   
          // System.out.println("agent is at table");
        }
        if(xPos== 400 && yPos == 200){
        	
        	//agent.msgAtCook();
        }
        if(xPos== 240 && yPos == 200){
        	
        	//agent.msgAtPlating();
        	
        }
        
        if(xPos == xHome && yPos == yHome){
        	atOrigin=true;
        	//agent.msgAtHome();
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

    public void doGoToWaitingArea(int x, int y) {
        
        
        	xDestination = xTeller + 20+ 100*t -100;
        	yDestination = yTeller - 20;
        
       }
    
    public void doGoToTeller1(int x, int y){
    	
    	xDestination = xTeller1;
    	yDestination = yTeller1;
    	
public void doGoToTeller2(int x, int y){
    	
    	xDestination = xTeller2;
    	yDestination = yTeller2;
    }
 public void doGoToBanker(int x, int y){
    	
    	xDestination = xTeller + 20+ 100*t -100;
    	yDestination = yTeller - 20;
    }
 
    public void DoDeliverOrder(int table){
    	t=table;
    	xDestination = xTeller + 20+ 100*t -100;
    	yDestination = yTeller - 20;
    	
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

