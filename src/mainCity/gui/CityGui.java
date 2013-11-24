package mainCity.gui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import mainCity.gui.AnimationPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.contactList.*;
import mainCity.gui.trace.*;

import java.awt.*;
import java.awt.event.*;

public class CityGui extends JFrame implements ActionListener, KeyListener{
	
	private AnimationPanel animationPanel = new AnimationPanel(); 
	private CityView view = new CityView(this);
	private TracePanel tracePanel1;
	private TracePanel tracePanel2;
	private TracePanel tracePanel3;
	private TracePanel tracePanel4;
	private TracePanel tracePanel5;
	private CityPanel cityPanel = new CityPanel(this);
	private JPanel mainPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel detailedPanel = new JPanel();

	//====Control panel components====
	private JPanel controlPanel = new JPanel();
	private GroupLayout layout = new GroupLayout(controlPanel);
	private JLabel nameFieldLabel = new JLabel("Enter name: ");
	private JTextField nameField = new JTextField(100);
	private JLabel moneyFieldLabel = new JLabel("Enter starting $: ");
	private JTextField moneyField = new JTextField(100);
	private JLabel occupationMenuLabel = new JLabel("Choose occupation: ");
	private JComboBox occupationMenu;
	private JLabel AMshiftLabel = new JLabel("Choose shift: ");
	private JLabel PMshiftLabel = new JLabel(" ");
	private JRadioButton AMshiftButton = new JRadioButton("AM shift");
	private JRadioButton PMshiftButton = new JRadioButton("PM shift");
	private ButtonGroup shiftGroup = new ButtonGroup();
	private JButton addPersonButton = new JButton("Add new person");
	
	public CityGui() { 
		
		int WINDOWX = 1300; 
		int WINDOWY = 600;

		animationPanel.setGui(this);
		
		setBounds(50, 50, WINDOWX, WINDOWY+150);
		setLayout(new BorderLayout());
		
		//---CONTROL PANEL BEGIN---//
		nameFieldLabel.setVisible(true);
        Dimension nameDim = new Dimension(150, 30);
		nameField.setPreferredSize(nameDim);
		nameField.setMinimumSize(nameDim);
		nameField.setMaximumSize(nameDim);
		
		moneyFieldLabel.setVisible(true);
		Dimension moneyDim = new Dimension(150, 30);
		moneyField.setPreferredSize(moneyDim);
		moneyField.setMinimumSize(moneyDim);
		moneyField.setMaximumSize(moneyDim);
		
		String[] occupationStrings = {"Random", "Rich (no occupation)", "Bank manager", "Bank teller", "Banker", 
				"Restaurant host", "Restaurant waiter", "Restaurant cook", "Restaurant cashier", 
				"Market greeter", "Market employee", "Market cashier", "Market delivery man"
		};
		occupationMenu = new JComboBox(occupationStrings);
		Dimension occupationDim = new Dimension(150, 30);
		occupationMenu.setPreferredSize(occupationDim);
		occupationMenu.setMinimumSize(occupationDim);
		occupationMenu.setMaximumSize(occupationDim);
		occupationMenu.setSelectedIndex(0);
		occupationMenu.addActionListener(this);
        occupationMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        //AM/PM shift
        AMshiftLabel.setVisible(true);
        AMshiftButton.setSelected(false);
        PMshiftButton.setSelected(false);
        shiftGroup.add(AMshiftButton);
        shiftGroup.add(PMshiftButton);
		
		//===GROUP LAYOUT FOR CONTROL PANEL====
		controlPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(nameFieldLabel).addComponent(moneyFieldLabel).addComponent(occupationMenuLabel).
	            addComponent(AMshiftLabel).addComponent(PMshiftLabel));
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(nameField).addComponent(moneyField).addComponent(occupationMenu).addComponent(AMshiftButton).
	            addComponent(PMshiftButton));
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(nameFieldLabel).addComponent(nameField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(moneyFieldLabel).addComponent(moneyField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(occupationMenuLabel).addComponent(occupationMenu));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(AMshiftLabel).addComponent(AMshiftButton));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(PMshiftLabel).addComponent(PMshiftButton));
		layout.setVerticalGroup(vGroup);
	   //====END GROUP LAYOUT=====
		
		
		
		//---MAIN PANEL BEGIN---//
		setBounds(50, 50, WINDOWX, WINDOWY+150);
		setLayout(new BorderLayout());
        
        Dimension mainDim = new Dimension((int) (WINDOWX * .6), WINDOWY);
        mainPanel.setPreferredSize(mainDim);
        mainPanel.setMinimumSize(mainDim);
        mainPanel.setMaximumSize(mainDim);
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        add(mainPanel, BorderLayout.CENTER);
        	//Main City View
        Dimension animationDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .8));
        getAnimationPanel().setPreferredSize(animationDim);
        getAnimationPanel().setMinimumSize(animationDim);
        getAnimationPanel().setMaximumSize(animationDim);
        getAnimationPanel().setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(getAnimationPanel(), BorderLayout.CENTER);
        	//Control Panel
        Dimension controlDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .4));
        controlPanel.setPreferredSize(controlDim);
        controlPanel.setMinimumSize(controlDim);
        controlPanel.setMaximumSize(controlDim);
        //detailedPanel.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
		
		//---LEFT PANEL BEGIN---//
		//Entire Left Panel Sizing
		Dimension leftDim = new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .5));
		leftPanel.setPreferredSize(leftDim);
		leftPanel.setMinimumSize(leftDim);
		leftPanel.setMaximumSize(leftDim);
		leftPanel.setBorder(BorderFactory.createEtchedBorder());
        add(leftPanel, BorderLayout.WEST);
		
        //Detailed Panel Sizing
        Dimension detailDim = new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .6));
        detailedPanel.setPreferredSize(detailDim);
        detailedPanel.setMinimumSize(detailDim);
        detailedPanel.setMaximumSize(detailDim);
        detailedPanel.add(getView());
        leftPanel.add(detailedPanel, BorderLayout.CENTER);
        
        //=============== TRACE PANEL ====================//
        JTabbedPane tabbedPane = new JTabbedPane();
        tracePanel1 = new TracePanel();
        tracePanel1.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel1.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel1.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel1.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel1.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel1.showAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags  
        tracePanel1.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel1.hideAlertsWithTag(AlertTag.BANK);
        tracePanel1.hideAlertsWithTag(AlertTag.RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel1);
        tabbedPane.addTab("PERSON", tracePanel1);
        
        tracePanel2 = new TracePanel();
        tracePanel2.setPreferredSize(new Dimension((int) (WINDOWY * .4), (int) (WINDOWY * .4)));
        tracePanel2.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel2.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel2.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel2.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel2.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel2.showAlertsWithTag(AlertTag.MARKET);
        tracePanel2.hideAlertsWithTag(AlertTag.BANK);
        tracePanel2.hideAlertsWithTag(AlertTag.RESTAURANT);
        
        
        AlertLog.getInstance().addAlertListener(tracePanel2);
        tabbedPane.addTab("MARKET", tracePanel2);
        
        tracePanel3 = new TracePanel();
        tracePanel3.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel3.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel3.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel3.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel3.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel3.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel3.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel3.showAlertsWithTag(AlertTag.BANK);
        tracePanel3.hideAlertsWithTag(AlertTag.RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel3);
        tabbedPane.addTab("BANK", tracePanel3);
        
        
        tracePanel4 = new TracePanel();
        tracePanel4.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel4.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel4.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel4.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel4.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel4.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel4.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel4.hideAlertsWithTag(AlertTag.BANK);
        tracePanel4.showAlertsWithTag(AlertTag.RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel4);
        tabbedPane.addTab("RESTAURANT", tracePanel4);
        
        leftPanel.add(tabbedPane, BorderLayout.SOUTH);       
	}
	
	public void actionPerformed(ActionEvent e){
		JComboBox cb = (JComboBox)e.getSource();
        String petName = (String)cb.getSelectedItem();
        //updateLabel(petName);
	}
	
	
	public static void main(String[] args) {
        CityGui gui = new CityGui();
        gui.setTitle("SimCity201");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
	
	public AnimationPanel getAnimationPanel() {
		return animationPanel;
	}

	public CityView getView() {
		return view;
	}

	public void setView(CityView view) {
		this.view = view;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		/*
		if (e.getSource() == nameField){
			hungryB.setEnabled(true);
			hungryB.setSelected(false);
		}
		else if (e.getSource() == moneyField){
			onBreakB.setEnabled(true);
			onBreakB.setSelected(false);
		}*/
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
