package mainCity.bank.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import mainCity.bank.interfaces.BankCustomer;
import mainCity.gui.Gui;



public class BankCustomerGui implements Gui {

    private BankCustomer bankcustomer = null;
    //BankGui gui;
	Graphics2D g2;
	
	
   
    
    private int xPos = -20, yPos = 520;//default  position
    private int xDestination = -20, yDestination = 520;//default start position
    
    
    public static final int xHome = -20;
    public static final int yHome = 520;
    public static final int xTeller0 = 150;
    public static final int yTeller0 = 120;
    public static final int xBanker=390;
    public static final int yBanker=320;
    public static final int xWaiting=50;
    public static final int yWaiting=400;
    
    public static final int xTeller1 = 300;
    public static final int yTeller1 = 120;
    
    
    public static final int width = 20;
    public static final int height = 20;
    int t;
    public boolean atOrigin= true;
    public boolean traveling = true;
   
    public BankCustomerGui(BankCustomer bankcustomer) {
        this.bankcustomer = bankcustomer;
        //this.gui=gui;
    }

    public void setOrigin( int x, int y){
    	xPos=x;
    	yPos=y;
    	xDestination = x;
    	yDestination =y;
    	
    	
    }
    
    public void updatePosition() {
        if (xPos < xDestination)
            //xPos++;
        	xPos=xPos+2;
        else if (xPos > xDestination)
            //xPos--;
        	xPos=xPos-2;

        if (yPos < yDestination)
            //yPos++;
        	yPos=yPos+2;
        else if (yPos > yDestination)
            //yPos--;
        	yPos=yPos-2;
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTeller0) & (yDestination == yTeller0)) {
    		bankcustomer.msgAtTeller();
    	 
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTeller1) & (yDestination == yTeller1)) {
    		bankcustomer.msgAtTeller();
    	   
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xBanker) & (yDestination == yBanker)) {
    		//System.out.println("reached banker");
        	bankcustomer.msgAtBanker();
    	   
        }
        
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xWaiting) & (yDestination == yWaiting)) {
    		bankcustomer.msgAtWaiting();    	 
        }
        
        if(xPos == xHome && yPos == yHome && xPos == xDestination && yPos == yDestination && traveling==true ){
        	traveling=false;
        	xPos = -20;
        	yPos = 520;
        	xDestination = -20; 
        	yDestination = 520;
        	atOrigin=true;
        	bankcustomer.msgLeftBank();
        	
        	
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

    public void doGoToWaitingArea() {
        	System.out.println("going to waiting");
        	xDestination = xWaiting;
        	yDestination = yWaiting;
        	traveling=true;
        
       }
    
    public void doGoToTeller1(){
    	
    	xDestination = xTeller0;
    	yDestination = yTeller0;
    	traveling=true;
    }
    	
public void doGoToTeller2(){
    	
    	xDestination = xTeller1;
    	yDestination = yTeller1;
    	traveling=true;
    }
 public void doGoToBanker(){
    	
    	xDestination = xBanker;
    	yDestination = yBanker;
    	traveling=true;
    }
 
    public void DoLeaveBank(){
    	
    	xDestination = -20;
    	yDestination = 520;
    	traveling=true;
    	
    }
    public boolean atStart()
    {
    	return atOrigin;
    }


    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public boolean goInside() {
		
		if(bankcustomer.bankClosed()){
			return false;
		}
		return true;
	}
}

