package housing.Interfaces;

import housing.gui.HomeAnimationPanel;

public interface OccupantGuiInterface {
	
	public abstract void DoGoToFridge();
	public abstract void DoGoToFridgeA();
	public abstract void DoGoToStove();
	public abstract void DoGoToStoveA();
	public abstract void DoGoToSink();
	public abstract void DoGoToSinkA();
	public abstract void DoGoToKitchenTable();
	public abstract void DoGoToKitchenTableA();
	public abstract void DoGoRest();
	public abstract void DoGoRestA();
	public abstract void DoLeave();
	public abstract void DoGoToAppliance(int xPos, int yPos);
	public abstract boolean isHungry();
	public abstract void setHungry();

}
