package housing.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;
import mainCity.gui.CityView;

	import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class HomeAnimationPanel extends CityCard implements ActionListener
{
	

	    private  int WINDOWX = 520;
	    private  int WINDOWY = 360;
	    private int applianceWidth = 20;
	    private int applianceHeight = 15;
	    private int tableWidth = 30;
	    private int tableHeight = 20;
	    private Image bufferImage;
	    private Dimension bufferSize;
	    private boolean ty;
		private HomePanel home = new HomePanel(this);
		
		
	    private BufferedImage sinkImg = null;
	    private BufferedImage stoveImg = null;
	    private BufferedImage fridgeImg = null;
	    private BufferedImage tableImg = null;
	    private BufferedImage couchImg = null;
	    private BufferedImage tvImg = null;
	    private BufferedImage bedImg = null;
	    private BufferedImage booksImg = null;
	    private BufferedImage computerImg = null;
	    
	    private List<Gui> guis = new ArrayList<Gui>();
	    private List<Gui> personGuis = new ArrayList<Gui>();

    

public HomeAnimationPanel(CityGui cg, boolean type) {
	    	super(cg);
	    	this.ty = type;
	    	if(type==true)
	    		ContactList.getInstance().setHome(home);
	    	
	    	StringBuilder path = new StringBuilder("imgs/");
			try {
				sinkImg = ImageIO.read(new File(path.toString() + "sink.png"));
				stoveImg = ImageIO.read(new File(path.toString() + "stove.png"));
				fridgeImg = ImageIO.read(new File(path.toString() + "fridge.png"));
				tableImg = ImageIO.read(new File(path.toString() + "table.png"));
				couchImg = ImageIO.read(new File(path.toString()+ "couch.png"));
				tvImg = ImageIO.read(new File(path.toString()+ "tv.png"));
				bedImg = ImageIO.read(new File(path.toString()+ "bed.png"));
				booksImg = ImageIO.read(new File(path.toString()+ "books.png"));
				computerImg = ImageIO.read(new File(path.toString()+ "computer.png"));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			
	    	setSize(520, 360);
	        setVisible(true);
	        bufferSize = this.getSize();
	 
	    	Timer timer = new Timer(7,this);
	    	timer.start();
	    }



		public void actionPerformed(ActionEvent e) {
			repaint();  //Will have paintComponent called
		}

		
	    public void paintComponent(Graphics g)
	    {
	        Graphics2D g2 = (Graphics2D)g;	        
		 
	        
	    if(ty)
	     
		     {
	        //Clear the screen by painting a rectangle the size of the frame
	        g2.setColor(getBackground());
	        g2.fillRect(0, 0, 540, 380 );

	        //Here is the table
	        g2.setColor(Color.BLACK);       
	        
	        //g2.fillRect(200 ,25, applianceWidth, applianceHeight);
	        g2.drawString("stove", 200, 15);
	        
	        //g2.fillRect(250, 25, applianceWidth, applianceHeight);//200 and 250 need to be table params
	        g2.drawString("sink", 250, 15);

	        
	        //g2.fillRect(300,  25,  applianceWidth,  applianceHeight);
	        g2.drawString("fridge", 300, 15);
	        
	        //Upgraded Gui
	        g.drawImage(sinkImg,250,20,null);
	        g.drawImage(stoveImg,200,20,null);
	        g.drawImage(fridgeImg,303,20,null);
	        ///////
	        g2.setColor(Color.ORANGE);       

	        //g2.fillRect(250, 150, tableWidth, tableHeight);//200 and 250 need to be table params
	        g.drawImage(tableImg,250,150,null);
	        
	        g2.setColor(Color.lightGray);       
	        //g2.fillRect(50 ,200, 50, 20);
	        g.drawImage(couchImg, 50,200,null);
	        
	        g2.setColor(Color.darkGray);       

	        //g2.fillRect(70 ,80, 25, 15);
	        g.drawImage(tvImg, 70,80,null);
	        
	        g.drawImage(bedImg,400,200,null);
	        g.drawImage(booksImg,430,200,null);
	        g.drawImage(computerImg,415,100,null);
	        
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
		        
		        g2.setColor(Color.BLUE);       
		        g2.fillRect(415, 63, 20, 13);//200 and 250 need to be table params
		        
		        g2.setColor(Color.MAGENTA);       
		        g2.fillRect(50 ,55, 50, 20);
		        
		        g2.setColor(Color.darkGray);       
		        g2.fillRect(65 ,10, 25, 10);
	     }
	       
	   

	        for(Gui gui : personGuis)
		     {
		            if (gui.isPresent()) 
		            {
		                gui.updatePosition();
		            }
		       }

		        for(Gui gui : guis) 
		        {
		            if (gui.isPresent())
		            {
		                gui.draw(g2);
		            }
		        }
		        
		        for(Gui gui : personGuis) 
		        {
		            if (gui.isPresent())
		            {
		                gui.draw(g2);
		            }
		        }
		        
	 }
	    
	    public void backgroundUpdate()
	    {
	    	for (Gui gui : personGuis)
	    	{

	    		if(gui.isPresent())
	    		{
	    			gui.updatePosition();
	    		}
	    	}
	    }

	    public void addGui(LandlordGui gui) 
	    {
	        personGuis.add(gui);
	    }
	    public void addGui(OccupantGui gui)
	    {
	    	
	    	System.out.println("gui list size " +personGuis.size());
	    	personGuis.add(gui);
	    }

		public HomePanel getHomeP() 
		{
			return home;
		}

		public void setGuis(List<Gui> guis) {
			this.guis = guis;
		}

		public void setHomeP(HomePanel home) {
			this.home = home;
		}
	    
		@Override
	    public void clearPeople() {
	    	personGuis.clear();
	    }
	}


