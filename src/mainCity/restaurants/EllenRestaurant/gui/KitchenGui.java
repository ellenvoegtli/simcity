package mainCity.restaurants.EllenRestaurant.gui;


import mainCity.restaurants.EllenRestaurant.*;

import java.awt.*;
import java.util.*;

import javax.swing.*;

public class KitchenGui implements Gui {

    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
	
    EllenAnimationPanel animation;
	private boolean isPresent = false;
	
	/* for reference:
	private final int grillX = WINDOWX - 30;
    private final int grillY = WINDOWY/2 - 60;
    private final int platingX = WINDOWX - 90;
    private final int platingY = WINDOWY/2 - 60;
	*/
	

    private int xPos = WINDOWX, yPos = WINDOWY/2;//default waiter position
   

	private Collection<Order> orderGuis = new ArrayList<Order>();
	enum OrderState {pending, cooking, finished};


    public KitchenGui(EllenAnimationPanel a) {
        //this.agent = agent;
        this.animation = a;

    }
    
    public boolean isPresent() {
        return true;
    }
    
    public void setPresent(boolean p){
    	isPresent = p;
    }


    public void updatePosition() {

    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
                
    	if (!orderGuis.isEmpty()){
	        for (Order o : orderGuis){
	            
	        	if (o.choice == "steak"){
	        		g.drawString("STK", o.posX, o.posY);
	        	}
	        	else if (o.choice == "pasta"){
	        		g.drawString("PST", o.posX, o.posY); 
	        	}
	        	else if (o.choice == "pizza"){
	        		g.drawString("PZA", o.posX, o.posY); 
	        	}
	        	else if (o.choice == "soup"){
	        		g.drawString("SP", o.posX, o.posY);
	        	}
	        }
    	}
    }
    

    public void DoGrilling(String choice, int table, int x, int y){
    	orderGuis.add(new Order(choice, table, x, y));
    	
    }
    
    public void DoPlating(String choice, int table, int x, int y){
    	Order o = null;
		for (Order thiso : orderGuis){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thiso.table == table){
				o = thiso;
				break;
			}
		}
		o.s = OrderState.finished;
		o.posX = x;
		o.posY = y;
    }
    
    public void deleteOrderGui(int table){
    	Order o = null;
		for (Order thiso : orderGuis){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thiso.table == table){
				o = thiso;
				break;
			}
		}
		orderGuis.remove(o);
    }
   
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    

    private class Order {
		//WaiterAgent waiter;
		String choice;
		int table;
		OrderState s;
		int posX;
		int posY;
		
		Order(String choice, int table, int x, int y){
			this.choice = choice;
			posX = x;
			posY = y;
			s = OrderState.cooking;
			this.table = table;
			
		}
		
	}
}
