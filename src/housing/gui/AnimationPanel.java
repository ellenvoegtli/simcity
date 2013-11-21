package housing.gui;

import javax.swing.*;

	import java.awt.*;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.util.List;
	import java.util.ArrayList;
public class AnimationPanel extends JPanel implements ActionListener
{
	

	    private final int WINDOWX = 550;
	    private final int WINDOWY = 350;
	    private int tableX = 200;
	    private int tableY = 350;
	    private int applianceWidth = 25;
	    private int applianceHeight = 25;
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

	        //Clear the screen by painting a rectangle the size of the frame
	        g2.setColor(getBackground());
	        g2.fillRect(0, 0, WINDOWX, WINDOWY );

	        //Here is the table
	        g2.setColor(Color.BLACK);       
	        g2.fillRect(200 ,25, applianceWidth, applianceHeight);

	        g2.fillRect(250, 25, applianceWidth, applianceHeight);//200 and 250 need to be table params

	        
	        g2.fillRect(300,  25,  applianceWidth,  applianceHeight);

	        
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


