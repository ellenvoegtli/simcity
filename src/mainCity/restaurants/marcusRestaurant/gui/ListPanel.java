package mainCity.restaurants.marcusRestaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    
    private JPanel waiterPanel = new JPanel();
    private JButton waiterButton = new JButton("Add Waiter");
    private JTextField waiterInput = new JTextField();

    private JPanel inputForm = new JPanel();
    private JTextField nameInput = new JTextField();
    
    private JCheckBox isHungryButton;
    
    private MarcusRestaurantPanel restPanel;
    private String type;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(MarcusRestaurantPanel rp, final String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BorderLayout());//new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        inputForm.setLayout(new BorderLayout());

        if(type.equals("Customers")) {
	        addPersonB.addActionListener(this);
	        nameInput.addActionListener(this);
		    isHungryButton = new JCheckBox();
		    isHungryButton.setText("Hungry?");
		    isHungryButton.setEnabled(false);
		    isHungryButton.addActionListener(this);
		    
	        inputForm.add(nameInput, BorderLayout.NORTH);
		    inputForm.add(isHungryButton, BorderLayout.EAST);
	        inputForm.add(addPersonB, BorderLayout.CENTER);
        }
        
	    if(type.equals("Waiters")) {
	        waiterButton.addActionListener(this);
	        waiterInput.addActionListener(this);
	
	        waiterPanel.setLayout(new BorderLayout());
	        waiterPanel.add(waiterInput, BorderLayout.CENTER);
	        waiterPanel.add(waiterButton, BorderLayout.SOUTH);
	        
	        inputForm.add(waiterPanel, BorderLayout.SOUTH);
	        waiterButton.setEnabled(false);
	    }
	    
        KeyListener keyListener = new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            	if(type.equals("Customers")) {
	            	if(nameInput.getText().trim().length() == 0 || nameInput.getText().trim().equals("")) {
	            		isHungryButton.setEnabled(false);
	            	}
	            	else if(nameInput.getText().trim().length() != 0 || !nameInput.getText().trim().equals("")) {
	            		isHungryButton.setEnabled(true);
	            	}
            	}

            	if(type.equals("Waiters")) {
	            	if(waiterInput.getText().trim().length() == 0 || waiterInput.getText().trim().equals("")) {
	            		waiterButton.setEnabled(false);
	            	}
	            	else if(waiterInput.getText().trim().length() != 0 || !waiterInput.getText().trim().equals("")) {
	            		waiterButton.setEnabled(true);
	            	}
            	}
            }

            public void keyTyped(KeyEvent keyEvent) {
            
            }
        };
        
        if(type.equals("Customers")) {
        	nameInput.addKeyListener(keyListener);
        }
        
        if(type.equals("Waiters")) {
        	waiterInput.addKeyListener(keyListener);
        }
        
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane, BorderLayout.CENTER);
        add(inputForm, BorderLayout.SOUTH);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if(type.equals("Customers") && e.getSource() == addPersonB) {        	
        	if(nameInput.getText().trim().length() != 0 && !nameInput.getText().trim().equals("")) {
        		addPerson(nameInput.getText(), isHungryButton.isSelected());
        		nameInput.setText("");
        		isHungryButton.setSelected(false);
        		isHungryButton.setEnabled(false);
        	}

        }
        
        else if(type.equals("Waiters") && e.getSource() == waiterButton) {
        	restPanel.addWaiter(waiterInput.getText());
    		addPerson(waiterInput.getText(), false);
        	waiterInput.setText("");
    		waiterButton.setEnabled(false);
        }
        
//        else {
//        	for (JButton temp:list){
//                if (e.getSource() == temp)
//                    restPanel.showInfo(type, temp.getText());
//            }
//        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name, boolean checked) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 8));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson(type, name, checked);//puts customer on list
            //restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}
