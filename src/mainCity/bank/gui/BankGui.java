package mainCity.bank.gui;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * 
 * Contains the main frame and subsequent panels
 */
public class BankGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Bank Animation");
	BankAnimationPanel bankAnimationPanel = new BankAnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private BankPanel bankPanel = new BankPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    //private JPanel infoPanel;
    //private JPanel myPanel;
    //private JLabel myLabel;
    //private JLabel infoLabel; //part of infoPanel
    //private JCheckBox stateCB;//part of infoLabel
    //private JButton askBreak = new JButton("Ask4Break");

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public BankGui() {
        int WINDOWX = 500;
        int WINDOWY = 500;

        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        animationFrame.setVisible(true);
    	animationFrame.add(bankAnimationPanel);
    	
    	
    	
    	setBounds(0, 0, WINDOWX, WINDOWY);

    	setLayout(new BoxLayout((Container) getContentPane(), 
                BoxLayout.Y_AXIS));
       
        Dimension bankrestDim = new Dimension((int) (WINDOWX), (int) (WINDOWY ));
        bankPanel.setPreferredSize(bankrestDim);
        bankPanel.setMinimumSize(bankrestDim);
        bankPanel.setMaximumSize(bankrestDim);
        add(bankPanel);
        
    
        add(bankAnimationPanel,BorderLayout.CENTER);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    
   
    public static void main(String[] args) {
        BankGui gui = new BankGui();
        gui.setTitle("csci201 Bank");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
