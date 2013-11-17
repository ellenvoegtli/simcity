package mainCity.restaurants.EllenRestaurant.gui;
/*
package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class HostGui implements Gui {

    private final int WINDOWX = 450;
    private final int WINDOWY = 350;
	

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;

    static final int hostWidth = 20;
    static final int hostHeight = 20;
    
    private ImageIcon imageIcon = new ImageIcon("host.png");
	private Image image = imageIcon.getImage();
	
	private boolean sendHostReady = false;

    public HostGui(HostAgent agent) {
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        }

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == 300) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == 400) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        }
        
        //starting position
        if (xPos == xDestination && yPos == yDestination
        		& xDestination == -20 & yDestination == -20 && sendHostReady == false) {
           agent.msgReadyAtStart();
           sendHostReady = true;
        }
        
        
        if (xPos > -19 && yPos > -19 && sendHostReady == true){
        	//System.out.println("msg host NOT ready");
        	sendHostReady = false;
        }
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, hostWidth, hostHeight);

    }

    public boolean isPresent() {
        return true;
    }

    

    public void DoBringToTable1(CustomerAgent customer) {
        xDestination = xTable + 20;
        yDestination = yTable - 20;
    }
    public void DoBringToTable2(CustomerAgent customer) {
        xDestination = 300;
        yDestination = yTable - 20;
    }
    public void DoBringToTable3(CustomerAgent customer) {
        xDestination = 400;
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

*/
