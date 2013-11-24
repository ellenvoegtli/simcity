package mainCity.bank.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import mainCity.bank.BankCustomerRole;
import mainCity.bank.BankTellerRole;
import mainCity.bank.BankerRole;
import mainCity.gui.Gui;

public class BankerGui implements Gui {

	private BankerRole banker = null;
    BankGui gui;
	Graphics2D g2;
	
	
   
    
    private int xPos = -20, yPos = 520;//default  position
    private int xDestination = -20, yDestination = 520;//default start position
    
    
    public static final int xHome = -20;
    public static final int yHome = 520;
    public static final int xTeller0 = 150;
    public static final int yTeller0 = 100;
    
    public static final int xBanker=440;
    public static final int yBanker=330;
    
    
    public static final int width = 20;
    public static final int height = 20;
    int t;
    public boolean atOrigin= true;
   
    public BankerGui(BankerRole banker, BankGui gui) {
        this.banker = banker;
        this.gui=gui;
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
        
        else
        {
        	atOrigin = false;
        }
        
    }
    
    public void doGoToWork(){
    	//System.out.println("teller number is" + tellernumber);
    	
    		xPos=xBanker;
    		yPos=yBanker;
    		xDestination =xBanker;
    		yDestination =yBanker;
    	
    }
    
    public void doLeaveWork(){
    	//instantaneous position movement
    		xPos=xHome;
    		yPos=yHome;
    		xDestination =xHome;
    		yDestination = yHome;
    	
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, width, height);
       
    }

    public boolean isPresent() {
        return true;
    }


 
    public void DoLeaveBank(){
    	
    	xDestination = -20;
    	yDestination = 520;
    	
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


}
