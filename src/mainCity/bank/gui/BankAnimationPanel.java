package mainCity.bank.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import mainCity.gui.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class BankAnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 500;
    private final int WINDOWY = 500;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int  X = 0;
    static final int  Y = 0;
    static final int width = 50;
    static final int height = 50;
    
    private BufferedImage teller_areaImg = null;
    private BufferedImage banker_areaImg = null;

    private List<Gui> guis = new ArrayList<Gui>();

    public BankAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    	StringBuilder path = new StringBuilder("imgs/");
	    try {
			teller_areaImg = ImageIO.read(new File(path.toString() + "teller_area.png"));
			banker_areaImg = ImageIO.read(new File(path.toString() + "banker_area.png"));
		} catch (IOException e) {
			
			e.printStackTrace();
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

        //Teller Area
        g2.setColor(Color.DARK_GRAY);
        g.drawImage(teller_areaImg,X+100, Y+75,null);
        //g2.fillRect(X+100, Y+75, 125, 40);//200 and 250 need to be table params
        g.drawImage(teller_areaImg,X+275, Y+75,null);
        //g2.fillRect(X+275, Y+75, 125, 40);
        
        //Banker Area
        g.drawImage(banker_areaImg, 410, 315, null);
        g2.drawLine(350,230 , 500, 230);
        
        g2.drawLine(350,230 , 350, 300);
        g2.drawLine(350,400 , 350, 500);
        g2.setColor(Color.RED);
        g2.drawString("Account Services", 380, 260);
        
        g2.setColor(Color.BLUE);
        //g2.fillRect(410, 315, 30, 90);
        
        
        //Draw customer waiting area
        g2.setColor(Color.orange);
        g2.fillRect( 0, 350, 100, 150 );
        
       
        

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
    
    public void addGui(BankCustomerGui gui){
    	guis.add(gui);
    }

    public void addGui(BankTellerGui gui){
    	guis.add(gui);
    }
    
}
