package mainCity.market.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AnimationPanel extends JPanel implements ActionListener {

    private static final int WINDOWX = 550;
    private static final int WINDOWY = 350;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int timerStart = 10;

    static final int tableWidth = 50;
    static final int tableHeight = 50;
    static final int originX = 0;
    static final int originY = 0;
    
    //kitchen/cook location variables
    static final int kitchenHeight = 140;
    static final int kitchenWidth = 35;
    static final int grillX = WINDOWX - 30;
    static final int grillY = WINDOWY/2 - 60;
    static final int platingX = WINDOWX - 90;
    static final int platingY = WINDOWY/2 - 60;
    
	Map<Integer, Integer> stationX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> stationY = new TreeMap<Integer, Integer>();

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	stationX.put(1, 200);
    	stationY.put(1, 100);
         
         stationX.put(2, 250);
         stationY.put(2, 100);
         
         stationX.put(3, 300);
         stationY.put(3, 100);
         
         stationX.put(4, 350);
         stationY.put(4, 100);
    	
    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(timerStart, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(originX, originY, WINDOWX, WINDOWY);

        Color purple = new Color(147, 112, 219);
        //Here is the table
        g2.setColor(purple);
        g2.fillRect(stationX.get(1), stationY.get(1), tableWidth, tableHeight);

        
        g2.setColor(purple);
        g2.fillRect(stationX.get(2), stationY.get(2), tableWidth, tableHeight);
        
        g2.setColor(purple);
        g2.fillRect(stationX.get(3), stationY.get(3), tableWidth, tableHeight);
        
        
        g2.setColor(purple);
        g2.fillRect(stationX.get(4), stationY.get(4), tableWidth, tableHeight);
        
        //cashier
        Color grun = new Color(46, 139, 87);
        g2.setColor(grun);
        g2.fillRect(20, 250, 25, 50);
        
        

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

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }
    /*
    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    */
    public void addGui(EmployeeGui gui){
    	guis.add(gui);
    }
}
