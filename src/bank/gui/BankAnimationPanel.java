package bank.gui;

import javax.swing.*;

import mainCity.gui.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class BankAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 500;
    private final int WINDOWY = 500;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int  X = 200;
    static final int  Y = 350;
    static final int width = 50;
    static final int height = 50;

    private List<Gui> guis = new ArrayList<Gui>();

    public BankAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
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

        //Teller Area
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(X, Y, 100, 100);//200 and 250 need to be table params
        
       
        //Draw customer waiting area
        g2.setColor(Color.cyan);
        g2.fillRect( 0, 400, 100, 100 );
        
       
        

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

    
    
}
