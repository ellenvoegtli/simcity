package enaRestaurant.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.sun.tools.javac.util.List;

import enaRestaurant.CookRole;
import enaRestaurant.CustomerRole;
import enaRestaurant.HostRole;

public class CookGui implements Gui 
{
	private boolean isPresent = true;
    private boolean fdCooking = false;
    public boolean fdPlating = false;
    public java.util.List<String> grillingFoods = Collections.synchronizedList(new ArrayList<String>());
    public Collection<String> plating = Collections.synchronizedCollection(new ArrayList<String>());
    public static String gfd; 
    public static String pfd; 


	private CookRole agent;
	private RestaurantGui gui;
	private int grillPosX = 375;
	private int platePosY = 500;

	    private int xPos, yPos;//default waiter position
	    //private int xDestination, yDestination;//default start position
  

	public CookGui(CookRole c)
	{
		agent = c;
		xPos = 280;
		yPos = 20;
		//xDestination = 50;
		//yDestination = 10;
		//this.gui = gui;
	}
	   
	public void draw(Graphics2D g) 
	{
			g.setColor(Color.BLUE);
	        g.fillRect(310, 530, 20, 20);
	        g.setColor(Color.RED);
	        g.fillRect(275, 490, 90, 25);
	        g.setColor(Color.LIGHT_GRAY);
	        g.fillRect(375, 490, 35, 105);
	       synchronized(grillingFoods)
	       {
	        for(String gfd: grillingFoods)
	       {
	    		       
	        	g.setColor(Color.BLACK);

	        
	        	if (gfd.equals("steak"))
	        	{
	        			g.drawString("ST", grillPosX, 492);
	        	}
	        	if(gfd.equals("chicken"))
	        	{
	        		g.drawString("CH", grillPosX, 502);

	        	}
	        	if (gfd.equals("salad"))
	        	{
	        		g.drawString("SAL", grillPosX, 512);
	        	}
	        	if (gfd.equals("pizza"))
	        	{
	        		g.drawString("PZ", grillPosX, 522);
	        	}
	        
	       }
	       }
	       synchronized(plating)
	       {
	        for(String pfd : plating)
	        {
	        	       
	        	g.setColor(Color.BLACK);

	        
	        	if (pfd.equals("steak"))
	        	{
	        			g.drawString("ST", 280, platePosY);
	        	}
	        	if(pfd.equals("chicken"))
	        	{
	        		g.drawString("CH", 290, platePosY);

	        	}
	        	if (pfd.equals("salad"))
	        	{
	        		g.drawString("SAL", 310, platePosY);
	        	}
	        	if (pfd.equals("pizza"))
	        	{
	        		g.drawString("PZ", 330, platePosY);
	        	}
	        
	        }
	       }
	        
	}

	public  void Cooking(String foodChoice)
	{	
		gfd = foodChoice;
		synchronized(grillingFoods)
		{
		  grillingFoods.add(gfd);
		}
	
	}
	
	public void Plating(String foodChoice)
	{		
		pfd = foodChoice;

		synchronized(grillingFoods)
		{
			grillingFoods.remove(pfd);

		}
			synchronized(plating)
			{
			  plating.add(pfd);
			}
	}
	public void platingDone(String fd)
	{
		synchronized(plating)
		{
			plating.remove(fd);
		}
	}
	
	public void updatePosition() 
	{
		
	}

	public void setPresent(boolean p) 
	{
		isPresent = p;
	}
	public boolean isPresent() 
	{
		return isPresent;
	}
	public int getXPos()
	{
		return xPos;
	}
	
	


}
