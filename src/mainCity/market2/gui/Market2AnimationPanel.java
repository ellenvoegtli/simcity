package mainCity.market2.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import mainCity.gui.*;


public class Market2AnimationPanel extends CityCard implements ActionListener {
	private Market2Panel market = new Market2Panel(this);
    private static final int WINDOWX = 500, WINDOWY = 370;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int timerStart = 10;
    
    static final int agentWidth = 20, agentHeight = 20;
    static final int stationWidth = 50, stationHeight = 17;
    static final int cashierWidth = 20, cashierHeight = 50;
    static final int deliveryWidth = 20, deliveryHeight = 50;
    static final int stockRoomWidth = 25, stockRoomHeight = 50;
    
    static final int originX = 0, originY = 0;
    static final int deliveryX = WINDOWX - deliveryWidth, deliveryY = WINDOWY/2 + 2*deliveryHeight;
    static final int cashierX = 0, cashierY = 250;
    static final int stockRoomX = stockRoomWidth*2, stockRoomY = WINDOWY - stockRoomWidth;
    
    
    
	Map<Integer, Integer> stationX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> stationY = new TreeMap<Integer, Integer>();

    private List<Gui> guis = new ArrayList<Gui>();

    public Market2AnimationPanel(CityGui gui) {
    	super(gui);
    	//ContactList.getInstance().setMarket(market);
    	
    	stationX.put(1, 150);	//station 1
    	stationY.put(1, 50);
        stationX.put(2, 250);	//station 2
        stationY.put(2, 50);
        stationX.put(3, 350);	//station 3
        stationY.put(3, 50);
        stationX.put(4, 450);	//station 4
        stationY.put(4, 50);
        stationX.put(5, 150);	//station 5
        stationY.put(5, 150);
    	stationX.put(6, 250);	//station 6
    	stationY.put(6, 150);
    	stationX.put(7, 350);	//station 7
    	stationY.put(7, 150);
    	stationX.put(8, 450);	//station 8
    	stationY.put(8, 150);
    	
    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(timerStart, this);
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
        //Here are the stations
        g2.setColor(purple);
        g2.fillRect(stationX.get(1), stationY.get(1), stationWidth, stationHeight);
        g2.fillRect(stationX.get(2), stationY.get(2), stationWidth, stationHeight);
        g2.fillRect(stationX.get(3), stationY.get(3), stationWidth, stationHeight);
        g2.fillRect(stationX.get(4), stationY.get(4), stationWidth, stationHeight);
        g2.fillRect(stationX.get(5), stationY.get(5), stationWidth, stationHeight);
        g2.fillRect(stationX.get(6), stationY.get(6), stationWidth, stationHeight);
        g2.fillRect(stationX.get(7), stationY.get(7), stationWidth, stationHeight);
        g2.fillRect(stationX.get(8), stationY.get(8), stationWidth, stationHeight);
        
        
        //cashier
        Color green = new Color(46, 139, 87);
        g2.setColor(green);
        g2.fillRect(cashierX, cashierY, cashierWidth, cashierHeight);
        
        
        //delivery man's station
        Color pink = new Color(247, 26, 152);
        g2.setColor(pink);
        g2.fillRect(deliveryX, deliveryY, deliveryWidth, deliveryHeight);
        
        Color yellow = new Color(240, 246, 78);
        g2.setColor(yellow);
        g2.fillRect(stockRoomX, stockRoomY, stockRoomWidth, stockRoomHeight);
        
        

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
    
    public void backgroundUpdate() {
    	for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }
    public void addGui(EmployeeGui gui){
    	guis.add(gui);
    }
}
