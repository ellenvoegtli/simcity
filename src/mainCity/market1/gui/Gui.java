package mainCity.market1.gui;

import java.awt.*;

public interface Gui {

    public void updatePosition();
    public void draw(Graphics2D g);

    //public void paintComponent(Graphics g);

    public boolean isPresent();

}
