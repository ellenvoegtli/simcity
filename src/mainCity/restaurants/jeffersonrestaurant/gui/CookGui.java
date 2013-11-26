package mainCity.restaurants.jeffersonrestaurant.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import role.jeffersonRestaurant.JeffersonCookRole;
import role.jeffersonRestaurant.JeffersonCustomerRole;

public class CookGui implements Gui {
	private int xPos;
	private int yPos;
	private int xHome=370;
	private int yHome = 200;
	
	Graphics2D g2;
	private JeffersonCookRole agent = null;
	
	boolean c1,c2,c3 = false;
	boolean p1,p2,p3 = false;
	
	
	int table;
	
	public CookGui(JeffersonCookRole c){ //HostAgent m) {
		agent = c;
		xHome=370;
		yHome = 200;
		xPos = -20;
		yPos = -20;
		
		
	}
	
	
	public void setOrigin(int x, int y){
		xHome=x;
		yHome=y;
		
	}
	
	public void updatePosition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		g2=g;
	
		g.setColor(Color.RED);
        g.fillRect(xPos, yPos, 20, 20);
        /*
        if(!c1 & !p1){
        	g2.setColor(Color.BLUE);
			g2.fillRect(400, 200, 8, 8);
			g2.setColor(Color.cyan);
			g2.fillRect(240, 200, 8, 8);	
        }
        if(!c2 & !p2){
        	g2.setColor(Color.BLUE);
			g2.fillRect(420, 200, 8, 8);
			g2.setColor(Color.cyan);
			g2.fillRect(260, 200, 8, 8);
        	
        }
        if(!c3 & !p3){
        	g2.setColor(Color.BLUE);
			g2.fillRect(400, 220, 8, 8);
			g2.setColor(Color.cyan);
			g2.fillRect(240, 220, 8, 8);
        	
        }
        
        
        
        
        
        if(c1==true){
        	g2.setColor(Color.GREEN);
			g2.fillRect(400, 200, 8, 8);
        }
        if(c2==true){
        	g2.setColor(Color.GREEN);
			g2.fillRect(420, 200, 8, 8);
        }
        if(c3==true){
        	g2.setColor(Color.GREEN);
			g2.fillRect(400, 220, 8, 8);
        }
	
     if(c1 | c2 | c3){   
		switch(table){
			
			case 1:
				
				g2.setColor(Color.GREEN);
				g2.fillRect(400, 200, 8, 8);
				break;
			
			
			case 2:
				g2.setColor(Color.GREEN);
				g2.fillRect(420, 200, 8, 8);
				break;
			
			case 3:
				g2.setColor(Color.GREEN);
				g2.fillRect(400, 220, 8, 8);
				break;
			
			}
     }
     
     if(p1==true){
     	g2.setColor(Color.GREEN);
		g2.fillRect(240, 200, 8, 8);
		
     }
     if(p2==true){
     	g2.setColor(Color.GREEN);
		g2.fillRect(260, 200, 8, 8);
     }
     if(p3==true){
     	g2.setColor(Color.GREEN);
		g2.fillRect(240, 220, 8, 8);
     }
	
  if(p1 | p2 | p3){   
		switch(table){
			
			case 1:
				
				g2.setColor(Color.GREEN);
				g2.fillRect(240, 200, 8, 8);
				g2.setColor(Color.BLUE);
				g2.fillRect(400, 200, 8, 8);
				break;
			
			
			case 2:
				g2.setColor(Color.GREEN);
				g2.fillRect(260, 200, 8, 8);
				g2.setColor(Color.BLUE);
				g2.fillRect(420, 200, 8, 8);
				break;
			
			case 3:
				g2.setColor(Color.GREEN);
				g2.fillRect(240, 220, 8, 8);
				g2.setColor(Color.BLUE);
				g2.fillRect(400, 200, 8, 8);
				
				break;
			
			}
 
  }
  
    */  
     
     
     
     
     
	}

	
	public boolean isPresent() {
		return true;
	}
	
	public void DoLeaveRestaurant(){
		xPos=-20;
		yPos=-20;
		agent.msgLeftRestaurant();
	}
	
	public void DoEnterRestaurant(){
		xPos=xHome;
		yPos=yHome;
	}
/*	
	public void drawFood(int t){
		table=t;
		draw(g2);
		switch(t){
			case 1:
				c1=true;
				break;
			case 2:
				c2=true;	
				break;
			case 3:	
				c3=true;
				break;
		}
		
	}
	
	public void moveFood(int t){
		table=t;
		
		switch(t){
			case 1:
				p1=true;
				c1=false;
				break;
			case 2:
				p2=true;
				c2=false;
				break;
			case 3:	
				p3=true;
				c3=false;
				break;
		}
		draw(g2);
	}

	public void unDraw(int t){
table=t;
		
		switch(t){
			case 1:
				p1=false;
				c1=false;
				break;
			case 2:
				p2=false;
				c2=false;
				break;
			case 3:	
				p3=false;
				c3=false;
				break;
		}
		draw(g2);
		
	}
	
*/	
	

}
