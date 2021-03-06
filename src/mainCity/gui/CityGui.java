package mainCity.gui;

import housing.OccupantRole;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.AnimationPanel;
import mainCity.PersonAgent.ActionType;
import mainCity.gui.trace.*;
import mainCity.gui.ListPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;


public class CityGui extends JFrame implements ActionListener, KeyListener{	
	private AnimationPanel animationPanel = new AnimationPanel(); 
	private CityView view = new CityView(this);
	
	private Vector<PersonAgent> people = new Vector<PersonAgent>();
	private ListPanel personPanel = new ListPanel(this);
	private CityPanel cityPanel = new CityPanel(this);
	private JPanel mainPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel detailedPanel = new JPanel();
	
	private TracePanel tracePanel1;
	private TracePanel tracePanel2;
	private TracePanel tracePanel3;
	private TracePanel tracePanel4;
	private TracePanel tracePanel5;
	private TracePanel tracePanel6;
	private TracePanel tracePanel7;
	private TracePanel tracePanel8;
	private TracePanel tracePanel9;

	
	private JPanel controlPanel = new JPanel();
	//=================Create panel components=========================
	private JPanel subControlPanel1 = new JPanel();
	
	private GroupLayout layout = new GroupLayout(subControlPanel1);
	private JLabel nameFieldLabel = new JLabel("Enter name: ");
	private JTextField nameField = new JTextField(100);
	private JLabel moneyFieldLabel = new JLabel("Enter starting $: ");
	private JTextField moneyField = new JTextField(100);
	private JLabel occupationMenuLabel = new JLabel("Choose occupation: ");
	private JComboBox occupationMenu;
	private JLabel shiftMenuLabel = new JLabel("Choose shift: ");
	private JComboBox shiftMenu;
	private JLabel housingMenuLabel = new JLabel("Choose housing: ");
	private JComboBox housingMenu;
	private JLabel carMenuLabel = new JLabel("Car or no car? : ");
	private JComboBox carMenu;
	
	private JLabel blankLabel = new JLabel(" ");
	private JButton addPersonButton = new JButton("Create person");
	
	//=================Control panel components========================
	private JPanel subControlPanel2 = new JPanel();
	
	private JLabel personLabel = new JLabel("Selected person: ");
	private JLabel infoLabel = new JLabel("");
	private JPanel infoPanel = new JPanel();
	private GroupLayout layout2 = new GroupLayout(infoPanel);
	private JLabel restLabel = new JLabel("Eat at restaurant: ");
	private JComboBox restaurantMenu; 
	private JButton restaurantButton = new JButton("Go");
	private JLabel orLabel = new JLabel("OR: ");
	private JButton homeButton = new JButton("Eat at home");
	private JLabel workLabel = new JLabel("Go to work?");
	private JCheckBox workCB = new JCheckBox();
	
	private JLabel breakLabel = new JLabel("");
	private JButton breakButton = new JButton("Break something");
	private JButton marketButton = new JButton("Go to market");
	private JButton bankButton = new JButton("Go to bank");
	
	//=================Scenario Hack panel components========================
	private JPanel subControlPanel3 = new JPanel();	
	
	private GroupLayout layout3 = new GroupLayout(subControlPanel3);
	private JLabel scenario1Label = new JLabel("Scenario 1: All employees/many rich (Default)");
	private JButton scenario1Button = new JButton("Run");
	private JLabel scenario2Label = new JLabel("Scenario 2: Norm: All employees/1 rich");			//DONE (working) 
	private JButton scenario2Button = new JButton("Run");
	private JLabel scenario3Label = new JLabel("Scenario 3: Norm: All employees/3 rich");			//DONE (working)
	private JButton scenario3Button = new JButton("Run");
	private JLabel scenario4Label = new JLabel("Scenario 4: Norm: Market/restaurant deliveries");	//DONE (working)
	private JButton scenario4Button = new JButton("Run");
	private JLabel scenario5Label = new JLabel("Scenario 5: Non-norm: Workplaces closing");			//DONE (working)
	private JButton scenario5Button = new JButton("Run");
	private JLabel scenario6Label = new JLabel("Scenario 6: Non-norm: Market delivery fails");		//DONE (working)
	private JButton scenario6Button = new JButton("Run");
	private JLabel scenario7Label = new JLabel("Scenario 7: Non-norm: Vehicle accident");
	private JButton scenario7Button = new JButton("Run");
	private JLabel scenario8Label = new JLabel("Scenario 8: Non-norm: Vehicle hits person");
	private JButton scenario8Button = new JButton("Run");
	private JLabel scenario9Label = new JLabel("Scenario 9: Non-norm: Weekend");					//DONE (working)
	private JButton scenario9Button = new JButton("Run");
	private JLabel scenario10Label = new JLabel("Scenario 10: Non-norm: Changing shifts: M/D/J REST");
	private JButton scenario10Button = new JButton("Run");
	private JLabel scenario11Label = new JLabel("Scenario 11: Non-norm: Changing shifts: E/E REST");	//DONE
	private JButton scenario11Button = new JButton("Run");
	private JLabel scenario12Label = new JLabel("Scenario 12: Non-norm: Changing shifts: MARKET/BANK");	//WORKING (without bank)
	private JButton scenario12Button = new JButton("Run");
	private JLabel scenario13Label = new JLabel("Scenario 13: Housing");
	private JButton scenario13Button = new JButton("Run"); 
	
	
	//=================Restaurant Inventory Hack panel components========================
	private JPanel subControlPanel4 = new JPanel();	
	
	private GroupLayout layout4 = new GroupLayout(subControlPanel4);
	private JLabel ellenRestLabel = new JLabel("Ellen's Restaurant: ");
	private JButton ellenRestButton = new JButton("Deplete all");
	private JLabel davidRestLabel = new JLabel("David's Restaurant: ");
	private JButton davidRestButton = new JButton("Deplete all");
	private JLabel enaRestLabel = new JLabel("Ena's Restaurant: ");
	private JButton enaRestButton = new JButton("Deplete all");
	private JLabel marcusRestLabel = new JLabel("Marcus' Restaurant: ");
	private JButton marcusRestButton = new JButton("Deplete all");
	private JLabel jeffersonRestLabel = new JLabel("Jefferson's Restaurant: ");
	private JButton jeffersonRestButton = new JButton("Deplete all");
	
	private Object currentPerson;
	public boolean collision = false;
	
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
		//moneyField.addKeyListener(this);
		
		
		String[] occupationStrings = {"rich", "jeffersonHost", "jeffersonCashier", "jeffersonCook", "jeffersonWaiter", "jeffersonSharedWaiter",
				"marcusHost", "marcusCashier", "marcusCook", "marcusWaiter", "marcusShareWaiter", 
				"enaHost", "enaCashier", "enaCook", "enaWaiter", "enaSharedWaiter",
				"ellenHost", "ellenCashier", "ellenCook", "ellenWaiter", "ellenShareWaiter",
				 "davidHost", "davidCashier", "davidCook", "davidWaiter", "davidSharedWaiter",
				 "marketGreeter", "marketCashier", "marketDeliveryMan", "marketEmployee",
				 "market2Greeter", "market2Cashier", "market2DeliveryMan", "market2Employee",
				 "bankManager", "bankTeller", "banker"
		};
		occupationMenu = new JComboBox(occupationStrings);
		Dimension occupationDim = new Dimension(150, 30);
		occupationMenu.setPreferredSize(occupationDim);
		occupationMenu.setMinimumSize(occupationDim);
		occupationMenu.setMaximumSize(occupationDim);
		occupationMenu.setSelectedIndex(0);
		occupationMenu.addActionListener(this);
		
		String[] shiftStrings = {"AM", "PM"};
		shiftMenu = new JComboBox(shiftStrings);
		Dimension shiftDim = new Dimension(150, 30);
		shiftMenu.setPreferredSize(shiftDim);
		shiftMenu.setMinimumSize(shiftDim);
		shiftMenu.setMaximumSize(shiftDim);
		shiftMenu.setSelectedIndex(0);
		shiftMenu.addActionListener(this);

		String[] housingStrings = {"House", "Apartment"};
		housingMenu = new JComboBox(housingStrings);
		Dimension housingDim = new Dimension(150, 30);
		housingMenu.setPreferredSize(housingDim);
		housingMenu.setMinimumSize(housingDim);
		housingMenu.setMaximumSize(housingDim);
		housingMenu.setSelectedIndex(0);
		housingMenu.addActionListener(this);
		
		String[] carStrings = {"Car", "No car"};
		carMenu = new JComboBox(carStrings);
		Dimension carDim = new Dimension(150, 30);
		carMenu.setPreferredSize(carDim);
		carMenu.setMinimumSize(carDim);
		carMenu.setMaximumSize(carDim);
		carMenu.setSelectedIndex(0);
		carMenu.addActionListener(this);
		
		String[] restStrings = {"Random", "David's Restaurant", "Ellen's Restaurant", "Ena's Restaurant", "Jefferson's Restaurant", "Marcus's Restaurant"};
		restaurantMenu = new JComboBox(restStrings);
		Dimension restDim = new Dimension(150, 30);
		restaurantMenu.setPreferredSize(restDim);
		restaurantMenu.setMinimumSize(restDim);
		restaurantMenu.setMaximumSize(restDim);
		restaurantMenu.setSelectedIndex(0);
		restaurantMenu.addActionListener(this);
		
		
		
		//==================GROUP LAYOUT 1 FOR **CREATE** PANEL====================================
		subControlPanel1.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(nameFieldLabel).addComponent(moneyFieldLabel).addComponent(occupationMenuLabel).
	            addComponent(housingMenuLabel).addComponent(addPersonButton)
	            );
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(nameField).addComponent(moneyField).addComponent(occupationMenu).
	            addComponent(housingMenu).addComponent(blankLabel)
	            );
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(nameFieldLabel).addComponent(nameField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(moneyFieldLabel).addComponent(moneyField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(occupationMenuLabel).addComponent(occupationMenu));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(housingMenuLabel).addComponent(housingMenu));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(addPersonButton).addComponent(blankLabel));
		layout.setVerticalGroup(vGroup);
		//==================END GROUP LAYOUT 1=====================================================
		JTabbedPane controlTabbedPane = new JTabbedPane();
		controlTabbedPane.addTab("Create", subControlPanel1);
		
		occupationMenu.addActionListener(this);
		housingMenu.addActionListener(this);
		addPersonButton.addActionListener(this);
		//=======================END SUBCONTROLPANEL1===============================================
		
		//=======================GROUP LAYOUT 2 FOR **CONTROL** PANEL===============================
		infoPanel.setLayout(layout2);
		layout2.setAutoCreateGaps(true);
		layout2.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup2 = layout2.createSequentialGroup();
		hGroup2.addGroup(layout2.createParallelGroup().addComponent(personLabel).addComponent(blankLabel).
	            addComponent(restLabel).addComponent(blankLabel).addComponent(orLabel).addComponent(workLabel).
	            addComponent(breakButton).addComponent(marketButton).addComponent(bankButton)
	            );
		hGroup2.addGroup(layout2.createParallelGroup().addComponent(infoLabel).addComponent(blankLabel).
	            addComponent(restaurantMenu).addComponent(restaurantButton).addComponent(homeButton).addComponent(workCB).
	            addComponent(breakLabel).addComponent(blankLabel).addComponent(blankLabel)
	            );
		layout2.setHorizontalGroup(hGroup2);
		
		GroupLayout.SequentialGroup vGroup2 = layout2.createSequentialGroup();
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(personLabel).addComponent(infoLabel));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(blankLabel).addComponent(blankLabel));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(restLabel).addComponent(restaurantMenu));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(blankLabel).addComponent(restaurantButton));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(orLabel).addComponent(homeButton));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(workLabel).addComponent(workCB));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(breakButton).addComponent(breakLabel));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(marketButton).addComponent(blankLabel));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(bankButton).addComponent(blankLabel));
		layout2.setVerticalGroup(vGroup2);
	   //=======================END GROUP LAYOUT 2===================================================
		
		restaurantButton.addActionListener(this);
		homeButton.addActionListener(this);
		workCB.addActionListener(this);
		breakButton.addActionListener(this);
		marketButton.addActionListener(this);
		bankButton.addActionListener(this);

		
		subControlPanel2.setLayout(new GridBagLayout());
		Dimension listDim = new Dimension((int) (WINDOWX * .15), (int) (WINDOWY * .5));
        personPanel.setPreferredSize(listDim);
        personPanel.setMinimumSize(listDim);
        personPanel.setMaximumSize(listDim);
		subControlPanel2.add(personPanel);
		infoLabel.setVisible(false);
		subControlPanel2.add(infoPanel);
		controlTabbedPane.addTab("Controls", subControlPanel2);
		//=================== END SUBCONTROLPANEL2 =================================================
		
		
		//==================GROUP LAYOUT 3 FOR **SCENARIO** PANEL====================================
		subControlPanel3.setLayout(layout3);
		layout3.setAutoCreateGaps(true);
		layout3.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup3 = layout3.createSequentialGroup();
		hGroup3.addGroup(layout3.createParallelGroup().
	            addComponent(scenario1Label).addComponent(scenario2Label).addComponent(scenario3Label).
	            addComponent(scenario4Label).addComponent(scenario5Label).addComponent(scenario6Label).
	            addComponent(scenario7Label).addComponent(scenario8Label).addComponent(scenario9Label).
	            addComponent(scenario10Label).addComponent(scenario11Label).addComponent(scenario12Label).
	            addComponent(scenario13Label)
	            );
		hGroup3.addGroup(layout3.createParallelGroup().
	            addComponent(scenario1Button).addComponent(scenario2Button).addComponent(scenario3Button).
	            addComponent(scenario4Button).addComponent(scenario5Button).addComponent(scenario6Button).
	            addComponent(scenario7Button).addComponent(scenario8Button).addComponent(scenario9Button).
	            addComponent(scenario10Button).addComponent(scenario11Button).addComponent(scenario12Button).
	            addComponent(scenario13Button)
	            );
		layout3.setHorizontalGroup(hGroup3);
		
		GroupLayout.SequentialGroup vGroup3 = layout3.createSequentialGroup();
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
	            addComponent(scenario1Label).addComponent(scenario1Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
	            addComponent(scenario2Label).addComponent(scenario2Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
	            addComponent(scenario3Label).addComponent(scenario3Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
	            addComponent(scenario4Label).addComponent(scenario4Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario5Label).addComponent(scenario5Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario6Label).addComponent(scenario6Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario7Label).addComponent(scenario7Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario8Label).addComponent(scenario8Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario9Label).addComponent(scenario9Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario10Label).addComponent(scenario10Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario11Label).addComponent(scenario11Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario12Label).addComponent(scenario12Button));
		vGroup3.addGroup(layout3.createParallelGroup(Alignment.BASELINE).
				addComponent(scenario13Label).addComponent(scenario13Button));
		layout3.setVerticalGroup(vGroup3);
		//==================END GROUP LAYOUT 3=====================================================
		JScrollPane scenarioPane = new JScrollPane(subControlPanel3);
		subControlPanel3.setAutoscrolls(true);
		scenarioPane.setPreferredSize(new Dimension((int) (WINDOWX * .35), (int) (WINDOWY * .4)));
		controlTabbedPane.addTab("Scenarios", scenarioPane);
		
		scenario1Button.addActionListener(this);
		scenario2Button.addActionListener(this);
		scenario3Button.addActionListener(this);
		scenario4Button.addActionListener(this);
		scenario5Button.addActionListener(this);
		scenario6Button.addActionListener(this);
		scenario7Button.addActionListener(this);
		scenario8Button.addActionListener(this);
		scenario9Button.addActionListener(this);
		scenario10Button.addActionListener(this);
		scenario11Button.addActionListener(this);
		scenario12Button.addActionListener(this);
		scenario13Button.addActionListener(this);
		
		
		//=================== END SUBCONTROLPANEL3 =================================================
		
		
		//==================GROUP LAYOUT 4 FOR **INVENTORY** PANEL====================================
		subControlPanel4.setLayout(layout4);
		layout4.setAutoCreateGaps(true);
		layout4.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup4 = layout4.createSequentialGroup();
		hGroup4.addGroup(layout4.createParallelGroup().
	            addComponent(davidRestLabel).addComponent(ellenRestLabel).addComponent(enaRestLabel).
	            addComponent(marcusRestLabel).addComponent(jeffersonRestLabel)
	            );
		hGroup4.addGroup(layout4.createParallelGroup().
	            addComponent(davidRestButton).addComponent(ellenRestButton).addComponent(enaRestButton).
	            addComponent(marcusRestButton).addComponent(jeffersonRestButton)
	            );
		layout4.setHorizontalGroup(hGroup4);
		
		GroupLayout.SequentialGroup vGroup4 = layout4.createSequentialGroup();
		vGroup4.addGroup(layout4.createParallelGroup(Alignment.BASELINE).
	            addComponent(davidRestLabel).addComponent(davidRestButton));
		vGroup4.addGroup(layout4.createParallelGroup(Alignment.BASELINE).
	            addComponent(ellenRestLabel).addComponent(ellenRestButton));
		vGroup4.addGroup(layout4.createParallelGroup(Alignment.BASELINE).
	            addComponent(enaRestLabel).addComponent(enaRestButton));
		vGroup4.addGroup(layout4.createParallelGroup(Alignment.BASELINE).
	            addComponent(marcusRestLabel).addComponent(marcusRestButton));
		vGroup4.addGroup(layout4.createParallelGroup(Alignment.BASELINE).
				addComponent(jeffersonRestLabel).addComponent(jeffersonRestButton));
		layout4.setVerticalGroup(vGroup4);
		//==================END GROUP LAYOUT 4=====================================================
		subControlPanel4.setPreferredSize(new Dimension((int) (WINDOWX * .35), (int) (WINDOWY * .4)));
		controlTabbedPane.addTab("Inventories", subControlPanel4);
		
		davidRestButton.addActionListener(this);
		ellenRestButton.addActionListener(this);
		enaRestButton.addActionListener(this);
		marcusRestButton.addActionListener(this);
		jeffersonRestButton.addActionListener(this);

		
		
		controlPanel.add(controlTabbedPane);
		

		//---MAIN PANEL BEGIN---//
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
        
       
        //=============== TRACE PANEL ====================//
        JTabbedPane tabbedPane = new JTabbedPane();
        tracePanel1 = new TracePanel();
        tracePanel1.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel1.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel1.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel1.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel1.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel1.showAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags  
        tracePanel1.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel1.hideAlertsWithTag(AlertTag.BANK);
        
        AlertLog.getInstance().addAlertListener(tracePanel1);
        tabbedPane.addTab("PERSON", tracePanel1);
        
        tracePanel2 = new TracePanel();
        tracePanel2.setPreferredSize(new Dimension((int) (WINDOWY * .4), (int) (WINDOWY * .25)));
        tracePanel2.hideAlertsWithLevel(AlertLevel.ERROR);          
        tracePanel2.hideAlertsWithLevel(AlertLevel.INFO);             
        tracePanel2.showAlertsWithLevel(AlertLevel.MESSAGE);      
        tracePanel2.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel2.hideAlertsWithTag(AlertTag.PERSON);   
        tracePanel2.showAlertsWithTag(AlertTag.MARKET);
        tracePanel2.hideAlertsWithTag(AlertTag.BANK);        
        
        AlertLog.getInstance().addAlertListener(tracePanel2);
        tabbedPane.addTab("MARKET", tracePanel2);
        
        
        tracePanel9 = new TracePanel();
        tracePanel9.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel9.hideAlertsWithLevel(AlertLevel.ERROR);          
        tracePanel9.hideAlertsWithLevel(AlertLevel.INFO);             
        tracePanel9.showAlertsWithLevel(AlertLevel.MESSAGE);         
        tracePanel9.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel9.hideAlertsWithTag(AlertTag.PERSON);   
        tracePanel9.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel9.hideAlertsWithTag(AlertTag.BANK);
        tracePanel9.showAlertsWithTag(AlertTag.MARKET2);
        
        AlertLog.getInstance().addAlertListener(tracePanel9);
        tabbedPane.addTab("MARKET2", tracePanel9);
        
        
        
        tracePanel3 = new TracePanel();
        tracePanel3.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel3.hideAlertsWithLevel(AlertLevel.ERROR);       
        tracePanel3.hideAlertsWithLevel(AlertLevel.INFO);              
        tracePanel3.showAlertsWithLevel(AlertLevel.MESSAGE);           
        tracePanel3.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel3.hideAlertsWithTag(AlertTag.PERSON);  
        tracePanel3.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel3.showAlertsWithTag(AlertTag.BANK);
        
        AlertLog.getInstance().addAlertListener(tracePanel3);
        tabbedPane.addTab("BANK", tracePanel3);
        
        
        tracePanel4 = new TracePanel();
        tracePanel4.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel4.hideAlertsWithLevel(AlertLevel.ERROR);         
        tracePanel4.hideAlertsWithLevel(AlertLevel.INFO);             
        tracePanel4.showAlertsWithLevel(AlertLevel.MESSAGE);    
        tracePanel4.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel4.hideAlertsWithTag(AlertTag.PERSON);   
        tracePanel4.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel4.hideAlertsWithTag(AlertTag.BANK);
        tracePanel4.showAlertsWithTag(AlertTag.ELLEN_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel4);
        tabbedPane.addTab("ELLEN RESTAURANT", tracePanel4);
        
        
        
        tracePanel5 = new TracePanel();
        tracePanel5.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel5.hideAlertsWithLevel(AlertLevel.ERROR);         
        tracePanel5.hideAlertsWithLevel(AlertLevel.INFO);         
        tracePanel5.showAlertsWithLevel(AlertLevel.MESSAGE);        
        tracePanel5.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel5.hideAlertsWithTag(AlertTag.PERSON);     
        tracePanel5.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel5.hideAlertsWithTag(AlertTag.BANK);
        tracePanel5.showAlertsWithTag(AlertTag.MARCUS_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel5);
        tabbedPane.addTab("MARCUS RESTAURANT", tracePanel5);
        
        
        tracePanel6 = new TracePanel();
        tracePanel6.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel6.hideAlertsWithLevel(AlertLevel.ERROR);           
        tracePanel6.hideAlertsWithLevel(AlertLevel.INFO);               
        tracePanel6.showAlertsWithLevel(AlertLevel.MESSAGE);              
        tracePanel6.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel6.hideAlertsWithTag(AlertTag.PERSON);   	
        tracePanel6.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel6.hideAlertsWithTag(AlertTag.BANK);
        tracePanel6.showAlertsWithTag(AlertTag.ENA_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel6);
        tabbedPane.addTab("ENA RESTAURANT", tracePanel6);
        
        
        tracePanel7 = new TracePanel();
        tracePanel7.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel7.hideAlertsWithLevel(AlertLevel.ERROR);            
        tracePanel7.hideAlertsWithLevel(AlertLevel.INFO);               
        tracePanel7.showAlertsWithLevel(AlertLevel.MESSAGE);         
        tracePanel7.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel7.hideAlertsWithTag(AlertTag.PERSON);    
        tracePanel7.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel7.hideAlertsWithTag(AlertTag.BANK);
        tracePanel7.showAlertsWithTag(AlertTag.DAVID_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel7);
        tabbedPane.addTab("DAVID RESTAURANT", tracePanel7);
        
        
        tracePanel8 = new TracePanel();
        tracePanel8.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .25)));
        tracePanel8.hideAlertsWithLevel(AlertLevel.ERROR);               
        tracePanel8.hideAlertsWithLevel(AlertLevel.INFO); 
        tracePanel8.showAlertsWithLevel(AlertLevel.MESSAGE); 
        tracePanel8.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel8.hideAlertsWithTag(AlertTag.PERSON);   
        tracePanel8.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel8.hideAlertsWithTag(AlertTag.BANK);
        tracePanel8.showAlertsWithTag(AlertTag.JEFFERSON_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel8);
        tabbedPane.addTab("JEFFERSON RESTAURANT", tracePanel8);

        
        

        
        
        
        
        
        
        mainPanel.add(tabbedPane, BorderLayout.SOUTH); 
        
        
        
        
        //---LEFT PANEL BEGIN---//
  		//Entire Left Panel Sizing
  		Dimension leftDim = new Dimension((int) (WINDOWX * .4), (int) (WINDOWY));
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
  		
  		//Control Panel
        Dimension controlDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .6));
        controlPanel.setPreferredSize(controlDim);
        controlPanel.setMinimumSize(controlDim);
        controlPanel.setMaximumSize(controlDim);
        //detailedPanel.setBorder(BorderFactory.createEtchedBorder());
        leftPanel.add(controlPanel, BorderLayout.SOUTH);  
        
        
        //generatePeopleList();
	}

	public void showInfo(String name) {
		for (int i = 0; i < people.size(); i++) {
                PersonAgent temp = people.get(i);
                if (temp.getName() == name) {
                    updateInfoPanel(temp);
                }
            }
    }
	
	public ListPanel getListPanel(){
		return personPanel;
	}
	public void addPerson(PersonAgent p){
		people.add(p);
		personPanel.addPerson(p.getName());
	}
	
	public void updateInfoPanel(Object person) {
        currentPerson = person;

        PersonAgent p = (PersonAgent) person;
        infoLabel.setText(p.getName());
        infoLabel.setVisible(true);
        
        restaurantButton.setEnabled(!p.isHungryForRestaurant());
        homeButton.setEnabled(!p.isHungryForHome());
        workCB.setEnabled(!p.isGoingOrAtWork());
        marketButton.setEnabled(!p.isGoingOrAtMarket());
        bankButton.setEnabled(true);
        
        if (restaurantButton.isEnabled())
        	restaurantMenu.setSelectedIndex(0);
        
        breakLabel.setText("");
        
        infoPanel.validate();
    }
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == addPersonButton){
			String name = nameField.getText();
			double money = 0;
			if (!moneyField.getText().isEmpty())
				money = Double.parseDouble(moneyField.getText());
			else
				money = 0;
			
			//String housing = (String) housingMenu.getSelectedItem();
			String occupation = (String) occupationMenu.getSelectedItem();
			String shift = (String) shiftMenu.getSelectedItem();
			String housing = (String) housingMenu.getSelectedItem();
			String car = (String) carMenu.getSelectedItem();
			boolean renter = false;
			
			if (housing.equalsIgnoreCase("apartment"))
			{
				renter = true;
			}
			if (housing.equalsIgnoreCase("house"))
			{
				renter = false;
			}
			else
			{//change this later--- other has to be random true/false not always false
				renter = false;
			}
			int sb = 0, se = 0;
			if (shift.equalsIgnoreCase("AM")){
				sb = 7;
				se = 9;
			}
			String [] actions ={"work"};

			//personPanel.addPerson(name);
			
			//Reset all fields
			nameField.setText("");
			moneyField.setText("");
			occupationMenu.setSelectedIndex(0);
			shiftMenu.setSelectedIndex(0);
			housingMenu.setSelectedIndex(0);
			carMenu.setSelectedIndex(0);
			restaurantMenu.setSelectedIndex(0);

			cityPanel.addPerson(name, money, renter, false, occupation, sb, se, actions);
		}
		
		//===================== CONTROL PANEL BUTTONS ========================================
		else if (e.getSource() == restaurantButton){
			System.out.println("RESTAURANT BUTTON PRESSED");
			PersonAgent p = (PersonAgent) currentPerson;
			
			if (restaurantMenu.getSelectedItem().toString().equalsIgnoreCase("random"))
				p.msgGoToRestaurant();
			else
				p.msgGoToRestaurant(restaurantMenu.getSelectedItem().toString());
			restaurantButton.setEnabled(false);
			homeButton.setEnabled(false);
		}
		else if (e.getSource() == homeButton){
			System.out.println("HOME BUTTON PRESSED");
			PersonAgent p = (PersonAgent) currentPerson;
			p.msgGotHungryForHome();
			homeButton.setEnabled(false);
			restaurantButton.setEnabled(false);
		}
		else if (e.getSource() == workCB){
			System.out.println("WORK CHECKBOX PRESSED");
			PersonAgent p = (PersonAgent) currentPerson;
			p.msgGoToWork();
			workCB.setEnabled(false);
		}
		else if (e.getSource() == breakButton){
			System.out.println("BREAK SOMETHING BUTTON PRESSED");
			PersonAgent p = (PersonAgent) currentPerson;
			for (PersonAgent pers : cityPanel.getOccupants()){
	        	if (pers.equals(p)){
	        		OccupantRole o = (OccupantRole) p.getRoles().get(ActionType.home);
	        		if (o !=null){
	        			o.applianceBroke();
	        			breakLabel.setText("Success!");
	        		}
	        		else
	        			breakLabel.setText("Not at home!");
	        		break;
	        	}
	        }
		}
		else if (e.getSource() == marketButton){
			System.out.println("BREAK SOMETHING BUTTON PRESSED");
			PersonAgent p = (PersonAgent) currentPerson;
			p.msgGoToMarket();
			marketButton.setEnabled(false);
		}
		else if (e.getSource() == bankButton){
			System.out.println("BANK BUTTON PRESSED");
			PersonAgent p = (PersonAgent) currentPerson;
			p.msgGoToBank("withdraw");
		}
		
		//======================= SCENARIO BUTTONS =========================================
		else if (e.getSource() == scenario1Button){
			System.out.println("SCENARIO1 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config1.txt");
			
			//generatePeopleList();
		}
		else if (e.getSource() == scenario2Button){
			System.out.println("SCENARIO2 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config2.txt");
			
			//generatePeopleList();
		}
		else if (e.getSource() == scenario3Button){
			System.out.println("SCENARIO3 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config3.txt");
		}
		else if (e.getSource() == scenario4Button){
			System.out.println("SCENARIO4 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config4.txt");
		}
		else if (e.getSource() == scenario5Button){
			System.out.println("SCENARIO5 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config5.txt");
		}
		else if (e.getSource() == scenario6Button){
			System.out.println("SCENARIO6 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config6.txt");
		}
		else if (e.getSource() == scenario7Button){
			collision = true;
			System.out.println("SCENARIO7 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config7.txt");
		}
		else if (e.getSource() == scenario8Button){
			collision = true;
			System.out.println("SCENARIO8 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config8.txt");
		}
		else if (e.getSource() == scenario9Button){
			System.out.println("SCENARIO9 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config9.txt");
		}
		else if (e.getSource() == scenario10Button){
			System.out.println("SCENARIO10 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config10.txt");
		}
		else if (e.getSource() == scenario11Button){
			System.out.println("SCENARIO11 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config11.txt");
		}
		else if (e.getSource() == scenario12Button){
			System.out.println("SCENARIO12 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config12.txt");
		}
		else if (e.getSource() == scenario13Button){
			System.out.println("SCENARIO13 BUTTON PRESSED");
			personPanel.resetPanel();
			cityPanel.parseConfig("config13.txt");
		}
		
		
		//================== INVENTORY PANEL BUTTONS ======================================
		else if (e.getSource() == davidRestButton){
			System.out.println("DAVID DEPLETE INVENTORY BUTTON PRESSED");
			ContactList.getInstance().davidCook.emptyInventory();
		}
		else if (e.getSource() == ellenRestButton){
			System.out.println("ELLEN DEPLETE INVENTORY BUTTON PRESSED");
			ContactList.getInstance().ellenCook.depleteInventory();
		}
		else if (e.getSource() == enaRestButton){
			System.out.println("ENA DEPLETE INVENTORY BUTTON PRESSED");
			ContactList.getInstance().enaCook.depleteInventory();
		}
		else if (e.getSource() == marcusRestButton){
			System.out.println("MARCUS DEPLETE INVENTORY BUTTON PRESSED");
			ContactList.getInstance().marcusCook.depleteInventory();
		}
		else if (e.getSource() == jeffersonRestButton){
			System.out.println("JEFFERSON DEPLETE INVENTORY BUTTON PRESSED");
			ContactList.getInstance().jeffersonCook.depleteInventory();
		}

		
		
	}
	
	
	public static void main(String[] args) {
        CityGui gui = new CityGui();
        gui.setTitle("SimCity201");
        gui.setVisible(true);
        gui.setResizable(true);
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
	/*
	private void generatePeopleList() {
		Timer timer = new Timer();
        timer.schedule(new TimerTask() {
			public void run() {
				synchronized(cityPanel.getOccupants()) {
			        for (PersonAgent p : cityPanel.getOccupants()){
			        	personPanel.addPerson(p.getName());
			        }
				}
			}
		}, 200);
	}
	*/
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

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
