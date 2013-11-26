package mainCity.bank.gui;

import java.awt.Color;
import java.awt.Graphics2D;



import mainCity.bank.BankTellerRole;
import mainCity.gui.Gui;

public class BankTellerGui implements Gui {
	private BankTellerRole bankteller = null;
    //BankGui gui;
	Graphics2D g2;
	
	
   
    
    private int xPos = -20, yPos = 520;//default  position
    private int xDestination = -20, yDestination = 520;//default start position
    
    
    public static final int xHome = -20;
    public static final int yHome = 520;
    public static final int xTeller0 = 145;
    public static final int yTeller0 = 80;
    
    public static final int xTeller1 = 320;
    public static final int yTeller1 = 80;
    
    
    public static final int width = 20;
    public static final int height = 20;
    int t;
    public boolean atOrigin= true;
   
    public BankTellerGui(BankTellerRole bankteller) {
        this.bankteller = bankteller;
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
        
        else
        {
        	atOrigin = false;
        }
        
    }
    
    public void doGoToWork(int tellernumber){
    	//System.out.println("teller number is" + tellernumber);
    	if(tellernumber==0){
    		xPos=xTeller0;
    		yPos=yTeller0;
    		xDestination =xTeller0;
    		yDestination = yTeller0;
    	}
    	if(tellernumber==1){
    		xPos=xTeller1;
    		yPos=yTeller1;
    		xDestination =xTeller1;
    		yDestination = yTeller1;
    	}
    	
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
