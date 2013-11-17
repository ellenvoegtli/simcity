package enaRestaurant.gui;


import enaRestaurant.CustomerRole;
import enaRestaurant.HostRole.Table;
import enaRestaurant.WaiterRole;
import sun.tools.javap.Tables;

import java.awt.*;


public class WaiterGui implements Gui
{
	
		
	    private WaiterRole agent = null;
	    
	   private RestaurantGui gui;

	    private int xPos = 500, yPos = 100;//default waiter position
	    private int xDestination = 500, yDestination = 100;//default start position
	    public int tableXX;
	    int home;
	    public static  int xTable;
	    public final int xTable1 = 50;
	    public final int xTable2 = 200;
	    public final int xTable3 = 350;
	    public static String fd; 
	    boolean lobby = false;
	    boolean entrance = false;

	    boolean onBreak = false;
	    public static final int yTable = 350;
	    private boolean fdChoice = false;
	    public WaiterGui(WaiterRole w, RestaurantGui gui, int yP)
		{ //HostAgent m) {
			agent = w;
			xPos =500;
			yPos = 100 + yP;
			xDestination = 500;
			yDestination = 100 + yP;
			home = 100 + yP;
			//maitreD = m;
			this.gui = gui;			
			
		}

	public int setPositionX(int tableNum)
	{
			if(tableNum == 0)
			{
				xTable = xTable1;
			}
			else if(tableNum == 1)
			{
				xTable = xTable2;
			}
			else if(tableNum == 2)
			{
				xTable = xTable3;
			}
			
			
			return xTable;
	}

	public void setXNum(int tablePlace)
	{
		tableXX = tablePlace;
	}
	    
	public  WaiterGui(WaiterRole agent) 
	{
	        this.agent = agent;
	}

	/*public void setBreak() 
	{
		onBreak = true;
		agent.gotBreak();
		setPresent(true);
	}*/
	
    public void updatePosition() 
    {
	        if (xPos < xDestination)
	            xPos++;
	        else if (xPos > xDestination)
	            xPos--;

	        if (yPos < yDestination)
	            yPos++;
	        else if (yPos > yDestination)
	            yPos--;
	        if(xDestination != 500 || yDestination != home)
       		{
       				lobby = true;
    	   			//agent.msgAtHome();
       		}
	        if(xDestination != 50 || yDestination != 10+20)
       		{
       				entrance = true;
    	   			//agent.msgAtHome();
       		}
	        
	        if (this.atDestination())
	        {
	        		if(xDestination == setPositionX(tableXX) + 20 && (yDestination == yTable - 20)) 
	           				{
	        					agent.msgAtTable();
	           				}
	        		if(xDestination == 280 && yDestination == 480)
	        		{
	        			agent.msgAtKitchen();
	        		}
	        		if(xDestination == 20 && yDestination == 0)
	        		{
	        			agent.msgAtCashier();
	        		}
	        		else if(xDestination == 50 && yDestination == 10+20 && entrance == true)
	           		{
	           				entrance = false;
	        	   			agent.msgAtEntrance();
	           		}
	           		else if(xDestination == 500 && yDestination == home && lobby == true)
	           		{
	           				lobby = false;
	        	   			agent.msgAtHome();
	           		}
	        }
	    }
	public boolean atDestination()
	{
		
		return xPos == xDestination && yPos == yDestination;
	}
	public void draw(Graphics2D g) 
	{
	        g.setColor(Color.MAGENTA);
	        g.fillRect(xPos, yPos, 20, 20);
	        if(fdChoice)
	        {	       
	        	g.setColor(Color.BLACK);

	        
	        	if (fd.equals("steak"))
	        	{
	        			g.drawString("ST", xPos, yPos);
	        	}
	        	if(fd.equals("chicken"))
	        	{
	        		g.drawString("CH", xPos, yPos);

	        	}
	        	if (fd.equals("salad"))
	        	{
	        		g.drawString("SAL", xPos, yPos);
	        	}
	        	if (fd.equals("pizza"))
	        	{
	        		g.drawString("PZ", xPos, yPos);
	        	}
	        }
	}

	
	public  void Arriving(String foodChoice)
	{
		fd = foodChoice;
		fdChoice = true;
	}
	
	public void Arriving()
	{
		 fdChoice = false;
	}
	public void setBreak() {
		onBreak = true;
		agent.wantBreak();
		//setPresent(true);
	}
	public boolean onBreak() {
		return onBreak;
	}

	
	public boolean isPresent() 
	{
	      return true;
	}

	public void SubmitOrder(String ch)
	{
		fd = ch;
	}
	
	public void DoGetCustomer(CustomerRole customer)
	{
		xDestination = customer.getXPos();
		yDestination = 10+20;
	}
	    
	public void DoBringToTable(CustomerRole customer, int tableN) 
	{
			//DoGetCustomer(customer);
	    	xDestination = setPositionX(tableXX) + 20;
			yDestination = yTable - 20;
	    
	 }
	    
	    public void DoGoToTable(CustomerRole customer, Table t)
		{
	    	setXNum(t.getTableNumber());
			xDestination = setPositionX(tableXX)+20;
			yDestination = yTable - 20;
		}
	    
		public void DoGoToKitchen()
		{
			xDestination = 280;
			yDestination = 480;
		}
		public void DoGoToCashier()
		{
			xDestination = 20;
			yDestination = 0;
		}
		public void DoGoOnBreak()
		{
			xDestination = -20;
			yDestination = -20;
		}

		public void DoServe(String choice, Table table)
		{
			setXNum(table.getTableNumber());
			xDestination = setPositionX(tableXX)+20;
			yDestination = yTable - 20;
		}
		
	    public void DoLeaveCustomer() 
	    {
	        xDestination = 500;
	        yDestination = home;
	    }

	    public int getXPos() {
	        return xPos;
	    }

	    public int getYPos() {
	        return yPos;
	    }
	}


