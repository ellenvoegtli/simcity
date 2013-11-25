package mainCity.gui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import mainCity.PersonAgent;
import mainCity.gui.AnimationPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.contactList.*;
import mainCity.gui.trace.*;
import mainCity.gui.ListPanel;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Vector;
import java.math.*;


public class CityGui extends JFrame implements ActionListener, KeyListener{	
	private AnimationPanel animationPanel = new AnimationPanel(); 
	private CityView view = new CityView(this);
	private TracePanel tracePanel1;
	private TracePanel tracePanel2;
	private TracePanel tracePanel3;
	private TracePanel tracePanel4;
	private TracePanel tracePanel5;
	private TracePanel tracePanel6;
	private TracePanel tracePanel7;
	private TracePanel tracePanel8;
	private CityPanel cityPanel = new CityPanel(this);
	private JPanel mainPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel detailedPanel = new JPanel();

	//====Control panel components====
	private JPanel controlPanel = new JPanel();
	
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
	
	
	private JPanel subControlPanel2 = new JPanel();
	
	
	private JLabel infoLabel;
	private JPanel infoPanel = new JPanel();
	private GroupLayout layout2 = new GroupLayout(infoPanel);
	private JLabel hungryLabel = new JLabel("Hungry?");
	private JButton restaurantButton = new JButton("Eat at restaurant");
	private JButton homeButton = new JButton("Eat at home");
	
	private JLabel workLabel = new JLabel("Go to work?");
	private JCheckBox workCB = new JCheckBox();
	
	private JButton depositButton = new JButton("Deposit");
	private JButton loanButton = new JButton("Request a loan");
	private JButton withdrawButton = new JButton("Withdraw");
	private JTextField depositField = new JTextField(100);
	private JTextField withrawField = new JTextField(100);
	private JTextField loanField = new JTextField(100);
	
	private JLabel blankLabel = new JLabel(" ");
	private JButton addPersonButton = new JButton("Create person");
	
	private Object currentPerson;
	//private JPanel infoPanel;		//add to subControlPanel2
	
	private ListPanel personPanel = new ListPanel(this);
    private Vector<PersonAgent> people = new Vector<PersonAgent>();

	
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
		
		
		String[] occupationStrings = {"Random", "Rich", "bankManager", "bankTeller", "banker", 
				"restaurantHost", "restaurantWaiter", "ellenCook", "restaurantCashier", 
				"marketGreeter", "marketEmployee", "marketCashier", "marketDeliveryMan"
		};
		occupationMenu = new JComboBox(occupationStrings);
		Dimension occupationDim = new Dimension(150, 30);
		occupationMenu.setPreferredSize(occupationDim);
		occupationMenu.setMinimumSize(occupationDim);
		occupationMenu.setMaximumSize(occupationDim);
		occupationMenu.setSelectedIndex(0);
		occupationMenu.addActionListener(this);
		
		String[] shiftStrings = {"Random", "AM", "PM"};
		shiftMenu = new JComboBox(shiftStrings);
		Dimension shiftDim = new Dimension(150, 30);
		shiftMenu.setPreferredSize(shiftDim);
		shiftMenu.setMinimumSize(shiftDim);
		shiftMenu.setMaximumSize(shiftDim);
		shiftMenu.setSelectedIndex(0);
		shiftMenu.addActionListener(this);

		String[] housingStrings = {"Random", "House", "Apartment"};
		housingMenu = new JComboBox(housingStrings);
		Dimension housingDim = new Dimension(150, 30);
		housingMenu.setPreferredSize(housingDim);
		housingMenu.setMinimumSize(housingDim);
		housingMenu.setMaximumSize(housingDim);
		housingMenu.setSelectedIndex(0);
		housingMenu.addActionListener(this);
		
		String[] carStrings = {"Random", "Car", "No car"};
		carMenu = new JComboBox(carStrings);
		Dimension carDim = new Dimension(150, 30);
		carMenu.setPreferredSize(carDim);
		carMenu.setMinimumSize(carDim);
		carMenu.setMaximumSize(carDim);
		carMenu.setSelectedIndex(0);
		carMenu.addActionListener(this);
		
		Dimension depositDim = new Dimension(150, 30);
		depositField.setPreferredSize(depositDim);
		depositField.setMinimumSize(depositDim);
		depositField.setMaximumSize(depositDim);
		
		Dimension withdrawDim = new Dimension(150, 30);
		withrawField.setPreferredSize(withdrawDim);
		withrawField.setMinimumSize(withdrawDim);
		withrawField.setMaximumSize(withdrawDim);
		
		Dimension loanDim = new Dimension(150, 30);
		loanField.setPreferredSize(loanDim);
		loanField.setMinimumSize(loanDim);
		loanField.setMaximumSize(loanDim);
		
		
		//===GROUP LAYOUT 1 FOR CONTROL PANEL====
		subControlPanel1.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(nameFieldLabel).addComponent(moneyFieldLabel).addComponent(occupationMenuLabel).
	            addComponent(shiftMenuLabel).addComponent(housingMenuLabel).addComponent(carMenuLabel).
	            addComponent(addPersonButton)
	            );
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(nameField).addComponent(moneyField).addComponent(occupationMenu).
	            addComponent(shiftMenu).addComponent(housingMenu).addComponent(carMenu).
	            addComponent(blankLabel)
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
	            addComponent(shiftMenuLabel).addComponent(shiftMenu));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(housingMenuLabel).addComponent(housingMenu));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
	            addComponent(carMenuLabel).addComponent(carMenu));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(addPersonButton).addComponent(blankLabel));
		layout.setVerticalGroup(vGroup);
		//====END GROUP LAYOUT 1=====
		JTabbedPane tabbedPane2 = new JTabbedPane();
		tabbedPane2.addTab("Create", subControlPanel1);
		
		//===GROUP LAYOUT 2 FOR CONTROL PANEL====
		infoPanel.setLayout(layout2);
		layout2.setAutoCreateGaps(true);
		layout2.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup2 = layout2.createSequentialGroup();
		hGroup2.addGroup(layout2.createParallelGroup().
	            addComponent(hungryLabel).addComponent(blankLabel).addComponent(workLabel).
	            addComponent(depositField).addComponent(withrawField).addComponent(loanField)
	            );
		hGroup2.addGroup(layout2.createParallelGroup().
	            addComponent(restaurantButton).addComponent(homeButton).addComponent(workCB).
	            addComponent(depositButton).addComponent(withdrawButton).addComponent(loanButton)
	            );
		layout2.setHorizontalGroup(hGroup2);
		
		GroupLayout.SequentialGroup vGroup2 = layout2.createSequentialGroup();
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(hungryLabel).addComponent(restaurantButton));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(blankLabel).addComponent(homeButton));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(workLabel).addComponent(workCB));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(depositField).addComponent(depositButton));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(withrawField).addComponent(withdrawButton));
		vGroup2.addGroup(layout2.createParallelGroup(Alignment.BASELINE).
	            addComponent(loanField).addComponent(loanButton));
		layout2.setVerticalGroup(vGroup2);
	   //====END GROUP LAYOUT 2=====
		
		subControlPanel2.setLayout(new GridBagLayout());
		Dimension listDim = new Dimension((int) (WINDOWX * .15), (int) (WINDOWY * .2));
        personPanel.setPreferredSize(listDim);
        personPanel.setMinimumSize(listDim);
        personPanel.setMaximumSize(listDim);
		subControlPanel2.add(personPanel);
		subControlPanel2.add(infoPanel);
		tabbedPane2.addTab("Controls", subControlPanel2);
		
		
		controlPanel.add(tabbedPane2);
		//controlPanel.add(subControlPanel2);
		
		occupationMenu.addActionListener(this);
		shiftMenu.addActionListener(this);
		housingMenu.addActionListener(this);
		carMenu.addActionListener(this);
		addPersonButton.addActionListener(this);
		

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
        tracePanel1.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
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
        tracePanel2.setPreferredSize(new Dimension((int) (WINDOWY * .4), (int) (WINDOWY * .4)));
        tracePanel2.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel2.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel2.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel2.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel2.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel2.showAlertsWithTag(AlertTag.MARKET);
        tracePanel2.hideAlertsWithTag(AlertTag.BANK);        
        
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
        tracePanel4.showAlertsWithTag(AlertTag.ELLEN_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel4);
        tabbedPane.addTab("ELLEN RESTAURANT", tracePanel4);
        
        
        
        tracePanel5 = new TracePanel();
        tracePanel5.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel5.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel5.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel5.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel5.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel5.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel5.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel5.hideAlertsWithTag(AlertTag.BANK);
        tracePanel5.showAlertsWithTag(AlertTag.MARCUS_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel5);
        tabbedPane.addTab("MARCUS RESTAURANT", tracePanel5);
        
        
        tracePanel6 = new TracePanel();
        tracePanel6.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel6.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel6.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel6.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel6.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel6.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel6.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel6.hideAlertsWithTag(AlertTag.BANK);
        tracePanel6.showAlertsWithTag(AlertTag.ENA_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel6);
        tabbedPane.addTab("ENA RESTAURANT", tracePanel6);
        
        
        tracePanel7 = new TracePanel();
        tracePanel7.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel7.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel7.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel7.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel7.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel7.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel7.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel7.hideAlertsWithTag(AlertTag.BANK);
        tracePanel7.showAlertsWithTag(AlertTag.DAVID_RESTAURANT);
        
        AlertLog.getInstance().addAlertListener(tracePanel7);
        tabbedPane.addTab("DAVID RESTAURANT", tracePanel7);
        
        
        tracePanel8 = new TracePanel();
        tracePanel8.setPreferredSize(new Dimension((int) (WINDOWX * .4), (int) (WINDOWY * .4)));
        tracePanel8.hideAlertsWithLevel(AlertLevel.ERROR);                //THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
        tracePanel8.hideAlertsWithLevel(AlertLevel.INFO);                //THESE PRINT BLUE
        tracePanel8.showAlertsWithLevel(AlertLevel.MESSAGE);                //THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
        tracePanel8.hideAlertsWithLevel(AlertLevel.DEBUG);
        tracePanel8.hideAlertsWithTag(AlertTag.PERSON);   	//as default, show all tags   
        tracePanel8.hideAlertsWithTag(AlertTag.MARKET);
        tracePanel8.hideAlertsWithTag(AlertTag.BANK);
        tracePanel8.showAlertsWithTag(AlertTag.DAVID_RESTAURANT);
        
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
        
        /*
        Dimension listDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .2));
        cityPanel.setPreferredSize(listDim);
        cityPanel.setMinimumSize(listDim);
        cityPanel.setMaximumSize(listDim);
        subControlPanel2.add(cityPanel);*/
        //Dimension listDim = new Dimension((int) (WINDOWX * .6), (int) (WINDOWY * .2));
        //personPanel.setPreferredSize(listDim);
        //personPanel.setMinimumSize(listDim);
        //personPanel.setMaximumSize(listDim);
        
        //controlPanel.add(subControlPanel2);        
	}
	
	public void showInfo(String name) {
		for (int i = 0; i < people.size(); i++) {
                PersonAgent temp = people.get(i);
                if (temp.getName() == name) {
                    updateInfoPanel(temp);
                }
            }
        }
	
	public void updateInfoPanel(Object person) {
        currentPerson = person;

        PersonAgent p = (PersonAgent) person;
        infoLabel.setText(
               "<html><pre>     Name: " + p.getName() + " </pre></html>");

        //set buttons enabled/disabled...
        infoPanel.validate();
    }
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == addPersonButton){
			System.out.println("name = " + nameField.getText());
			String name = nameField.getText();
			double money = Double.parseDouble(moneyField.getText());
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
			cityPanel.addPerson(name, money, renter, occupation, sb, se, actions);
		}
		else if (e.getSource() == restaurantButton){
			restaurantButton.setEnabled(false);
			//...
		}
		else if (e.getSource() == homeButton){
			homeButton.setEnabled(false);
		}
		else if (e.getSource() == workCB){
			workCB.setEnabled(false);
			
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
