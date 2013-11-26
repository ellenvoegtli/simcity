package housing.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mainCity.contactList.ContactList;
import mainCity.gui.AnimationPanel;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.gui.EnaAnimationPanel;
import mainCity.restaurants.enaRestaurant.gui.EnaWaiterGui;
import role.Role;
import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.gui.OccupantGui;
import housing.gui.LandlordGui;



public class ApartAnimationPanel extends HomeAnimationPanel{


    private static final  int WINDOWX = 260;
	private static final int WINDOWY = 180;
    private Image bufferImage;
    private Dimension bufferSize;
    private int applianceWidth = 20;
    private int applianceHeight = 15;
    private int tableWidth = 30;
    private int tableHeight = 20;
    private CityGui gui;
	private HomePanel home = new HomePanel(this);
	Dimension dim = new Dimension(WINDOWX,50);

    private List<HomeAnimationPanel> apts = new ArrayList<HomeAnimationPanel>();
    
    private HomeAnimationPanel apt1 = new HomeAnimationPanel(gui, false);
    private HomeAnimationPanel apt2 = new HomeAnimationPanel(gui, false);
    private HomeAnimationPanel apt3 = new HomeAnimationPanel(gui, false);
    private HomeAnimationPanel apt4 = new HomeAnimationPanel(gui, false);
    
    //protected List<Gui> guis = new ArrayList<Gui>();

	public ApartAnimationPanel(CityGui gui) 
	{
		super(gui, false);
		setPreferredSize(new Dimension(520,380));

		setSize(520, 380);
        setVisible(true);
		setLayout( new GridLayout(6,0,0,0));
	       
		
		bufferSize = this.getSize();

	
		
        apt1.setBorder(BorderFactory.createRaisedBevelBorder());
		apt1.setPreferredSize(dim);

        apt2.setBorder(BorderFactory.createRaisedBevelBorder());
		apt2.setPreferredSize(dim);
	
        apt3.setBorder(BorderFactory.createRaisedBevelBorder());
		apt3.setPreferredSize(dim);

        apt4.setBorder(BorderFactory.createRaisedBevelBorder());
		apt4.setPreferredSize(dim);
	
			add(apt1);
			add(apt2);
			add(apt3);
			add(apt4);

	    apts.add(apt1);
	    apts.add(apt2);
	    apts.add(apt3);
	    apts.add(apt4);
		
	    ContactList.getInstance().setHome(apt1.getHomeP());
	    ContactList.getInstance().setHome(apt2.getHomeP());
	    ContactList.getInstance().setHome(apt3.getHomeP());
	    ContactList.getInstance().setHome(apt4.getHomeP());

		//ContactList.getInstance().setApart(apts);
        
        
	}
	

	/*public void actionPerformed(ActionEvent e) 
	{
		apt1.actionPerformed(e);
		apt2.actionPerformed(e);
		apt3.actionPerformed(e);
		apt4.actionPerformed(e);

	}*/


}
