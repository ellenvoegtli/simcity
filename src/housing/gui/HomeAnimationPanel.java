package housing.gui;

import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;
import mainCity.gui.CityView;
import mainCity.restaurants.enaRestaurant.gui.EnaRestaurantPanel;

	import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
public class HomeAnimationPanel extends CityCard implements ActionListener
{
	

	   // private  int WINDOWX = 520;
	   // private  int WINDOWY = 360;
	    
	    private int applianceWidth = 20;
	    private int applianceHeight = 15;
	    private int tableWidth = 30;
	    private int tableHeight = 20;
	    private Image bufferImage;
	    private Dimension bufferSize;
	    private boolean ty;
		private HomePanel home = new HomePanel(this);
		



		

	    protected List<Gui> guis = new ArrayList<Gui>();

public HomeAnimationPanel(CityGui cg, boolean type) {
	    	super(cg);
	    	this.ty = type;
	    	ContactList.getInstance().setHome(home);
	        setBorder(BorderFactory.createRaisedBevelBorder());

	    	
	    	
	    	//**********************
	    	//ContactList.getInstance().setAptB(AptBuilding);

	    	
	    	setSize(520, 360);
	        setVisible(true);
	        
	        bufferSize = this.getSize();
	 
	    	Timer timer = new Timer(7, this );
	    	timer.start();
	    }

		public void actionPerformed(ActionEvent e) {
			repaint();  //Will have paintComponent called
		}

	    public void paintComponent(Graphics g) {
	        Graphics2D g2 = (Graphics2D)g;
	        JLabel label;
	        
	        
	     if(ty)
	     {

	        //Clear the screen by painting a rectangle the size of the frame
	        g2.setColor(getBackground());
	        g2.fillRect(0, 0, 540, 380 );

	        //Here is the table
	        g2.setColor(Color.BLACK);       
	        g2.fillRect(200 ,25, applianceWidth, applianceHeight);
	        
	        g2.drawString("stove", 200, 20);
	        g2.fillRect(250, 25, applianceWidth, applianceHeight);//200 and 250 need to be table params
	        g2.drawString("sink", 250, 20);

	        
	        g2.fillRect(300,  25,  applianceWidth,  applianceHeight);

	        g2.drawString("fridge", 300, 20);
	        
	        g2.setColor(Color.ORANGE);       

	        g2.fillRect(250, 150, tableWidth, tableHeight);//200 and 250 need to be table params
	        
	        g2.setColor(Color.lightGray);       
	        g2.fillRect(50 ,200, 50, 20);
	        
	        g2.setColor(Color.darkGray);       
	        g2.fillRect(70 ,80, 25, 15);
	     }
	     
	     
	     if (!ty)
	     {
	    	//Clear the screen by painting a rectangle the size of the frame
		        g2.setColor(getBackground());
		        g2.fillRect(0, 0, 540, 380 );

		        //Here is the table
		        g2.setColor(Color.BLACK);       
		        g2.fillRect(350 ,15, applianceWidth, applianceHeight);
		        g2.drawString("stove", 350, 15);
		       
		        g2.fillRect(400, 15, applianceWidth, applianceHeight);//200 and 250 need to be table params
		        g2.drawString("sink", 400, 15);

		        
		        g2.fillRect(450,  15,  applianceWidth,  applianceHeight);
		        g2.drawString("fridge", 450, 15);
		        
		        g2.setColor(Color.ORANGE);       
		        g2.fillRect(250, 150, tableWidth, tableHeight);//200 and 250 need to be table params
		        
		        g2.setColor(Color.lightGray);       
		        g2.fillRect(50 ,200, 50, 20);
		        
		        g2.setColor(Color.darkGray);       
		        g2.fillRect(70 ,80, 25, 15);
	     }
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }

	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
	    }

	    public void addGui(LandlordGui gui) 
	    {
	        guis.add(gui);
	    }
	    public void addGui(OccupantGui gui)
	    {
	    	guis.add(gui);
	    }

		public HomePanel getHomeP() 
		{
			return home;
		}
	
	}


