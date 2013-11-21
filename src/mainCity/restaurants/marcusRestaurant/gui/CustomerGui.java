package mainCity.restaurants.marcusRestaurant.gui;

import java.awt.*;

import javax.swing.JLabel;

import role.marcusRestaurant.MarcusCustomerRole;

public class CustomerGui implements Gui{

	private MarcusCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	MarcusRestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination, waitX, waitY;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private JLabel order;

	public static final int xTable = 100;
	public static final int yTable = 260;
	
	private static final int w = 20;
	private static final int h = 20;

	public CustomerGui(MarcusCustomerRole c, MarcusRestaurantGui gui, int pos){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		waitX = xDestination = 0;
		waitY = yDestination = 40 + 40*pos-15*pos;
		order = new JLabel();
		//maitreD = m;
		this.gui = gui;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}
	
	public void DoCreateLabel(String o) {
		order.setText(o);
		order.setLocation(xPos+10, yPos+20);
        order.setSize(order.getPreferredSize());		
		order.setVisible(true);

		gui.getAnimationPanel().add(order);
	}
	
	public void DoUpdateLabel() {
		order.setText(order.getText().substring(0, order.getText().length()-1));
		order.setLocation(order.getX(), order.getY());
	}
	
	public void DoClearLabel() {
		order.setText("");
		order.setVisible(false);
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, w, h);
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
		xDestination = waitX;
		yDestination = waitY;
	}
	
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatNumber) {//later you will map seatnumber to table coordinates.
		xDestination = xTable + 100*(seatNumber-1);
		yDestination = yTable;
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
	
	public int getX() {
		return xDestination;
	}
	
	public int getY() {
		return yDestination;
	}
}
