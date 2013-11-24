package mainCity.gui;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.market.gui.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import mainCity.PersonAgent;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */

public class ListPanel extends JPanel implements ActionListener {
    public JScrollPane personPane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel personView = new JPanel();
    private List<JButton> personList = new ArrayList<JButton>();
   
    //private CityPanel cityPanel;
    private CityGui cityGui;
    private String type;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(CityGui cg) {

        //cityPanel = cp;
    	cityGui = cg;
        //this.type = type;
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));    	
        add(new JLabel("<html><u> People </u><br></html>"));

        personView.setLayout(new BoxLayout((Container) personView, BoxLayout.Y_AXIS));
        personPane.setViewportView(personView);
        add(personPane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */

    
    
    public void actionPerformed(ActionEvent e) {
        for (JButton temp:personList){
    		if (e.getSource() == temp){
    			cityGui.showInfo(temp.getText());
    		}
    	}//end of second for loop
    }


    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */

    public void addPerson(String name) {

        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = personPane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            personList.add(button);
            personView.add(button);
         
            cityGui.showInfo(name);//puts hungry button on panel

            validate();
        }
    }
    

}
