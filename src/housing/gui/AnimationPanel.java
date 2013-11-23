package housing.gui;

import javax.swing.*;

	import java.awt.*;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.util.List;
	import java.util.ArrayList;
public class AnimationPanel extends JPanel implements ActionListener
{
	

	    private final int WINDOWX = 950;
	    private final int WINDOWY = 350;
	    
	    private int applianceWidth = 20;
	    private int applianceHeight = 15;
	    private int tableWidth = 30;
	    private int tableHeight = 20;
	    private Image bufferImage;
	    private Dimension bufferSize;

	    private List<Gui> guis = new ArrayList<Gui>();

	    public AnimationPanel() {
	    	setSize(WINDOWX, WINDOWY);
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

	        //Clear the screen by painting a rectangle the size of the frame
	        g2.setColor(getBackground());
	        g2.fillRect(0, 0, WINDOWX, WINDOWY );

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
	
	}


