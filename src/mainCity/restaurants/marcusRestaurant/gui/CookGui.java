package mainCity.restaurants.marcusRestaurant.gui;

import java.awt.*;

import mainCity.restaurants.marcusRestaurant.interfaces.CookGuiInterface;
import role.marcusRestaurant.MarcusCookRole;

public class CookGui implements Gui, CookGuiInterface{
	private MarcusCookRole agent = null;
	private boolean isPresent = false;

	private int xPos;
	private int xDestination, yDestination;
	private boolean onDuty;
	private boolean cooking, grill1, grill2, grill3, grill4;

	public CookGui(MarcusCookRole c) {
		agent = c;
		xPos = 235;
		xDestination = 235;
		cooking = grill1 = grill2 = grill3 = grill4 = false;
		onDuty = true;
	}
	
	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;
		
		if(xPos == xDestination && (grill1 || grill2 || grill3 || grill4)) {
			cooking = true;
		}
	}

	@Override
	public void DoGoToCounter() {
		xDestination = 235;
	}
	
	@Override
	public void DoGoToGrill(int grill) {
		xDestination = 148 + 20*grill;
		
		switch(grill) {
			case 1:
				grill1 = true;
				break;
			case 2:
				grill2 = true;
				break;
			case 3:
				grill3 = true;
				break;
			case 4:
				grill4 = true;
				break;
		}
	}
	
	@Override
	public void DoClearGrill(int grill) {
		switch(grill) {
		case 1:
			grill1 = false;
			break;
		case 2:
			grill2 = false;
			break;
		case 3:
			grill3 = false;
			break;
		case 4:
			grill4 = false;
			break;
		}

		cooking = (grill1 || grill2 || grill3 || grill4);
	}
	
	@Override
	public void DoLeaveRestaurant() {
		onDuty = false;
	}
	
	public void draw(Graphics2D g) {
		if(onDuty) {
			g.setColor(Color.RED);
			g.fillRect(xPos, 20, 20, 20);
			
			if(cooking) {
				if(grill1) {
					g.setColor(Color.ORANGE);
					g.fillRect(174, 4, 8, 8);
				}
				if(grill2) {
					g.setColor(Color.ORANGE);
					g.fillRect(194, 4, 8, 8);
				}
				if(grill3) {
					g.setColor(Color.ORANGE);
					g.fillRect(214, 4, 8, 8);
				}
				if(grill4) {
					g.setColor(Color.ORANGE);
					g.fillRect(234, 4, 8, 8);
				}
			}
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
}
