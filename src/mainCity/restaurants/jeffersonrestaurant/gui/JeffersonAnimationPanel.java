package mainCity.restaurants.jeffersonrestaurant.gui;

import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class JeffersonAnimationPanel extends CityCard implements ActionListener {

    private final int WINDOWX = 650;
    private final int WINDOWY = 550;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int  X = 200;
    static final int  Y = 300;
    static final int width = 50;
    static final int height = 50;
    private JeffersonRestaurantPanel JRestPanel = new JeffersonRestaurantPanel(this);
    

    private List<Gui> guis = new ArrayList<Gui>();

    public JeffersonAnimationPanel(CityGui gui) {
    	super(gui);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
        ContactList.getInstance().setJeffersonRestaurant(JRestPanel);
 
    	Timer timer = new Timer(30, this );
    	timer.start();
    }

    public void backgroundUpdate() {
    	for(Gui guit : guis) {
            if (guit.isPresent()) {
                guit.updatePosition();
            }
        }
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
        g2.setColor(Color.ORANGE);
        g2.fillRect(X, Y, width, height);//200 and 250 need to be table params
        
        g2.fillRect(X+100, Y, width, height);//200 and 250 need to be table params
       
        g2.fillRect(X+200, Y, width, height);//200 and 250 need to be table params
        
        //Draw customer waiting area
        
        g2.fillRect( 0, 0, 2*width, 3*width );
        
        g2.setColor(Color.WHITE);
        g2.fillRect(230, 180, 200, 60);
        
        g2.setColor(Color.cyan);
        
        g2.fillRect(240, 200,30,30);
        g2.drawString("Plating", 240, 200);
        
        g2.setColor(Color.BLUE);
        g2.fillRect(400, 200,30,30);
        g2.drawString("Cooking", 400, 200);
        

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

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    public void addGui(CookGui gui) {
        guis.add(gui);
    }
    
}
