package mainCity.restaurants.enaRestaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 850;
    private final int WINDOWY = 850;
    private int tableX = 200;
    private int tableY = 350;
    private int tableWidth = 50;
    private int tableHeight = 50;
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
        g2.setColor(Color.ORANGE);       
        g2.fillRect(50 ,tableY, tableWidth, tableHeight);

        g2.fillRect(tableX, tableY, tableWidth, tableHeight);//200 and 250 need to be table params

        
        g2.fillRect(350,  tableY,  tableWidth,  tableHeight);

        g.setColor(Color.RED);
        g.fillRect(275, 490, 90, 25);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(375, 490, 35, 105);
        
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

    public void addGui(EnaCustomerGui gui) 
    {
        guis.add(gui);
    }
public void addGui(EnaWaiterGui gui)
{
	guis.add(gui);
}
    public void addGui(EnaHostGui gui) 
    {
        guis.add(gui);
    }
    public void addGui(EnaCookGui gui)
    {
    	guis.add(gui);
    }
}

