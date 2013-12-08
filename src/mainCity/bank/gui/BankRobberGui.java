package mainCity.bank.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import mainCity.bank.BankRobberRole;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.gui.Gui;

public class BankRobberGui implements Gui {

	private BankRobberRole bankrobber = null;
    //BankGui gui;
	Graphics2D g2;
	
	
   
    
    private int xPos = -20, yPos = 520;//default  position
    private int xDestination = -20, yDestination = 520;//default start position
    
    
    public static final int xHome = -20;
    public static final int yHome = 520;
    public static final int xBankmanager=20;
    public static final int yBankmanager=280;


    
    
    public static final int width = 20;
    public static final int height = 20;
    int t;
    public boolean atOrigin= true;
    public boolean traveling = true;
   
    public BankRobberGui(BankRobberRole bankrobber) {
        this.bankrobber = bankrobber;
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
       
        if(xPos== xDestination && yPos == yDestination && xDestination ==xBankmanager && yDestination == yBankmanager && traveling==true ){
        	traveling=false;
        	bankrobber.msgAtBankManager();
        	
        }
        
        if(xPos == xHome && yPos == yHome && traveling==true ){
        	traveling=false;
        	bankrobber.msgLeftBank();
        	
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


 
    public void DoLeaveBank(){
    	xDestination = -20;
    	yDestination = 520;
    	traveling=true;
    	
    }
    public void doGoToBankManager() {
    	xDestination = xBankmanager;
    	yDestination = yBankmanager;
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
		
		if(bankrobber.bankClosed()){
			System.out.println("Bank is closed.");
			return false;
		}
		return true;
	}

	

}
